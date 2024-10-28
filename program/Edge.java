package program;

import java.util.List;
import java.util.ArrayList;

// Edge that weights the total amount of shared function points between two nodes
public class Edge{
	Node node1, node2;
	int weight = 0;
	List<TypeOfFunction> functions;
	
	public Edge(String name, Node node1, Node node2, int weight) throws Exception{		//Tener cuidado con si se reutiliza el nombre de un tipo de función existente con un peso distinto
		this.node1 = node1;
		this.node2 = node2;
		this.functions = new ArrayList<TypeOfFunction>(10);
		int i = 0;
		while(i < FPAnalyzer.allFunctions.size() && !FPAnalyzer.allFunctions.get(i).name.equals(name)){
			++i;
		}
		if(i < FPAnalyzer.allFunctions.size()){
			TypeOfFunction function = FPAnalyzer.allFunctions.get(i);
			this.functions.add(function);
			//FPAnalyzer.allFunctions.get(i).addNode(node1);		// No importa si el nodo ya estaba
			//FPAnalyzer.allFunctions.get(i).addNode(node2);		// No importa si el nodo ya estaba
			this.weight = this.weight;
			node1.addEdge(this, function);
			node2.addEdge(this, function);
		}
		else{
			TypeOfFunction function = new TypeOfFunction(name, weight, node1, node2, this);
			this.functions.add(function);
			this.weight = this.weight + weight;
			node1.addEdge(this, function);
			node2.addEdge(this, function);
		}
	}
	
	// Adds shared types of functions between the same pair of nodes
	public void addTypeOfFunction(TypeOfFunction function){		// Tener cuidado con si se añade el mismo tipo de función
		functions.add(function);
		node1.functions.add(function);
		node2.functions.add(function);
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
}