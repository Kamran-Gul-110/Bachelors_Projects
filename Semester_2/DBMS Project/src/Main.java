import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

        System.out.println("donor added");
    }

}
class Main{
    public static void main(String[] args) throws Exception {
        Connect connect = new Connect();
        String name,fatherName,contact;
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your choice: ");
        int choice = scan.nextInt();
        scan.nextLine();
        switch(choice) {
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
        }
}
}