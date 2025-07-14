package com.actualites.siteactualites.controller;

import com.actualites.siteactualites.model.entity.Article;
import com.actualites.siteactualites.model.entity.Category;
import com.actualites.siteactualites.model.entity.User;
import com.actualites.siteactualites.service.ArticleService;
import com.actualites.siteactualites.service.CategoryService;
import com.actualites.siteactualites.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/editeur")
public class EditeurController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    // Vérifier si l'utilisateur peut éditer
    private boolean peutEditer(HttpSession session) {
        User user = (User) session.getAttribute("utilisateurConnecte");
        return userService.peutEditer(user);
    }

    // Dashboard éditeur
    @GetMapping
    public String dashboardEditeur(HttpSession session, Model model) {
        if (!peutEditer(session)) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("utilisateurConnecte");
        List<Article> mesArticles = articleService.listerParAuteur(user);
        List<Category> categories = categoryService.listerToutes();

        model.addAttribute("mesArticles", mesArticles);
        model.addAttribute("categories", categories);
        model.addAttribute("utilisateur", user);

        return "editeur/dashboard";
    }

    // Formulaire de création d'article
    @GetMapping("/article/nouveau")
    public String nouveauArticle(HttpSession session, Model model) {
        if (!peutEditer(session)) {
            return "redirect:/login";
        }

        List<Category> categories = categoryService.listerToutes();
        model.addAttribute("categories", categories);
        model.addAttribute("article", new Article());

        return "editeur/article-form";
    }

    // Création d'un article
    @PostMapping("/article/creer")
    public String creerArticle(@RequestParam @NotBlank String titre,
                               @RequestParam @NotBlank String resume,
                               @RequestParam @NotBlank String contenu,
                               @RequestParam @NotNull Long categoryId,
                               HttpSession session,
                               Model model) {

        if (!peutEditer(session)) {
            return "redirect:/login";
        }

        try {
            User auteur = (User) session.getAttribute("utilisateurConnecte");
            Optional<Category> categoryOpt = categoryService.trouverParId(categoryId);

            if (categoryOpt.isPresent()) {
                articleService.creerArticle(titre, resume, contenu, categoryOpt.get(), auteur);
                return "redirect:/editeur";
            } else {
                model.addAttribute("erreur", "Catégorie non trouvée");
                model.addAttribute("categories", categoryService.listerToutes());
                return "editeur/article-form";
            }
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la création : " + e.getMessage());
            model.addAttribute("categories", categoryService.listerToutes());
            return "editeur/article-form";
        }
    }

    // Formulaire de modification d'article
    @GetMapping("/article/modifier/{id}")
    public String modifierArticle(@PathVariable Long id, HttpSession session, Model model) {
        if (!peutEditer(session)) {
            return "redirect:/login";
        }

        Optional<Article> articleOpt = articleService.trouverParId(id);
        if (articleOpt.isPresent()) {
            List<Category> categories = categoryService.listerToutes();
            model.addAttribute("article", articleOpt.get());
            model.addAttribute("categories", categories);
            return "editeur/article-edit";
        } else {
            return "redirect:/editeur";
        }
    }

    // Mise à jour d'un article
    @PostMapping("/article/modifier/{id}")
    public String mettreAJourArticle(@PathVariable Long id,
                                     @RequestParam @NotBlank String titre,
                                     @RequestParam @NotBlank String resume,
                                     @RequestParam @NotBlank String contenu,
                                     @RequestParam @NotNull Long categoryId,
                                     HttpSession session,
                                     Model model) {

        if (!peutEditer(session)) {
            return "redirect:/login";
        }

        Optional<Article> articleOpt = articleService.trouverParId(id);
        Optional<Category> categoryOpt = categoryService.trouverParId(categoryId);

        if (articleOpt.isPresent() && categoryOpt.isPresent()) {
            Article article = articleOpt.get();
            article.setTitre(titre);
            article.setResume(resume);
            article.setContenu(contenu);
            article.setCategory(categoryOpt.get());

            articleService.modifierArticle(article);
            return "redirect:/editeur";
        } else {
            model.addAttribute("erreur", "Article ou catégorie non trouvé");
            model.addAttribute("categories", categoryService.listerToutes());
            return "editeur/article-edit";
        }
    }

    // Suppression d'un article
    @GetMapping("/article/supprimer/{id}")
    public String supprimerArticle(@PathVariable Long id, HttpSession session) {
        if (!peutEditer(session)) {
            return "redirect:/login";
        }

        articleService.supprimerArticle(id);
        return "redirect:/editeur";
    }

    // Gestion des catégories
    @GetMapping("/categories")
    public String gererCategories(HttpSession session, Model model) {
        if (!peutEditer(session)) {
            return "redirect:/login";
        }

        List<Category> categories = categoryService.listerToutes();
        model.addAttribute("categories", categories);

        return "editeur/categories";
    }

    // Créer une catégorie
    @PostMapping("/categorie/creer")
    public String creerCategorie(@RequestParam @NotBlank String nom,
                                 @RequestParam String description,
                                 HttpSession session,
                                 Model model) {

        if (!peutEditer(session)) {
            return "redirect:/login";
        }

        try {
            categoryService.creerCategorie(nom, description);
            return "redirect:/editeur/categories";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur : " + e.getMessage());
            List<Category> categories = categoryService.listerToutes();
            model.addAttribute("categories", categories);
            return "editeur/categories";
        }
    }
}