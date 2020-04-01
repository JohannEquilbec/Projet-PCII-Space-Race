package control;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import modele.Etat;

/**
 * Crée un Controleur qui gère les clics de souris
 */
public class ControlKey {
	private Etat etat;

	/**
	 * Crée le Controleur et le lie avec {@link JFrame} et {@link Etat}
	 * 
	 * @param aff l'affichage
	 * @param et  l'etat
	 */
	public ControlKey(JFrame fenetre, Etat et) {
		this.etat = et;
		fenetre.addKeyListener(this.Listener(fenetre));
	}

	/**
	 * Crée le KeyAdapter pour faire l'action voulue
	 * 
	 * @return le KeyAdapter qui execute les actions
	 */
	private KeyAdapter Listener(JFrame fenetre) {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// On transmet les touches
				if (!etat.isPerdu()) {
					// Si c'est une direction, on transmet à etat
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						etat.select(Etat.DIR.HAUT);
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						etat.select(Etat.DIR.BAS);
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						etat.select(Etat.DIR.DROITE);
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						etat.select(Etat.DIR.GAUCHE);
						// Si c'est Espace, on active le boosteur
					} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						etat.space();
						// Si c'est shift, on freine
					} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
						etat.shift();
						// Si c'est Echap, on met en pause
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						etat.paused();
						// Si c'est Entrer, on choicit dans le menu (pause si rien n'est affiché)
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						etat.choose();
					}
				}
				// Si le jeu doit quitter
				if (etat.isQuit()) {
					// On ferme la fenetre
					fenetre.dispose();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!etat.isPerdu()) {
					// Si on relache une direction, on transmet à etat
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						etat.unselect(Etat.DIR.HAUT);
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						etat.unselect(Etat.DIR.BAS);
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						etat.unselect(Etat.DIR.DROITE);
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						etat.unselect(Etat.DIR.GAUCHE);
						// Si on relache Espace, on arrete le turbo
					} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						etat.unspace();
						// Si on relache Shift, on arrete de freiner
					} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
						etat.vaisseau.unbraker();
					}
				}
			}
		};
	}
}