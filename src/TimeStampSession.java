import java.time.LocalDate;
import java.util.Objects;

public class TimeStampSession {
    private LocalDate timestamp;
    private boolean isStart;

    public TimeStampSession(LocalDate timestamp, boolean isStart)
    {
        this.timestamp = timestamp;
        this.isStart = isStart;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, isStart);
    }

    @Override
    public String toString() {
        return "{TimeStampSession:{ timestamp:"+ this.timestamp+", isStart:"+this.isStart+" }}";
    }
}
