package com.perficient.restassured.utils;

import com.perficient.restassured.pojo.Comment;
import com.perficient.restassured.pojo.Post;
import com.perficient.restassured.pojo.User;

import java.util.UUID;

/**
 * Test data management utility.
 * Generates unique data for every run. This matters because GoRest rejects a
 * user whose e-mail already exists, so hard-coded data would fail on the 2nd run.
 */
public final class TestDataGenerator {

    private static final String[] GENDERS = {"male", "female"};

    private TestDataGenerator() {
    }

    /** @return a short random string such as "a1b2c3d4". */
    private static String uniqueId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /** @return a new, valid and unique user (always created as "active"). */
    public static User randomUser() {
        String id = uniqueId();
        String gender = GENDERS[(int) (Math.random() * GENDERS.length)];
        return new User("QA User " + id, "qa.user." + id + "@example.com", gender, "active");
    }

    /** @return a new post with a unique title and some body text. */
    public static Post randomPost() {
        String id = uniqueId();
        return new Post("Automation Post " + id,
                "This post was created by the Rest Assured capstone framework. Ref " + id);
    }

    /** @return a new comment with a unique e-mail and some body text. */
    public static Comment randomComment() {
        String id = uniqueId();
        return new Comment("Commenter " + id, "commenter." + id + "@example.com",
                "Automated comment " + id);
    }
}
