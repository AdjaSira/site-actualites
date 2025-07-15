package com.siteactualites.soapclient;

import com.siteactualites.soapclient.generated.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.util.Scanner;
import javax.xml.namespace.QName;

@SpringBootApplication
public class SoapClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SoapClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application cliente SOAP démarrée !");

        Scanner scanner = new Scanner(System.in);

        // Demander un token (simulé, car pas d'authentification explicite dans le WSDL)
        System.out.print("Entrez votre token d'authentification : ");
        String token = scanner.nextLine();

        // Vérifier si le token est valide (simplifié)
        if (token != null && !token.isEmpty()) {
            System.out.println("Authentification simulée avec succès. Accès au menu administrateur.");
            showAdminMenu(scanner, token);
        } else {
            System.out.println("Token invalide. Veuillez réessayer.");
        }

        scanner.close();
    }

    private void showAdminMenu(Scanner scanner, String token) {
        // Créer une instance du service avec l'URL correcte du WSDL
        URL wsdlUrl;
        try {
            wsdlUrl = new URL("http://localhost:8080/ws/users.wsdl");
            // Code corrigé :
            UserService service = new UserService();
            System.out.println("Ports disponibles : " + service.getPorts());
            UserServicePortType port = service.getUserServicePort();
            boolean running = true;

            while (running) {
                System.out.println("\nMenu administrateur :");
                System.out.println("1. Lister les utilisateurs");
                System.out.println("2. Ajouter un utilisateur");
                System.out.println("3. Modifier un utilisateur");
                System.out.println("4. Supprimer un utilisateur");
                System.out.println("5. Quitter");
                System.out.print("Choisissez une option : ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        // Lister les utilisateurs
                        ListUsersRequest listRequest = new ObjectFactory().createListUsersRequest();
                        listRequest.setToken(token);
                        try {
                            ListUsersResponse listResponse = port.listUsers(listRequest);
                            // Affiche le nombre d'utilisateurs trouvés
                            System.out.println("Nombre d'utilisateurs : " + listResponse.getUser().size());
                            if (!listResponse.getUser().isEmpty()) {
                                for (User user : listResponse.getUser()) {
                                    System.out.println("ID: " + user.getId() + ", Login: " + user.getLogin() +
                                            ", Email: " + user.getEmail() + ", Role: " + user.getRole());
                                }
                            } else {
                                System.out.println("Aucun utilisateur trouvé.");
                            }
                        } catch (Exception e) {
                            System.out.println("Erreur lors de la liste des utilisateurs : " + e.getMessage());
                        }
                        break;

                    case "2":
                        // Ajouter un utilisateur
                        System.out.print("Login : ");
                        String login = scanner.nextLine();
                        System.out.print("Mot de passe : ");
                        String motDePasse = scanner.nextLine();
                        System.out.print("Email : ");
                        String email = scanner.nextLine();
                        System.out.print("Rôle (VISITOR, EDITOR, ADMIN) : ");
                        String role = scanner.nextLine();

                        CreateUserRequest createRequest = new ObjectFactory().createCreateUserRequest();
                        createRequest.setToken(token);
                        createRequest.setLogin(login);
                        createRequest.setMotDePasse(motDePasse);
                        createRequest.setEmail(email);
                        createRequest.setRole(role);
                        try {
                            CreateUserResponse createResponse = port.createUser(createRequest);
                            if (createResponse != null && createResponse.getUser() != null) {
                                System.out.println("Utilisateur créé : " + createResponse.getUser().getLogin());
                            } else {
                                System.out.println("Échec de la création.");
                            }
                        } catch (Exception e) {
                            System.out.println("Erreur lors de la création : " + e.getMessage());
                        }
                        break;

                    case "3":
                        // Modifier un utilisateur
                        System.out.print("ID de l'utilisateur à modifier : ");
                        long id = Long.parseLong(scanner.nextLine());
                        System.out.print("Nouveau login : ");
                        String newLogin = scanner.nextLine();
                        System.out.print("Nouveau mot de passe (optionnel, laissez vide si inchangé) : ");
                        String motDePasseInput = scanner.nextLine();
                        String newMotDePasse = motDePasseInput.isEmpty() ? null : motDePasseInput;
                        System.out.print("Nouvel email : ");
                        String newEmail = scanner.nextLine();
                        System.out.print("Nouveau rôle (VISITOR, EDITOR, ADMIN) : ");
                        String newRole = scanner.nextLine();

                        UpdateUserRequest updateRequest = new ObjectFactory().createUpdateUserRequest();
                        updateRequest.setToken(token);
                        updateRequest.setId(id);
                        updateRequest.setLogin(newLogin);
                        updateRequest.setMotDePasse(newMotDePasse);
                        updateRequest.setEmail(newEmail);
                        updateRequest.setRole(newRole);
                        try {
                            UpdateUserResponse updateResponse = port.updateUser(updateRequest);
                            if (updateResponse != null && updateResponse.getUser() != null) {
                                System.out.println("Utilisateur modifié : " + updateResponse.getUser().getLogin());
                            } else {
                                System.out.println("Échec de la modification.");
                            }
                        } catch (Exception e) {
                            System.out.println("Erreur lors de la modification : " + e.getMessage());
                        }
                        break;

                    case "4":
                        // Supprimer un utilisateur
                        System.out.print("ID de l'utilisateur à supprimer : ");
                        long deleteId = Long.parseLong(scanner.nextLine());

                        DeleteUserRequest deleteRequest = new ObjectFactory().createDeleteUserRequest();
                        deleteRequest.setToken(token);
                        deleteRequest.setId(deleteId);
                        try {
                            DeleteUserResponse deleteResponse = port.deleteUser(deleteRequest);
                            if (deleteResponse != null && deleteResponse.isSuccess()) {
                                System.out.println("Utilisateur supprimé avec ID : " + deleteId);
                            } else {
                                System.out.println("Échec de la suppression : " +
                                        (deleteResponse != null ? deleteResponse.getMessage() : "Réponse nulle"));
                            }
                        } catch (Exception e) {
                            System.out.println("Erreur lors de la suppression : " + e.getMessage());
                        }
                        break;

                    case "5":
                        running = false;
                        System.out.println("Déconnexion...");
                        break;

                    default:
                        System.out.println("Option invalide. Veuillez réessayer.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'initialisation du service : " + e.getMessage());
        }
    }
}