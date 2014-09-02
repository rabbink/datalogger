package nl.rabbink.datalogger;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;

@ManagedBean
public class GraphBean implements Serializable {

    private Mode mode;

    public Mode getMode() {
        return mode;
    }

    public void setToLast24Hours() {
        this.mode = Mode.LAST_24_HOURS;
    }
    
    public void setToLast2Days() {
        this.mode = Mode.LAST_2_DAYS;
    }

    public void setToLastWeek() {
        this.mode = Mode.LAST_WEEK;
    }
    
    public enum Mode {

        LAST_24_HOURS, LAST_2_DAYS, LAST_WEEK, LAST_MONTH, LAST_YEAR;
    }
}
