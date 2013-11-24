package org.notes.search.text;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;
import org.notes.search.interfaces.TextExtractor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Stateless
@EJB(name = PdfTextExtractor.BEAN_NAME, beanInterface = TextExtractor.class)
public class PdfTextExtractor implements TextExtractor {

    public static final String BEAN_NAME = "PdfTextExtractor";

    @Override
    public ExtractionResult extract(FileReference file) throws NotesException {

        PDDocument pdDoc = null;
        COSDocument cosDoc = null;

        try {
            List<String> pages = new LinkedList<String>();

            PDFParser parser = new PDFParser(new FileInputStream(new File(file.getReference())));
            parser.parse();
            cosDoc = parser.getDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);

            for (int page = 1; page <= pdDoc.getNumberOfPages(); page++) {

                stripper.setStartPage(page);
                stripper.setEndPage(page);

                pages.add(stripper.getText(pdDoc));
            }

            // todo page information should not been lost
            return new ExtractionResult(pages, pdDoc.getNumberOfPages());

        } catch (IOException e) {
            throw new NotesException("pdf extractor failed", e);

        } finally {
            try {
                if (cosDoc != null)
                    cosDoc.close();
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
