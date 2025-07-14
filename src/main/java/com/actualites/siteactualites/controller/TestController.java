package com.actualites.siteactualites.controller;

import com.actualites.siteactualites.model.entity.User;
import com.actualites.siteactualites.model.entity.Category;
import com.actualites.siteactualites.model.entity.Article;
import com.actualites.siteactualites.service.UserService;
import com.actualites.siteactualites.service.CategoryService;
import com.actualites.siteactualites.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/test")
    public String test() {
        StringBuilder result = new StringBuilder();
        result.append("<h1>Test des Services</h1>");

        // Test UserService
        result.append("<h2>Utilisateurs :</h2>");
        List<User> users = userService.listerTous();
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                result.append("<p>").append(user.getLogin()).append(" - ").append(user.getRole()).append("</p>");
            }
        } else {
            result.append("<p>Aucun utilisateur trouvé</p>");
        }

        // Test CategoryService
        result.append("<h2>Catégories :</h2>");
        List<Category> categories = categoryService.listerToutes();
        if (categories != null && !categories.isEmpty()) {
            for (Category category : categories) {
                result.append("<p>").append(category.getNom()).append(" - ").append(category.getDescription()).append("</p>");
            }
        } else {
            result.append("<p>Aucune catégorie trouvée</p>");
        }

        // Test ArticleService
        result.append("<h2>Articles :</h2>");
        List<Article> articles = articleService.listerTousParDate();
        if (articles != null && !articles.isEmpty()) {
            for (Article article : articles) {
                result.append("<p><strong>").append(article.getTitre()).append("</strong><br>");
                result.append("Catégorie: ").append(article.getCategory() != null ? article.getCategory().getNom() : "N/A").append("<br>");
                result.append("Auteur: ").append(article.getAuteur() != null ? article.getAuteur().getLogin() : "N/A").append("</p>");
            }
        } else {
            result.append("<p>Aucun article trouvé</p>");
        }

        return result.toString();
    }
}