package jeu;

import modele.Etat;
import view.ThreadAff;

/**
 * Thread responsable de l'avancement
 */
public class Avancer extends Thread {
	private ThreadAff affi;
	private Etat etat;
	private static final int tempsAvancement = 20;
	
	/**
	 * Associe le thread avec {@link Etat} et {@link ThreadAffichage}
	 * @param tAffi le ThreadAffichage responsable de l'affichage
	 * @param et l'etat
	 */
	public Avancer(ThreadAff tAffi, Etat et) {
		this.etat = et;
		this.affi = tAffi;
	}
	
	/**
	 * Définit la fonction run :
	 * Une boucle (s'arretant lors du gameover) qui fait avancer le décors
	 */
	@Override
	public void run() {
		while (!etat.isPerdu()) {
			if (!etat.isPause()) {
				etat.piste.avance(etat);
				affi.redraw();
			}
			try {
				Thread.sleep(tempsAvancement);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}