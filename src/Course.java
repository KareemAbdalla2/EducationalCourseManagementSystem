public class Course {
    private int id;
    private String name;
    private String code;
    private int grad;

    // Constructor
    public Course(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public Course(int id, String name, String code, int grad) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.grad = grad;
    }
// Getters are required for the TableView to find the data

    public int getGrad() {
        return grad;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
