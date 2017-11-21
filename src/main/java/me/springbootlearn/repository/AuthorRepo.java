package me.springbootlearn.repository;

import me.springbootlearn.entity.Author;

/**
 * Created by reimi on 11/19/17.
 */
public interface AuthorRepo extends UniqueNameRepository<Author, Integer> {
}
