package com.velora.backend.controller;

import com.velora.backend.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailTestController {

    @Autowired
    private MailService mailService;

    @GetMapping("/test")
    public String testMail() {

        mailService.sendMail(
                "himanisoni979@gmail.com",
                "Velora Test Email",
                "Congratulations! Email configuration is working."
        );

        return "Email Sent Successfully";
    }
}