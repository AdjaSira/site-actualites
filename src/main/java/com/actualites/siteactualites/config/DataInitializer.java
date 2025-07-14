package com.actualites.siteactualites.config;

import com.actualites.siteactualites.model.entity.User;
import com.actualites.siteactualites.model.entity.Category;
import com.actualites.siteactualites.model.enums.UserRole;
import com.actualites.siteactualites.service.UserService;
import com.actualites.siteactualites.service.CategoryService;
import com.actualites.siteactualites.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    @Bean
    public ApplicationRunner initializer() {
        return args -> initializeData();
    }

    @Transactional
    public void initializeData() {
        // Création d'utilisateurs de test
        if (!userService.trouverParLogin("admin").isPresent()) {
            userService.creerUtilisateur("admin", "admin123", "admin@actualites.com", UserRole.ADMINISTRATEUR);
        }
        if (!userService.trouverParLogin("editeur").isPresent()) {
            userService.creerUtilisateur("editeur", "edit123", "editeur@actualites.com", UserRole.EDITEUR);
        }
        if (!userService.trouverParLogin("visiteur").isPresent()) {
            userService.creerUtilisateur("visiteur", "visit123", "visiteur@actualites.com", UserRole.VISITEUR);
        }

        // Création de catégories de test enrichies
        if (!categoryService.trouverParNom("Technologie").isPresent()) {
            categoryService.creerCategorie("Technologie", "Actualités sur les innovations, gadgets, IA, web, etc.");
        }
        if (!categoryService.trouverParNom("Sport").isPresent()) {
            categoryService.creerCategorie("Sport", "Résultats, analyses et événements sportifs majeurs");
        }
        if (!categoryService.trouverParNom("Politique").isPresent()) {
            categoryService.creerCategorie("Politique", "Débats, élections, réformes et vie politique");
        }
        if (!categoryService.trouverParNom("Économie").isPresent()) {
            categoryService.creerCategorie("Économie", "Marchés, entreprises, emploi, finances et analyses économiques");
        }
        if (!categoryService.trouverParNom("Culture").isPresent()) {
            categoryService.creerCategorie("Culture", "Cinéma, musique, littérature, expositions et spectacles");
        }
        if (!categoryService.trouverParNom("Santé").isPresent()) {
            categoryService.creerCategorie("Santé", "Découvertes médicales, prévention, bien-être et actualités santé");
        }

        // Création d'articles enrichis
        User editeur = userService.trouverParLogin("editeur").orElse(null);
        Category tech = categoryService.trouverParNom("Technologie").orElse(null);
        Category sport = categoryService.trouverParNom("Sport").orElse(null);
        Category politique = categoryService.trouverParNom("Politique").orElse(null);
        Category economie = categoryService.trouverParNom("Économie").orElse(null);
        Category culture = categoryService.trouverParNom("Culture").orElse(null);
        Category sante = categoryService.trouverParNom("Santé").orElse(null);

        if (editeur != null && tech != null) {
            articleService.creerArticle(
                "L'intelligence artificielle bouleverse le secteur médical",
                "Des algorithmes révolutionnent le diagnostic et le suivi des patients.",
                "L'IA permet aujourd'hui de détecter des maladies plus tôt et d'améliorer la prise en charge. Plusieurs hôpitaux testent des solutions innovantes...",
                tech,
                editeur
            );
            articleService.creerArticle(
                "Web3 : la nouvelle ère d'Internet ?",
                "Décryptage des promesses et limites du Web décentralisé.",
                "Le Web3 suscite autant d'espoirs que de doutes. Entre blockchain, NFT et métavers, tour d'horizon des enjeux...",
                tech,
                editeur
            );
        }
        if (editeur != null && sport != null) {
            articleService.creerArticle(
                "JO 2024 : Paris prêt à accueillir le monde",
                "Les préparatifs s'accélèrent à quelques mois de l'événement.",
                "Stades, transports, sécurité : la capitale française se transforme pour offrir des Jeux inoubliables...",
                sport,
                editeur
            );
            articleService.creerArticle(
                "Football : le mercato d'été bat son plein",
                "Transferts records et surprises dans les grands clubs européens.",
                "Les plus grands clubs rivalisent pour attirer les stars du ballon rond. Analyse des mouvements majeurs...",
                sport,
                editeur
            );
        }
        if (editeur != null && politique != null) {
            articleService.creerArticle(
                "Réforme des retraites : le débat relancé",
                "Le gouvernement présente un nouveau projet controversé.",
                "Manifestations, négociations et enjeux sociaux : la réforme des retraites divise la société...",
                politique,
                editeur
            );
            articleService.creerArticle(
                "Élections européennes : quels enjeux pour l'avenir ?",
                "Les citoyens appelés aux urnes dans un contexte incertain.",
                "Entre montée des extrêmes et abstention record, les élections européennes s'annoncent décisives...",
                politique,
                editeur
            );
        }
        if (editeur != null && economie != null) {
            articleService.creerArticle(
                "Inflation : comment les ménages s'adaptent",
                "Hausse des prix, salaires, pouvoir d'achat : état des lieux.",
                "Face à l'inflation persistante, les Français modifient leurs habitudes de consommation...",
                economie,
                editeur
            );
            articleService.creerArticle(
                "Start-up françaises : une année record pour la tech",
                "Levées de fonds et innovations au rendez-vous.",
                "Les jeunes pousses françaises séduisent les investisseurs et exportent leur savoir-faire...",
                economie,
                editeur
            );
        }
        if (editeur != null && culture != null) {
            articleService.creerArticle(
                "Festival de Cannes : le palmarès 2024",
                "Retour sur les temps forts et les films primés.",
                "La Croisette a vibré au rythme des projections et des rencontres. Découvrez les lauréats de cette édition...",
                culture,
                editeur
            );
            articleService.creerArticle(
                "Musique : la scène française en pleine effervescence",
                "Nouveaux talents et retours attendus dans les bacs.",
                "Entre pop, rap et électro, les artistes hexagonaux s'imposent sur la scène internationale...",
                culture,
                editeur
            );
        }
        if (editeur != null && sante != null) {
            articleService.creerArticle(
                "Vaccins : les dernières avancées scientifiques",
                "Des chercheurs publient des résultats prometteurs contre plusieurs maladies.",
                "La recherche médicale progresse à grands pas, notamment dans la lutte contre le cancer et les maladies infectieuses...",
                sante,
                editeur
            );
            articleService.creerArticle(
                "Bien-être : comment mieux gérer le stress au quotidien",
                "Techniques et conseils pour une vie plus sereine.",
                "Yoga, méditation, alimentation : tour d'horizon des méthodes pour préserver sa santé mentale...",
                sante,
                editeur
            );
        }

        System.out.println("=== DONNÉES DE TEST CRÉÉES ===");
        System.out.println("Utilisateurs : admin/admin123, editeur/edit123, visiteur/visit123");
        System.out.println("Catégories : Technologie, Sport, Politique, Économie, Culture, Santé");
        System.out.println("Articles : 10 articles de test créés");
    }
}