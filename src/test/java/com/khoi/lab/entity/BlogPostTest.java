package com.khoi.lab.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the BlogPost entity.
 */
@ExtendWith(MockitoExtension.class)
public class BlogPostTest {
    @Mock
    private Account mockAuthor;

    @InjectMocks
    private BlogPost blogPost;

    @BeforeEach
    void setUp() {
        blogPost = new BlogPost(mockAuthor, "test-image-url.png", "Test Title", "Test Description");
    }

    @Test
    void testConstructorWithValidImageUrl() {
        // Test basic constructor and getter methods with a valid image URL.
        assertEquals(mockAuthor, blogPost.getAuthor());
        assertEquals("test-image-url.png", blogPost.getImageUrl());
        assertEquals("Test Title", blogPost.getTitle());
        assertEquals("Test Description", blogPost.getDescription());
        assertNotNull(blogPost.getDate(), "The date should be set upon creation.");
        assertNotNull(blogPost.getComments(), "Comments list should be initialized, not null.");
        assertTrue(blogPost.getComments().isEmpty(), "Comments list should be empty upon creation.");
    }

    @Test
    void testConstructorWithNullImageUrl() {
        // Test that a null image URL is handled gracefully by the constructor.
        BlogPost blogPostWithNullImage = new BlogPost(mockAuthor, null, "Null Image Test", "Description");
        assertEquals("https://media.sproutsocial.com/uploads/2019/09/how-to-write-a-blog-post.svg",
                blogPostWithNullImage.getImageUrl());
    }

    @Test
    void testConstructorWithEmptyImageUrl() {
        // Test that an empty image URL is handled gracefully by the constructor.
        BlogPost blogPostWithEmptyImage = new BlogPost(mockAuthor, "", "Empty Image Test", "Description");
        assertEquals("https://media.sproutsocial.com/uploads/2019/09/how-to-write-a-blog-post.svg",
                blogPostWithEmptyImage.getImageUrl());
    }

    @Test
    void testGetCommentsSortedByNewest() {
        // Prepare mock comments with different IDs.
        BlogPostComment comment1 = new BlogPostComment();
        comment1.setId(1L);
        BlogPostComment comment2 = new BlogPostComment();
        comment2.setId(3L); // This should be first in the sorted list.
        BlogPostComment comment3 = new BlogPostComment();
        comment3.setId(2L);

        // Set the comments list, out of order.
        blogPost.setComments(new ArrayList<>(Arrays.asList(comment1, comment2, comment3)));

        // Get the comments and verify they are sorted by ID descending.
        List<BlogPostComment> sortedComments = blogPost.getComments();

        // Use a stream to get the IDs in the correct order for comparison.
        List<Long> sortedIds = sortedComments.stream()
                .map(BlogPostComment::getId)
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(3L, 2L, 1L), sortedIds, "Comments should be sorted by ID in descending order.");
    }

    @Test
    void testGetCommentsCount() {
        // Initially, the count should be 0.
        assertEquals(0, blogPost.getCommentsCount());

        // Add some comments and verify the count updates.
        blogPost.setComments(new ArrayList<>(Arrays.asList(new BlogPostComment(), new BlogPostComment())));
        assertEquals(2, blogPost.getCommentsCount(), "Count should reflect the number of comments in the list.");

        // Set comments to null and verify count handles it gracefully.
        blogPost.setComments(null);
        assertEquals(0, blogPost.getCommentsCount(), "Count should be 0 if the comments list is null.");
    }

    @Test
    void testGetTimeAgo_JustNow() {
        // Set date to a few seconds in the past
        blogPost.setDate(LocalDateTime.now().minusSeconds(10));
        assertEquals("just now", blogPost.getTimeAgo());
    }

    @Test
    void testGetTimeAgo_MinutesAgo() {
        // Set date to a few minutes in the past
        blogPost.setDate(LocalDateTime.now().minusMinutes(5));
        assertEquals("5 minutes ago", blogPost.getTimeAgo());
    }

    @Test
    void testGetTimeAgo_HoursAgo() {
        // Set date to a few hours in the past
        blogPost.setDate(LocalDateTime.now().minusHours(3));
        assertEquals("3 hours ago", blogPost.getTimeAgo());
    }

    @Test
    void testGetTimeAgo_DaysAgo() {
        // Set date to a few days in the past
        blogPost.setDate(LocalDateTime.now().minusDays(2));
        assertEquals("2 days ago", blogPost.getTimeAgo());
    }

    @Test
    void testGetTimeAgo_OlderThanAWeek() {
        // Set date to more than 7 days ago
        LocalDateTime oldDate = LocalDateTime.now().minusDays(8);
        blogPost.setDate(oldDate);
        assertEquals(oldDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")),
                blogPost.getTimeAgo());
    }

    @Test
    void testToString() {
        // Verify the toString method returns a predictable string.
        blogPost.setId(1L);
        when(mockAuthor.toString()).thenReturn("Mock Author");
        String expectedString = "BlogPost [id=1, author=Mock Author, title=Test Title]";
        assertEquals(expectedString, blogPost.toString());
    }
}
