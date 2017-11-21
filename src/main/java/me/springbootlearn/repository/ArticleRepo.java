package me.springbootlearn.repository;

import me.springbootlearn.entity.Article;
import me.springbootlearn.entity.Revision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by reimi on 11/19/17.
 */
public interface ArticleRepo extends CrudRepository<Article, Integer>,
    PagingAndSortingRepository<Article, Integer> {

    @Query("select r from Revision r where r.article.id = ?1")
    Page<Revision> findRevisionsById(Integer id, Pageable page);
}
