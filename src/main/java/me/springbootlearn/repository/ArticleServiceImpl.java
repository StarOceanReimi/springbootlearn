package me.springbootlearn.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.springbootlearn.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

/**
 * Created by reimi on 11/19/17.
 */
@Component
public class ArticleServiceImpl implements ArticleService {

    private static Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleRepo articleRepo;

    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private TagsRepo tagsRepo;

    @Autowired
    @Qualifier("serviceMapper")
    private ObjectMapper mapper;

    /**
     * typical json passing from client is like below:
     * {
     *     author : {
     *         id : xxx
     *     },
     *     latestRev : {
     *          title : xxx,
     *          text : xxx,
     *     },
     *     tags : [ { name : xxx }, { name : xxx } ]
     * }
     * @param json the data representation received
     *             from client
     * @return
     */
    @Override
    @Transactional
    public Article newArticle(String json) {
        Objects.requireNonNull(json);
        Article article = null;
        try {
            article = mapper.readValue(json, Article.class);
        } catch (IOException e) {
            LOGGER.error("Json parsing error: ", e);
            throw new RuntimeException(e);
        }
        return newArticle(article);
    }


    private <T extends EntityBase<PK>, PK>
    Function<T, T> syncWithDatabase(CrudRepository<T, PK> repo) {
        return (obj)-> {
            String targetName = obj.getClass().getName();
            Optional<T> result = null;
            if(obj instanceof UniqueNameEntity && repo instanceof UniqueNameRepository) {
                UniqueNameEntity namedEntity = (UniqueNameEntity) obj;
                UniqueNameRepository<T, PK> uniqueNameRepo = (UniqueNameRepository<T, PK>) repo;
                if(obj.getId() != null ||
                        namedEntity.getName() != null &&
                        namedEntity.getName().length() != 0) {
                    result = uniqueNameRepo.findByIdOrName(obj.getId(), namedEntity.getName());
                    if(!result.isPresent()) {
                        //client choose an unknown id is suspicious
                        if(obj.getId() != null) {
                            LOGGER.warn("suspicious behavior detected. non-existed {} id: {}", targetName, obj.getId());
                            return null;
                        }
                        //the name is not exists in database implies a
                        //insert operation
                        return obj;
                    }
                    return result.get();
                }
            } else if (obj.getId() != null) {
                result = repo.findById(obj.getId());
                if (!result.isPresent()) {
                    LOGGER.warn("suspicious behavior detected. non-existed {} id: {}", targetName, obj.getId());
                    return null;
                }
                return result.get();
            }
            //implies a insert operation
            return obj;
        };
    }

    @Override
    @Transactional
    public Article newArticle(Article article) {
        Objects.requireNonNull(article);
        try {
//            LOGGER.debug("Article data: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(article));
            Author author = article.getAuthor();
            author = syncWithDatabase(authorRepo).apply(author);
            Objects.requireNonNull(author);
            article.setAuthor(author);
            Set<Tags> tagSet = article.getTags().stream()
                    .map(syncWithDatabase(tagsRepo))
                    .filter(t->t!=null)
                    .collect(toSet());
            article.setTags(tagSet);
            long now = System.currentTimeMillis();
            article.getLatestRev().setId(UUID.randomUUID().toString());
            article.getLatestRev().setCreateTime(now);
            article.getLatestRev().setArticle(article);
            article.setChangedTime(now);
            article = articleRepo.save(article);
            return article;
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Client data example:
     * {
     *     id : xxx,
     *     latestRev : {
     *         title: "xxx",
     *         text: "..."
     *     },
     *     tags : [ ... ]
     * }
     * @param json
     * @return
     */
    @Transactional
    @Override
    public Article newRevision(String json) {
        Objects.requireNonNull(json);
        Article article = null;
        try {
            article = mapper.readValue(json, Article.class);
        } catch (IOException e) {
            LOGGER.error("Json parsing error: ", e);
            throw new RuntimeException(e);
        }
        return newRevision(article);
    }

    @Transactional
    @Override
    public Article newRevision(Article article) {
        Objects.requireNonNull(article);
        if(article.getId() == null)
            throw new RuntimeException("can add revision to non-existed article");
        Revision detachedNewRev = article.getLatestRev();
        Set<Tags> detachedNewTags = article.getTags();
        article = syncWithDatabase(articleRepo).apply(article);
        Objects.requireNonNull(article);
        if(detachedNewTags != null) {
            Set<Tags> tagSet = detachedNewTags.stream()
                    .map(syncWithDatabase(tagsRepo))
                    .filter(t->t!=null)
                    .collect(toSet());
            article.setTags(tagSet);
        }
        long now = System.currentTimeMillis();
        detachedNewRev.setArticle(article);
        detachedNewRev.setCreateTime(now);
        detachedNewRev.setId(UUID.randomUUID().toString());
        article.setChangedTime(now);
        article.setLatestRev(detachedNewRev);
        return articleRepo.save(article);
    }
}
