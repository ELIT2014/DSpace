package ua.edu.sumdu.essuir.service;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.essuir.entity.Publication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Component
public class ExportDocumentProcessorService {

    public XWPFDocument createDocument(String author, List<Publication> publications) throws IOException {
        XWPFDocument document = new XWPFDocument();
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(1020L));
        pageMar.setTop(BigInteger.valueOf(455L));
        pageMar.setRight(BigInteger.valueOf(455L));
        pageMar.setBottom(BigInteger.valueOf(455L));

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
        document.createParagraph();
    }

    private XWPFParagraph tableCellParagraph(XWPFParagraph paragraph, String text, ParagraphAlignment alignment) {
        paragraph.setAlignment(alignment);

        XWPFRun run = paragraph.createRun();
        paragraph.setSpacingAfter(0);
        run.setFontSize(14);
        run.setFontFamily("Times New Roman");
        run.getCTR().getRPr().getRFonts().setHAnsi("Times New Roman");
        run.setText(text);
        return paragraph;
    }

    private void processRow(XWPFTable table, int rowIndex, String[] text) {
        XWPFTableRow row = table.getRow(rowIndex);
        for(int cellIndex = 0; cellIndex < text.length; cellIndex++) {
            XWPFTableCell cell = row.getCell(cellIndex);
            cell.setParagraph(tableCellParagraph(cell.getParagraph(), text[cellIndex], ParagraphAlignment.CENTER));
        }
    }

    private void setTableProperties(XWPFTable table) {
        Integer[] width = {540, 2200, 1250, 3000, 1400, 2300};
        table.setWidth(Arrays.stream(width).reduce(0, (a, b) -> a + b));
        table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(width[0]));
        for (int col = 1 ; col < width.length; col++) {
            table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(width[col]));
        }

        for(int i = 0; i < width.length; i++) {
            CTTblWidth tblWidth = table.getRow(0).getCell(i).getCTTc().addNewTcPr().addNewTcW();
            tblWidth.setW(BigInteger.valueOf(width[i]));
            tblWidth.setType(STTblWidth.DXA);
        }
    }
    private void createPublicationsTable(XWPFDocument document, List<Publication> publications) {
        XWPFTable table = document.createTable(publications.size() + 1, 6);
        setTableProperties(table);

        processRow(table, 0, new String[]{"№ з/п", "Назва", "Характер роботи", "Вихідні дані", "Обсяг", "Співавтори"});
        for (int index = 0; index < publications.size(); index++) {
            String[] rowData = {Integer.toString(index + 1),
                    publications.get(index).getTitle(),
                    publications.get(index).getType(),
                    publications.get(index).getCitation(),
                    "",
                    publications.get(index).getAuthors().replaceAll(";", ";\r\n").replaceAll(",", "")};
            processRow(table, index + 1, rowData);
        }
    }

}
