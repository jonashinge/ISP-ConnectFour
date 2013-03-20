

public class Node implements Cloneable {
	private Node next;
	public int x;
	public int y;
	public int playerID;

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public Node(int x, int y, int playerID) {
		this.x = x;
		this.y = y;
		this.playerID = playerID;
	}

	public Object clone() {
		return (Object) new Node(x,y,playerID);
	}
}

