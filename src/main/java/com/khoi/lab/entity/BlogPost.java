package com.khoi.lab.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    // Changed from java.sql.Date to java.time.LocalDateTime
    @Column(name = "date")
    private LocalDateTime date;

    @OneToMany(mappedBy = "blog", cascade = { CascadeType.ALL })
    private List<BlogPostComment> comments;

    // Removed the 'timeAgo' field as it's now a calculated getter

    public BlogPost() {
    }

    public BlogPost(Account author, String imageUrl, String title, String description) {
        this.author = author;
        this.imageUrl = imageUrl == null || imageUrl.isEmpty() // Handle null or empty string for default image
                ? "https://media.sproutsocial.com/uploads/2019/09/how-to-write-a-blog-post.svg"
                : imageUrl;
        this.title = title;
        this.description = description;
        this.comments = new ArrayList<>();
        // Set date to current LocalDateTime when creating a new post
        this.date = LocalDateTime.now();
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

    // Changed return type and parameter type to LocalDateTime
    public LocalDateTime getDate() {
        return date;
    }

    // Changed parameter type to LocalDateTime
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<BlogPostComment> getComments() {
        return comments.stream()
                .sorted(Comparator.comparing(BlogPostComment::getId).reversed())
                .collect(Collectors.toList());
    }

    public void setComments(List<BlogPostComment> comments) {
        this.comments = comments;
    }

    public int getCommentsCount() {
        // Ensure comments list is not null before accessing size
        return (this.comments != null) ? this.comments.size() : 0;
    }

    /**
     * Calculates a human-readable "time ago" string for this blog post's date.
     * This method now directly uses LocalDateTime, simplifying the conversion.
     *
     * @return A string indicating how long ago the date was (e.g., "5 minutes ago",
     *         "2 days ago").
     *         Returns "just now" if the date is very recent.
     *         Returns the formatted date "dd MMM yyyy" if the date is older than 7
     *         days.
     */
    public String getTimeAgo() {
        if (this.date == null) {
            return ""; // Handle case where date might be null
        }

        // Directly use LocalDateTime for duration calculation
        Duration duration = Duration.between(this.date, LocalDateTime.now());

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
            return this.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }
    }

    // Removed setTimeAgo(String timeAgo) as getTimeAgo is now a calculated property

    @Override
    public String toString() {
        return "BlogPost [id=" + id + ", author=" + author + ", title=" + title + "]";
    }
}