package org.uhk.pogr.shadows;

import java.awt.Window;

import javax.swing.JPanel;

public class ShadowPanel extends JPanel{

	private static final long serialVersionUID = 8056680551977062035L;

	protected Window opener;
	
	public ShadowPanel(Window opener) {
		this.opener = opener;
	}
	
}
