package jeu;

import modele.Etat;
import view.ThreadAff;

/**
 * Thread responsable de l'acceleration
 */
public class Accelerer extends Thread {
	private ThreadAff affi;
	private Etat etat;
	private static final int frame = 40;
	
	/**
	 * Associe le thread avec {@link Etat} et {@link ThreadAffichage}
	 * @param tAffi le ThreadAffichage responsable de l'affichage
	 * @param et l'etat
	 */
	public Accelerer(ThreadAff tAffi, Etat et) {
		this.etat = et;
		this.affi = tAffi;
	}
	
	/**
	 * Définit la fonction run :
	 * Une boucle (s'arretant lors du gameover) qui fait augmenter la vitesse du vaiseau
	 */
	@Override
	public void run() {
		while (!etat.isPerdu()) {
			if (!etat.isPause()) {
				etat.vaisseau.accelere();
				affi.redraw();
			}
			try {
				Thread.sleep(frame);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}