public class enrollment {
    private int id;
    private String student_name;
    private int course_id;
    private int grad;

    public enrollment(int id, String student_name, int course_id, int grad) {
        this.id = id;
        this.student_name = student_name;
        this.course_id = course_id;
        this.grad = grad;
    }

    public int getId() {
        return id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public int getCourse_id() {
        return course_id;
    }

    public int getGrad() {
        return grad;
    }

    public void setGrad(int grad) {
        this.grad = grad;
    }
}
