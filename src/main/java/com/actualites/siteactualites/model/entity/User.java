package com.actualites.siteactualites.model.entity;

import com.actualites.siteactualites.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;

import java.time.LocalDateTime;

@XmlRootElement(name = "user")
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String motDePasse;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    public User() {
    }

    public User(String login, String motDePasse, String email, UserRole role) {
        this.login = login;
        this.motDePasse = motDePasse;
        this.email = email;
        this.role = role;
        this.dateCreation = LocalDateTime.now();
    }

    @XmlElement
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @XmlElement
    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    @XmlElement
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name = "role", required = true)
    public String getRole() {
        return role != null ? role.name() : null;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setRole(String role) {
        this.role = role != null ? UserRole.valueOf(role) : null;
    }

    @XmlElement
    @XmlSchemaType(name = "dateTime")
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}