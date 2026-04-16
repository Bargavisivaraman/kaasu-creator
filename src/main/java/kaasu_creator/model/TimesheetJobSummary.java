package kaasu_creator.model;

import java.math.BigDecimal;

public class TimesheetJobSummary {
    private String jobName;
    private BigDecimal hourlyPay;
    private BigDecimal totalHours;
    private BigDecimal totalEarned;

    public TimesheetJobSummary() {}

    public TimesheetJobSummary(String jobName, BigDecimal hourlyPay, BigDecimal totalHours, BigDecimal totalEarned) {
        this.jobName = jobName;
        this.hourlyPay = hourlyPay;
        this.totalHours = totalHours;
        this.totalEarned = totalEarned;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public BigDecimal getHourlyPay() {
        return hourlyPay;
    }

    public void setHourlyPay(BigDecimal hourlyPay) {
        this.hourlyPay = hourlyPay;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public BigDecimal getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(BigDecimal totalEarned) {
        this.totalEarned = totalEarned;
    }
}
