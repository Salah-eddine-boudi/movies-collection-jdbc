package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;


/**
 * DAO for Movie entity - Manages database operations for movies.
 * Includes JOIN operations with Genre table.
 * 
 * @author SALAH
 * @version 1.0
 * @since 2026-02
 */
public class MovieDao {
	
	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<>();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
		     PreparedStatement statement = connection.prepareStatement(
		         "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre");
		     ResultSet resultSet = statement.executeQuery()) {
			
			while (resultSet.next()) {
				Movie movie = createMovieFromResultSet(resultSet);
				movies.add(movie);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("Error listing movies", e);
		}
		
		return movies;
	}
	
	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
		     PreparedStatement statement = connection.prepareStatement(
		         "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?")) {
			
			statement.setString(1, genreName);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Movie movie = createMovieFromResultSet(resultSet);
					movies.add(movie);
				}
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("Error listing movies by genre: " + genreName, e);
		}
		
		return movies;
	}
	
	public Movie addMovie(Movie movie) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
		     PreparedStatement statement = connection.prepareStatement(
		         "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) VALUES(?, ?, ?, ?, ?, ?)",
		         Statement.RETURN_GENERATED_KEYS)) {
			
			
			statement.setString(1, movie.getTitle());
			statement.setDate(2, Date.valueOf(movie.getReleaseDate()));
			statement.setInt(3, movie.getGenre().getId());
			statement.setInt(4, movie.getDuration());
			statement.setString(5, movie.getDirector());
			statement.setString(6, movie.getSummary());
			
			
			statement.executeUpdate();
			
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int generatedId = generatedKeys.getInt(1);
					
					
					return new Movie(
						generatedId,
						movie.getTitle(),
						movie.getReleaseDate(),
						movie.getGenre(),
						movie.getDuration(),
						movie.getDirector(),
						movie.getSummary()
					);
				} else {
					throw new RuntimeException("Failed to get generated ID for movie");
				}
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("Error adding movie: " + movie.getTitle(), e);
		}
	}
	
	/**
	 * Méthode utilitaire pour créer un Movie à partir d'un ResultSet
	 * (évite la duplication de code entre listMovies et listMoviesByGenre)
	 */
	private Movie createMovieFromResultSet(ResultSet resultSet) throws SQLException {
		
		int movieId = resultSet.getInt("idmovie");
		String title = resultSet.getString("title");
		Date releaseDate = resultSet.getDate("release_date");
		int duration = resultSet.getInt("duration");
		String director = resultSet.getString("director");
		String summary = resultSet.getString("summary");
		
		
		int genreId = resultSet.getInt("idgenre");
		String genreName = resultSet.getString("name");
		Genre genre = new Genre(genreId, genreName);
		
		
		return new Movie(
			movieId,
			title,
			releaseDate != null ? releaseDate.toLocalDate() : null,
			genre,
			duration,
			director,
			summary
		);
	}
}