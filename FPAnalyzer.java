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
	public static int total = 0;
	
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
			if (isSelected){
				updateTotal(value);
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
				updateTotal(weight);
			}
			else{
				updateTotal(-weight);
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
	
	public static class TypeOfFunction{
		String name;
		int weight;
		List<Node> nodes;
		List<Edge> edges;
		Node implementingNode;	// It is the node that assumes this function's weight. It must be a selected node. If no node is implementing this function, this attribute will be null.
		int isImplemented = 0;	// Number of nodes selected
		
		// Creates a type of function without associated nodes.
		public TypeOfFunction(String name, int weight) throws Exception{
			System.out.println("Created without nodes");
			if (weight < 0){
				throw new Exception();
			}
			this.name = name;
			this.weight = weight;
			this.nodes = new ArrayList<Node>(10);
			this.edges = new ArrayList<Edge>(10);
			System.out.println("Reached the end");
			debugTypeOfFunction();
		}
		
		// Creates a type of function with one node. Used when creating types of function for an specific node.
		public TypeOfFunction(String name, int weight, Node node) throws Exception{
			System.out.println("Created with one node");
			if (weight < 0){
				throw new Exception();
			}
			this.name = name;
			this.weight = weight;
			this.nodes = new ArrayList<Node>(10);
			this.edges = new ArrayList<Edge>(10);
			addNode(node);
			System.out.println("Reached the end");
			debugTypeOfFunction();
		}
		
		// Creates a type of function with two nodes. Used when two nodes are connected mannualy.
		public TypeOfFunction(String name, int weight, Node node1, Node node2, Edge edge) throws Exception{
			System.out.println("Created with two nodes");
			if (weight < 0){
				throw new Exception();
			}
			this.name = name;
			this.weight = weight;
			this.nodes = new ArrayList<Node>(10);
			this.edges = new ArrayList<Edge>(10);
			addNode(node1);
			addNode(node2);
			this.edges.add(edge);
			System.out.println("Reached the end");
			debugTypeOfFunction();
		}
		
		// Creates a type of function with several nodes. Used when assigning a group of nodes to a non-existing type of function.
		/*
		public TypeOfFunction(String name, int weight, List<Node> nodes){
			this.name = name;
			this.weight = weight;
			this.nodes = new ArrayList<Node>(10);
			for (int i = 0; i < nodes.size(); ++i){
				addNode(nodes.get(i));
			}
		}
		*/
		
		// Adds a node that uses this type of function.
		public boolean addNode(Node node){
			boolean wasAdded = nodes.add(node);
			if (implementingNode != null){
				node.addWeight(-this.weight);
				if (node.isSelected){
					this.isImplemented += 1;
				}
			}
			else{
				if (node.isSelected){
					this.isImplemented += 1;
					implementingNode = node;
					updateNodes();
				}
			}
			
			/*Son equivalentes
			if (isSelected){
				this.isImplemented += 1;
				if (implementingNode != null){
					node.addWeight(-this.weight);
				}
				else{
					implementingNode = node;
					updateNodes();				//Puede ser que todos los nodos de la red estén desactivados
				}
			}
			else if(!isSelected && implementingNode != null){
				node.addWeight(-this.weight);
			}
			*/
			return wasAdded;
		}
		
		// Removes a node that used this type of function.
		public boolean removeNode(Node node){
			boolean wasRemoved = nodes.remove(node);
			if (wasRemoved){
				if (node.isSelected){
					this.isImplemented -= 1;
					if (implementingNode == node){
						chooseNewImplementingNode();
						if (implementingNode != null){
							implementingNode.addWeight(this.weight);
						}
						else{
							updateNodes();
						}
					}
					else if(implementingNode != node && implementingNode != null){
						node.addWeight(this.weight);
					}
				}
				else{
					if (implementingNode != node && implementingNode != null){
						node.addWeight(this.weight);
					}
				}
			}
			return wasRemoved;
		}
		
		// Chooses a new node to assume this function's weight.
		private void chooseNewImplementingNode(){
			int i = 0;
			while (i < nodes.size() && !nodes.get(i).isSelected){
				i++;
			}
			if (i < nodes.size()){
				implementingNode = nodes.get(i);
				nodes.get(i).addWeight(this.weight);
			}
			else{
				implementingNode = null;
			}
		}
		
		// Updates the number of nodes that are implementing this function based on if node is selected or not, and assigns an implementing node if there is none.
		public void nodeWasSelected(Node node){
			if(nodes.contains(node)){
				if (node.isSelected){
					this.isImplemented += 1;
					if (implementingNode == null){
						implementingNode = node;
						updateNodes();
					}
				}
				else{
					this.isImplemented -= 1;
					if (implementingNode == node){
						chooseNewImplementingNode();
						node.addWeight(-this.weight);
						if (implementingNode == null){
							updateNodes();
						}
					}
				}
				debugTypeOfFunction();
			}
		}
		
		// Updates, if necessary, all nodes that use this type of function, except for the implementing node. This is called inmediatly after a node implementing this function has changed its state or when a node has added or removed this type of function.
		public int updateNodes(){
			int numberOfNodesUpdated = 0;
			if (isImplemented == 1){		// This type of function has just been implemented -> Update nodes
				for(int i = 0; i < nodes.size(); ++i){
					if (nodes.get(i) != implementingNode){
						nodes.get(i).addWeight(-this.weight);
						numberOfNodesUpdated += 1;
					}
				}
			}
			else if (isImplemented == 0){	// This type of function has just stopped being implemented -> Update nodes
				for(int i = 0; i < nodes.size(); ++i){
					if (nodes.get(i) != implementingNode){
						nodes.get(i).addWeight(this.weight);
						numberOfNodesUpdated += 1;
					}
				}
			}
			return numberOfNodesUpdated;
		}
		
		private void debugTypeOfFunction(){
			System.out.println("\tName = " + this.name);
			System.out.println("\tWeight = " + this.weight);
			for(int i = 0; i < nodes.size(); ++i){
				System.out.println("\t" + nodes.get(i).name);
			}
			/*
			for(int i = 0; i < edges.size(); ++i){
				System.out.println("\t[" + edges.get(i).node1.name + ", " + edges.get(i).node2.name + "]");
			}
			*/
			if(implementingNode == null){
				System.out.println("\timplementingNode = " + this.implementingNode);
			}
			else{
				System.out.println("\timplementingNode = " + this.implementingNode.name);
			}
			System.out.println("\tisImplemented = " + this.isImplemented);
		}
	}
	
	// Edge that weights the total amount of shared function points between two nodes
	public static class Edge{
		Node node1, node2;
		int weight = 0;
		List<TypeOfFunction> functions;
		
		public Edge(String name, Node node1, Node node2, int weight) throws Exception{		//Tener cuidado con si se reutiliza el nombre de un tipo de función existente con un peso distinto
			this.node1 = node1;
			this.node2 = node2;
			this.functions = new ArrayList<TypeOfFunction>(10);
			int i = 0;
			while(i < allFunctions.size() && !allFunctions.get(i).name.equals(name)){
				++i;
			}
			if(i < allFunctions.size()){
				TypeOfFunction function = allFunctions.get(i);
				this.functions.add(function);
				//allFunctions.get(i).addNode(node1);		// No importa si el nodo ya estaba
				//allFunctions.get(i).addNode(node2);		// No importa si el nodo ya estaba
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
	public static Edge createEdge(String name, Node node1, Node node2, int weight) throws Exception{
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
	
	public static void updateTotal(int value){
		total += value;
		System.out.println("Nuevo total: " + total);
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("Inicio del programa.");
		// Se inician las ventanas
		
		// Se inician las estructuras de datos
		allNodes = new ArrayList<Node>(10);
		allEdges = new ArrayList<Edge>(10);
		allFunctions = new ArrayList<TypeOfFunction>(10);
		
		// Se ejecuta el programa
		Node rq1 = createNode("RQ 1", 20);
		Node rq2 = createNode("RQ 2", 30);
		//Node rq3 = createNode("RQ 3", 23);
		//Node rq4 = createNode("RQ 4", 47);
		
		createEdge("ILF1", rq1, rq2, 7);
		System.out.println("Fus");
		//createEdge("ILF2", rq1, rq3, 4);
		//createEdge("ILF3", rq1, rq4, 5);
		
		System.out.println("1. RQ1 = " + rq1.weight);
		System.out.println("1. RQ2 = " + rq2.weight);
		rq1.select();
		System.out.println("2. RQ1 = " + rq1.weight);
		System.out.println("2. RQ2 = " + rq2.weight);
		rq2.select();
		System.out.println("3. RQ1 = " + rq1.weight);
		System.out.println("3. RQ2 = " + rq2.weight);
		rq1.select();
		System.out.println("4. RQ1 = " + rq1.weight);
		System.out.println("4. RQ2 = " + rq2.weight);
		rq1.select();
		System.out.println("5. RQ1 = " + rq1.weight);
		System.out.println("5. RQ2 = " + rq2.weight);
		updateTotal(0);
		System.out.println("6. RQ1 = " + rq1.weight);
		System.out.println("6. RQ2 = " + rq2.weight);
		rq2.select();
		System.out.println("7. RQ1 = " + rq1.weight);
		System.out.println("7. RQ2 = " + rq2.weight);
		rq1.select();
		System.out.println("8. RQ1 = " + rq1.weight);
		System.out.println("8. RQ2 = " + rq2.weight);
		
		
		// Revisar si funciona bien la inicialización de un TypeOfFunction si lo creo con un List<Node>. Si no funciona correctamente será porque la lista original se borra.
	}
}