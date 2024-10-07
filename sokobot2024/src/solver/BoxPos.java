package solver;

public class BoxPos {
    private final int x, y;

    public BoxPos(BoxPos box) {
        x = box.getX();
        y = box.getY();
    }

    public BoxPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        BoxPos b = (BoxPos) o;
        return b.getX() == x && b.getY() == y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
