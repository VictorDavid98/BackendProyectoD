package net.purocodigo.encuestabackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import net.purocodigo.encuestabackend.Dto.ProfesionalDTO;
import net.purocodigo.encuestabackend.Dto.UserDTO;
import net.purocodigo.encuestabackend.entities.UserEntity;
import net.purocodigo.encuestabackend.repositories.UserRepository;
import net.purocodigo.encuestabackend.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AdminController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> user = userService.getUsersWithRoleUser();
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/profesionales")
    public ResponseEntity<List<ProfesionalDTO>> getAllProfesional() {
        List<ProfesionalDTO> profesional = userService.getProfesionalWithRoleUser();
        return ResponseEntity.ok(profesional);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/{userId}/assign-professional/{professionalId}")
    public ResponseEntity<String> assignProfessional(
            @PathVariable Long userId,
            @PathVariable Long professionalId) {
        userService.assignProfessionalToUser(userId, professionalId);
        return ResponseEntity.ok("Usuario asignado a profesional correctamente.");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/all_rol_users")
    public ResponseEntity<List<UserDTO>> getProfessionalsAndUsers() {
        List<UserDTO> users = userService.getUserProfesionales();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/professionals/{id}/assigned-users")
    public ResponseEntity<List<UserDTO>> getAssignedUsers(@PathVariable long id) {
        UserEntity professional = userService.getUserById(id);

        if (!"ROLE_PROFESIONAL".equals(professional.getRole().getRoleName())) {
            return ResponseEntity.badRequest().build();
        }

        List<UserEntity> assignedUsers = professional.getAssignedUsers();
        List<UserDTO> userDTOs = assignedUsers.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().getRoleName(),
                        null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }

}
