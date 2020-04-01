package modele;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import modele.Etat.DIR;
import view.Affichage;

/**
 * Classe representant un menu avec differentes options
 */
public class Menu {
	public String nomMenu;
	private int choix_menu = 0;
	private String[] choixMenu;
	private Menu menuParent;
	
	/**
	 * Construit le menu
	 * @param nom le nom du menu (utilisé pour définir l'action à  faire)
	 * @param choix la liste de proposition à  afficher
	 */
	public Menu(String nom, String[] choix, Menu menuPa) {
		this.nomMenu = nom;
		this.choixMenu = choix;
		this.menuParent = menuPa;
	}
	
	/**
	 * Change d'un cran la sélection si haut ou bas
	 * @param dir
	 */
	public void choisir(DIR dir) {
		if (dir == DIR.HAUT) {
			if (choix_menu > 0) {
				choix_menu--;
			//Si on est tout en haut, on redescend tout en bas
			} else {
				choix_menu = choixMenu.length - 1;
			}
		} else if (dir == DIR.BAS) {
			if (choix_menu < choixMenu.length - 1) {
				choix_menu++;
				//Si on est tout en bas, on redescend tout en haut
			} else {
				choix_menu = 0;
			}
		}
	}
	
	/**
	 * Sélctionne une option dans le menu
	 * @param etat l'état (pour faire des modifications dessus)
	 */
	public void choose(Etat etat) {
		String selection = choixMenu[choix_menu];
		
		//Choix "universels" : la meme action peut importe le menu en cours
		//Si on selectionne Retour, on repart au menu précédant
		if (selection == "Retour") {
			etat.changeMenuActuel(menuParent);
		//Si on selectionne Reprendre, le jeu reprend
		} else if (selection == "Reprendre") {
			etat.paused();
		//Si on selectionne Quitter, on quitte le jeu
		} else if (selection == "Quitter") {
			etat.quit = true;
		}
		
		//Choix spécifiques
		if (nomMenu == "MenuPause") {
			if (selection == "Options") {
				etat.changeMenuActuel(etat.menuOption);
			} 
		} else if (nomMenu == "MenuOption") {
			if (selection == "Son") {
				
			} else if (selection == "Skins") {
				etat.changeMenuActuel(etat.menuSkin);
			}
		} else if (nomMenu == "MenuSkin") {
			etat.vaisseau.setSkin(choixMenu[choix_menu]);
		}
	}
	
	/**
	 * Remet le choix premier au menu
	 */
	public void resetSelection() {
		choix_menu = 0;
	}
	
	/**
	 * Dessine le menu
	 * @param g Graphics2D
	 */
	public void draw(Graphics2D g) {
		g.setFont(new Font("TimesRoman", Font.PLAIN, (Affichage.LARG + Affichage.HAUT)/40));
		int x = Affichage.LARG/2-(Affichage.LARG + Affichage.HAUT)/15;
		int y = Affichage.HAUT/3+(Affichage.LARG+Affichage.HAUT)/20;
		int size_char = (Affichage.LARG + Affichage.HAUT)/40;
		
		for (int i = 0; i < choixMenu.length; i++) {
			//Si la sélection est ici, on dessine un rectangle
			if (i == choix_menu) {
				g.setColor(Color.GRAY);
				g.fillRect(x, y - size_char + (i * Affichage.HAUT / 10) + size_char/5, size_char * choixMenu[i].length() * 3/5, size_char);
			}
			//On écrit la ligne du menu
			g.setColor(Color.BLACK);
			g.drawString(choixMenu[i], x, y + (i * Affichage.HAUT / 10));
		}
	}
}
