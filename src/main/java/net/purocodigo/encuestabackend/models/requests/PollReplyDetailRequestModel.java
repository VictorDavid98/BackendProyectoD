package net.purocodigo.encuestabackend.models.requests;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class PollReplyDetailRequestModel {

    @NotNull
    @Positive
    private long questionId;

    @NotNull
    @Positive
    private long answerId;
}
