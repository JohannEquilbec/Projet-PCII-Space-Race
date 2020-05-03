package modele;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import view.Affichage;

/**
 * Classe s'occupant de representer la piste et ce qui s'affiche sur la route
 */
public class Piste {
	//Liste des points qui constituent la route
	private ArrayList<Point> ligne = new ArrayList<Point>();
	
	//Liste des décors sur la route
	private ArrayList<Decors> decors = new ArrayList<>();
	
	private static final Random rand = new Random();
	
	//Plus la valeur est petite, plus il y a de décors
	private static final int TauxApparitionDecors = 100;
	
	//Position de la pointe de la piste
	private int x_current = Affichage.LARG/2;
	private int y_current = Affichage.HAUT;
	
	//Avancement
	private int position = 0;
	
	//Caractéristiques de la route
	private int longueur_segment = 300;
	private int tournant = 300;
	
	private Point prev_line_position = new Point(0,0);
	private Point prev_line_position_raw = new Point(0,0);
	
	//Variables pour les checkpoints
	public int prochainCheckpoint = 200; // Le prochain checkpoint se trouve dans X pixels multiplié par multDistance
	public double multDistance = 1.8;  // Pour creer une courbe de progression de plus en plus dure

	public int vieRecuperee = 50;
	
	//Variables de base pour la création des checkpoints
	public int posYCheckpoint = Affichage.HAUT / 3;
	
	public int position1 = 0;
	public int position2 = 0;
	
	//public boolean checkFin = false;
	public boolean waitCheck = false; // Pour ne pas reboucler debutCheckpoint dans le Thread
	public boolean isCheckpoint = false; // Si un checkpoint doit apparaitre ou non
	public boolean afficheMessage = false; // Si on affiche le message de prevention a l'ecran
	public boolean checked = false; // True si le checkpoint courant a été collecté, false par défaut
	
	/**
	 * Crée la ligne aléatoirement
	 */
	public Piste() {
		addPoint();
	}
	
	/**
	 * Ajoute un décors
	 */
	public void addDecors() {
		decors.add(new Decors(this, position - Affichage.HAUT/3, positionPiste(Affichage.HORIZON)));
	}
	
	/**
	 * Ajoute un point à la ligne
	 */
	private void addPoint() {
		//Définie la curvature
		x_current += rand.nextInt(tournant) - tournant/2;
		if (x_current < 50) {
			x_current=50;
		} else if (x_current > Affichage.LARG - (Affichage.LARG/100 + Affichage.HAUT*17/60)) {
			x_current= Affichage.LARG - (Affichage.LARG/100 + Affichage.HAUT*17/60);
		}
		ligne.add(new Point(x_current, y_current));
		//Définie la longueur du prochain segment
		y_current -= rand.nextInt(longueur_segment/3) + longueur_segment;
	}
	
	/**
	 * Revoie les points visibles dans la fenetre et les avancent suivant la position
	 * @return les points visibles dans la fenetre (sans décalage pour qu'ils soient centrés)
	 */
	public ArrayList<Point> getRawParcours() {
		ArrayList<Point> ligne_visible = new ArrayList<Point>();
		for (int i=0; i<ligne.size(); i++) {
			
			//On regarde si il est encore visible
			if (ligne.get(i).y + position < Affichage.HAUT*2) {
				ligne_visible.add(new Point(ligne.get(i).x, ligne.get(i).y + position));
				if (ligne.get(i).y + position < 0) {
					return ligne_visible;
				}
				//Si il n'y a plus assez de points
				if (i==ligne.size()-1) {
					//On en rajoute un
					addPoint();
				}
			//Sinon on le supprime
			} else {
				ligne.remove(i);
			}
		}
		return ligne_visible;
	}
	
	/**
	 * Décale le parcours pour que la piste soit à l'horizon au centre
	 * @param ligne_visible la ligne à décaler
	 * @return la piste décalée
	 */
	public ArrayList<Point> decaleParcours(ArrayList<Point> ligne_visible) {
		int centre = positionPiste(Affichage.HORIZON, true);
		for (Point p : ligne_visible) {
			p.x = p.x - centre + Affichage.LARG/2;
		}
		return ligne_visible;
	}
	
	/**
	 * Renvoi le parcours tel qu'il est vu à l'écran
	 * @return
	 */
	public ArrayList<Point> getParcours() {
		ArrayList<Point> ligne_visible = getRawParcours();
		if (Etat.centrer) {
			decaleParcours(ligne_visible);
		}
		return ligne_visible;
	}
	
	/**
	 * Getter de la position
	 * @return position
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Avance la postion du parcours
	 * @param etat 
	 */
	public void avance(Etat etat) {
		this.position += etat.vaisseau.vitesse/10;

		//Génération de décors aléatoire
		if (rand.nextInt(TauxApparitionDecors) == 0) {
			addDecors();
		}

		for (Decors p : decors) {
			if (!p.crash_into) {
				//Test de collision en hauteur
				if (position - p.y >= Affichage.SOL - p.getSize(position).y && 
						position - p.y <= Affichage.HAUT && 
						etat.vaisseau.y >= Affichage.SOL - p.getSize(position).y) {
					//Test de collision en largeur
					if (etat.vaisseau.x < p.getX(position) + p.getSize(position).x  &&
							etat.vaisseau.x + Vaisseau.LARG > p.getX(position)) {
						etat.vaisseau.crash();
						p.crash();
					}
				}
			}
		}

		if (checked == false && isCheckpoint == true) {
			int posBaseAx = positionPiste(posYCheckpoint) - taillePiste(posYCheckpoint)*3/4;
			int posBaseBx = positionPiste(posYCheckpoint) + taillePiste(posYCheckpoint)*3/4;
			crossCheckpoint(etat.vaisseau.x, etat.vaisseau.y, etat.vaisseau.x + Vaisseau.LARG, etat.vaisseau.y + Vaisseau.HAUT, posBaseAx, posYCheckpoint, posBaseBx, posYCheckpoint, etat);
			//crossCheckpoint((Affichage.LARG/2) - 100, Affichage.HAUT - 150, (Affichage.LARG/2) + 100, Affichage.HAUT - 150, posBaseAx, posBaseAy, posBaseBx, posBaseBy, etat);
		}
	}
	
	/**
	 * Affiche le parcours
	 * @param g les Graphics
	 */
	public void drawPiste(Graphics2D g) {
		ArrayList<Point> parcours = getParcours();
		for (int i=0; i < parcours.size()-1; i++) {
			
			g.setStroke(new BasicStroke(4));
			g.setColor(Color.BLACK);
			
			Point i0 = new Point(parcours.get(i).x, parcours.get(i).y);
			Point i1 = new Point(parcours.get(i+1).x, parcours.get(i+1).y);
			
			//Caclule la partie gauche de la piste à afficher
			int x1_haut = i0.x - (Affichage.LARG/100 + (i0.y - Affichage.HAUT/3)/2)/2;
			int x1_bas = i1.x - (Affichage.LARG/100 + (i1.y - Affichage.HAUT/3)/2)/2;
			
			//Caclule la partie droite de la piste à afficher
			int x2_haut = i0.x + (Affichage.LARG/100 + (i0.y - Affichage.HAUT/3)/2)/2;
			int x2_bas = i1.x + (Affichage.LARG/100 + (i1.y - Affichage.HAUT/3)/2)/2;
			
			//Trace les pavés
			g.setColor(Color.LIGHT_GRAY);
			g.setStroke(new BasicStroke(1));
			int[] x = {x1_haut, x1_bas, x2_bas, x2_haut};
			int[] y = {i0.y, i1.y, i1.y, i0.y};
			g.fillPolygon(x, y, 4);
			
			//Trace les bordures de la route
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.BLACK);
			g.drawLine(x1_haut, i0.y , x1_bas, i1.y);
			g.drawLine(x2_haut, i0.y, x2_bas, i1.y);
			
			//Trace la ligne blanche
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.WHITE);
			int height_difference = (i0.y - i1.y)/10;
			Point haut = Etat.intersection(i0.x, i0.y, i1.x, i1.y, 0, i0.y - height_difference, 100, i0.y - height_difference);
			Point bas = Etat.intersection(i0.x, i0.y, i1.x, i1.y, 0, i1.y + height_difference, 100, i1.y + height_difference);
			g.drawLine(haut.x, haut.y, bas.x, bas.y);
		}
	}
		
		//Test sur la position de la piste à l'horizon (debug)
		/*g.setStroke(new BasicStroke(2));
		g.setColor(Color.RED);
		int x = Affichage.HORIZON;
		g.drawOval(positionTaillePiste(x)[0]-5, x-5, 10, 10);
		*/
		
	////////////// DEBUT CHECKPOINT ///////////////////	

	/**
	 * Affiche un court message a l'ecran, avertissant de l'arrivee du checkpoint
	 * Dessine le debut du checkpoint, a l'horizon
	 * @throws InterruptedException 
	 */
	public void messageCheckpoint() {
		afficheMessage = true;
	}


	public void debutCheckpoint() {
		if (this.position >= prochainCheckpoint) {
			messageCheckpoint();
			//setCheckTrue();
			setWaitTrue();
		}
	}

	/**
	 * Definit qu'un checkpoint doit apparaitre a l'ecran
	 */
	public void setCheckTrue() {
		System.out.println("Changement de valeur");
		isCheckpoint = true;
	}


	public void setWaitTrue() {
		waitCheck = true;
	}

	/**
	 * Dessine la ligne de checkpoint qui va suivre la route
	 * 
	 * @param g
	 */
	public void dessineCheckpoint(Graphics2D g) {
		afficheMessage = false;
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(8));
		int posBaseAx = positionPiste(posYCheckpoint) - taillePiste(posYCheckpoint)*3/4;
		int posBaseBx = positionPiste(posYCheckpoint) + taillePiste(posYCheckpoint)*3/4;

		position2 = position1;
		position1 = position;
		posYCheckpoint += (position1 - position2);
			
		g.drawLine(posBaseAx, posYCheckpoint, posBaseBx, posYCheckpoint);
		
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(4));
	}
	
	public void checkpointCrossed(Etat etat) {
		etat.vaisseau.vie += vieRecuperee;
	}
	
	public void crossCheckpoint(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, Etat etat) {
		if (etat.vaisseau.y >= (Affichage.HAUT/2) && (((x3 - x1)*(y2 - y1)-(y3 - y1)*(x2 - x1))*((x4 - x1)*(y2 - y1)-(y4 - y1)*(x2 - x1))) < 0 && (((x1 - x3)*(y4 - y3)-(y1 - y3)*(x4 - x3))*((x2 - x3)*(y4 - y3)-(y2 - y3)*(x4 - x3))) < 0) {
			checkpointCrossed(etat);
			checked = true;
		}
	}

	public void changeDifficulte(String diff) {
		if (diff == "Facile") {
			setMultDistance(1.2);
		}
		if (diff == "Normal") {
			setMultDistance(1.8);
		}
		if (diff == "Difficile") {
			setMultDistance(5.0);
		}
	}
	
	public void setMultDistance(double multDistance) {
		this.multDistance = multDistance;
	}

	/*
	 * Definit ou sera le prochain checkpoint rencontre
	 */
	public void nouveauCheckpoint() {
		prochainCheckpoint += prochainCheckpoint * multDistance;
	}
	
	public void reinitialiseValeurs() {
		this.posYCheckpoint = Affichage.HAUT / 3;
	}

	//////////// FIN CHECKPOINT /////////////////
		
	
	/**
	 * Dessine les décors
	 * @param g {@link Graphics2D}
	 */
	public void drawDecors(Graphics2D g) {	
		//Dessine les decors
		for (int i = 0; i < decors.size(); i++) {
			//Si il est trop loin
			if (position - decors.get(i).y > Affichage.HAUT * 2) {
				//On le supprime
				decors.remove(i);
			} else {
				decors.get(i).draw(g, position);
			}
		}
	}
	
	/**
	 * Calcule la distance de la piste au x du point
	 * @param x le x du point
	 * @return la distance entre les deux (en pixel)
	 */
	public int distancePisteX(int x) {
		return Math.abs((x+Vaisseau.LARG/2) - positionPiste(Affichage.SOL));
	}
	
	/**
	 * Calcule la distance de la piste au y du point
	 * @param y le y du point
	 * @return la distance entre les deux (en pixel)
	 */
	public int distancePisteY(int y) {
		return Affichage.HAUT - y - Affichage.HAUT/10;
	}
	
	/**
	 * Calcule la distance de la piste au point
	 * @param x le x du point
	 * @param y le y du point
	 * @return la distance entre les deux (en pixel)
	 */
	public int distancePiste(int x, int y) {
		return (int) Math.sqrt(Math.pow(distancePisteX(x), 2) + Math.pow(distancePisteY(y), 2));
	}
	
	/**
	 * Calcule la position de la piste au y donné (tel qu'affiché)
	 * @param y l'endoit où regarder la position de la piste
	 * @return la position x de la piste
	 */
	public int positionPiste(int y) {
		ArrayList<Point> parcours = getParcours();
		for (int i=1; i < parcours.size()-1; i++) {
			if (parcours.get(i).y <= y) {
				Point pos = Etat.intersection(0, y, 100, y, 
						parcours.get(i-1).x, parcours.get(i-1).y, parcours.get(i).x, parcours.get(i).y);
				if (y == Affichage.HORIZON) {
					prev_line_position = pos;
				}
				int res = pos.x;
				return res;
			}
		}
		int res = prev_line_position.x;
		return res;
	}
	
	/**
	 * Calcule la position de la piste au y donné
	 * @param y l'endoit où regarder la position de la piste
	 * @param raw si on doit prendre en compte le décalage centrer
	 * @return la position x de la piste
	 */
	private int positionPiste(int y, boolean raw) {
		ArrayList<Point> parcours;
		//Si on veut raw
		if (raw) {
			//On prend le parcours raw
			parcours = getRawParcours();
		} else {
			//Sinon on renvoi la position classique
			return positionPiste(y);
		}
		for (int i=1; i < parcours.size()-1; i++) {
			if (parcours.get(i).y < y) {
				Point pos = Etat.intersection(0, y, 100, y, 
						parcours.get(i-1).x, parcours.get(i-1).y, parcours.get(i).x, parcours.get(i).y);
				if (y == Affichage.HORIZON) {
					prev_line_position_raw = pos;
				}
				int res = pos.x;
				return res;
			}
		}
		int res = prev_line_position_raw.x;
		return res;
	}
	
	/**
	 * Calcule la taille de la piste au y donné
	 * @param y le y où calculer la taille
	 * @return la taille de la piste
	 */
	public int taillePiste(int y) {
		//TODO sur-évalué de 1/3 (d'où le * 2/3)
		return (Affichage.LARG/100 + (y - Affichage.HAUT/3)/2) * 2 /3;
	}
}