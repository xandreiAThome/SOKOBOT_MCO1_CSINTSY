package solver;

import java.util.HashSet;

public class BoardState {
    private Point playerPos;
    // use hashset to use .contains method that is O(1)
    private HashSet<Point> boxesPos;
    private HashSet<Point> goalsPos;
    private char move;
    private BoardState parent;

    private int euclideanHeuristic;
    private int pathCost;
    

    public BoardState(Point playerPos, HashSet<Point> boxes, HashSet<Point> goals, char move, BoardState parent) {
        this.playerPos = playerPos;
        this.boxesPos = boxes;
        this.goalsPos = goals;
        this.move = move;
        this.parent = parent;

        // compute heuristic as the sum of box distances to each goal plus the distance
        // of the player to each box
        euclideanHeuristic = computeEuclideanHeuristic();
        pathCost = computePathCost();
    }

    public int getEuclideanHeuristic() {
        return euclideanHeuristic;
    }


    public void setPathCost(int pathCost) {
        this.pathCost = pathCost;  
    }

    public int getPathCost() {
        return pathCost;
    }


 
    private int computeEuclideanHeuristic() {

        int h = 0;
        int least;
        // for all boxes
        for (Point b : boxesPos) {
            // for all goals
            least = 99999999;
            for (Point g : goalsPos) {
                int m = (int) Math.sqrt(Math.pow(b.getX() - g.getX(), 2) + Math.pow(b.getY() - g.getY(), 2));
                // compute the Euclideean distance between box and goal and keep the lowest one
                if (m < least)
                    least = m;
            }
            h += least;
        }
        // return the sum of distances as heuristic
        return h;
    }


    private int computePathCost() {
        int pathCost = 0;

       
        BoardState currentState = this;  
        
        while (currentState.getParent() != null) {
            pathCost++;  
            currentState = currentState.getParent();  
        }
        
        return pathCost; 
    }
    
    /**
     *
     * @param goalsCoord
     * @return
     */
    public boolean boxesIsOnGoal(HashSet<Point> goalsCoord) {
        for (Point box : boxesPos) {
            if (!goalsCoord.contains(box)) {
                return false;
            }
        }

        return true;
    }

    public char getMove() {
        return move;
    }

    public BoardState getParent() {
        return parent;
    }

    public Point getPlayerPos() {
        return playerPos;
    }

    // also uses prime numbers to make a unique hash
    @Override
    public int hashCode() {
        int result = 19;
        for (Point b : boxesPos) {
            result = 41 * result + b.hashCode();
        }
        result = 41 * result + playerPos.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass())
            return false;
        if (this.hashCode() == ((BoardState) o).hashCode())
            return true;
        if (this.boxesPos == ((BoardState) o).getBoxesPos() && this.playerPos == ((BoardState) o).getPlayerPos())
            return true;

        return false;
    }

    public HashSet<Point> getBoxesPos() {
        return boxesPos;
    }

    public HashSet<Point> getGoalPos() {
        return goalsPos;
    }

    public boolean isDeadLock(char[][] mapData, HashSet<Point> goalsCoord) {
        boolean deadLock = false;
        HashSet<Character> invalid = new HashSet<>();
        invalid.add('#'); // Wall
        invalid.add('$'); // Box
    
        // Corner Deadlock check
        for (Point b : boxesPos) {
            // Skip if the box is on a goal
            if (goalsCoord.contains(b)) {
                continue;
            } else {
                // Top-left corner deadlock
                if (invalid.contains(mapData[b.getY() - 1][b.getX()])  // Wall/Box above
                        && invalid.contains(mapData[b.getY()][b.getX() - 1])) {  // Wall/Box left
                    return true;
                }
    
                // Top-right corner deadlock
                if (invalid.contains(mapData[b.getY() - 1][b.getX()])  // Wall/Box above
                        && invalid.contains(mapData[b.getY()][b.getX() + 1])) {  // Wall/Box right
                    return true;
                }
    
                // Bottom-left corner deadlock
                if (invalid.contains(mapData[b.getY() + 1][b.getX()])  // Wall/Box below
                        && invalid.contains(mapData[b.getY()][b.getX() - 1])) {  // Wall/Box left
                    return true;
                }
    
                // Bottom-right corner deadlock
                if (invalid.contains(mapData[b.getY() + 1][b.getX()])  // Wall/Box below
                        && invalid.contains(mapData[b.getY()][b.getX() + 1])) {  // Wall/Box right
                    return true;
                }
            }
        }
    
        // Double-box Deadlock check (adjacent boxes against walls)
        int[][] doubleBoxOffsets = {
            {0, -1, -1, -1},   // Two boxes vertically aligned against a wall above
            {0, 1, -1, 1},     // Two boxes vertically aligned against a wall below
            {-1, 0, -1, -1},   // Two boxes horizontally aligned against a wall to the left
            {1, 0, 1, -1},     // Two boxes horizontally aligned against a wall to the right
        };
    
        for (Point b : boxesPos) {
            for (int[] offset : doubleBoxOffsets) {
                int bX = b.getX();
                int bY = b.getY();
                // Get the two adjacent box positions based on the current offset
                int adjBox1X = bX + offset[0];
                int adjBox1Y = bY + offset[1];
                int adjBox2X = bX + offset[2];
                int adjBox2Y = bY + offset[3];
    
                // Check if both positions contain boxes and are against a wall or another box
                if (mapData[adjBox1Y][adjBox1X] == '$' && mapData[adjBox2Y][adjBox2X] == '$') {
                    if (invalid.contains(mapData[adjBox1Y + offset[1]][adjBox1X + offset[0]])
                            && invalid.contains(mapData[adjBox2Y + offset[3]][adjBox2X + offset[2]])) {
                        return true;
                    }
                }
            }
        }
    
        // Too-many-boxes-in-a-row/column deadlock check
        int boxCount, goalCount;
    
        // Check rows
        for (int row = 0; row < mapData.length; row++) {
            boxCount = goalCount = 0;
            for (int col = 0; col < mapData[0].length; col++) {
                if (mapData[row][col] == '$') {
                    boxCount++;
                } else if (mapData[row][col] == '.' || mapData[row][col] == '+') {  // Goals
                    goalCount++;
                }
            }
            if (boxCount > goalCount) {
                return true; // Too many boxes in the row compared to goals
            }
        }
    
        // Check columns
        for (int col = 0; col < mapData[0].length; col++) {
            boxCount = goalCount = 0;
            for (int row = 0; row < mapData.length; row++) {
                if (mapData[row][col] == '$') {
                    boxCount++;
                } else if (mapData[row][col] == '.' || mapData[row][col] == '+') {  // Goals
                    goalCount++;
                }
            }
            if (boxCount > goalCount) {
                return true; // Too many boxes in the column compared to goals
            }
        }
    
        return deadLock;
    }

}
