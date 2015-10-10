package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class DefaultTeam {

	/**
	 * @param points Liste de points sur laquelle appliquer plusieur fois l'algorithme de Bafna-Berman-Fujito
	 * @return Une liste de points correspondant aux points a enlever pour supprimer les cycles
	 */
	public ArrayList<Point> calculFVS(ArrayList<Point> points) {
		ArrayList<Point> Fmin=null;
		ArrayList<Point> Fcourant;
		int min=Integer.MAX_VALUE;
		
		for(int i=0;i<points.size() || min>78;i++){
			Collections.shuffle(points); //mélange la liste, possible d'avoir un meilleur résultat
			Fcourant=compute(points);
			if(Fcourant.size()<min){
				min=Fcourant.size();
				Fmin=Fcourant;
			}
			
		}
		return Fmin;
	}

	/**
	 * @param points Liste de points sur laquelle appliquer l'algortihme de Bafna-Berman-Fujito
	 * @return Une liste de points correspondant aux points a enlever pour supprimer les cycles
	 */
	public ArrayList<Point> compute(ArrayList<Point> points){
		ArrayList<Point> F=new ArrayList<>();
		HashMap<Point, Double> w=new HashMap<>();
		Stack<Point> stack=new Stack<>();
		
		//Initialisation des poids a 1
		for(Point p:points){
			w.put(p, 1.0);
		}
		
		Evaluation eval= new Evaluation();
		//Construction du graphe en fonction de l'évalution eval
		Graph g=new Graph(points,eval);

		g.cleanup();

		while(!g.keySet().isEmpty()){
			
			ArrayList<Point> C=g.findSemidisjointCycle();
			ArrayList<Point> V=new ArrayList<>(g.keySet());
			
			if(C!=null){//Application dans le cas d'un cycle semidisjoint
				Double l=minW(C, w, g,0);

				for(Point u:C){
					w.put(u, w.get(u)-l);
				}
				
			}else{//Application dans le cas où il n'y à pas de cycle semidisjoint
				Double l=minW(new ArrayList<>(g.keySet()), w, g, 1);
				
				for(Point u:V){
					w.put(u, w.get(u)-l*(g.degre(u)-1.0));
				}
			}
			
			//On enlève tout les points de poids un
			for(Point u:V){
				if(w.get(u)<=0.0){
					g.delect(u);
					F.add(u);
					stack.push(u);
				}
			}
			g.cleanup();
		}
		
		//On rafine la liste des points enlevés
		while(!stack.isEmpty()){
			Point u=stack.pop();
			F.remove(u);
			if(!eval.isValide(points, F))
				F.add(u);
		}

		return F;
	}

	/**
	 * @param points Liste de cles concernees dans w  
	 * @param w HashMap dans laquelle selectionner le point minimum selon la formule proposer par le mode
	 * @param g Graph dans lequelle les cles sont presentent
	 * @param mode si mode=1 formule a appliquer est w.get(point)/(g.degre(point) - 1.0) sinon w.get(point)
	 * @return point minimum selon la formule proposer par le mode
	 */
	public double minW(ArrayList<Point> points,HashMap<Point, Double> w, Graph g, int mode){
		Double min=Double.MAX_VALUE;
		Double val;

		for (Point point : points) {
			if(mode==1){
				val=w.get(point)/(g.degre(point) - 1.0);
			}else{
				val=w.get(point);
			}
			if(val<min)
				min=val;
		}
		return min;
	}
}