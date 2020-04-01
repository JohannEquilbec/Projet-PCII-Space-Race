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
	 * Crée {@link Etat}, {@link Affichage} et {@link Control}
	 * @param args les arguments du main
	 */
	public static void main(String[] args) {
		//Nouvelle fenetre
		JFrame fenetre = Fenetre.newJFrame();
		Etat etat = new Etat();
		Affichage affichage = new Affichage(etat);
		
		//Ajoute la vue
		fenetre.add(affichage);
		
		//Assembler
		fenetre.pack();
		
		//Ajoute les controles
		new ControlKey(fenetre, etat);
		
		ThreadAff affi = new ThreadAff(affichage, etat);
		etat.setThreadAff(affi);
		
		//Lancement des threads
		affi.start();
		(new Avancer(affi, etat)).start();
		(new Accelerer(affi, etat)).start();
		(new DiminuerTemps(etat)).start();
	}
}