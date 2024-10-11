package solver;

import java.util.HashSet;

public class BoardState {
    private Point playerPos;
    // use hashset to use .contains method that is O(1)
    private HashSet<Point> boxesPos;
    private char move;
    private BoardState parent;
    private int cost;

    public BoardState(Point playerPos, HashSet<Point> boxes, char move, BoardState parent) {
        this.playerPos = playerPos;
        this.boxesPos = boxes;
        this.move = move;
        this.parent = parent;

        if (parent == null) {
            cost = 0;
        } else {
            cost = parent.cost + 1; // depth in the tree
        }
    }

    public int getCost() {
        return cost;
    }

    /**
     * 
     * @param b
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

    public boolean isDeadLock(char[][] mapData, HashSet<Point> goalsCoord) {
        boolean deadLock = false;
        HashSet<Character> invalid = new HashSet<>();
        invalid.add('#');
        invalid.add('$');
        for (Point b : boxesPos) {
            // if box is on goal
            if (goalsCoord.contains(b)) {
                continue;
            } else {
                // for top left of the box
                if (invalid.contains(mapData[b.getY() - 1][b.getX()])
                        && invalid.contains(mapData[b.getY()][b.getX() - 1])) {

                    deadLock = true;
                    break;
                }

                // top right of the box
                if (invalid.contains(mapData[b.getY() - 1][b.getX()])
                        && invalid.contains(mapData[b.getY()][b.getX() + 1])) {

                    deadLock = true;
                    break;
                }

                // bottom left of the box
                if (invalid.contains(mapData[b.getY() + 1][b.getX()])
                        && invalid.contains(mapData[b.getY()][b.getX() - 1])) {

                    deadLock = true;
                    break;
                }

                // bottom right of the box
                if (invalid.contains(mapData[b.getY() + 1][b.getX()])
                        && invalid.contains(mapData[b.getY()][b.getX() + 1])) {

                    deadLock = true;
                    break;
                }
            }

        }

        return deadLock;
    }
}
