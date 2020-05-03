package view;

import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * Crée une fenetre défaut
 */
public class Fenetre extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Crée une fenetre par défaut
	 */
	public Fenetre() {
		//Nouvelle fenetre
		super("Race");
		
		//Vrai plein-écran
		if (Affichage.fullscreen) {
			this.setUndecorated(true);
		}
		
		//Cache le curseur
		this.setCursor(this.getToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new Point(), null));
		
		//Définir comportement face à  fermeture
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Assembler
		this.pack();
		//Rendre visible
		this.setVisible(true);
	}
}