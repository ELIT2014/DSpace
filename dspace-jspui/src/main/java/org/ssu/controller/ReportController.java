package org.ssu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class ReportController {
    @RequestMapping("/report")
    public ModelAndView homepage(ModelAndView model) {
        model.setViewName("report-homepage");
        return model;
    }

    @RequestMapping("/report/recent-person")
    public ModelAndView recentRegistrationsPersons(ModelAndView model, HttpServletRequest request, HttpServletResponse response) {
        model.addObject("limit", Optional.ofNullable(request.getParameter("limit")).map(Integer::valueOf).orElse(20));
        model.setViewName("report-recent-registrations");
        return model;
    }
}
