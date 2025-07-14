package com.actualites.siteactualites.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {

    // Sauvegarder une entité
    T save(T entity);

    // Trouver par ID
    Optional<T> findById(ID id);

    // Trouver tous les éléments
    List<T> findAll();

    // Supprimer une entité
    void delete(T entity);

    // Supprimer par ID
    void deleteById(ID id);

    // Vérifier si existe
    boolean existsById(ID id);

    // Compter le nombre d'éléments
    long count();
}