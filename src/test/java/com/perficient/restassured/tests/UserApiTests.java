package com.perficient.restassured.tests;

import com.perficient.restassured.base.BaseTest;
import com.perficient.restassured.pojo.User;
import com.perficient.restassured.utils.ApiClient;
import com.perficient.restassured.utils.Endpoints;
import com.perficient.restassured.utils.ResponseValidator;
import com.perficient.restassured.utils.TestDataGenerator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Assignment 3 - User API automation (5 test cases).
 *
 * <p>Validations in each test: status code, user details, response time.</p>
 *
 * <p>Execution flow: tests run in the order given by "priority". The last three
 * tests form a chain (create -> update -> delete) on the same user, so they use
 * "dependsOnMethods": if one step fails, the following steps are SKIPPED instead
 * of failing for a misleading reason.</p>
 */
public class UserApiTests extends BaseTest {

    /** An existing user picked from the list (used by "get user by id"). */
    private static int existingUserId;

    /** The user created by testCreateUser; reused by update and delete. */
    private static int createdUserId;
    private static User createdUser;

    // ------------------------------------------------------------------
    // 1. GET all users
    // ------------------------------------------------------------------
    @Test(priority = 1, description = "GET /users - returns a non-empty list of users")
    public void testGetAllUsers() {
        Response response = ApiClient.get(Endpoints.USERS);

        // Validation 1: status code
        ResponseValidator.assertStatusCode(response, 200);

        // Validation 2: user details - list is not empty and each entry has the main fields
        JsonPath json = response.jsonPath();
        List<Integer> ids = json.getList("id");
        Assert.assertFalse(ids.isEmpty(), "User list should not be empty");
        Assert.assertNotNull(json.getString("[0].name"), "First user should have a name");
        Assert.assertNotNull(json.getString("[0].email"), "First user should have an email");

        // Validation 3: response time
        ResponseValidator.assertResponseTime(response);

        // Remember the first user so the next test can fetch it by id
        existingUserId = ids.get(0);
    }

    // ------------------------------------------------------------------
    // 2. GET user by id
    // ------------------------------------------------------------------
    @Test(priority = 2, dependsOnMethods = "testGetAllUsers",
            description = "GET /users/{id} - returns the details of a single user")
    public void testGetUserById() {
        Response response = ApiClient.get(Endpoints.USER_BY_ID, existingUserId);

        ResponseValidator.assertStatusCode(response, 200);

        // Convert the JSON response into a User object (POJO) and verify the details
        User user = response.as(User.class);
        Assert.assertEquals(user.getId().intValue(), existingUserId, "User id mismatch");
        Assert.assertFalse(user.getName().isEmpty(), "Name should not be empty");
        Assert.assertTrue(user.getEmail().contains("@"), "Email should be valid: " + user.getEmail());
        Assert.assertTrue(Arrays.asList("male", "female").contains(user.getGender()),
                "Unexpected gender: " + user.getGender());
        Assert.assertTrue(Arrays.asList("active", "inactive").contains(user.getStatus()),
                "Unexpected status: " + user.getStatus());

        ResponseValidator.assertResponseTime(response);
    }

    // ------------------------------------------------------------------
    // 3. POST - create user
    // ------------------------------------------------------------------
    @Test(priority = 3, description = "POST /users - creates a new user")
    public void testCreateUser() {
        // Request body is built from the User POJO with unique random data
        User requestUser = TestDataGenerator.randomUser();

        Response response = ApiClient.post(Endpoints.USERS, requestUser);

        // 201 = Created
        ResponseValidator.assertStatusCode(response, 201);

        // The server must echo back what we sent, plus a generated id
        User responseUser = response.as(User.class);
        Assert.assertNotNull(responseUser.getId(), "Server should return a generated id");
        Assert.assertEquals(responseUser.getName(), requestUser.getName(), "Name mismatch");
        Assert.assertEquals(responseUser.getEmail(), requestUser.getEmail(), "Email mismatch");
        Assert.assertEquals(responseUser.getGender(), requestUser.getGender(), "Gender mismatch");
        Assert.assertEquals(responseUser.getStatus(), requestUser.getStatus(), "Status mismatch");

        ResponseValidator.assertResponseTime(response);

        // Store for the update and delete tests
        createdUser = responseUser;
        createdUserId = responseUser.getId();
    }

    // ------------------------------------------------------------------
    // 4. PUT - update user
    // ------------------------------------------------------------------
    @Test(priority = 4, dependsOnMethods = "testCreateUser",
            description = "PUT /users/{id} - updates the user created earlier")
    public void testUpdateUser() {
        // Change two fields; keep the others the same
        User updateRequest = new User(
                createdUser.getName() + " Updated",
                createdUser.getEmail(),
                createdUser.getGender(),
                "inactive");

        Response response = ApiClient.put(Endpoints.USER_BY_ID, updateRequest, createdUserId);

        ResponseValidator.assertStatusCode(response, 200);

        User updated = response.as(User.class);
        Assert.assertEquals(updated.getId().intValue(), createdUserId, "Id must not change");
        Assert.assertEquals(updated.getName(), updateRequest.getName(), "Name was not updated");
        Assert.assertEquals(updated.getStatus(), "inactive", "Status was not updated");

        ResponseValidator.assertResponseTime(response);
    }

    // ------------------------------------------------------------------
    // 5. DELETE - delete user
    // ------------------------------------------------------------------
    @Test(priority = 5, dependsOnMethods = "testUpdateUser",
            description = "DELETE /users/{id} - deletes the user and confirms it is gone")
    public void testDeleteUser() {
        Response deleteResponse = ApiClient.delete(Endpoints.USER_BY_ID, createdUserId);

        // 204 = No Content (deleted successfully, nothing returned)
        ResponseValidator.assertStatusCode(deleteResponse, 204);
        ResponseValidator.assertResponseTime(deleteResponse);

        // Confirm deletion: asking for the same user again must return 404
        Response getResponse = ApiClient.get(Endpoints.USER_BY_ID, createdUserId);
        ResponseValidator.assertStatusCode(getResponse, 404);
        Assert.assertEquals(getResponse.jsonPath().getString("message"), "Resource not found",
                "Unexpected error message for a deleted user");
    }
}
