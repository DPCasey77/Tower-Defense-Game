package BoneForgeDefense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	

	public PathFinder(int Cols, int Rows, int[][] map, int startX, int startY, int endX, int endY) {
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
			}
		}
		
		search(startX, startY, endX, endY);
	}
	
	//debug output
	public void drawDebugMapPath() {
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
	
	//takes the start x and y and the end x and y to find the best path between them
	//also used to update path mid round, using current position instead of start position
	public boolean search(int startX, int startY, int endX, int endY) {
		int step = 0;
		//int bestNodeIndex = 0;
		//double bestNodeCost = 100000;
		setEndNode(endX,endY);
		setStartNode(startX,startY);
		currentNode.tCost(startNode,endNode);
		int maxSteps = rows*cols;
		
		while(goalReached == false && step < maxSteps) {
			step++;
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
			//if openList is empty no path exists return false
			if(openList.isEmpty()) {
				return false;
			}

			currentNode = Collections.min(openList,Comparator.comparingDouble(Node::getTCost));
			
			if(currentNode == endNode) {
				goalReached = true;
				getPath();
				return true;
			}
		}
		return false;
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
		if (node.getOpen()==false && node.getChecked()==false && (node.getWall()==false || node.getBones()>=100)) {
			node.setIsOpen();
			node.setParent(currentNode);
			node.tCost(startNode,endNode);
			openList.add(node);
			
			//System.out.println("\t opened");
		}
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