package com.perficient.restassured.tests;

import com.perficient.restassured.base.BaseTest;
import com.perficient.restassured.pojo.Comment;
import com.perficient.restassured.pojo.Post;
import com.perficient.restassured.utils.ApiClient;
import com.perficient.restassured.utils.Endpoints;
import com.perficient.restassured.utils.ResponseValidator;
import com.perficient.restassured.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Post APIs from the "Application and Its Endpoints" section:
 * "Create a post for user" and "Comments on a post".
 *
 * <p>A post must belong to a user, so this class creates its own temporary user
 * before the tests (@BeforeClass) and deletes it afterwards (@AfterClass).
 * The tests therefore never depend on, or damage, other data.</p>
 */
public class PostApiTests extends BaseTest {

    private int userId;      // owner of the post
    private int postId;      // post created by the first test

    // ------------------------------------------------------------------
    // Set-up / clean-up
    // ------------------------------------------------------------------

    /** Runs once before the tests of this class: creates the user that will own the post. */
    @BeforeClass
    public void createUserForPosts() {
        Response response = ApiClient.post(Endpoints.USERS, TestDataGenerator.randomUser());
        ResponseValidator.assertStatusCode(response, 201);
        userId = response.jsonPath().getInt("id");
    }

    /** Runs once after the tests, even if they failed: removes the temporary user (and its posts). */
    @AfterClass(alwaysRun = true)
    public void deleteTemporaryUser() {
        if (userId != 0) {
            ApiClient.delete(Endpoints.USER_BY_ID, userId);
        }
    }

    // ------------------------------------------------------------------
    // 1. Create a post for a user
    // ------------------------------------------------------------------
    @Test(priority = 1, description = "POST /users/{id}/posts - creates a post for a user")
    public void testCreatePostForUser() {
        Post requestPost = TestDataGenerator.randomPost();

        Response response = ApiClient.post(Endpoints.USER_POSTS, requestPost, userId);

        ResponseValidator.assertStatusCode(response, 201);

        Post created = response.as(Post.class);
        Assert.assertNotNull(created.getId(), "Server should return a post id");
        Assert.assertEquals(created.getUserId().intValue(), userId, "Post belongs to the wrong user");
        Assert.assertEquals(created.getTitle(), requestPost.getTitle(), "Title mismatch");
        Assert.assertEquals(created.getBody(), requestPost.getBody(), "Body mismatch");

        ResponseValidator.assertResponseTime(response);

        postId = created.getId();
    }

    // ------------------------------------------------------------------
    // 2. Comments on a post
    // ------------------------------------------------------------------
    @Test(priority = 2, dependsOnMethods = "testCreatePostForUser",
            description = "POST/GET /posts/{id}/comments - adds a comment and reads it back")
    public void testCommentOnPost() {
        Comment requestComment = TestDataGenerator.randomComment();

        // Step 1: add the comment
        Response createResponse = ApiClient.post(Endpoints.POST_COMMENTS, requestComment, postId);
        ResponseValidator.assertStatusCode(createResponse, 201);

        Comment created = createResponse.as(Comment.class);
        Assert.assertNotNull(created.getId(), "Server should return a comment id");
        Assert.assertEquals(created.getPostId().intValue(), postId, "Comment belongs to the wrong post");
        Assert.assertEquals(created.getName(), requestComment.getName(), "Name mismatch");
        Assert.assertEquals(created.getEmail(), requestComment.getEmail(), "Email mismatch");
        Assert.assertEquals(created.getBody(), requestComment.getBody(), "Body mismatch");
        ResponseValidator.assertResponseTime(createResponse);

        // Step 2: read all comments of the post and confirm ours is listed
        Response listResponse = ApiClient.get(Endpoints.POST_COMMENTS, postId);
        ResponseValidator.assertStatusCode(listResponse, 200);
        List<Integer> commentIds = listResponse.jsonPath().getList("id");
        Assert.assertTrue(commentIds.contains(created.getId()),
                "New comment " + created.getId() + " not found in " + commentIds);
    }
}
