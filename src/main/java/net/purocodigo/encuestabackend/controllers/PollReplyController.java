package net.purocodigo.encuestabackend.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.purocodigo.encuestabackend.models.requests.PollReplyRequestModel;
import net.purocodigo.encuestabackend.models.responses.CreatedPollReplyRest;
import net.purocodigo.encuestabackend.services.PollReplyService;

@RestController
@RequestMapping("/polls/reply")
public class PollReplyController {

    @Autowired
    PollReplyService pollReplyService;
    
    @PostMapping
    public CreatedPollReplyRest replyPoll(@RequestBody @Valid PollReplyRequestModel model) {
       return new CreatedPollReplyRest(pollReplyService.createPollReply(model));
    }
 
}
