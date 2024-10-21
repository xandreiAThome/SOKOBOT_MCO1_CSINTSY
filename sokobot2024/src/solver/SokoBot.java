package solver;

import java.util.*;

// XY plane starts from the topmost left and starts at 0
/*
 * example
 * 0 1 2 3 - X
 * 1
 * 2
 * 3
 * Y
 */

public class SokoBot {
  final int[] UP = { 0, -1 };
  final int[] DOWN = { 0, 1 };
  final int[] LEFT = { -1, 0 };
  final int[] RIGHT = { 1, 0 };

  private HashSet<Point> goalsCoord;

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
    goalsCoord = getGoalsCoord(mapData);
    BoardState initState = new BoardState(playerCoord, boxesCoord, goalsCoord, ' ', null);
    // String moves = BFS(mapData, initState);
    // String moves = Astar(mapData, initState, 'm');
    String moves = Greedy(mapData, initState, 'm');
    // String moves = IDAstar(mapData, initState);
    return moves;
  }

  /**
   * 
   * @param mapData
   * @param goalsCoord
   * @return
   */
  public String BFS(char[][] mapData, BoardState initState) {
    HashSet<BoardState> visited = new HashSet<>();
    Queue<BoardState> availMoves = new LinkedList<>();
    int generated = 0;
    if (initState.boxesIsOnGoal())
      return getSolution(initState);
    availMoves.add(initState);

    while (!availMoves.isEmpty()) {
      BoardState currState = availMoves.poll();
      visited.add(currState);

      for (BoardState n : getNeighbors(mapData, currState)) {
        if (!visited.contains(n) && !availMoves.contains(n)) {
          generated++;
          if (n.boxesIsOnGoal()) {
            System.out.println("generated: " + generated);
            System.out.println("visited nodes: " + visited.size());
            return getSolution(n);
          } else if (!n.isDeadLock(mapData)) {
            availMoves.add(n);
          }
        }
      }
    }

    System.out.println("No solution");
    return "";

  }

  /**
   * 
   * @param mapData
   * @param initState
   * @param goalsCoord
   * @param heuristicType 'e' for euclidean, default is manhattan
   * @return
   */
  public String Greedy(char[][] mapData, BoardState initState, char heuristicType) {
    HashSet<BoardState> visited = new HashSet<>();

    // declare comparator which arranges the priority queue to arrange the
    // BoardStates
    // with the ones having the least heuristic values as higher priority
    Comparator<BoardState> comp = new ManhattanComparator();
    if (heuristicType == 'e') {
      comp = new EuclideanComparator();
    }
    if (initState.boxesIsOnGoal())
      return getSolution(initState);
    PriorityQueue<BoardState> availMoves = new PriorityQueue<BoardState>(10, comp);
    availMoves.add(initState);

    while (!availMoves.isEmpty()) {
      BoardState currState = availMoves.poll();
      visited.add(currState);
      // System.out.println("heuristic:" + currState.getHeuristic());

      List<BoardState> succ = getNeighbors(mapData, currState);
      Collections.sort(succ, new ManhattanAstarComparator());

      for (BoardState n : succ) {
        if (!visited.contains(n) && !availMoves.contains(n)) {
          if (n.boxesIsOnGoal()) {
            System.out.println("visited nodes: " + visited.size());
            return getSolution(n);
          } else if (!n.isDeadLock(mapData)) {
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
    System.out.println(reverse.length());
    return reverse;
  }

  /**
   * A* uses the same algorithm as UCS only with heuristic
   * 
   * @param mapData
   * @param initState
   * @param goalsCoord
   * @param heuristicType 'e' for euclidean, default is manhattan
   * @return
   */
  public String Astar(char[][] mapData, BoardState initState, char heuristicType) {
    HashSet<BoardState> visited = new HashSet<>();
    // hashmap for bestcost of a particular state
    HashMap<BoardState, BoardState> bestCost = new HashMap<>();
    Comparator<BoardState> comp = new ManhattanAstarComparator();
    if (heuristicType == 'e') {
      comp = new EuclideanAstarComparator();
    }
    Queue<BoardState> frontier = new PriorityQueue<BoardState>(10, comp);
    int generated = 0;
    frontier.add(initState);

    while (!frontier.isEmpty()) {
      BoardState currState = frontier.remove();
      if (currState.boxesIsOnGoal()) {
        System.out.println("visited nodes: " + visited.size());
        System.out.println("generated: " + generated);
        return getSolution(currState);
      }
      visited.add(currState);
      for (BoardState neighbor : getNeighbors(mapData, currState)) {
        if (visited.contains(neighbor))
          continue;

        // if new move has a evaluation better than prevBest then replace it in the
        // bestHashMap and frontier, otherwise dont put the new move in frontier
        BoardState prevBest = bestCost.get(neighbor);
        if (!neighbor.isDeadLock(mapData)) {
          // if euclidean heuristic
          if (heuristicType == 'e') {
            if (!bestCost.containsKey(neighbor) || neighbor.getCost()
                + neighbor.getEuclideanHeuristic() < prevBest.getCost() + prevBest.getEuclideanHeuristic()) {
              bestCost.put(neighbor, neighbor);
              frontier.remove(prevBest);
              frontier.add(neighbor);
              generated++;
            }
          } else {
            // if manhattan
            if (!bestCost.containsKey(neighbor) || neighbor.getCost()
                + neighbor.getManhattanHeuristic() < prevBest.getCost() + prevBest.getManhattanHeuristic()) {
              bestCost.put(neighbor, neighbor);
              frontier.remove(prevBest);
              frontier.add(neighbor);
              generated++;
            }
          }
        }

      }
    }

    System.out.println("No solution");
    return "";
  }

  public String IDAstar(char[][] mapData, BoardState initState) {
    int bound = lowerBoundEstimate(initState);
    Stack<BoardState> path = new Stack<>();
    path.add(initState);

    while (true) {
      int t = IDAsearch(path, bound, mapData);
      if (t == 0)
        return getSolution(path.peek());
      if (t == 99999999) {
        System.out.println("No solution found");
        return "";
      }
      bound = t;
    }
  }

  public int IDAsearch(Stack<BoardState> path, int bound, char[][] mapData) {
    BoardState state = path.peek();
    int f = state.getCost() + state.getManhattanHeuristic();
    if (f > bound)
      return f * 2;
    if (state.boxesIsOnGoal())
      return 0;

    int min = 99999999;
    for (BoardState neighbor : getNeighbors(mapData, state)) {
      if (!path.contains(neighbor)) {
        path.push(neighbor);
        int t = IDAsearch(path, bound, mapData);
        if (t == 0)
          return 0;
        if (t < min)
          min = t;
        path.pop();
      }
    }

    return min;
  }

  public int lowerBoundEstimate(BoardState initState) {
    int sumDistance = 0;

    for (Point b : initState.getBoxesPos()) {
      int minDistance = 99999999;
      for (Point g : initState.getGoalsPos()) {
        int m = Math.abs(b.getX() - g.getX()) + Math.abs(b.getY() - g.getY());
        // compute the manhattan distance between box and goal and keep the lowest one
        if (m < minDistance)
          minDistance = m;
      }
      sumDistance += minDistance;
    }

    return sumDistance * 10; // arbitrary multiplier for the depth estimate
  }

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
      BoardState newState = new BoardState(movedPlayer, movedBoxes, goalsCoord, 'u', state);
      neighbors.add(newState);
    }

    if (isMoveValid(mapData, DOWN, state)) {
      HashSet<Point> movedBoxes = moveBoxes(state, DOWN);
      Point movedPlayer = new Point(playerPos.getX() + DOWN[0], playerPos.getY() + DOWN[1]);
      BoardState newState = new BoardState(movedPlayer, movedBoxes, goalsCoord, 'd', state);
      neighbors.add(newState);
    }

    if (isMoveValid(mapData, RIGHT, state)) {
      HashSet<Point> movedBoxes = moveBoxes(state, RIGHT);
      Point movedPlayer = new Point(playerPos.getX() + RIGHT[0], playerPos.getY() + RIGHT[1]);
      BoardState newState = new BoardState(movedPlayer, movedBoxes, goalsCoord, 'r', state);
      neighbors.add(newState);
    }

    if (isMoveValid(mapData, LEFT, state)) {
      HashSet<Point> movedBoxes = moveBoxes(state, LEFT);
      Point movedPlayer = new Point(playerPos.getX() + LEFT[0], playerPos.getY() + LEFT[1]);
      BoardState newState = new BoardState(movedPlayer, movedBoxes, goalsCoord, 'l', state);
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
