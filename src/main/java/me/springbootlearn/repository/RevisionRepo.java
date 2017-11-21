package me.springbootlearn.repository;

import me.springbootlearn.entity.Revision;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by reimi on 11/20/17.
 */
public interface RevisionRepo extends CrudRepository<Revision, String> {
}
