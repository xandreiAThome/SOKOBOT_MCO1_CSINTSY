package solver;

public class Point {
    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // hashing the coordinate to ensure unique value for each point
    // code from
    // https://stackoverflow.com/questions/22826326/good-hashcode-function-for-2d-coordinates
    @Override
    public int hashCode() {
        return 997 * x + y;
    }

    @Override
    public boolean equals(Object object) {
        if (this.getClass() != object.getClass())
            return false;
        if (this.hashCode() == ((Point) object).hashCode())
            return true;
        return ((this.x == ((Point) object).getX()) && (this.y == ((Point) object).getY()));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
