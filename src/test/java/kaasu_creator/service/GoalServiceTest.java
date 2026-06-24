package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kaasu_creator.dao.GoalDao;
import kaasu_creator.dao.RoadmapDao;
import kaasu_creator.model.Goal;
import kaasu_creator.model.Roadmap;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalDao goalDao;

    @Mock
    private RoadmapDao roadmapDao;

    @InjectMocks
    private GoalService goalService;

    private Goal goal(BigDecimal target, BigDecimal current) {
        return new Goal(1L, 5L, "Laptop", target, current,
                LocalDate.now().plusWeeks(10), null);
    }

    @Test
    void getProgress_returnsZeroWhenGoalMissing() {
        when(goalDao.findById(1L)).thenReturn(null);

        assertThat(goalService.getProgress(1L)).isEqualByComparingTo("0");
    }

    @Test
    void getProgress_returnsZeroWhenTargetIsZero() {
        when(goalDao.findById(1L)).thenReturn(goal(BigDecimal.ZERO, new BigDecimal("50")));

        assertThat(goalService.getProgress(1L)).isEqualByComparingTo("0");
    }

    @Test
    void getProgress_computesPercentage() {
        when(goalDao.findById(1L)).thenReturn(goal(new BigDecimal("200"), new BigDecimal("50")));

        // 50 / 200 = 0.25 -> 25%
        assertThat(goalService.getProgress(1L)).isEqualByComparingTo("25");
    }

    @Test
    void addSavings_delegatesToDao() {
        goalService.addSavings(1L, new BigDecimal("30"));

        verify(goalDao).addToCurrentAmount(1L, new BigDecimal("30"));
    }

    @Test
    void getGoalsByUser_returnsDaoResults() {
        List<Goal> expected = List.of(goal(new BigDecimal("100"), BigDecimal.ZERO));
        when(goalDao.findByUserId(5L)).thenReturn(expected);

        assertThat(goalService.getGoalsByUser(5L)).isEqualTo(expected);
    }

    @Test
    void createGoal_savesGoalAndGeneratesRoadmap() {
        goalService.createGoal(5L, "Phone", new BigDecimal("400"), LocalDate.now().plusWeeks(4));

        verify(goalDao).save(org.mockito.ArgumentMatchers.any(Goal.class));
        // generateRoadmap should persist weekly milestones
        verify(roadmapDao).saveAll(org.mockito.ArgumentMatchers.<List<Roadmap>>any());
    }
}
