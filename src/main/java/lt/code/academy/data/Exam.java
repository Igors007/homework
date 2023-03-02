package lt.code.academy.data;

public class Exam extends Subject {

    private String title;
    private TypeOfExam type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TypeOfExam getType() {
        return type;
    }

    public void setType(TypeOfExam type) {
        this.type = type;
    }
}
