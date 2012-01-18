import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {

		Storage storage = new Storage();
		
		Scanner sc = new Scanner(System.in);
		int input;
		int id;
		int[] sourceNodeIds;
		
		
		
		Main.ui();
		ui:
		{
			while (true) {
				System.out.println();
				System.out.print("Your input: ");
				
try {
				input = sc.nextInt();
				
				switch(input) {
				case 0:
					Main.ui();
					break;
					
				// 1. Load input file and distribute to nodes
				case 1:
					byte[][] parts = storage.getInputFile(Arguments.path_input);
					System.out.println("Input file has been loaded.");				
					storage.distributePieces(parts);
					System.out.println("Input file has been distributed to nodes.");
					break;
					
				// 2. Construct files from alive nodes
				case 2:
					if (storage.generateFile(Arguments.path_output)) 
						System.out.println("A file has been recontructed from alive nodes.");
					break;
				
				// 3. Kill a node
				case 3:					
					System.out.print("Input node id to delete : ");
					id = sc.nextInt();
					storage.deleteNode(id);
					break;
				
				// 4. Reconstruct a dead node
				case 4:
					storage.showStates();
					System.out.print("Select id of the node to reconstruct: ");
					id = sc.nextInt();
					System.out.print("How many nodes you want to use. 2 or 3? ");
					int numNodes = sc.nextInt();					
					if (!(numNodes == 2 || numNodes == 3 )) {
						System.err.println("Error: invalid number of nodes. Select 2 or 3 only.");
						break;
					}
					
					sourceNodeIds= new int[numNodes];
					System.out.print("Input id of the source nodes: ");	
				
					for(int i=0; i<numNodes; i++ )
						sourceNodeIds[i] = sc.nextInt();
					
					if (storage.reconstructNode(id, sourceNodeIds, numNodes))
						System.out.println("Node " + id + " has been reconstructed succesfully.");
					break;
				
				// 5. Show status of nodes
				case 5:
					storage.showStates();
					break;
					
				default:
					System.out.println("Bye. :)");	
					break ui;
				}
}
catch (InputMismatchException ex){
	//System.err.println("Error: invalid input.");

}
			}
		}
		
	}

	public static void ui() {
		System.out.println("==============================================");
		System.out.println("Welcome to Storage System");
		System.out.println("1. Load input file and distribute to nodes");
		System.out.println("2. Construct files from alive nodes");
		System.out.println("3. Kill a node");
		System.out.println("4. Reconstruct a dead node");
		System.out.println("5. Show status of nodes");
		System.out.println("Please select your input");
		System.out.println("==============================================");

	}

}
