package program;

import java.util.List;
import java.util.ArrayList;

// Edge that weights the total amount of shared function points between two nodes
public class Edge{
	Node node1, node2;
	int weight = 0;
	List<TypeOfFunction> functions;
	
	// Creates an edge to link two nodes using an existing function
	public Edge(TypeOfFunction function, Node node1, Node node2){
		this.node1 = node1;
		this.node2 = node2;
		this.functions = new ArrayList<TypeOfFunction>(10);
		this.functions.add(function);
		//FPAnalyzer.allFunctions.get(i).addNode(node1);		// No importa si el nodo ya estaba
		//FPAnalyzer.allFunctions.get(i).addNode(node2);		// No importa si el nodo ya estaba
	}
	
	// Adds shared types of functions between the same pair of nodes
	public void addTypeOfFunction(TypeOfFunction function){		// Tener cuidado con si se añade el mismo tipo de función
		functions.add(function);
		weight += function.weight;
		node1.addFunction(function);
		node2.addFunction(function);
	}
	
	// Adjust the other edge's node's weight.
	public void updateNodes(Node node){
		if (node == node1){
			if (node1.isSelected){
				for (int i = 0; i < functions.size(); ++i){
					node2.addWeight(-functions.get(i).updateNodes());
				}
			}
			else{
				for (int i = 0; i < functions.size(); ++i){
					node2.addWeight(functions.get(i).weight);
				}
			}
		}
		if (!node.isSelected){
			//this.weight
		}
		System.out.println("El nombre de quien me ha invocado es " + node.name);
	}
	
	// Deletes this edge, updating its nodes
	public void delete(){
		// Recalculate all affected nodes values
	}
}