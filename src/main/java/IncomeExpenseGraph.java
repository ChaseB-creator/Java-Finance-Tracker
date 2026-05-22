import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class IncomeExpenseGraph {
    private static final int CHART_DAYS = 30;

    public static ChartPanel createChartPanel(double income, double expense, double goal) {
        XYSeries minimumSeries = new XYSeries("Minimum");
        XYSeries averageSeries = new XYSeries("Average");
        XYSeries optimalSeries = new XYSeries("Optimal");

        double dailyNet = (income - expense) / CHART_DAYS;
        double averageNet = dailyNet * 1.10;
        double optimalNet = dailyNet * 1.25;

        if (dailyNet > 0) {
            int projectedDays = (int) Math.max(CHART_DAYS, Math.ceil(goal / dailyNet) + 5);
            for (int day = 0; day <= projectedDays; day++) {
                minimumSeries.add(day, dailyNet * day);
                averageSeries.add(day, averageNet * day);
                optimalSeries.add(day, optimalNet * day);
            }
        } else {
            for (int day = 0; day <= CHART_DAYS; day++) {
                minimumSeries.add(day, 0);
                averageSeries.add(day, 0);
                optimalSeries.add(day, 0);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(minimumSeries);
        dataset.addSeries(averageSeries);
        dataset.addSeries(optimalSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Savings projection vs Goal",
                "Days",
                "Projected Balance ($)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        return new ChartPanel(chart);
    }
}
