
public class Arguments {
	
	public static final String path_input = "src/input.mp3";
	public static final String path_output = "src/output.mp3";
	
	public static final String[][] path_pieces = 
			{{"src/node0/piece0.mp3", "src/node0/piece1.mp3"},
			{"src/node1/piece2.mp3", "src/node1/piece3.mp3"},
			{"src/node2/piece4.mp3", "src/node2/piece5.mp3"},
			{"src/node3/piece6.mp3", "src/node3/piece7.mp3"},
			{"src/node4/piece8.mp3", "src/node4/piece9.mp3"}};
	
	public static final String[] path_nodes = {"src/node0", "src/node1", "src/node2", "src/node3", "src/node4"};
	
	public static final int[][] basis_vectors = {{8, 6}, {4, 3}, {2, 13}, {1, 10}, {12, 5}};

	public static final int NUMBER_OF_NODES = path_nodes.length;
	public static final int NUMBER_OF_PIECES = path_pieces.length * path_pieces[0].length;
	
	public static final int[][] possible_answer4 = {
			{0, 0, 0, 1}, {0, 0, 1, 0}, {0, 0, 1, 1}, {0, 1, 0, 0},
			{0, 1, 0, 1}, {0, 1, 1, 0}, {0, 1, 1, 1}, {1, 0, 0, 0},
			{1, 0, 0, 1}, {1, 0, 1, 0}, {1, 0, 1, 1}, {1, 1, 0, 0},
			{1, 1, 0, 1}, {1, 1, 1, 0}, {1, 1, 1, 1}};	
	
	public static final int[][] possible_answer3 = {
			{0, 0, 1}, {0, 1, 0}, {0, 1, 1}, 
			{1, 0, 0}, {1, 0, 1}, {1, 1, 0}, 
			{1, 1, 1}};
			
			
	
	
}
