package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Random;

import javax.swing.JPanel;

import modele.Etat;

/**
 * Classe s'occupant de l'affichage graphique
 */
public class Affichage extends JPanel {
	private static final long serialVersionUID = 1L;
	public final static boolean fullscreen = true;
	private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public Random rand = new Random();
	
	//Constantes
	public final static int LARG = fullscreen ? screenSize.width : 1000;
	public final static int HAUT = fullscreen ? screenSize.height : 1000;
	public final static int DIAGO = (int) Math.sqrt(Math.pow(Affichage.HAUT, 2) + Math.pow(Affichage.LARG, 2));
	public final static int SOL = HAUT*9/10;
	public final static int HORIZON = HAUT/3;
	
	private Etat etat;
	
	/**
	 * Constructeur
	 * @param et l'{@link Etat}
	 */
	public Affichage(Etat et) {
		setPreferredSize(new Dimension(LARG, HAUT));
		etat = et;
	}
	
	/**
	 * Dessine une ligne formant un retangle
	 * @param g {@link Graphics2D}
	 * @param x le x du rectangle
	 * @param y le y du rectangle
	 * @param larg la largeur du rectangle
	 * @param haut la hauteur du rectangle
	 */
	public static void drawLineRect(Graphics2D g, int x, int y, int larg, int haut) {
		g.drawLine(x, y, x+larg, y);
		g.drawLine(x, y, x, y+haut);
		g.drawLine(x, y+haut, x+larg, y+haut);
		g.drawLine(x+larg, y, x+larg, y+haut);
	}
	
	/**
	 * Dessine des lignes reliant les angles de deux rectangles
	 * @param g {@link Graphics2D}
	 * @param x1 le x du rectangle 1
	 * @param y1 le y du rectangle 1
	 * @param larg1 la largeur du rectangle 1
	 * @param haut1 la hauteur du rectangle 1
	 * @param x2 le x du rectangle 2
	 * @param y2 le y du rectangle 2
	 * @param larg2 la largeur du rectangle 2
	 * @param haut2 la hauteur du rectangle 2
	 */
	public static void drawLineBetweenRects(Graphics2D g, int x1, int y1, int larg1, int haut1, int x2, int y2, int larg2, int haut2) {
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x1+larg1, y1, x2+larg2, y2);
		g.drawLine(x1, y1+haut1, x2, y2+haut2);
		g.drawLine(x1+larg1, y1+haut1, x2+larg2, y2+haut2);
	}
	
	/**
	 * Dessine le background
	 * @param g {@link Graphics2D}
	 */
	public void background(Graphics2D g) {
		g.clearRect(0, 0, LARG, HAUT/3);
		g.setColor(new Color(50,200,250,100));
		g.fillRect(0, 0, LARG, HAUT/3);
	}
	
	/**
	 * Dessine le texte
	 * @param g {@link Graphics2D}
	 */
	public void etatTexte(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.PLAIN+1, (LARG+HAUT)/100)); 
		g.drawString("Score : "+Integer.toString(etat.piste.getPosition()/100), 20, 40);
		if (etat.vaisseau.isBoost()) {
			g.setColor(Color.RED.darker());
		}
		g.drawString("Temps : "+Integer.toString(etat.vaisseau.vie), LARG - (LARG+HAUT)/13, 40);
		g.drawString(Integer.toString(etat.vaisseau.vitesse)+" km/h", 20, HAUT - (LARG+HAUT)/30);
	}
	
	/**
	 * Dessine le gameOver
	 * @param g {@link Graphics2D}
	 */
	public void gameOver(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.PLAIN, (LARG+HAUT)/30)); 
		g.drawString("GAME OVER", LARG/2-(LARG+HAUT)/9, HAUT/3);
		g.setFont(new Font("TimesRoman", Font.PLAIN, (LARG+HAUT)/40));
		g.drawString("Score : "+Integer.toString(etat.piste.getPosition()/100), LARG/2-(LARG+HAUT)/15, HAUT/3+(LARG+HAUT)/20);
	}
	
	/**
	 * Dessine les traits pour faire un effet d'accéleration
	 * @param g {@link Graphics2D}
	 */
	public void drawTraitAcceleration(Graphics2D g) {
		//Affiche les traits autour de l'écran pour le boost
		int vitesse = etat.vaisseau.vitesse;
		
		g.setColor(new Color(255, 255, 255, etat.vaisseau.isBoost() ? 250 : 125));
		g.setStroke(new BasicStroke(6));
		
		//On choisit à partir de quelle vitesse on affiche les traits
		int pallier_pour_trait = 50;
		//On choisit le nombre de trait à afficher
		int nombre_trait = 0;
		if (etat.vaisseau.isBoost()) {
			nombre_trait = (vitesse)/15;
		} else if (vitesse > pallier_pour_trait) {
			nombre_trait = (vitesse - pallier_pour_trait)/20;
		}
		for (int i = 0; i < nombre_trait; i++) {
			//Les coordonnées de début de la ligne
			int x=0;
			int y=0;
			Point p = null;
			int type_trait = rand.nextInt(4);
			//Défini le type de trait : venant d'en haut, d'en bas, de droite ou de gauche
			
			if (type_trait == 0) {
				//La ligne part d'en haut
				x = rand.nextInt(Affichage.LARG);
				y = 0;
				//Défini où la ligne s'arrete en y
				int y_inter = rand.nextInt(Affichage.HAUT/4);
				p = Etat.intersection(x, y, Affichage.LARG/2, Affichage.HAUT/3, 0, y_inter, 100, y_inter);
				
			} else if (type_trait == 2) {
				//La ligne part de la gauche
				x = 0;
				y = rand.nextInt(Affichage.HAUT);
				//Défini où la ligne s'arrete en x
				int x_inter = rand.nextInt(Affichage.LARG/4);
				p = Etat.intersection(x, y, Affichage.LARG/2, Affichage.HAUT/3, x_inter, 0, x_inter, 100);
				
			} else if (type_trait == 3) {
				//La ligne part d'en bas
				x = rand.nextInt(Affichage.LARG);
				y = Affichage.HAUT;
				//Défini où la ligne s'arrete en y
				int y_inter = rand.nextInt(Affichage.HAUT*2/6) + Affichage.HAUT*4/6;
				p = Etat.intersection(x, y, Affichage.LARG/2, Affichage.HAUT/3, 0, y_inter, 100, y_inter);
				
			} else {
				//La ligne part de la droite
				x = Affichage.LARG;
				y = rand.nextInt(Affichage.HAUT);
				//Défini où la ligne s'arrete en x
				int x_inter = rand.nextInt(Affichage.LARG/4) + Affichage.LARG*3/4;
				p = Etat.intersection(x, y, Affichage.LARG/2, Affichage.HAUT/3, x_inter, 0, x_inter, 100);
			}
			g.drawLine(x, y, p.x, p.y);
		}
	}
	
	@Override
	public void paint(Graphics g2) {
		super.paint(g2);
		Graphics2D g = (Graphics2D)g2;
		
		//Herbe
		g.setColor(new Color(50, 200, 50));
		g.fillRect(0, HAUT/3, LARG, HAUT);
		
		etat.piste.drawPiste(g);
		
		background(g);
		
		//Horizon
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.BLACK);
		g.drawLine(0, HAUT/3, LARG, HAUT/3);
		
		etat.piste.drawDecors(g);
		
		etat.vaisseau.drawVaisseau(g);
		
		drawTraitAcceleration(g);
		
		etatTexte(g);
		
		if (etat.isPause()) {
			etat.menuActuel.draw(g);
		}
		
		if (etat.isPerdu()) {
			gameOver(g);
		}
	}
}
