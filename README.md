# 📰 Site d'Actualités - Application Web Multi-Rôles

## 👨‍💻 Auteurs

**Adja Sira Doumbouya**  
**Amadou Sall Gueye**  
**Zeynabou Ba**

## 🎯 Description du Projet

Ce projet est une application web complète de gestion d'actualités développée avec **Spring Boot**. Elle propose un système multi-rôles avec gestion des utilisateurs, des articles et des catégories, exposant des services web **REST** et **SOAP**.

### 🏗️ Architecture

L'application suit une architecture en couches classique :

- **Présentation** : Templates Thymeleaf avec interface responsive
- **Contrôleur** : Contrôleurs Spring MVC et Web Services
- **Service** : Logique métier
- **DAO/DAL** : Accès aux données
- **Modèle** : Entités JPA

## 🚀 Fonctionnalités

### 👥 Gestion Multi-Rôles

- **Visiteur** : Consultation des articles, recherche, navigation par catégories
- **Éditeur** : Création/modification d'articles, gestion des catégories
- **Administrateur** : Gestion complète (utilisateurs, articles, catégories, tokens)

### 📝 Gestion des Articles

- CRUD complet des articles
- Association aux catégories
- Système de recherche
- Affichage détaillé

### 🏷️ Gestion des Catégories

- CRUD des catégories
- Navigation par catégories
- Articles associés

### 👤 Gestion des Utilisateurs

- Authentification sécurisée
- Gestion des rôles
- Tokens de sécurité

### 🌐 Services Web

- **REST API** : Gestion des articles (JSON/XML)
- **SOAP Service** : Gestion des utilisateurs
- **Application cliente SOAP** : Interface console pour la gestion utilisateurs

## 🛠️ Technologies Utilisées

### Backend

- **Spring Boot 3.3.5** : Framework principal
- **Spring Security** : Authentification et autorisation
- **Spring Data JPA** : Persistance des données
- **Hibernate** : ORM
- **H2 Database** : Base de données en mémoire
- **Thymeleaf** : Moteur de templates
- **Spring Web Services** : Services SOAP
- **Jackson** : Sérialisation JSON/XML

### Frontend

- **Bootstrap 5** : Framework CSS
- **Thymeleaf** : Templates côté serveur
- **JavaScript** : Interactions côté client

### Outils de Développement

- **Maven** : Gestion des dépendances
- **Java 17** : Langage de programmation
- **JAXB** : Binding XML pour SOAP

## 📋 Prérequis

- **Java 17** ou supérieur
- **Maven 3.6** ou supérieur
- **Git** pour le versioning

## 🚀 Installation et Démarrage

### 1. Cloner le Repository

```bash
git clone https://github.com/votre-username/site-actualites.git
cd site-actualites
```

### 2. Compiler le Projet

```bash
mvn clean compile
```

### 3. Lancer l'Application

```bash
mvn spring-boot:run
```

L'application sera accessible à l'adresse : **http://localhost:8080**

### 4. Accès aux Interfaces

#### 🌐 Site Web Principal

- **URL** : http://localhost:8080
- **Fonctionnalités** : Consultation des articles, recherche, navigation

#### 🔐 Interface d'Administration

- **URL** : http://localhost:8080/admin
- **Identifiants par défaut** :
  - **Admin** : `admin@example.com` / `admin123`
  - **Éditeur** : `editeur@example.com` / `editeur123`

#### 🗄️ Console H2 (Base de Données)

- **URL** : http://localhost:8080/h2-console
- **JDBC URL** : `jdbc:h2:mem:actualites`
- **Username** : `sa`
- **Password** : (vide)

## 🌐 Services Web

### REST API - Articles

**Base URL** : `http://localhost:8080/api/articles`

| Méthode | Endpoint | Description             |
| ------- | -------- | ----------------------- |
| GET     | `/`      | Liste tous les articles |
| GET     | `/{id}`  | Article par ID          |
| POST    | `/`      | Créer un article        |
| PUT     | `/{id}`  | Modifier un article     |
| DELETE  | `/{id}`  | Supprimer un article    |

**Exemple de requête** :

```bash
curl -X GET http://localhost:8080/api/articles
```

### SOAP Service - Utilisateurs

**WSDL** : `http://localhost:8080/ws/users.wsdl`

**Endpoints** :

- Créer un utilisateur
- Modifier un utilisateur
- Supprimer un utilisateur
- Lister tous les utilisateurs
- Authentifier un utilisateur

## 💻 Application Cliente SOAP

### Compilation et Exécution

```bash
# Générer les classes clientes depuis le WSDL
wsimport -p com.actualites.client -s src/main/java http://localhost:8080/ws/users.wsdl

# Compiler l'application cliente
javac -cp ".:lib/*" src/main/java/com/actualites/client/SoapClientApp.java

# Exécuter l'application
java -cp ".:lib/*" com.actualites.client.SoapClientApp
```

### Fonctionnalités du Client

- Authentification utilisateur
- Menu interactif console
- Gestion CRUD des utilisateurs
- Affichage des résultats

## 📁 Structure du Projet

```
site-actualites/
├── src/
│   ├── main/
│   │   ├── java/com/actualites/siteactualites/
│   │   │   ├── config/          # Configuration Spring
│   │   │   ├── controller/      # Contrôleurs MVC et Web Services
│   │   │   ├── dal/            # Couche d'accès aux données
│   │   │   ├── dao/            # Interfaces DAO
│   │   │   ├── model/          # Entités JPA
│   │   │   ├── service/        # Services métier
│   │   │   └── webservice/     # Services SOAP
│   │   └── resources/
│   │       ├── templates/      # Templates Thymeleaf
│   │       ├── static/         # Ressources statiques
│   │       ├── wsdl/           # Définitions WSDL
│   │       └── xsd/            # Schémas XML
│   └── test/                   # Tests unitaires
├── pom.xml                     # Configuration Maven
└── README.md                   # Documentation
```

## 🔧 Configuration

### Base de Données

L'application utilise H2 en mode mémoire par défaut. Pour changer vers une base de données persistante, modifiez `application.properties` :

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/actualites
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/actualites
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Sécurité

- **CSRF** : Activé par défaut
- **Sessions** : Gérées par Spring Security
- **Mots de passe** : Chiffrés avec BCrypt

## 🧪 Tests

### Tests Unitaires

```bash
mvn test
```

### Tests d'Intégration

```bash
mvn verify
```

## 🐛 Débogage

### Logs

Les logs sont configurés pour afficher :

- Requêtes SQL Hibernate
- Messages Spring Web Services
- Logs de l'application

### Mode Debug

Activez le mode debug en ajoutant dans `application.properties` :

```properties
logging.level.com.actualites=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 📊 Données de Test

L'application inclut des données de test automatiquement chargées au démarrage :

- **Utilisateurs** : Admin, Éditeur, Visiteur
- **Catégories** : Technologie, Politique, Sport, Culture, Économie
- **Articles** : Articles d'exemple dans chaque catégorie

## 🔒 Sécurité

### Authentification

- Formulaire de connexion personnalisé
- Gestion des sessions
- Protection CSRF

### Autorisation

- Contrôle d'accès par rôles
- URLs sécurisées par profil
- Interface d'administration protégée
