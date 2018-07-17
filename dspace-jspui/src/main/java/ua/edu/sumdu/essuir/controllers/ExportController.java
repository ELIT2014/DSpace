package ua.edu.sumdu.essuir.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

@Controller
@RequestMapping(value = "/export")
public class ExportController {
    @RequestMapping(value = "/user", method = RequestMethod.POST, produces = {"application/json; charset=UTF-8"})
    public void export(@RequestParam("publications") String publications) throws UnsupportedEncodingException {
        System.out.println(publications);
    }
}
