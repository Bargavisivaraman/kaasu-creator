package kaasu_creator.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Goal {
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate deadline;
    private Timestamp createdAt;

    public Goal() {}

    public Goal(Long id, Long userId, String name, BigDecimal targetAmount,
                BigDecimal currentAmount, LocalDate deadline, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public LocalDate getDeadline() { return deadline; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}