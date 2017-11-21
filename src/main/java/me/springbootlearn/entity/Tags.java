package me.springbootlearn.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;

/**
 * Created by reimi on 11/18/17.
 */
@Entity
@Table(name="tags_t")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tags extends EntityBase<Integer> implements UniqueNameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Field
    @Column(nullable = false, unique = true)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
