package net.purocodigo.encuestabackend.models.responses;

import java.util.List;

import lombok.Data;
import net.purocodigo.encuestabackend.enums.QuestionType;

@Data
public class QuestionRest {
    private long id;

    private String content;
    
    private int questionOrder;

    private QuestionType type;

    private List<AnswerRest> answers;
}
