package com.actualites.siteactualites.dao;

import com.actualites.siteactualites.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenDAO extends JpaRepository<Token, Long> {
    Optional<Token> findByValeur(String valeur);
}