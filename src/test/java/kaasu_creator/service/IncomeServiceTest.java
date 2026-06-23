package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kaasu_creator.dao.IncomeDao;
import kaasu_creator.model.Income;

/**
 * Unit tests for IncomeService. The DAO is mocked so these tests exercise
 * the service logic in isolation without a database.
 */
@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {

    @Mock
    private IncomeDao incomeDao;

    @InjectMocks
    private IncomeService incomeService;

    private Income sampleIncome() {
        return new Income(1L, 42L, "EXTRA", "Freelance",
                new BigDecimal("150.00"), new Date(System.currentTimeMillis()), null);
    }

    @Test
    void addIncome_delegatesToDao() {
        Income income = sampleIncome();

        incomeService.addIncome(income);

        verify(incomeDao, times(1)).save(income);
    }

    @Test
    void getIncomesByUser_returnsDaoResults() {
        List<Income> expected = List.of(sampleIncome());
        when(incomeDao.findByUserId(42L)).thenReturn(expected);

        List<Income> result = incomeService.getIncomesByUser(42L);

        assertThat(result).isEqualTo(expected);
        verify(incomeDao).findByUserId(42L);
    }

    @Test
    void getExtraIncomesByUser_returnsDaoResults() {
        List<Income> expected = List.of(sampleIncome());
        when(incomeDao.findExtraByUserId(42L)).thenReturn(expected);

        List<Income> result = incomeService.getExtraIncomesByUser(42L);

        assertThat(result).isEqualTo(expected);
        verify(incomeDao).findExtraByUserId(42L);
    }

    @Test
    void getTotalIncome_returnsDaoSum() {
        when(incomeDao.sumByUserId(42L)).thenReturn(new BigDecimal("300.00"));

        BigDecimal total = incomeService.getTotalIncome(42L);

        assertThat(total).isEqualByComparingTo("300.00");
    }

    @Test
    void getTotalExtraIncome_returnsDaoSum() {
        when(incomeDao.sumExtraByUserId(eq(42L))).thenReturn(new BigDecimal("75.50"));

        BigDecimal total = incomeService.getTotalExtraIncome(42L);

        assertThat(total).isEqualByComparingTo("75.50");
        verify(incomeDao).sumExtraByUserId(any());
    }
}
