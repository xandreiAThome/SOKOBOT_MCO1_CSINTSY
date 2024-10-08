package solver;

public class Node {
    private Position pos;
    private int totalCost;
    private int heuristic;
    private Node previous;
    private boolean rejected;

    public Node(int x, int y, int totalCost, Position goal, Node previous){
        this.previous = previous;
        pos = new Position(x,y);
        this.totalCost = totalCost;
        computeHeuristic(goal);
    }

    private void computeHeuristic(Position goal){
        // get manhattan distance between goal and position as heuristic
        heuristic = Math.abs(pos.getX() - goal.getX()) + Math.abs(pos.getY() - goal.getY());
    }

    public int getCostWithHeuristic(){
        return totalCost + heuristic;
    }

    public int getCost(){
        return totalCost;
    }

    // we assume the player cannot move diagonally
    public char getMove(){
        char output = 'r';

        if (previous.getPos().getX() > pos.getX()){
            // move was left
            output = 'l';

        }else if (previous.getPos().getX() < pos.getX()){
            // move was right
            output = 'r';
        }else if (previous.getPos().getY() > pos.getY()){
            output = 'u';
        }else{
            output = 'd';
        }

        return output;
    }

    public Position getPos(){
        return pos;
    }

    public Node getPrevious(){
        return previous;
    }

    public boolean isAccepted(){
        return !rejected;
    }

    public void setRejected(boolean a){
        rejected = a;
    }

    public void setPrevious(Node p){
        this.previous = p;
    }
}
