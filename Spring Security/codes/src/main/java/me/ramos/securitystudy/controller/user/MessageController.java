package me.ramos.securitystudy.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MessageController {

    @GetMapping("/messages")
    public String myPage() {
        return "user/messages";
    }
}
