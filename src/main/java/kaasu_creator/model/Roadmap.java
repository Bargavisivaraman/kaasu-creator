package kaasu_creator.model;

import java.math.BigDecimal;

public class Roadmap {
    private Long id;
    private Long goalId;
    private int weekNumber;
    private BigDecimal targetAmount;
    private String status;

    public Roadmap() {}

    public Roadmap(Long id, Long goalId, int weekNumber, BigDecimal targetAmount, String status) {
        this.id = id;
        this.goalId = goalId;
        this.weekNumber = weekNumber;
        this.targetAmount = targetAmount;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getGoalId() { return goalId; }
    public int getWeekNumber() { return weekNumber; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }
    public void setWeekNumber(int weekNumber) { this.weekNumber = weekNumber; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public void setStatus(String status) { this.status = status; }
}