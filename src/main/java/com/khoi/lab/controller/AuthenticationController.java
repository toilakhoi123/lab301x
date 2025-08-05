package com.khoi.lab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Account;
import com.khoi.lab.service.EmailSenderService;
import com.khoi.lab.service.StringService;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for authentication mappings
 */
@Controller
public class AuthenticationController {
    private final AccountDAO accountDAO;
    private final DonationDAO donationDAO;

    @Autowired
    private EmailSenderService senderService;

    /**
     * DAO Initiator
     * 
     * @param accountDAO
     */
    public AuthenticationController(AccountDAO accountDAO, DonationDAO donationDAO) {
        this.accountDAO = accountDAO;
        this.donationDAO = donationDAO;
    }

    /**
     * Login page
     * 
     * @return
     */
    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    /**
     * Handles login request
     * (loginSuccess/loginFailure/loginAlready/loginDisabled/loginForbiddenMethod)
     * 
     * @param session
     * @param usernameOrEmailOrPhone
     * @param password
     * @param rememberMe
     * @return
     */
    @PostMapping("/login")
    public ModelAndView loginRequest(
            HttpSession session,
            @RequestParam String usernameOrEmailOrPhone,
            @RequestParam String password,
            @RequestParam(required = false, defaultValue = "false") boolean rememberMe) {
        // check if not logged in
        if ((Account) session.getAttribute("account") == null) {
            Account account = accountDAO.accountLogin(usernameOrEmailOrPhone, password);

            if (account == null) {
                // login failure
                ModelAndView mav = login();
                mav.addObject("loginFailure", true);
                return mav;
            } else {
                if ("".equals(account.getPassword())) {
                    ModelAndView mav = login();
                    mav.addObject("loginForbiddenMethod", true);
                    return mav;
                }

                // check if account is disabled
                if (!account.isDisabled()) {
                    session.setAttribute("account", account);

                    // login success
                    ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
                    mav.addObject("loginSuccess", true);
                    return mav;
                } else {
                    System.out.println("| Account is disabled by admins!!!");

                    // account disabled
                    ModelAndView mav = login();
                    mav.addObject("loginDisabled", true);
                    return mav;
                }
            }
        } else {
            System.out.println("| Already logged in!");
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("loginAlready", true);
            return mav;
        }
    }

    /**
     * Register page
     * 
     * @return
     */
    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    /**
     * Handles register request
     * (registerSuccess/passwordMismatch/registerUsernameExists/registerUsernameExists/registerPhoneNumberExists)
     * 
     * @param firstName
     * @param lastName
     * @param username
     * @param phoneNumber
     * @param email
     * @param password
     * @param passwordConfirm
     * @return
     */
    @PostMapping("/register")
    public ModelAndView registerRequest(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String passwordConfirm) {
        // check password confirm
        if (!password.equals(passwordConfirm)) {
            System.out.println("| Password mismatch: " + password + " vs " + passwordConfirm);
            ModelAndView mav = register();
            mav.addObject("passwordMismatch", true);
            return mav;
        }

        // check account username exist
        if (accountDAO.accountFindWithUsername(username) != null) {
            System.out.println("| Username exists!");
            ModelAndView mav = register();
            mav.addObject("registerUsernameExists", true);
            return mav;
        }

        // check account username exist
        if (accountDAO.accountFindWithEmail(email) != null) {
            System.out.println("| Email exists!");
            ModelAndView mav = register();
            mav.addObject("registerEmailExists", true);
            return mav;
        }

        // check account phone number exist
        if (accountDAO.accountFindWithPhoneNumber(phoneNumber) != null) {
            System.out.println("| Phone number exists!");
            ModelAndView mav = register();
            mav.addObject("registerPhoneNumberExists", true);
            return mav;
        }

        // register successful
        accountDAO.accountRegister(username, firstName, lastName, email, phoneNumber, password);
        ModelAndView mav = login();
        mav.addObject("registerSuccess", true);
        return mav;
    }

    /**
     * Forgot password page
     * 
     * @return
     */
    @GetMapping("/forgot-password")
    public ModelAndView forgotPassword() {
        return new ModelAndView("forgot-password");
    }

    /**
     * Handle password forgot request
     * (accountNotFound/emailSent)
     * 
     * @param email
     * @return
     */
    @PostMapping("/forgot-password")
    public ModelAndView forgotPasswordRequest(@RequestParam String email) {
        Account account = accountDAO.accountFindWithEmail(email);

        // check if email associates with an account
        if (account == null) {
            ModelAndView mav = forgotPassword();
            mav.addObject("accountNotFound", true);
            return mav;
        }

        // create token
        String code = StringService.getAlphaNumericString(6);
        accountDAO.createPasswordResetCodeForAccount(account.getId(), code);

        // send email async
        new Thread(() -> {
            senderService.sendEmail(email, "Password Reset", "Your password reset code is: " + code);
        }).start();

        // forward to verify-code view
        ModelAndView mav = verifyCode();
        mav.addObject("emailSent", true);
        mav.addObject("accountId", account.getId());
        mav.addObject("code", code);
        return mav;
    }

    @GetMapping("/change-password")
    public ModelAndView changePassword(HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            return login();
        }

        return new ModelAndView("change-password");
    }

    /**
     * Handle change password request
     * (passwordChangeSuccess/wrongPassword/passwordMismatch)
     * 
     * @param session
     * @param oldPassword
     * @param password
     * @param passwordConfirm
     * @return
     */
    @PostMapping("/change-password")
    public ModelAndView changePasswordRequest(
            HttpSession session,
            @RequestParam String oldPassword,
            @RequestParam String password,
            @RequestParam String passwordConfirm) {
        Account account = (Account) session.getAttribute("account");

        // check if correct old password
        if (!account.getPassword().equals(oldPassword)) {
            ModelAndView mav = changePassword(session);
            mav.addObject("wrongPassword", true);
            return mav;
        }

        // check if password confirmation is right
        if (!password.equals(passwordConfirm)) {
            ModelAndView mav = changePassword(session);
            mav.addObject("passwordMismatch", true);
            return mav;
        }

        // success
        account.setPassword(password);
        account = accountDAO.accountUpdate(account);
        session.setAttribute("account", account);

        ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
        mav.addObject("passwordChangeSuccess", true);
        return mav;
    }

    /**
     * Verify code page.
     * Redirect to forgot-password page
     * 
     * @param param
     * @return
     */
    @GetMapping("/verify-code")
    public ModelAndView verifyCode() {
        return new ModelAndView("verify-code");
    }

    /**
     * Handles verification code validation request
     * (validCode/invalidCode)
     * 
     * @param accountId
     * @param code
     * @param codeEntered
     * @return
     */
    @PostMapping("/verify-code")
    public ModelAndView verifyCodeRequest(
            @RequestParam Long accountId,
            @RequestParam String code,
            @RequestParam String codeEntered) {
        if (code.equals(codeEntered)) {
            // correct code
            System.out.println("| Valid code entered!");
            ModelAndView mav = resetPassword();
            mav.addObject("validCode", true);
            mav.addObject("accountId", accountId);
            return mav;
        }

        // incorrect code
        System.out.println("| Invalid code entered!");
        ModelAndView mav = verifyCode();
        mav.addObject("invalidCode", true);
        mav.addObject("accountId", accountId);
        mav.addObject("code", code);
        return mav;
    }

    /**
     * Reset password page
     * Redirect to forgot-password page
     * 
     * @return
     */
    @GetMapping("/reset-password")
    public ModelAndView resetPassword() {
        return new ModelAndView("reset-password");
    }

    /**
     * Handle reset password request
     * (passwordResetSuccess/passwordMismatch)
     * 
     * @param accountId
     * @param password
     * @param passwordConfirm
     * @return
     */
    @PostMapping("/reset-password")
    public ModelAndView resetPasswordRequest(
            @RequestParam Long accountId,
            @RequestParam String password,
            @RequestParam String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            // confirmation does not match
            ModelAndView mav = resetPassword();
            mav.addObject("passwordMismatch", true);
            mav.addObject("accountId", accountId);
            return mav;
        }

        // confirmation valid, set new password
        accountDAO.accountChangePassword(accountId, password);
        ModelAndView mav = login();
        mav.addObject("passwordResetSuccess", true);
        return mav;
    }

    /**
     * Log out the current user
     * 
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute("account");
        System.out.println("| Logged the current user out!");
        return (new GeneralController(donationDAO, accountDAO)).index();
    }
}
