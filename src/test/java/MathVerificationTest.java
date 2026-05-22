import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

// Note - This file contains a independent class that won't be displayed in the application.
// This is just to verify each of the mathematical calculations are correct.

public class MathVerificationTest {

    private static final int PROJECTION_DAYS = 30;

    @Test
    public void testBasicCalculation() {
        // Test case: $3000 income, $2000 expenses, $1000 goal
        double income = 3000;
        double expense = 2000;
        double goal = 1000;

        double dailyNet = (income - expense) / PROJECTION_DAYS;
        assertEquals("Daily net should be $33.33", 33.33, dailyNet, 0.01);

        double minDays = goal / dailyNet;
        assertEquals("Minimum days should be 30.0", 30.0, minDays, 0.1);

        double avgDays = goal / (dailyNet * 1.10);
        assertEquals("Average days should be ~27.3", 27.3, avgDays, 0.1);

        double optDays = goal / (dailyNet * 1.25);
        assertEquals("Optimal days should be 24.0", 24.0, optDays, 0.1);
    }

    @Test
    public void testZeroGoal() {
        double income = 3000;
        double expense = 2000;
        double goal = 0;

        double dailyNet = (income - expense) / PROJECTION_DAYS;
        double minDays = goal / dailyNet;

        assertEquals("Minimum days for zero goal should be 0.0", 0.0, minDays, 0.01);
    }

    @Test
    public void testNegativeSavings() {
        double income = 1000;
        double expense = 2000;

        double dailyNet = (income - expense) / PROJECTION_DAYS;
        assertTrue("Daily net should be negative", dailyNet < 0);
    }

    @Test
    public void testHighGoal() {
        double income = 3000;
        double expense = 2000;
        double goal = 10000;

        double dailyNet = (income - expense) / PROJECTION_DAYS;
        double minDays = goal / dailyNet;

        assertEquals("Minimum days for $10k goal should be 300.0", 300.0, minDays, 1.0);
    }

    @Test
    public void testChartSeries() {
        // Verify that series accumulate correctly over days
        double income = 3000;
        double expense = 2000;

        double dailyNet = (income - expense) / PROJECTION_DAYS;
        double averageNet = dailyNet * 1.10;
        double optimalNet = dailyNet * 1.25;

        // After 30 days
        double min30 = dailyNet * 30;
        double avg30 = averageNet * 30;
        double opt30 = optimalNet * 30;

        assertEquals("Minimum series at day 30 should be ~1000", 1000.0, min30, 0.01);
        assertEquals("Average series at day 30 should be ~1100", 1100.0, avg30, 0.01);
        assertEquals("Optimal series at day 30 should be ~1250", 1250.0, opt30, 0.01);
    }

    @Test
    public void testEfficiencyMultipliers() {
        // Verify the efficiency multipliers are correct
        double baseRate = 100;
        double averageRate = baseRate * 1.10;
        double optimalRate = baseRate * 1.25;

        assertEquals("Average should be 10% improvement", 110, averageRate, 0.01);
        assertEquals("Optimal should be 25% improvement", 125, optimalRate, 0.01);
    }
}
