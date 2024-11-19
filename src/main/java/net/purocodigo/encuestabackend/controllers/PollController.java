package net.purocodigo.encuestabackend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.purocodigo.encuestabackend.entities.PollEntity;
import net.purocodigo.encuestabackend.interfaces.PollResult;
import net.purocodigo.encuestabackend.models.requests.PollCreationRequestModel;
import net.purocodigo.encuestabackend.models.responses.CreatedPollRest;
import net.purocodigo.encuestabackend.models.responses.PaginatedPollRest;
import net.purocodigo.encuestabackend.models.responses.PollRest;
import net.purocodigo.encuestabackend.models.responses.PollResultWrapperRest;
import net.purocodigo.encuestabackend.services.PollService;
import net.purocodigo.encuestabackend.utils.transformer.PollResultTransformer;

@RestController
@RequestMapping("/polls") // 
public class PollController {

    @Autowired
    PollService pollService;

    @PostMapping
    public CreatedPollRest createPoll(@RequestBody @Valid PollCreationRequestModel model , Authentication authentication) {
        String pollId = pollService.createPoll(model, authentication.getPrincipal().toString());
        return new CreatedPollRest(pollId);
    }

    @GetMapping(path = "{id}/questions")
    public PollRest getPollWithQuestions(@PathVariable String id) {
        PollEntity poll = pollService.getPoll(id);

        ModelMapper mapper = new ModelMapper();

        return mapper.map(poll, PollRest.class);
    }

    @GetMapping
    public PaginatedPollRest getPolls(
        @RequestParam(value="page", defaultValue = "0") int page, 
        @RequestParam(value="limit", defaultValue = "10") int limit,  
        Authentication authentication
    ) {
        Page<PollEntity> paginatedPolls = pollService.getPolls(page, limit, authentication.getPrincipal().toString());

        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(PollEntity.class, PollRest.class).addMappings(m -> m.skip(PollRest::setQuestions));

        PaginatedPollRest paginatedPollRest = new PaginatedPollRest();

        paginatedPollRest.setPolls(
            paginatedPolls.getContent().stream().map(p -> mapper.map(p, PollRest.class)).collect(Collectors.toList())
        );

        paginatedPollRest.setTotalPages(paginatedPolls.getTotalPages());
        paginatedPollRest.setTotalRecords(paginatedPolls.getTotalElements());
        paginatedPollRest.setCurrentPageRecords(paginatedPolls.getNumberOfElements());
        paginatedPollRest.setCurrentPage(paginatedPolls.getPageable().getPageNumber() + 1);

        return paginatedPollRest;

    }

    @PatchMapping(path="/{id}")
    public void togglePollOpened(@PathVariable String id, Authentication authentication) {
        pollService.togglePollOpened(id, authentication.getPrincipal().toString());
    }

    @DeleteMapping(path="/{id}")
    public void deletePoll(@PathVariable String id, Authentication authentication) {
        pollService.deletePoll(id, authentication.getPrincipal().toString());
    }

    @GetMapping(path = "/{id}/results")
    public PollResultWrapperRest getResults(@PathVariable String id, Authentication authentication) {
        List<PollResult> results = pollService.getResults(id, authentication.getPrincipal().toString());

        PollEntity poll = pollService.getPoll(id);

        PollResultTransformer transformer = new PollResultTransformer();        

        return new PollResultWrapperRest(transformer.transformData(results), poll.getContent(), poll.getId());
        
    }

}
