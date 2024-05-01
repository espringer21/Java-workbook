package dmit2015.restclient;

import java.time.LocalDate;

public class Weather {
    private LocalDate date;
    private int tempc;
    private int tempf;
    private String summary;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTempc() {
        return tempc;
    }

    public void setTempc(int tempc) {
        this.tempc = tempc;
    }

    public int getTempf() {
        return tempf;
    }

    public void setTempf(int tempf) {
        this.tempf = tempf;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
