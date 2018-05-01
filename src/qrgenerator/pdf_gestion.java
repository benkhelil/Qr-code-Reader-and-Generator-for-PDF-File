package qrgenerator;

import com.lowagie.text.BadElementException;
import java.io.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Rectangle;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFFile;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.imageio.ImageIO;

public class pdf_gestion {
    public static void main(java.lang.String[] args) {
        Document document = new Document();
        try {

            // Read the src pdf
            String src = "pdf-source/pdf-4.pdf";
            PDFFile pdf = read_pdf(src);

            /// Convert the pdf to img and put it in tmp folder.
            convert_pdf_to_image(pdf);

            // Put the Gr code img in the last img of the pdf.
         //   put_qr_into_pdf_imgs(pdf.getNumPages());


            //Create a new pdf (result) using the img in tmp file.
            //create_new_pdf(document,pdf.getNumPages());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        document.close();
    }
    
    static PDFFile read_pdf(String src) throws FileNotFoundException, IOException{
            File pdfFile = new File(src);
            RandomAccessFile raf = new RandomAccessFile(pdfFile, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            PDFFile pdf = new PDFFile(buf);
            return pdf;
    }
    
    static void convert_pdf_to_image(PDFFile pdf) throws IOException {
        int nb_page = pdf.getNumPages();
            for (int i = 0; i < nb_page; i++) {
                createImage(pdf.getPage(i + 1), "tmp/tmp-" + i + ".png");
            }
    }
    
    static void put_qr_into_pdf_imgs(int page_numbers, String imagePath, int pos){
            File sourceImageFile = null;
            File watermarkImageFile = new File(imagePath);
            File destImageFile = null;
            for (int i = 0; i < page_numbers; i++) {
                
                sourceImageFile = new File("tmp/tmp-" + i + ".png");
                destImageFile = new File("tmp/pdf-img-" + i + ".png");
                if(pos==i+1) {
                    addImageWatermark(watermarkImageFile, sourceImageFile, destImageFile, new Integer(0),true);
                } else {
                    addImageWatermark(watermarkImageFile, sourceImageFile, destImageFile, new Integer(0),false);
                }
            }
    }
    
    static void create_new_pdf(Document document, int page_numbers, String pdfPath) throws DocumentException, FileNotFoundException, BadElementException, IOException {
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            Image tmp_img = null;
            int indentation = 0;

            for (int i = 0; i < page_numbers; i++) {
                tmp_img = Image.getInstance("tmp/pdf-img-" + i + ".png");
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - indentation) / tmp_img.getWidth()) * 100;
                //tmp_img.setAbsolutePosition(0, 0);
                tmp_img.setAlignment(Image.BOTTOM);
                //System.out.println(scaler);
                tmp_img.scalePercent(90);
                System.out.println("tmp/pdf-img-" + i + ".png");
                document.add(tmp_img);
            }
    }

    static void createImage(PDFPage page, String destination) throws IOException {
        Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(),
                (int) page.getBBox().getHeight());
        BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height,
                BufferedImage.TYPE_INT_RGB);

        java.awt.Image image = page.getImage(rect.width, rect.height,    // width & height
                rect,                       // clip rect
                null,                       // null for the ImageObserver
                true,                       // fill background with white
                true                        // block until drawing is done
        );
        Graphics2D bufImageGraphics = bufferedImage.createGraphics();
        bufImageGraphics.drawImage(image, 0, 0, null);
        ImageIO.write(bufferedImage, "PNG", new File(destination));
    }

    static void addImageWatermark(File watermarkImageFile, File sourceImageFile, File destImageFile, Integer postion, boolean p) {
        try {
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

            // initializes necessary graphic properties
            Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
            AlphaComposite alphaChannel;
            
            if(p) {
                alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
            }else {
                alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f);
            }
            g2d.setComposite(alphaChannel);

            // calculates the coordinate where the image is painted
            int topLeftX = (sourceImage.getWidth() - watermarkImage.getWidth()) / 1;
            int topLeftY = (sourceImage.getHeight() - watermarkImage.getHeight()) / 1;

            // paints the image watermark
            g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

            ImageIO.write(sourceImage, "png", destImageFile);
            g2d.dispose();

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}