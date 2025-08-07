package com.khoi.lab.controller;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.BlogDAO;
import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.BlogPost;
import com.khoi.lab.service.PaginationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for blog mappings
 */
@Controller
@RequestMapping("/blogs")
public class BlogController {
    @SuppressWarnings("unused")
    private final DonationDAO donationDAO;
    @SuppressWarnings("unused")
    private final AccountDAO accountDAO;
    private final BlogDAO blogDAO;

    private static final Map<String, String> FILTER_NAMES = Map.of(
            "newest", "Newest First",
            "oldest", "Oldest First",
            "popular", "Most Popular First",
            "week", "This Week",
            "month", "This Month",
            "year", "This Year");

    /**
     * DAO Initiator
     * * @param donationDAO
     */
    public BlogController(DonationDAO donationDAO, AccountDAO accountDAO, BlogDAO blogDAO) {
        this.donationDAO = donationDAO;
        this.accountDAO = accountDAO;
        this.blogDAO = blogDAO;
    }

    /**
     * View a page of blog list
     * * @param page
     * 
     * @return
     */
    @GetMapping("")
    public ModelAndView blogListPage(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String filter) {
        if (page == null || page < 1)
            page = 1;
        if (pageSize == null || pageSize < 1)
            pageSize = 3;

        // 1. Get all blog posts from the data source.
        List<BlogPost> blogPosts = blogDAO.listBlogPosts();

        // 2. Apply category filter if 'query' is present.
        if (query != null && !query.trim().isEmpty()) {
            blogPosts = blogPosts.stream()
                    .filter(post -> post.getTitle().contains(query) || post.getDescription().contains(query))
                    .collect(Collectors.toList());
        }

        // 3. Apply sorting or time-based filtering if 'filter' is present.
        if (filter != null && !filter.trim().isEmpty()) {
            LocalDate now = LocalDate.now();
            switch (filter.toLowerCase()) {
                case "newest":
                    blogPosts.sort(Comparator.comparing(BlogPost::getDate).reversed());
                    break;
                case "oldest":
                    blogPosts.sort(Comparator.comparing(BlogPost::getDate));
                    break;
                case "popular":
                    blogPosts.sort(Comparator.comparingInt(BlogPost::getCommentsCount).reversed());
                    break;
                case "week":
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
                    int currentYearForWeek = now.get(weekFields.weekBasedYear());
                    blogPosts = blogPosts.stream()
                            .filter(post -> {
                                // Now post.getDate() returns LocalDateTime directly
                                LocalDate postDate = post.getDate().toLocalDate();
                                return postDate.get(weekFields.weekOfWeekBasedYear()) == currentWeek &&
                                        postDate.get(weekFields.weekBasedYear()) == currentYearForWeek;
                            })
                            .collect(Collectors.toList());
                    break;
                case "month":
                    blogPosts = blogPosts.stream()
                            .filter(post -> {
                                // Now post.getDate() returns LocalDateTime directly
                                LocalDate postDate = post.getDate().toLocalDate();
                                return postDate.getMonth() == now.getMonth() &&
                                        postDate.getYear() == now.getYear();
                            })
                            .collect(Collectors.toList());
                    break;
                case "year":
                    blogPosts = blogPosts.stream()
                            .filter(post -> {
                                // Now post.getDate() returns LocalDateTime directly
                                LocalDate postDate = post.getDate().toLocalDate();
                                return postDate.getYear() == now.getYear();
                            })
                            .collect(Collectors.toList());
                    break;
                default:
                    // No action if filter is unknown
                    break;
            }
        }

        // 4. Paginate the (now filtered and sorted) results.
        int totalItems = blogPosts.size();
        int maxPage = (totalItems == 0) ? 1 : (int) Math.ceil((double) totalItems / pageSize);
        List<BlogPost> paginatedBlogPosts = PaginationService.getPage(blogPosts, page, pageSize);
        List<BlogPost> recentBlogPosts = blogDAO.listBlogPosts().stream().limit(10).collect(Collectors.toList());

        // 5. Build and return the view.
        System.out.println("| [blogListPage] Displaying page " + page + " of " + maxPage + " pages!");
        ModelAndView mav = new ModelAndView("blogs");
        mav.addObject("blogPosts", paginatedBlogPosts);
        mav.addObject("currentPage", page);
        mav.addObject("maxPage", maxPage);
        mav.addObject("currentQuery", query);
        mav.addObject("currentFilter", filter);
        mav.addObject("recentPosts", recentBlogPosts);

        // Add null check before accessing the map
        String currentFilterDisplay = (filter != null) ? FILTER_NAMES.get(filter.toLowerCase()) : null;
        mav.addObject("currentFilterDisplay", currentFilterDisplay);
        return mav;
    }

    /**
     * View details of a blog
     * (blogPostNotExist)
     * * @param id
     * 
     * @return
     */
    @GetMapping("/blog")
    public ModelAndView blogViewDetail(@RequestParam Long id) {
        BlogPost blogPost = blogDAO.findBlogPostById(id);

        if (blogPost == null) {
            ModelAndView mav = new ModelAndView("blog-list");
            mav.addObject("blogPostNotExist", true);
            return mav;
        }

        // Fetch ALL blog posts, sorted by a consistent criteria (e.g., date descending
        // or id ascending)
        // This is crucial to ensure consistent "next" and "previous" posts.
        // It's assumed blogDAO.listBlogPosts() returns a sorted list.
        List<BlogPost> allBlogPosts = blogDAO.listBlogPosts();

        // Find the index of the current blog post
        int currentIndex = -1;
        for (int i = 0; i < allBlogPosts.size(); i++) {
            if (allBlogPosts.get(i).getId().equals(id)) {
                currentIndex = i;
                break;
            }
        }

        BlogPost previousPost = null;
        // The fix: Add this check to prevent IndexOutOfBoundsException
        if (currentIndex > 0) {
            previousPost = allBlogPosts.get(currentIndex - 1);
        }

        BlogPost nextPost = null;
        // Check if a next post exists (i.e., we are not at the last post)
        if (currentIndex >= 0 && currentIndex < allBlogPosts.size() - 1) {
            nextPost = allBlogPosts.get(currentIndex + 1);
        }

        // Get recent posts as you were before
        List<BlogPost> recentBlogPosts = allBlogPosts.stream().limit(10).collect(Collectors.toList());

        ModelAndView mav = new ModelAndView("blog-details");
        mav.addObject("blogPost", blogPost);
        mav.addObject("recentPosts", recentBlogPosts);
        mav.addObject("previousPost", previousPost); // Add previousPost to the model
        mav.addObject("nextPost", nextPost); // Add nextPost to the model
        return mav;
    }

    @PostMapping("/comment")
    public String postMethodName(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }
}
