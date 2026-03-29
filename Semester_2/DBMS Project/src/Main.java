import java.sql.Connection;
import java.sql.DriverManager;

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
    String status;
    String city;
    Donor(String name,String fatherName,String contact,int age,String bloodGroup,String city,String status){
    super(name,fatherName,contact);
    this.age = age;
    this.status = status;
    this.bloodGroup = bloodGroup;
    this.city = city;
    }
}
class Connect{
    public void getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/blood_bank_db";
        String user = "root";
        String pass = "Kami@123";
        Connection connection= DriverManager.getConnection(url, user, pass);
    }
    
}
class Main{
    public static void main(String[] args) {
        Donor donor = new Donor("kamran gul","ameen","030044545",20,"B+","Hangu","emergency");

}
}