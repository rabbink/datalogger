package nl.rabbink.datalogger.model;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {

    private Integer count = 0;

    private final List<T> values = new ArrayList<>();

    private long queryDuration;

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getValues() {
        return values;
    }

    public long getQueryDuration() {
        return queryDuration;
    }

    public void setQueryDuration(long queryDuration) {
        this.queryDuration += queryDuration;
    }

}
