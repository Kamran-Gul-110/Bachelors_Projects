class Person{
    private String firstName;
    private String lastName;
    private String fatherName;
    private String contact;
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