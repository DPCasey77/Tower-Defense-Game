package BoneForgeDefense;

import java.util.ArrayList;
import java.util.List;

public class PathFinder {
	private int cols;
	private int rows;
	private boolean goalReached = false;
	
	Node[][] node;
	ArrayList<Node> openList = new ArrayList<Node>();
	ArrayList<Node> checkedList = new ArrayList<Node>();
	
	Node currentNode;
	Node startNode;
	Node endNode;
	
	
	//Map key
	/* 0 empty
	 * 1 wall
	 * 2 start
	 * 3 end
	 * 
	 * example:
	 * 20000000010
	 * 11111000011
	 * 00000000000
	 * 00100011111
	 * 00000000003
	 * 
	 */
	public PathFinder(int Cols, int Rows, int[][] map) {
		this.cols = Cols;
		this.rows = Rows;
		this.node = new Node[rows][cols];
		//Initializes base map
		for(int i = 0; i<rows; i++) {
			for(int j = 0; j<cols; j++) {
				node[i][j] = new Node(i,j);
				
				if (map[i][j] == 1) {
					node[i][j].setIsWall();
				}
				else if (map[i][j] == 2) {
					setStartNode(i,j);
				}
				else if (map[i][j] == 3) {
					setEndNode(i,j);
				}
			}
		}
		//debug show node positions
		/*
		for(int i = 0; i<rows; i++) {
			for(int j = 0; j<cols; j++) {
				System.out.print(node[i][j].getX() + "," + node[i][j].getY() + "  ");
			}
			System.out.print("\n");
		}
		*/
	}
	
	//debug output
	public void drawMapPath() {
		char[][] pathMap = new char[rows][cols];
		for(int i = 0; i<rows; i++) {
			for(int j = 0; j<cols; j++) {
				
				if (node[i][j].getWall()) {
					pathMap[i][j] = 'W';
				}
				else if (node[i][j].getStart()) {
					pathMap[i][j] = 'S';
				}
				else if (node[i][j].getEnd()) {
					pathMap[i][j] = 'E';
				}
				else if (node[i][j].getIsPath()) {
					pathMap[i][j] = '-';
				}
				else {
					pathMap[i][j] = ' ';
				}
			}
		}
		for(int i = 0; i<rows; i++) {
			for(int j = 0; j<cols; j++) {
				System.out.print(pathMap[i][j]+" ");
			}
			System.out.print("\n");
		}
	}
	
	public void search() {
		int step = 0;
		int bestNodeIndex = 0;
		double bestNodeCost = 100000;
		currentNode.tCost(startNode,endNode);
		int maxSteps = rows*cols;
		
		while(goalReached == false && step < maxSteps) {
			step++;
			
			//debug
			//System.out.println("step: " + step);
			//System.out.println(currentNode.getX() + " " + currentNode.getY());
			
			int col = currentNode.getY();
			int row = currentNode.getX();
			
			currentNode.setChecked();
			checkedList.add(currentNode);
			openList.remove(currentNode);
			
			if (row > 0 ) {
				//open node above if not on top row
				//System.out.println("opened above");
				openNode(node[row-1][col]);
				
			}
			if (row < rows-1) {
				//open node below if not on bottom row
				//System.out.println("opened below");
				openNode(node[row+1][col]);
				
			}
			if (col < cols-1) {
				//open right node if not on right row
				//System.out.println("opened right");
				openNode(node[row][col+1]);
				
			}
			if (col > 0) {
				//open left node if not on left row
				//System.out.println("opened left");
				openNode(node[row][col-1]);
				
			}
			
			
			for (int i=0; i < openList.size();i++) {
				openList.get(i).tCost(startNode,endNode);
				
				//debug code
				//System.out.println(openList.get(i).getX() + " " + openList.get(i).getY());
				//System.out.println("openList "+ i + "\n\ttcost:" + openList.get(i).tCost + "\n\tscost:" + openList.get(i).sCost + "\n\tecost:" + openList.get(i).eCost);
				
				if(openList.get(i).tCost < bestNodeCost) {
					bestNodeIndex=i;
					bestNodeCost = openList.get(i).tCost;
				}
			
				else if(openList.get(i).tCost == bestNodeCost) {
					if(bestNodeIndex > openList.size() - 1) {
						bestNodeIndex = 0;
					}
					if(openList.get(i).eCost < openList.get(bestNodeIndex).eCost){
						bestNodeIndex=i;
						bestNodeCost = openList.get(i).tCost;
					}
				}
			}
			
			//debug code
			//System.out.println(openList.get(bestNodeIndex).getX() + " " + openList.get(bestNodeIndex).getY());
			//System.out.println("Open list size: " + openList.size());
			//System.out.println("Best Index: " + bestNodeIndex);
			
			currentNode = openList.get(bestNodeIndex);
			if(currentNode == endNode) {
				goalReached = true;
				getPath();
			}
		}
	}
	
	private void getPath() {
		Node current = endNode;
		while(current != startNode) {
			current = current.getParent();
			if(current != startNode) {
				current.isPath();
			}
		}
	}
	
	public void openNode(Node node) {
		if (node.getOpen()==false && node.getChecked()==false && node.getWall()==false) {
			node.setIsOpen();
			node.setParent(currentNode);
			openList.add(node);
			
			//System.out.println("\t opened");
		}
		/*else if(node.getWall()){
			System.out.println("\t not opened is wall");
		}
		else if(node.getChecked()){
			System.out.println("\t not opened is checked");
		}
		else if(node.getOpen()){
			System.out.println("\t not opened is open");
		}*/
	}
	public Node[][] getNodes() {
		return node;
	}

	// Returns the path in start-to-end order by following parent pointers from the end node
	public List<Node> getOrderedPath() {
		List<Node> reversed = new ArrayList<>();
		Node current = endNode;
		while (current != startNode) {
			reversed.add(current);
			current = current.getParent();
		}
		reversed.add(startNode);

		List<Node> ordered = new ArrayList<>();
		for (int i = reversed.size() - 1; i >= 0; i--) {
			ordered.add(reversed.get(i));
		}
		return ordered;
	}

	private void setStartNode(int x, int y) {
		node[x][y].setIsStart();
		startNode = node[x][y];
		currentNode = startNode;
		openNode(node[x][y]);
	}
	private void setEndNode(int x, int y) {
		node[x][y].setIsEnd();
		endNode = node[x][y];
	}
	
}