package program;

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
	
	// Creates a node.
	public static Node createNode(String name, int weight){
		// Do graphical stuff
		Node node = new Node(name, weight);
		allNodes.add(node);
		return node;
	}
	
	// Deletes a node
	public static boolean deleteNode(Node node){
		// Do graphical stuff
		node.delete();
		return allNodes.remove(node);
	}
	
	public static TypeOfFunction createTypeOfFunction(String functionName, int functionWeight, Node node1, Node node2) throws Exception{
		TypeOfFunction function = new TypeOfFunction(functionName, functionWeight, node1, node2);
		allFunctions.add(function);
		return function;
	}
	
	// Deletes a function
	public static boolean deleteTypeOfFunction(TypeOfFunction function){
		function.delete();
		return allFunctions.remove(function);
	}
	
	// Creates an edge between two nodes with a function.
	public static Edge createEdge(TypeOfFunction function, Node node1, Node node2) throws Exception{
		// Do graphical stuff
		Edge edge = new Edge(function, node1, node2);
		node1.addEdge(edge);
		node2.addEdge(edge);
		node1.addFunction(function);
		node2.addFunction(function);
		function.addNode(node1);
		function.addNode(node2);
		function.addEdge(edge);
		allEdges.add(edge);
		return edge;
	}
	
	public static boolean deleteEdge(Edge edge){
		// Do graphical stuff
		edge.delete();
		return allEdges.remove(edge);
	}
	
	public static void updateTotal(int value){
		total += value;
	}
	
	/*
	public static void showFunctions(){
		for (int i = 0; i < allFunctions.size(); ++i){
			allFunctions.get(i).debugTypeOfFunction();
		}
	}
	*/
	
	public static void showNodes(){
		System.out.println("\nActual state of the graph:");
		System.out.println("Total = " + total);
		for(int i = 0; i < allNodes.size(); ++i){
			System.out.println(allNodes.get(i));
		}
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