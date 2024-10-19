import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

// Los nodos son los requisitos
// Las aristas son los puntos de función compartidos entre requisitos
public class FPAnalyzer{
	public static List<Node> allNodes;		// Esto se puede mantener como List. Que sea el usuario el que se encargue de asegurarse de que no repite nombres.
	public static List<Edge> allEdges;
	public static List<TypeOfFunction> allFunctions;
	
	public static class Node{
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
		}
		
		public void addFunction(TypeOfFunction function){
			this.functions.add(function);
		}
		
		// Connects this node to an edge
		public void addEdge(Edge edge){
			this.edges.add(edge);
		}
		
		// Changes node's state
		public void select(){
			isSelected = !isSelected;
			for (int i = 0; i < functions.size(); ++i){
				functions.get(i).updateNodes(this);
			}
			for(int i = 0; i < this.edges.size(); ++i){
				this.edges.get(i).updateNodes(this);
			}
		}
		
		// Shows all edges
		public void showAllEdges(){
			System.out.println("My shared function types are:");
			for(int i = 0; i < edges.size(); ++i){
				System.out.println(edges.get(i).function.name);
			}
		}
	}
	
	public static class TypeOfFunction{
		String name;
		int weight;
		List<Node> nodes;
		List<Edge> edges;
		int isImplemented = 0;
		
		// Creates a type of function without associated nodes.
		public TypeOfFunction(String name, int weight){
			if (weight < 0){
				throw new Exception();
			}
			this.name = name;
			this.weight = weight;
			this.nodes = new ArrayList<Node>(10);
			this.edges = new ArrayList(10);
		}
		
		// Creates a type of function without associated nodes. Used when creating types of function for an specific node.
		public TypeOfFunction(String name, int weight, Node node1){
			if (weight < 0){
				throw new Exception();
			}
			this.name = name;
			this.weight = weight;
			this.nodes = new ArrayList<Node>(10);
			this.nodes.add(node1);
			this.edges = new ArrayList(10);
			if (node1.isSelected){
				this.isImplemented += 1;
			}
		}
		
		// Creates a type of function with two nodes. Used when two nodes are connected mannualy.
		public TypeOfFunction(String name, int weight, Node node1, Node node2, Edge edge){
			if (weight < 0){
				throw new Exception();
			}
			this.name = name;
			this.weight = weight;
			this.nodes = new ArrayList<Node>(10);
			this.nodes.add(node1);
			this.nodes.add(node2);
			this.edges = new ArrayList(10);
			this.edges.add(edge);
			updateNodes(node1);
			updateNodes(node2);
		}
		
		// Creates a type of function with several nodes. Used when assigning a group of nodes to a non-existing type of function.
		/*
		public TypeOfFunction(String name, int weight, List<Node> nodes){
			this.name = name;
			this.weight = weight;
			this.nodes = new ArrayList<Node>(10);
			this.nodes = nodes;
			for (int i = 0; i < nodes.size(); ++i){
				updateNodes(nodes.get(i));
			}
		}
		*/
		
		// Adds a node that uses this type of function. Used when adding a node to an existing type of function.
		public boolean addNode(Node node){
			if (!nodes.contains(node)){
				nodes.add(node);
				if (this.isImplemented > 0){
					node.addWeight(-this.weight);
				}
				if (node.isSelected){
					isImplemented += 1;
					updateNodes(node);
				}
				return true;
			}
			else{
				return false;
			}
		}
		
		// Updates, if necessary, all nodes that use this type of function. This is called inmediatly after a node has changed its state or when a node has added this type of function.
		public int updateNodes(Node node){
			if (nodes.contains(node)){
				if (node.isSelected){
					this.isImplemented += 1;
				}
				else{
					this.isImplemented -= 1;
				}
				if (node.isSelected && isImplemented == 1){		// This type of function has just been implemented -> Update nodes
					for(int i = 0; i < nodes.size(); ++i){
						nodes.get(i).addWeight(-this.weight);
					}
				}
				else if (!node.isSelected && isImplemented == 0){	// This type of function has just stopped being implemented -> Update nodes
					for(int i = 0; i < nodes.size(); ++i){
						nodes.get(i).addWeight(this.weight);
					}
				}
			}
		}
	}
	
	// Edge that weights the total amount of shared function points between two nodes
	public static class Edge{
		Node node1, node2;
		int weight = 0;
		List<TypeOfFunction> functions;
		
		public Edge(String name, Node node1, Node node2, int weight){		//Tener cuidado con si se reutiliza el nombre de un tipo de función existente con un peso distinto
			this.functions = new ArrayList<TypeOfFunction>(10);
			int i = 0;
			while(i < allFunctions.size() && !allFunctions.get(i).name.equals(name)){
				++i;
			}
			if(i < allFunctions.size()){
				this.functions.add(allFunctions.get(i));
				allFunctions.get(i).addNode(node1);		// No importa si el nodo ya estaba
				allFunctions.get(i).addNode(node2);		// No importa si el nodo ya estaba
				this.weight = this.weight + allFunctions.get(i).updateNodes();
			}
			else{
				this.functions.add(new TypeOfFunction(name, weight, node1, node2));
				this.weight = this.weight + weight;
			}
			this.node1 = node1;
			this.node2 = node2;
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
				this.weight
			}
			System.out.println("El nombre de quien me ha invocado es " + node.name);
		}
	}
	
	// Creates a node.
	public static Node createNode(String name, int weight){
		Node node = new Node(name, weight);
		allNodes.add(node);
		return node;
	}
	
	// Deletes a node
	public static boolean deleteNode(Node node){
		// Do graphical stuff
		if (node.isSelected){
			// Recalculate all adjacent nodes values
		}
		// Update/erase all affected edges
		return allNodes.remove(node);
	}
	
	// Creates an edge between two nodes.
	public static Edge createEdge(String name, Node node1, Node node2, int weight){
		Edge edge = new Edge(name, node1, node2, weight);
		node1.edges.add(edge);
		node2.edges.add(edge);
		allEdges.add(edge);
		return edge;
	}
	
	public static boolean deleteEdge(Edge edge){
		// Do graphical stuff
		// Recalculate all affected nodes values
		return allEdges.remove(edge);
	}
	
	public static int obtainSum(){
		int sum = 0;
		for(int i = 0; i < allNodes.size(); ++i){
			if(allNodes.get(i).isSelected){
				sum += allNodes.get(i).weight;
			}
		}
		return sum;
	}
	
	public static void main(String[] args){
		System.out.println("Inicio del programa.");
		// Se inician las ventanas
		
		// Se inician las estructuras de datos
		allNodes = new ArrayList<Node>(10);
		allEdges = new ArrayList<Edge>(10);
		allFunctions = new ArrayList<TypeOfFunction>(10);
		
		// Se ejecuta el programa
		Node rq1 = createNode("RQ 1", 20);
		Node rq2 = createNode("RQ 2", 30);
		Node rq3 = createNode("RQ 3", 23);
		Node rq4 = createNode("RQ 4", 47);
		
		createEdge("ILF1", rq1, rq2, 7);
		createEdge("ILF2", rq1, rq3, 4);
		createEdge("ILF3", rq1, rq4, 5);
		
		rq1.select();
		//rq2.select();
		
		System.out.println("El valor total del programa es " + obtainSum());
		
		
		
		// Revisar si funciona bien la inicialización de un TypeOfFunction si lo creo con un List<Node>. Si no funciona correctamente será porque la lista original se borra.
	}
}