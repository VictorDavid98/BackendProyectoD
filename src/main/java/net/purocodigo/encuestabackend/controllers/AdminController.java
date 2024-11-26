package net.purocodigo.encuestabackend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.purocodigo.encuestabackend.entities.UserEntity;
import net.purocodigo.encuestabackend.services.UserService;

import java.util.List;

@RestController
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/users")
    public List<UserEntity> getAllUsers() {
        return userService.getUsersWithRoleUser();
    }
}
