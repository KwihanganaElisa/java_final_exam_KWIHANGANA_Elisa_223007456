package com.eduportal.dao;

import java.util.List;

/**
 * Generic interface for Data Access Objects (DAOs) to define standard CRUD operations.
 */
public interface BaseDAO<T> {

    /** Inserts a new object into the database. */
    boolean insert(T object);

    /** Retrieves an object by its primary key (ID). */
    T getById(int id);

    /** Retrieves all objects of this type from the database. */
    List<T> getAll();

    /** Updates an existing object in the database. */
    boolean update(T object);

    /** Deletes an object from the database by its primary key (ID). */
    boolean delete(int id);
}