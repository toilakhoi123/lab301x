package com.khoi.lab.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "blog_post_comment")
public class BlogPostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "blog_post_id", nullable = false)
    private BlogPost blog;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "time")
    private LocalDateTime time;

    public BlogPostComment() {
    }

    public BlogPostComment(Account account, BlogPost blog, String content) {
        this.account = account;
        this.blog = blog;
        this.content = content;
        this.time = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BlogPost getBlog() {
        return blog;
    }

    public void setBlog(BlogPost blog) {
        this.blog = blog;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "BlogPostComment [id=" + id + ", account=" + account.getFullName() + ", blog=" + blog.getTitle()
                + ", content="
                + content + "]";
    }
}
