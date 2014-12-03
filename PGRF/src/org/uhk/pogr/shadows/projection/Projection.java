package org.uhk.pogr.shadows.projection;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * Trida, ktera spousti projekcni metodu vrzneych stinu
 * 
 * http://www.it.hiof.no/~borres/gb/exp-shadow/p-shadow.html
 */

public class Projection extends JFrame {

	private static final long serialVersionUID = 5678632230535099687L;
	
	private JButton keyHelp;
    private JLabel lightPosition;
    private GLJPanel panel;
	
	private GLRenderer glr;
    private KeyboardDialog kd;

    public Projection(/*Window opener*/) {
        //super(opener);
    	initComponents();
        
        glr=new GLRenderer(lightPosition);
        panel.addGLEventListener(glr);
        panel.addKeyListener(glr);
        kd = new KeyboardDialog(this);
    }

    private void initComponents() {

        panel = new GLJPanel(createGLCapabilites());
        lightPosition = new JLabel();
        keyHelp = new JButton();

        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 396, Short.MAX_VALUE));
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 397, Short.MAX_VALUE));

        lightPosition.setText("jLabel1");

        keyHelp.setText("Keyboard use");
        keyHelp.setFocusable(false);
        keyHelp.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent evt) {
            	kd.setVisible(true);
            }
        });

        setLayout(new FormLayout("l:p:g, f:p:g, r:p:g", "p:g,6dlu,p:g"));
        CellConstraints cc = new CellConstraints();
        
        add(lightPosition, cc.xy(1, 1));
        add(keyHelp, cc.xy(3, 1));
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
    	Projection p = new Projection();
    	p.setDefaultCloseOperation(EXIT_ON_CLOSE);
    	p.pack();
    	p.setVisible(true);
    }
}
