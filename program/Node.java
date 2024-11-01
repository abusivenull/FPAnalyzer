package program;

import java.lang.StringBuilder;
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
	
	// Connects this node to an edge
	public void addEdge(Edge edge){
		this.edges.add(edge);
	}
	
	// Adds a function to this node
	public void addFunction(TypeOfFunction function){
		if (!functions.contains(function)){
			this.functions.add(function);
		}
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
	
	// Deletes this node
	public void delete(){
		for (int i = 0; i < functions.size(); ++i){
			functions.get(i).removeNode(this);
		}
		this.functions = null;
		for (int i = 0; i < edges.size(); ++i){
			edges.get(i).delete();
		}
		this.edges = null;
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
	
	public String toString(){
		StringBuilder builder = new StringBuilder(this.name + "\nWeight: " + this.weight + "\nSelected: " + this.isSelected + "\nUses functions:\n");
		for(int i = 0; i < this.functions.size(); ++i){
			builder.append(functions.get(i).name + "\n");
		}
		return builder.toString();
	}
}