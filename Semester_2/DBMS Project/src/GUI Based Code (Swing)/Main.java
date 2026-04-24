import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

// 1. MODELS
class Person {
    String name, fatherName, contact;

    Person(String name, String fatherName, String contact) {
        this.name = name;
        this.fatherName = fatherName;
        this.contact = contact;
    }
}

class Admin extends Person {
    private int adminId;
    private String adminPassword;

    Admin(String name, String fatherName, String contact, int adminId, String adminPassword) {
        super(name, fatherName, contact);
        this.adminId = adminId;
        this.adminPassword = adminPassword;
    }
}

class Donor extends Person {
    int age;
    String bloodGroup, city;

    Donor(String name, String fatherName, String contact, int age, String bloodGroup, String city) {
        super(name, fatherName, contact);
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.city = city;
    }
}

// 2. DATABASE ENGINE
class Connect {
    private final String URL = "jdbc:mysql://localhost:3306/blood_bank";
    private final String USER = "root";
    private final String PASS = "Kami@123";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    boolean validateAdmin(String adminId, String adminPassword) {
        String query = "SELECT admin_id FROM admins WHERE admin_id = ? AND admin_password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, adminId);
            ps.setString(2, adminPassword);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "DB Error: " + e.getMessage());
            return false;
        }
    }

    String addDonor(Donor donor) {
        String query = "INSERT INTO donors(name,father_name,blood_group,phone_number,city) values(?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, donor.name);
            ps.setString(2, donor.fatherName);
            ps.setString(3, donor.bloodGroup);
            ps.setString(4, donor.contact);
            ps.setString(5, donor.city);
            ps.executeUpdate();
            int id = getDonorId(donor.name, donor.fatherName);
            return "Success! Donor added with ID: " + id;
        } catch (Exception e) {
            return "Error adding donor: " + e.getMessage();
        }
    }

    int getDonorId(String name, String fatherName) {
        String query = "SELECT donor_id FROM donors WHERE name = ? AND father_name = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, fatherName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("donor_id");
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    boolean validateDonorTime(int donorId) {
        String query = "SELECT MAX(donation_date) AS last_date FROM donations WHERE donor_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, donorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date sqlDate = rs.getDate("last_date");
                    if (sqlDate != null) {
                        long total1 = sqlDate.toLocalDate().toEpochDay();
                        long total2 = LocalDate.now().toEpochDay();
                        if ((total2 - total1) < 90) return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    String recordDonation(int donorId, int units) {
        try (Connection conn = getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("INSERT into donations(donor_id,units_donated) values(?,?)");
            ps1.setInt(1, donorId);
            ps1.setInt(2, units);
            ps1.executeUpdate();

            String bg = "";
            PreparedStatement ps2 = conn.prepareStatement("SELECT blood_group FROM donors WHERE donor_id = ?");
            ps2.setInt(1, donorId);
            ResultSet rs = ps2.executeQuery();
            if (rs.next()) bg = rs.getString("blood_group");

            PreparedStatement ps3 = conn.prepareStatement("UPDATE blood_stock set total_units = total_units+? where blood_group = ?");
            ps3.setInt(1, units);
            ps3.setString(2, bg);
            ps3.executeUpdate();

            PreparedStatement ps4 = conn.prepareStatement("UPDATE donors set status = 'instant' where donor_id = ?");
            ps4.setInt(1, donorId);
            ps4.executeUpdate();

            return "Donation recorded successfully for ID: " + donorId;
        } catch (Exception e) {
            return "Error recording donation: " + e.getMessage();
        }
    }

    String showHistory(int donorId) {
        StringBuilder sb = new StringBuilder("History for Donor " + donorId + ":\n\n");
        boolean found = false;
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("Select * from donations where donor_id=?")) {
            ps.setInt(1, donorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                found = true;
                sb.append("Units: ").append(rs.getInt("units_donated"))
                        .append(" | Date: ").append(rs.getDate("donation_date")).append("\n");
            }
            if (!found) sb.append("No previous donations found.");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return sb.toString();
    }

    String manageStock(String bg, int units, char choice) {
        String query = (choice == 'a') ?
                "UPDATE blood_stock set total_units = total_units+? where blood_group=?" :
                "UPDATE blood_stock set total_units = total_units-? where blood_group=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, units);
            ps.setString(2, bg);
            ps.executeUpdate();
            return "Stock updated successfully!";
        } catch (Exception e) {
            return "Error updating stock: " + e.getMessage();
        }
    }

    String search(String column, String value) {
        StringBuilder sb = new StringBuilder("Search Results:\n\n");
        boolean found = false;
        String query = "Select * from donors where " + column + "=? and status='emergency'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                found = true;
                sb.append("ID: ").append(rs.getInt("donor_id"))
                        .append(" | Name: ").append(rs.getString("name"))
                        .append(" | Contact: ").append(rs.getString("phone_number"))
                        .append(" | BG: ").append(rs.getString("blood_group")).append("\n");
            }
            if (!found) sb.append("No donors found.");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return sb.toString();
    }
}


// 3. SWING USER INTERFACE
public class Main extends JFrame {
    private Connect db = new Connect();
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public Main() {
        setTitle("Al-Shifa Blood Bank Management System");
        setSize(550, 450); // Made slightly taller to fit new buttons
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createAdminPanel(), "Admin");
        mainPanel.add(createUserPanel(), "User");

        add(mainPanel);
    }

    // --- MAIN RUNNER ---
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }

    // --- SCREEN 1: HOME / LOGIN ---
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel title = new JLabel("Al-Shifa Blood Bank", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JButton adminBtn = new JButton("Login as Admin");
        JButton userBtn = new JButton("Continue as User");

        adminBtn.addActionListener(e -> adminLoginDialog());
        userBtn.addActionListener(e -> cardLayout.show(mainPanel, "User"));

        panel.add(title);
        panel.add(adminBtn);
        panel.add(userBtn);
        return panel;
    }

    private void adminLoginDialog() {
        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();
        Object[] message = {"Admin ID:", idField, "Password:", passField};

        int option = JOptionPane.showConfirmDialog(this, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (db.validateAdmin(idField.getText(), new String(passField.getPassword()))) {
                cardLayout.show(mainPanel, "Admin");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- SCREEN 2: ADMIN DASHBOARD ---
    private JPanel createAdminPanel() {
        // Increased Grid rows to 7 to fit the new search button
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton addDonorBtn = new JButton("Add Donor");
        JButton searchIdBtn = new JButton("Search Donor Id"); // NEW BUTTON
        JButton recordBtn = new JButton("Record Donation");
        JButton historyBtn = new JButton("Show History of a Donor");
        JButton stockBtn = new JButton("Manage Blood Bank Stock");
        JButton logoutBtn = new JButton("Logout");

        // 1. Add Donor Logic
        addDonorBtn.addActionListener(e -> {
            JTextField name = new JTextField(), fname = new JTextField(), contact = new JTextField();
            JTextField age = new JTextField(), bg = new JTextField(), city = new JTextField();
            Object[] msg = {"Name:", name, "Father Name:", fname, "Contact:", contact, "Age:", age, "Blood Group:", bg, "City:", city};

            if (JOptionPane.showConfirmDialog(this, msg, "Add Donor", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    Donor d = new Donor(name.getText(), fname.getText(), contact.getText(), Integer.parseInt(age.getText()), bg.getText(), city.getText());
                    JOptionPane.showMessageDialog(this, db.addDonor(d));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Age input!");
                }
            }
        });

        // 2. Search Donor ID Logic (NEW)
        searchIdBtn.addActionListener(e -> {
            JTextField name = new JTextField(), fname = new JTextField();
            Object[] msg = {"Donor Name:", name, "Father's Name:", fname};
            if (JOptionPane.showConfirmDialog(this, msg, "Search Donor ID", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                int id = db.getDonorId(name.getText(), fname.getText());
                if (id != -1) {
                    JOptionPane.showMessageDialog(this, "ID Found: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Donor not found in registry.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 3. Record Donation Logic
        recordBtn.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog("Enter Donor ID:");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                if (db.validateDonorTime(id)) {
                    String units = JOptionPane.showInputDialog("Enter Units Donated:");
                    if (units != null) {
                        JOptionPane.showMessageDialog(this, db.recordDonation(id, Integer.parseInt(units)));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot donate! 90-day gap required.", "Blocked", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // 4. Show History Logic
        historyBtn.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog("Enter Donor ID:");
            if (idStr != null) showTextDialog("Donor History", db.showHistory(Integer.parseInt(idStr)));
        });

        // 5. Manage Stock Logic
        stockBtn.addActionListener(e -> {
            JTextField bg = new JTextField(), units = new JTextField(), action = new JTextField("a (add) or r (remove)");
            Object[] msg = {"Blood Group:", bg, "Units:", units, "Action:", action};
            if (JOptionPane.showConfirmDialog(this, msg, "Manage Stock", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(this, db.manageStock(bg.getText(), Integer.parseInt(units.getText()), action.getText().charAt(0)));
            }
        });

        // 6. Exit Logic
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        panel.add(new JLabel("Admin Dashboard", SwingConstants.CENTER));
        panel.add(addDonorBtn);
        panel.add(searchIdBtn);
        panel.add(recordBtn);
        panel.add(historyBtn);
        panel.add(stockBtn);
        panel.add(logoutBtn);
        return panel;
    }

    // --- SCREEN 3: USER DASHBOARD ---
    private JPanel createUserPanel() {
        // Increased grid rows to 6 to fit new buttons
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton registerBtn = new JButton("Register yourself as a donor"); // NEW BUTTON
        JButton searchBGBtn = new JButton("Search donors by blood group");
        JButton searchCityBtn = new JButton("Search donors by City");
        JButton userHistoryBtn = new JButton("See your donation history"); // NEW BUTTON
        JButton backBtn = new JButton("Exit");

        // 1. Register User Logic (With Age Validation!)
        registerBtn.addActionListener(e -> {
            JTextField name = new JTextField(), fname = new JTextField(), contact = new JTextField();
            JTextField age = new JTextField(), bg = new JTextField(), city = new JTextField();
            Object[] msg = {"Name:", name, "Father Name:", fname, "Contact:", contact, "Age:", age, "Blood Group:", bg, "City:", city};

            if (JOptionPane.showConfirmDialog(this, msg, "Register as Donor", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    int userAge = Integer.parseInt(age.getText());
                    if (userAge >= 18) {
                        Donor d = new Donor(name.getText(), fname.getText(), contact.getText(), userAge, bg.getText(), city.getText());
                        JOptionPane.showMessageDialog(this, db.addDonor(d));
                    } else {
                        JOptionPane.showMessageDialog(this, "You are under 18, cannot donate blood now.\nWe appreciate your courage and dedication!", "Underage", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Age input!");
                }
            }
        });

        // 2. Search BG Logic
        searchBGBtn.addActionListener(e -> {
            String bg = JOptionPane.showInputDialog("Enter Blood Group (e.g., O+):");
            if (bg != null) showTextDialog("Search Results", db.search("blood_group", bg));
        });

        // 3. Search City Logic
        searchCityBtn.addActionListener(e -> {
            String city = JOptionPane.showInputDialog("Enter City:");
            if (city != null) showTextDialog("Search Results", db.search("city", city));
        });

        // 4. See Donation History Logic
        userHistoryBtn.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog("Enter your Donor ID:");
            if (idStr != null) showTextDialog("Your History", db.showHistory(Integer.parseInt(idStr)));
        });

        // 5. Exit Logic
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        panel.add(new JLabel("User Portal", SwingConstants.CENTER));
        panel.add(registerBtn);
        panel.add(searchBGBtn);
        panel.add(searchCityBtn);
        panel.add(userHistoryBtn);
        panel.add(backBtn);
        return panel;
    }

    // Helper to show large results in a clean text box
    private void showTextDialog(String title, String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), title, JOptionPane.INFORMATION_MESSAGE);
    }
}