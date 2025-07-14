package com.actualites.siteactualites.dal;

import com.actualites.siteactualites.dao.CategoryDAO;
import com.actualites.siteactualites.model.entity.Category;
import org.springframework.stereotype.Repository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDALImpl extends GenericDALImpl<Category, Long> implements CategoryDAO {

    @Override
    public Optional<Category> findByNom(String nom) {
        try {
            TypedQuery<Category> query = entityManager.createQuery(
                    "SELECT c FROM Category c WHERE c.nom = :nom", Category.class);
            query.setParameter("nom", nom);
            Category category = query.getSingleResult();
            return Optional.of(category);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByNom(String nom) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.nom = :nom", Long.class);
        query.setParameter("nom", nom);
        return query.getSingleResult() > 0;
    }

    @Override
    public List<Category> findAllOrderByNom() {
        TypedQuery<Category> query = entityManager.createQuery(
                "SELECT c FROM Category c ORDER BY c.nom", Category.class);
        return query.getResultList();
    }

    @Override
    public long countAll() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Category c", Long.class);
        return query.getSingleResult();
    }
}