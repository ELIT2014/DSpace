package org.ssu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.ssu.service.AuthorsService;
import org.ssu.service.localization.AuthorsCache;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
@RequestMapping
public class AdminController {
    @Resource
    private AuthorsService authorsService;

    @RequestMapping("/authors/list")
    public ModelAndView autofillPage(ModelAndView model, HttpServletRequest request, HttpServletResponse response) {
        Optional<String> startsWith = Optional.ofNullable(request.getParameter("startsWith"));
        model.addObject("authors", authorsService.getAllAuthors(startsWith));
        model.setViewName("autofill");
        return model;
    }

    @RequestMapping(value = "/authors/edit", method = RequestMethod.GET)
    public ModelAndView authorEditPage(ModelAndView model, HttpServletRequest request, HttpServletResponse response) {
        Optional<String> author = Optional.ofNullable(request.getParameter("author"));
        model.addObject("author", authorsService.getAuthorLocalization(author));
        model.setViewName("author-edit");
        return model;
    }
}
