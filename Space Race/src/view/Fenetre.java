package view;

import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * Cr�e une fenetre d�faut
 */
public class Fenetre extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Cr�e une fenetre par d�faut
	 */
	public Fenetre() {
		//Nouvelle fenetre
		super("Race");
		
		//Vrai plein-�cran
		if (Affichage.fullscreen) {
			this.setUndecorated(true);
		}
		
		//Cache le curseur
		this.setCursor(this.getToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new Point(), null));
		
		//D�finir comportement face � fermeture
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Assembler
		this.pack();
		//Rendre visible
		this.setVisible(true);
	}
}