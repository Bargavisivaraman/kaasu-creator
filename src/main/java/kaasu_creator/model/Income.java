package kaasu_creator.model;

import java.math.BigDecimal;
import java.sql.Date;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Income {
    private Long id;

    @NotNull(message = "User id is required")
    private Long userId;

    @Size(max = 50, message = "Income type must be at most 50 characters")
    private String incomeType;

    @Size(max = 255, message = "Source must be at most 255 characters")
    private String source;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Income date is required")
    private Date incomeDate;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    public Income() {}

    public Income(Long id, Long userId, String incomeType, String source, BigDecimal amount, Date incomeDate, String notes) {
        this.id = id;
        this.userId = userId;
        this.incomeType = incomeType;
        this.source = source;
        this.amount = amount;
        this.incomeDate = incomeDate;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getIncomeType() { return incomeType; }
    public String getSource() { return source; }
    public BigDecimal getAmount() { return amount; }
    public Date getIncomeDate() { return incomeDate; }
    public String getNotes() { return notes; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setIncomeType(String incomeType) { this.incomeType = incomeType; }
    public void setSource(String source) { this.source = source; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setIncomeDate(Date incomeDate) { this.incomeDate = incomeDate; }
    public void setNotes(String notes) { this.notes = notes; }
}