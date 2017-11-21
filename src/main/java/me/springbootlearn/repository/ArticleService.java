package me.springbootlearn.repository;

import me.springbootlearn.entity.Article;
import me.springbootlearn.entity.Revision;
import org.springframework.stereotype.Service;

/**
 * Created by reimi on 11/19/17.
 */
public interface ArticleService {

    Article newArticle(String json);

    Article newArticle(Article article);

    Article newRevision(String json);

    Article newRevision(Article article);

}
