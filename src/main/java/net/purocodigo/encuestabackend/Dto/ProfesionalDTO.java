package net.purocodigo.encuestabackend.Dto;

import java.util.List;

public class ProfesionalDTO {
    private Long id;
    private String name;
    private String email;
    private String roleName;
    private List<UserDTO> assignedUsers;

    // Constructor vacío
    public ProfesionalDTO() {
    }

    // Constructor con argumentos
    public ProfesionalDTO(Long id, String name, String email, String roleName, List<UserDTO> assignedUsers) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.assignedUsers = assignedUsers;
        this.roleName = roleName;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<UserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<UserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}
