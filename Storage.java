import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Storage {
	
	int fileSize;
	int partSize;
	private Node[] nodes;	
	
	public Storage() {
		this.nodes = new Node[Arguments.NUMBER_OF_NODES];
		for (int id = 0; id < nodes.length; id++) {
			nodes[id] = new Node(id, 
					Arguments.basis_vectors[id], 
					Arguments.path_pieces[id], 
					Arguments.path_nodes[id]);
		}
	}

	/**
	 * Read input file from the localdisk and split it into four parts.
	 * Each parts will be an array of bytes.
	 * @param path_input
	 * @return parts
	 */
	public byte[][] getInputFile(String path_input) {

		File inputFile = new File(path_input);

		FileInputStream fileinputstream = null;
		byte bytearray[]; 
		byte parts[][] = null; 

		try {
			fileinputstream = new FileInputStream(inputFile);
			fileSize = fileinputstream.available();
			partSize = (int) Math.ceil(((double) fileSize) / 4);
			bytearray = new byte[fileSize];
			parts = new byte[4][partSize];
			
			fileinputstream.read(bytearray);
			
			int i;
			for (i = 0; i < 3; i++) {
				parts[i] = Arrays.copyOfRange(bytearray, i*partSize, (i+1)*partSize);
			}
			
			//dealing with the last part
			byte[] tmp = Arrays.copyOfRange(bytearray, 3*partSize, (int) fileSize);
			parts[i] = Arrays.copyOf(tmp, partSize);
			
			fileinputstream.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parts;
	}

	/**
	 * Create pieces from parts based on the pre-determined basis vectors
	 * @param parts
	 */
	public void distributePieces(byte[][] parts) {
		byte[][] pieces = new byte[Arguments.NUMBER_OF_PIECES][partSize];
	
		// Node 0 --> piece 0 and piece 1 : 1000 and 0110
		{
		pieces[0] = parts[0];
		byte[][] inputs = {parts[1], parts[2]};
		pieces[1] = xor(inputs);
		}
		
		// Node 1 --> piece 2 and piece 3 : 0100 and 0011
		{
		pieces[2] = parts[1];
		byte[][] inputs = {parts[2], parts[3]};
		pieces[3] = xor(inputs);
		}
		
		// Node 2 --> piece 4 and piece 5 : 0010 and 1101
		{
			pieces[4] = parts[2];
			byte[][] inputs = {parts[0], parts[1], parts[3]};
			pieces[5] = xor(inputs);
		}
		
		// Node 3 --> piece 6 and piece 7 : 0001 and 1010
		{
			pieces[6] = parts[3];
			byte[][] inputs = {parts[0], parts[2]};
			pieces[7] = xor(inputs);
		}
		
		// Nodes 4 --> piece 8 and piece 9 : 1100 and 0101 
		{
			byte[][] inputs0 = {parts[0], parts[1]};
			pieces[8] = xor(inputs0);
			byte[][] inputs1 = {parts[1], parts[3]};
			pieces[9] = xor(inputs1);
		}
		
		
		for (int i = 0; i < nodes.length; i++) {		
			for(int j = 0; j < 2; j++) {
				nodes[i].writePiece(pieces[2*i + j], j);
			}
		}
	}
	
	/**
	 * Helper method: to compute the xor of several arrays of bytes
	 * @param input
	 * @return xor
	 */
	private byte[] xor(byte[][] input) {
		byte[] output = new byte[input[0].length];
			
		for (int i = 0; i < input[0].length; i++) {
			byte tmp = (byte) 0;
			for (int j = 0; j < input.length; j++) {
				tmp ^= input[j][i];
			}
			output[i] = tmp;
			
		}
		return output;
	}
	
	/**
	 * Delete an alive node
	 * @param id of the node to be deleted
	 */
	public void deleteNode(int id) {
		if (id >= 0 && id < nodes.length)
			nodes[id].kill();
		else
			System.out.println("Error: invalid id.");
	}
	

	/**
	 * Reconstruct a node based on the specified nodes
	 * @param id
	 * @param sourceNodes
	 * @return successful or not
	 */
	public boolean reconstructNode(int id, int[] sourceNodes, int numNodes) {
		
		// checking for the input parameters
		if (id < 0 || id >= nodes.length) {
			System.err.println("Error: invalid id of node to reconstruct.");
			return false;
		}
		
		if (nodes[id].isAlive()) {
			System.err.println("Error: the node to reconstruct is alive.");
			return false;
		}
		
		for (int i = 0; i < sourceNodes.length; i++) {
			if (!nodes[sourceNodes[i]].isAlive()) {
				System.err.println("Error: invalid source nodes.");
				return false;
			}
		}
		
		// extracting the information for the input of the algorithm
		// datacode, basis vectors, and piece ids
		
		int dc[] = nodes[id].getBasisVectors();
		int bv[] = new int[2 * sourceNodes.length];
		int piece_ids[] = new int[2 * sourceNodes.length];
		int tmp[];
		for(int i=0; i < sourceNodes.length; i++){
			tmp = nodes[sourceNodes[i]].getBasisVectors();
			for(int j=0; j< 2; j++) {
				 bv			[2 * i + j] = tmp[j];
				 piece_ids	[2 * i + j] = 2 * sourceNodes[i] + j;
			}
				 
		}
		
		// executing the algorithm to obtain the formula
		
		int[][] formula = null;
		if (numNodes == 2) {
			formula = Algo.determineComponent(bv, dc); 			
		} 
		
		else if (numNodes == 3) {
			// do for loop, create a group of 3 pieces before running the algo
			int[] bvNew = new int[3];
			int[] old_piece_ids = piece_ids; // WARNING: deep or shallow copy
			piece_ids = new int[3];
			
			// create a group of three pieces which each comes from different nodes
			// then run the algorithm and see whether the group works
			search:
			for (int a = 0; a < 2; a++) {
				bvNew[0] = bv[a];
				piece_ids[0] = old_piece_ids[a];
				for (int b = 0; b < 2; b++) {
					bvNew[1] = bv[2 + b];
					piece_ids[1] = old_piece_ids[2 + b];
					for (int c = 0; c < 2; c++) {
						bvNew[2] = bv[4 + c];
						piece_ids[2] = old_piece_ids[4 + c];

						formula = Algo.determineComponent(bvNew, dc);

						if (formula != null) {
							System.out.println("datacode = " + Arrays.toString(dc));
							System.out.println("basisvectors = " + Arrays.toString(bvNew));
							for (int i = 0; i < formula.length; i++)
								System.out.println("formula[" + i + "] = " + Arrays.toString(formula[i]));
							
							//System.out.println("[a, b, c] = [" + a + ", " + b + ", " + c + "]" );
							break search;
						}
					}
				}
			}
		}
		
		if (formula == null) {
			//System.err.println("Error: formula is not found.");
			System.out.println("There is no possible combination to reconstruct a node from 3 pieces only.");
			System.out.println("Try with other combination of 3 nodes or with 2 nodes.");
			return false;
		}
		
		// load the pieces from nodes
		byte[][] pieces = new byte[piece_ids.length][];
		for (int i = 0; i < pieces.length; i++)
			pieces[i] = nodes[piece_ids[i]/2].getPiece(piece_ids[i] % 2);
			
		// calculate the new pieces using the obtained formula
		byte[][] piecesNew = new byte[nodes[id].getNumberOfPieces()][]; // the pieces of the reborn node
		for (int i = 0; i < formula.length; i++) {
			
			// example: formula[0] = {1, 1, 1, 0} ===> pieces[0] xor pieces[1] xor pieces[2]

			int number_of_pieces_involved  = 0;
			for (int j = 0; j < formula[i].length; j ++ )
				number_of_pieces_involved += formula[i][j];
			
			byte[][] inputs = new byte[number_of_pieces_involved][];
			int c = 0;
			for (int j = 0; j < pieces.length; j++) {
				if (formula[i][j] == 1)
					inputs[c++] = pieces[j];
			}
			
			if (inputs == null)
				System.err.println("Error: inputs is null.");
			piecesNew[i] = xor(inputs);
		}
		
		// reborn the node by feeding its new pieces
		nodes[id].makeAlive(piecesNew);
		
		return true;
		
	}

	/**
	 * To generate an output file from four pieces of any two alive nodes
	 * @param path_output
	 * @return
	 */
	public boolean generateFile(String path_output) {
		// select any two alive nodes
		int found = 0;
		int[] chosenNodes = new int[2];
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isAlive()) {
				chosenNodes[found++] = i;
			}
			if (found == 2)
				break;			
		}
		
		if (found != 2) {
			System.err.println("Error: not enough alive nodes to generate file.");
			return false;
		}
			
		
		// contact two nodes to get the pieces and basis vectors (four is enough)
		int counter = 0;
		byte[][] pieces = new byte[4][];
		int[] bv = new int[4];
		for (int i = 0; i < chosenNodes.length; i++) {
			int[] tmpBV = nodes[chosenNodes[i]].getBasisVectors();
			for (int j = 0; j < nodes[chosenNodes[i]].getNumberOfPieces(); j++) {
				pieces[counter] = nodes[chosenNodes[i]].getPiece(j);
				bv[counter] = tmpBV[j];
				counter++;
			}
		}
				
		// convert pieces to parts
		int[] part_code = {8, 4, 2, 1};
		int[][] formula = Algo.determineComponent(bv, part_code); 
		byte[][] parts = new byte[part_code.length][];
		for (int i = 0; i < formula.length; i++) {
			// formula[0] = {1, 1, 1, 0} ===> pieces[0] xor pieces[1] xor pieces[2]

			int number_of_pieces_involved = formula[i][0] + formula[i][1] + formula[i][2] + formula[i][3];
			
			byte[][] inputs = new byte[number_of_pieces_involved][];
			int c = 0;
			for (int j = 0; j < pieces.length; j++) {
				if (formula[i][j] == 1)
					inputs[c++] = pieces[j];
			}
			parts[i] = xor(inputs);
		}
		
		// combine 4 parts into one file
		contructFile(parts, path_output);
		return true;
	}

	/**
	 * a helper method to combine 4 parts of file into one single file
	 * @param parts
	 * @param path_output
	 * @return
	 */
	private File contructFile(byte[][] parts, String path_output) {
		
		File result = new File(path_output);
		
		try {
			
			File f = new File(path_output);
			f.deleteOnExit();
			if (f.exists()) {
	            f.delete(); 
	        }
			
			FileOutputStream fileoutputstream = new FileOutputStream(f, true);
			
			int i;
			for (i = 0; i < 4; i++)	
				fileoutputstream.write(parts[i]);
			
			fileoutputstream.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * To show the states of nodes (alive or dead)
	 */
	public void showStates() {
		System.out.print("Alive nodes: ");
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isAlive())
				System.out.print("node" + i + " ");
		}
		System.out.println();
		
		System.out.print("Dead nodes: ");
		for (int i = 0; i < nodes.length; i++) {
			if (!nodes[i].isAlive())
				System.out.print("node" + i + " ");
		}
		System.out.println();
	}

}
