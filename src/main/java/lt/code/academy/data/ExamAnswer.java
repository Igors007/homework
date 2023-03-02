package lt.code.academy.data;

import java.util.List;

public class ExamAnswer {
    private Exam exam;
    private List<QuestionAnswer> answer;

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }
    public List<QuestionAnswer> getAnswers() {
        return answer;
    }

    public void setAnswers(List<QuestionAnswer> answers) {
        this.answer = answers;
    }
}
