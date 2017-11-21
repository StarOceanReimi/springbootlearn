package me.springbootlearn.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;

/**
 * Created by reimi on 11/18/17.
 */
@Entity
@Table(name="article_rev_t")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Revision extends EntityBase<String> {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name="article_id", nullable = false, foreignKey = @ForeignKey)
    private Article article;

    @Field
    @Column(nullable = false)
    private String title;

    @Field
    private String text;

    private long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
