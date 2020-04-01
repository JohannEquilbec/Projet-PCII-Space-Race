package modele;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import view.Affichage;

/**
 * Classe representant un decors sur le terrain
 */
public class Decors {
	public Piste piste;
	private Random rand = new Random();
	public int x = 0;
	public int y = 0;
	public final int variabilite = 20;
	public final int LARG = (Affichage.LARG+Affichage.HAUT)/(30 - variabilite/2 + rand.nextInt(variabilite));
	public final int HAUT = (Affichage.LARG+Affichage.HAUT)/(30 - variabilite/2 + rand.nextInt(variabilite));
	public int position_piste;
	public boolean crash_into = false;
	private final int colorR = rand.nextInt(127);
	private final int colorG = rand.nextInt(127);
	private final int colorB = rand.nextInt(127);
	
	/**
	 * Initialisation du décors
	 * @param yy la position initiale en y du décors
	 * @param pos_piste la position de la piste sur le y initial
	 */
	public Decors(Piste pist, int yy, int pos_piste) {
		piste = pist;
		this.position_piste = pos_piste;
		if (Etat.centrer) {
			this.x = rand.nextInt(Affichage.LARG * 5) - Affichage.LARG*2;
		} else {
			do {
				this.x = rand.nextInt(Affichage.LARG*3/5)+Affichage.LARG*3/5;
			} while (this.x - 50 >= pos_piste && this.x + 50 <= pos_piste);
		}
		this.y = yy;
	}
	
	/**
	 * Dessine le décors
	 * @param g Graphics2D
	 * @param pos l'avancement actuel de la piste
	 */
	public void draw(Graphics2D g, int pos) {
		int position_y = pos - y;
		int i = x > position_piste-10 ? 1 : -1;
		int position_x = 0;
		int x_decal = 0;
		//Si centrer, le point de fuite est le milieu de l'horizon
		if (Etat.centrer) {
			int centre = piste.positionPiste(Affichage.HORIZON);
			position_x = Etat.intersection(0, position_y, 100, position_y, Affichage.LARG/2, Affichage.HORIZON, x, Affichage.HAUT*2).x;
			x_decal = position_x - (position_x - centre)/10;
		//Sinon, c'est le centre de la piste
		} else {	
			position_x = x + i*(x - position_piste + (position_y - Affichage.HAUT/3)/2);
			x_decal = position_x - (position_x - position_piste)/10;
		}
		int y_decal = position_y - (position_y - Affichage.HAUT/4)/10;
		
		//Calcul de la taille de l'obstacle en tenant compte de l'effet de profondeur
		int HAUT_back = (position_y * HAUT - Affichage.HAUT/3)/(Affichage.HAUT*2/3) ;
		int LARG_back = (position_y * LARG - Affichage.HAUT/3)/(Affichage.HAUT*2/3) ;
		int HAUT_front = HAUT_back + (position_y * HAUT)/(Affichage.HAUT)/10;
		int LARG_front = LARG_back + (position_y * HAUT)/(Affichage.HAUT)/10;
		
		position_y -= HAUT_front;
		y_decal -= HAUT_back;
		
		//Si on s'est cogné dedans, l'obstacle est un peu transparent
		Color color = new Color(colorR, colorG, colorB, crash_into ? 130 : 255);
		g.setStroke(new BasicStroke(4));
		g.setColor(color);
		
		//Rectangle du fond (inutile car on dessine par dessus)
		//g.fillRect(x_decal, y_decal, LARG_back, HAUT_back);
		
		//Dessine les contours du rectangle du fond
		g.setColor(Color.BLACK);
		Affichage.drawLineRect(g, x_decal, y_decal, LARG_back, HAUT_back);
		
		g.setStroke(new BasicStroke(2));
		g.setColor(color);
		//Carré du dessus
		int[] x = {x_decal, position_x, position_x + LARG_front, x_decal + LARG_back};
		int[] y = {y_decal, position_y, position_y, y_decal};
		g.fillPolygon(x, y, 4);
		
		//Carré coté droit
		int[] x1 = {position_x + LARG_front, x_decal + LARG_back, x_decal + LARG_back, position_x + LARG_front};
		int[] y1 = {position_y, y_decal, y_decal + HAUT_back, position_y + HAUT_front};
		g.fillPolygon(x1, y1, 4);
		
		//Carré coté gauche
		int[] x2 = {x_decal, position_x, position_x, x_decal};
		int[] y2 = {y_decal, position_y, position_y + HAUT_front, y_decal + HAUT_back};
		g.fillPolygon(x2, y2, 4);
		
		//Ligne entre les deux rectangles (devant et derrière)
		g.setColor(Color.BLACK);
		Affichage.drawLineBetweenRects(g, x_decal, y_decal, LARG_back, HAUT_back, position_x, position_y, LARG_front, HAUT_front);
		
		//Rectangle de devant
		g.setColor(color.darker());
		g.fillRect(position_x, position_y, LARG_front, HAUT_front);
		g.setColor(Color.BLACK);
		Affichage.drawLineRect(g, position_x, position_y, LARG_front, HAUT_front);
	}
	
	/**
	 * Getter de la position en x du décors
	 * @param pos l'avancement actuel de la piste
	 * @return la position x du décors
	 */
	public int getX(int pos) {
		int position_y = pos - y;
		if (Etat.centrer) {
			return Etat.intersection(0, position_y, 100, position_y, Affichage.LARG/2, Affichage.HORIZON, x, Affichage.HAUT*2).x;
		} else {
			int i = x > position_piste-10 ? 1 : -1;
			return x + i*(x - position_piste + (position_y - Affichage.HAUT/3)/2);
		}
	}
	
	public void crash() {
		crash_into = true;
	}
	
	/**
	 * Getter de la taille du décors
	 * @param pos l'avancement actuel de la piste
	 * @return la taille du décors
	 */
	public Point getSize(int pos) {
		int position_y = pos - y;
		int HAUT_back = (position_y * HAUT - Affichage.HAUT/3)/(Affichage.HAUT*2/3) ;
		int LARG_back = (position_y * LARG - Affichage.HAUT/3)/(Affichage.HAUT*2/3) ;
		int HAUT_front = HAUT_back + (position_y * HAUT)/(Affichage.HAUT)/10;
		int LARG_front = LARG_back + (position_y * HAUT)/(Affichage.HAUT)/10;
		return new Point(LARG_front, HAUT_front);
	}
}
