import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

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
class Connect{
    Connection connection;
    public void getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/blood_bank";
        String user = "root";
        String pass = "Kami@123";
        connection= DriverManager.getConnection(url, user, pass);
    }
    void addDonor(Donor donor) throws Exception{
        getConnection();
        String query="INSERT INTO donors(name,father_name,blood_group,phone_number,city) values(?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,donor.name);
        preparedStatement.setString(2,donor.fatherName);
        preparedStatement.setString(3,donor.bloodGroup);
        preparedStatement.setString(4,donor.contact);
        preparedStatement.setString(5,donor.city);
        preparedStatement.executeUpdate();
        System.out.println("Donor added with id: "+getDonorId(donor.name,donor.fatherName));
        connection.close();
    }
//    Method 2
    int getDonorId(String name, String fatherName) throws Exception {
    getConnection();
    int id = -1; // -1 means "Not Found"
    String query = "SELECT donor_id FROM donors WHERE name = ? AND father_name = ?";

    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, name);
    preparedStatement.setString(2, fatherName);

    ResultSet rs = preparedStatement.executeQuery();
    if (rs.next()) {
        id = rs.getInt("donor_id");
    }
    connection.close();
    return id;
}
//    Method 3

    void recordDonation(int donorId,int units) throws Exception{
        getConnection();
        String bloodGroup = "";

//      The query will insert the units donated into the donations table
        String query = "INSERT into donations(donor_id,units_donated) values(?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1,donorId);
        preparedStatement.setInt(2,units);
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
        preparedStatement3.setInt(1,units);
        preparedStatement3.setString(2,bloodGroup);
        preparedStatement3.executeUpdate();


//        As the donor has donated so query4 will change the status of donor to instant
        String query4 = "UPDATE donors set status = 'instant' where donor_id = ?";
        PreparedStatement preparedStatement4 = connection.prepareStatement(query4);
        preparedStatement4.setInt(1,donorId);
        preparedStatement4.executeUpdate();

        System.out.println("Donation recorded");
        connection.close();
    }

//    Method 4
    void showHistory(int donorId) throws Exception{
        getConnection();
        boolean found = false;
        String query = "Select * from donations where donor_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1,donorId);
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()){
            found = true;
            System.out.print("Units donated: " + rs.getInt("units_donated") +"   ");
            System.out.println("Date: " + rs.getDate("donation_date"));
        }
        if(!found){
            System.out.println("No donation from this donor yet...");
        }
        connection.close();
    }

//    Method 5
    void manageStock(String bloodGroup,int units,char choice) throws Exception{
        getConnection();
        PreparedStatement preparedStatement;
        String query;
        if(choice=='a' || choice=='A'){
            query = "UPDATE blood_stock set total_units = total_units+? where blood_group=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,units);
            preparedStatement.setString(2,bloodGroup);
            preparedStatement.executeUpdate();
            System.out.println("Blood added to stock");
        }
        else{
            query = "UPDATE blood_stock set total_units = total_units-? where blood_group=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,units);
            preparedStatement.setString(2,bloodGroup);
            preparedStatement.executeUpdate();
            System.out.println("Blood removed from stock");
        }

        connection.close();
    }

}
class Main{
    public static void main(String[] args) throws Exception {
        Connect connect = new Connect();
        String name,fatherName,contact;
        Scanner scan = new Scanner(System.in);
        System.out.println("Blood Bank Menu");
        System.out.println("==================");
        System.out.println("1. Add Donor");
        System.out.println("2. Search Donor Id");
        System.out.println("3. Record Donation");
        System.out.println("4. Show History of a Donor");
        System.out.println("5. Manage Blood Bank Stock");
        System.out.print("Enter your choice: ");
        int choice = scan.nextInt();
        scan.nextLine();
        switch(choice){
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
                Donor donor = new Donor(name,fatherName,contact,age,bloodGroup,city);
                connect.addDonor(donor);
                System.out.print("Wants to donate blood right now?(y/n) ");
                choice = scan.next().toLowerCase().charAt(0);
                if(choice=='y'){
                    System.out.print("Enter Donor id: ");
                    int donorId = scan.nextInt();
                    System.out.print("Units donated: ");
                    int units = scan.nextInt();
                    connect.recordDonation(donorId,units);
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
                    System.out.println("Record donation now? (y/n)");
                    choice =scan.next().charAt(0);
                    if(choice=='y' || choice=='Y'){
                        System.out.print("Enter Donor id: ");
                        int donorId = scan.nextInt();
                        System.out.print("Units donated: ");
                        int units = scan.nextInt();
                        connect.recordDonation(donorId,units);
                    }
                } else {
                    System.out.println("[Error] Donor not found in registry.");
                }
                break;
            case 3:
                System.out.print("Enter Donor id: ");
                int donorId = scan.nextInt();
                System.out.print("Units donated: ");
                int units = scan.nextInt();
                connect.recordDonation(donorId,units);
                break;
            case 4:
                System.out.print("Enter donor id: ");
                donorId = scan.nextInt();
                connect.showHistory(donorId);
                break;
            case 5:
                System.out.print("Add to stock or remove: (a/r)");
                char ch = scan.next().charAt(0);
                System.out.print("Enter blood group: ");
                bloodGroup = scan.next();
                System.out.print("Enter units of blood: ");
                units = scan.nextInt();
                connect.manageStock(bloodGroup,units,ch);
        }

}
}