package com.actualites.siteactualites.service;

import com.actualites.siteactualites.dao.CategoryDAO;
import com.actualites.siteactualites.model.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;

    // Créer une nouvelle catégorie
    public Category creerCategorie(String nom, String description) {
        // Vérifier si le nom existe déjà
        if (categoryDAO.existsByNom(nom)) {
            throw new RuntimeException("Cette catégorie existe déjà : " + nom);
        }

        Category category = new Category(nom, description);
        return categoryDAO.save(category);
    }

    // Trouver une catégorie par ID
    public Optional<Category> trouverParId(Long id) {
        return categoryDAO.findById(id);
    }

    // Trouver une catégorie par nom
    public Optional<Category> trouverParNom(String nom) {
        return categoryDAO.findByNom(nom);
    }

    // Lister toutes les catégories
    public List<Category> listerToutes() {
        return categoryDAO.findAll();
    }

    // Modifier une catégorie
    public Category modifierCategorie(Category category) {
        return categoryDAO.save(category);
    }

    // Supprimer une catégorie
    public void supprimerCategorie(Long id) {
        categoryDAO.deleteById(id);
    }

    // Compter le nombre de catégories
    public long compterCategories() {
        return categoryDAO.count();
    }
}