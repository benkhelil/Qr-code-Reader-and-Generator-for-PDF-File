package qrgenerator;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.PropertiesManager;
/**
 *
 * @author HYDRA tech
 */
public class display_pdf extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    SwingController control=new SwingController();
    public display_pdf() {
        initComponents();
        jScrollPane1.setViewportView(openpdf("pdf-source/pdf-4.pdf"));
         
      
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(299, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(display_pdf.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 display_pdf display_pdff = new display_pdf ();
                  display_pdff.pack();
                display_pdff.setVisible(true);
            }
        });
    }
int get_controller_page(JTextField s) {
    System.out.println(""+control.getCurrentPageNumber());
    control.setCurrentPageNumberTextField(s);
    return control.getCurrentPageNumber();
}
JPanel openpdf(String file){
   
    
           
          
             // Construct a PropertiesManager from the default properties file and default message bundle
            PropertiesManager properties =
                    new PropertiesManager(System.getProperties(),
                                          ResourceBundle.getBundle(PropertiesManager.DEFAULT_MESSAGE_BUNDLE));

            // Change the value of a couple default viewer Properties.
            // Note: this should be done before the factory is initialized.
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ANNOTATION,
                    Boolean.FALSE);
            properties.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT,
                    Boolean.FALSE);  
            SwingViewBuilder factry = new SwingViewBuilder(control, properties);
            
             // add interactive mouse link annotation support via callback
            control.getDocumentViewController().setAnnotationCallback(
                    new org.icepdf.ri.common.MyAnnotationCallback(
                            control.getDocumentViewController()));
            
            JPanel veiwerCompntpnl=factry.buildViewerPanel();
            ComponentKeyBinding.install(control, veiwerCompntpnl);control.getDocumentViewController().setAnnotationCallback(new org.icepdf.ri.common.MyAnnotationCallback(control.getDocumentViewController()));
            
            control.openDocument(file);
             control.zoomOut();control.zoomOut();control.zoomOut();control.zoomOut();
             
             return veiwerCompntpnl;
   
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
