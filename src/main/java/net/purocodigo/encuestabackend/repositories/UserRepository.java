package net.purocodigo.encuestabackend.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import net.purocodigo.encuestabackend.entities.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    public UserEntity findByEmail(String email);

    public UserEntity findById(long id);

    List<UserEntity> findByRole_RoleName(String roleName);

    List<UserEntity> findByRole_RoleNameIn(List<String> roles);

    List<UserEntity> findByAssignedProfessional(UserEntity professional);
}
