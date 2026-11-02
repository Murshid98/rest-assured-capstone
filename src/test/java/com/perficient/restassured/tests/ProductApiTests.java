package com.perficient.restassured.tests;

import com.perficient.restassured.base.BaseTest;
import com.perficient.restassured.pojo.Post;
import com.perficient.restassured.utils.ApiClient;
import com.perficient.restassured.utils.Endpoints;
import com.perficient.restassured.utils.ResponseValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Assignment 4 - "Product" API automation (3 test cases).
 *
 * <p><b>Important mapping:</b> the GoRest application has no /products endpoint
 * (its endpoints are /users, /posts and /comments). A GoRest <i>post</i> is therefore
 * treated as the "product" of this assignment:</p>
 * <ul>
 *   <li>Product ID   = post id</li>
 *   <li>Product Name = post title</li>
 * </ul>
 */
public class ProductApiTests extends BaseTest {

    /** Data captured from the list call and reused by the next tests. */
    private static int productId;
    private static String productName;

    // ------------------------------------------------------------------
    // 1. Get product list
    // ------------------------------------------------------------------
    @Test(priority = 1, description = "GET /posts - returns the product (post) list")
    public void testGetProductList() {
        Response response = ApiClient.get(Endpoints.POSTS);

        ResponseValidator.assertStatusCode(response, 200);

        List<Integer> ids = response.jsonPath().getList("id");
        Assert.assertFalse(ids.isEmpty(), "Product list should not be empty");

        // Product ID and Product Name of the first entry must be present
        productId = response.jsonPath().getInt("[0].id");
        productName = response.jsonPath().getString("[0].title");
        Assert.assertTrue(productId > 0, "Product ID should be positive");
        Assert.assertFalse(productName == null || productName.isEmpty(), "Product name should not be empty");

        ResponseValidator.assertResponseTime(response);
    }

    // ------------------------------------------------------------------
    // 2. Get product details
    // ------------------------------------------------------------------
    @Test(priority = 2, dependsOnMethods = "testGetProductList",
            description = "GET /posts/{id} - returns the details of one product")
    public void testGetProductDetails() {
        Response response = ApiClient.get(Endpoints.POST_BY_ID, productId);

        ResponseValidator.assertStatusCode(response, 200);

        Post product = response.as(Post.class);
        Assert.assertEquals(product.getId().intValue(), productId, "Product ID mismatch");
        Assert.assertEquals(product.getTitle(), productName, "Product name mismatch");

        ResponseValidator.assertResponseTime(response);
    }

    // ------------------------------------------------------------------
    // 3. Search product
    // ------------------------------------------------------------------
    @Test(priority = 3, dependsOnMethods = "testGetProductList",
            description = "GET /posts?title=<name> - searches products by name")
    public void testSearchProduct() {
        // Search using the product name as the query parameter "title"
        Response response = ApiClient.getWithQuery(Endpoints.POSTS, Map.of("title", productName));

        ResponseValidator.assertStatusCode(response, 200);

        // The searched product must be in the result with the correct id and name
        List<Integer> ids = response.jsonPath().getList("id");
        Assert.assertFalse(ids.isEmpty(), "Search returned no products for: " + productName);
        Assert.assertTrue(ids.contains(productId),
                "Product id " + productId + " not found in search results " + ids);

        int index = ids.indexOf(productId);
        Assert.assertEquals(response.jsonPath().getString("[" + index + "].title"), productName,
                "Product name mismatch in search result");

        ResponseValidator.assertResponseTime(response);
    }
}
