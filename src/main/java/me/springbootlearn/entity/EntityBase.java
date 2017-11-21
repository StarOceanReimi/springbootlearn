package me.springbootlearn.entity;

/**
 * Created by reimi on 11/19/17.
 */
public abstract class EntityBase<PK> {
    public abstract PK   getId();
    public abstract void setId(PK id);
}
