import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;

public class Ex2 {
	public static void main(String[] args) throws IOException, FileFormatException {

		// Choose a file in the folder Graphs in the current directory
		JFileChooser jf = new JFileChooser("Graphs");
		int result = jf.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jf.getSelectedFile();
			// System.out.println("Selected file: " + selectedFile.getAbsolutePath()); //
			// Debug

			readGraph(selectedFile); // Read nodes and edges from the selected file
		}
	}

	// Read in a graph from a file and print out the nodes and edges
	public static void readGraph(File selectedFile) throws IOException, FileFormatException {

		BufferedReader r = new BufferedReader(new FileReader(selectedFile));
		String line = null;

		try {
			// Skip over comment lines in the beginning of the file
			while (!(line = r.readLine()).equalsIgnoreCase("[Vertex]")) {
			}
			;
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
					}

				} catch (Exception e) { // Something is wrong, Edges should be in format v1:v2
					r.close();
					throw new FileFormatException("Error in edge definition");
				}
			}
		}
		r.close(); // Close the reader
	}

}

@SuppressWarnings("serial")
class FileFormatException extends Exception { // Input file has the wrong format
	public FileFormatException(String message) {
		super(message);
	}
}
