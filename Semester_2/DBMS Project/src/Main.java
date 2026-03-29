import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

class Person{
    String firstName;
    String lastName;
    String fatherName;
    String contact;
    Person(String firstName,String lastName,String fatherName,String contact){
        this.firstName = firstName;
        this.lastName = lastName;
        this.fatherName = fatherName;
        this.contact = contact;
    }
}
class Admin extends Person{
    private int adminId;
    private String adminPassword;
    Admin(String firstName,String lastName,String fatherName,String contact,int adminId,String adminPassword){
        super(firstName,lastName,fatherName,contact);
        this.adminId = adminId;
        this.adminPassword = adminPassword;
    }
}
class Donor extends Person{
    int age;
    String bloodGroup;
    String status;
    String city;
    Donor(String firstName,String lastName,String fatherName,String contact,int age,String bloodGroup,String city,String status){
    super(firstName,lastName,fatherName,contact);
    this.age = age;
    this.status = status;
    this.bloodGroup = bloodGroup;
    this.city = city;
    }
}
class Connect{
    Connection connection;
    Statement statement;
    void getConnection() throws Exception{
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root","root","Kami@123");
        statement = connection.createStatement();
    }

}
class Main{
    public static void main(String[] args) {
        Donor donor = new Donor("kamran","gul","ameen","030044545",20,"B+","Hangu","emergency");
}
}