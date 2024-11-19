package net.purocodigo.encuestabackend.models.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PollResultWrapperRest {
    private List<PollResultRest> results;
    private String content;
    private long id;
}
