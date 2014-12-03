package org.uhk.pogr.shadows.stencil;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 *
 * @author tomas pesek, http://www.uhk.cz
 * ,
 * http://www.it.hiof.no/~borres/gb/exp-shadow/p-shadow.html
 */

public class Stencil extends JFrame {

	private static final long serialVersionUID = 5678632230535099687L;
	
    private JLabel lightPosition;
    private GLJPanel panel;
	
	private GLRenderer glr;

    /** Creates new form MainFrame */
    public Stencil(/*Window opener*/) {
        //super(opener);
    	initComponents();
        
        glr=new GLRenderer(lightPosition);
        panel.addGLEventListener(glr);
        //panel.addKeyListener(glr);
    }

    private void initComponents() {

        panel = new GLJPanel(createGLCapabilites());
        lightPosition = new JLabel();

        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 396, Short.MAX_VALUE));
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 397, Short.MAX_VALUE));

        lightPosition.setText("jLabel1");

        setLayout(new FormLayout("l:p:g, f:p:g, r:p:g", "p:g,6dlu,p:g"));
        CellConstraints cc = new CellConstraints();
        
        add(lightPosition, cc.xy(1, 1));
        add(panel, cc.xyw(1, 3, 3));
    }

    private GLCapabilities createGLCapabilites() {
        
        GLCapabilities capabilities = new GLCapabilities(null);
        capabilities.setHardwareAccelerated(true);

        // try to enable 2x anti aliasing - should be supported on most hardware
        capabilities.setNumSamples(2);
        capabilities.setSampleBuffers(true);
        
        return capabilities;
    }
    
    public static void main(String[] a){
    	Stencil s = new Stencil();
    	s.setDefaultCloseOperation(EXIT_ON_CLOSE);
    	s.pack();
    	s.setVisible(true);
    }
}
