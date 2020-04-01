package modele;

import java.awt.Point;

import view.ThreadAff;

/**
 * Classe representant l'etat interne du jeu
 */
public class Etat {
	public static enum DIR {HAUT, BAS, GAUCHE, DROITE};
	public Vaisseau vaisseau = new Vaisseau(this);
	public Piste piste = new Piste();
	private ThreadAff affi;
	private boolean pause = false;
	public boolean quit = false;

	//Défini le mode de vision du jeu
	public static final boolean centrer = true;

	//Listes des options dans les menus
	private String[] listeMenuPause = {"Reprendre", "Options", "Quitter"};
	public Menu menuPause = new Menu("MenuPause", listeMenuPause, null);

	private String[] listeMenuOption = {"Son","Skins","Retour"};
	public Menu menuOption = new Menu("MenuOption", listeMenuOption, menuPause);

	private String[] listeMenuSkin = {"Gris","Bleu","Vert","Rouge","Retour"};
	public Menu menuSkin = new Menu("MenuSkin", listeMenuSkin, menuOption);

	public Menu menuActuel = menuPause;

	/**
	 * Initialise l'affichage
	 * @param Taffi le thread d'affichage
	 */
	public void setThreadAff(ThreadAff Taffi) {
		affi = Taffi;
	}

	/**
	 * Transmet l'appui d'une touche directionnelle aux bons composants 
	 * ({@link Menu} si le jeu est en pause, {@link Vaisseau} sinon)
	 * @param dir la direction
	 */
	public void select(DIR dir) {
		//Si en pause
		if (pause) {
			//Transmet au menuActuel
			menuActuel.choisir(dir);
			affi.redraw();
		} else {
			//Sinon au vaisseau
			vaisseau.vaVers(dir);
			affi.redraw();
		}
	}

	/**
	 * Transmet l'arret d'appui d'une touche directionnelle aux bons composants 
	 * ({@link Menu} actuel si le jeu est en pause, {@link Vaisseau} sinon)
	 * @param dir la direction appuyé
	 */
	public void unselect(DIR dir) {
		if (!pause) {
			vaisseau.stopVers(dir);
			affi.redraw();
		}
	}

	public void space() {
		if (!isPerdu()) {
			vaisseau.booster();
		}
	}

	public void unspace() {
		vaisseau.unbooster();
	}

	public void shift() {
		vaisseau.braker();
	}

	public void unshift() {
		vaisseau.unbraker();
	}

	/**
	 * Transmet la selection (enter) au {@link Menu}
	 */
	public void choose() {
		menuActuel.choose(this);
		affi.redraw();
	}

	/**
	 * Fin de jeu
	 * @return vrai si le jeu est fini
	 */
	public boolean isPerdu() {
		return vaisseau.isDead() || quit;
	}

	/**
	 * Enregistre que le jeu se finit et qu'il faut fermer la fenetre
	 */
	public void quit() {
		quit = true;
	}

	/**
	 * Getter de quit
	 * @return quit
	 */
	public boolean isQuit() {
		return quit;
	}

	/**
	 * Jeu en pause ?
	 * @return true si le jeu est en pause
	 */
	public boolean isPause() {
		return pause;
	}

	/**
	 * Change d'etat le jeu (pause devient reprise, jeu normal devient pause)
	 */
	public void paused() {
		if (!pause) {
			pause = true;
			affi.redraw();
		} else {
			pause = false;
			changeMenuActuel(menuPause);
			affi.redraw();
		}
	}

	/**
	 * Change le menu actuellement en cours
	 * @param next le menu suivant à mettre en cours
	 */
	public void changeMenuActuel(Menu next) {
		menuActuel.resetSelection();
		menuActuel = next;
	}

	/**
	 * Calcule l'intersection de deux droites (en utilisant quatre points)
	 * @param x1 le x du 1 point
	 * @param y1 le y du 1 point
	 * @param x2 le x du 2 point
	 * @param y2 le y du 2 point
	 * @param x3 le x du 3 point
	 * @param y3 le y du 3 point
	 * @param x4 le x du 4 point
	 * @param y4 le y du 4 point
	 * @return le point d'intersection 
	 */
	public static Point intersection(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
		int x = 1000000;
		int y = 1000000;
		if (((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4)) != 0) {
			x = ((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/
					((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4));
		}
		if (((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4)) != 0)
			y = ((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/
			((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4));
		return new Point(x,y);
	}
}
