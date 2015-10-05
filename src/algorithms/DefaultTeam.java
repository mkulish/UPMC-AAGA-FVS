package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DefaultTeam {

	public ArrayList<Point> calculFVS(ArrayList<Point> points) {
		ArrayList<Point> F=new ArrayList<>();
		HashMap<Point, Double> w=new HashMap<>();
		Stack<Point> stack=new Stack<>();
		
		for(Point p:points){
			w.put(p, 1.0);
		}
		Evaluation eval= new Evaluation();
		Graph g=new Graph(points,eval);

		g.cleanup();

		System.out.println("debut while");
		while(!g.keySet().isEmpty()){
			
			System.out.println("find cycle");
			ArrayList<Point> C=g.findSemidisjointCycle2();
			ArrayList<Point> V=new ArrayList<>(g.keySet());
			
			if(!C.isEmpty()){
				System.out.println("cycle found : "+C);
				Double l=minW(C, w, g,0);

				for(Point u:C){
					w.put(u, w.get(u)-l);
				}
			}else{
				System.out.println("no cycle");
				Double l=minW(new ArrayList<>(g.keySet()), w, g, 1);
				
				for(Point u:V){
					w.put(u, w.get(u)-l*(g.degre(u)-1));
				}
			}
			
			for(Point u:V){
				if(w.get(u)==0){
					g.delect(u);
					F.add(u);
					stack.push(u);
				}
			}
			System.out.println("cleanup");
			g.cleanup();
		}
		
		System.out.println("stack");
		while(!stack.isEmpty()){
			Point u=stack.pop();
			F.remove(u);
			if(!eval.isValide(points, F))
				F.add(u);
		}

		return F;

	}


	public double minW(ArrayList<Point> points,HashMap<Point, Double> w, Graph g, int mode){
		Double min=Double.MAX_VALUE;
		Double val;

		for (Point point : points) {
			val=w.get(point)/((1.0 - mode) + mode*(g.degre(point) - 1));
			if(val<min)
				min=val;
		}
		return min;
	}
}
