package net.purocodigo.encuestabackend.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.purocodigo.encuestabackend.entities.PollEntity;
import net.purocodigo.encuestabackend.interfaces.PollResult;

@Repository
public interface PollRepository extends CrudRepository<PollEntity, Long> {
    PollEntity findByPollId(String pollId);

    PollEntity findById(long id);

    Page<PollEntity> findAllByUserId(long userId, Pageable pageable);

    PollEntity findByPollIdAndUserId(String pollId, long userId);

    @Query(value = "SELECT q.question_order AS questionOrder, prd.question_id AS questionId, q.content AS question, prd.answer_id AS answerId, a.content AS answer, COUNT(prd.answer_id) AS result FROM poll_replies pr LEFT JOIN poll_reply_details prd ON prd.poll_reply_id = pr.id LEFT JOIN answers a ON a.id = prd.answer_id LEFT JOIN questions q ON q.id = prd.question_id WHERE pr.poll_id = :pollId GROUP BY q.question_order, prd.question_id, q.content, prd.answer_id, a.content ORDER BY q.question_order ASC", nativeQuery = true)
    List<PollResult> getPollResults(@Param("pollId") long id);

}
