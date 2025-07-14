package com.actualites.siteactualites.dal;

import com.actualites.siteactualites.dao.UserDAO;
import com.actualites.siteactualites.model.entity.User;
import com.actualites.siteactualites.model.enums.UserRole;
import org.springframework.stereotype.Repository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDALImpl extends GenericDALImpl<User, Long> implements UserDAO {

    @Override
    public Optional<User> findByLogin(String login) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.login = :login", User.class);
            query.setParameter("login", login);
            User user = query.getSingleResult();
            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            User user = query.getSingleResult();
            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findByRole(UserRole role) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.role = :role", User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    @Override
    public boolean existsByLogin(String login) {
        if (login == null || login.isEmpty()) {
            return false;
        }
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class);
        query.setParameter("login", login);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    @Override
    public Optional<User> authenticate(String login, String motDePasse) {
        if (login == null || motDePasse == null || login.isEmpty() || motDePasse.isEmpty()) {
            return Optional.empty();
        }
        return findByLogin(login); // L'authentification est gérée dans UserService avec BCrypt
    }

    @Override
    public List<User> findAllOrderByLogin() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u ORDER BY u.login", User.class);
        return query.getResultList();
    }

    @Override
    public long countAll() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u", Long.class);
        return query.getSingleResult();
    }
}