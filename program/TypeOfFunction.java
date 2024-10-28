package program;

import java.util.List;
import java.util.ArrayList;

public class TypeOfFunction{
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