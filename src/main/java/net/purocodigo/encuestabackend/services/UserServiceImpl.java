package net.purocodigo.encuestabackend.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.purocodigo.encuestabackend.entities.RoleEntity;
import net.purocodigo.encuestabackend.entities.UserEntity;
import net.purocodigo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.purocodigo.encuestabackend.repositories.RoleRepository;
import net.purocodigo.encuestabackend.repositories.UserRepository;

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
    public List<UserEntity> getUsersWithRoleUser() {
        return userRepository.findByRole_RoleName("ROLE_USER");
    }

    @Override
    public List<UserEntity> getProfesionalWithRoleUser() {
        return userRepository.findByRole_RoleName("ROLE_PROFESIONAL");
    }

    @Override
    public UserEntity assignProfessionalToUser(Long userId, Long professionalId) {
        // Buscar al usuario y al profesional
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        UserEntity professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado."));

        // Validar que los roles sean los correctos
        if (!"ROLE_PROFESIONAL".equals(professional.getRole().getRoleName())) {
            throw new RuntimeException("El usuario seleccionado no tiene el rol ROLE_PROFESIONAL.");
        }
        if (!"ROLE_USER".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("El usuario seleccionado no tiene el rol ROLE_USER.");
        }

        // Asignar profesional al usuario
        user.setAssignedProfessional(professional);

        // Guardar cambios
        return userRepository.save(user);
    }

}
