package lt.code.academy;

import lt.code.academy.data.ExamAnswer;
import lt.code.academy.data.ExamEvaluation;
import lt.code.academy.data.QuestionAnswer;
import lt.code.academy.service.Examination;
import lt.code.academy.service.FileExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExaminationEvaluationSystem {

    private static final Logger logger = LoggerFactory.getLogger(ExaminationEvaluationSystem.class);

    public static void main(String [] args) {
        if (args.length != 2) {
            throw new RuntimeException("Blogas argumentu kiekis!");
        }

        String studentExamDirectory = args[0];
        String examAnswerPlace = args[1];

        ExaminationEvaluationSystem system = new ExaminationEvaluationSystem();
        FileExecutor fileExecutor = new FileExecutor(Paths.get(examAnswerPlace));

        Map<Integer, Map<Integer, String>> examAnswerMap = system.createExamAnswerMap(
                fileExecutor);

        system.monituredStudentFolder(fileExecutor, new Examination(examAnswerMap),
                studentExamDirectory);
    }
    private Map<Integer, Map<Integer, String>> createExamAnswerMap(FileExecutor fileExecutor) {
        List<ExamAnswer> examAnswers = fileExecutor.getExamAnswer();

        return examAnswers
                .stream()
                .collect(
                        Collectors.groupingBy(
                                ats -> ats.getExam().getId(),
                                Collectors.flatMapping(
                                        a -> a.getAnswers().stream(),
                                        Collectors.toMap(
                                                QuestionAnswer::getQuestion,
                                                QuestionAnswer::getAnswer))));
    }

    private void monituredStudentFolder(FileExecutor fileExecutor,
                                        Examination examination,
                                        String studentExamDirectory) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            logger.info("Sistema veikia");

            Path monituredDirectory = Path.of(studentExamDirectory);
            WatchKey watchKey = monituredDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    Path fileLocation = monituredDirectory.resolve((Path) event.context());

                    logger.info("Gautas naujas atsakymas {}", fileLocation);

                    var studentSubmittedExam = fileExecutor.getStudentExam(fileLocation);

                    studentSubmittedExam.ifPresent(ex -> {
                        int score = examination.checkExam(ex);

                        ExamEvaluation examEvaluation = new ExamEvaluation();
                        examEvaluation.setEvaluation(score);
                        examEvaluation.setStudent(ex.getStudent());
                        examEvaluation.setExam(ex.getExam());

                        logger.info("Egzaminas ivertintas, rezultatas {}", score);

                        fileExecutor.writeStudentAnswer(examEvaluation);
                    });

                }

                boolean valid = watchKey.reset();
                if (!valid) {
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Kazkas nenumatyto...", e);
        }

    }
}