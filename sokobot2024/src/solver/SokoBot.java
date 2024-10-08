package solver;

import java.util.ArrayList;
import java.util.List;
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

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */

    ArrayList<BoxPos> boxesCoord = getBoxesCoord(itemsData);
    PlayerPos playerCoord = getPlayerCoord(itemsData, boxesCoord);
    ArrayList<int[]> goalsCoord = getGoalsCoord(mapData);

    String moves = BFS(mapData, playerCoord, goalsCoord);
    return moves;

  }

  /**
   * 
   * @param mapData
   * @param playerCoord
   * @param goalsCoord
   * @return
   */
  public String BFS(char[][] mapData, PlayerPos playerCoord, ArrayList<int[]> goalsCoord) {
    boolean finished = false;
    List<PlayerPos> visited = new ArrayList<>();
    visited.add(playerCoord);

    while (!finished) {
      List<PlayerPos> availMoves = new ArrayList<>();
      for (int i = 0; i < visited.size(); ++i) {
        PlayerPos playerPos = visited.get(i);
        for (PlayerPos neighbor : getNeighbors(mapData, playerPos)) {
          if (!posIsIn(visited, neighbor) && !posIsIn(availMoves, neighbor) &&
              !neighbor.isDeadLock(mapData, goalsCoord)) {
            availMoves.add(neighbor);
          }
        }
      }

      for (PlayerPos playerPos : availMoves) {
        visited.add(playerPos);

        boolean boxesGoal[] = new boolean[playerPos.getBoxesPos().size()];
        for (int i = 0; i < boxesGoal.length; i++) {
          boxesGoal[i] = false;
        }

        int i = 0;
        for (BoxPos box : playerPos.getBoxesPos()) {
          boolean boxInGoal = false;
          for (int[] goal : goalsCoord) {
            if (box.getX() == goal[0] && box.getY() == goal[1])
              boxInGoal = true;
          }
          boxesGoal[i] = boxInGoal;
          i++;
        }

        boolean goalAchieved = true;
        for (boolean b : boxesGoal) {
          if (!b) {
            goalAchieved = b;
            break;
          }
        }

        if (goalAchieved) {
          finished = goalAchieved;
          break;
        }
      }

      if (!finished && availMoves.isEmpty())
        return ""; // no solution found
    }

    String moves = "";
    PlayerPos point = visited.get(visited.size() - 1);
    while (point.getPrevPoint() != null) {
      moves += point.getMove();
      point = point.getPrevPoint();
    }
    String reverse = new StringBuilder(moves.trim()).reverse().toString();
    System.out.println(reverse);
    return reverse;
  }

  /**
   * Checks if the given position is already in the list
   * 
   * @param playerPosList
   * @param pos
   * @return
   */
  public boolean posIsIn(List<PlayerPos> playerPosList, PlayerPos pos) {
    for (PlayerPos p : playerPosList) {
      if (p.equals(pos))
        return true;
    }

    return false;
  }

  /**
   * 
   * @param itemsData
   * @return the player coordinate in an array, returns null if no player found
   */
  protected PlayerPos getPlayerCoord(char[][] itemsData, ArrayList<BoxPos> boxesCoord) {
    PlayerPos playerCoord;
    for (int y = 0; y < itemsData.length; y++) {
      for (int x = 0; x < itemsData[y].length; x++) {
        if (itemsData[y][x] == '@') {
          playerCoord = new PlayerPos(x, y, null, ' ', boxesCoord);
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
  protected ArrayList<BoxPos> getBoxesCoord(char[][] itemsData) {
    ArrayList<BoxPos> boxes = new ArrayList<BoxPos>();

    for (int y = 0; y < itemsData.length; y++) {
      for (int x = 0; x < itemsData[y].length; x++) {
        if (itemsData[y][x] == '$' || itemsData[y][x] == '*') {
          boxes.add(new BoxPos(x, y));
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
  protected ArrayList<int[]> getGoalsCoord(char[][] mapData) {
    ArrayList<int[]> goals = new ArrayList<int[]>();

    for (int y = 0; y < mapData.length; y++) {
      for (int x = 0; x < mapData[y].length; x++) {
        if (mapData[y][x] == '.') {
          int goal[] = new int[2];
          goal[0] = x;
          goal[1] = y;
          goals.add(goal);
        }
      }
    }
    return goals;
  }

  /**
   * 
   * @param mapData
   * @param playerCoord
   * @return
   */
  public List<PlayerPos> getNeighbors(char[][] mapData, PlayerPos playerCoord) {
    List<PlayerPos> neighbors = new ArrayList<>();
    if (isMoveValid(mapData, UP, playerCoord)) {
      ArrayList<BoxPos> movedBoxes = moveBoxes(playerCoord, UP);
      neighbors.add(new PlayerPos(playerCoord.getX() + UP[0], playerCoord.getY() + UP[1], playerCoord, 'u',
          movedBoxes));
    }

    if (isMoveValid(mapData, DOWN, playerCoord)) {
      ArrayList<BoxPos> movedBoxes = moveBoxes(playerCoord, DOWN);
      neighbors.add(new PlayerPos(playerCoord.getX() + DOWN[0], playerCoord.getY() + DOWN[1], playerCoord, 'd',
          movedBoxes));
    }

    if (isMoveValid(mapData, RIGHT, playerCoord)) {
      ArrayList<BoxPos> movedBoxes = moveBoxes(playerCoord, RIGHT);
      neighbors.add(new PlayerPos(playerCoord.getX() + RIGHT[0], playerCoord.getY() + RIGHT[1], playerCoord, 'r',
          movedBoxes));
    }

    if (isMoveValid(mapData, LEFT, playerCoord)) {
      ArrayList<BoxPos> movedBoxes = moveBoxes(playerCoord, LEFT);
      neighbors.add(new PlayerPos(playerCoord.getX() + LEFT[0], playerCoord.getY() + LEFT[1], playerCoord, 'l',
          movedBoxes));
    }

    return neighbors;
  }

  // Makes a deep copy of the boxes arraylist
  private ArrayList<BoxPos> moveBoxes(PlayerPos playerCoord, int[] move) {
    ArrayList<BoxPos> boxes = new ArrayList<BoxPos>();
    for (BoxPos b : playerCoord.getBoxesPos()) {
      if (b.getX() == playerCoord.getX() + move[0] && b.getY() == playerCoord.getY() + move[1]) {
        boxes.add(new BoxPos(b.getX() + move[0], b.getY() + move[1]));
      } else {
        boxes.add(new BoxPos(b));
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
  private boolean isMoveValid(char[][] mapData, int[] move, PlayerPos playerCoord) {
    int x = playerCoord.getX() + move[0];
    int y = playerCoord.getY() + move[1];

    if (mapData[y][x] == '#')
      return false;

    BoxPos movedBox = null;
    for (BoxPos b : playerCoord.getBoxesPos()) {
      if (b.getX() == x && b.getY() == y) {
        movedBox = new BoxPos(b.getX() + move[0], b.getY() + move[1]);
        break;
      }
    }

    if (movedBox != null) {
      if (mapData[movedBox.getY()][movedBox.getX()] == '#')
        return false;

      for (BoxPos b : playerCoord.getBoxesPos()) {
        if (b.getX() == movedBox.getX() && b.getY() == movedBox.getY()) {
          return false;
        }
      }
    }

    return true;
  }

}
