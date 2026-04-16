package kaasu_creator.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class TimesheetEntry {

    private Long id;
    private Long userId;
    private Long jobId;
    private Date workDate;
    private BigDecimal hoursWorked;
    private String notes;
    private Timestamp createdAt;

    // Transient fields for display (populated via JOIN in repository)
    private transient String jobName;
    private transient BigDecimal hourlyWage;
    private transient BigDecimal earnedAmount;

    public TimesheetEntry() {
    }

    public TimesheetEntry(Long id, Long userId, Long jobId, Date workDate,
            BigDecimal hoursWorked, String notes, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public TimesheetEntry(Long id, Long userId, Long jobId, Date workDate,
            BigDecimal hoursWorked, String notes, Timestamp createdAt,
            String jobName, BigDecimal hourlyWage, BigDecimal earnedAmount) {
        this(id, userId, jobId, workDate, hoursWorked, notes, createdAt);
        this.jobName = jobName;
        this.hourlyWage = hourlyWage;
        this.earnedAmount = earnedAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(BigDecimal hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public BigDecimal getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(BigDecimal hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public BigDecimal getEarnedAmount() {
        return earnedAmount;
    }

    public void setEarnedAmount(BigDecimal earnedAmount) {
        this.earnedAmount = earnedAmount;
    }
}