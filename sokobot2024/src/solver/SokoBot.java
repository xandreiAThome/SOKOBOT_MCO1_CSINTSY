package solver;

import java.util.ArrayList;
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

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */ // --------Move------ Right ---- Down ---- Left ------ Up
    final int MOVES[][] = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    int playerCoord[] = getPlayerCoord(itemsData);
    ArrayList<int[]> boxesCoord = getBoxesCoord(itemsData);
    System.out.println(isMoveValid(mapData, MOVES[0], boxesCoord, playerCoord));

    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "l";
  }

  /**
   * 
   * @param itemsData
   * @return the player coordinate in an array, the array will contain -1, -1 if
   *         it did not find the player
   */
  protected int[] getPlayerCoord(char[][] itemsData) {
    int playerCoord[] = new int[2];
    for (int y = 0; y < itemsData.length; y++) {
      for (int x = 0; x < itemsData[y].length; x++) {
        if (itemsData[y][x] == '@') {
          playerCoord[0] = x;
          playerCoord[1] = y;
          return playerCoord;
        }
      }
    }

    playerCoord[0] = -1;
    playerCoord[1] = -1;
    return playerCoord;
  }

  /**
   * 
   * @param itemsData
   * @return the coordinates of all the boxes
   */
  protected ArrayList<int[]> getBoxesCoord(char[][] itemsData) {
    ArrayList<int[]> boxes = new ArrayList<int[]>();

    for (int y = 0; y < itemsData.length; y++) {
      for (int x = 0; x < itemsData[y].length; x++) {
        if (itemsData[y][x] == '$') {
          int box[] = new int[2];
          box[0] = x;
          box[1] = y;
          boxes.add(box);
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
        if (mapData[y][x] == '$') {
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
   * @param move
   * @param boxesCoord
   * @param playerCoord
   * @return
   */
  public boolean isMoveValid(char[][] mapData, int[] move, ArrayList<int[]> boxesCoord, int[] playerCoord) {
    playerCoord[0] += move[0];
    playerCoord[1] += move[1];
    System.out.println(playerCoord[0] + ", " + playerCoord[1]);

    if (mapData[playerCoord[1]][playerCoord[0]] == '#')
      return false;

    int box[] = { -1, -1 }; // possible coord of box that the player moves
    int index = 0;
    for (int[] i : boxesCoord) {
      if (i[0] == playerCoord[0] && i[1] == playerCoord[1]) {
        box[0] = i[0];
        box[1] = i[1];
        break;
      }
      index++;
    }
    boxesCoord.remove(index);

    if (box[0] != -1 && box[1] != -1) {
      box[0] += move[0];
      box[1] += move[1];

      if (mapData[box[1]][box[0]] == '#')
        return false;

      for (int[] i : boxesCoord) {
        if (i[0] == box[0] && i[1] == box[1]) {
          return false;
        }
      }
    }

    return true;
  }
}
