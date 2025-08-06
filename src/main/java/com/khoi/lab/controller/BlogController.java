package com.khoi.lab.controller;

import java.util.List;

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
    private final DonationDAO donationDAO;
    private final AccountDAO accountDAO;
    private final BlogDAO blogDAO;

    /**
     * DAO Initiator
     * 
     * @param donationDAO
     */
    public BlogController(DonationDAO donationDAO, AccountDAO accountDAO, BlogDAO blogDAO) {
        this.donationDAO = donationDAO;
        this.accountDAO = accountDAO;
        this.blogDAO = blogDAO;
    }

    /**
     * View a page of blog list
     * 
     * @param page
     * @return
     */
    @GetMapping("")
    public ModelAndView blogListPage(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String query) {
        if (page == null)
            page = 1;
        if (pageSize == null)
            pageSize = 3;

        // get blog posts
        List<BlogPost> blogPosts = blogDAO.listBlogPosts();
        int maxPage = (int) Math.round(Math.ceil(blogPosts.size() / pageSize));
        blogPosts = PaginationService.getPage(blogPosts, page, pageSize);

        // build and return view
        System.out.println("| [blogListPage] Displaying page " + page + " of " + maxPage + " pages!");
        ModelAndView mav = new ModelAndView("blogs");
        mav.addObject("blogPosts", blogPosts);
        mav.addObject("currentPage", page);
        mav.addObject("maxPage", maxPage);
        return mav;
    }

    /**
     * View details of a blog
     * (blogPostNotExist)
     * 
     * @param id
     * @return
     */
    @GetMapping("/blog")
    public ModelAndView blogViewDetail(@RequestParam Long id) {
        BlogPost blogPost = blogDAO.findBlogPostById(id);

        if (blogPost == null) {
            ModelAndView mav = blogListPage(null, null, null);
            mav.addObject("blogPostNotExist", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("blog-details");
        mav.addObject("blogPost", blogPost);
        return mav;
    }

    @PostMapping("/comment")
    public String postMethodName(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

}
