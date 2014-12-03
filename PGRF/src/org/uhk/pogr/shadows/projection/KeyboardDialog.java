package org.uhk.pogr.shadows.projection;

import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
 * How to control triangle and light:
 *
 * Arrow up:    Rotate triangle around x-axis in negative z-direction
 * Arrow down:  Rotate triangle around x-axis in positive z-direction
 * Arrow left:  Rotate triangle around z-axis in negative x-direction
 * Arrow right: Rotate triangle around z-axis in positive x-direction
 * D or d key:  Move triangle down
 * U or u key:  Move triangle up
 * L or l key:  Move triangle left
 * R or r key:  Move triangle right
 * A or a key:  Move triangle away (negative z-axis)
 * T or t key:  Move triangle towards (positive z-axis)
 * F1:          Move light left (negative x-axis)
 * F2:          Move light right (positive x-axis)
 * F3:          Move light away (negative z-axis)
 * F4:          Move light towards (positive z-axis)
 */
public class KeyboardDialog extends JDialog {

	private static final long serialVersionUID = 4715210213823663619L;

	private JButton ok;
    private JTextArea help;
	
	public KeyboardDialog(Window parent) {
		super(parent, ModalityType.APPLICATION_MODAL);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        help = new JTextArea();
        ok = new JButton();

        help.setColumns(20);
        help.setRows(5);
        help.setText(" Arrow up:    Rotate triangle around x-axis in negative z-direction\n Arrow down:  Rotate triangle around x-axis in positive z-direction\n Arrow left:  Rotate triangle around z-axis in negative x-direction\n Arrow right: Rotate triangle around z-axis in positive x-direction\n D or d key:  Move triangle down\n U or u key:  Move triangle up\n L or l key:  Move triangle left\n R or r key:  Move triangle right\n A or a key:  Move triangle away (negative z-axis)\n T or t key:  Move triangle towards (positive z-axis)\n F1:          Move light left (negative x-axis)\n F2:          Move light right (positive x-axis)\n F3:          Move light away (negative z-axis)\n F4:          Move light towards (positive z-axis)\n");

        ok.setText("ok");
        ok.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
            	KeyboardDialog.this.setVisible(false);
            }
        });
        setTitle("keyboard control");

        setLayout(new FormLayout("f:230dlu:g", "f:190dlu:g,3dlu,p"));
        CellConstraints cc = new CellConstraints();
        
        add(new JScrollPane(help), cc.xy(1, 1));
        add(ok, cc.xy(1, 3));
        pack();
    }
}
