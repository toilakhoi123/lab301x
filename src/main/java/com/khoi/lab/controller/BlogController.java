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
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.BlogPost;
import com.khoi.lab.entity.BlogPostComment;
import com.khoi.lab.enums.UserPermission;
import com.khoi.lab.service.PaginationService;
import com.khoi.lab.service.UserPermissionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for blog mappings
 */
@Controller
@RequestMapping("/blogs")
public class BlogController {
    private final DonationDAO donationDAO;
    private final AccountDAO accountDAO;
    private final BlogDAO blogDAO;
    private final UserPermissionService userPermissionService;

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
    public BlogController(DonationDAO donationDAO, AccountDAO accountDAO, BlogDAO blogDAO,
            UserPermissionService userPermissionService) {
        this.donationDAO = donationDAO;
        this.accountDAO = accountDAO;
        this.blogDAO = blogDAO;
        this.userPermissionService = userPermissionService;
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
        List<BlogPost> allBlogPosts = blogDAO.listBlogPosts().stream()
                .sorted(Comparator.comparing(BlogPost::getId).reversed())
                .collect(Collectors.toList());
        List<BlogPost> blogPosts = allBlogPosts;

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
        List<BlogPost> recentBlogPosts = allBlogPosts.stream().limit(10).collect(Collectors.toList());

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
            ModelAndView mav = new ModelAndView("blog-details");
            mav.addObject("blogPostNotExist", true);
            return mav;
        }

        // fetch all posts by recent
        List<BlogPost> allBlogPosts = blogDAO.listBlogPosts().stream()
                .sorted(Comparator.comparing(BlogPost::getId).reversed())
                .collect(Collectors.toList());

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
        List<BlogPost> recentBlogPosts = allBlogPosts.stream()
                .limit(10)
                .collect(Collectors.toList());

        ModelAndView mav = new ModelAndView("blog-details");
        mav.addObject("blogPost", blogPost);
        mav.addObject("recentPosts", recentBlogPosts);
        mav.addObject("previousPost", previousPost); // Add previousPost to the model
        mav.addObject("nextPost", nextPost); // Add nextPost to the model
        return mav;
    }

    /**
     * Post a comment
     * (commentPostSuccess)
     * 
     * @param id
     * @param content
     * @param session
     * @return
     */
    @PostMapping("/comment/post")
    public ModelAndView postComment(
            @RequestParam Long id,
            @RequestParam String content,
            HttpSession session) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.CREATE_COMMENTS)) {
            ModelAndView mav = blogListPage(null, null, null, null);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // Find the blog post by its ID
        BlogPost blogPost = blogDAO.findBlogPostById(id);

        // Ensure the user is logged in, the comment content is not empty, and the blog
        // post exists
        if (blogPost != null && !content.trim().isEmpty()) {
            sessionAccount = accountDAO.accountFindWithId(sessionAccount.getId());
            blogDAO.createBlogPostComment(blogPost, sessionAccount, content);
        }

        ModelAndView mav = blogViewDetail(id);
        mav.addObject("commentPostSuccess", true);
        return mav;
    }

    /**
     * Handles the deletion of a blog post comment.
     * (commentDeleteSuccess/commentDeleteFailure)
     *
     * @param id      The ID of the comment to delete.
     * @param session The current HTTP session to get the logged-in user.
     * @return a ModelAndView to the blog post detail page.
     */
    @GetMapping("/comment/delete")
    public ModelAndView deleteComment(@RequestParam("comment") Long id, HttpSession session) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_COMMENTS)
                && !userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_OWN_COMMENTS)) {
            ModelAndView mav = blogListPage(null, null, null, null);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // delete comment
        BlogPostComment commentToDelete = blogDAO.findBlogPostCommentById(id);
        Long blogPostId = null;

        if (commentToDelete != null && ((sessionAccount.getId().equals(commentToDelete.getAccount().getId())
                && userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_OWN_COMMENTS))
                || (userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_COMMENTS))))
            if (commentToDelete != null
                    && (sessionAccount.getId().equals(commentToDelete.getAccount().getId())
                            && userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_OWN_COMMENTS))
                    && (userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_COMMENTS))) {
                blogDAO.deleteBlogPostCommentById(id);
                blogPostId = commentToDelete.getBlog().getId();
            }

        // check if failed
        if (blogPostId == null) {
            ModelAndView mav = blogListPage(null, null, null, null);
            mav.addObject("commentDeleteFailure", true);
            return mav;
        }

        // build view
        ModelAndView mav = blogViewDetail(blogPostId);
        mav.addObject("commentDeleteSuccess", true);
        return mav;
    }

    /**
     * Handles the update of a comment.
     *
     * @param blogPostId The ID of the blog post the comment belongs to.
     * @param commentId  The ID of the comment to be updated.
     * @param content    The new content of the comment.
     * @param session    The current HTTP session to get the logged-in user.
     * @return a ModelAndView to the blog post detail page.
     */
    @PostMapping("/comment/update")
    public ModelAndView updateComment(@RequestParam("id") Long blogPostId,
            @RequestParam Long commentId,
            @RequestParam String content,
            HttpSession session) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_COMMENTS)
                && !userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_OWN_COMMENTS)) {
            ModelAndView mav = blogListPage(null, null, null, null);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // update comment
        BlogPostComment commentToUpdate = blogDAO.findBlogPostCommentById(commentId);
        if (commentToUpdate != null
                && sessionAccount.getId().equals(commentToUpdate.getAccount().getId())
                && userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_OWN_COMMENTS)) {
            commentToUpdate.setContent(content);
            blogDAO.updateBlogPostComment(commentToUpdate);
        }

        return blogViewDetail(blogPostId);
    }
}
