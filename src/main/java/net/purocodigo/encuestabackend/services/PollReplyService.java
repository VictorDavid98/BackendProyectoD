package net.purocodigo.encuestabackend.services;

import net.purocodigo.encuestabackend.models.requests.PollReplyRequestModel;

public interface PollReplyService {
    public long createPollReply(PollReplyRequestModel model);
}
