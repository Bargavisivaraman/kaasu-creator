package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kaasu_creator.model.Job;
import kaasu_creator.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository repository;

    @InjectMocks
    private JobService jobService;

    @Test
    void addJob_delegatesToRepository() {
        Job job = new Job();
        jobService.addJob(job);
        verify(repository).save(job);
    }

    @Test
    void updateJob_delegatesToRepository() {
        Job job = new Job();
        jobService.updateJob(job);
        verify(repository).update(job);
    }

    @Test
    void deleteJob_delegatesToRepository() {
        jobService.deleteJob(5L);
        verify(repository).deleteById(5L);
    }

    @Test
    void getJobsByUser_returnsRepositoryResults() {
        List<Job> expected = List.of(new Job());
        when(repository.findByUserId(3L)).thenReturn(expected);
        assertThat(jobService.getJobsByUser(3L)).isEqualTo(expected);
    }

    @Test
    void getJobsWithSummary_returnsRepositoryResults() {
        List<Job> expected = List.of(new Job());
        when(repository.findWithSummaryByUserId(3L)).thenReturn(expected);
        assertThat(jobService.getJobsWithSummary(3L)).isEqualTo(expected);
    }

    @Test
    void getJobByIdAndUser_returnsRepositoryResult() {
        Job job = new Job();
        when(repository.findByIdAndUserId(7L, 3L)).thenReturn(Optional.of(job));
        assertThat(jobService.getJobByIdAndUser(7L, 3L)).contains(job);
    }
}
