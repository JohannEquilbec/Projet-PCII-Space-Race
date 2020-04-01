package jeu;

import modele.Etat;

/**
 * Thread responsable du temps qui passe
 */
public class DiminuerTemps extends Thread {
	private Etat etat;
	
	/**
	 * Associe le thread avec {@link Etat} et {@link ThreadAffichage}
	 * @param tAffi le ThreadAffichage responsable de l'affichage
	 * @param et l'etat
	 */
	public DiminuerTemps(Etat et) {
		this.etat = et;
	}
	
	/**
	 * Définit la fonction run :
	 * Une boucle (s'arretant lors du gameover) qui fait diminuer le temps de 1 seconde (sauf en cas de turbo, une seconde passe tout les 1/4 de secondes)
	 */
	@Override
	public void run() {
		while (!etat.isPerdu()) {
			if (!etat.isPause()) {
				etat.vaisseau.perdVie();
			}
			try {
				//Si il y a le turbo, le temps diminue plus
				Thread.sleep(etat.vaisseau.boost? 250 : 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}