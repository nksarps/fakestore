package com.automation.api.utils;

/**
 * Centralized endpoint paths and route builders for Fake Store API resources.
 */
public final class Endpoints {

    public static final String PRODUCTS = "/products";
    public static final String CARTS = "/carts";
    public static final String USERS = "/users";
    public static final String AUTH = "/auth";
    public static final String AUTH_LOGIN = AUTH + "/login";

    /**
     * @param id product identifier
     * @return product endpoint by id
     */
    public static String productById(int id) {
        return PRODUCTS + "/" + id;
    }

    /**
     * @param limit list size limit
     * @return products endpoint with limit query
     */
    public static String productsWithLimit(int limit) {
        return PRODUCTS + "?limit=" + limit;
    }

    /**
     * @param sort sort direction (asc or desc)
     * @return products endpoint with sort query
     */
    public static String productsWithSort(String sort) {
        return PRODUCTS + "?sort=" + sort;
    }

    /**
     * @return products categories endpoint
     */
    public static String productCategories() {
        return PRODUCTS + "/categories";
    }

    /**
     * @param id cart identifier
     * @return cart endpoint by id
     */
    public static String cartById(int id) {
        return CARTS + "/" + id;
    }

    /**
     * @param id user identifier
     * @return user endpoint by id
     */
    public static String userById(int id) {
        return USERS + "/" + id;
    }
}
