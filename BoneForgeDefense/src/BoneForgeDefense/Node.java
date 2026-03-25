package BoneForgeDefense;

public class Node {
	private int X;
	private int Y;

	public double tCost = 1000000.0;
	public double sCost = 1000000.0;
	public double eCost = 1000000.0;
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
	
	
	public void tCost(Node startNode, Node endNode) {
		eCost = Math.abs(this.X - endNode.X) + Math.abs(this.Y - endNode.Y);
		if(this.start==true) 
			sCost=0;
		else 
			sCost=this.parent.sCost+1;
		
		this.tCost = sCost + eCost;
	}
	
}
