package net.purocodigo.encuestabackend.services;


import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import net.purocodigo.encuestabackend.entities.PollEntity;
import net.purocodigo.encuestabackend.entities.PollReplyDetailEntity;
import net.purocodigo.encuestabackend.entities.PollReplyEntity;
import net.purocodigo.encuestabackend.models.requests.PollReplyRequestModel;
import net.purocodigo.encuestabackend.repositories.PollReplyRepository;
import net.purocodigo.encuestabackend.repositories.PollRepository;

@Service
public class PollReplyServiceImpl implements PollReplyService{

    PollReplyRepository pollReplyRepository;
    PollRepository pollRepository;

    public PollReplyServiceImpl(PollReplyRepository pollReplyRepository, PollRepository pollRepository) {
        this.pollReplyRepository = pollReplyRepository;
        this.pollRepository = pollRepository;
    }

    @Override
    public long createPollReply(PollReplyRequestModel model) {
        
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setAmbiguityIgnored(true);

        PollReplyEntity pollReply = mapper.map(model, PollReplyEntity.class);

        PollEntity poll = pollRepository.findById(model.getPoll());

        pollReply.setPoll(poll);

        Set<Long> uniqueReplies = new HashSet<>();

        for (PollReplyDetailEntity pollReplyDetailEntity: pollReply.getPollReplies()) {
            pollReplyDetailEntity.setPollReply(pollReply);
            uniqueReplies.add(pollReplyDetailEntity.getQuestionId());
        }

        if (uniqueReplies.size() != poll.getQuestions().size()) {
            throw new RuntimeException("You must answer all the questions");
        }

        PollReplyEntity replyEntity = pollReplyRepository.save(pollReply);    
        
        return replyEntity.getId();
    }
    
}
