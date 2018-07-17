package ua.edu.sumdu.essuir.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.edu.sumdu.essuir.entity.Publication;
import ua.edu.sumdu.essuir.service.ExportDocumentProcessorService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/export")
public class ExportController {

    @Resource
    private ExportDocumentProcessorService documentProcessor;

    @RequestMapping(value = "/user", method = RequestMethod.POST, produces = {"application/json; charset=UTF-8"})
    public void export(@RequestParam("publications") String publicationList, @RequestParam("author") String author) throws IOException {
        List<Publication> publications = new ObjectMapper()
                .readValue(publicationList, new TypeReference<List<Publication>>() {});
        documentProcessor.createDocument(author, publications);
    }
}
