
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    static final int startYear = 2020;
    static final int endYear = 2022;

    public static void main(String[] args) {


        int maxOnline = findMaxOnline();

        System.out.printf("Max online:%d%n",maxOnline);

    }

    public static int findMaxOnline() {
        // Найти самое большое количество онлайна одновременно

        Random random = new Random();
        int i = random.nextInt(4, 7);

        List<UserOnline> onlineList = Stream.generate(Main::generateUser).limit(i).toList();

        //===========================================================================================================

        // у нас есть Timeshtamp и признак начала интервала и конца интервала.
        // Идея - преобразовать даты в поток дат и признаком - был ли это начало интервала или конец.
        // найдем поток времени с признаками начала и конца и отсортируем его по дате- получим нечто типо такого:
        // 20-01-2010 true, 21-02-2010 true , 22-03-2011 true, 22-03-2011 false

        List<TimeStampSession> timeStampSessions = new ArrayList<>();

        // сформируем непрерывный поток дат
        onlineList.forEach(userOnline -> {
            //start session
            timeStampSessions.add(new TimeStampSession(userOnline.getStartSession(),true));
            //end session
            timeStampSessions.add(new TimeStampSession(userOnline.getEndSession(),false));
        });

        // отсортируем по дате.
        Collections.sort(timeStampSessions, Comparator.comparing(TimeStampSession::getTimestamp));

        // осталось найти максимальную последовательность с признаком isStart
        // ну и для красоты выведем в консоль
        System.out.println("------------------------");
        timeStampSessions.forEach(System.out::println);
        System.out.println("------------------------");
        // теперь будем идти по timeStampSessions и подсчитывать:
        int max = 0; // максимальное количество юзеров в сессии.
        int con = 0; // количество юзеров в сессии
        for(TimeStampSession timeStampSession:timeStampSessions){
            if (timeStampSession.isStart())
            {
                con++;
                max=con>max?con:max;
            }else
            {
                // если сессия false, значит отминусовывем кол-во юзеров
                con--;
            }
        };


        return max;
    }

    public LocalDate findMaxOnlineDate() {
        // Найти дату самого большого онлайна(первый день в диапазоне, когда было одновременно онлайн самое большое кол-во людей)
        // Более сложный вариант, это найти диапазон дат наибольшего онлайна
        // (то есть к примеру дата начала самого большого онлайна 05.05.2023, дата завершения 10.07.2023)

        Random random = new Random();
        int i = random.nextInt(4, 7);

        List<UserOnline> onlineList = Stream.generate(Main::generateUser).limit(i).toList();

        return null;
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

