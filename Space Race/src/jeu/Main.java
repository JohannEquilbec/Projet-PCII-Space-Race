package jeu;

import javax.swing.JFrame;

import control.ControlKey;
import modele.Etat;
import view.Affichage;
import view.Fenetre;
import view.ThreadAff;

/**
 * Main du programme
 */
public class Main {
	
	/**
	 * Cree {@link Etat}, {@link Affichage} et {@link Control}
	 * @param args les arguments du main
	 */
	public static void main(String[] args) {
		//Nouvelle fenetre
		JFrame fenetre = Fenetre.newJFrame();
		Etat etat = new Etat();
		Affichage affichage = new Affichage(etat);
		//new ControlKey(affichage, etat);
		//affichage.setFocusable(true);
		//Ajoute la vue
		fenetre.add(affichage);
		
		new ControlKey(fenetre, etat);
		
		ThreadAff affi = new ThreadAff(affichage, etat);
		etat.setThreadAff(affi);
		affi.start();
		(new Avancer(affi, etat)).start();
		(new Accelerer(affi, etat)).start();
		(new DiminuerTemps(etat)).start();
		(new Checkpoint(affi, etat)).start();
		
		//Assembler
		fenetre.pack();
	}
}