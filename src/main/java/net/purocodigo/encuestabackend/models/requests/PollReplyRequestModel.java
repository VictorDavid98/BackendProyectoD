package net.purocodigo.encuestabackend.models.requests;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PollReplyRequestModel {

    @NotEmpty
    private String user;

    @NotNull
    @Positive
    private long poll;

    @Valid
    @NotEmpty
    @Size(min = 1)
    private List<PollReplyDetailRequestModel> pollReplies;
}
