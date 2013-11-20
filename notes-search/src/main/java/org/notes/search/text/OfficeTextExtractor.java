package org.notes.search.text;

public class OfficeTextExtractor { //implements TextExtractor {

//    @Override
//    public String[] getContentTypes() {
//        return new String[]{"application/msword", "application/msexcel", "application/mspowerpoint"};
//    }
//
//    @Override
//    public List<String> extract(FileReference file) throws NotesException {
//
//        PDDocument pdDoc = null;
//        COSDocument cosDoc = null;
//
//        try {
//            List<String> pages = new LinkedList<String>();
//
//            PDFParser parser = new PDFParser(new FileInputStream(new File(file.getReference())));
//            parser.parse();
//            cosDoc = parser.getDocument();
//            PDFTextStripper stripper = new PDFTextStripper();
//            pdDoc = new PDDocument(cosDoc);
//
//            for (int page = 1; page <= pdDoc.getNumberOfPages(); page++) {
//
//                stripper.setStartPage(page);
//                stripper.setEndPage(page);
//
//                pages.add(stripper.getText(pdDoc));
//            }
//
//            return pages;
//
//        } catch (IOException e) {
//            throw new NotesException("pdf extractor", e);
//        } finally {
//            try {
//                if (cosDoc != null)
//                    cosDoc.close();
//                if (pdDoc != null)
//                    pdDoc.close();
//            } catch (Exception e) {
//                // ignore
//            }
//        }
//    }
}
