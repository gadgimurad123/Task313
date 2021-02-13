package com.gaz.web.spring_boot_v1.controller;

import com.gaz.web.spring_boot_v1.entity.Role;
import com.gaz.web.spring_boot_v1.entity.User;
import com.gaz.web.spring_boot_v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AppController {

    @Autowired
    UserService userService;

    @GetMapping({"/", "login"})
    public String welcome() {

        return "login";
    }

    @GetMapping("/user")
    public String user(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByName(username);

        model.addAttribute("currentUser", user);

        return "user";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> allUsersList = userService.getAllUsers();
        model.addAttribute("listRole", userService.getListRole());
        model.addAttribute("user", userService.getUserByName(name));
        model.addAttribute("allUsersList", allUsersList);

        return "admin";
    }

    @PostMapping("/admin/users/{id}")
    public String update(@ModelAttribute("id") Long id,
                         @ModelAttribute("username") String username,
                         @ModelAttribute("password") String password,
                         @ModelAttribute("name") String name,
                         @ModelAttribute("lastname") String lastname,
                         @ModelAttribute("age") int age,
                         @ModelAttribute("email") String email,
                         @ModelAttribute("roles") String[] roles) {
        User user = userService.getUserById(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setLastName(lastname);
        user.setAge(age);
        user.setEmail(email);

        user.setRoles(getRoles(roles));
        userService.saveUser(user);

        return "redirect:/admin";
    }

    @PostMapping(value = "/admin/users")
    public String add(@ModelAttribute("username") String username,
                       @ModelAttribute("password") String password,
                       @ModelAttribute("name") String name,
                       @ModelAttribute("lastname") String lastname,
                       @ModelAttribute("age") int age,
                       @ModelAttribute("email") String email,
                       @ModelAttribute("roles") String[] roles) {
        User user = new User(username, password, name, lastname, age, email);
        user.setId(9999L);
        user.setRoles(getRoles(roles));
        userService.saveUser(user);

        return "redirect:/admin";
    }

    @DeleteMapping("/admin/users/{id}")
    public String delete(@ModelAttribute("id") Long id) {
        userService.deleteUser(id);

        return "redirect:/admin";
    }

    private Set<Role> getRoles(String[] role) {
        Set<Role> roleSet = new HashSet<>();
        for (String s : role) {
            roleSet.add(userService.getRoleByName(s));
        }
        return roleSet;
    }
}
