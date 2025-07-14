package com.actualites.siteactualites.dao;

import com.actualites.siteactualites.model.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CategoryDAO extends GenericDAO<Category, Long> {

    // Trouver une catégorie par son nom
    Optional<Category> findByNom(String nom);

    // Vérifier si un nom de catégorie existe déjà
    boolean existsByNom(String nom);

    // NOUVELLES MÉTHODES POUR L'ADMINISTRATION
    // Lister toutes les catégories triées par nom
    @Query("SELECT c FROM Category c ORDER BY c.nom")
    List<Category> findAllOrderByNom();

    // Compter toutes les catégories
    long countAll();

    // Nouvelle méthode : Trouver une catégorie par ID avec jointure si besoin
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findById(@Param("id") Long id);
}