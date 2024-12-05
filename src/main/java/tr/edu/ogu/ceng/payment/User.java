package tr.edu.ogu.ceng.payment;

public class User {
    private String username; 
    private String firstname; 
    private String lastname; 

    // Getter metodu
    public String getUsername() {
        return username;
    }
    public String getFirstname() {
        return firstname;
    }
    public String getLastname() {
        return lastname;
    }

    // Setter metodu
    public void setUsername(String username) {
        this.username = username;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
