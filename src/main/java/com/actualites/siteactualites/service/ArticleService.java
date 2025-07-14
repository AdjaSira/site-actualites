package com.actualites.siteactualites.service;

import com.actualites.siteactualites.dao.ArticleDAO;
import com.actualites.siteactualites.model.entity.Article;
import com.actualites.siteactualites.model.entity.Category;
import com.actualites.siteactualites.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleService {

    @Autowired
    private ArticleDAO articleDAO;

    // Créer un nouvel article
    public Article creerArticle(String titre, String resume, String contenu, Category category, User auteur) {
        if (titre == null || titre.isEmpty() || resume == null || resume.isEmpty() || contenu == null || contenu.isEmpty() || category == null || auteur == null) {
            throw new IllegalArgumentException("Les champs titre, resume, contenu, catégorie et auteur sont obligatoires");
        }
        Article article = new Article(titre, resume, contenu, category, auteur);
        return articleDAO.save(article);
    }

    // Trouver un article par ID
    public Optional<Article> trouverParId(Long id) {
        return articleDAO.findById(id);
    }

    // Lister tous les articles (triés par date)
    public List<Article> listerTousParDate() {
        return articleDAO.findAllOrderByDateCreationDesc();
    }

    // Lister les articles par catégorie
    public List<Article> listerParCategorie(Category category) {
        return articleDAO.findByCategory(category);
    }

    // Lister les articles par auteur
    public List<Article> listerParAuteur(User auteur) {
        return articleDAO.findByAuteur(auteur);
    }

    // Rechercher des articles par titre
    public List<Article> rechercherParTitre(String titre) {
        return articleDAO.findByTitreContaining(titre);
    }

    // Pagination des articles
    public Page<Article> listerAvecPagination(int page, int taille) {
        if (page < 0 || taille <= 0) {
            throw new IllegalArgumentException("La page doit être >= 0 et la taille > 0");
        }
        Pageable pageable = PageRequest.of(page, taille);
        return articleDAO.findAllPaginated(pageable);
    }

    // Pagination des articles par catégorie
    public Page<Article> listerParCategorieAvecPagination(Category category, int page, int taille) {
        if (page < 0 || taille <= 0) {
            throw new IllegalArgumentException("La page doit être >= 0 et la taille > 0");
        }
        Pageable pageable = PageRequest.of(page, taille);
        return articleDAO.findByCategoryPaginated(category, pageable);
    }

    // Modifier un article
    public Article modifierArticle(Article article) {
        if (article == null || article.getTitre() == null || article.getResume() == null || article.getContenu() == null || article.getCategory() == null) {
            throw new IllegalArgumentException("Les champs titre, resume, contenu et catégorie sont obligatoires");
        }
        article.setDateModification(LocalDateTime.now());
        return articleDAO.save(article);
    }

    // Supprimer un article
    public void supprimerArticle(Long id) {
        articleDAO.deleteById(id);
    }

    // Compter le nombre d'articles
    public long compterArticles() {
        return articleDAO.count();
    }

    // Obtenir les derniers articles (pour la page d'accueil)
    public List<Article> obtenirDerniersArticles(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("La limite doit être > 0");
        }
        List<Article> articles = articleDAO.findAllOrderByDateCreationDesc();
        return articles.size() > limite ? articles.subList(0, limite) : articles;
    }

    public Map<String, List<Article>> listerGroupesParCategorie() {
        return articleDAO.findAllOrderByDateCreationDesc().stream()
                .collect(Collectors.groupingBy(article -> article.getCategory().getNom()));
    }
}