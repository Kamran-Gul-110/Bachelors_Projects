import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;

class Person{
    String name;
    String fatherName;
    String contact;
    Person(String name,String fatherName,String contact){
        this.name = name;
        this.fatherName = fatherName;
        this.contact = contact;
    }
}
class Admin extends Person{
    private int adminId;
    private String adminPassword;
    Admin(String name,String fatherName,String contact,int adminId,String adminPassword){
        super(name,fatherName,contact);
        this.adminId = adminId;
        this.adminPassword = adminPassword;
    }
}
class Donor extends Person{
    int age;
    String bloodGroup;
    String city;
    Donor(String name,String fatherName,String contact,int age,String bloodGroup,String city){
    super(name,fatherName,contact);
    this.age = age;
    this.bloodGroup = bloodGroup;
    this.city = city;
    }
}
class Connect {

    public Connection getConnection() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/blood_bank";
            String user = "root";
            String pass = "Kami@123";
            return DriverManager.getConnection(url, user, pass);
    }
    //    Method to validate admin login
    boolean validateAdmin(String adminId, String adminPassword){
        try(Connection connection =getConnection()){
            boolean validAdmin = false;
            String query = "select admin_id,admin_password from admins where admin_id = ? and admin_password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, adminId);
            preparedStatement.setString(2, adminPassword);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                validAdmin = true;
            }
            return validAdmin;
        }catch (Exception e){
            System.out.println("Connection Failed! validateAdmin");
        }
        return false;
    }
    //    Method 1
    void addDonor(Donor donor) {
        try(Connection connection =getConnection()) {
            String query = "INSERT INTO donors(name,father_name,blood_group,phone_number,city) values(?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, donor.name);
            preparedStatement.setString(2, donor.fatherName);
            preparedStatement.setString(3, donor.bloodGroup);
            preparedStatement.setString(4, donor.contact);
            preparedStatement.setString(5, donor.city);
            preparedStatement.executeUpdate();
            System.out.println("Donor added with id: " + getDonorId(donor.name, donor.fatherName));
        }catch (Exception e){
            System.out.println("Connection Failed! Check output console");
        }

    }

    //    Method 2
    int getDonorId(String name, String fatherName) {
        try(Connection connection =getConnection()) {
            int id = -1; // -1 means "Not Found"
            String query = "SELECT donor_id FROM donors WHERE name = ? AND father_name = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, fatherName);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                id = rs.getInt("donor_id");
            }
            return id;
        }catch (Exception e){
            System.out.println("Connection Failed! Check output console");
        }
        return -1;
    }

    boolean validateDonorTime(int donorId) {
        try(Connection connection =getConnection()) {
            // 1. Get the most recent donation date for this ID
            String query = "SELECT MAX(donation_date) AS last_date FROM donations WHERE donor_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, donorId);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                Date sqlDate = rs.getDate("last_date");

                if (sqlDate != null) {
                    LocalDate lastDonation = sqlDate.toLocalDate();
                    LocalDate today = LocalDate.now();

                    // 3. Epoch Day math (Safe for New Year transitions)
                    long total1 = lastDonation.toEpochDay();
                    long total2 = today.toEpochDay();
                    long gap = total2 - total1;

                    if (gap < 90) {
                        System.out.println("Wait! It has only been " + gap + " days since the last donation.");
                        return false;
                    }
                }
            }
            // If no records found or gap is 90+ days
            return true;
        } catch (Exception e) {
            System.out.println("Connection Failed! validateDonorTime: " + e.getMessage());
            return false;
        }
    }
    //    Method 3
    void recordDonation(int donorId, int units) {
        try(Connection connection =getConnection()) {
            String bloodGroup = "";

//      The query will insert the units donated into the donations table
            String query = "INSERT into donations(donor_id,units_donated) values(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, donorId);
            preparedStatement.setInt(2, units);
            preparedStatement.executeUpdate();

//        The query2 will find the blood group of that donor later used to insert the stock in that group
            String query2 = "SELECT blood_group FROM donors WHERE donor_id = ?";
            PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setInt(1, donorId);
            ResultSet rs = preparedStatement2.executeQuery();
            if (rs.next()) {
                bloodGroup = rs.getString("blood_group");
            }

//        The query3 is actually inserting the units in blood_stock
            String query3 = "UPDATE blood_stock set total_units = total_units+? where blood_group = ?";
            PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
            preparedStatement3.setInt(1, units);
            preparedStatement3.setString(2, bloodGroup);
            preparedStatement3.executeUpdate();


//        As the donor has donated so query4 will change the status of donor to instant
            String query4 = "UPDATE donors set status = 'instant' where donor_id = ?";
            PreparedStatement preparedStatement4 = connection.prepareStatement(query4);
            preparedStatement4.setInt(1, donorId);
            preparedStatement4.executeUpdate();

            System.out.println("Donation recorded");
        }catch (Exception e){
            System.out.println("Connection Failed! Check output console");
        }
    }

    //    Method 4
    void showHistory(int donorId) {
        try(Connection connection =getConnection()) {
            boolean found = false;
            String query = "Select * from donations where donor_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, donorId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                found = true;
                System.out.print("Units donated: " + rs.getInt("units_donated") + "   ");
                System.out.println("Date: " + rs.getDate("donation_date"));
            }
            if (!found) {
                System.out.println("No donation from this donor yet...");
            }
        }catch (Exception e){
            System.out.println("Connection Failed! Check output console");
        }
    }

    //    Method 5
    void manageStock(String bloodGroup, int units, char choice){
        try{
        try(Connection connection =getConnection()) {
            PreparedStatement preparedStatement;
            String query;
            if (choice == 'a') {
                query = "UPDATE blood_stock set total_units = total_units+? where blood_group=?";
            } else {
                query = "UPDATE blood_stock set total_units = total_units-? where blood_group=?";
            }
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, units);
            preparedStatement.setString(2, bloodGroup);
            preparedStatement.executeUpdate();
            System.out.println("Operation Successful");
        }catch (SQLException e){
            System.out.println("No enough quantity\nTry contacting willing donors");
            searchByBloodGroup(bloodGroup);
        }
    } catch (Exception e) {
            throw new RuntimeException(e);
        }}

    //    Method 6
    void searchByBloodGroup(String bloodGroup){
        try(Connection connection =getConnection()){
        int count = 1;
        boolean found = false;
        String query = "Select * from donors where blood_group=? and status=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, bloodGroup);
        preparedStatement.setString(2, "emergency");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            found = true;
            System.out.printf("Donor number: %d\n", count);
            System.out.printf("Donor id: %d | Name: %s | Father Name: %s | Contact: %s | City: %s\n",
                    rs.getInt("donor_id"), rs.getString("name"), rs.getString("father_name"),
                    rs.getString("phone_number"), rs.getString("city"));

            count++;
        }
        if (!found) {
            System.out.printf("Sorry!!!, No donor found for %s blood group", bloodGroup);
        }
    } catch (Exception e) {
            throw new RuntimeException(e);
        }}

    //    Method 7
    void searchByCity(String city){
        try(Connection connection =getConnection()){
            boolean found = false;

            String query = "Select * from donors where city=? and status=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, "emergency");
            ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            found = true;
            System.out.printf("Donor id: %d | Name: %s | Father Name: %s | Contact: %s | Blood Group: %s\n",
                    rs.getInt("donor_id"), rs.getString("name"), rs.getString("father_name"),
                    rs.getString("phone_number"), rs.getString("blood_group"));

        }
        if (!found) {
            System.out.printf("Sorry!!!, No donor found in %s City", city);
        }
    }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

class Main {
        public static void main(String[] args) {
            Connect connect = new Connect();
            String name, fatherName, contact;
            Scanner scan = new Scanner(System.in);
            while (true) {
                System.out.println("Blood Bank Menu");
                System.out.println("==================");
                System.out.println("1. Login as admin");
                System.out.println("2. Continue as User");

                System.out.print("Enter your choice: ");
                int choice = scan.nextInt();
                scan.nextLine();

                if (choice == 1) {
                    while(true) {
                    System.out.print("Please enter your admin id: ");
                    String adminId = scan.nextLine();
                    System.out.print("Please enter your admin password: ");
                    String adminPassword = scan.nextLine();
                    if (connect.validateAdmin(adminId, adminPassword)) {
                        int adminChoice =0;
                        while (adminChoice != 6) {
                            System.out.println("1. Add Donor");
                            System.out.println("2. Search Donor Id");
                            System.out.println("3. Record Donation");
                            System.out.println("4. Show History of a Donor");
                            System.out.println("5. Manage Blood Bank Stock");
                            System.out.println("6. Exit");

                            System.out.print("Enter your choice: ");
                            adminChoice = scan.nextInt();
                            scan.nextLine();
                            switch (adminChoice) {
                                case 1:
                                    System.out.print("Donor name: ");
                                    name = scan.nextLine();
                                    System.out.print("Father name: ");
                                    fatherName = scan.nextLine();
                                    System.out.print("Contact: ");
                                    contact = scan.nextLine();
                                    System.out.print("Age of Donor: ");
                                    int age = scan.nextInt();
                                    scan.nextLine();
                                    System.out.print("Blood Group: ");
                                    String bloodGroup = scan.nextLine();
                                    System.out.print("City: ");
                                    String city = scan.nextLine();
                                    Donor donor = new Donor(name, fatherName, contact, age, bloodGroup, city);
                                    connect.addDonor(donor);
                                    System.out.print("Wants to donate blood right now?(y/n) ");
                                    choice = scan.next().toLowerCase().charAt(0);
                                    if (choice == 'y') {
                                        System.out.print("Enter Donor id: ");
                                        int donorId = scan.nextInt();
                                        System.out.print("Units donated: ");
                                        int units = scan.nextInt();
                                        connect.recordDonation(donorId, units);
                                    }
                                    break;
                                case 2:
                                    System.out.print("Enter Donor Name: ");
                                    String sName = scan.nextLine();
                                    System.out.print("Enter Father's Name: ");
                                    String sFather = scan.nextLine();

                                    int foundId = connect.getDonorId(sName, sFather);

                                    if (foundId != -1) {
                                        System.out.println("ID Found: " + foundId);
                                        System.out.println("Record donation now? (y/n) ");
                                        choice = scan.next().toLowerCase().charAt(0);
                                        if (choice == 'y') {
                                            if (connect.validateDonorTime(foundId)) {
                                                System.out.print("Units donated: ");
                                                int units = scan.nextInt();
                                                connect.recordDonation(foundId, units);
                                            } else
                                                System.out.println("Cannot donate again before 90 days gap");
                                        }
                                    } else {
                                        System.out.println("[Error] Donor not found in registry.");
                                    }
                                    break;
                                case 3:
                                    System.out.print("Enter Donor id: ");
                                    int donorId = scan.nextInt();
                                    if (connect.validateDonorTime(donorId)) {
                                        System.out.print("Units donated: ");
                                        int units = scan.nextInt();
                                        connect.recordDonation(donorId, units);
                                    } else
                                        System.out.println("Cannot donate again before 90 days gap");
                                    break;
                                case 4:
                                    System.out.print("Enter donor id: ");
                                    donorId = scan.nextInt();
                                    connect.showHistory(donorId);
                                    break;
                                case 5:
                                    System.out.print("Add to stock or remove: (a/r) ");
                                    char ch = scan.next().toLowerCase().charAt(0);
                                    System.out.print("Enter blood group: ");
                                    bloodGroup = scan.next();
                                    System.out.print("Enter units of blood: ");
                                    int units = scan.nextInt();
                                    connect.manageStock(bloodGroup, units, ch);
                                    break;
                            }
                            System.out.println("\n\n\n");

                        }
                        break;
                    } else {
                        System.out.println("No such admin found....");
                    }
                }
                } else if (choice == 2) {
                    int userChoice =0;
                    while (userChoice != 5) {
                        System.out.println("1. Register yourself as a donor");
                        System.out.println("2. Search donors by blood group");
                        System.out.println("3. Search donors by City");
                        System.out.println("4. See your donation history");
                        System.out.println("5. Exit");

                        System.out.print("Enter your choice: ");
                        userChoice = scan.nextInt();
                        scan.nextLine();
                        switch (userChoice) {
                            case 1:
                                System.out.print("Donor name: ");
                                name = scan.nextLine();
                                System.out.print("Father name: ");
                                fatherName = scan.nextLine();
                                System.out.print("Contact: ");
                                contact = scan.nextLine();
                                System.out.print("Age of Donor: ");
                                int age = scan.nextInt();
                                if (age >= 18) {
                                    scan.nextLine();
                                    System.out.print("Blood Group: ");
                                    String bloodGroup = scan.nextLine();
                                    System.out.print("City: ");
                                    String city = scan.nextLine();
                                    Donor donor = new Donor(name, fatherName, contact, age, bloodGroup, city);
                                    connect.addDonor(donor);
                                } else {
                                    System.out.println("You are under 18, cannot donate blood now\nWe appreciate your courage and dedication");
                                }
                                break;
                            case 2:
                                System.out.print("Enter blood group you want to search: ");
                                String bg = scan.nextLine();
                                connect.searchByBloodGroup(bg);
                                break;
                            case 3:
                                System.out.print("Enter city you want to search for donors in: ");
                                String city = scan.nextLine();
                                connect.searchByCity(city);
                                break;

                            case 4:
                                System.out.print("Enter your donor id: ");
                                int donorId = scan.nextInt();
                                connect.showHistory(donorId);
                                break;
                            }
                        System.out.println("\n\n\n");
                    }
                    }else{
                        System.out.println("Invalid choice");
                }
            }
        }
}