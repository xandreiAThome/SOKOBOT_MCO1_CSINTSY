package solver;

import java.util.Comparator;

public class BoardStateComparator implements Comparator<BoardState> {
    @Override
    public int compare(BoardState o1, BoardState o2) {
        if (o1.getHeuristic() < o2.getHeuristic()){
            return -1;
        }else if (o2.getHeuristic() < o1.getHeuristic()){
            return 1;
        }
        return 0;
    }
}
