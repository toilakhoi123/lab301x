package com.khoi.lab.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.BlogPost;
import com.khoi.lab.entity.BlogPostComment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * Implementation of BlogDAO
 */
@Repository
public class BlogDAOImpl implements BlogDAO {
    private EntityManager em;

    public BlogDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void initiate() {
        Account account1 = em.find(Account.class, 1);
        Account account2 = em.find(Account.class, 2);
        Account account3 = em.find(Account.class, 3);

        BlogPost blog1 = createBlogPost(account1,
                "https://images.squarespace-cdn.com/content/v1/66638306647eed2efff9e8c8/9b72af69-7bac-41ba-8bac-c9e9b991c119/Johns_Louise_PeetsHill_LR-30_web_2500w.jpg",
                "First Blog Post Ever", "Let's Celebrate");
        BlogPost blog2 = createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Second Blog!!", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupe", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupe!", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupe!!", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupe!!!", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupe!!!!", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupe fafdafsd", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dup afafae", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe duaffafpe", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe aaadupe", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe adupe dffffupe", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe aadupe dupe", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupeff dupe dupe", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupeuyeyey", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupeuyeyey", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupeuyeyey", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupeuyeyey", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupeuyeyey", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupeuyeyey", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupeuyeyey", "Easy work");
        createBlogPost(account2, "https://goodwillsv.org/wp-content/uploads/2021/04/donation.jpg",
                "Dupe dupe dupeuyeyey", "Easy work");

        BlogPostComment cmt1 = createBlogPostComment(blog1, account2, "Yo this is sick wth");
        BlogPostComment cmt2 = createBlogPostComment(blog1, account2, "Actually goated");
        BlogPostComment cmt3 = createBlogPostComment(blog1, account3, "W");
        BlogPostComment cmt4 = createBlogPostComment(blog1, account1, "How's my post guys");
        BlogPostComment cmt5 = createBlogPostComment(blog2, account2, "Very pro");
        BlogPostComment cmt6 = createBlogPostComment(blog2, account3, "So nub");
    }

    @Override
    @Transactional
    public BlogPost saveBlogPost(BlogPost blogPost) {
        em.persist(blogPost);
        System.out.println("| [saveBlogPost] Saved blog post: " + blogPost);
        return blogPost;
    }

    @Override
    public BlogPost findBlogPostById(Long id) {
        BlogPost blogPost = em.find(BlogPost.class, id);
        if (blogPost == null) {
            System.out.println("| [findBlogPostById] Couldn't find blog post with id: " + id);
        } else {
            System.out.println("| [findBlogPostById] Found blog post: " + blogPost);
        }
        return blogPost;
    }

    @Override
    public List<BlogPost> listBlogPosts() {
        TypedQuery<BlogPost> tq = em.createQuery(
                "SELECT c FROM BlogPost c",
                BlogPost.class);
        List<BlogPost> blogPosts = tq.getResultList();
        System.out.println("| [listBlogPosts] Found and returned: " + blogPosts.size() + " blogPosts!");
        return blogPosts;
    }

    @Override
    @Transactional
    public BlogPost updateBlogPost(BlogPost blogPost) {
        blogPost = em.merge(blogPost);
        System.out.println("| [updateBlogPost] Updated blogPost: " + blogPost);
        return blogPost;
    }

    @Override
    @Transactional
    public void deleteBlogPostById(Long id) {
        BlogPost blogPost = findBlogPostById(id);
        em.remove(blogPost);
        System.out.println("| [deleteBlogPostById] Deleted blogPost with id: " + id);
    }

    @Override
    @Transactional
    public BlogPost createBlogPost(Account author, String imageUrl, String title, String description) {
        // TODO: Check perms
        BlogPost blogPost = new BlogPost(author, imageUrl, title, description);
        blogPost = saveBlogPost(blogPost);

        author.getBlogPosts().add(blogPost);
        em.merge(author);

        System.out.println("| [createBlogPost] blogPost created: " + blogPost);

        return blogPost;
    }

    @Override
    @Transactional
    public BlogPostComment saveBlogPostComment(BlogPostComment blogPostComment) {
        em.persist(blogPostComment);
        System.out.println("| [saveBlogPostComment] Saved blogPostComment: " + blogPostComment);
        return blogPostComment;
    }

    @Override
    public BlogPostComment findBlogPostCommentById(Long id) {
        BlogPostComment blogPostComment = em.find(BlogPostComment.class, id);
        if (blogPostComment == null) {
            System.out.println("| [findBlogPostCommentById] Couldn't find blogPostComment with id: " + id);
        } else {
            System.out.println("| [findBlogPostCommentById] Found blogPostComment: " + blogPostComment);
        }
        return blogPostComment;
    }

    @Override
    @Transactional
    public BlogPostComment updateBlogPostComment(BlogPostComment blogPostComment) {
        blogPostComment = em.merge(blogPostComment);
        System.out.println("| [updateBlogPostComment] Updated blogPostComment: " + blogPostComment);
        return blogPostComment;
    }

    @Override
    @Transactional
    public void deleteBlogPostCommentById(Long id) {
        BlogPostComment blogPostComment = findBlogPostCommentById(id);
        em.remove(blogPostComment);
        System.out.println("| [deleteBlogPostCommentById] Deleted blogPostComment with id: " + id);
    }

    @Override
    @Transactional
    public BlogPostComment createBlogPostComment(BlogPost blogPost, Account account, String content) {
        BlogPostComment blogPostComment = new BlogPostComment(account, blogPost, content);
        saveBlogPostComment(blogPostComment);

        account.getBlogPostComments().add(blogPostComment);
        em.merge(account);
        blogPost.getComments().add(blogPostComment);
        em.merge(blogPost);

        System.out.println("| [blogPostComment] blogPost created: " + blogPost);

        return blogPostComment;
    }
}
