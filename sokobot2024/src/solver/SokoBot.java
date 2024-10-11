package solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
// XY plane starts from the topmost left and starts at 0
/*
 * example
 * 0 1 2 3 - X
 * 1
 * 2
 * 3
 * Y
 */
import java.util.Queue;

import org.w3c.dom.Node;

public class SokoBot {
  final int[] UP = { 0, -1 };
  final int[] DOWN = { 0, 1 };
  final int[] LEFT = { -1, 0 };
  final int[] RIGHT = { 1, 0 };

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */

    HashSet<Point> boxesCoord = getBoxesCoord(itemsData);
    Point playerCoord = getPlayerCoord(itemsData);
    HashSet<Point> goalsCoord = getGoalsCoord(mapData);
    BoardState initState = new BoardState(playerCoord, boxesCoord, ' ', null);

    String moves = UCS(mapData, initState, goalsCoord);
    return moves;

  }

  /**
   * 
   * @param mapData
   * @param initNode
   * @param goalsCoord
   * @return
   */
  public String BFS(char[][] mapData, BoardState initState, HashSet<Point> goalsCoord) {
    HashSet<BoardState> visited = new HashSet<>();
    Queue<BoardState> availMoves = new LinkedList<>();
    availMoves.add(initState);

    while (!availMoves.isEmpty()) {
      BoardState currState = availMoves.poll();
      visited.add(currState);

      for (BoardState n : getNeighbors(mapData, currState)) {
        if (!visited.contains(n) && !availMoves.contains(n)) {
          if (n.boxesIsOnGoal(goalsCoord)) {
            System.out.println(n.getCost());
            return getSolution(n);
          } else if (!n.isDeadLock(mapData, goalsCoord)) {
            availMoves.add(n);
          }
        }
      }
    }

    System.out.println("No solution");
    return "";
  }

  public String getSolution(BoardState goalState) {
    String moves = "";

    while (goalState.getParent() != null) {
      moves += goalState.getMove();
      goalState = goalState.getParent();
    }
    // reverse because we start getting the moves from the goal to the initial state
    String reverse = new StringBuilder(moves.trim()).reverse().toString();
    System.out.println(reverse);
    return reverse;
  }

  public String UCS(char[][] mapData, BoardState initState, HashSet<Point> goalsCoord) {
    HashSet<BoardState> visited = new HashSet<>();
    Queue<BoardState> frontier = new PriorityQueue<BoardState>(10, costComparator);
    frontier.add(initState);

    while (!frontier.isEmpty()) {
      BoardState currState = frontier.remove();

      if (currState.boxesIsOnGoal(goalsCoord)) {
        System.out.println(currState.getCost());
        return getSolution(currState);
      } else if (!currState.isDeadLock(mapData, goalsCoord)) {
        visited.add(currState);
        for (BoardState neighbor : getNeighbors(mapData, currState)) {
          if (!visited.contains(neighbor) && !frontier.contains(neighbor)) {
            frontier.add(neighbor);
          } else {
            for (BoardState front : frontier) {
              // if new move is already in frontier and new move has lesser cost than the
              // frontier
              // then replace the frontier with the new move
              if (front == neighbor) {
                if (neighbor.getCost() < front.getCost()) {
                  frontier.remove(front);
                  frontier.add(neighbor);
                }
              }
            }
          }
        }
      }
    }

    System.out.println("No solution");
    return "";
  }

  public static Comparator<BoardState> costComparator = new Comparator<BoardState>() {
    @Override
    public int compare(BoardState state1, BoardState state2) {
      return (int) (state1.getCost() - state2.getCost());
    }
  };

  /**
   * 
   * @param itemsData
   * @return the player coordinate in an array, returns null if no player found
   */
  protected Point getPlayerCoord(char[][] itemsData) {
    Point playerCoord;
    for (int y = 0; y < itemsData.length; y++) {
      for (int x = 0; x < itemsData[y].length; x++) {
        if (itemsData[y][x] == '@') {
          playerCoord = new Point(x, y);
          return playerCoord;
        }
      }
    }

    return null;
  }

  /**
   * 
   * @param itemsData
   * @return the coordinates of all the boxes
   */
  protected HashSet<Point> getBoxesCoord(char[][] itemsData) {
    HashSet<Point> boxes = new HashSet<Point>();

    for (int y = 0; y < itemsData.length; y++) {
      for (int x = 0; x < itemsData[y].length; x++) {
        if (itemsData[y][x] == '$' || itemsData[y][x] == '*') {
          boxes.add(new Point(x, y));
        }
      }
    }
    return boxes;
  }

  /**
   * 
   * @param mapData
   * @return the coordinates of all the goal points
   */
  protected HashSet<Point> getGoalsCoord(char[][] mapData) {
    HashSet<Point> goals = new HashSet<Point>();

    for (int y = 0; y < mapData.length; y++) {
      for (int x = 0; x < mapData[y].length; x++) {
        if (mapData[y][x] == '.') {
          goals.add(new Point(x, y));
        }
      }
    }
    return goals;
  }

  /**
   * 
   * @param mapData
   * @param state
   * @return
   */
  public List<BoardState> getNeighbors(char[][] mapData, BoardState state) {
    List<BoardState> neighbors = new ArrayList<>();
    Point playerPos = state.getPlayerPos();

    if (isMoveValid(mapData, UP, state)) {
      HashSet<Point> movedBoxes = moveBoxes(state, UP);
      Point movedPlayer = new Point(playerPos.getX() + UP[0], playerPos.getY() + UP[1]);
      BoardState newState = new BoardState(movedPlayer, movedBoxes, 'u', state);
      neighbors.add(newState);
    }

    if (isMoveValid(mapData, DOWN, state)) {
      HashSet<Point> movedBoxes = moveBoxes(state, DOWN);
      Point movedPlayer = new Point(playerPos.getX() + DOWN[0], playerPos.getY() + DOWN[1]);
      BoardState newState = new BoardState(movedPlayer, movedBoxes, 'd', state);
      neighbors.add(newState);
    }

    if (isMoveValid(mapData, RIGHT, state)) {
      HashSet<Point> movedBoxes = moveBoxes(state, RIGHT);
      Point movedPlayer = new Point(playerPos.getX() + RIGHT[0], playerPos.getY() + RIGHT[1]);
      BoardState newState = new BoardState(movedPlayer, movedBoxes, 'r', state);
      neighbors.add(newState);
    }

    if (isMoveValid(mapData, LEFT, state)) {
      HashSet<Point> movedBoxes = moveBoxes(state, LEFT);
      Point movedPlayer = new Point(playerPos.getX() + LEFT[0], playerPos.getY() + LEFT[1]);
      BoardState newState = new BoardState(movedPlayer, movedBoxes, 'l', state);
      neighbors.add(newState);
    }

    return neighbors;
  }

  private HashSet<Point> moveBoxes(BoardState state, int[] move) {
    HashSet<Point> boxes = new HashSet<Point>();
    Point playerPos = state.getPlayerPos();
    for (Point b : state.getBoxesPos()) {
      if (b.getX() == playerPos.getX() + move[0] && b.getY() == playerPos.getY() + move[1]) {
        boxes.add(new Point(b.getX() + move[0], b.getY() + move[1]));
      } else {
        boxes.add(new Point(b.getX(), b.getY()));
      }
    }

    return boxes;
  }

  /**
   * 
   * @param mapData
   * @param move
   * @param boxesCoord
   * @param playerCoord
   * @return
   */
  private boolean isMoveValid(char[][] mapData, int[] move, BoardState state) {
    int x = state.getPlayerPos().getX() + move[0];
    int y = state.getPlayerPos().getY() + move[1];

    if (mapData[y][x] == '#')
      return false;

    Point movedBox = null;
    for (Point b : state.getBoxesPos()) {
      if (b.getX() == x && b.getY() == y) {
        movedBox = new Point(b.getX() + move[0], b.getY() + move[1]);
        break;
      }
    }

    if (movedBox != null) {
      if (mapData[movedBox.getY()][movedBox.getX()] == '#')
        return false;

      for (Point b : state.getBoxesPos()) {
        if (b.getX() == movedBox.getX() && b.getY() == movedBox.getY()) {
          return false;
        }
      }
    }

    return true;
  }

}
