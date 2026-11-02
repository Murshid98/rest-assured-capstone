package com.perficient.restassured.utils;

/**
 * Central place for all API resource paths.
 * If an endpoint changes, it only needs to be updated here.
 * Paths are relative to baseUrl + basePath (https://gorest.co.in/public/v2).
 * Placeholders like {id} are filled in by Rest Assured.
 */
public final class Endpoints {

    private Endpoints() {
    }

    // ---- Users ----
    public static final String USERS = "/users";
    public static final String USER_BY_ID = "/users/{id}";
    public static final String USER_POSTS = "/users/{id}/posts";   // create a post for a user

    // ---- Posts (used as the "Product" APIs, see README) ----
    public static final String POSTS = "/posts";
    public static final String POST_BY_ID = "/posts/{id}";

    // ---- Comments ----
    public static final String POST_COMMENTS = "/posts/{id}/comments";
}
