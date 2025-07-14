package com.actualites.siteactualites.dao;

import com.actualites.siteactualites.model.entity.Article;
import com.actualites.siteactualites.model.entity.Category;
import com.actualites.siteactualites.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleDAO extends JpaRepository<Article, Long> {

    @Query("SELECT DISTINCT a FROM Article a JOIN FETCH a.category c JOIN FETCH a.auteur u ORDER BY a.dateCreation DESC")
    List<Article> findAllOrderByDateCreationDesc();

    @Query("SELECT DISTINCT a FROM Article a JOIN FETCH a.category c JOIN FETCH a.auteur u ORDER BY a.dateCreation DESC")
    Page<Article> findAllPaginated(Pageable pageable);

    @Query("SELECT DISTINCT a FROM Article a JOIN FETCH a.category c JOIN FETCH a.auteur u WHERE a.category = :category ORDER BY a.dateCreation DESC")
    List<Article> findByCategory(@Param("category") Category category);

    @Query("SELECT DISTINCT a FROM Article a JOIN FETCH a.category c JOIN FETCH a.auteur u WHERE a.category = :category ORDER BY a.dateCreation DESC")
    Page<Article> findByCategoryPaginated(@Param("category") Category category, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Article a JOIN FETCH a.category c JOIN FETCH a.auteur u WHERE a.auteur = :auteur")
    List<Article> findByAuteur(@Param("auteur") User auteur);

    @Query("SELECT DISTINCT a FROM Article a JOIN FETCH a.category c JOIN FETCH a.auteur u WHERE a.titre LIKE %:titre%")
    List<Article> findByTitreContaining(@Param("titre") String titre);

    @Query("SELECT DISTINCT a FROM Article a JOIN FETCH a.category c JOIN FETCH a.auteur u WHERE a.id = :id")
    Optional<Article> findById(@Param("id") Long id);
}