package me.springbootlearn;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.springbootlearn.entity.Article;
import me.springbootlearn.entity.Tags;
import me.springbootlearn.repository.ArticleRepo;
import me.springbootlearn.repository.ArticleSearchService;
import me.springbootlearn.repository.ArticleService;
import me.springbootlearn.repository.TagsRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class App implements CommandLineRunner
{
    @Autowired
    ApplicationContext context;

    public static void main( String[] args )
    {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(App.class, args);
//        ArticleRepo repo = context.getBean(ArticleRepo.class);
//        Article article = new Article();
//        Author author = new Author();
//        author.setName("Haha");
//        author.setGithub("github");
//        author.setWebpage("web");
//        author.setTweeter("tweets");
//        author.setLinkedIn("profile");
//        article.setAuthor(author);
//        article.setChangedTime(System.currentTimeMillis());
//        Set<Tags> tags = Arrays.<String>asList("java", "spring", "hibernate").stream()
//                            .map(s->{Tags t = new Tags(); t.setName(s); return t; })
//                            .collect(toSet());
//        article.setTags(tags);
//        Revision revision = new Revision();
//        revision.setId(UUID.randomUUID().toString());
//        revision.setArticle(article);
//        revision.setCreateTime(System.currentTimeMillis());
//        revision.setTitle("Random tech article");
//        revision.setText("Random Content");
//        article.setLasteatRev(revision);
//        repo.save(article);
    }

    @Override
    public void run(String... strings) throws Exception {

//        String[] names = context.getBeanDefinitionNames();
//        Arrays.sort(names);
//        Arrays.stream(names).forEach(System.out::println);
//        ArticleService service = context.getBean(ArticleService.class);
        TagsRepo repo = context.getBean(TagsRepo.class);
        Optional<Tags> tag = repo.findByIdOrName(null, "nodejs");
        if(tag.isPresent()) {
            System.out.println(tag.get().getId() + "," +tag.get().getName());
        }

//        ArticleSearchService searchService = context.getBean(ArticleSearchService.class);
//        searchService.setUseLocalEntityManager(true);
//        List<Article> result = searchService.fuzzySearchArticle("Yt");
//        ObjectMapper mapper = (ObjectMapper) context.getBean("serviceMapper");
//        String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
//        System.out.println(result.get(0).getLatestRev().getText());
//        searchService.close();
    }
}
