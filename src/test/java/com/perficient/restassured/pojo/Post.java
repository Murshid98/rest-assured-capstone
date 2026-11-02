package com.perficient.restassured.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** POJO for a GoRest post (title + body written by a user). */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    private Integer id;

    // JSON field is "user_id"; @JsonProperty maps it to this camelCase Java field
    @JsonProperty("user_id")
    private Integer userId;

    private String title;
    private String body;

    public Post() {
    }

    /** Convenience constructor for creating a post. */
    public Post(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
