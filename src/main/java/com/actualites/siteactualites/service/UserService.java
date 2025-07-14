package com.actualites.siteactualites.service;

import com.actualites.siteactualites.dao.UserDAO;
import com.actualites.siteactualites.model.entity.User;
import com.actualites.siteactualites.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User creerUtilisateur(String login, String motDePasse, String email, UserRole role) {
        if (userDAO.existsByLogin(login)) {
            throw new RuntimeException("Ce login existe déjà : " + login);
        }
        if (email != null && !email.isEmpty() && userDAO.existsByEmail(email)) {
            throw new RuntimeException("Cet email existe déjà : " + email);
        }

        String hashedPassword = passwordEncoder.encode(motDePasse);
        User user = new User(login, hashedPassword, email, role);
        return userDAO.save(user);
    }

    public Optional<User> authentifier(String login, String motDePasse) {
        Optional<User> userOpt = userDAO.findByLogin(login);
        if (userOpt.isPresent() && passwordEncoder.matches(motDePasse, userOpt.get().getMotDePasse())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public Optional<User> trouverParId(Long id) {
        return userDAO.findById(id);
    }

    public Optional<User> trouverParLogin(String login) {
        return userDAO.findByLogin(login);
    }

    public List<User> listerTous() {
        return userDAO.findAll();
    }

    public List<User> listerParRole(UserRole role) {
        return userDAO.findByRole(role);
    }

    public User modifierUtilisateur(User user) {
        Optional<User> existingByLogin = userDAO.findByLogin(user.getLogin());
        if (existingByLogin.isPresent() && !existingByLogin.get().getId().equals(user.getId())) {
            throw new RuntimeException("Ce login est déjà utilisé par un autre utilisateur : " + user.getLogin());
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            Optional<User> existingByEmail = userDAO.findByEmail(user.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(user.getId())) {
                throw new RuntimeException("Cet email est déjà utilisé par un autre utilisateur : " + user.getEmail());
            }
        }

        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        }
        return userDAO.save(user);
    }

    public void supprimerUtilisateur(Long id) {
        userDAO.deleteById(id);
    }

    public boolean estAdministrateur(User user) {
        return user != null && user.getRole() != null && "ADMINISTRATEUR".equals(user.getRole());
    }

    public boolean estEditeur(User user) {
        return user != null && user.getRole() != null && UserRole.valueOf(user.getRole()) == UserRole.EDITEUR;
    }

    public boolean peutEditer(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        UserRole role = UserRole.valueOf(user.getRole());
        return role == UserRole.EDITEUR || role == UserRole.ADMINISTRATEUR;
    }

    public long compterUtilisateurs() {
        return userDAO.count();
    }
}