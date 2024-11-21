import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings("unused")
final class Graph {
	/// Represents a graph where each key is a node and the value is the set of nodes that that node points to.
	private final Map<String, Set<String>> incidenceLists = new HashMap<>();
	// Maps the name of a vertex onto an object containing metadata about it.
	private final Map<String, Integer> inDegrees = new HashMap<>();

	public void addVertex(String nodeName) {
		if (nodeName == null) {
			throw new IllegalArgumentException("Node name must not be null.");
		}
		if (incidenceLists.containsKey(nodeName) || inDegrees.containsKey(nodeName)) {
			throw new IllegalArgumentException("Node name must be unique.");
		}
		incidenceLists.putIfAbsent(nodeName, new HashSet<>());
		inDegrees.putIfAbsent(nodeName, 0);
	}

	public void addEdge(String key, String value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("Key and value must not be null.");
		}
		if (!incidenceLists.containsKey(key)) {
			throw new IllegalArgumentException("Key must be a node.");
		}
		if (!inDegrees.containsKey(value)) {
			throw new IllegalArgumentException("Value must be a node.");
		}
		incidenceLists.get(key).add(value);
		inDegrees.computeIfPresent(value, (_, v) -> v + 1);
	}

	/// Returns a list of nodes in topological order by name.
	public List<String> topSort() throws CycleDetectedException {
		Queue<String> q = new LinkedList<String>();
		int counter = 0;
		List<String> result = new ArrayList<>();

		// Clone the in-degrees map to avoid modifying the original map.
		Map<String, Integer> inDegrees = new HashMap<>(this.inDegrees);

		for (Map.Entry<String, Integer> entry : inDegrees.entrySet()) {
			String name = entry.getKey();
			int inDegree = entry.getValue();
			if (inDegree == 0) {
				q.add(name);
			}
		}
		while (!q.isEmpty()) {
			String parentVertex = q.poll();
			++counter;
			result.add(parentVertex);
			for (String childVertex : incidenceLists.get(parentVertex)) {
				inDegrees.computeIfPresent(childVertex, (_, v) -> v - 1);
				if (inDegrees.get(childVertex) == 0) {
					q.add(childVertex);
				}
			}
		}
		if (counter != inDegrees.size()) {
			throw new CycleDetectedException();
		}
		return result;
	}
}

public class Ex2 {
	public static void main(String[] args) throws IOException, FileFormatException {

		// Choose a file in the folder Graphs in the current directory
		JFileChooser jf = new JFileChooser("Graphs");
		int result = jf.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jf.getSelectedFile();

			readGraph(selectedFile); // Read nodes and edges from the selected file
		}
	}

	// Read in a graph from a file and print out the nodes and edges
	public static void readGraph(File selectedFile) throws IOException, FileFormatException {

		BufferedReader r = new BufferedReader(new FileReader(selectedFile));
		String line = null;
		Graph graph = new Graph();
		try {
			// Skip over comment lines in the beginning of the file
			while (!(line = r.readLine()).equalsIgnoreCase("[Vertex]")) {
			}
			System.out.println();
			System.out.println("Nodes:");

			// Read all vertex definitions
			while (!(line = r.readLine()).equalsIgnoreCase("[Edges]")) {
				if (line.trim().length() > 0) { // Skip empty lines
					try {
						// Split the line into a comma separated list V1,V2 etc
						String[] nodeNames = line.split(",");

						for (String n : nodeNames) {
							System.out.println(n.trim()); // Trim and print the node name

							// Here you should create a node in the graph
							graph.addVertex(n.trim());
						}

					} catch (Exception e) { // Something wrong in the graph file
						r.close();
						throw new FileFormatException("Error in vertex definitions");
					}
				}
			}

		} catch (NullPointerException e1) { // The input file has wrong format
			throw new FileFormatException(
					" No [Vertex] or [Edges] section found in the file " + selectedFile.getName());
		}

		System.out.println();
		System.out.println("Edges:");
		// Read all edge definitions
		while ((line = r.readLine()) != null) {
			if (line.trim().length() > 0) { // Skip empty lines
				try {
					String[] edges = line.split(","); // Edges are comma separated pairs e1:e2

					for (String e : edges) { // For all edges
						String[] edgePair = e.trim().split(":"); // Split edge components v1:v2
						System.out.println(edgePair[0].trim() + " " + edgePair[1].trim());

						// Here you should create an edge in the graph
						graph.addEdge(edgePair[0].trim(), edgePair[1].trim());
					}

				} catch (Exception e) { // Something is wrong, Edges should be in format v1:v2
					r.close();
					throw new FileFormatException("Error in edge definition");
				}
			}
		}
		r.close(); // Close the reader
		List<String> nodes = null;
		try {
			nodes = graph.topSort(); // Topologically sort the graph.
		} catch (CycleDetectedException e) {
			System.out.println("Cycle detected in the graph");
			return;
		}
		System.out.println();
		System.out.println();
		System.out.println("Graph nodes in topological order:");
		for (String n : nodes) {
			System.out.println(n);
		}
		System.out.println("End of graph");
	}
}

@SuppressWarnings("serial")
class FileFormatException extends Exception { // Input file has the wrong format
	public FileFormatException(String message) {
		super(message);
	}
}

@SuppressWarnings("serial")
class CycleDetectedException extends Exception { // A cycle was detected in the graph
	public CycleDetectedException() {
		super();
	}
}
