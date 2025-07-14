package com.actualites.siteactualites.service;

import com.actualites.siteactualites.dao.TokenDAO;
import com.actualites.siteactualites.model.entity.Token;
import com.actualites.siteactualites.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private TokenDAO tokenDAO;

    @Autowired
    private UserService userService;

    public Token genererToken(Long userId) {
        Optional<User> userOpt = userService.trouverParId(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        String valeur = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusDays(7); // Jeton valide 7 jours

        Token token = new Token(valeur, userOpt.get(), now, expiration);
        return tokenDAO.save(token);
    }

    public boolean validerToken(String valeur) {
        Optional<Token> tokenOpt = tokenDAO.findByValeur(valeur);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        Token token = tokenOpt.get();
        return token.getDateExpiration().isAfter(LocalDateTime.now());
    }

    public List<Token> listerTous() {
        return tokenDAO.findAll();
    }

    public void supprimerToken(Long id) {
        tokenDAO.deleteById(id);
    }

    public Optional<Token> trouverParId(Long id) {
        return tokenDAO.findById(id);
    }
}