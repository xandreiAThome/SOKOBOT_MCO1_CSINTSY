package solver;

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly. test5
     */
    int playerCoord[] = getPlayerCoord(itemsData);
    System.out.println(playerCoord[0] + ", " + playerCoord[1]);

    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  }

  /**
   * 
   * @param itemsData
   * @return the player coordinate in an array, the array will contain -1, -1 if
   *         it did not find the player
   */
  public int[] getPlayerCoord(char[][] itemsData) {
    int playerCoord[] = new int[2];
    for (int i = 0; i < itemsData.length; i++) {
      for (int j = 0; j < itemsData[i].length; j++) {
        if (itemsData[i][j] == '@') {
          playerCoord[0] = i;
          playerCoord[1] = j;
          return playerCoord;
        }
      }
    }

    playerCoord[0] = -1;
    playerCoord[1] = -1;
    return playerCoord;
  }

}
