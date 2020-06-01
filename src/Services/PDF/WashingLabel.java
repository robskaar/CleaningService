package Services.PDF;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.apache.pdfbox.tools.PrintPDF;
import org.apache.pdfbox.util.Matrix;

import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author Robert Skaar
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 25-05-2020
 **/

public class WashingLabel {
    private PDDocument document;
    private String fileName;
    private float fontSize;
    private PDPage page;
    private int orderID;
    private int laundryItemID;
    private int costumerID;
    private String savePath;
    private ArrayList<String> textLines = new ArrayList<>();

    public WashingLabel(String fileName, float fontSize, int orderID, int laundryItemID, int costumerID, String...lines) {
        String docFileName = fileName + ".pdf";
        this.fileName = docFileName;
        this.fontSize = fontSize;
        this.document = new PDDocument();
        this.page = new PDPage();
        this.page.setMediaBox(PDRectangle.LETTER);
        this.page.setRotation(90);
        this.orderID = orderID;
        this.laundryItemID = laundryItemID;
        this.costumerID = costumerID;
        this.savePath = "src/Foundation/Resources/Files/WashingLabels/" + fileName+".pdf";
        for(String line:lines) {
           textLines.add(line);
        }
        try {
            PDRectangle pageSize = this.page.getMediaBox();
            float pageWidth = pageSize.getWidth();
            PDPageContentStream contentStream = new PDPageContentStream(this.document, this.page);
            contentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, this.fontSize);
            contentStream.setLeading(60f);
            contentStream.newLineAtOffset(25,550);
            for (String line:textLines) {
                contentStream.showText(line);
                contentStream.newLine();
            }
            contentStream.endText();
            contentStream.close();
            this.document.addPage(this.page);
            this.document.save(this.savePath);
            this.document.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        //"laundryItemNo_" + orderItem.getID()
    }

    public void saveDocument(PDDocument document){
        try {
            document.save(this.savePath);
            document.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void printLabel( ){
        //added this for future print capabilities
        //this.document.getDocument().print();
    }
}
