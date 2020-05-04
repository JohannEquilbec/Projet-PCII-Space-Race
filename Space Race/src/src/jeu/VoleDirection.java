package jeu;

import modele.Etat;

/**
 * Thread responsable du déplacement haut/bas/droite/gauche
 */
public class VoleDirection extends Thread {
	public Etat etat;
	public Etat.DIR dir;
	public boolean arreter = false;
	public static final int frame = 20;
	
	/**
	 * Associe le thread avec {@link Etat} et une direction
	 * @param tAffi le ThreadAffichage responsable de l'affichage
	 * @param dire la direction du thread
	 */
	public VoleDirection(Etat et, Etat.DIR dire) {
		this.dir = dire;
		this.etat = et;
	}
	
	/**
	 * Définit la fonction run :
	 * Une boucle (s'arretant lors du gameover ou avec arret() ) qui fait bouger le vaisseau régulierement dans la direction
	 */
	@Override
	public void run() {
		int i = 0;
		//Tant que le Thread ne s'arrete pas, on fait bouger le vaisseau
		while (!etat.isPerdu() && !arreter) {
			if (!etat.isPause()) {
				//i est le % de puissance
				//Au début, il est bas pour representer l'inertie initiale
				etat.vaisseau.vole(dir, i);
				if (i<100) {
					i+=3;
				} else if (i > 100) {
					i = 100;
				}
			}
			try {
				Thread.sleep(frame);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//Quand le Thread s'arrete, il fait l'inertie de fin de déplacement
		for (int j=i; j>0; j-=4) {
			etat.vaisseau.vole(dir, j);
			try {
				Thread.sleep(frame);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Fonction qui arrete le thread
	 */
	public void arret() {
		this.arreter = true;
	}
	
	/**
	 * Getter de la direction du thread
	 * @return dir la direction du thread
	 */
	public Etat.DIR getDir() {
		return this.dir;
	}
}
