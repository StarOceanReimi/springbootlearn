package me.springbootlearn;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import me.springbootlearn.entity.Article;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> articleMap = new HashMap<>();
        articleMap.put("id", 1);
        articleMap.put("changedTime", System.currentTimeMillis());
        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("name", "hello");
        articleMap.put("author", authorMap);
        Article article = mapper.convertValue(articleMap, Article.class);
        assertEquals("hello", article.getAuthor().getName());
    }
}
