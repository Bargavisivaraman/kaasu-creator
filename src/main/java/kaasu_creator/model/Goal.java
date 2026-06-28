package kaasu_creator.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class Goal {
    private Long id;

    @NotNull(message = "User id is required")
    private Long userId;

    @NotBlank(message = "Goal name is required")
    @Size(max = 255, message = "Goal name must be at most 255 characters")
    private String name;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than zero")
    private BigDecimal targetAmount;

    @PositiveOrZero(message = "Current amount cannot be negative")
    private BigDecimal currentAmount;

    @NotNull(message = "Deadline is required")
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