package org.ssu.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.browse.BrowseInfo;
import org.dspace.core.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ssu.entity.Publication;
import org.ssu.service.BrowseContext;
import org.ssu.service.ExportDocumentProcessorService;
import org.ssu.service.ExportTempService;


import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping(value = "/export")
public class ExportController {
    @Resource
    private ExportTempService exportTempService;

    @Resource
    private ExportDocumentProcessorService documentProcessor;

    @ResponseBody
    @RequestMapping(value = "/user", method = RequestMethod.POST, produces = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document"})
    public void export(@RequestParam("author") String author, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ServletException, AuthorizeException {
        Context dspaceContext = UIUtil.obtainContext(request);

        BrowseInfo browseInfo = new BrowseContext().getBrowseInfo(dspaceContext, request, response);
        String publicationList = exportTempService.publicationList(browseInfo);

        List<Publication> publications = new ObjectMapper()
                .readValue(publicationList, new TypeReference<List<Publication>>() {
                });

        XWPFDocument document = documentProcessor.createDocument(author, publications);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.write(byteArrayOutputStream);

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=publications.docx");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.getOutputStream().write(byteArrayOutputStream.toByteArray());
        response.getOutputStream().flush();
    }
}