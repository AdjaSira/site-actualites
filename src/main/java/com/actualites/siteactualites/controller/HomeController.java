package com.actualites.siteactualites.controller;

import com.actualites.siteactualites.model.entity.Article;
import com.actualites.siteactualites.model.entity.User;
import com.actualites.siteactualites.model.entity.Category;
import com.actualites.siteactualites.service.ArticleService;
import com.actualites.siteactualites.service.CategoryService;
import com.actualites.siteactualites.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    // Page d'accueil avec pagination
    @GetMapping("/")
    public String accueil(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size) {
        Page<Article> articlesPage = articleService.listerAvecPagination(page, size);
        List<Category> categories = categoryService.listerToutes();

        model.addAttribute("articlesPage", articlesPage);
        model.addAttribute("articles", articlesPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlesPage.getTotalPages());
        model.addAttribute("hasNext", articlesPage.hasNext());
        model.addAttribute("hasPrevious", articlesPage.hasPrevious());

        return "index";
    }

    // Détail d'un article
    @GetMapping("/article/{id}")
    public String detailArticle(@PathVariable Long id, Model model) {
        Optional<Article> articleOpt = articleService.trouverParId(id);
        if (articleOpt.isPresent()) {
            Article article = articleOpt.get();
            List<Category> categories = categoryService.listerToutes();
            model.addAttribute("article", article);
            model.addAttribute("categories", categories);
            return "article-detail";
        } else {
            return "redirect:/";
        }
    }

    // Recherche d'articles
    @GetMapping("/recherche")
    public String rechercherArticles(@RequestParam(required = false) String q, Model model) {
        List<Category> categories = categoryService.listerToutes();
        model.addAttribute("categories", categories);

        if (q != null && !q.trim().isEmpty()) {
            List<Article> articles = articleService.rechercherParTitre(q.trim());
            model.addAttribute("articles", articles);
            model.addAttribute("recherche", q.trim());
            model.addAttribute("nombreResultats", articles.size());
        } else {
            model.addAttribute("articles", new ArrayList<>());
            model.addAttribute("recherche", "");
            model.addAttribute("nombreResultats", 0);
        }

        return "recherche";
    }

    // Articles par catégorie
    @GetMapping("/categorie/{id}")
    public String articlesParCategorie(@PathVariable Long id,
                                       Model model,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size) {
        Optional<Category> categoryOpt = categoryService.trouverParId(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            Page<Article> articlesPage = articleService.listerParCategorieAvecPagination(category, page, size);
            List<Category> categories = categoryService.listerToutes();

            model.addAttribute("articlesPage", articlesPage);
            model.addAttribute("articles", articlesPage.getContent());
            model.addAttribute("categories", categories);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", articlesPage.getTotalPages());
            model.addAttribute("hasNext", articlesPage.hasNext());
            model.addAttribute("hasPrevious", articlesPage.hasPrevious());

            return "articles-par-categorie";
        } else {
            return "redirect:/";
        }
    }

    // Ajouter les infos utilisateur à tous les templates
    @ModelAttribute
    public void ajouterInfosUtilisateur(HttpSession session, Model model) {
        User utilisateur = (User) session.getAttribute("utilisateurConnecte");
        if (utilisateur != null) {
            model.addAttribute("utilisateurConnecte", utilisateur);
            model.addAttribute("estAdmin", userService.estAdministrateur(utilisateur));
        }
    }
}