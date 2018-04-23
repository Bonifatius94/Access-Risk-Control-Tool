package office;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

public class WordTest {

    public static void main(String[] args) {

        try {
            createWordDocument("test.docx");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createWordDocument(String filePath) throws Exception {

        // create new document
        XWPFDocument document = new XWPFDocument();

        // write test paragraph
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("This is a test text.");

        // save document as file
        FileOutputStream out = new FileOutputStream( new File(filePath));
        document.write(out);
        out.close();
    }

    @SuppressWarnings("unused")
    private static void writeBookmark(XWPFDocument document, String key, String value) {

        // TODO: test code

        for (XWPFParagraph paragraph : document.getParagraphs()) {

            CTP ctp = paragraph.getCTP();
            List<CTBookmark> bookmarks = ctp.getBookmarkStartList();
            Optional<CTBookmark> bookmark = bookmarks.stream().filter(x -> x.getName().equals(key)).findFirst();

            if (bookmark != null) {

                if (value != null && !value.equals("")) {

                    // Create a new RSID (revision identifier) and add text
                    CTR ctr = ctp.addNewR();
                    CTText text = ctr.addNewT();
                    text.setStringValue(value);
                }

            } else {

                throw new IllegalArgumentException("bookmark '" + key + "' could not be found!");

            }
        }
    }

}
