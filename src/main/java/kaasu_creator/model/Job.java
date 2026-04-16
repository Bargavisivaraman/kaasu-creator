package kaasu_creator.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Job {
    private Long id;
    private Long userId;
    private String jobName;
    private BigDecimal hourlyWage;
    private String notes;
    private Timestamp createdAt;

    // Populated by the summary JOIN query — not stored in the database
    private transient BigDecimal totalHours  = BigDecimal.ZERO;
    private transient BigDecimal totalEarned = BigDecimal.ZERO;

    public Job() {}

    public Job(Long id, Long userId, String jobName, BigDecimal hourlyWage, String notes, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.jobName = jobName;
        this.hourlyWage = hourlyWage;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getJobName() { return jobName; }
    public BigDecimal getHourlyWage() { return hourlyWage; }
    public String getNotes() { return notes; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public void setHourlyWage(BigDecimal hourlyWage) { this.hourlyWage = hourlyWage; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public BigDecimal getTotalHours()  { return totalHours; }
    public BigDecimal getTotalEarned() { return totalEarned; }
    public void setTotalHours(BigDecimal totalHours)   { this.totalHours  = totalHours  != null ? totalHours  : BigDecimal.ZERO; }
    public void setTotalEarned(BigDecimal totalEarned) { this.totalEarned = totalEarned != null ? totalEarned : BigDecimal.ZERO; }
}
