package ua.edu.sumdu.essuir.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.essuir.entity.Publication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportDocumentProcessorService {

    public XWPFDocument createDocument(String author, List<Publication> publications) throws IOException {
        XWPFDocument document = new XWPFDocument();

        createTitle(author, document);
        createPublicationsTable(document, publications);

        FileOutputStream out = new FileOutputStream(new File("C:/test/create_table.docx"));
        document.write(out);
        out.close();
        return document;
    }

    private void createHeaderRun(XWPFDocument document, ParagraphAlignment alignment, boolean bold, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(alignment);
        XWPFRun run = paragraph.createRun();
        paragraph.setSpacingAfter(0);
        run.setBold(bold);
        run.setFontFamily("Times New Roman");
        run.getCTR().getRPr().getRFonts().setHAnsi("Times New Roman");
        run.setFontSize(14);
        run.setText(text);
    }

    private void createTitle(String author, XWPFDocument document) {
        createHeaderRun(document, ParagraphAlignment.CENTER, true, "СПИСОК");
        createHeaderRun(document, ParagraphAlignment.CENTER, true, "навчально-методичних та наукових праць");
        createHeaderRun(document, ParagraphAlignment.CENTER, false, author.replaceAll(",", ""));
    }

    private void createPublicationsTable(XWPFDocument document, List<Publication> publications) {
        XWPFTable table = document.createTable(publications.size() + 1, 5);

        XWPFTableRow row = table.getRow(0);
        row.getCell(0).setText("№ з/п");
        row.getCell(1).setText("Назва");
        row.getCell(2).setText("Вихідні дані");
        row.getCell(3).setText("Обсяг (у сторінках)/авторський доробок");
        row.getCell(4).setText("Співавтори");

        for (int index = 0; index < publications.size(); index++) {
            row = table.getRow(index + 1);
            row.getCell(0).setText(Integer.toString(index + 1));
            row.getCell(1).setText(publications.get(index).getTitle());
            row.getCell(2).setText(publications.get(index).getCitation());
            row.getCell(3).setText("");
            row.getCell(4).setText(publications.get(index).getAuthors());
        }
    }

}
