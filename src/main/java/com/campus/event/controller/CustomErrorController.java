package com.campus.event.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = 500;
        String title = "Unexpected Error";
        String description = "Something went wrong while processing your request.";

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                title = "404 - Page or Event Not Found";
                description = "The page or campus event you are looking for does not exist or has been removed.";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                title = "403 - Access Denied";
                description = "You do not have permission to access this campus resource. Please log in with appropriate privileges.";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                title = "400 - Invalid Request";
                description = "The submitted information was incomplete or malformed.";
            }
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorDescription", description);
        model.addAttribute("detailMessage", message != null ? message.toString() : "");

        return "error";
    }
}
