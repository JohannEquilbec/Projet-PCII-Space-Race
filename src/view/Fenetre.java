package view;

import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * Crée une fenetre défaut
 */
public class Fenetre {

	/**
	 * Crée une fenetre par défaut
	 * @return renvoie la fenetre defaut crée {@link JFrame}
	 */
	public static JFrame newJFrame() {
		//Nouvelle fenetre
		JFrame fenetre = new JFrame("Race");
		
		//Vrai plein-écran
		if (Affichage.fullscreen) {
			fenetre.setUndecorated(true);
		}
		
		//Cache le curseur
		fenetre.setCursor(fenetre.getToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new Point(), null));
		
		//Définir comportement face à  fermeture
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Assembler
		fenetre.pack();
		//Rendre visible
		fenetre.setVisible(true);
		return fenetre;
	}
}