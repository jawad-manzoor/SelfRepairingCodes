import java.util.Arrays;

public class Algo {
	
	public static int NUMBER_OF_PARTS = 4;
	
	/**
	 * Not the real main class. It is only for testing the Algorithm
	 * @param args
	 */
	public static void main(String[] args) {
		int[] bv = {8, 6, 2 ,13};
		int[] dc = {12, 5};	
		int[][] result = Algo.determineComponent(bv, dc);
		
		System.out.println("result: " + Arrays.toString(result[0]) + ", " + Arrays.toString(result[1]));
	}
	
	/**
	 * The algorithm to determine which pieces should be included in the computation
	 * to generate the data code.
	 * Algorithm: exhaustively search for each possible combination of pieces,
	 * the very first solution will be returned.
	 * @param basisVectors for each pieces (only support 3 or 4 pieces)
	 * @param datacode
	 * @return formulas for each datacodes
	 */
	public static int[][] determineComponent(int[] basisVectors, int[] datacode) {
		int result[][] = new int[datacode.length][basisVectors.length];
		int found = 0;
		
		int[][] possible_answer = null;
		
		if (basisVectors.length == 4)
			possible_answer = Arrays.copyOf(Arguments.possible_answer4, Arguments.possible_answer4.length);
		else if (basisVectors.length == 3)
			possible_answer = Arrays.copyOf(Arguments.possible_answer3, Arguments.possible_answer3.length);
		else {
			System.err.println("Error: unsupported number of basis vectors. either 3 or 4.");
			return null; //
		}
		
		search:
		{
			int temp;
			for (int i = 0; i < possible_answer.length; i++) {
				temp = 0; 
				for (int j = 0; j < basisVectors.length; j++) {
					temp = temp ^ (possible_answer[i][j] * basisVectors[j]);
				}				

				for (int j = 0; j < datacode.length; j++) {
					if (temp == datacode[j]) {						
						result[j] = possible_answer[i];
						found++;
						break;
					}
				}
				
				if (found == datacode.length)
					break search;	
			}
		}
		
		if (found != datacode.length) {
			//System.err.println("Error: algo not found!"); // comment it later.
			result = null;
		}
		
		return result;
		
	}

}
