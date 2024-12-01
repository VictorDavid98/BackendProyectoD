package net.purocodigo.encuestabackend.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.purocodigo.encuestabackend.Dto.ProfesionalDTO;
import net.purocodigo.encuestabackend.Dto.UserDTO;
import net.purocodigo.encuestabackend.entities.RoleEntity;
import net.purocodigo.encuestabackend.entities.UserEntity;
import net.purocodigo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.purocodigo.encuestabackend.repositories.RoleRepository;
import net.purocodigo.encuestabackend.repositories.UserRepository;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // Método para transformar UserEntity en UserDTO
    private UserDTO convertToUserDTO(UserEntity user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getAssignedProfessional() != null ? user.getAssignedProfessional().getId() : null);
    }

    // Método para transformar UserEntity (profesional) en ProfessionalDTO
    private ProfesionalDTO convertToProfesionalDTO(UserEntity professional) {
        // Transforma los usuarios asignados a UserDTO
        List<UserDTO> assignedUsers = professional.getAssignedUsers().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        return new ProfesionalDTO(
                professional.getId(),
                professional.getName(),
                professional.getEmail(),
                professional.getRole().getRoleName(),
                assignedUsers);
    }

    @Override
    public UserEntity createUser(UserRegisterRequestModel user) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        // Encriptar la contraseña
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        // Asignar rol por defecto
        RoleEntity role = roleRepository.findByRoleName("ROLE_USER");
        userEntity.setRole(role);

        return userRepository.save(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }

        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                AuthorityUtils.createAuthorityList(userEntity.getRole().getRoleName()));
    }

    @Override
    public UserEntity getUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity assignRoleToUser(String email, String roleName) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            RoleEntity role = roleRepository.findByRoleName(roleName);
            if (role != null) {
                user.setRole(role);
                return userRepository.save(user);
            }
        }
        return null;
    }

    @Override
    public List<UserDTO> getUsersWithRoleUser() {
        return userRepository.findByRole_RoleName("ROLE_USER").stream() // Filtra usuarios
                .map(this::convertToUserDTO) // Convierte cada entidad en DTO
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfesionalDTO> getProfesionalWithRoleUser() {
        return userRepository.findByRole_RoleName("ROLE_PROFESIONAL").stream() // Filtra profesionales
                .map(this::convertToProfesionalDTO) // Convierte cada entidad en DTO
                .collect(Collectors.toList());
    }

    @Override
    public UserEntity assignProfessionalToUser(Long userId, Long professionalId) {
        // Buscar al usuario y al profesional
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        UserEntity professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado."));

        // Validar roles
        if (!"ROLE_PROFESIONAL".equals(professional.getRole().getRoleName())) {
            throw new RuntimeException("El usuario seleccionado no tiene el rol ROLE_PROFESIONAL.");
        }
        if (!"ROLE_USER".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("El usuario seleccionado no tiene el rol ROLE_USER.");
        }

        // Asignar el usuario al profesional
        user.setAssignedProfessional(professional);
        professional.getAssignedUsers().add(user);

        // Guardar ambos
        userRepository.save(user); // Guardar primero el usuario para que se registre la relación
        return userRepository.save(professional);
    }

    @Override
    public List<UserDTO> getUserProfesionales() {
        List<String> roles = Arrays.asList("ROLE_PROFESIONAL", "ROLE_USER");
        return userRepository.findByRole_RoleNameIn(roles).stream().map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

}
