package com.actualites.siteactualites.controller;

import com.actualites.siteactualites.model.entity.Article;
import com.actualites.siteactualites.model.entity.Category;
import com.actualites.siteactualites.model.entity.Token;
import com.actualites.siteactualites.model.entity.User;
import com.actualites.siteactualites.model.enums.UserRole;
import com.actualites.siteactualites.service.ArticleService;
import com.actualites.siteactualites.service.CategoryService;
import com.actualites.siteactualites.service.TokenService;
import com.actualites.siteactualites.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Vérification des droits d'administration
    private boolean verifierDroitsAdmin(HttpSession session) {
        User utilisateur = (User) session.getAttribute("utilisateurConnecte");
        return utilisateur != null && userService.estAdministrateur(utilisateur);
    }

    // Dashboard principal
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        long nombreArticles = articleService.compterArticles();
        long nombreCategories = categoryService.compterCategories();
        List<User> tousUtilisateurs = userService.listerTous();
        long nombreUtilisateurs = tousUtilisateurs.size();

        model.addAttribute("nombreArticles", nombreArticles);
        model.addAttribute("nombreCategories", nombreCategories);
        model.addAttribute("nombreUtilisateurs", nombreUtilisateurs);

        List<Article> articlesRecents = articleService.obtenirDerniersArticles(5);
        model.addAttribute("articlesRecents", articlesRecents);

        return "admin/dashboard";
    }

    // === GESTION DES ARTICLES ===
    @GetMapping("/articles")
    public String listerArticles(HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        List<Article> articles = articleService.listerTousParDate();
        model.addAttribute("articles", articles);
        return "admin/articles";
    }

    @GetMapping("/articles/nouveau")
    public String nouveauArticle(HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryService.listerToutes());
        model.addAttribute("utilisateurs", userService.listerTous());
        return "admin/article-form";
    }

    @PostMapping("/articles/sauvegarder")
    public String sauvegarderArticle(@ModelAttribute Article article,
                                     @RequestParam Long categoryId,
                                     @RequestParam Long auteurId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        try {
            Optional<Category> categoryOpt = categoryService.trouverParId(categoryId);
            Optional<User> auteurOpt = userService.trouverParId(auteurId);

            if (categoryOpt.isEmpty() || auteurOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erreur", "Catégorie ou auteur introuvable !");
                return "redirect:/admin/articles";
            }

            if (article.getId() == null) {
                articleService.creerArticle(
                        article.getTitre(),
                        article.getResume(),
                        article.getContenu(),
                        categoryOpt.get(),
                        auteurOpt.get()
                );
            } else {
                Optional<Article> existingOpt = articleService.trouverParId(article.getId());
                if (existingOpt.isPresent()) {
                    Article existing = existingOpt.get();
                    existing.setTitre(article.getTitre());
                    existing.setResume(article.getResume());
                    existing.setContenu(article.getContenu());
                    existing.setCategory(categoryOpt.get());
                    existing.setAuteur(auteurOpt.get());
                    articleService.modifierArticle(existing);
                }
            }

            redirectAttributes.addFlashAttribute("succes", "Article sauvegardé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
        }

        return "redirect:/admin/articles";
    }

    @GetMapping("/articles/modifier/{id}")
    public String modifierArticle(@PathVariable Long id, HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        Optional<Article> articleOpt = articleService.trouverParId(id);
        if (articleOpt.isEmpty()) {
            return "redirect:/admin/articles";
        }

        model.addAttribute("article", articleOpt.get());
        model.addAttribute("categories", categoryService.listerToutes());
        model.addAttribute("utilisateurs", userService.listerTous());
        return "admin/article-form";
    }

    @PostMapping("/articles/supprimer/{id}")
    public String supprimerArticle(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        try {
            articleService.supprimerArticle(id);
            redirectAttributes.addFlashAttribute("succes", "Article supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la suppression : " + e.getMessage());
        }

        return "redirect:/admin/articles";
    }

    // === GESTION DES CATÉGORIES ===
    @GetMapping("/categories")
    public String listerCategories(HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        List<Category> categories = categoryService.listerToutes();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    @GetMapping("/categories/nouvelle")
    public String nouvelleCategorie(HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    @PostMapping("/categories/sauvegarder")
    public String sauvegarderCategorie(@ModelAttribute Category category,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        try {
            if (category.getId() == null) {
                categoryService.creerCategorie(category.getNom(), category.getDescription());
            } else {
                Optional<Category> existingOpt = categoryService.trouverParId(category.getId());
                if (existingOpt.isPresent()) {
                    Category existing = existingOpt.get();
                    existing.setNom(category.getNom());
                    existing.setDescription(category.getDescription());
                    categoryService.modifierCategorie(existing);
                }
            }
            redirectAttributes.addFlashAttribute("succes", "Catégorie sauvegardée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/modifier/{id}")
    public String modifierCategorie(@PathVariable Long id, HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        Optional<Category> categoryOpt = categoryService.trouverParId(id);
        if (categoryOpt.isEmpty()) {
            return "redirect:/admin/categories";
        }

        model.addAttribute("category", categoryOpt.get());
        return "admin/category-form";
    }

    @PostMapping("/categories/supprimer/{id}")
    public String supprimerCategorie(@PathVariable Long id,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        try {
            categoryService.supprimerCategorie(id);
            redirectAttributes.addFlashAttribute("succes", "Catégorie supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la suppression : " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    // === GESTION DES UTILISATEURS ===
    @GetMapping("/utilisateurs")
    public String listerUtilisateurs(HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        List<User> utilisateurs = userService.listerTous();
        model.addAttribute("utilisateurs", utilisateurs);
        return "admin/utilisateurs";
    }

    @GetMapping("/utilisateurs/nouveau")
    public String nouvelUtilisateur(HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values());
        return "admin/user-form";
    }

    @PostMapping("/utilisateurs/sauvegarder")
    public String sauvegarderUtilisateur(@ModelAttribute User user,
                                         @RequestParam UserRole role,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        try {
            // Vérifier l'unicité du login
            if (userService.trouverParLogin(user.getLogin()).isPresent() && user.getId() == null) {
                redirectAttributes.addFlashAttribute("erreur", "Ce login est déjà utilisé !");
                return "redirect:/admin/utilisateurs/nouveau";
            }

            // Vérifier l'email (si fourni)
            if (user.getEmail() != null && !user.getEmail().isEmpty() && !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                redirectAttributes.addFlashAttribute("erreur", "Adresse email invalide !");
                return "redirect:/admin/utilisateurs/nouveau";
            }

            if (user.getId() == null) {
                // Nouvel utilisateur
                userService.creerUtilisateur(user.getLogin(), user.getMotDePasse(), user.getEmail(), role);
            } else {
                // Modification d'utilisateur existant
                Optional<User> existingOpt = userService.trouverParId(user.getId());
                if (existingOpt.isPresent()) {
                    User existing = existingOpt.get();
                    existing.setLogin(user.getLogin());
                    existing.setEmail(user.getEmail());
                    existing.setRole(role);
                    if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
                        existing.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
                    }
                    userService.modifierUtilisateur(existing);
                }
            }
            redirectAttributes.addFlashAttribute("succes", "Utilisateur sauvegardé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la sauvegarde : " + e.getMessage());
        }

        return "redirect:/admin/utilisateurs";
    }

    @GetMapping("/utilisateurs/modifier/{id}")
    public String modifierUtilisateur(@PathVariable Long id, HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.trouverParId(id);
        if (userOpt.isEmpty()) {
            return "redirect:/admin/utilisateurs";
        }

        model.addAttribute("user", userOpt.get());
        model.addAttribute("roles", UserRole.values());
        return "admin/user-form";
    }

    @PostMapping("/utilisateurs/supprimer/{id}")
    public String supprimerUtilisateur(@PathVariable Long id,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }

        try {
            userService.supprimerUtilisateur(id);
            redirectAttributes.addFlashAttribute("succes", "Utilisateur supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la suppression : " + e.getMessage());
        }

        return "redirect:/admin/utilisateurs";
    }

    // === GESTION DES JETONS ===
    @GetMapping("/tokens")
    public String listerTokens(HttpSession session, Model model) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("tokens", tokenService.listerTous());
        model.addAttribute("utilisateurs", userService.listerTous());
        return "admin/tokens";
    }

    @PostMapping("/tokens/generer")
    public String genererToken(@RequestParam Long userId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }
        try {
            tokenService.genererToken(userId);
            redirectAttributes.addFlashAttribute("succes", "Jeton généré avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la génération du jeton : " + e.getMessage());
        }
        return "redirect:/admin/tokens";
    }

    @PostMapping("/tokens/supprimer/{id}")
    public String supprimerToken(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!verifierDroitsAdmin(session)) {
            return "redirect:/login";
        }
        try {
            tokenService.supprimerToken(id);
            redirectAttributes.addFlashAttribute("succes", "Jeton supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la suppression du jeton : " + e.getMessage());
        }
        return "redirect:/admin/tokens";
    }
}