package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Graph extends HashMap<Point, ArrayList<Point>>{

	Evaluation eval;
	public Graph(ArrayList<Point> points,Evaluation eval) {
		this.eval=eval;
		for (Point point : points) {
			put(point, eval.neighbor(point, points));
		}
	}

	public boolean delect(Point point){
		for(Point p:get(point)){
			get(p).remove(point);
		}

		return remove(point)!=null;
	}

	public ArrayList<Point> findSemidisjointCycle(){
		List<Point> visited=new ArrayList<>();
		Stack<Point> toVisite=new Stack<>();
		toVisite.addAll(keySet());
		ArrayList<Point> res=new ArrayList<>();

		
		if(!possibleToHaveSemiDisjointCycle()){
			return res;
		}
		if(eval.isValide(new ArrayList<>(keySet()), res)){
			return res;
		}
		
		while(!toVisite.isEmpty()){
			Point p=toVisite.pop();
			ArrayList<Point> neighbor=get(p);

			//compte le nombre de voisins visite
			int count=0;
			for(Point n:neighbor){
				if(visited.contains(n))
					count++;
			}

			//tout les voisins visites
			boolean isSecond=false;
			if(count==neighbor.size()){
				isSecond=true;
			}

			//si deuxieme  pop de p
			if(isSecond){
				ArrayList<Point> childs=new ArrayList<>();
				for(Point n:neighbor){
					int myIndex=visited.indexOf(p);
					if(visited.indexOf(n)>myIndex)
						childs.add(n);
				}
				visited.removeAll(childs);
			}else{
				//si premier pop de p
				if(visited.contains(p)){
					res=new ArrayList<>(visited.subList(visited.indexOf(p), visited.size()));
					if(countException(res)<2)
						return res;
					else
						res.clear();
				}else{
					visited.add(p);
					toVisite.push(p);
					for(Point n:neighbor){
						if(!visited.contains(n))
							toVisite.push(n);
					}
				}
			}
		}	

		return res;
	}


	public int degre(Point p){
		return get(p).size();
	}

	public int countException(List<Point> points){
		int count=0;
		for(Point p:points){
			if(degre(p)>2)
				count++;
		}
		return count;
	}

	public boolean possibleToHaveSemiDisjointCycle(){
		LinkedList<Point> allowedPoints = new LinkedList<>();

		for(Point p : keySet()){
			if(degre(p) == 2){
				for(Point alreadyAdded : allowedPoints){
					if(get(p).contains(alreadyAdded)){
						return true;
					}
				}
				allowedPoints.add(p);
			}
		}

		return false;
	}

	public void cleanup(){
		boolean wasCleanup;

		do{
			wasCleanup=false;
			Set<Point> keys=keySet();
			Set<Point> keysToDelete=new HashSet<Point>();
			for(Point k:keys){//int i=0;i<keys.size();i++
				if(degre(k)<2){
					keysToDelete.add(k);//i--;
				}
			}
			for(Point k:keysToDelete){
				wasCleanup=delect(k);
			}
			
		}while(wasCleanup);
	}
}
