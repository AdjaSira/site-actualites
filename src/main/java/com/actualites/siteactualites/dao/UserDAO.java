    package com.actualites.siteactualites.dao;

    import com.actualites.siteactualites.model.entity.User;
    import com.actualites.siteactualites.model.enums.UserRole; // Correction ici
    import java.util.List;
    import java.util.Optional;

    public interface UserDAO extends GenericDAO<User, Long> {

        // Trouver un utilisateur par login
        Optional<User> findByLogin(String login);

        // Trouver un utilisateur par email
        Optional<User> findByEmail(String email);

        // Vérifier si un login existe déjà
        boolean existsByLogin(String login);

        // Vérifier si un email existe déjà
        boolean existsByEmail(String email);

        // Trouver les utilisateurs par rôle
        List<User> findByRole(UserRole role);

        // Lister tous les utilisateurs triés par login
        List<User> findAllOrderByLogin();

        // Compter tous les utilisateurs
        long countAll();

        // Méthode d'authentification
        Optional<User> authenticate(String login, String motDePasse);
    }