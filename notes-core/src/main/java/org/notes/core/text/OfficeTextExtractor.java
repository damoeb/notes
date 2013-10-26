package org.notes.core.text;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.TextExtractor;
import org.notes.core.model.FileReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class OfficeTextExtractor implements TextExtractor {
    @Override
    public String[] getContentTypes() {
        return new String[]{"application/msword", "application/msexcel", "application/mspowerpoint"};
    }

    @Override
    public List<String> extract(FileReference file) throws NotesException {

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

            return pages;

        } catch (IOException e) {
            throw new NotesException("pdf extractor", e);
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
