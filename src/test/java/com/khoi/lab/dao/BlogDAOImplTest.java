package com.khoi.lab.dao;

import com.khoi.lab.entity.BlogPost;
import com.khoi.lab.entity.BlogPostComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BlogDAOImpl class using Mockito to mock the EntityManager.
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BlogDAOImplTest {

    // Inject the mocks into the DAO implementation
    @InjectMocks
    private BlogDAOImpl blogDAO;

    // Mock the EntityManager, which is a dependency of the DAO
    @Mock
    private EntityManager entityManager;

    // Mock entities and queries for use in the tests
    @Mock
    private BlogPost mockBlogPost;
    @Mock
    private BlogPostComment mockComment;
    @Mock
    private TypedQuery<BlogPost> mockTypedQuery;

    // --- Tests for BlogPost methods ---

    @Test
    void testSaveBlogPost() {
        // Arrange
        BlogPost newBlogPost = new BlogPost();

        // Act
        BlogPost savedBlogPost = blogDAO.saveBlogPost(newBlogPost);

        // Assert
        // Verify that EntityManager's persist method was called exactly once with the
        // new blog post
        verify(entityManager, times(1)).persist(newBlogPost);
        // The method should return the same object passed to it
        assertEquals(newBlogPost, savedBlogPost);
    }

    @Test
    void testFindBlogPostById_Found() {
        // Arrange
        Long id = 1L;
        // Mock the find() method to return a valid object when called with the
        // specified ID
        when(entityManager.find(BlogPost.class, id)).thenReturn(mockBlogPost);

        // Act
        BlogPost foundBlogPost = blogDAO.findBlogPostById(id);

        // Assert
        // The returned object should be the mock object we defined
        assertNotNull(foundBlogPost);
        assertEquals(mockBlogPost, foundBlogPost);
        // Verify that the find method was called once with the correct parameters
        verify(entityManager, times(1)).find(BlogPost.class, id);
    }

    @Test
    void testListBlogPosts() {
        // Arrange
        List<BlogPost> blogPosts = List.of(mockBlogPost, mockBlogPost);
        // Mock the behavior of the TypedQuery to return our list of mock blog posts
        when(entityManager.createQuery("SELECT c FROM BlogPost c", BlogPost.class)).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(blogPosts);

        // Act
        List<BlogPost> resultList = blogDAO.listBlogPosts();

        // Assert
        // Check that the returned list is not null and has the expected size
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        // Verify that the query was created and the getResultList method was called
        verify(entityManager, times(1)).createQuery("SELECT c FROM BlogPost c", BlogPost.class);
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    void testUpdateBlogPost() {
        // Arrange
        BlogPost updatedBlogPost = new BlogPost();
        // Mock the merge() method to return the merged object
        when(entityManager.merge(updatedBlogPost)).thenReturn(updatedBlogPost);

        // Act
        BlogPost result = blogDAO.updateBlogPost(updatedBlogPost);

        // Assert
        // Verify that the merge method was called once
        verify(entityManager, times(1)).merge(updatedBlogPost);
        // The returned object should be the merged object
        assertEquals(updatedBlogPost, result);
    }

    @Test
    void testDeleteBlogPostById() {
        // Arrange
        Long id = 1L;
        // Mock the find() method to return an object to be deleted
        when(entityManager.find(BlogPost.class, id)).thenReturn(mockBlogPost);

        // Act
        blogDAO.deleteBlogPostById(id);

        // Assert
        // Verify that the find and remove methods were called exactly once
        verify(entityManager, times(1)).find(BlogPost.class, id);
        verify(entityManager, times(1)).remove(mockBlogPost);
    }

    // --- Tests for BlogPostComment methods ---

    @Test
    void testFindBlogPostCommentById_Found() {
        // Arrange
        Long id = 1L;
        when(entityManager.find(BlogPostComment.class, id)).thenReturn(mockComment);

        // Act
        BlogPostComment foundComment = blogDAO.findBlogPostCommentById(id);

        // Assert
        assertNotNull(foundComment);
        assertEquals(mockComment, foundComment);
        verify(entityManager, times(1)).find(BlogPostComment.class, id);
    }

    @Test
    void testDeleteBlogPostCommentById() {
        // Arrange
        Long id = 1L;
        when(entityManager.find(BlogPostComment.class, id)).thenReturn(mockComment);

        // Act
        blogDAO.deleteBlogPostCommentById(id);

        // Assert
        verify(entityManager, times(1)).find(BlogPostComment.class, id);
        verify(entityManager, times(1)).remove(mockComment);
    }
}
