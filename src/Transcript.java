public class Transcript {
    private int id  ;
    private String student;
    private  String transcript;
    private String status;

    public Transcript(int id, String student, String transcript, String status) {
        this.id = id;
        this.student = student;
        this.transcript = transcript;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStudent() {
        return student;
    }

    public String getTranscript() {
        return transcript;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
    }
}
