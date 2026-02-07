package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDaoTestCase {
	
	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	@Test
	public void shouldListMovies() {
		// GIVEN
		MovieDao movieDao = new MovieDao();
		
		// WHEN
		List<Movie> movies = movieDao.listMovies();
		
		// THEN
		assertThat(movies).isNotNull();
		assertThat(movies).hasSize(3);
		
		// Vérifier le premier film
		assertThat(movies.get(0).getId()).isEqualTo(1);
		assertThat(movies.get(0).getTitle()).isEqualTo("Title 1");
		assertThat(movies.get(0).getGenre()).isNotNull();
		assertThat(movies.get(0).getGenre().getName()).isEqualTo("Drama");
		assertThat(movies.get(0).getDuration()).isEqualTo(120);
		assertThat(movies.get(0).getDirector()).isEqualTo("director 1");
	}
	
	@Test
	public void shouldListMoviesByGenre() {
		// GIVEN
		MovieDao movieDao = new MovieDao();
		
		// WHEN
		List<Movie> comedyMovies = movieDao.listMoviesByGenre("Comedy");
		
		// THEN
		assertThat(comedyMovies).isNotNull();
		assertThat(comedyMovies).hasSize(2);  // 2 films Comedy dans initDb()
		
		// Vérifier que tous les films sont bien du genre Comedy
		assertThat(comedyMovies.get(0).getGenre().getName()).isEqualTo("Comedy");
		assertThat(comedyMovies.get(1).getGenre().getName()).isEqualTo("Comedy");
		
		// Vérifier les titres
		assertThat(comedyMovies.get(0).getTitle()).isEqualTo("My Title 2");
		assertThat(comedyMovies.get(1).getTitle()).isEqualTo("Third title");
		
		// WHEN - tester avec Drama
		List<Movie> dramaMovies = movieDao.listMoviesByGenre("Drama");
		
		// THEN
		assertThat(dramaMovies).hasSize(1);  // 1 seul film Drama
		assertThat(dramaMovies.get(0).getTitle()).isEqualTo("Title 1");
	}
	
	@Test
	public void shouldAddMovie() throws Exception {
		// GIVEN
		MovieDao movieDao = new MovieDao();
		
		// Créer un Genre existant (Drama = id 1)
		Genre drama = new Genre(1, "Drama");
		
		// Créer un nouveau Movie SANS ID
		Movie newMovie = new Movie(
			"New Movie Title",
			LocalDate.of(2024, 3, 15),
			drama,
			150,
			"New Director",
			"This is a new movie summary"
		);
		
		// WHEN
		Movie addedMovie = movieDao.addMovie(newMovie);
		
		// THEN - Vérifier que le movie retourné a un ID
		assertThat(addedMovie).isNotNull();
		assertThat(addedMovie.getId()).isNotNull();
		assertThat(addedMovie.getId()).isEqualTo(4);  
		
		// Vérifier que les autres champs sont identiques
		assertThat(addedMovie.getTitle()).isEqualTo("New Movie Title");
		assertThat(addedMovie.getReleaseDate()).isEqualTo(LocalDate.of(2024, 3, 15));
		assertThat(addedMovie.getGenre().getId()).isEqualTo(1);
		assertThat(addedMovie.getDuration()).isEqualTo(150);
		assertThat(addedMovie.getDirector()).isEqualTo("New Director");
		assertThat(addedMovie.getSummary()).isEqualTo("This is a new movie summary");
		
		// WHEN - Vérifier que le film a bien été ajouté en base
		List<Movie> allMovies = movieDao.listMovies();
		
		// THEN
		assertThat(allMovies).hasSize(4);  
	}
}