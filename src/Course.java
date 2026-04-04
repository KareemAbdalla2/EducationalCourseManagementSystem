public class Course {
    private int id;
    private String name;
    private String code;

    // Constructor
    public Course(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    // Getters are required for the TableView to find the data

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
