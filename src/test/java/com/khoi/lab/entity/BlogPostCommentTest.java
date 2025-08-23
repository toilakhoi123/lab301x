package com.khoi.lab.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the BlogPostComment entity.
 */
@ExtendWith(MockitoExtension.class)
public class BlogPostCommentTest {

    @Mock
    private Account mockAccount;

    @Mock
    private BlogPost mockBlog;

    @InjectMocks
    private BlogPostComment blogPostComment;

    private static final String COMMENT_CONTENT = "This is a test comment.";

    @BeforeEach
    void setUp() {
        // Initialize the comment with mock objects before each test
        blogPostComment = new BlogPostComment(mockAccount, mockBlog, COMMENT_CONTENT);
    }

    @Test
    void testConstructorAndGetters() {
        // Verify that the constructor sets the initial values correctly
        assertEquals(mockAccount, blogPostComment.getAccount(),
                "The account should match the one passed to the constructor.");
        assertEquals(mockBlog, blogPostComment.getBlog(), "The blog should match the one passed to the constructor.");
        assertEquals(COMMENT_CONTENT, blogPostComment.getContent(), "The content should be set correctly.");
        assertNotNull(blogPostComment.getTime(), "The comment time should be set upon creation.");
    }

    @Test
    void testSetters() {
        // Create new mock objects for testing setters
        Account newMockAccount = new Account();
        BlogPost newMockBlog = new BlogPost();
        String newContent = "This is updated content.";
        LocalDateTime newTime = LocalDateTime.now().minusHours(1);

        // Test setters to ensure they update the state correctly
        Long newId = 1L;
        blogPostComment.setId(newId);
        assertEquals(newId, blogPostComment.getId(), "The ID should be updated.");

        blogPostComment.setAccount(newMockAccount);
        assertEquals(newMockAccount, blogPostComment.getAccount(), "The account should be updated.");

        blogPostComment.setBlog(newMockBlog);
        assertEquals(newMockBlog, blogPostComment.getBlog(), "The blog post should be updated.");

        blogPostComment.setContent(newContent);
        assertEquals(newContent, blogPostComment.getContent(), "The content should be updated.");

        blogPostComment.setTime(newTime);
        assertEquals(newTime, blogPostComment.getTime(), "The time should be updated.");
    }

    @Test
    void testToString() {
        // Mock the required methods for the toString()
        when(mockAccount.getFullName()).thenReturn("Test Account");
        when(mockBlog.getTitle()).thenReturn("Test Blog Post");

        // Set an ID to make the output predictable
        blogPostComment.setId(1L);

        // Define the expected output string
        String expectedString = "BlogPostComment [id=1, account=Test Account, blog=Test Blog Post, content="
                + COMMENT_CONTENT + "]";

        // Assert that the toString() method produces the expected string
        assertEquals(expectedString, blogPostComment.toString(),
                "The toString method should return the correct formatted string.");
    }
}
