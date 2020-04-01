package modele;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import jeu.VoleDirection;
import view.Affichage;

/**
 * Représente le vaisseau jouable
 */
public class Vaisseau {
	private Etat etat;
	public Random rand = new Random();
	public VoleDirection ThreadX = null;
	public VoleDirection ThreadY = null;
	public boolean vaVersX = false;
	public boolean vaVersY = false;
	
	
	//Les caractéristiques du vaisseau :
	
	//La position/taille
	public int x = Affichage.LARG/2 - (Affichage.LARG+Affichage.HAUT)/60;
	public int y = Affichage.SOL - HAUT;
	public final static int LARG = (Affichage.LARG+Affichage.HAUT)/30;
	public final static int HAUT = (Affichage.LARG+Affichage.HAUT)/30;
	public final static int dep = (Affichage.LARG+Affichage.HAUT)/100;
	public final static int dep_acceleration = 4;
	
	//Les statistiques physiques
	public int vitesse = 0;
	public int vitesse_atteignable = 0;
	public int vitesse_max = 300;
	public int acceleration = 1;
	
	//Les états
	public String skin = "Gris";
	public int crash = 0;
	public boolean boost = false;
	public boolean brake = false;
	public int vie = 100;
	
	/**
	 * Constructeur du vaisseau
	 * @param et l'{@link Etat} du jeu
	 */
	public Vaisseau(Etat et) {
		etat = et;
	}
	
	/**
	 * Fait avancer le vaisseau
	 */
	public void accelere() {
		//Premier calcul utilisé, qui marchait magiquement avec les bonnes valeurs
		//vitesse_atteignable = Affichage.DIAGO/etat.piste.distancePiste(x, y + HAUT) * vitesse_max;
		
		vitesse_atteignable = (int)(Math.pow((1 - (float)etat.piste.distancePiste(x, y)/Affichage.DIAGO),2) * vitesse_max);
		
		if (brake) {
			vitesse_atteignable = 0;
		} else if (isBoost()) {
			vitesse_atteignable = vitesse_max;
		}
		//Si il doit aller plus vite
		if (vitesse <= vitesse_atteignable - acceleration) {
			//Il accelere
			if (isBoost()) {
				vitesse += 3 * acceleration;
			} else {
				vitesse += acceleration;
			}
		//Si il va atteindre sa vitesse max
		} else if (vitesse < vitesse_atteignable) {
			vitesse = vitesse_atteignable;
		//Sinon il ralentit
		} else if (vitesse > vitesse_atteignable && vitesse > 0) {
			if (brake) {
				vitesse -= 3 * 2 * acceleration;
			} else {
				vitesse -= 2 * acceleration;
			}
		}
	}
	
	/**
	 * Indique au vaisseau qu'il s'est crashé
	 */
	public void crash() {
		vitesse = 1;
		crash = 100;
	}
	
	/**
	 * Test si le vaisseau a encore de la vie
	 * @return si il est en vie
	 */
	public boolean isDead() {
		return vie <= 0;
	}
	
	/**
	 * Indique au vaisseau le début du turbo
	 */
	public void booster() {
		boost = true;
	}
	
	/**
	 * Indique au vaisseau la fin du turbo
	 */
	public void unbooster() {
		boost = false;
	}
	
	/**
	 * Indique au vaisseau le début du freinage
	 */
	public void braker() {
		brake = true;
	}
	
	/**
	 * Indique au vaisseau la fin du freinage
	 */
	public void unbraker() {
		brake = false;
	}
	
	/**
	 * Indique au vaisseau qu'il doit se déplacer
	 * @param dir la direction à prendre
	 */
	public void vaVers(Etat.DIR dir) {
		//Il y a d'un coté le déplacement haut/bas et de l'autre droite/gauche
		if (dir == Etat.DIR.HAUT || dir == Etat.DIR.BAS) {
			//Si il faut changer de direction
			if (ThreadY != null && ThreadY.getDir() != dir) {
				//Arrete le thread precedant
				ThreadY.arret();
				ThreadY = null;
			}
			if (ThreadY == null) {
				//Et lance un thread dans la bonne direction
				ThreadY = new VoleDirection(etat, dir);
				ThreadY.start();
			}
		} else if (dir == Etat.DIR.DROITE || dir == Etat.DIR.GAUCHE) {
			//Si il faut changer de direction
			if (ThreadX != null && ThreadX.getDir() != dir) {
				//Arrete le thread precedant
				ThreadX.arret();
				ThreadX = null;
			}
			if (ThreadX == null) {
				//Et lance un thread dans la bonne direction
				ThreadX = new VoleDirection(etat, dir);
				ThreadX.start();
			}
		}
	}
	
	/**
	 * Signale au vaisseau qu'il doit arreter d'aller vers une direction
	 * @param dir la direction à arreter de prendre
	 */
	public void stopVers(Etat.DIR dir) {
		if (ThreadY != null && (dir == Etat.DIR.HAUT || dir == Etat.DIR.BAS) && ThreadY.getDir() == dir) {
			ThreadY.arret();
			ThreadY = null;
		} else if (ThreadX != null && (dir == Etat.DIR.DROITE || dir == Etat.DIR.GAUCHE) && ThreadX.getDir() == dir) {
			ThreadX.arret();
			ThreadX = null;
		}
	}
	
	/**
	 * Déplace le vaisseau dans une direction
	 * @param dir la direction
	 * @param intensite l'intensité du déplacement
	 */
	public void vole(Etat.DIR dir, int intensite) {
		int deplacement = (dep + (vitesse/vitesse_max) * dep_acceleration) * intensite/100;
		if (dir == Etat.DIR.HAUT) {
			if (y >= deplacement) {
				y -= deplacement;
			} else {
				y = 0;
			}
		} else if (dir == Etat.DIR.BAS) {
			if (y < Affichage.SOL - deplacement - HAUT) {
				y += deplacement;
			} else {
				y = Affichage.SOL - HAUT;
			}
		} else if (dir == Etat.DIR.DROITE) {
			if (x < Affichage.LARG - deplacement - LARG) {
				x += deplacement;
			} else {
				x = Affichage.LARG - LARG;
			}
		} else if (dir == Etat.DIR.GAUCHE) {
			if (x >= deplacement) {
				x -= deplacement;
			} else {
				x = 0;
			}
		}
	}
	
	/**
	 * Change le skin du vaisseau
	 * @param sk le skin à mettre
	 */
	public void setSkin(String sk) {
		skin = sk;
	}
	
	/**
	 * Dessine le vaisseau si necessaire
	 * @param g {@link Graphics2D}
	 */
	public void drawVaisseau(Graphics2D g) {
		//Si le vaisseau est en crash, le fait clignoter
		if (crash%10 >= 0 && crash%10 <=4) {
			dessineVaisseau(g);
		}
		if (crash > 0) {
			crash --;
		} 
	}
	
	/**
	 * Indique si le vaisseau est en train d'utiliser le turbo
	 * @return si le turbo est utilisé
	 */
	public boolean isBoost() {
		return boost && crash==0;
	}
	
	/**
	 * Dessine le vaisseau, les particules et l'ombre
	 * @param g {@link Graphics2D}
	 */
	public void dessineVaisseau(Graphics2D g) {
		g.setStroke(new BasicStroke(2));
		int x_decal = 0;
		if (Etat.centrer) {
			 x_decal = x - (x- Affichage.LARG/2)/10;
		} else {
			x_decal = x - (x- etat.piste.positionPiste(Affichage.SOL))/10;
		}
		int y_decal = y - (y-Affichage.HAUT/3)/10;
		
		//Selectionne la bonne couleur en fonction du skin
		Color color = Color.GRAY;
		if (skin == "Bleu") {
			color = new Color(0, 135, 180);
		} else if (skin == "Vert") {
			color = new Color(0, 100, 30);
		} else if (skin == "Rouge") {
			color = new Color(180, 0, 0);
		}
		
		//Ombre au sol
		int decal_max = (Affichage.SOL - HAUT - Affichage.HAUT/3)/10;
		g.setColor(new Color(0, 0, 0, 50 * y /Affichage.HAUT + 100));
		int[] x2 = {x, x_decal, x_decal + HAUT, x + HAUT};
		int[] y2 = {Affichage.SOL, Affichage.SOL - decal_max, Affichage.SOL - decal_max, Affichage.SOL};
		g.fillPolygon(x2, y2, 4);
		
		//Rectangle du fond
		g.setColor(color);
		g.fillRect(x_decal, y_decal, LARG, HAUT);
		g.setColor(Color.BLACK);
		Affichage.drawLineRect(g, x_decal, y_decal, LARG, HAUT);
		
		//Lignes entre les deux parties
		g.setColor(Color.BLACK);
		Affichage.drawLineBetweenRects(g, x_decal, y_decal, LARG, HAUT, x, y, LARG, HAUT);
		
		//Rectangle le plus proche de l'écran
		g.setColor(color.darker());
		g.fillRect(x, y, LARG, HAUT);
		g.setColor(Color.BLACK);
		Affichage.drawLineRect(g, x, y, LARG, HAUT);
		
		//Affiche les particules sous le vaisseau
		if (y >= Affichage.SOL - HAUT - Affichage.HAUT/10) {
			if (x + LARG/2 <= etat.piste.positionPiste(y) + etat.piste.taillePiste(y) && x + LARG/2 >= etat.piste.positionPiste(y) - etat.piste.taillePiste(y)) {
				g.setColor(Color.GRAY);
			} else {
				g.setColor(new Color(50/2, 200/2, 50/2));
			}
			int taille = (LARG+HAUT)/10;
			int nombre = (y - (Affichage.SOL - HAUT - Affichage.HAUT/10))/10;
			for (int i = 0; i < nombre; i++) {
				g.fillRect(x +rand.nextInt(LARG - taille), y + HAUT + rand.nextInt(taille), taille, taille);
			}
		}
	}
	
	/**
	 * Fait perdre un point de vie au vaisseau
	 */
	public void perdVie() {
		vie--;
	}
}
