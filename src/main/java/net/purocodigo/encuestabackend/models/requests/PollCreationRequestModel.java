package net.purocodigo.encuestabackend.models.requests;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PollCreationRequestModel {
    
    @NotEmpty
    private String content;

    @NotNull
    private boolean isOpened;

    @Valid
    @NotEmpty
    @Size(min = 1, max = 30)
    private List<QuestionCreationRequestModel> questions;


}
