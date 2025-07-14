package com.actualites.siteactualites.controller;

import com.actualites.siteactualites.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String pageConnexion() {
        return "login";
    }

    @GetMapping("/logout")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/after-login")
    public String afterLogin(HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.trouverParLogin(username)
                .map(user -> {
                    System.out.println("Définition de utilisateurConnecte : " + user.getLogin());
                    session.setAttribute("utilisateurConnecte", user);
                    if (userService.estAdministrateur(user)) {
                        System.out.println("Redirection finale vers /admin");
                        return "redirect:/admin";
                    } else if (userService.peutEditer(user)) {
                        System.out.println("Redirection finale vers /editeur");
                        return "redirect:/editeur";
                    } else {
                        System.out.println("Redirection finale vers /");
                        return "redirect:/";
                    }
                })
                .orElse("redirect:/login");
    }
}