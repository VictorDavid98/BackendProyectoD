package net.purocodigo.encuestabackend.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import net.purocodigo.encuestabackend.Dto.ProfesionalDTO;
import net.purocodigo.encuestabackend.Dto.UserDTO;
import net.purocodigo.encuestabackend.entities.UserEntity;
import net.purocodigo.encuestabackend.models.requests.UserRegisterRequestModel;

public interface UserService extends UserDetailsService {

    public UserDetails loadUserByUsername(String email);

    public UserEntity getUser(String email);

    public UserEntity createUser(UserRegisterRequestModel user);

    UserEntity assignRoleToUser(String email, String roleName);

    public List<UserDTO> getUsersWithRoleUser();

    public List<ProfesionalDTO> getProfesionalWithRoleUser();

    public List<UserDTO> getUserProfesionales();

    UserEntity assignProfessionalToUser(Long userId, Long professionalId);

}
