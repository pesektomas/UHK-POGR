package org.uhk.pogr;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PGRA {

	private JFrame mainFrame;
	
	private JButton openMoreNormal;
	private JButton openShadowVolume;
	
	public PGRA() {
		mainFrame = new JFrame("PGRF");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new FormLayout("3dlu,f:200dlu:g,3dlu","3dlu,f:40dlu:g,3dlu,f:40dlu:g,3dlu"));
		CellConstraints cc = new CellConstraints();
		
		openMoreNormal = new JButton(moreNormal);
		openShadowVolume = new JButton(shadowVolume);
		
		mainFrame.add(openMoreNormal, cc.xy(2, 2));
		mainFrame.add(openShadowVolume, cc.xy(2, 4));
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private Action moreNormal = new AbstractAction("More normal shadow") {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			JDialog dlg = new JDialog(mainFrame);
			dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			//dlg.add(new Projection(mainFrame));
			dlg.pack();
			dlg.setVisible(true);
		}
	};
	
	private Action shadowVolume = new AbstractAction("Shadow volume") {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			JDialog dlg = new JDialog(mainFrame);
			dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			//dlg.add(new StencilPanel(mainFrame));
			dlg.pack();
			dlg.setVisible(true);
		}
	};
	
	public static void main(String[] args) {
		new PGRA();
	}
}
