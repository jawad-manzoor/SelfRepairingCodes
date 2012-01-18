import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class Node {
	
	private int id;
	private int[] basisVectors;
	private String[] pathPieces;
	private String pathNode;
	private boolean alive;
	
	private File[] filePieces;
	private File fileNode;
	
	
	public Node(int id) {
		this.id = id;
		this.alive = false;
	}
	
	public Node(int id, int[] basisVectors, String[] pathPieces, String pathNode){
		this.id = id;
		this.alive = false;
		
		this.basisVectors = basisVectors;
		this.pathPieces = pathPieces;
		this.pathNode = pathNode;
		
		this.fileNode = new File(this.pathNode);
		this.fileNode.mkdir();
		this.fileNode.deleteOnExit();
		
		this.filePieces = new File[pathPieces.length];
		for(int i = 0; i < pathPieces.length; i++) {
			this.filePieces[i] = new File(this.pathPieces[i]);
			this.filePieces[i].deleteOnExit();			
		}
		
	}
	
	// getter method
	public int getId() {
		return id;
	}
	
	public int[] getBasisVectors() {
		return basisVectors;
	}
	
	public String[] getPathPieces() {
		return pathPieces;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public String getPathNode() {
		return pathNode;
	}
	
	
	//setter method
	
	public void setBasisVectors(int[] basisVectors) {
		this.basisVectors = basisVectors;
	}
	
	public void setPathPieces(String[] pathPieces) {
		this.pathPieces = pathPieces;
	}
	
	public void setState(boolean alive) {
		this.alive = alive;
	}
	
	public void setPathNode(String pathNode) {
		this.pathNode = pathNode;
	}
	
	// worker methods
	
	public int getNumberOfPieces() {
		return pathPieces.length;
	}
	
	/**
	 * To extract one piece from a node with specified piece id
	 * @param piece_id
	 * @return an array of byte (the piece)
	 */
	public byte[] getPiece(int piece_id) {
		byte[] piece = null;
		int pieceSize;
		
		try {
			FileInputStream fileinputstream = new FileInputStream(this.filePieces[piece_id]);
			pieceSize = fileinputstream.available();
			piece= new byte[pieceSize];
			fileinputstream.read(piece);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return piece;
	}
	
	/**
	 * To write the piece (an array of bytes) into the localdisk
	 * @param piece
	 * @param piece_id
	 */
	public void writePiece(byte[] b, int piece_id) {
		this.alive = true;
		
		try{	
			FileOutputStream fileoutputstream = new FileOutputStream(this.filePieces[piece_id]);
			fileoutputstream.write(b);
			fileoutputstream.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * To kill the node
	 */
	public void kill() {	
		
		if (!this.alive) {
			System.out.println("Node " + id + " is already dead.");
			return;
		}
		
		this.alive = false;

		for (int i = 0; i < this.filePieces.length; i++) {
			filePieces[i].delete();
		}
	
		if (this.fileNode.delete())
			System.out.println("Node " + id + " has been deleted.");
		else
			System.err.println("Error: in deleting node " + id);

	}
	
	/**
	 * To restart a dead node
	 * @param pieces
	 */
	public void makeAlive(byte[][] pieces) {
		this.alive = true;
		
		this.fileNode.mkdir();
		this.fileNode.deleteOnExit();
		
		for(int i = 0; i < pathPieces.length; i++) {
			this.filePieces[i] = new File(this.pathPieces[i]);
			this.filePieces[i].deleteOnExit();
		}
		
		for (int i = 0; i < pieces.length; i++) {
			writePiece(pieces[i], i);
		}
		
	}

}
