package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kaasu_creator.dao.ExpenseDao;
import kaasu_creator.model.Expense;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private ExpenseDao expenseDao;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void addExpense_buildsExpenseAndSaves() {
        budgetService.addExpense(7L, "Coffee", "Food", new BigDecimal("4.50"));

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseDao).save(captor.capture());

        Expense saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(7L);
        assertThat(saved.getTitle()).isEqualTo("Coffee");
        assertThat(saved.getCategory()).isEqualTo("Food");
        assertThat(saved.getAmount()).isEqualByComparingTo("4.50");
    }

    @Test
    void getExpensesByUser_returnsDaoResults() {
        List<Expense> expected = List.of(
                new Expense(1L, 7L, "Rent", "Housing", new BigDecimal("900"), null));
        when(expenseDao.findByUserId(7L)).thenReturn(expected);

        assertThat(budgetService.getExpensesByUser(7L)).isEqualTo(expected);
    }

    @Test
    void getTotalExpenses_returnsDaoSum() {
        when(expenseDao.sumByUserId(7L)).thenReturn(new BigDecimal("904.50"));

        assertThat(budgetService.getTotalExpenses(7L)).isEqualByComparingTo("904.50");
    }

    @Test
    void deleteExpense_delegatesToDao() {
        budgetService.deleteExpense(99L);

        verify(expenseDao).deleteById(99L);
        verify(expenseDao, org.mockito.Mockito.never()).save(any());
    }
}
