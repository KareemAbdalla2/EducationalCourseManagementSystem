public class Payment {
    private int id;
    private String description;
    private double amount;
    private String method;
    private String status;

    public Payment(int id, String description, double amount, String method, String status) {
        this.id          = id;
        this.description = description;
        this.amount      = amount;
        this.method      = method;
        this.status      = status;
    }

    // Getters are required for JavaFX TableView PropertyValueFactory

    public int getId()             { return id; }
    public String getDescription() { return description; }
    public double getAmount()      { return amount; }
    public String getMethod()      { return method; }
    public String getStatus()      { return status; }
}