package solver;

import java.util.ArrayList;

public class SokoBot {

  private char[][] mapData;
  private char[][] itemsData;

  private int width;
  private int height;

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

    this.mapData = mapData;
    this.itemsData = itemsData;
    this.width = width;
    this.height = height;
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */
    //System.out.println(getBotPos(itemsData).getX() + " " + getBotPos(itemsData).getY());

    // TODO: remove lines for pathfinding to make way for actual solving
    /* DEBUG: testing to see if pathfinder works. Assign the x and y values of first
     *  passed Position in the pathFind function below to set a different target.
     *  goal at 17,8 works on level "original1".
     */
    String road = pathFind(new Position(17,8),getBotPos(itemsData));

    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    // TODO: replace "road" string with the actual solution and not just a path
    return road;//"lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  }

  public Position getBotPos(char[][] itemsData){
    Position output = new Position(0,0);

    for (int r = 0; r < height; r++){
      for (int c = 0; c < width; c++){
        if (itemsData[r][c] == '@'){
          output.setPosition(c,r);
        }
      }
    }

    return output;
  }

  /**
   * given an end position and the player's starting position, return a path
   * @return the list of moves to reach the goal position if a path is found
   * "x" if no path to the target is found
   */
  public String pathFind(Position goal, Position start){

    // declare list to hold frontier path nodes
    ArrayList<Node> frontier = new ArrayList<>();

    // declare list to hold explored path nodes
    ArrayList<Node> explored = new ArrayList<>();

    // declare current node being explored, initialize with start
    Node current = new Node(start.getX(), start.getY(),0,goal,null);

    // add current position to frontier before exploring it
    frontier.add(current);

    //System.out.println(frontier.size());

    current.setPrevious(current);

    // while goal state has not been explored and frontier is not empty
    while (hasPosition(goal.getX(),goal.getY(),explored) == -1 && !frontier.isEmpty()){
      // the node to expand will be the one in the frontier with the lowest overall cost that has not been rejected
      current = cheapestNode(frontier);
      // call method to explore the current node
      exploreNode(current,frontier,explored,goal);
      //System.out.println("frontier:" + frontier.size());
      //printList(frontier);
    }

    int goalIndex = hasPosition(goal.getX(),goal.getY(),explored);
    StringBuilder sb = new StringBuilder();
    // if goal was found
    if (goalIndex != -1){
      // back-track from goal until start state has been reached
      Node node = explored.get(goalIndex);
      // while the current node is not at the start yet
      while (!equalPos(node.getPos(),start)){
        //System.out.println("path back: "+node.getPos().getX() + " " + node.getPos().getY());
        // append the moves
        sb.append(node.getMove());
        node = node.getPrevious();
      }
      // return the moves in reverse to get the action sequence
      return sb.reverse().toString();
    }
    // if no path can be found simply return an "x"
    return "x";
  }

  /**
   *
   * @param a first position
   * @param b second position
   * @return true if the first and second position
   * objects have the same coordinates
   */
  private boolean equalPos(Position a, Position b){
    return (a.getX() == b.getX() && a.getY() == b.getY());
  }

  /**
   * for debugging purposes only, prints out the costs
   * of a given list of nodes
   * @param nodes is the collection of nodes
   */
  private void printList(ArrayList<Node> nodes){
    for (Node node : nodes) {
      System.out.println(node.getCost());
    }
  }

  private Node cheapestNode(ArrayList<Node> frontier){

    int least = 0;

    for (int i = 0; i < frontier.size(); i++){
      if (frontier.get(i).getCost() < frontier.get(least).getCost()){
        least = i;
      }
    }

    return frontier.get(least);
  }

  /**
   *
   * @param x horizontal position
   * @param y vertical position
   * @param nodes
   * @return the index where an accepted node of the
   * same position is found, -1 if none is found
   */
  private int hasPosition(int x, int y, ArrayList<Node> nodes){

    int output = -1;
    int i = 0;

    if (!nodes.isEmpty()){
      for (Node n : nodes){

        if (n.getPos().getX() == x && n.getPos().getY() == y){
          return i;
        }
        i++;
      }
    }

    return output;
  }

  /**
   * Determines whether a given x,y position can be
   * traversed by the player
   * @param x horizontal position
   * @param y vertical position
   * @return whether the given coordinates are a valid
   * position that can be traversed by the player
   */
  private boolean validPos(int x, int y){

    boolean output = x >= 0 && x < width && y >= 0 && y < height &&
            (mapData[y][x] != '#' && itemsData[y][x] != '$');

    // note that the zero coordinate is the top left
    //System.out.println(output);
    return output;
  }

  /**
   * Method for determining if a given position x,y does not
   * coincide with the coordinates of a Position
   * @param x horizontal position
   * @param y vertical position
   * @param pos is a Position object that the values x and y
   *            will be compared to
   * @return true if given x,y do not coincide with the given Position
   */
  private boolean notAtPosition(int x, int y, Position pos){
    //System.out.println(x != previous.getX() && y != previous.getY());
    return x != pos.getX() || y != pos.getY();
  }

  /**
   * Expands the node by attempting to add adjacent positions as
   * nodes in the frontier
   * @param node the current node being explored
   * @param frontier the collection of nodes in the frontier
   * @param explored the collection of nodes that have been explored
   * @param goal the position of the goal
   */
  private void exploreNode(Node node, ArrayList<Node> frontier, ArrayList<Node> explored, Position goal){
    // add node to list of explored nodes
    explored.add(node);

    // DEBUGGING LINE to display explored
    //System.out.println("explored: "+ node.getPos().getX()+" "+node.getPos().getY());

    Position pos = node.getPos();

    int x = pos.getX(); int y = pos.getY()-1;

    //TODO: make code below more compact and readable

    // if position above current node is valid and not previous
    if (validPos(x,y) && notAtPosition(x,y,node.getPrevious().getPos())){
      // call method to add it to frontier
      addToFrontier(x,y,frontier,explored,node,goal);
    }

    x = pos.getX(); y = pos.getY()+1;
    if (validPos(x,y) && notAtPosition(x,y,node.getPrevious().getPos())){
      // call method to add it to frontier
      addToFrontier(x,y,frontier,explored,node,goal);
    }

    x = pos.getX()-1; y = pos.getY();
    if (validPos(x,y) && notAtPosition(x,y,node.getPrevious().getPos())){
      // call method to add it to frontier
      addToFrontier(x,y,frontier,explored,node,goal);
    }

    x = pos.getX()+1; y = pos.getY();
    if (validPos(x,y) && notAtPosition(x,y,node.getPrevious().getPos())){
      // call method to add it to frontier
      addToFrontier(x,y,frontier,explored,node,goal);
    }

    // remove node from frontier
    frontier.remove(node);
  }

  /**
   * Takes values x and y (assumed to be valid) and attempts to instantiate
   * a node to have that position
   * @param x horizontal position
   * @param y vertical position
   * @param frontier the collection of nodes in the frontier
   * @param explored the collection of nodes that have been explored
   * @param prevNode the previous node from where the new node was discovered from
   * @param goal the position of the goal
   */
  private void addToFrontier(int x, int y, ArrayList<Node> frontier, ArrayList<Node> explored,
                             Node prevNode, Position goal){
    // DEBUGGING LINE to display frontier
    //System.out.println("frontier: "+ x+" "+y);

    //TODO: remove the need for a "rejected state" from pathNode. rejected nodes are simply removed

    boolean checkingExplored = false;
    int index = hasPosition(x,y,frontier);

    if (index == -1){
      index = hasPosition(x,y,explored);
      checkingExplored = true;
    }

    Node node = new Node(x,y,prevNode.getCost()+1,goal,prevNode);
    // declare new PathNode and add to frontier
    frontier.add(node);

    // if the node has an identical position to a previously explored node or a node in the frontier
    if (index > -1){
      // pathNode with lesser cost wins
      if (checkingExplored){
        if (explored.get(index).getCostWithHeuristic() <= node.getCostWithHeuristic()){
          node.setRejected(true);
          frontier.remove(node);
        }
      }else{
        if (frontier.get(index).getCostWithHeuristic() <= node.getCostWithHeuristic()){
          node.setRejected(true);
          frontier.remove(node);
        }else{
          frontier.get(index).setRejected(true);
          frontier.remove(index);
        }
      }
    }
  }
}
