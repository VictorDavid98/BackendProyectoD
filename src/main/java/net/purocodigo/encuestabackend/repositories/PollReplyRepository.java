package net.purocodigo.encuestabackend.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import net.purocodigo.encuestabackend.entities.PollReplyEntity;

public interface PollReplyRepository extends CrudRepository<PollReplyEntity, Long> {

    public List<PollReplyEntity> findAll();
}
