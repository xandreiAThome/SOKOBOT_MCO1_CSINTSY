package solver;

import java.util.Comparator;

public class EuclideanAstarComparator implements Comparator<BoardState> {
    @Override
    public int compare(BoardState o1, BoardState o2) {
        if (o1.getCost() + o1.getEuclideanHeuristic() < o2.getCost() + o2.getEuclideanHeuristic()) {
            return -1;
        } else if (o2.getCost() + o2.getEuclideanHeuristic() < o1.getCost() + o1.getEuclideanHeuristic()) {
            return 1;
        }
        return 0;
    }
}
