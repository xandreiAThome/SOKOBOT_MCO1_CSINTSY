package solver;

public class Position {
    // column number starting from 0
    private int x;
    // row number starting from 0
    private int y;

    public Position(int x, int y){
        setPosition(x,y);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
}
