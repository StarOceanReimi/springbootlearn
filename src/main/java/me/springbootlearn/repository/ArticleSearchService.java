package me.springbootlearn.repository;

import me.springbootlearn.entity.Article;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by reimi on 11/19/17.
 */
@Component
public class ArticleSearchService implements InitializingBean, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleSearchService.class);

    ThreadLocal<EntityManager> entityManagerHolder = new ThreadLocal<>();

    @Autowired
    private EntityManagerFactory factory;

    @Autowired
    private EntityManager entityManager;

    private volatile boolean useLocalEntityManager;

    public void setUseLocalEntityManager(boolean use) {
        useLocalEntityManager = use;
        if(useLocalEntityManager && entityManagerHolder.get() == null) {
            entityManagerHolder.set(factory.createEntityManager());
        }
    }

    public void reindex(Class<?>... indexers) throws InterruptedException {
        EntityManager em = null;
        try {
            em = factory.createEntityManager();
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
            LOGGER.info("CREATING LUCENE INDEX...");
            fullTextEntityManager.createIndexer(indexers).startAndWait();
        } finally {
            if(em != null) em.close();
        }
    }

    @Transactional
    public List<Article> fuzzySearchArticle(String searchTerm) {
        EntityManager entityManager = null;
        if(useLocalEntityManager) {
            entityManager = entityManagerHolder.get();
        } else {
            entityManager = this.entityManager;
        }
        FullTextEntityManager manager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder builder = manager.getSearchFactory().buildQueryBuilder().forEntity(Article.class).get();
        Query luceneQuery = builder.keyword().fuzzy().withEditDistanceUpTo(2)
                .withPrefixLength(1)
                .onFields("author.name", "latestRev.title", "latestRev.text")
                .matching(searchTerm).createQuery();
        javax.persistence.Query jpaQuery = manager.createFullTextQuery(luceneQuery, Article.class);
        List<Article> result = null;
        try {
            result = jpaQuery.getResultList();
        } catch (NoResultException e) {

        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reindex();
    }

    @Override
    public void close() throws Exception {
        if(entityManagerHolder.get() != null) {
            entityManagerHolder.get().close();
            entityManagerHolder.remove();
        }
    }
}
