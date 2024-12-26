package tr.edu.ogu.ceng.payment.restClientOrder;

public class User {
    private String username; 
    private String firstname; 
    private String lastname; 
    private String userId;

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
    public String getId() {
        return userId;
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
    public void setId(String userId) {
        this.userId = userId;
    }
}
