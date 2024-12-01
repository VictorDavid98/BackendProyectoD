package net.purocodigo.encuestabackend.Dto;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String roleName;
    private Long assignedProfessionalId;

    // Constructor vacío
    public UserDTO() {
    }

    // Constructor con argumentos
    public UserDTO(Long id, String name, String email, String roleName, Long assignedProfessionalId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleName = roleName;
        this.assignedProfessionalId = assignedProfessionalId;
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

    public Long getAssignedProfessionalId() {
        return assignedProfessionalId;
    }

    public void setAssignedProfessionalId(Long assignedProfessionalId) {
        this.assignedProfessionalId = assignedProfessionalId;
    }
}
