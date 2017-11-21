package me.springbootlearn.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by reimi on 11/18/17.
 */
@Entity
@Table(name = "project_t")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Project extends EntityBase<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "project_tags_t",
        joinColumns = @JoinColumn(name = "proj_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "tags_id", referencedColumnName = "id")
    )
    private Set<Tags> tags;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Tags> getTags() {
        return tags;
    }

    public void setTags(Set<Tags> tags) {
        this.tags = tags;
    }
}
