package solver;

import java.util.Comparator;

public class ManhattanAstarComparator implements Comparator<BoardState> {
    @Override
    public int compare(BoardState o1, BoardState o2) {
        if (o1.getCost() + o1.getManhattanHeuristic() < o2.getCost() + o2.getManhattanHeuristic()) {
            return -1;
        } else if (o2.getCost() + o2.getManhattanHeuristic() < o1.getCost() + o1.getManhattanHeuristic()) {
            return 1;
        }
        return 0;
    }
}
