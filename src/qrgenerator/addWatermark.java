/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrgenerator;


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

/**
 *
 * @author HYDRA tech
 */
public class addWatermark {
    static void addImageWatermark(File watermarkImageFile, File sourceImageFile, File destImageFile) {
    try {
        BufferedImage sourceImage = ImageIO.read(sourceImageFile);
        BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);
 
        // initializes necessary graphic properties
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2d.setComposite(alphaChannel);
 
        // calculates the coordinate where the image is painted
        int topLeftX = (sourceImage.getWidth() - watermarkImage.getWidth()) / 3;
        int topLeftY = (sourceImage.getHeight() - watermarkImage.getHeight()) / 3;
 
        // paints the image watermark
        g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);
 
        ImageIO.write(sourceImage, "png", destImageFile);
        g2d.dispose();
 
        System.out.println("The image watermark is added to the image.");
 
    } catch (IOException ex) {
        System.err.println(ex);
    }
}
    
        public static void main(String[] args) throws IOException {
            File sourceImageFile = new File("d:\\PICTUREe_11.jpg");
            File watermarkImageFile = new File("d:\\qr.png");
            File destImageFile = new File("d:\\PICTUREe1.jpg");

            addImageWatermark(watermarkImageFile, sourceImageFile, destImageFile);
    }
}
