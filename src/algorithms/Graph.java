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
	
	public ArrayList<Point> findSemidisjointCycle2(){
		ArrayList<Point> res = new ArrayList<>();

        //check if there are no cycles at all
		if(!possibleToHaveSemiDisjointCycle()){
			return res;
		}
		if(eval.isValide(new ArrayList<>(keySet()), res)){
			return res;
		}

        List<Point> validPoints = new LinkedList<>();
        for(Point p: keySet()){
            if(degre(p) == 2){
                validPoints.add(p);
            }
        }

        //startPoint is the root of the tree we will explore
        for(Point startPoint : validPoints) {
            Point parent = null;
            //stack with the lists of points to visit at each level
            Stack<LinkedList<Point>> pointsToVisit = new Stack<>();
            //current trace to find a cycle
            LinkedList<Point> currentTrace = new LinkedList<Point>();
            //the root will always stay in the trace till the end
            currentTrace.add(startPoint);
            //pushing to stack all the children
            LinkedList<Point> children = getChildren(startPoint, parent);
            pointsToVisit.push(children);

            //while we still have points to visit
            outer:
            while (!pointsToVisit.isEmpty()) {
                //get the list of current level
                children = pointsToVisit.pop();

                //if no points left on this level - go back to the upper level
                if (children.isEmpty()) {
                    if (currentTrace.size() > 1) {
                        //removing the last parent point from the trace, if it is nit the root
                        currentTrace.removeLast();
                    }
                    continue;
                }
                //reinitialise the parent's parent and the current parent
                parent = currentTrace.getLast();
                startPoint = children.removeFirst();

                //preventing the 2 exception points in the cycle
                int exceptionsCount = countException(currentTrace);
                if (exceptionsCount >= 2 || (exceptionsCount == 1 && degre(startPoint) > 2)) {
                    continue;
                }

                //pushing back the list of points of the current level
                //without the point we have taken to explore
                pointsToVisit.push(children);

                //exploring children
                children = getChildren(startPoint, parent);
                if (!children.isEmpty()) {
                    //checking all the children if they are making the cycle
                    for (Point child : children) {
                        if (currentTrace.contains(child)) {
                            List<Point> foundCycle = currentTrace.subList(currentTrace.indexOf(child), currentTrace.size());
                            if (countException(foundCycle) < 2) {
                                res = new ArrayList<Point>();
                                break outer;
                            }
                        }
                    }
                    //no cycle found, going down to the next level
                    //last point become a parent
                    currentTrace.addLast(startPoint);
                    //pushing the next level of points to visit
                    pointsToVisit.push(children);
                }
            }
            //remove the root
            currentTrace.removeLast();
        }
		
		return res;
	}
	
	private LinkedList<Point> getChildren(Point point, Point parent){
		LinkedList<Point> children = new LinkedList<>(get(point));
		children.remove(parent);
		return children;
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
