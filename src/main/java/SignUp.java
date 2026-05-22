import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

public class SignUp extends JFrame {
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JPasswordField confirmPasswordField = new JPasswordField(18);

    public SignUp() {
        super("Finance Tracker - Create Account");
        initialize();
    }

    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 320);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Username:"), constraints);

        constraints.gridx = 1;
        panel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Password:"), constraints);

        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), constraints);

        constraints.gridx = 1;
        panel.add(confirmPasswordField, constraints);

        JButton registerButton = new JButton("Create Account");
        registerButton.addActionListener(this::onCreateAccount);
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(registerButton, constraints);

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(Login::new);
        });
        constraints.gridx = 1;
        panel.add(backButton, constraints);

        add(panel);
        setVisible(true);
    }

    private void onCreateAccount(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in every field.", "Sign Up Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (DataBaseManager.createUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Account created successfully! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(Login::new);
        } else {
            JOptionPane.showMessageDialog(this, "That username is already taken or invalid.", "Sign Up Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
