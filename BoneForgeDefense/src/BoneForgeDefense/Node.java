package BoneForgeDefense;

public class Node {
	private int X;
	private int Y;

	public double fCost = 1000000.0;
	public double gCost = 1000000.0;
	public double hCost = 1000000.0;
	private boolean start = false;
	private boolean end  = false;
	private boolean wall  = false;
	private boolean open  = false;
	private boolean checked = false;
	private boolean isPath = false;
	
	private Node parent = null;
	
	
	public Node(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
	public void setX(int x) {this.X = x;}
	public void setY(int y) {this.Y = y;}
	public void setIsStart() {start=true;}
	public void setIsEnd() {end=true;}
	public void setIsWall() {wall=true;}
	public void setIsClosed() {open=false;}
	public void setIsOpen() {open=true;}
	public void setChecked() {checked=true;}
	public void setParent(Node Parent) {parent = Parent;}
	public void isPath() {isPath = true;}
	
	
	public int getX() {return this.X;}
	public int getY() {return this.Y;}
	public Node getParent() {return parent;}
	public boolean getStart() {return start;}
	public boolean getEnd() {return end;}
	public boolean getWall() {return wall;}
	public boolean getOpen() {return open;}
	public boolean getChecked() {return checked;}
	public boolean getIsPath() {return isPath;}
	
	
	public void fCost(Node startNode, Node endNode) {
		hCost = Math.abs(this.X - endNode.X) + Math.abs(this.Y - endNode.Y);
		gCost = Math.abs(this.X - startNode.X) + Math.abs(this.Y - startNode.Y);
		this.fCost = gCost + hCost;
	}
	
}
