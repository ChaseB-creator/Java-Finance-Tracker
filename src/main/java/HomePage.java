import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class HomePage extends JFrame {
    private final UserSession session;
    private final JTextField incomeField = new JTextField(12);
    private final JTextField expenseField = new JTextField(12);
    private final JTextField goalField = new JTextField(12);
    private static final int PROJECTION_DAYS = 30;
    private final JLabel minLabel = new JLabel("Minimum time: N/A");
    private final JLabel avgLabel = new JLabel("Average time: N/A");
    private final JLabel optLabel = new JLabel("Optimal time: N/A");
    private final JPanel chartContainer = new JPanel(new BorderLayout());

    public HomePage(UserSession session) {
        super("Finance Tracker - Dashboard");
        this.session = session;
        initialize();
    }

    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 640);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Savings Projection Inputs"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(new JLabel("Monthly Income ($):"), constraints);

        constraints.gridx = 1;
        inputPanel.add(incomeField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(new JLabel("Monthly Expenses ($):"), constraints);

        constraints.gridx = 1;
        inputPanel.add(expenseField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        inputPanel.add(new JLabel("Financial Goal ($):"), constraints);

        constraints.gridx = 1;
        inputPanel.add(goalField, constraints);

        JButton calculateButton = new JButton("Calculate Projection");
        calculateButton.addActionListener(this::onCalculate);
        constraints.gridx = 0;
        constraints.gridy = 3;
        inputPanel.add(calculateButton, constraints);

        JButton saveButton = new JButton("Save To Account");
        saveButton.addActionListener(this::onSave);
        constraints.gridx = 1;
        inputPanel.add(saveButton, constraints);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(Login::new);
        });
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        inputPanel.add(logoutButton, constraints);

        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Projection Results"));
        GridBagConstraints statsConstraints = new GridBagConstraints();
        statsConstraints.insets = new Insets(4, 4, 4, 4);
        statsConstraints.anchor = GridBagConstraints.WEST;
        statsConstraints.gridx = 0;
        statsConstraints.gridy = 0;
        statsPanel.add(minLabel, statsConstraints);
        statsConstraints.gridy = 1;
        statsPanel.add(avgLabel, statsConstraints);
        statsConstraints.gridy = 2;
        statsPanel.add(optLabel, statsConstraints);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(statsPanel, BorderLayout.SOUTH);

        chartContainer.setBorder(BorderFactory.createTitledBorder("Goal Projection Chart"));
        chartContainer.add(IncomeExpenseGraph.createChartPanel(0, 0, 0), BorderLayout.CENTER);

        add(new JLabel("Welcome, " + session.getUsername()), BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(chartContainer, BorderLayout.CENTER);

        loadLastRecord();
        setVisible(true);
    }

    private void loadLastRecord() {
        Optional<DataBaseManager.FinanceRecord> record = DataBaseManager.getLastFinanceRecord(session.getUserId());
        if (record.isPresent()) {
            DataBaseManager.FinanceRecord last = record.get();
            incomeField.setText(String.valueOf(last.getIncome()));
            expenseField.setText(String.valueOf(last.getExpense()));
            goalField.setText(String.valueOf(last.getGoal()));
            updateChart(last.getIncome(), last.getExpense(), last.getGoal());
        }
    }

    private void onCalculate(ActionEvent event) {
        try {
            double income = parseDouble(incomeField.getText());
            double expense = parseDouble(expenseField.getText());
            double goal = parseDouble(goalField.getText());
            updateChart(income, expense, goal);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onSave(ActionEvent event) {
        try {
            double income = parseDouble(incomeField.getText());
            double expense = parseDouble(expenseField.getText());
            double goal = parseDouble(goalField.getText());
            if (DataBaseManager.saveFinanceData(session.getUserId(), income, expense, goal, PROJECTION_DAYS)) {
                JOptionPane.showMessageDialog(this, "Finance goal saved to your account.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                updateChart(income, expense, goal);
            } else {
                JOptionPane.showMessageDialog(this, "Unable to save your goal. Please try again.", "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateChart(double income, double expense, double goal) {
        chartContainer.removeAll();
        chartContainer.add(IncomeExpenseGraph.createChartPanel(income, expense, goal), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();

        double dailyNet = (income - expense) / PROJECTION_DAYS;
        if (dailyNet <= 0) {
            minLabel.setText("Minimum time: No positive savings projected.");
            avgLabel.setText("Average time: No positive savings projected.");
            optLabel.setText("Optimal time: No positive savings projected.");
            return;
        }

        double minDays = goal / dailyNet;
        double avgDays = goal / (dailyNet * 1.10);
        double optDays = goal / (dailyNet * 1.25);

        minLabel.setText(String.format("Minimum time: %.1f days", minDays));
        avgLabel.setText(String.format("Average time: %.1f days", avgDays));
        optLabel.setText(String.format("Optimal time: %.1f days", optDays));
    }

    private double parseDouble(String text) {
        try {
            double value = Double.parseDouble(text.trim());
            if (value < 0) {
                throw new IllegalArgumentException("Please enter a non-negative numeric value.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Please enter valid numeric values.");
        }
    }


}
