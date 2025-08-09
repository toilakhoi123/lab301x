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

    @SuppressWarnings("unused")
    @Override
    @Transactional
    public void initiate() {
        Account account1 = em.find(Account.class, 1);
        Account account2 = em.find(Account.class, 2);
        Account account3 = em.find(Account.class, 3);

        BlogPost blog1 = createBlogPost(account1,
                "https://images.unsplash.com/photo-1599059813005-11265ba4b4ce?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Launching Hope: Our First Campaign",
                "We are thrilled to launch our inaugural donation campaign, bringing essential food, school supplies, and hygiene kits to underserved rural communities.\n\nYour contributions empower families, ensuring children can attend school and communities have access to basic needs. Join us in spreading hope—and real change—to those who need it most.");

        BlogPost blog2 = createBlogPost(account2,
                "https://images.unsplash.com/photo-1646314274609-b4064cdd02d9?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8ZG9uYXRpb258ZW58MHx8MHx8fDA%3D",
                "Feeding Futures: Seasonal Food Drive",
                "As the school year begins again, our Back‑to‑School Food Drive supplies backpacks, meal kits, and books to students in need.\n\nEvery donation ensures a child can learn with dignity. Help us fuel dreams and nourish young minds for a brighter tomorrow.");

        BlogPost blog3 = createBlogPost(account2,
                "https://images.unsplash.com/photo-1527788263495-3518a5c1c42d?q=80&w=1508&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Emergency Flood Relief Effort",
                "Recent floods displaced thousands across the region. We are raising funds to provide emergency shelters, clean drinking water, and medical aid.\n\nYour prompt support can save lives—let’s come together to offer relief and hope when it’s needed most.");

        BlogPost blog4 = createBlogPost(account2,
                "https://images.unsplash.com/photo-1609139027234-57570f43f692?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Volunteer Spotlight: Meet Sarah",
                "Sarah has volunteered over 200 hours distributing food kits across communities. Her dedication exemplifies selfless service.\n\nLearn about her journey, the impact of her work, and how you can join as a volunteer to be a force for good.");

        BlogPost blog5 = createBlogPost(account2,
                "https://images.unsplash.com/photo-1603827457577-609e6f42a45e?q=80&w=1631&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Holiday Cheer for Orphanages",
                "During the holiday season, we sent gifts, clothing, and festive meals to orphanages across the country.\n\nYour generosity created smiles, warmth, and lasting memories for over 500 children. Let’s keep the spirit of giving alive all year round.");

        BlogPost blog6 = createBlogPost(account2,
                "https://images.unsplash.com/photo-1593113598332-cd288d649433?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Become a Champion: Monthly Giving Program",
                "Monthly donors are the backbone of consistent impact. Your recurring support sustains long‑term projects—like education access and clean‑water systems.\n\nJoin our Monthly Giving Circle and help us build lasting community resilience.");

        BlogPost blog7 = createBlogPost(account2,
                "https://images.unsplash.com/photo-1609139003551-ee40f5f73ec0?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3Dy",
                "Rebuilding Lives After Natural Disasters",
                "We are partnering with local organizations to rebuild homes, clinics, and community centers. Trauma support and financial aid are also provided to those affected.\n\nTogether, we can help communities heal and rebuild stronger foundations.");

        BlogPost blog8 = createBlogPost(account2,
                "https://images.unsplash.com/photo-1638526970908-b18e32b0bc42?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Where Your Donations Go: Annual Report",
                "We believe in full transparency. In our annual report, we break down how funds are allocated—logistics, direct aid, and community programs.\n\nYour trust fuels our mission; here's where every donated dollar makes a measurable difference.");

        BlogPost blog9 = createBlogPost(account2,
                "https://images.unsplash.com/photo-1536856136534-bb679c52a9aa?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "On the Ground: Healthcare Outreach in Rural Clinics",
                "Our team recently visited clinics in remote villages that rely on donations to operate. We met healthcare workers and patients whose lives have transformed.\n\nRead their stories—stories powered by your kindness and commitment.");

        BlogPost blog10 = createBlogPost(account2,
                "https://plus.unsplash.com/premium_photo-1661962402563-9ad78bf7feda?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTN8fGRvbmF0aW9ufGVufDB8fDB8fHww",
                "Partner Profile: Empowering Local NGOs",
                "We collaborate with grassroots NGOs to extend reach and impact. These local partners distribute aid, organize community events, and offer counseling services.\n\nThis profile highlights their goals, challenges, and the lives they change daily. Your donations help scale their reach.");

        BlogPost blog11 = createBlogPost(account1,
                "", "Celebrating a Decade of Impact",
                "For ten years, we have worked tirelessly to empower communities. From our first small campaign to our current nationwide initiatives, we've come a long way. This post highlights our key milestones, the people we've served, and the incredible journey we've been on. Thank you for being a part of it.");

        BlogPost blog12 = createBlogPost(account2,
                "", "Water for Life: Clean Water Project",
                "Access to clean water is a fundamental human right. Our latest project focuses on installing water purification systems in drought-stricken regions. Your support helps us ensure children have safe drinking water, reducing disease and improving health.");

        BlogPost blog13 = createBlogPost(account2,
                "", "Behind the Scenes: A Day with Our Volunteers",
                "Ever wondered what a day in the life of our volunteers looks like? Follow along as we share stories, photos, and insights from the front lines of our community efforts. Their passion and dedication are truly inspiring.");

        BlogPost blog14 = createBlogPost(account1,
                "", "Mental Health Awareness: Our New Initiative",
                "Beyond providing physical aid, we are now focusing on mental wellness. Our new initiative offers counseling services and support groups for individuals and families in need. Your donations can help us break the stigma and provide much-needed support.");

        BlogPost blog15 = createBlogPost(account2,
                "", "The Power of Education: Scholarship Program",
                "Our new scholarship program is helping bright, underprivileged students attend college. We believe that education is the key to breaking the cycle of poverty. Meet the first recipients of our scholarship and hear their inspiring stories.");

        BlogPost blog16 = createBlogPost(account2,
                "", "Thank You: Donor Appreciation Event",
                "To our incredible community of donors, we want to say thank you. We recently hosted an event to celebrate your generosity and highlight the impact of your contributions. We couldn't do this without you.");

        BlogPost blog17 = createBlogPost(account2,
                "", "Empowering Women: Microloan Program",
                "We launched a microloan program to help women entrepreneurs in rural areas start their own businesses. This initiative provides not just financial support but also mentorship and training. Witness the ripple effect of these small loans on entire communities.");

        BlogPost blog18 = createBlogPost(account1,
                "", "The Harvest Project: Sustainable Farming",
                "Our latest project focuses on sustainable farming practices to combat food insecurity. We're providing seeds, tools, and training to local farmers, helping them cultivate a more resilient food supply for their communities.");

        BlogPost blog19 = createBlogPost(account2,
                "", "Our Impact on Wildlife Conservation",
                "While our primary focus is human aid, we also understand the importance of a healthy environment. We're proud to announce a new partnership with a wildlife conservation group to protect endangered species in the regions we serve.");

        BlogPost blog20 = createBlogPost(account2,
                "", "How to Fundraise for Us: A Guide",
                "Want to help but don't know where to start? This guide provides tips and resources on how to host your own fundraising event for our organization. Every dollar you raise makes a difference.");

        BlogPost blog21 = createBlogPost(account2,
                "", "A New Chapter: Our 2024 Goals",
                "As we begin a new year, we're setting ambitious goals to expand our reach and impact. Learn about our plans for the upcoming year, from new projects to expanding our volunteer network. We're excited for what's to come!");

        BlogPost blog22 = createBlogPost(account1,
                "", "Partnership Spotlight: The Tech for Good Foundation",
                "We are proud to announce a new partnership with the Tech for Good Foundation. This collaboration will help us streamline our logistics and track our aid distribution more effectively, ensuring every donation reaches its intended destination.");

        BlogPost blog23 = createBlogPost(account2,
                "",
                "Children’s Health and Wellness Program",
                "Our new program is focused on providing health screenings, vaccinations, and nutritional education to children in underserved areas. A healthy start in life is crucial for a brighter future, and your support makes it possible.");

        BlogPost blog24 = createBlogPost(account2,
                "", "Holiday Charity Auction: Bid for a Cause",
                "Join us for our annual holiday charity auction, where you can bid on amazing items and experiences. All proceeds go directly to our holiday aid projects, bringing joy and warmth to families in need.");

        BlogPost blog25 = createBlogPost(account2,
                "", "The Human Touch: A Story from the Field",
                "Read the moving story of a family whose life was transformed by our aid. This post shares a personal account from one of our field workers, highlighting the tangible and emotional impact of our work.");

        BlogPost blog26 = createBlogPost(account2,
                "",
                "Beyond the Check: Corporate Social Responsibility",
                "We believe in building long-term partnerships with businesses that share our values. This post highlights our collaborations with companies that are making a difference through their corporate social responsibility programs.");

        BlogPost blog27 = createBlogPost(account2,
                "",
                "A Volunteer's Perspective: My Time with the Organization",
                "Hear from one of our long-time volunteers, who shares their personal journey, the challenges they've faced, and the rewarding experiences they've had. Their story is a powerful testament to the spirit of giving.");

        BlogPost blog28 = createBlogPost(account1,
                "",
                "Upcoming Events: Join Us in Person!",
                "We're excited to announce a series of in-person events, from community clean-up drives to gala dinners. Join us to meet fellow supporters, learn more about our work, and get involved in a meaningful way.");

        BlogPost blog29 = createBlogPost(account2,
                "",
                "Impact of Your Dollars: A Financial Breakdown",
                "We believe in being transparent with our donors. This post provides a detailed breakdown of our finances for the past quarter, showing exactly how your contributions are being used to make a difference.");

        BlogPost blog30 = createBlogPost(account2,
                "",
                "The Lasting Legacy of a Single Donation",
                "This blog post tells the story of how a single, anonymous donation led to the creation of a new community center. It's a testament to the incredible power of generosity and the lasting legacy of a single act of kindness.");

        BlogPostComment cmt1 = createBlogPostComment(blog1, account2, "Yo this is sick wth");
        BlogPostComment cmt2 = createBlogPostComment(blog1, account2, "Actually goated");
        BlogPostComment cmt3 = createBlogPostComment(blog1, account3, "W");
        BlogPostComment cmt4 = createBlogPostComment(blog1, account1, "How's my post guys");
        BlogPostComment cmt5 = createBlogPostComment(blog2, account2, "Very pro");
        BlogPostComment cmt6 = createBlogPostComment(blog2, account3, "So nub");
        BlogPostComment cmt7 = createBlogPostComment(blog2, account1, "Keep up the great work!");
        BlogPostComment cmt8 = createBlogPostComment(blog3, account3,
                "Incredible response to the floods, proud to support this.");
        BlogPostComment cmt9 = createBlogPostComment(blog3, account2, "Thanks for all you do!");
        BlogPostComment cmt10 = createBlogPostComment(blog4, account1,
                "Sarah is an inspiration. So glad to hear her story.");
        BlogPostComment cmt11 = createBlogPostComment(blog5, account3,
                "This is so heartwarming. The kids must have loved it.");
        BlogPostComment cmt12 = createBlogPostComment(blog6, account2,
                "Monthly giving is such a powerful idea. I'm signing up!");
        BlogPostComment cmt13 = createBlogPostComment(blog7, account1,
                "It's amazing how you're helping people rebuild their lives.");
        BlogPostComment cmt14 = createBlogPostComment(blog7, account2,
                "Thank you for the update on the disaster relief efforts.");
        BlogPostComment cmt15 = createBlogPostComment(blog8, account3,
                "Transparency is key. Appreciate you sharing this.");
        BlogPostComment cmt16 = createBlogPostComment(blog9, account1,
                "Reading these stories is so moving. You're doing God's work.");
        BlogPostComment cmt17 = createBlogPostComment(blog9, account2, "This makes me want to become a volunteer!");
        BlogPostComment cmt18 = createBlogPostComment(blog10, account3,
                "Love that you're collaborating with local NGOs. That's how real change happens.");
        BlogPostComment cmt19 = createBlogPostComment(blog10, account1,
                "Great partner profile. They sound like a fantastic organization.");
        BlogPostComment cmt20 = createBlogPostComment(blog11, account2,
                "Congrats on a decade of success! So inspiring.");
        BlogPostComment cmt21 = createBlogPostComment(blog12, account3,
                "Clean water projects are so vital. How can I help?");
        BlogPostComment cmt22 = createBlogPostComment(blog13, account1,
                "Seeing the volunteers in action is really cool. They're the real heroes.");
        BlogPostComment cmt23 = createBlogPostComment(blog14, account2,
                "This is a much-needed initiative. Mental health is so important.");
        BlogPostComment cmt24 = createBlogPostComment(blog15, account3,
                "Education is the best investment. So happy to hear about the scholarship program.");
        BlogPostComment cmt25 = createBlogPostComment(blog16, account1,
                "Thanks for everything you do! We love supporting you.");
        BlogPostComment cmt26 = createBlogPostComment(blog17, account2,
                "Empowering women is so important. This is a great project.");
        BlogPostComment cmt27 = createBlogPostComment(blog18, account3,
                "Sustainable farming is the way to go. Great work!");
        BlogPostComment cmt28 = createBlogPostComment(blog19, account1,
                "So glad to see you're also focused on the environment. It all connects.");
        BlogPostComment cmt29 = createBlogPostComment(blog20, account2,
                "This guide is super helpful! I'm planning my own fundraiser now.");
        BlogPostComment cmt30 = createBlogPostComment(blog21, account3,
                "Excited to see what's next for the organization!");
        BlogPostComment cmt31 = createBlogPostComment(blog22, account1,
                "Love the new partnership. Efficiency is so important.");
        BlogPostComment cmt32 = createBlogPostComment(blog23, account2,
                "Health and wellness for kids is a perfect focus.");
        BlogPostComment cmt33 = createBlogPostComment(blog24, account3,
                "I'm definitely going to bid at the auction this year!");
        BlogPostComment cmt34 = createBlogPostComment(blog25, account1,
                "What a powerful story. Thank you for sharing.");
        BlogPostComment cmt35 = createBlogPostComment(blog26, account2,
                "Great to see businesses getting involved. It's a win-win.");
        BlogPostComment cmt36 = createBlogPostComment(blog27, account3,
                "Volunteer stories are the best. It's so inspiring to read them.");
        BlogPostComment cmt37 = createBlogPostComment(blog28, account1,
                "I'll be at the next clean-up drive. See you there!");
        BlogPostComment cmt38 = createBlogPostComment(blog29, account2,
                "I love the financial breakdown. It makes me feel good about my donations.");
        BlogPostComment cmt39 = createBlogPostComment(blog30, account3,
                "The legacy of that donation is incredible. It really shows how much of a difference one person can make.");
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
