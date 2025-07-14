package com.actualites.siteactualites.webservice;

import com.actualites.siteactualites.model.entity.Article;
import com.actualites.siteactualites.model.entity.Category;
import com.actualites.siteactualites.service.ArticleService;
import com.actualites.siteactualites.service.CategoryService; // Ajouté
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class ArticleRestController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService; // Ajouté pour récupérer la catégorie par ID

    // Récupérer la liste de tous les articles (XML ou JSON)
    @GetMapping(produces = {"application/json", "application/xml"})
    public List<Article> getAllArticles() {
        return articleService.listerTousParDate();
    }

    // Récupérer les articles par catégorie (XML ou JSON)
    @GetMapping(value = "/by-category/{categoryId}", produces = {"application/json", "application/xml"})
    public List<Article> getArticlesByCategory(@PathVariable Long categoryId) {
        Optional<Category> categoryOpt = categoryService.trouverParId(categoryId);
        if (categoryOpt.isPresent()) {
            return articleService.listerParCategorie(categoryOpt.get());
        } else {
            throw new IllegalArgumentException("Catégorie non trouvée pour l'ID : " + categoryId);
        }
    }

    // Récupérer les articles regroupés par catégorie (XML ou JSON)
    @GetMapping(value = "/grouped-by-category", produces = {"application/json", "application/xml"})
    public Map<String, List<Article>> getArticlesGroupedByCategory() {
        return articleService.listerGroupesParCategorie();
    }
}