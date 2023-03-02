package lt.code.academy.service;
import lt.code.academy.data.StudentExam;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Examination {

    private final Map<Integer, Map<Integer, String>> examAnswerMap;

    public Examination(Map<Integer, Map<Integer, String>> examAnswerMap) {
        this.examAnswerMap= examAnswerMap;
    }

    public int checkExam(StudentExam studentExam) {
        Map<Integer, String> exam = examAnswerMap.get(studentExam.exam.getId());

        int answerNumber = exam.values().size();
        AtomicInteger correctAnswers = new AtomicInteger();

        studentExam.answers.forEach(studentQuestionAnswer -> {
            var examAnswer = exam.get(studentQuestionAnswer.getQuestion());

            if (examAnswer.equals(studentQuestionAnswer.getAnswer())) {
                correctAnswers.getAndIncrement();
            }
        });
          return correctAnswers.get() * 10/ answerNumber;
    }
}
