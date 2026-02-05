# TP05 - JDBC - Movies Collection

## Auteur
**Nom :** BOUDI
**Prénom :** Salah Eddine
**Formation :** Java 2 - 2026

## Description
Application Java de gestion de collection de films utilisant JDBC pour la persistance des données.

## Technologies utilisées
- Java 21
- JDBC
- SQLite
- JUnit 5
- AssertJ
- Maven

## Structure du projet

### Entités
- `Genre` : Catégorie de film (Drama, Comedy, Thriller...)
- `Movie` : Film avec titre, date de sortie, genre, durée, réalisateur et résumé

### DAO (Data Access Objects)
- `GenreDao` : Gestion des genres (CRUD)
- `MovieDao` : Gestion des films (CRUD avec JOIN)
- `DataSourceFactory` : Factory pour la connexion à la base de données

### Base de données
- SQLite (`sqlite.db`)
- Tables : `genre`, `movie`
- Relation : `movie.genre_id` → `genre.idgenre`

## Fonctionnalités implémentées

### GenreDao
- ✅ `listGenres()` - Liste tous les genres
- ✅ `getGenre(String name)` - Récupère un genre par nom (retourne Optional)
- ✅ `addGenre(String name)` - Ajoute un nouveau genre

### MovieDao
- ✅ `listMovies()` - Liste tous les films avec leur genre (JOIN)
- ✅ `listMoviesByGenre(String genreName)` - Filtre les films par genre
- ✅ `addMovie(Movie movie)` - Ajoute un film et retourne l'objet avec l'ID généré

## Bonus réalisés

### Bonus 1 : DriverManager
- ✅ Refactorisation de `DataSourceFactory` pour utiliser `DriverManager`
- ✅ Code indépendant du driver SQLite (database-agnostic)
- ✅ Changement de base de données facilité

### Bonus 2 : Optional<Genre>
- ✅ `getGenre()` retourne `Optional<Genre>` au lieu de `null`
- ✅ Élimine les risques de `NullPointerException`
- ✅ Tests adaptés avec `isPresent()` et `isEmpty()`

## Tests

### GenreDaoTestCase
- ✅ `shouldListGenres()` - 4/4 tests verts
- ✅ `shouldGetGenreByName()`
- ✅ `shouldNotGetUnknownGenre()`
- ✅ `shouldAddGenre()`

### MovieDaoTestCase
- ✅ `shouldListMovies()` - 3/3 tests verts
- ✅ `shouldListMoviesByGenre()`
- ✅ `shouldAddMovie()`

**Total : 7/7 tests passent ✅**

## Installation et exécution

### Prérequis
- JDK 21 ou supérieur
- Maven 3.6+

### Compilation
```bash
mvn clean compile
```

### Lancer les tests
```bash
mvn test
```

### Structure de la base de données

#### Table genre
```sql
CREATE TABLE genre (
    idgenre INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL
);
```

#### Table movie
```sql
CREATE TABLE movie (
    idmovie INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(100) NOT NULL,
    release_date DATETIME,
    genre_id INT NOT NULL,
    duration INT,
    director VARCHAR(100) NOT NULL,
    summary MEDIUMTEXT,
    FOREIGN KEY (genre_id) REFERENCES genre(idgenre)
);
```

## Points techniques importants

### Sécurité SQL
- ✅ Utilisation systématique de `PreparedStatement`
- ✅ Protection contre les injections SQL
- ✅ Paramètres bindés avec `setString()`, `setInt()`, etc.

### Gestion des ressources
- ✅ `try-with-resources` pour la fermeture automatique
- ✅ Pas de fuite de connexions
- ✅ Ordre de fermeture respecté (inverse de l'ouverture)

### Conversions de types
- ✅ `LocalDate` ↔ `java.sql.Date`
- ✅ Gestion des objets imbriqués (`Movie` contient `Genre`)

### Récupération d'ID auto-généré
- ✅ Flag `Statement.RETURN_GENERATED_KEYS`
- ✅ `getGeneratedKeys()` après `executeUpdate()`

## Date de soumission
Février le 07 2026