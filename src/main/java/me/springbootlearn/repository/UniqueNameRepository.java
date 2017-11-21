package me.springbootlearn.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * Created by reimi on 11/20/17.
 */
@NoRepositoryBean
public interface UniqueNameRepository<T, PK> extends CrudRepository<T, PK> {

    Optional<T> findByIdOrName(PK pk, String name);
}
