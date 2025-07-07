package com.library.interfaces;

/**
 * Interface defining search behavior for library entities
 */
public interface Searchable {
    /**
     * Determines if the entity matches the given search term
     * @param searchTerm the term to search for
     * @return true if the entity matches the search term
     */
    boolean matches(String searchTerm);
}
