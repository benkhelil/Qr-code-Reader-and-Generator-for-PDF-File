/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrgenerator;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.sun.pdfview.PDFFile;
import java.awt.Image;
import static java.awt.SystemColor.control;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import static org.icepdf.ri.common.FileExtensionUtils.pdf;
import static qrgenerator.pdf_gestion.convert_pdf_to_image;
import static qrgenerator.pdf_gestion.create_new_pdf;
import static qrgenerator.pdf_gestion.put_qr_into_pdf_imgs;
/**
 *
 * @author Benkhelil Khalil
 */
public class QrGen extends javax.swing.JFrame {
private boolean fileChoosed = false;
    private static String text_to_generate = "";
    private static String path = "";
    private static String pdfPath = "";
    Connection conn=null;
    ResultSet rs=null;
    PreparedStatement pst=null;
    display_pdf displayPdff = new display_pdf();
    pdf_gestion testin = new pdf_gestion();

    public void close(){
        WindowEvent winClosingEvent = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }   

    public ImageIcon ResizeImage(String imgPath) {
        ImageIcon MyImage = new ImageIcon(imgPath);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(imageqr.getWidth(), imageqr.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);
        return image;
    }
    
   ////// generate qr code //////////// 
    public void AddRow(String str1, String str2, JTable table){
        text_to_generate="";
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{str1, str2});    
    }

     
    private void Update_table_add(JTextField text1, JTextField text2, JTable table) {
        AddRow(text1.getText(),text2.getText(),table); 
    }
    public void modifyItem(JTable table) {
             
       DefaultTableModel model = (DefaultTableModel) table.getModel();
       int row = table.getSelectedRow();
       
         model.removeRow(row);
         AddRow(text1.getText(),text2.getText(),table);
       }
        public void modifyItem1(JTable table) {
             
       DefaultTableModel model = (DefaultTableModel) table.getModel();
       int row = table.getSelectedRow();
       
         model.removeRow(row);
         AddRow(text3.getText(),text4.getText(),table);
       }
    public void removeSelectedRows(JTable table){
       text_to_generate = "";
       DefaultTableModel model = (DefaultTableModel) table.getModel();
       int[] rows = table.getSelectedRows();
       for(int i=0;i<rows.length;i++){
         model.removeRow(rows[i]-i);
       }
    }
     
    public void removeAllRows(JTable table){
       //text_to_generate = "";
       DefaultTableModel dm = (DefaultTableModel) table.getModel();
        int rowCount = dm.getRowCount();
        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            dm.removeRow(i);
        }
    }
    
    public void getTextGen(JTable table) {
        int row = table.getRowCount();
        int column = table.getColumnCount();
        for (int j = 0; j  < row; j++) {
            //for (int i = 0; i  < column; i++) {
                text_to_generate += table.getValueAt(j, 0)+"  :  ";
                text_to_generate += table.getValueAt(j, 1)+" |";                
            //}
        }
    }

    public static void path() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("/home/me/Documents"));
        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile()+".PNG";
            
   /*     try(FileWriter fw = new FileWriter(chooser.getSelectedFile()+".PNG")) {
            fw.write(sb.toString());
            fw.close();
        } catch (Exception ex) {
                ex.printStackTrace();
            }*/
        } 
  
    }
    public static String pathPdf() {
            String pdfPath="";
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("/home/me/Documents"));
        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            pdfPath = chooser.getSelectedFile()+".PDF";
            
   /*     try(FileWriter fw = new FileWriter(chooser.getSelectedFile()+".PNG")) {
            fw.write(sb.toString());
            fw.close();
        } catch (Exception ex) {
                ex.printStackTrace();
            }*/
        }
    return pdfPath;
    }
        private static void qrGen() {
            path();
            ByteArrayOutputStream out = QRCode.from(text_to_generate).to(ImageType.PNG).stream();

            try {
                 try (FileOutputStream fout = new FileOutputStream(new File(path))) {
                     fout.write(out.toByteArray());
                     fout.flush();
                 }

            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }   
}
        private static void qrGen1() {
            
            ByteArrayOutputStream out = QRCode.from(text_to_generate).to(ImageType.PNG).stream();

            try {
                 try (FileOutputStream fout = new FileOutputStream(new File("tmp/qr.png"))) {
                     fout.write(out.toByteArray());
                     fout.flush();
                 }

            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }   
}        
        
     ////////// read qr code ///////////////
    private static String decodeQRCode(File qrCodeimage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }
    
    private ArrayList<String> meto_split(String str , char separator){
        String s1="";
        ArrayList<String> table = new ArrayList<>();
        for(int i=0; i<str.length();i++){
            if(str.charAt(i)==separator){
                table.add(s1);
                s1="";                
            } else{
                s1 +=  str.charAt(i);
            }
        }
        return table;
    }
    
    private void afficherQrCode(String str) {
        ArrayList<String> table1 = new ArrayList<>();
        table1 = this.meto_split(str, '|');

        String s ="";
        for(int i=0; i<table1.size();i++){
            System.out.print(table1.get(i)+"\n");
            s += table1.get(i)+"\n";
        }
        textReadQr.setText(s);
    }
    
    private void readQr() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File file = chooser.getSelectedFile();
            String ppath =file.getAbsolutePath();
            
           // File file = new File("MyQRCode.png");
            String decodedText = decodeQRCode(file);
            
            if(decodedText == null) {
                System.out.println("No QR Code found in the image");
            } else {
                afficherQrCode(decodedText);
                imageqr.setIcon(ResizeImage(ppath));
            }
        } catch (IOException e) {
            System.out.println("Could not decode QR Code, IOException :: " + e.getMessage());
        }
        
    }
    
    private String readPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        return file.getAbsolutePath();
    }
    
    private void Update_table_ad(String text1, String text2, JTable table) {
        AddRow(text1,text2,table); 
    }
    private void selectModel( JTable tablee,JComboBox<String> model) {
        Utils a = new Utils();
      
        //System.out.println("Hi ");
        for (int j = 0; j < a.getAttr(model.getSelectedItem().toString()).size(); j++) {
            
            AddRow(a.getAttr(model.getSelectedItem().toString()).get(j),"", tablee);
        }
    }
    private void setParam(JTable table,JTextField text1, JTextField text2) {
               int row = table.getSelectedRow();

        String click1 = (table.getModel().getValueAt(row, 0).toString());
        String click2 = (table.getModel().getValueAt(row, 1).toString());
        text1.setText(click1);
        text2.setText(click2);
    }
    private void add_model(ArrayList<String> stringArrayList) {
        for (int j = 0; j < stringArrayList.size(); j++) {
            System.out.println("\t " + stringArrayList.get(j));
        }
    //    int row = table.getSelectedRow();

       // String click1 = (table.getModel().getValueAt(row, 0).toString());
        //String click2 = (table.getModel().getValueAt(row, 1).toString());
        //text1.setText(click1);
        //text2.setText(click2);
    }
    /**
     * Creates new form QrGen
     */
    public QrGen() {
        initComponents();
        etape1.setVisible(false);
        etape2.setVisible(false);
        write.setForeground(new java.awt.Color(20, 20, 20));
        read.setForeground(new java.awt.Color(20, 20, 20));
        etape1.setForeground(new java.awt.Color(20, 20, 20));
        etape2.setForeground(new java.awt.Color(20, 20, 20));
        add.setForeground(new java.awt.Color(20, 20, 20));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu = new javax.swing.JPanel();
        add = new javax.swing.JLabel();
        write = new javax.swing.JLabel();
        read = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        etape1 = new javax.swing.JLabel();
        etape2 = new javax.swing.JLabel();
        FirstCardLayot = new javax.swing.JPanel();
        qrGen = new org.jdesktop.swingx.JXPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        jXLabel2 = new org.jdesktop.swingx.JXLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        text1 = new javax.swing.JTextField();
        text2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        model = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        qrRead = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        imageqr = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        textReadQr = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fileSelector = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jButton7 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        done = new javax.swing.JTextField();
        qrGen2 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        qrGen1 = new org.jdesktop.swingx.JXPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();
        jXPanel3 = new org.jdesktop.swingx.JXPanel();
        jXLabel3 = new org.jdesktop.swingx.JXLabel();
        jXLabel4 = new org.jdesktop.swingx.JXLabel();
        jButton9 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        text3 = new javax.swing.JTextField();
        text4 = new javax.swing.JTextField();
        jButton15 = new javax.swing.JButton();
        model1 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        imageqr1 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jButton13 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        add.setBackground(new java.awt.Color(0, 0, 0));
        add.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        add.setText("Ajouter le QR à un fichier");
        add.setToolTipText("");
        add.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        add.setFocusCycleRoot(true);
        add.setFocusTraversalPolicy(null);
        add.setFocusTraversalPolicyProvider(true);
        add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addMouseExited(evt);
            }
        });

        write.setBackground(new java.awt.Color(0, 0, 0));
        write.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        write.setText("Lire un code QR");
        write.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        write.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                writeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                writeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                writeMouseExited(evt);
            }
        });

        read.setBackground(new java.awt.Color(0, 0, 0));
        read.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        read.setText("Générer un code QR");
        read.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        read.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                readMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                readMouseExited(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qrgenerator/logo.png"))); // NOI18N

        etape1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        etape1.setText("Etape 1");
        etape1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        etape1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                etape1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                etape1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                etape1MouseExited(evt);
            }
        });

        etape2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        etape2.setText("Etape 2");
        etape2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        etape2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                etape2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                etape2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                etape2MouseExited(evt);
            }
        });

        javax.swing.GroupLayout menuLayout = new javax.swing.GroupLayout(menu);
        menu.setLayout(menuLayout);
        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(add)
                .addGap(25, 25, 25))
            .addGroup(menuLayout.createSequentialGroup()
                .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(read))
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(write))
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(etape1)
                            .addComponent(etape2))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(menuLayout.createSequentialGroup()
                    .addComponent(jLabel6)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addGap(221, 221, 221)
                .addComponent(write)
                .addGap(18, 18, 18)
                .addComponent(read)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etape1)
                .addGap(13, 13, 13)
                .addComponent(etape2)
                .addContainerGap(170, Short.MAX_VALUE))
            .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 541, Short.MAX_VALUE))
        );

        FirstCardLayot.setLayout(new java.awt.CardLayout());

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Paramètre", "Valeur"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(table);

        jXLabel1.setText("Nom du paramètre");

        jXLabel2.setText("Valeur");

        jButton1.setText("Ajouter un paramètre");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Generate QRCode");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Supprimer un paramètre");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        text1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text1ActionPerformed(evt);
            }
        });

        jLabel3.setText("All Rights Reserved @Benkhelil Khalil & Metourni Nourredine");

        jButton14.setText("Modifier la valeur");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXPanel2Layout = new javax.swing.GroupLayout(jXPanel2);
        jXPanel2.setLayout(jXPanel2Layout);
        jXPanel2Layout.setHorizontalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel2Layout.createSequentialGroup()
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel2Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jXLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jXLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(63, 63, 63)
                                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(text1)
                                    .addComponent(text2, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)))
                            .addGroup(jXPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)))
                .addContainerGap())
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jXPanel2Layout.setVerticalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jXLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(text1, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(text2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addGap(15, 15, 15)
                .addComponent(jButton14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addGap(70, 70, 70)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qrgenerator/qrgenerator.PNG"))); // NOI18N

        model.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Facture de Gateau", "Pointage", "Canva" }));
        model.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                modelItemStateChanged(evt);
            }
        });
        model.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelActionPerformed(evt);
            }
        });
        model.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                modelPropertyChange(evt);
            }
        });

        jLabel5.setText("Choisir un modèle:");

        jButton5.setText("Ajouter un nouveau modèle");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout qrGenLayout = new javax.swing.GroupLayout(qrGen);
        qrGen.setLayout(qrGenLayout);
        qrGenLayout.setHorizontalGroup(
            qrGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(qrGenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(qrGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(qrGenLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrGenLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
                        .addGroup(qrGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrGenLayout.createSequentialGroup()
                                .addComponent(jButton5)
                                .addGap(93, 93, 93))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrGenLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(37, 37, 37)
                                .addComponent(model, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47))))))
            .addGroup(qrGenLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 2, Short.MAX_VALUE))
        );
        qrGenLayout.setVerticalGroup(
            qrGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(qrGenLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(qrGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrGenLayout.createSequentialGroup()
                        .addComponent(jXPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrGenLayout.createSequentialGroup()
                        .addGroup(qrGenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(model, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        FirstCardLayot.add(qrGen, "card2");

        jButton4.setText("Choisir un code QR");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        textReadQr.setColumns(20);
        textReadQr.setRows(5);
        jScrollPane3.setViewportView(textReadQr);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qrgenerator/qrreader.PNG"))); // NOI18N

        jLabel4.setText("All Rights Reserved @Benkhelil Khalil & Metourni Nourredine");

        javax.swing.GroupLayout qrReadLayout = new javax.swing.GroupLayout(qrRead);
        qrRead.setLayout(qrReadLayout);
        qrReadLayout.setHorizontalGroup(
            qrReadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrReadLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(qrReadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrReadLayout.createSequentialGroup()
                        .addComponent(imageqr, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrReadLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(204, 204, 204))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrReadLayout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(282, 282, 282))))
        );
        qrReadLayout.setVerticalGroup(
            qrReadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrReadLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGroup(qrReadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(qrReadLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(imageqr, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(qrReadLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(18, 18, 18)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        FirstCardLayot.add(qrRead, "card3");

        jButton6.setText("Choisir un fichier");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jScrollPane2.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jScrollPane2MouseWheelMoved(evt);
            }
        });

        jButton7.setText("Suivant");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel7.setText("Le code va être ajouté à la page N° : ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(jButton6)
                .addGap(61, 61, 61)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(done, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addGap(37, 37, 37))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton6)
                        .addComponent(jLabel7))
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(done))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout fileSelectorLayout = new javax.swing.GroupLayout(fileSelector);
        fileSelector.setLayout(fileSelectorLayout);
        fileSelectorLayout.setHorizontalGroup(
            fileSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileSelectorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        fileSelectorLayout.setVerticalGroup(
            fileSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileSelectorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        FirstCardLayot.add(fileSelector, "card4");

        jButton8.setText("jButton8");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout qrGen2Layout = new javax.swing.GroupLayout(qrGen2);
        qrGen2.setLayout(qrGen2Layout);
        qrGen2Layout.setHorizontalGroup(
            qrGen2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrGen2Layout.createSequentialGroup()
                .addContainerGap(634, Short.MAX_VALUE)
                .addComponent(jButton8)
                .addGap(60, 60, 60))
        );
        qrGen2Layout.setVerticalGroup(
            qrGen2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(qrGen2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jButton8)
                .addContainerGap(477, Short.MAX_VALUE))
        );

        FirstCardLayot.add(qrGen2, "card5");

        jScrollPane4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane4MouseClicked(evt);
            }
        });
        jScrollPane4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jScrollPane4KeyReleased(evt);
            }
        });

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Paramètre", "Valeur"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table1MouseClicked(evt);
            }
        });
        table1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table1KeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(table1);

        jXLabel3.setText("Nom du paramètre");

        jXLabel4.setText("Valeur");

        jButton9.setText("Ajouter un paramètre");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton11.setText("Supprimer un paramètre");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        text3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXPanel3Layout = new javax.swing.GroupLayout(jXPanel3);
        jXPanel3.setLayout(jXPanel3Layout);
        jXPanel3Layout.setHorizontalGroup(
            jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel3Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel3Layout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11))
                    .addGroup(jXPanel3Layout.createSequentialGroup()
                        .addGroup(jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jXLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jXLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(63, 63, 63)
                        .addGroup(jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(text3)
                            .addComponent(text4, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)))))
        );
        jXPanel3Layout.setVerticalGroup(
            jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jXLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(text3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(text4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton15.setText("Modifier la valeur");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(jButton15)
                .addContainerGap(161, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jXPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton15)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jXPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(39, Short.MAX_VALUE)))
        );

        model1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Facture de Gateau", "Canva", "Pointage" }));
        model1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                model1ActionPerformed(evt);
            }
        });

        jLabel10.setText("Choisir un modèle:");

        jButton12.setText("Ajouter un nouveau modèle");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel8.setText("All Rights Reserved @Benkhelil Khalil & Metourni Nourredine");

        jButton10.setText("Generate QRCode");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        imageqr1.setBackground(new java.awt.Color(153, 153, 153));
        imageqr1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qrLogo.jpg"))); // NOI18N

        jButton13.setText("Générer le fichier PDF");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout qrGen1Layout = new javax.swing.GroupLayout(qrGen1);
        qrGen1.setLayout(qrGen1Layout);
        qrGen1Layout.setHorizontalGroup(
            qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(qrGen1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton13)
                .addGap(123, 123, 123))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrGen1Layout.createSequentialGroup()
                .addGroup(qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(qrGen1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qrGen1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator4))
                    .addGroup(qrGen1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(qrGen1Layout.createSequentialGroup()
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(qrGen1Layout.createSequentialGroup()
                                        .addGap(118, 118, 118)
                                        .addComponent(jButton10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE))
                                    .addGroup(qrGen1Layout.createSequentialGroup()
                                        .addGap(70, 70, 70)
                                        .addComponent(imageqr1)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(qrGen1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(qrGen1Layout.createSequentialGroup()
                                        .addGap(35, 35, 35)
                                        .addComponent(jButton12))
                                    .addGroup(qrGen1Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addGap(37, 37, 37)
                                        .addComponent(model1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(56, 56, 56)))))
                .addContainerGap())
        );
        qrGen1Layout.setVerticalGroup(
            qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(qrGen1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(qrGen1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(model1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(qrGen1Layout.createSequentialGroup()
                                .addComponent(imageqr1)
                                .addGap(49, 49, 49)
                                .addComponent(jButton10)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE))
                    .addGroup(qrGen1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(qrGen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13))
                .addContainerGap())
        );

        FirstCardLayot.add(qrGen1, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 199, Short.MAX_VALUE)
                .addComponent(FirstCardLayot, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 768, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(FirstCardLayot, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

Update_table_add(text1,text2,table);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        removeSelectedRows(table);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void text1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        getTextGen(table);
        qrGen();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        readQr();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void writeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_writeMouseClicked
        textReadQr.setText("");
        FirstCardLayot.removeAll();
        FirstCardLayot.add(qrRead);
        FirstCardLayot.repaint();
        FirstCardLayot.revalidate();
        // TODO add your handling code here:
    }//GEN-LAST:event_writeMouseClicked

    private void readMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readMouseClicked
        FirstCardLayot.removeAll();
        FirstCardLayot.add(qrGen);
        FirstCardLayot.repaint();
        FirstCardLayot.revalidate();
        // TODO add your handling code here:
    }//GEN-LAST:event_readMouseClicked

    private void writeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_writeMouseEntered
write.setForeground(new java.awt.Color(122, 22, 225));
        // TODO add your handling code here:
    }//GEN-LAST:event_writeMouseEntered

    private void readMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readMouseEntered
read.setForeground(new java.awt.Color(122, 22, 225));        // TODO add your handling code here:
    }//GEN-LAST:event_readMouseEntered

    private void readMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readMouseExited
read.setForeground(new java.awt.Color(20, 20, 20));        // TODO add your handling code here:
    }//GEN-LAST:event_readMouseExited

    private void writeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_writeMouseExited
write.setForeground(new java.awt.Color(20, 20, 20));
        // TODO add your handling code here:
    }//GEN-LAST:event_writeMouseExited

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        add_model(Utils.getAttr(model.getSelectedItem().toString()));
        //new modele().setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void addMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseClicked
        textReadQr.setText("");
        FirstCardLayot.removeAll();
        FirstCardLayot.add(fileSelector);
        etape1.setVisible(true);
        FirstCardLayot.repaint();
        FirstCardLayot.revalidate();
        if(add.getForeground().equals(new java.awt.Color(122, 22, 225))) {
            add.setForeground(new java.awt.Color(21, 20, 20));
            etape1.setForeground(new java.awt.Color(123, 22, 225));
        }
        
    }//GEN-LAST:event_addMouseClicked

    private void addMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseEntered
        if(!add.getForeground().equals(new java.awt.Color(123, 22, 225))) {
            add.setForeground(new java.awt.Color(122, 22, 225));
        }
    }//GEN-LAST:event_addMouseEntered

    private void addMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseExited

        if(!add.getForeground().equals(new java.awt.Color(21, 20, 20))) {
            add.setForeground(new java.awt.Color(20, 20, 20));
        }
    }//GEN-LAST:event_addMouseExited

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        pdfPath = readPdf();
        jScrollPane2.setViewportView(displayPdff.openpdf(pdfPath));
        displayPdff.get_controller_page(done);
        fileChoosed = true;
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        if(fileChoosed && !done.getText().isEmpty()) {
        textReadQr.setText("");
        FirstCardLayot.removeAll();
        FirstCardLayot.add(qrGen1);
        FirstCardLayot.repaint();
        FirstCardLayot.revalidate();
        etape2.setVisible(true);
        etape1.setForeground(new java.awt.Color(20, 20, 20));
        etape2.setForeground(new java.awt.Color(123, 22, 225));
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed

    }//GEN-LAST:event_jButton8ActionPerformed

    private void jScrollPane2MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jScrollPane2MouseWheelMoved

    }//GEN-LAST:event_jScrollPane2MouseWheelMoved

    private void etape2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_etape2MouseEntered

    }//GEN-LAST:event_etape2MouseEntered

    private void etape2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_etape2MouseExited

    }//GEN-LAST:event_etape2MouseExited

    private void etape2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_etape2MouseClicked
        textReadQr.setText("");
        FirstCardLayot.removeAll();
        FirstCardLayot.add(qrGen1);
        FirstCardLayot.repaint();
        FirstCardLayot.revalidate();
    }//GEN-LAST:event_etape2MouseClicked

    private void etape1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_etape1MouseClicked
        textReadQr.setText("");
        FirstCardLayot.removeAll();
        FirstCardLayot.add(fileSelector);
        FirstCardLayot.repaint();
        FirstCardLayot.revalidate();
    }//GEN-LAST:event_etape1MouseClicked

    private void etape1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_etape1MouseEntered

    }//GEN-LAST:event_etape1MouseEntered

    private void etape1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_etape1MouseExited

    }//GEN-LAST:event_etape1MouseExited

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        Update_table_add(text3,text4,table1);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        getTextGen(table1);
        qrGen1();
        imageqr1.setIcon(ResizeImage("tmp/qr.png"));
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        removeSelectedRows(table1);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void text3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text3ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        String pathPDF = pathPdf();
        Document document = new Document();
        Integer pos = Integer.parseInt(done.getText());
                int res = pos;
        try {
            PDFFile pdf = pdf_gestion.read_pdf(pdfPath);
            pdf_gestion.convert_pdf_to_image(pdf);
            pdf_gestion.put_qr_into_pdf_imgs(pdf.getNumPages(),"tmp/qr.png",res);
            pdf_gestion.create_new_pdf(document,pdf.getNumPages(),pathPDF);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(QrGen.class.getName()).log(Level.SEVERE, null, ex);
        }
        document.close();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void modelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelActionPerformed
        removeAllRows(table);
        selectModel(table,model);        // TODO add your handling code here:
    }//GEN-LAST:event_modelActionPerformed

    private void modelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_modelItemStateChanged
      //  ArrayList<String> a = new ArrayList();
   
       
    }//GEN-LAST:event_modelItemStateChanged

    private void modelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_modelPropertyChange
       // TODO add your handling code here:
    }//GEN-LAST:event_modelPropertyChange

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        setParam(table,text1,text2); // TODO add your handling code here:
    }//GEN-LAST:event_tableMouseClicked

    private void tableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyReleased
      setParam(table,text1,text2);  // TODO add your handling code here:
    }//GEN-LAST:event_tableKeyReleased

    private void jScrollPane4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane4KeyReleased
       // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane4KeyReleased

    private void jScrollPane4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane4MouseClicked

    private void table1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table1KeyReleased
setParam(table1,text3,text4);         // TODO add your handling code here:
    }//GEN-LAST:event_table1KeyReleased

    private void table1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table1MouseClicked
setParam(table1,text3,text4);         // TODO add your handling code here:
    }//GEN-LAST:event_table1MouseClicked

    private void model1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_model1ActionPerformed
removeAllRows(table1);
        selectModel(table1,model1);         // TODO add your handling code here:
    }//GEN-LAST:event_model1ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
modifyItem(table);     // TODO add your handling code here:
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
modifyItem1(table1);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton15ActionPerformed

    /**
     * @param args the command line arguments
     */
public static void main(String args[]) throws UnsupportedLookAndFeelException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QrGen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QrGen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QrGen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QrGen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            javax.swing.UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(QrGen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                QrGen display_pdff = new QrGen ();
                  display_pdff.pack();
                display_pdff.setVisible(true);
            }
        });
        
        
        
        
        
        
        
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FirstCardLayot;
    private javax.swing.JLabel add;
    private javax.swing.JTextField done;
    private javax.swing.JLabel etape1;
    private javax.swing.JLabel etape2;
    private javax.swing.JPanel fileSelector;
    private javax.swing.JLabel imageqr;
    private javax.swing.JLabel imageqr1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXLabel jXLabel2;
    private org.jdesktop.swingx.JXLabel jXLabel3;
    private org.jdesktop.swingx.JXLabel jXLabel4;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private org.jdesktop.swingx.JXPanel jXPanel3;
    private javax.swing.JPanel menu;
    private javax.swing.JComboBox<String> model;
    private javax.swing.JComboBox<String> model1;
    private org.jdesktop.swingx.JXPanel qrGen;
    private org.jdesktop.swingx.JXPanel qrGen1;
    private javax.swing.JPanel qrGen2;
    private javax.swing.JPanel qrRead;
    private javax.swing.JLabel read;
    private javax.swing.JTable table;
    private javax.swing.JTable table1;
    private javax.swing.JTextField text1;
    private javax.swing.JTextField text2;
    private javax.swing.JTextField text3;
    private javax.swing.JTextField text4;
    private javax.swing.JTextArea textReadQr;
    private javax.swing.JLabel write;
    // End of variables declaration//GEN-END:variables
}
