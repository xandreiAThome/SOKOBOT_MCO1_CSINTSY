package solver;

import java.util.HashSet;
import java.util.HashMap;

public class BoardState {
    private Point playerPos;
    // use hashset to use .contains method that is O(1)
    private HashSet<Point> boxesPos;
    private HashSet<Point> goalsPos;
    private char move;
    private BoardState parent;
    private int cost;
    private int manhattanHeuristic;
    private int euclideanHeuristic;

    public BoardState(Point playerPos, HashSet<Point> boxes, HashSet<Point> goals, char move, BoardState parent) {
        this.playerPos = playerPos;
        this.boxesPos = boxes;
        this.goalsPos = goals;
        this.move = move;
        this.parent = parent;

        if (parent == null) {
            cost = 0;
        } else {
            cost = parent.cost + 1; // depth in the tree
        }
        manhattanHeuristic = computeManhattanHeuristic();
        euclideanHeuristic = computeEuclideanHeuristic();
    }

    public int getCost() {
        return cost;
    }

    public int getManhattanHeuristic() {
        return manhattanHeuristic;
    }

    public int getEuclideanHeuristic() {
        return euclideanHeuristic;
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

    /**
     *
     * 
     * 
     * @return
     */
    private int computeManhattanHeuristic() {

        int h = 0;
        int least;

        // for all boxes
        for (Point b : boxesPos) {
            // for all goals
            least = 99999999;
            for (Point g : goalsPos) {
                int m = Math.abs(b.getX() - g.getX()) + Math.abs(b.getY() - g.getY());
                // compute the manhattan distance between box and goal and keep the lowest one
                if (m < least)
                    least = m;
            }
            h += least;
        }
        // return the sum of distances as heuristic
        return h;
    }

    /**
     *
     * 
     * 
     * 
     * @param goalsCoord
     * @return
     */
    public boolean boxesIsOnGoal() {
        for (Point box : boxesPos) {
            if (!goalsPos.contains(box)) {
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

    public boolean isDeadLock(char[][] mapData, HashMap<String, Boolean> edgeContainGoal, int width, int height) {
        HashSet<Character> invalid = new HashSet<>();
        invalid.add('#');
        invalid.add('$');
        for (Point b : boxesPos) {
            // if box is on goal
            if (goalsPos.contains(b)) {
                continue;
            } else {
                // for top left of the box
                if (invalid.contains(mapData[b.getY() - 1][b.getX()])
                        && invalid.contains(mapData[b.getY()][b.getX() - 1]))
                    return true;

                // top right of the box
                if (invalid.contains(mapData[b.getY() - 1][b.getX()])
                        && invalid.contains(mapData[b.getY()][b.getX() + 1]))
                    return true;

                // bottom left of the box
                if (invalid.contains(mapData[b.getY() + 1][b.getX()])
                        && invalid.contains(mapData[b.getY()][b.getX() - 1]))
                    return true;

                // bottom right of the box
                if (invalid.contains(mapData[b.getY() + 1][b.getX()])
                        && invalid.contains(mapData[b.getY()][b.getX() + 1]))
                    return true;

                if (edgeContainGoal.get("top") == null && b.getY() == 1)
                    return true;

                if (edgeContainGoal.get("bot") == null && b.getY() == height - 2)
                    return true;

                if (edgeContainGoal.get("left") == null && b.getX() == 1)
                    return true;

                if (edgeContainGoal.get("right") == null && b.getX() == width - 2)
                    return true;

            }

        }

        return false;
    }
}
