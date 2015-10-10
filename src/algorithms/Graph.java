package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Classe representant un graphe
 * herite de HashMap pour que a partir d'un point
 * on obtien ces voisins
 */
public class Graph extends HashMap<Point, ArrayList<Point>>{

	Evaluation eval;
	
	/**
	 * Constructeur
	 * @param points Liste de points du graphe
	 * @param eval Evaluateur pour definire les relations de voisinnage des points
	 */
	public Graph(ArrayList<Point> points,Evaluation eval) {
		this.eval=eval;
		for (Point point : points) {
			put(point, eval.neighbor(point, points));
		}
	}

	/**
	 * Supprime un point du graphe
	 * @param point Point du graphe à supprimer
	 * @return true si le point a ete supprime, false sinon
	 */
	public boolean delect(Point point){
		for(Point p:get(point)){
			get(p).remove(point);
		}

		return remove(point)!=null;
	}

	/**
	 * Cherche un cycle semidisjoint
	 * @return Liste de point constituant un cycle semidisjoint dans le graphe, null si il y en a pas
	 */
	public ArrayList<Point> findSemidisjointCycle(){

		List<Point> visited=new ArrayList<>(); //liste de point visité
		HashMap<Point, Point> fatherMap=new HashMap<>();
		Stack<Point> toVisite=new Stack<>(); //liste de point à visité
		toVisite.addAll(keySet()); //toute les clefscar graph possible non connexe
		ArrayList<Point> res=null;

		while(!toVisite.isEmpty()){
			Point inVisite=toVisite.pop();

			if(!visited.contains(inVisite)){
				visited.add(inVisite);
				
				ArrayList<Point> childrens=getChildren(inVisite, fatherMap.get(inVisite));
				for(Point n:childrens){
					if(visited.contains(n)){
						res=backUp(fatherMap, inVisite, n);
						if(countException(res)<2)
							return res;
						else
							res=null;
					}else{
						fatherMap.put(n, inVisite);
						toVisite.push(n);
					}
				}
			}
		}

		return res;
	}

	/**
	 * Retourne les fils d'un point du graphe
	 * @param point Point considere
	 * @param parent Parent du point considere
	 * @return La liste des points voisins du point considere prive de son parent
	 */
	private ArrayList<Point> getChildren(Point point, Point parent){
		ArrayList<Point> children = new ArrayList<>(get(point));
		children.remove(parent);
		return children;
	}


	/**
	 * Calcule le degres d'un point
	 * @param p Point considere
	 * @return le degre du point considere dans le graphe
	 */
	public int degre(Point p){
		return get(p).size();
	}

	/**
	 * Compte le nombre d'exception dans une liste de point
	 * @param points Liste de point consideres dans le graphe
	 * @return Le nombre de points ayant un degre strictement superieur a 2 dans la liste de point
	 */
	public int countException(List<Point> points){
		int count=0;
		for(Point p:points){
			if(degre(p)>2)
				count++;
		}
		return count;
	}

	/**
	 * Supprime tout les points du graphe ayant un degre strictement inferieure a 2
	 */
	public void cleanup(){
		boolean wasCleanup;

		do{
			wasCleanup=false;
			Set<Point> keys=keySet();
			Set<Point> keysToDelete=new HashSet<Point>();
			for(Point k:keys){
				if(degre(k)<2){
					keysToDelete.add(k);
				}
			}
			for(Point k:keysToDelete){
				wasCleanup=delect(k);
			}

		}while(wasCleanup);
	}

	/**
	 * Calcule le chemin a effectuer d'un point a un autre celon une traces
	 * @param traces HashMap representant une trace ou a partir d'un point on obtient sont predecesseur  
	 * @param from Point de depart dans traces
	 * @param to Point d'arrive dans traces
	 * @return La liste de points constituant le chemin de from à to dans traces
	 */
	public ArrayList<Point> backUp(HashMap<Point, Point> traces,Point from ,Point to){
		ArrayList<Point> res=new ArrayList<>();
		while(!from.equals(to)){
			res.add(from);
			from=traces.get(from);
		}

		return res;

	}
}
