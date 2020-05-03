package jeu;

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
		Fenetre fenetre = new Fenetre();
		Etat etat = new Etat();
		Affichage affichage = new Affichage(etat);
		
		//Ajoute la vue
		fenetre.add(affichage);
		
		//Assembler
		fenetre.pack();
		
		ThreadAff affi = new ThreadAff(affichage, etat);
		etat.setThreadAff(affi);
		
		//Ajoute les controles
		new ControlKey(fenetre, etat);
		
		//Lancement des threads
		affi.start();
		(new Avancer(affi, etat)).start();
		(new Accelerer(affi, etat)).start();
		(new DiminuerTemps(etat)).start();
		(new Checkpoint(affi, etat)).start();
	}
}
