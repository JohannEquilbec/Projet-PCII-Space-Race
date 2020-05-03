package jeu;

import modele.Etat;
import view.ThreadAff;

public class Checkpoint extends Thread {
	private ThreadAff affi;
	private Etat etat;

	/**
	 * Associe le thread avec Etat et Affichage
	 * @param tAffi le {@link ThreadAffichage} responsable de l'affichage
	 * @param et l'etat
	 */
	public Checkpoint(ThreadAff tAffi, Etat et) {
		this.etat = et;
		this.setAffi(tAffi);
	}

	/*
	public void incrDistance() {
		distanceCheckpoint += 500; 
	}
	 */

	/**
	 * Definit la fonction run :
	 * Une boucle (s'arretant lors du gameover) qui definit si un checkpoint est sur la piste
	 * @param g 
	 */
	public void run() {
		while (!etat.isQuit()) {
			if (!etat.isPause() && !etat.isGameOver()) {
				if (etat.piste.waitCheck == false) {
					etat.piste.debutCheckpoint();
				}
				if (etat.piste.afficheMessage == true) {
					try {
						Thread.sleep(3000);
						etat.piste.afficheMessage = false;
						etat.piste.isCheckpoint = true;
						Thread.sleep(6000);
						etat.piste.waitCheck = false;
						etat.piste.isCheckpoint = false;
						etat.piste.reinitialiseValeurs();
						etat.piste.nouveauCheckpoint();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public ThreadAff getAffi() {
		return affi;
	}

	public void setAffi(ThreadAff affi) {
		this.affi = affi;
	}
}
