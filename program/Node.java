package program;

import java.util.List;
import java.util.ArrayList;

public class Node{
	String name;
	int weight;		// Es importante que el nodo tenga su propio weight para modelizar los tipos de función que no se comparten con ningún otro requisito (no hay arista)
	boolean isSelected = false;
	List<Edge> edges;
	List<TypeOfFunction> functions;
	
	public Node(String name, int weight){
		this.name = name;
		this.weight = weight;
		this.edges = new ArrayList<Edge>(10);
		this.functions = new ArrayList<TypeOfFunction>(10);
	}
	
	// Changes node's weight when adjacent node's state is changed
	public void addWeight(int value){
		this.weight = this.weight + value;
		if (isSelected){
			FPAnalyzer.updateTotal(value);
		}
	}
	
	/*
	public void addFunction(TypeOfFunction function){
		this.functions.add(function);
	}
	*/
	
	// Connects this node to an edge
	public void addEdge(Edge edge, TypeOfFunction function){
		this.edges.add(edge);
		this.functions.add(function);
	}
	
	// Changes node's state
	public void select(){
		isSelected = !isSelected;
		if (isSelected){
			FPAnalyzer.updateTotal(weight);
		}
		else{
			FPAnalyzer.updateTotal(-weight);
		}
		for (int i = 0; i < functions.size(); ++i){
			functions.get(i).nodeWasSelected(this);
		}
		/*	Edges still not implemented
		for(int i = 0; i < this.edges.size(); ++i){
			this.edges.get(i).updateNodes(this);
		}
		*/
	}
	
	// Shows all edges
	public void showAllEdges(){
		System.out.println("My shared function types are:");
		for(int i = 0; i < edges.size(); ++i){
			for (int j = 0; j < functions.size(); ++j){
				System.out.println(edges.get(i).functions.get(j).name);
			}
		}
	}
}