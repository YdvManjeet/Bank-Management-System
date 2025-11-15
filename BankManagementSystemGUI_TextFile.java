import java.awt.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

class Transaction {
    String type; // "Deposit", "Withdraw", "FD_Transfer", "FD_Withdraw", "Card_Payment"
    double amount;
    String date;
    String description;
    String cardType; // "Debit" or "Credit" for card payments

    public Transaction(String type, double amount, String description, String cardType) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.cardType = cardType;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.date = sdf.format(new Date());
    }

    public String toString() {
        return type + "|" + amount + "|" + date + "|" + description + "|" + cardType;
    }

    public static Transaction fromString(String str) {
        String[] parts = str.split("\\|");
        if (parts.length >= 4) {
            Transaction t = new Transaction(parts[0], Double.parseDouble(parts[1]),
                    parts.length > 3 ? parts[3] : "", parts.length > 4 ? parts[4] : "");
            if (parts.length > 2)
                t.date = parts[2];
            return t;
        }
        return null;
    }
}

class User {
    String username;
    String password;
    double balance;
    double fdBalance;

    // Debit Card Details
    String debitCardNumber;
    String debitCVV;
    String debitExpiryDate;
    String debitCardType; // Visa, Master, RuPay
    String debitPIN; // 4-digit PIN
    boolean tapToPayEnabled;
    double monthlySpendingLimit;
    double dailySpendingLimit;
    double monthlySpent;
    double dailySpent;

    // Credit Card Details
    String creditCardNumber;
    String creditCVV;
    String creditExpiryDate;
    String creditPIN; // 4-digit PIN
    double creditLimit;
    double creditUsed;
    int pendingEMIs;
    int cibilScore;

    // Transaction History
    ArrayList<Transaction> transactionHistory;

    public User(String username, String password, double balance, double fdBalance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.fdBalance = fdBalance;

        // Initialize debit card with default values
        this.debitCardNumber = generateCardNumber();
        this.debitCVV = generateCVV();
        this.debitExpiryDate = generateExpiryDate();
        this.debitCardType = generateCardType();
        this.debitPIN = generatePIN();
        this.tapToPayEnabled = true;
        this.monthlySpendingLimit = 100000;
        this.dailySpendingLimit = 50000;
        this.monthlySpent = 0;
        this.dailySpent = 0;

        // Initialize credit card with default values
        this.creditCardNumber = generateCardNumber();
        this.creditCVV = generateCVV();
        this.creditExpiryDate = generateExpiryDate();
        this.creditPIN = generatePIN();
        this.creditLimit = 50000;
        this.creditUsed = 0;
        this.pendingEMIs = 0;
        this.cibilScore = 750;

        // Initialize transaction history
        this.transactionHistory = new ArrayList<>();
    }

    // Constructor for loading from file
    public User(String username, String password, double balance, double fdBalance,
            String debitCardNumber, String debitCVV, String debitExpiryDate, String debitCardType,
            String debitPIN, boolean tapToPayEnabled, double monthlySpendingLimit, double dailySpendingLimit,
            double monthlySpent, double dailySpent,
            String creditCardNumber, String creditCVV, String creditExpiryDate, String creditPIN,
            double creditLimit, double creditUsed, int pendingEMIs, int cibilScore,
            ArrayList<Transaction> transactionHistory) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.fdBalance = fdBalance;
        this.debitCardNumber = debitCardNumber;
        this.debitCVV = debitCVV;
        this.debitExpiryDate = debitExpiryDate;
        this.debitCardType = debitCardType;
        this.debitPIN = debitPIN;
        this.tapToPayEnabled = tapToPayEnabled;
        this.monthlySpendingLimit = monthlySpendingLimit;
        this.dailySpendingLimit = dailySpendingLimit;
        this.monthlySpent = monthlySpent;
        this.dailySpent = dailySpent;
        this.creditCardNumber = creditCardNumber;
        this.creditCVV = creditCVV;
        this.creditExpiryDate = creditExpiryDate;
        this.creditPIN = creditPIN;
        this.creditLimit = creditLimit;
        this.creditUsed = creditUsed;
        this.pendingEMIs = pendingEMIs;
        this.cibilScore = cibilScore;
        this.transactionHistory = transactionHistory != null ? transactionHistory : new ArrayList<>();
    }

    private String generateCardNumber() {
        Random rand = new Random();
        StringBuilder card = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (i > 0 && i % 4 == 0) {
                card.append(" ");
            }
            card.append(rand.nextInt(10));
        }
        return card.toString();
    }

    private String generateCVV() {
        Random rand = new Random();
        return String.format("%03d", rand.nextInt(1000));
    }

    private String generateExpiryDate() {
        Random rand = new Random();
        int month = rand.nextInt(12) + 1;
        int year = 2025 + rand.nextInt(5);
        return String.format("%02d/%d", month, year);
    }

    private String generateCardType() {
        String[] types = { "Visa", "Master", "RuPay" };
        Random rand = new Random();
        return types[rand.nextInt(types.length)];
    }

    private String generatePIN() {
        Random rand = new Random();
        return String.format("%04d", rand.nextInt(10000));
    }

    public void addTransaction(Transaction transaction) {
        if (transactionHistory == null) {
            transactionHistory = new ArrayList<>();
        }
        transactionHistory.add(transaction);
    }

    public void addInterestOnce() {
        double interest = fdBalance * 0.05; // 5% interest rate
        fdBalance += interest;
    }

    public double getAvailableCredit() {
        return creditLimit - creditUsed;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(username).append(",").append(password).append(",").append(balance).append(",").append(fdBalance)
                .append(",");
        sb.append(debitCardNumber).append(",").append(debitCVV).append(",").append(debitExpiryDate).append(",")
                .append(debitCardType).append(",");
        sb.append(debitPIN != null ? debitPIN : generatePIN()).append(",");
        sb.append(tapToPayEnabled).append(",").append(monthlySpendingLimit).append(",").append(dailySpendingLimit)
                .append(",");
        sb.append(monthlySpent).append(",").append(dailySpent).append(",");
        sb.append(creditCardNumber).append(",").append(creditCVV).append(",").append(creditExpiryDate).append(",");
        sb.append(creditPIN != null ? creditPIN : generatePIN()).append(",");
        sb.append(creditLimit).append(",").append(creditUsed).append(",").append(pendingEMIs).append(",")
                .append(cibilScore);

        // Add transaction count
        if (transactionHistory != null) {
            sb.append(",").append(transactionHistory.size());
            for (Transaction t : transactionHistory) {
                sb.append(",").append(t.toString().replace(",", ";;"));
            }
        } else {
            sb.append(",0");
        }

        return sb.toString();
    }
}

public class BankManagementSystemGUI_TextFile extends JFrame {
    private static final String FILE_NAME = "bank_users.txt";
    private static HashMap<String, User> users = new HashMap<>();
    private User currentUser;

    private CardLayout cardLayout;
    private JPanel mainPanel, loginPanel, dashboardPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel balanceLabel, fdLabel, messageLabel;
    private boolean interestAddedThisSession = false;

    public BankManagementSystemGUI_TextFile() {
        setTitle("🏦 Bank Of Modi - Banking System");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        loadUsers();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createLoginPanel();
        createDashboardPanel();

        mainPanel.add(loginPanel, "Login");
        mainPanel.add(dashboardPanel, "Dashboard");
        add(mainPanel);

        cardLayout.show(mainPanel, "Login");
        setVisible(true);
    }

    // ---------------------- LOGIN PANEL ----------------------
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Bank Logo and Name
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Logo (using emoji as logo)
        JLabel logoLabel = new JLabel("🏛", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.PLAIN, 48));

        // Bank Name
        JLabel bankName = new JLabel("Bank Of Modi", SwingConstants.CENTER);
        bankName.setFont(new Font("Serif", Font.BOLD, 32));
        bankName.setForeground(new Color(0, 51, 102));

        JLabel tagline = new JLabel("Trusted Banking Since 1947", SwingConstants.CENTER);
        tagline.setFont(new Font("Arial", Font.ITALIC, 12));
        tagline.setForeground(new Color(100, 100, 100));

        headerPanel.add(logoLabel, BorderLayout.NORTH);
        headerPanel.add(bankName, BorderLayout.CENTER);
        headerPanel.add(tagline, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(headerPanel, gbc);

        // Login Title
        JLabel title = new JLabel("🔏 Login to Your Account");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0, 51, 102));

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> loginUser());
        loginPanel.add(loginBtn, gbc);
    }

    // ---------------------- DASHBOARD PANEL ----------------------
    private void createDashboardPanel() {
        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BorderLayout());
        dashboardPanel.setBackground(new Color(250, 250, 255));

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        balanceLabel = new JLabel("Balance: ₹0", SwingConstants.CENTER);
        fdLabel = new JLabel("FD Balance: ₹0", SwingConstants.CENTER);
        messageLabel = new JLabel("Welcome!", SwingConstants.CENTER);

        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        fdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(Color.BLUE);

        topPanel.add(balanceLabel);
        topPanel.add(fdLabel);
        topPanel.add(messageLabel);
        dashboardPanel.add(topPanel, BorderLayout.NORTH);
    }

    private void updateDashboardButtons() {
        dashboardPanel.removeAll();

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Hide balances for admin
        if (!currentUser.username.equalsIgnoreCase("admin")) {
            topPanel.add(balanceLabel);
            topPanel.add(fdLabel);
        }
        topPanel.add(messageLabel);
        dashboardPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        if (currentUser.username.equalsIgnoreCase("admin")) {
            JButton addUserBtn = new JButton("Add New User");
            JButton editUserBtn = new JButton("Edit User");
            JButton deleteUserBtn = new JButton("Delete User");
            JButton viewAllBtn = new JButton("View All Users");
            JButton logoutBtn = new JButton("Logout");
            JButton exitBtn = new JButton("Exit");

            buttonPanel.add(addUserBtn);
            buttonPanel.add(editUserBtn);
            buttonPanel.add(deleteUserBtn);
            buttonPanel.add(viewAllBtn);
            buttonPanel.add(logoutBtn);
            buttonPanel.add(exitBtn);

            addUserBtn.addActionListener(e -> addNewUser());
            editUserBtn.addActionListener(e -> editUser());
            deleteUserBtn.addActionListener(e -> deleteUser());
            viewAllBtn.addActionListener(e -> viewAllUsers());
            logoutBtn.addActionListener(e -> logout());
            exitBtn.addActionListener(e -> {
                saveUsers();
                System.exit(0);
            });
        } else {
            JButton viewBtn = new JButton("View Balance");
            JButton depositBtn = new JButton("Deposit");
            JButton withdrawBtn = new JButton("Withdraw");
            JButton fdBtn = new JButton("Transfer to FD");
            JButton withdrawFDBtn = new JButton("Withdraw from FD");
            JButton debitCardBtn = new JButton("💳 Debit Card");
            JButton creditCardBtn = new JButton("💳 Credit Card");
            JButton payCardBtn = new JButton("💳 Pay from Card");
            JButton historyBtn = new JButton("📃 Transaction History");
            JButton logoutBtn = new JButton("Logout");
            JButton exitBtn = new JButton("Exit");

            buttonPanel.setLayout(new GridLayout(5, 2, 10, 10));
            buttonPanel.add(viewBtn);
            buttonPanel.add(depositBtn);
            buttonPanel.add(withdrawBtn);
            buttonPanel.add(fdBtn);
            buttonPanel.add(withdrawFDBtn);
            buttonPanel.add(debitCardBtn);
            buttonPanel.add(creditCardBtn);
            buttonPanel.add(payCardBtn);
            buttonPanel.add(historyBtn);
            buttonPanel.add(logoutBtn);
            buttonPanel.add(exitBtn);

            viewBtn.addActionListener(e -> viewBalance());
            depositBtn.addActionListener(e -> depositMoney());
            withdrawBtn.addActionListener(e -> withdrawMoney());
            fdBtn.addActionListener(e -> transferToFD());
            withdrawFDBtn.addActionListener(e -> withdrawFromFD());
            debitCardBtn.addActionListener(e -> viewDebitCard());
            creditCardBtn.addActionListener(e -> viewCreditCard());
            payCardBtn.addActionListener(e -> payFromCard());
            historyBtn.addActionListener(e -> viewTransactionHistory());
            logoutBtn.addActionListener(e -> logout());
            exitBtn.addActionListener(e -> {
                saveUsers();
                System.exit(0);
            });
        }

        dashboardPanel.add(buttonPanel, BorderLayout.CENTER);
        dashboardPanel.revalidate();
        dashboardPanel.repaint();
    }

    // ---------------------- FUNCTIONALITY ----------------------
    private void loginUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (users.containsKey(username)) {
            User u = users.get(username);
            if (u.password.equals(password)) {
                currentUser = u;
                messageLabel.setText("Welcome, " + username + "!");
                updateDashboardButtons();

                // Add interest only once per login (not repeatedly)
                interestAddedThisSession = false;
                if (!username.equalsIgnoreCase("admin")) {
                    currentUser.addInterestOnce();
                    interestAddedThisSession = true;
                }

                updateDashboard();
                cardLayout.show(mainPanel, "Dashboard");
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateDashboard() {
        balanceLabel.setText("Balance: ₹" + currentUser.balance);
        fdLabel.setText("FD Balance: ₹" + currentUser.fdBalance);
    }

    private void viewBalance() {
        // ❌ No interest added again here!
        updateDashboard();
        JOptionPane.showMessageDialog(this,
                "Balance: ₹" + currentUser.balance + "\nFixed Deposit: ₹" + currentUser.fdBalance,
                "Account Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void depositMoney() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        if (input != null && !input.isEmpty()) {
            try {
                double amt = Double.parseDouble(input);
                if (amt > 0) {
                    currentUser.balance += amt;
                    currentUser.addTransaction(new Transaction("Deposit", amt, "Cash Deposit", ""));
                    saveUsers();
                    updateDashboard();
                    messageLabel.setText("Deposited ₹" + amt + " successfully!");
                } else {
                    messageLabel.setText("Invalid amount!");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter a valid number!");
            }
        }
    }

    private void withdrawMoney() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        if (input != null && !input.isEmpty()) {
            try {
                double amt = Double.parseDouble(input);
                if (amt > 0 && amt <= currentUser.balance) {
                    currentUser.balance -= amt;
                    currentUser.addTransaction(new Transaction("Withdraw", amt, "Cash Withdrawal", ""));
                    saveUsers();
                    updateDashboard();
                    messageLabel.setText("Withdrawn ₹" + amt + " successfully!");
                } else {
                    messageLabel.setText("Insufficient balance or invalid amount!");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter a valid number!");
            }
        }
    }

    private void transferToFD() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to transfer to FD:");
        if (input != null && !input.isEmpty()) {
            try {
                double amt = Double.parseDouble(input);
                if (amt > 0 && amt <= currentUser.balance) {
                    currentUser.balance -= amt;
                    currentUser.fdBalance += amt;
                    currentUser.addTransaction(new Transaction("FD_Transfer", amt, "Transfer to Fixed Deposit", ""));
                    saveUsers();
                    updateDashboard();
                    messageLabel.setText("Transferred ₹" + amt + " to FD successfully!");
                } else {
                    messageLabel.setText("Invalid or insufficient balance!");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter a valid number!");
            }
        }
    }

    private void withdrawFromFD() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to withdraw from FD:");
        if (input != null && !input.isEmpty()) {
            try {
                double amt = Double.parseDouble(input);
                if (amt > 0 && amt <= currentUser.fdBalance) {
                    currentUser.fdBalance -= amt;
                    currentUser.balance += amt;
                    currentUser
                            .addTransaction(new Transaction("FD_Withdraw", amt, "Withdrawal from Fixed Deposit", ""));
                    saveUsers();
                    updateDashboard();
                    messageLabel.setText("Withdrawn ₹" + amt + " from FD successfully!");
                } else {
                    messageLabel.setText("Insufficient FD balance or invalid amount!");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter a valid number!");
            }
        }
    }

    // ---------------------- ADMIN FEATURES ----------------------
    private void addNewUser() {
        JTextField userField = new JTextField();
        JTextField passField = new JTextField();
        JTextField balanceField = new JTextField();
        JTextField fdField = new JTextField();

        Object[] fields = {
                "Username:", userField,
                "Password:", passField,
                "Initial Balance:", balanceField,
                "Initial FD Balance:", fdField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New User", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String uname = userField.getText().trim();
            String pass = passField.getText().trim();

            if (uname.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
                return;
            }

            if (users.containsKey(uname)) {
                JOptionPane.showMessageDialog(this, "User already exists!");
                return;
            }

            try {
                double bal = Double.parseDouble(balanceField.getText().trim());
                double fd = Double.parseDouble(fdField.getText().trim());
                users.put(uname, new User(uname, pass, bal, fd));
                saveUsers();
                JOptionPane.showMessageDialog(this, "New user '" + uname + "' added successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Enter valid numeric values for balances!");
            }
        }
    }

    private void editUser() {
        String uname = JOptionPane.showInputDialog(this, "Enter username to edit:");
        if (uname == null || uname.trim().isEmpty())
            return;
        if (!users.containsKey(uname)) {
            JOptionPane.showMessageDialog(this, "User not found!");
            return;
        }

        User u = users.get(uname);
        JTextField passField = new JTextField(u.password);
        JTextField balanceField = new JTextField(String.valueOf(u.balance));
        JTextField fdField = new JTextField(String.valueOf(u.fdBalance));

        Object[] fields = {
                "Password:", passField,
                "Balance:", balanceField,
                "FD Balance:", fdField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit User: " + uname, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                u.password = passField.getText().trim();
                u.balance = Double.parseDouble(balanceField.getText().trim());
                u.fdBalance = Double.parseDouble(fdField.getText().trim());
                saveUsers();
                JOptionPane.showMessageDialog(this, "User '" + uname + "' updated successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Enter valid numbers for balances!");
            }
        }
    }

    private void deleteUser() {
        String uname = JOptionPane.showInputDialog(this, "Enter username to delete:");
        if (uname == null || uname.trim().isEmpty())
            return;
        if (!users.containsKey(uname)) {
            JOptionPane.showMessageDialog(this, "User not found!");
            return;
        }

        if (uname.equalsIgnoreCase("admin")) {
            JOptionPane.showMessageDialog(this, "Admin account cannot be deleted!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete '" + uname + "'?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            users.remove(uname);
            saveUsers();
            JOptionPane.showMessageDialog(this, "User '" + uname + "' deleted successfully!");
        }
    }

    private void viewAllUsers() {
        String[] columns = { "Username", "Balance (₹)", "FD Balance (₹)" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (User u : users.values()) {
            if (!u.username.equalsIgnoreCase("admin")) {
                model.addRow(new Object[] { u.username, u.balance, u.fdBalance });
            }
        }

        JTable table = new JTable(model);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "All Users", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------- CARD DETAILS ----------------------
    private void viewDebitCard() {
        User u = currentUser;
        JDialog dialog = new JDialog(this, "💳 Debit Card Details", true);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(102, 126, 234));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(102, 126, 234));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        // Bank Logo and Name
        JPanel bankHeader = new JPanel();
        bankHeader.setLayout(new BoxLayout(bankHeader, BoxLayout.Y_AXIS));
        bankHeader.setBackground(new Color(102, 126, 234));
        bankHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankLogo = new JLabel("🏛", SwingConstants.CENTER);
        bankLogo.setFont(new Font("Arial", Font.PLAIN, 32));
        bankLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankName = new JLabel("Bank Of Modi", SwingConstants.CENTER);
        bankName.setFont(new Font("Serif", Font.BOLD, 20));
        bankName.setForeground(Color.WHITE);
        bankName.setAlignmentX(Component.CENTER_ALIGNMENT);

        bankHeader.add(bankLogo);
        bankHeader.add(Box.createVerticalStrut(5));
        bankHeader.add(bankName);

        JLabel titleLabel = new JLabel("💳 Debit Card", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        headerPanel.add(bankHeader, BorderLayout.CENTER);
        headerPanel.add(titleLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Card Visual
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(new Color(102, 126, 234));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(118, 75, 162), 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        cardPanel.setPreferredSize(new Dimension(450, 200));

        // Bank Logo and Name on Card
        JPanel cardTop = new JPanel(new BorderLayout());
        cardTop.setOpaque(false);
        JLabel cardLogo = new JLabel("🏦");
        cardLogo.setFont(new Font("Arial", Font.PLAIN, 24));
        JLabel cardBankName = new JLabel("Bank Of Modi");
        cardBankName.setFont(new Font("Serif", Font.BOLD, 16));
        cardBankName.setForeground(Color.WHITE);
        cardTop.add(cardLogo, BorderLayout.WEST);
        cardTop.add(cardBankName, BorderLayout.EAST);
        cardPanel.add(cardTop, BorderLayout.NORTH);

        JLabel cardNumberLabel = new JLabel(formatCardNumber(u.debitCardNumber));
        cardNumberLabel.setFont(new Font("Courier", Font.BOLD, 22));
        cardNumberLabel.setForeground(Color.WHITE);
        cardNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardNumberLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        cardPanel.add(cardNumberLabel, BorderLayout.CENTER);

        JPanel cardBottomPanel = new JPanel(new BorderLayout());
        cardBottomPanel.setOpaque(false);
        JLabel cvvLabel = new JLabel("CVV: " + u.debitCVV);
        cvvLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cvvLabel.setForeground(Color.WHITE);
        JLabel expiryLabel = new JLabel("Exp: " + u.debitExpiryDate);
        expiryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        expiryLabel.setForeground(Color.WHITE);
        cardBottomPanel.add(cvvLabel, BorderLayout.WEST);
        cardBottomPanel.add(expiryLabel, BorderLayout.EAST);
        cardPanel.add(cardBottomPanel, BorderLayout.SOUTH);

        contentPanel.add(cardPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Card Info Section
        JPanel infoPanel = createInfoSection("Card Information");
        JPanel infoGrid = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel typePanel = createInfoBox("Card Type", u.debitCardType, new Color(102, 126, 234));
        JPanel tapPanel = createInfoBox("Tap to Pay", u.tapToPayEnabled ? "✅ Enabled" : "❌ Disabled",
                new Color(102, 126, 234));
        infoGrid.add(typePanel);
        infoGrid.add(tapPanel);
        infoPanel.add(infoGrid);
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Spending Limits
        JPanel limitsPanel = createInfoSection("Spending Limits");

        // Monthly Limit
        double monthlyPercent = (u.monthlySpent / u.monthlySpendingLimit) * 100;
        limitsPanel.add(createLimitRow("Monthly Limit", u.monthlySpendingLimit, u.monthlySpent,
                u.monthlySpendingLimit - u.monthlySpent, monthlyPercent, new Color(102, 126, 234)));
        limitsPanel.add(Box.createVerticalStrut(10));

        // Daily Limit
        double dailyPercent = (u.dailySpent / u.dailySpendingLimit) * 100;
        limitsPanel.add(createLimitRow("Daily Limit", u.dailySpendingLimit, u.dailySpent,
                u.dailySpendingLimit - u.dailySpent, dailyPercent, new Color(240, 147, 251)));

        contentPanel.add(limitsPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(102, 126, 234));
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createInfoSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(102, 126, 234), 2),
                        title, 0, 0, new Font("Arial", Font.BOLD, 16), new Color(102, 126, 234)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return panel;
    }

    private JPanel createInfoBox(String label, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Arial", Font.BOLD, 12));
        labelLbl.setForeground(color);
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Arial", Font.BOLD, 16));
        valueLbl.setForeground(Color.BLACK);
        panel.add(labelLbl, BorderLayout.NORTH);
        panel.add(valueLbl, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLimitRow(String label, double limit, double spent, double remaining, double percent,
            Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel labelLbl = new JLabel(label + ":");
        labelLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel limitLbl = new JLabel("₹" + String.format("%.2f", limit));
        limitLbl.setFont(new Font("Arial", Font.BOLD, 14));
        limitLbl.setForeground(color);
        topPanel.add(labelLbl, BorderLayout.WEST);
        topPanel.add(limitLbl, BorderLayout.EAST);

        // Progress Bar
        JPanel progressContainer = new JPanel(new BorderLayout());
        progressContainer.setBackground(Color.WHITE);
        progressContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        JPanel progressBg = new JPanel(new BorderLayout());
        progressBg.setBackground(new Color(224, 224, 224));
        progressBg.setPreferredSize(new Dimension(400, 20));
        progressBg.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JPanel progressFill = new JPanel();
        progressFill.setBackground(color);
        progressFill.setPreferredSize(new Dimension((int) (400 * percent / 100), 20));
        progressBg.add(progressFill, BorderLayout.WEST);
        progressContainer.add(progressBg, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        JLabel spentLbl = new JLabel("Spent: ₹" + String.format("%.2f", spent));
        spentLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        spentLbl.setForeground(new Color(100, 100, 100));
        JLabel remainingLbl = new JLabel("Remaining: ₹" + String.format("%.2f", remaining));
        remainingLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        remainingLbl.setForeground(new Color(100, 100, 100));
        bottomPanel.add(spentLbl, BorderLayout.WEST);
        bottomPanel.add(remainingLbl, BorderLayout.EAST);

        panel.add(topPanel);
        panel.add(progressContainer);
        panel.add(bottomPanel);

        return panel;
    }

    private void viewCreditCard() {
        User u = currentUser;
        JDialog dialog = new JDialog(this, "💳 Credit Card Details", true);
        dialog.setSize(500, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 147, 251));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 147, 251));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        // Bank Logo and Name
        JPanel bankHeader = new JPanel();
        bankHeader.setLayout(new BoxLayout(bankHeader, BoxLayout.Y_AXIS));
        bankHeader.setBackground(new Color(240, 147, 251));
        bankHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankLogo = new JLabel("🏦", SwingConstants.CENTER);
        bankLogo.setFont(new Font("Arial", Font.PLAIN, 32));
        bankLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bankName = new JLabel("Bank Of Modi", SwingConstants.CENTER);
        bankName.setFont(new Font("Serif", Font.BOLD, 20));
        bankName.setForeground(Color.WHITE);
        bankName.setAlignmentX(Component.CENTER_ALIGNMENT);

        bankHeader.add(bankLogo);
        bankHeader.add(Box.createVerticalStrut(5));
        bankHeader.add(bankName);

        JLabel titleLabel = new JLabel("💳 Credit Card", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        headerPanel.add(bankHeader, BorderLayout.CENTER);
        headerPanel.add(titleLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Card Visual
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(new Color(240, 147, 251));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 87, 108), 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        cardPanel.setPreferredSize(new Dimension(450, 200));

        // Bank Logo and Name on Card
        JPanel cardTop = new JPanel(new BorderLayout());
        cardTop.setOpaque(false);
        JLabel cardLogo = new JLabel("🏦");
        cardLogo.setFont(new Font("Arial", Font.PLAIN, 24));
        JLabel cardBankName = new JLabel("Bank Of Modi");
        cardBankName.setFont(new Font("Serif", Font.BOLD, 16));
        cardBankName.setForeground(Color.WHITE);
        cardTop.add(cardLogo, BorderLayout.WEST);
        cardTop.add(cardBankName, BorderLayout.EAST);
        cardPanel.add(cardTop, BorderLayout.NORTH);

        JLabel cardNumberLabel = new JLabel(formatCardNumber(u.creditCardNumber));
        cardNumberLabel.setFont(new Font("Courier", Font.BOLD, 22));
        cardNumberLabel.setForeground(Color.WHITE);
        cardNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardNumberLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        cardPanel.add(cardNumberLabel, BorderLayout.CENTER);

        JPanel cardBottomPanel = new JPanel(new BorderLayout());
        cardBottomPanel.setOpaque(false);
        JLabel cvvLabel = new JLabel("CVV: " + u.creditCVV);
        cvvLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cvvLabel.setForeground(Color.WHITE);
        JLabel expiryLabel = new JLabel("Exp: " + u.creditExpiryDate);
        expiryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        expiryLabel.setForeground(Color.WHITE);
        cardBottomPanel.add(cvvLabel, BorderLayout.WEST);
        cardBottomPanel.add(expiryLabel, BorderLayout.EAST);
        cardPanel.add(cardBottomPanel, BorderLayout.SOUTH);

        contentPanel.add(cardPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Credit Information Section
        JPanel creditPanel = createInfoSection("Credit Information");
        JPanel creditGrid = new JPanel(new GridLayout(1, 2, 10, 10));

        double availableCredit = u.getAvailableCredit();
        Color availColor = availableCredit > 0 ? new Color(0, 184, 148) : new Color(214, 48, 49);
        JPanel limitPanel = createInfoBox("Credit Limit", "₹" + String.format("%.2f", u.creditLimit),
                new Color(214, 48, 49));
        JPanel availPanel = createInfoBox("Available Credit", "₹" + String.format("%.2f", availableCredit), availColor);
        creditGrid.add(limitPanel);
        creditGrid.add(availPanel);
        creditPanel.add(creditGrid);
        creditPanel.add(Box.createVerticalStrut(10));

        // Credit Utilization
        double utilizationPercent = u.creditLimit > 0 ? (u.creditUsed / u.creditLimit) * 100 : 0;
        creditPanel.add(createLimitRow("Credit Used", u.creditLimit, u.creditUsed,
                availableCredit, utilizationPercent, new Color(240, 147, 251)));
        creditPanel.add(Box.createVerticalStrut(10));

        // Pending EMIs
        JPanel emiPanel = new JPanel(new BorderLayout());
        emiPanel.setBackground(new Color(255, 236, 210));
        emiPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 112, 85), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel emiLabel = new JLabel("Pending EMIs", SwingConstants.CENTER);
        emiLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emiLabel.setForeground(new Color(225, 112, 85));
        JLabel emiValue = new JLabel(String.valueOf(u.pendingEMIs), SwingConstants.CENTER);
        emiValue.setFont(new Font("Arial", Font.BOLD, 32));
        emiValue.setForeground(new Color(225, 112, 85));
        emiPanel.add(emiLabel, BorderLayout.NORTH);
        emiPanel.add(emiValue, BorderLayout.CENTER);
        creditPanel.add(emiPanel);

        contentPanel.add(creditPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // CIBIL Score Section
        String cibilStatus;
        Color cibilColor;
        Color cibilBgColor;
        if (u.cibilScore >= 750) {
            cibilStatus = "Excellent";
            cibilColor = new Color(0, 128, 0);
            cibilBgColor = new Color(232, 245, 233);
        } else if (u.cibilScore >= 700) {
            cibilStatus = "Good";
            cibilColor = new Color(0, 150, 0);
            cibilBgColor = new Color(200, 230, 201);
        } else if (u.cibilScore >= 650) {
            cibilStatus = "Fair";
            cibilColor = new Color(255, 165, 0);
            cibilBgColor = new Color(255, 243, 224);
        } else {
            cibilStatus = "Poor";
            cibilColor = new Color(255, 0, 0);
            cibilBgColor = new Color(255, 235, 238);
        }

        JPanel cibilPanel = new JPanel(new BorderLayout());
        cibilPanel.setBackground(cibilBgColor);
        cibilPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, cibilColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(BorderFactory.createLineBorder(cibilColor, 2),
                                "CIBIL Score", 0, 0, new Font("Arial", Font.BOLD, 16), cibilColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10))));

        JPanel cibilCenter = new JPanel();
        cibilCenter.setLayout(new BoxLayout(cibilCenter, BoxLayout.Y_AXIS));
        cibilCenter.setBackground(cibilBgColor);
        cibilCenter.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cibilScoreLabel = new JLabel(String.valueOf(u.cibilScore), SwingConstants.CENTER);
        cibilScoreLabel.setFont(new Font("Arial", Font.BOLD, 56));
        cibilScoreLabel.setForeground(cibilColor);
        cibilScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cibilStatusLabel = new JLabel(cibilStatus, SwingConstants.CENTER);
        cibilStatusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        cibilStatusLabel.setForeground(cibilColor);
        cibilStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cibilStatusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        cibilCenter.add(cibilScoreLabel);
        cibilCenter.add(cibilStatusLabel);
        cibilPanel.add(cibilCenter, BorderLayout.CENTER);

        contentPanel.add(cibilPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 147, 251));
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private String formatCardNumber(String cardNumber) {
        // Format card number with spaces for display (remove existing spaces first,
        // then add them)
        String cleaned = cardNumber.replaceAll("\\s+", "");
        return cleaned.replaceAll("(.{4})", "$1 ").trim();
    }

    // ---------------------- TRANSACTION HISTORY & CARD PAYMENT
    // ----------------------
    private void viewTransactionHistory() {
        User u = currentUser;
        JDialog dialog = new JDialog(this, "📃 Transaction History", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("🏦 Bank Of Modi - Transaction History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Date & Time", "Type", "Amount (₹)", "Description", "Card Type" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (u.transactionHistory != null && !u.transactionHistory.isEmpty()) {
            // Sort by date (newest first)
            ArrayList<Transaction> sorted = new ArrayList<>(u.transactionHistory);
            sorted.sort((a, b) -> b.date.compareTo(a.date));

            for (Transaction t : sorted) {
                model.addRow(new Object[] {
                        t.date,
                        t.type,
                        String.format("%.2f", t.amount),
                        t.description,
                        t.cardType.isEmpty() ? "-" : t.cardType
                });
            }
        } else {
            model.addRow(new Object[] { "No transactions yet", "", "", "", "" });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0, 51, 102));
        table.getTableHeader().setForeground(Color.WHITE);

        // Center align amount column
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
        
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void payFromCard() {
        User u = currentUser;

        // Step 1: Card Number
        String cardNum = JOptionPane.showInputDialog(this, "Enter Card Number (16 digits):");
        if (cardNum == null)
            return;
        cardNum = cardNum.replaceAll("\\s+", "");

        // Determine card type
        boolean isDebit = cardNum.equals(u.debitCardNumber.replaceAll("\\s+", ""));
        boolean isCredit = cardNum.equals(u.creditCardNumber.replaceAll("\\s+", ""));

        if (!isDebit && !isCredit) {
            JOptionPane.showMessageDialog(this, "Card number not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cardType = isDebit ? "Debit" : "Credit";
        String cardCVV = isDebit ? u.debitCVV : u.creditCVV;
        String cardExpiry = isDebit ? u.debitExpiryDate : u.creditExpiryDate;
        String cardPIN = isDebit ? u.debitPIN : u.creditPIN;

        // Step 2: CVV
        String cvv = JOptionPane.showInputDialog(this, "Enter CVV:");
        if (cvv == null)
            return;
        if (!cvv.equals(cardCVV)) {
            JOptionPane.showMessageDialog(this, "Invalid CVV!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 3: Expiry Date
        String expiry = JOptionPane.showInputDialog(this, "Enter Expiry Date (MM/YYYY):");
        if (expiry == null)
            return;
        if (!expiry.equals(cardExpiry)) {
            JOptionPane.showMessageDialog(this, "Invalid Expiry Date!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 4: PIN
        JPasswordField pinField = new JPasswordField();
        int pinOption = JOptionPane.showConfirmDialog(this,
                new Object[] { "Enter 4-digit PIN:", pinField },
                "Card PIN", JOptionPane.OK_CANCEL_OPTION);
        if (pinOption != JOptionPane.OK_OPTION)
            return;

        String pin = new String(pinField.getPassword());
        if (!pin.equals(cardPIN)) {
            JOptionPane.showMessageDialog(this, "Invalid PIN!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 5: Amount
        String amountStr = JOptionPane.showInputDialog(this, "Enter payment amount:");
        if (amountStr == null || amountStr.trim().isEmpty())
            return;

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Process payment
            if (isDebit) {
                // Check limits
                if (u.dailySpent + amount > u.dailySpendingLimit) {
                    JOptionPane.showMessageDialog(this,
                            "Daily spending limit exceeded! Remaining: ₹" + (u.dailySpendingLimit - u.dailySpent),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (u.monthlySpent + amount > u.monthlySpendingLimit) {
                    JOptionPane.showMessageDialog(this,
                            "Monthly spending limit exceeded! Remaining: ₹" + (u.monthlySpendingLimit - u.monthlySpent),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (amount > u.balance) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                u.balance -= amount;
                u.dailySpent += amount;
                u.monthlySpent += amount;
            } else {
                // Credit card
                if (u.creditUsed + amount > u.creditLimit) {
                    JOptionPane.showMessageDialog(this,
                            "Credit limit exceeded! Available: ₹" + u.getAvailableCredit(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                u.creditUsed += amount;
            }

            // Add transaction
            u.addTransaction(new Transaction("Card_Payment", amount,
                    "Payment via " + cardType + " Card", cardType));
            saveUsers();
            updateDashboard();

            JOptionPane.showMessageDialog(this,
                    "Payment of ₹" + amount + " successful using " + cardType + " Card!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            messageLabel.setText("Payment of ₹" + amount + " successful!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        saveUsers();
        usernameField.setText("");
        passwordField.setText("");
        currentUser = null;
        cardLayout.show(mainPanel, "Login");
    }

    // ---------------------- FILE HANDLING ----------------------
    private void loadUsers() {
        users.clear();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            users.put("admin", new User("admin", "1234", 10000, 5000));
            users.put("john", new User("john", "john123", 8000, 3000));
            saveUsers();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue; // Skip empty lines
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    // Old format - create user with default card details
                    users.put(parts[0],
                            new User(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
                } else if (parts.length >= 20) {
                    // Handle old format (20 fields) - add default PINs
                    String debitPIN = parts.length > 20 ? parts[20] : "1234";
                    String creditPIN = parts.length > 21 ? parts[21] : "1234";
                    ArrayList<Transaction> transactions = new ArrayList<>();

                    if (parts.length > 22) {
                        int txCount = Integer.parseInt(parts[22]);
                        for (int i = 0; i < txCount && (23 + i) < parts.length; i++) {
                            String txStr = parts[23 + i].replace(";;", ",");
                            Transaction tx = Transaction.fromString(txStr);
                            if (tx != null)
                                transactions.add(tx);
                        }
                    }

                    if (parts.length >= 22) {
                        // New format with PINs
                        users.put(parts[0], new User(
                                parts[0], parts[1],
                                Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                                parts[4], parts[5], parts[6], parts[7],
                                parts.length > 20 ? parts[20] : "1234", // debitPIN
                                Boolean.parseBoolean(parts[8]),
                                Double.parseDouble(parts[9]), Double.parseDouble(parts[10]),
                                Double.parseDouble(parts[11]), Double.parseDouble(parts[12]),
                                parts[13], parts[14], parts[15],
                                parts.length > 21 ? parts[21] : "1234", // creditPIN
                                Double.parseDouble(parts[16]), Double.parseDouble(parts[17]),
                                Integer.parseInt(parts[18]), Integer.parseInt(parts[19]),
                                transactions));
                    } else {
                        // Old format without PINs (20 fields)
                        users.put(parts[0], new User(
                                parts[0], parts[1],
                                Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                                parts[4], parts[5], parts[6], parts[7],
                                "1234", // default debitPIN
                                Boolean.parseBoolean(parts[8]),
                                Double.parseDouble(parts[9]), Double.parseDouble(parts[10]),
                                Double.parseDouble(parts[11]), Double.parseDouble(parts[12]),
                                parts[13], parts[14], parts[15],
                                "1234", // default creditPIN
                                Double.parseDouble(parts[16]), Double.parseDouble(parts[17]),
                                Integer.parseInt(parts[18]), Integer.parseInt(parts[19]),
                                transactions));
                    }
                } else {
                    System.out.println("Warning: Skipping line with unexpected format: " + line);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (User u : users.values()) {
                pw.println(u.toString());
            }
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // ---------------------- MAIN ----------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankManagementSystemGUI_TextFile());
    }
}
