package com.khoi.lab.dao;

import java.util.List;

import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.BlogPost;
import com.khoi.lab.entity.BlogPostComment;

/**
 * Blog DAO
 */
public interface BlogDAO {
    /**
     * Initiate test data
     */
    void initiate();

    /**
     * Save blog post
     * 
     * @param blogPost
     * @return
     */
    BlogPost saveBlogPost(BlogPost blogPost);

    /**
     * Find blog post by ID
     * 
     * @param id
     * @return
     */
    BlogPost findBlogPostById(Long id);

    /**
     * List all blog posts available
     * 
     * @return
     */
    List<BlogPost> listBlogPosts();

    /**
     * Update blog post
     * 
     * @param blogPost
     * @return
     */
    BlogPost updateBlogPost(BlogPost blogPost);

    /**
     * Delete blog post by ID
     * 
     * @param id
     */
    void deleteBlogPostById(Long id);

    /**
     * Create blog post by ID
     * 
     * @param author
     * @param imageUrl
     * @param title
     * @param description
     * @return
     */
    BlogPost createBlogPost(Account author, String imageUrl, String title, String description);

    /**
     * Save blog comment
     * 
     * @param blogPostComment
     * @return
     */
    BlogPostComment saveBlogPostComment(BlogPostComment blogPostComment);

    /**
     * Find blog comment by ID
     * 
     * @param id
     * @return
     */
    BlogPostComment findBlogPostCommentById(Long id);

    /**
     * Update blog comment
     * 
     * @param blogPostComment
     * @return
     */
    BlogPostComment updateBlogPostComment(BlogPostComment blogPostComment);

    /**
     * Delete blog post comment by ID
     * 
     * @param id
     */
    void deleteBlogPostCommentById(Long id);

    /**
     * Make a comment by account on a blog post
     * 
     * @param blogPost
     * @param account
     * @param content
     * @return
     */
    BlogPostComment createBlogPostComment(BlogPost blogPost, Account account, String content);
}
