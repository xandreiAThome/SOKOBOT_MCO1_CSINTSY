package solver;

import java.util.ArrayList;
import java.util.List;

public class PlayerPos {
    private final int x, y;
    private PlayerPos previous;
    private final char move;
    private ArrayList<BoxPos> boxes = new ArrayList<BoxPos>();

    public PlayerPos(int x, int y, PlayerPos previous, char move, ArrayList<BoxPos> boxes) {
        this.x = x;
        this.y = y;
        this.previous = previous;
        this.move = move;
        this.boxes = boxes;
    }

    /**
     * checks if a box is stuck in a corner
     * 
     * @param mapData
     * @return
     */
    public boolean isDeadLock(char[][] mapData, ArrayList<int[]> goalsCoord) {
        boolean deadLock = false;
        List<Character> invalid = new ArrayList<>();
        invalid.add('#');
        invalid.add('$');
        for (BoxPos b : boxes) {
            if (boxIsOnGoal(b, goalsCoord)) {
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

    /**
     * 
     * @param b
     * @param goalsCoord
     * @return
     */
    public boolean boxIsOnGoal(BoxPos b, ArrayList<int[]> goalsCoord) {
        for (int g[] : goalsCoord) {
            if (g[0] == b.getX() && g[1] == b.getY()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        PlayerPos p = (PlayerPos) o;
        if (p.getX() != x || p.getY() != y)
            return false;

        for (int i = 0; i < boxes.size(); i++) {
            if (!boxes.get(i).equals(p.getBoxesPos().get(i))) {
                return false;

            }
        }

        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PlayerPos getPrevPoint() {
        return previous;
    }

    public char getMove() {
        return move;
    }

    public ArrayList<BoxPos> getBoxesPos() {
        return boxes;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
