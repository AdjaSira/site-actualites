package com.actualites.siteactualites.webservice;

import com.actualites.siteactualites.dao.TokenDAO;
import com.actualites.siteactualites.model.entity.Token;
import com.actualites.siteactualites.model.entity.User;
import com.actualites.siteactualites.model.enums.UserRole;
import com.actualites.siteactualites.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Endpoint
@Component
public class UserSoapService {

    private static final Logger LOGGER = Logger.getLogger(UserSoapService.class.getName());
    private static final String NAMESPACE_URI = "http://siteactualites.com/soap/users";

    @Autowired
    private UserService userService;

    @Autowired
    private TokenDAO tokenDAO;

    private boolean isValidToken(String tokenValue) {
        if (tokenValue == null || tokenValue.trim().isEmpty()) {
            LOGGER.warning("Jeton null ou vide");
            return false;
        }

        Optional<Token> tokenOpt = tokenDAO.findByValeur(tokenValue);
        if (tokenOpt.isEmpty()) {
            LOGGER.warning("Jeton invalide : " + tokenValue);
            return false;
        }

        Token token = tokenOpt.get();
        if (token.getDateExpiration().isBefore(LocalDateTime.now())) {
            LOGGER.warning("Jeton expiré : " + tokenValue);
            return false;
        }

        User user = token.getUser();
        boolean isAdmin = userService.estAdministrateur(user);
        LOGGER.info("Validation du jeton : " + tokenValue + ", Admin : " + isAdmin);
        return isAdmin;
    }

    // Adaptateur JAXB pour role
    public static class RoleAdapter extends XmlAdapter<String, String> {
        @Override
        public String unmarshal(String v) throws Exception {
            LOGGER.info("Unmarshalling role : " + v);
            return v != null ? v.trim() : null;
        }

        @Override
        public String marshal(String v) throws Exception {
            return v;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ListUsersRequest")
    @ResponsePayload
    public ListUsersResponse listUsers(@RequestPayload ListUsersRequest request) {
        LOGGER.info("Requête SOAP ListUsers reçue, request: " + request);
        LOGGER.info("Jeton reçu : " + (request != null ? request.getToken() : "null request"));
        if (!isValidToken(request.getToken())) {
            throw new RuntimeException("Jeton invalide ou utilisateur non autorisé");
        }
        ListUsersResponse response = new ListUsersResponse();
        List<User> users = userService.listerTous();
        response.getUsers().addAll(users);
        LOGGER.info("Retour de " + users.size() + " utilisateurs");
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateUserRequest")
    @ResponsePayload
    public CreateUserResponse createUser(@RequestPayload CreateUserRequest request) {
        LOGGER.info("Requête SOAP CreateUser reçue, request: " + request);
        LOGGER.info("Jeton reçu : " + request.getToken());
        LOGGER.info("Login reçu : " + request.getLogin());
        LOGGER.info("Mot de passe reçu : " + (request.getMotDePasse() != null ? "non null" : "null"));
        LOGGER.info("Email reçu : " + request.getEmail());
        LOGGER.info("Role reçu avant conversion : " + request.getRole());

        if (!isValidToken(request.getToken())) {
            throw new RuntimeException("Jeton invalide ou utilisateur non autorisé");
        }

        UserRole role;
        try {
            if (request.getRole() == null || request.getRole().trim().isEmpty()) {
                LOGGER.warning("Rôle est null ou vide : " + request.getRole());
                throw new RuntimeException("Le rôle ne peut pas être null ou vide");
            }
            role = UserRole.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Rôle invalide : " + request.getRole());
            throw new RuntimeException("Rôle invalide : " + request.getRole());
        }

        User userEntity = userService.creerUtilisateur(
                request.getLogin(),
                request.getMotDePasse(),
                request.getEmail(),
                role
        );

        CreateUserResponse response = new CreateUserResponse();
        response.setUser(userEntity); // Utilise l'entité User du serveur
        LOGGER.info("Utilisateur créé : " + userEntity.getLogin());
        return response;
    }

    // Classes internes pour les requêtes/réponses
    @XmlRootElement(name = "ListUsersRequest", namespace = "http://siteactualites.com/soap/users")
    public static class ListUsersRequest {
        private String token;

        @XmlElement(name = "token", namespace = "http://siteactualites.com/soap/users", required = true)
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @XmlRootElement(name = "ListUsersResponse", namespace = NAMESPACE_URI)
    public static class ListUsersResponse {
        private List<User> users = new java.util.ArrayList<>();

        @XmlElement(name = "user")
        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }
    }

    @XmlRootElement(name = "CreateUserRequest", namespace = NAMESPACE_URI)
    public static class CreateUserRequest {
        private String token;
        private String login;
        private String motDePasse;
        private String email;
        private String role;

        public CreateUserRequest() {
            LOGGER.info("Constructeur CreateUserRequest appelé");
        }

        @XmlElement(name = "token", namespace = "http://siteactualites.com/soap/users", required = true)
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        @XmlElement(name = "login", namespace = "http://siteactualites.com/soap/users", required = true)
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        @XmlElement(name = "motDePasse", namespace = "http://siteactualites.com/soap/users", required = true)
        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

        @XmlElement(name = "email", namespace = "http://siteactualites.com/soap/users")
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @XmlElement(name = "role", namespace = "http://siteactualites.com/soap/users", required = true)
        @XmlJavaTypeAdapter(RoleAdapter.class)
        public String getRole() {
            LOGGER.info("Récupération de role : " + role);
            return role;
        }
        public void setRole(String role) { this.role = role; }
    }

    @XmlRootElement(name = "CreateUserResponse", namespace = NAMESPACE_URI)
    public static class CreateUserResponse {
        private User user;

        @XmlElement(required = true)
        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    @XmlRootElement(name = "UpdateUserRequest", namespace = NAMESPACE_URI)
    public static class UpdateUserRequest {
        private String token;
        private Long id;
        private String login;
        private String motDePasse;
        private String email;
        private String role;

        @XmlElement(name = "token", namespace = NAMESPACE_URI, required = true)
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        @XmlElement(name = "id", namespace = NAMESPACE_URI, required = true)
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        @XmlElement(name = "login", namespace = NAMESPACE_URI, required = true)
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        @XmlElement(name = "motDePasse", namespace = NAMESPACE_URI)
        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

        @XmlElement(name = "email", namespace = NAMESPACE_URI)
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @XmlElement(name = "role", namespace = NAMESPACE_URI, required = true)
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    @XmlRootElement(name = "DeleteUserRequest", namespace = NAMESPACE_URI)
    public static class DeleteUserRequest {
        private String token;
        private Long id;

        @XmlElement(name = "token", namespace = NAMESPACE_URI, required = true)
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        @XmlElement(name = "id", namespace = NAMESPACE_URI, required = true)
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateUserRequest")
    @ResponsePayload
    public CreateUserResponse updateUser(@RequestPayload UpdateUserRequest request) {
        LOGGER.info("Requête SOAP UpdateUser reçue, request: " + request);
        if (!isValidToken(request.getToken())) {
            throw new RuntimeException("Jeton invalide ou utilisateur non autorisé");
        }

        Optional<User> userOpt = userService.trouverParId(request.getId());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'id : " + request.getId());
        }

        User user = userOpt.get();
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole()));
        if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
            user.setMotDePasse(request.getMotDePasse()); // Sera haché par UserService
        }
        user = userService.modifierUtilisateur(user);

        CreateUserResponse response = new CreateUserResponse();
        response.setUser(user);
        LOGGER.info("Utilisateur modifié : " + user.getLogin());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeleteUserRequest")
    @ResponsePayload
    public void deleteUser(@RequestPayload DeleteUserRequest request) {
        LOGGER.info("Requête SOAP DeleteUser reçue, request: " + request);
        if (!isValidToken(request.getToken())) {
            throw new RuntimeException("Jeton invalide ou utilisateur non autorisé");
        }
        userService.supprimerUtilisateur(request.getId());
        LOGGER.info("Utilisateur supprimé avec ID : " + request.getId());
    }
}