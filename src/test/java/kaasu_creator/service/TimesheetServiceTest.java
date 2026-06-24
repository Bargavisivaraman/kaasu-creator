package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kaasu_creator.model.TimesheetEntry;
import kaasu_creator.model.TimesheetJobSummary;
import kaasu_creator.repository.TimesheetRepository;

@ExtendWith(MockitoExtension.class)
class TimesheetServiceTest {

    @Mock
    private TimesheetRepository repository;

    @InjectMocks
    private TimesheetService timesheetService;

    @Test
    void save_delegatesToRepository() {
        TimesheetEntry entry = new TimesheetEntry();

        timesheetService.save(entry);

        verify(repository).save(entry);
    }

    @Test
    void getEntriesByUser_returnsRepositoryResults() {
        List<TimesheetEntry> expected = List.of(new TimesheetEntry());
        when(repository.findByUserId(3L)).thenReturn(expected);

        assertThat(timesheetService.getEntriesByUser(3L)).isEqualTo(expected);
    }

    @Test
    void deleteEntry_returnsRowsAffected() {
        when(repository.deleteByIdAndUserId(9L, 3L)).thenReturn(1);

        assertThat(timesheetService.deleteEntry(9L, 3L)).isEqualTo(1);
    }

    @Test
    void getTotalEarned_returnsRepositorySum() {
        when(repository.sumEarnedAmountByUserId(3L)).thenReturn(new BigDecimal("125.00"));

        assertThat(timesheetService.getTotalEarned(3L)).isEqualByComparingTo("125.00");
    }

    @Test
    void getTotalHours_returnsRepositorySum() {
        when(repository.sumHoursWorkedByUserId(3L)).thenReturn(new BigDecimal("12.5"));

        assertThat(timesheetService.getTotalHours(3L)).isEqualByComparingTo("12.5");
    }

    @Test
    void getJobSummariesByUser_returnsRepositoryResults() {
        List<TimesheetJobSummary> expected = List.of();
        when(repository.findJobSummariesByUserId(3L)).thenReturn(expected);

        assertThat(timesheetService.getJobSummariesByUser(3L)).isEqualTo(expected);
    }
}
