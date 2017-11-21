package me.springbootlearn;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.springbootlearn.config.JPAConfig;
import me.springbootlearn.config.ServiceConfig;
import me.springbootlearn.entity.Article;
import me.springbootlearn.entity.Revision;
import me.springbootlearn.repository.ArticleRepo;
import me.springbootlearn.repository.ArticleService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Created by reimi on 11/18/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfig.class, ServiceConfig.class})
public class TestDatabase  {

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("serviceMapper")
    ObjectMapper mapper;

    @BeforeClass
    public static void globalSetup() throws IOException {
        DDLTools.main(null);
    }

    @Test
    public void testInsertArticle() throws IOException {
        ArticleService service = context.getBean(ArticleService.class);
        Path testData = Paths.get(System.getProperty("user.dir"), "articledata.json");
        Article[] articles = mapper.readValue(Files.newInputStream(testData, StandardOpenOption.READ), Article[].class);
        service.newArticle(articles[0]);
        service.newArticle(articles[1]);
        service.newRevision(articles[2]);
        service.newRevision(articles[3]);
        service.newRevision(articles[4]);
    }

    @Test
    public void testSelectArticle() throws IOException {
        ArticleRepo articleRepo = context.getBean(ArticleRepo.class);
        Pageable page = PageRequest.of(0, 2, Sort.Direction.DESC, "createTime");
        Page<Revision> pageIter = articleRepo.findRevisionsById(1, page);
//        List<Revision> revisionList = pageIter.getContent();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageIter));
//        page = pageIter.nextPageable();
//        pageIter = articleRepo.findRevisionsById(1, page);
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageIter));
    }

}
