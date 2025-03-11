package com.aplavina.springsecurityjwt.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSecurityController {
    @GetMapping("/public")
    public String getPublic() {
        return "accessed public data";
    }

    @GetMapping("/auth")
    @PreAuthorize("isAuthenticated()")
    public String getForAuth() {
        return "accessed data for authenticated";
    }

    @GetMapping("/moderator")
    @PreAuthorize("hasRole('MODERATOR')")
    public String getForModerator() {
        return "accessed data for moderators";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String getForSuperAdmin() {
        return "accessed data for super admins";
    }
}
