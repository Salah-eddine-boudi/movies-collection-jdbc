package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.isen.java2.db.entities.Genre;

/**
 * Data Access Object for Genre entity.
 * Provides CRUD operations for movie genres in the database.
 * 
 * @author SALAH EDDINE
 * @version 1.0
 * @since February 2026
 */


public class GenreDao {
	
	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
		     PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre");
		     ResultSet resultSet = statement.executeQuery()) {
			
			while (resultSet.next()) {
				int id = resultSet.getInt("idgenre");
				String name = resultSet.getString("name");
				genres.add(new Genre(id, name));
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("Error listing genres", e);
		}
		
		return genres;
	}
	
	public Optional<Genre> getGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
		     PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
			
			statement.setString(1, name);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					int id = resultSet.getInt("idgenre");
					String genreName = resultSet.getString("name");
					return Optional.of(new Genre(id, genreName));
				}
				return Optional.empty();
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("Error getting genre: " + name, e);
		}
	}
	
	public void addGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
		     PreparedStatement statement = connection.prepareStatement("INSERT INTO genre(name) VALUES(?)")) {
			
			statement.setString(1, name);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			throw new RuntimeException("Error adding genre: " + name, e);
		}
	}
}