package kaasu_creator.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Expense {
    private Long id;

    @NotNull(message = "User id is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 100, message = "Category must be at most 100 characters")
    private String category;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private Timestamp date;

    public Expense() {}

    public Expense(Long id, Long userId, String title, String category, BigDecimal amount, Timestamp date) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public Timestamp getDate() { return date; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDate(Timestamp date) { this.date = date; }
}