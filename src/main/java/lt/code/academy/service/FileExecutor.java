package lt.code.academy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lt.code.academy.data.ExamAnswer;
import lt.code.academy.data.ExamEvaluation;
import lt.code.academy.data.StudentExam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class FileExecutor {

    private static final Logger logger = LoggerFactory.getLogger(FileExecutor.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter datosFormateris = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Path examAnswerFileLocation;
    private final Path studentExamEvaluationLocation;

    public FileExecutor(Path examAnswerFileLocation) {
        this.examAnswerFileLocation = examAnswerFileLocation;
        this.studentExamEvaluationLocation = examAnswerFileLocation.getParent();
    }
    public Optional<StudentExam> getStudentExam(Path fileLocation) {
        try {
            return Optional.of(objectMapper.readValue(fileLocation.toFile(), StudentExam.class));

        } catch (IOException e) {
            logger.error("Nepavyko apdaroti egzamino failo");
            return Optional.empty();
        }
    }
    public List<ExamAnswer> getExamAnswer() {
        try {
            return objectMapper.readValue(examAnswerFileLocation.toFile(), new TypeReference<>() {
            });

        } catch (IOException e) {
            throw new RuntimeException("Nepavyko apdaroti egzamino atsakymo failo", e);
        }
    }
    public void writeStudentAnswer(ExamEvaluation examEvaluation) {
        LocalDateTime now = LocalDateTime.now();
        String evaluationTitle = examEvaluation.getExam().getTitle()
                .concat(" ")
                .concat(examEvaluation.getStudent().getName())
                .concat(" ")
                .concat(examEvaluation.getStudent().getSurname())
                .concat(" ")
                .concat(now.format(datosFormateris))
                .concat(".json");

        Path fileTitle = studentExamEvaluationLocation.resolve(Path.of(evaluationTitle));

        try {
            if (fileTitle.toFile().createNewFile()) {

                objectMapper.writeValue(fileTitle.toFile(), examEvaluation);

            } else {
                logger.warn("Nepavyko sukurti atsakymo failo");
            }

        } catch (IOException e) {
            logger.error("Nepavyko irasyti atsakymo", e);
        }
    }

}

