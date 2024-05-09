
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    static final int startYear = 2020;
    static final int endYear = 2022;

    public static void main(String[] args) {


        Random random = new Random();
        int i = random.nextInt(4, 7);

        List<UserOnline> onlineList = Stream.generate(Main::generateUser).limit(i).toList();

        List<TimeStampSession> timeline = getTimeLine(onlineList);

        printTimeLine(timeline);
        System.out.println("-------------------------------");
        int maxOnline = findMaxOnline(timeline);
        System.out.printf("Max online:%d%n",maxOnline);

        LocalDate startPeakOnline = findMaxOnlineDate(timeline);
        System.out.printf("Start peak online:%s%n", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startPeakOnline));

        LocalDate endPeakOnline = findEndMaxOnlineDate(timeline);
        System.out.printf("End peak online:%s%n", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(endPeakOnline));
        System.out.println("-------------------------------");


    }

    /**
     * Трансформирование сессий в таймлайн сессий с признаками начала и окончания сессий.
      * @param onlineList
     * @return
     */
    private static List<TimeStampSession> getTimeLine(List<UserOnline> onlineList)
    {
        List<TimeStampSession> timeStampSessions = new ArrayList<>();

        onlineList.forEach(userOnline -> {
            //start session
            timeStampSessions.add(new TimeStampSession(userOnline.getStartSession(),true));
            //end session
            timeStampSessions.add(new TimeStampSession(userOnline.getEndSession(),false));
        });

        // При такой сортировке начало сессий всегда будет в начале массива.

        sortTimeLine( timeStampSessions,true);

        return  timeStampSessions;
    }

    /**
     * Метод сортирует массив timeLine сессий по возрастанию\убыванию.
     * (в данной задаче по убыванию не используется)
     * @param timeStampSessions
     * @param ask_or_desk
     */
    private static void sortTimeLine(List<TimeStampSession> timeStampSessions,boolean ask_or_desk)
    {

        if(ask_or_desk)
            Collections.sort(timeStampSessions, Comparator.comparing(TimeStampSession::getTimestamp));
        else
            Collections.sort(timeStampSessions, Comparator.comparing(TimeStampSession::getTimestamp).reversed());
    }

    /**
     * findMaxOnline - определение максимального количества открытых сессий из всех когда-либо открытых
     * @param timeStampSessions - лента сессий.
     * @return максимального количества открытых сессий из всех когда-либо открытых
     */

    public static int findMaxOnline(List<TimeStampSession> timeStampSessions) {
        // Найти самое большое количество онлайна одновременно
        //===========================================================================================================

        // у нас есть Timeshtamp и признак начала интервала и конца интервала.
        // Идея - преобразовать даты в поток дат и признаком - был ли это начало интервала или конец.
        // найдем поток времени с признаками начала и конца и отсортируем его по дате- получим нечто типо такого:
        // 20-01-2010 true, 21-02-2010 true , 22-03-2011 true, 22-03-2011 false

        int maxOnline = 0; // максимальное количество сессий.
        int con = 0; // количество в сессии
        for(TimeStampSession timeStampSession:timeStampSessions){
            if (timeStampSession.isStart())
            {
                con++;
                maxOnline=con>maxOnline?con:maxOnline;
            }else
            {
                con--;
            }
        };


        return maxOnline;
    }

    /**
     * findMaxOnlineDate - определение даты начала максимального количества открытых сессий из всех когда-либо открытых
     * @param timeStampSessions
     * @return дата максимального количества открытых сессий.
     */
    public static LocalDate findMaxOnlineDate(List<TimeStampSession> timeStampSessions) {
        // Найти дату самого большого онлайна(первый день в диапазоне, когда было одновременно онлайн самое большое кол-во людей)
        // ??? интересно, что делать, если таких диапазонов несколько? будем брать последний, штош.
        //================================================================

        // В основе функции такой же принцип, как и у findMaxOnline
        LocalDate maxOnlineDate = null;

        int maxOnline = 0;
        int con = 0;
        for(TimeStampSession timeStampSession:timeStampSessions){
            if (timeStampSession.isStart())
            {
                con++;
                if(con>maxOnline)
                {
                    maxOnline = con;
                    maxOnlineDate = timeStampSession.getTimestamp();
                }
            }else
            {
               con -- ;
            }
        };


        return maxOnlineDate;
    }
    /**
     * findMaxOnlineDate - определение даты окончания максимального количества открытых сессий из всех когда-либо открытых
     * @param timeStampSessions
     * @return дата оклнчания максимального количества открытых сессий.
     */
    public static LocalDate findEndMaxOnlineDate(List<TimeStampSession> timeStampSessions) {

        // Более сложный вариант, это найти диапазон дат наибольшего онлайна

        // (то есть к примеру дата начала самого большого онлайна 05.05.2023, дата завершения 10.07.2023)
        // Так же нужно учесть, что максимальный диапазон может завершиться в тот же день
        // !!!  дата начала диапазона будет findMaxOnlineDate, а конец этого диапазона - первый false после максимального.

        //================================================================

        // Получаем начальную дату пиковой нагрузки.
        LocalDate finalMaxOnlineStartDate =  findMaxOnlineDate( timeStampSessions);
        // ищем дату когда пиковая нагрузка "спадет", т.е. первое значение признака окончания сессии - это и будет концом диапазана максимальной нагрузки
        TimeStampSession endMaxSessionOnline = timeStampSessions.stream()
                                                .filter(timeStampSession ->
                                                            (finalMaxOnlineStartDate.compareTo(timeStampSession.getTimestamp())<=0)
                                                          & !timeStampSession.isStart()
                                                       )
                                                .findFirst()
                                                .orElse(null);

        return endMaxSessionOnline==null?finalMaxOnlineStartDate:endMaxSessionOnline.getTimestamp();

    }

    /**
     * Вывод на экран тайм-лайн сессий
     * @param timeStampSessions
     */
    private static void printTimeLine(List<TimeStampSession> timeStampSessions)
    {
        timeStampSessions.forEach(System.out::println);
    }

    public static UserOnline generateUser() {
        Random random = new Random();

        int randomYearStart = random.nextInt(startYear, endYear);
        int randomMonthStart = random.nextInt(1, 12);
        int randomDayStart = random.nextInt(1, Month.of(randomMonthStart).maxLength());
        int randomYearEnd = random.nextInt(startYear, endYear);
        int randomMonthEnd = random.nextInt(1, 12);
        int randomDayEnd = random.nextInt(1, Month.of(randomMonthEnd).maxLength());

        LocalDate startUserOnline = LocalDate.of(randomYearStart, randomMonthStart, randomDayStart);
        LocalDate endUserOnline = LocalDate.of(randomYearEnd, randomMonthEnd, randomDayEnd);

        if (startUserOnline.isAfter(endUserOnline)) {
            LocalDate temp = startUserOnline;
            startUserOnline = endUserOnline;
            endUserOnline = temp;
        }

        return new UserOnline(startUserOnline, endUserOnline);
    }
}

