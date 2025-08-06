package com.khoi.lab.entity;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "blog_post")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "author_account_id", nullable = false)
    private Account author;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date")
    private Date date;

    @OneToMany(mappedBy = "blog", cascade = { CascadeType.ALL })
    private List<BlogPostComment> comments;

    @SuppressWarnings("unused")
    private String timeAgo;

    public BlogPost() {
    }

    public BlogPost(Account author, String imageUrl, String title, String description) {
        this.author = author;
        this.imageUrl = imageUrl == ""
                ? "https://media.sproutsocial.com/uploads/2019/09/how-to-write-a-blog-post.svg"
                : imageUrl;
        this.title = title;
        this.description = description;
        this.comments = new ArrayList<>();
        this.date = new java.sql.Date(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAuthor() {
        return author;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<BlogPostComment> getComments() {
        return comments;
    }

    public void setComments(List<BlogPostComment> comments) {
        this.comments = comments;
    }

    public int getCommentsCount() {
        return this.comments.size();
    }

    public String getTimeAgo() {
        if (this.date == null) {
            return ""; // Or handle as an error, e.g., "Invalid Date"
        }

        // Convert java.sql.Date to java.util.Date, then to Instant, then to
        // LocalDateTime
        LocalDateTime postDateTime = new java.util.Date(this.date.getTime())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        Duration duration = Duration.between(postDateTime, LocalDateTime.now());

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) {
            return "just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (days < 7) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else {
            // For dates older than a week, display the full date
            return postDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    @Override
    public String toString() {
        return "BlogPost [id=" + id + ", author=" + author + ", title=" + title + "]";
    }
}
