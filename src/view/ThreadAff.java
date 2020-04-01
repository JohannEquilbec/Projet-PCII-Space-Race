package view;

import modele.Etat;

/**
 * Définit un thread qui redessine la {@link JFrame} régulierement
 */
public class ThreadAff extends Thread {
	private Boolean draw = true;
	private Affichage affichage;
	private Etat etat;
	
	/**
	 * Associe le thread avec {@link Etat} et {@link Affichage}
	 * @param aff l'affichage
	 * @param et l'etat
	 */
	public ThreadAff(Affichage aff, Etat et) {
		affichage = aff;
		etat = et;
	}
	
	/**
	 * Fonction qui sert à  savoir si il faut redessiner la fenetre (appelée par les autres fontions)
	 */
	public void redraw() {
		draw = true;
	}
	
	/**
	 * Actualise la fenetre régulierement si besoin
	 */
	@Override
	public void run() {
		while (!etat.isPerdu()) {
			if (draw) {
				affichage.repaint();
				draw = false;
			}
			try {
				Thread.sleep(1/24*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		affichage.repaint();
	}
}
