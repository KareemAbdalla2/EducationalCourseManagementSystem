public class User {

    private String username;
    private String email;
    private String password;
    private String role;

    public User(String username, String password, String role, String email) {

        this.username = username;

        this.password = password;
        this.role = role;
        this.email = email;
    }



    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}