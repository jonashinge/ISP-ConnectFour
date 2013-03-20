import java.util.ArrayList;
import java.util.*;

public class TheFirstLawofRobotics implements IGameLogic {
    private int x;
    private int y;
    private int playerID;
    private int min = 1;
    private int max = 2;
    private int[][] board;
    private int counter; //depth counter
    private int alpha;
    private int beta;


    //Lists used to maintain lines of possible wins
    private ArrayList<Node> vertical = new ArrayList<Node>();
    private ArrayList<Node> horizontal = new ArrayList<Node>();
    private ArrayList<Node> rightDiagonal = new ArrayList<Node>();
    private ArrayList<Node> leftDiagonal = new ArrayList<Node>();

    private ArrayList<ArrayList<Node>> lists = new ArrayList<ArrayList<Node>>();



    public TheFirstLawofRobotics() {   
        lists.add(vertical);
        lists.add(horizontal);
        lists.add(rightDiagonal);
        lists.add(leftDiagonal);
    }

    /**
    * Constructor used to create a new TheFirstLawofRobotics object. 
    **/
    public TheFirstLawofRobotics(int x, int y, int playerID, int[][] board,int counter, ArrayList<Node> vertical,ArrayList<Node> horizontal,ArrayList<Node> rightDiagonal,ArrayList<Node> leftDiagonal,int alpha, int beta) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        this.board = new int[x][y];
        this.counter = counter;
        
        //initialize board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j<board[i].length; j++) {
                this.board[i][j] = board[i][j];
                
            }
        }

        //Initialize lists of winning streaks/lines
        for (Node n : vertical) {
            this.vertical.add((Node)n.clone());
        }

        for (Node n : horizontal) {
            this.horizontal.add((Node)n.clone());
        }


        for (Node n : rightDiagonal) {
            this.rightDiagonal.add((Node)n.clone());
        }


        for (Node n : leftDiagonal) {
            this.leftDiagonal.add((Node)n.clone());
        }


        lists.add(vertical);
        lists.add(horizontal);
        lists.add(rightDiagonal);
        lists.add(leftDiagonal);

        this.alpha = alpha;
        this.beta = beta;

    }
    
    /**
    * Simple copy constructor used to simulate opponents turn.
    **/
    public TheFirstLawofRobotics createOther(int x, int y, int playerID) {
        return new TheFirstLawofRobotics(x,  y, playerID , this.board,this.counter-1, vertical, horizontal,rightDiagonal,leftDiagonal, alpha, beta);
    }

    /**
    * Initialization of game
    **/
    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        board = new int[x][y];
    }

    /**
    * Determine i there is a winner, a tie or if the game is not finished. 
    **/	
    public Winner gameFinished() {
        //TODO Write your implementation for this method

        Integer[] columns = getFreeColumns(); //Get a list of free columens, i.e. actions

        //Iterate over our lists of coin streaks. 
        for (ArrayList<Node> list : lists ) {
            Winner result = LineHelper.finalLineExistsIn(list); //Evaluate winner
            if (result != Winner.NOT_FINISHED && result != Winner.TIE) {
                System.out.println();
                printBoard();
                System.out.println();
                System.out.println("Game status:");
                System.out.println("WINNER");
                return result;
            }

        }

        //In case the game is still on
        if(columns != null && columns.length>0) {
            printBoard();
            System.out.println();
            System.out.println("Game status:");
            System.out.println("NOT FINISHED");
            
            return Winner.NOT_FINISHED;
        }
        else { //Tie
            System.out.println();
            printBoard();
            System.out.println();
            System.out.println("Game status:");
            System.out.println("TIE");
            return Winner.TIE;
        }




        
    }

    /**
    * Update board and auxillary data structures with new move
    **/
    public void insertCoin(int column, int playerID) {
        int free = getFreeRow(column); //return next free row in a column or -1 if not available
        if(free != -1) {
            board[column][free] = playerID; //Update board data structure

            //Auxillary data structures used to evaluate winner in gameFinished() and used to 
            //determine expected utility in evaluation function utility().

            LineHelper.insertHorizontal(horizontal,column,free,playerID);
            LineHelper.insertVertical(vertical,column,free,playerID);
            LineHelper.insertLeftDiagonal(leftDiagonal,column,free,playerID);
            LineHelper.insertRightDiagonal(rightDiagonal,column,free,playerID);
        }
        else System.out.println("column is full - column: " + column + " row: " + free);
    }


    /**
    * Decides next move for AI player. 
    **/
    public int decideNextMove() {
        counter = 8; //Cut-off depth is set here. 

        Integer[] columns = getFreeColumns(); //Free columns, i.e. actions
        
        //Initialize variables
        int bestAction = -1;
        int currentMin = Integer.MIN_VALUE;
        int currentMax = Integer.MAX_VALUE;
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;

        //MiniMax algorithm part 1
        if (playerID == max) { //PLayer is MAX
            int v = Integer.MIN_VALUE;
            for(int i = 0; i < columns.length; i++) {

                TheFirstLawofRobotics otherPlayer = this.createOther(x,y,min); //Create new player
                otherPlayer.insertCoin(columns[i],max); //Update with current players coin
                
                 currentMin = otherPlayer.minValue(); //recursive call -> bulk of MiniMax algorithm

                //Select action based off expected utility
                 if (currentMin > v) {
                    v = currentMin;
                    bestAction = columns[i];
                }   else if (currentMin == v && Math.random() >=0.5d) { //If more than one game state evalutes to the same, randomize
                    v = currentMin;
                    bestAction = columns[i];

                }

            }
            System.out.println("choose " + bestAction + " with utility " + v);
        } else { //Player is MIN
            int v = Integer.MAX_VALUE;
            for(int i = 0; i < columns.length; i++) {

                TheFirstLawofRobotics otherPlayer = this.createOther(x,y,max); //Createnew player
                otherPlayer.insertCoin(columns[i],min);  //Update with current players coin
                
                currentMax = otherPlayer.maxValue(); //recursive call -> bulk of MiniMax algorithm

                //Select action based off expected utility
                if (currentMax < v) {
                    v = currentMax;
                    bestAction = columns[i];
                } else if (currentMax == v && Math.random() >=0.5d) { //If more than one game state evalutes to the same, randomize
                    v = currentMax;
                    bestAction = columns[i];

                }

            }
            System.out.println("choose " + bestAction + " with utility " + v);
        }
        return bestAction;
    }

    /**
    * The maximizing player's turn
    **/
    public int maxValue() {
        if (terminalTest()) return utility(); //Terminal/cut-off test
        Integer[] columns = getFreeColumns(); //available actions
        int v = Integer.MIN_VALUE;

        //Iterate over actions and recursively progress through the game tree for Max.
        for(int i : columns) {
            TheFirstLawofRobotics otherPlayer = this.createOther(x,y,max); //One object per possible action
            otherPlayer.insertCoin(i,max); //Update board
            v = Math.max(otherPlayer.minValue(),v); //Update v
            //Alpha-beta prunning
            if (v>= beta) 
                return v;
            alpha = Math.max(alpha,v);
        }        
        return v;

    }

    /**
    * The minimizing player's turn
    **/
    public int minValue() {
        if (terminalTest()) return utility(); //Terminal/cut-off test
        Integer[] columns = getFreeColumns(); //available actions
        int v = Integer.MAX_VALUE;

        //Iterate over actions and recursively progress through the game tree for Min.
        for(int i : columns) {
            TheFirstLawofRobotics otherPlayer = this.createOther(x,y,min); //One object per possible action
            otherPlayer.insertCoin(i,min); //Update Board
            v = Math.min(otherPlayer.maxValue(),v); //Update v
            //Alpha-beta prunning
            if (v<= alpha)
                return v;
            beta = Math.min(beta,v);
        }
        return v;
    }

    /**
    * Available free columns, i.e. actions
    **/
    private Integer[] getFreeColumns() {
        ArrayList<Integer> columns = new ArrayList<Integer>();
        for(int i=0; i<board.length; i++) {
            if(getFreeRow(i) != -1)
                columns.add(i);
        }
        return columns.toArray(new Integer[columns.size()]);
    }

    /**
    * Next free row in a particular column
    **/
    private int getFreeRow(int column) {
        int i = 0;
        while(board[column][i]!=0) {
            i++;
            if(i >= board[column].length) return -1;
            
        }
        return i;
    }

    /**
    * Terminal/cut-off test function
    **/
    public boolean terminalTest() {

        //Counter is decreased for each new object created.
        if (counter <= 0) { //depth reached
            return true;
        } else if (getFreeColumns().length <= 0) //No actions left
        return true;

        else return false;


    }


    /**
    * Our evaluation function
    **/
    public int utility() {
        Integer[] columns = getFreeColumns();
        int[] utilities = new int[columns.length];
        int utility = 0;

        //Calculate an expected utility off our heuristic function(s)
        for (int x = 0; x < columns.length; x++) {
            int y = getFreeRow(x);
            utilities[x] += horizontalUtility(x,y);
            utilities[x] += verticalUtility(x,y);
            utilities[x] += rightDiagonalUtility(x,y);
            utilities[x] += leftDiagonalUtility(x,y);
        }
        //Sum
        for (int i = 0; i < utilities.length; i++)
            utility += utilities[i];

        return utility;


    }

    /**
    * Heuristic for horizontal winning lines
    **/
    public int horizontalUtility(int x, int y) {  
        int utility = 0;
            //Use auxillary data structure with linked lists of lines
        for(Node n : horizontal) {
            Node tempNode = n;
            Node result = n;
            int partUtility = 0;
            int i = 0;

            //Only count if for this player and in the same row
            if (playerID == n.playerID  && n.y == y){
                while(true) {
                    i++;
            partUtility = (++partUtility)*(int)Math.pow(2,i); //Weights added to longer lines
            if(tempNode != null) {
                result = tempNode;
                tempNode = tempNode.getNext();

            } else {
                        //If free cell/action is not next to line, do not count it.
                if (result.x != x-1)
                    partUtility = 0;
                utility += partUtility;
                break;
            }

        }
    }
}
return utility;

}



    /**
    * Heuristic for Vertical winning lines
    **/
    public int verticalUtility(int x, int y) {  
        int utility = 0;
        for(Node n : vertical) {
            Node tempNode = n;
            Node result = n;

            int partUtility = 0;
            int i = 0;
            if (playerID == n.playerID  && n.x == x){

                while(true) {
                    i++;
                    partUtility = (++partUtility)*(int)Math.pow(2,i);
                    if(tempNode != null) {

                        result = tempNode;
                        tempNode = tempNode.getNext();

                    } else {

                        if (result.y != y-1)
                            partUtility = 0;

                        utility += partUtility;
                        break;
                    }

                }
            }
        }        
        return utility;
        
    }



    /**
    * Heuristic for diagonal to the right winning lines
    **/
    public int rightDiagonalUtility(int x, int y) {  
        int utility = 0;
        for(Node n : rightDiagonal) {
            Node tempNode = n;
            Node result = n;

            int partUtility = 0;
            int i = 0;
            if (playerID == n.playerID ){

                while(true) {

                    i++;
                    partUtility = (++partUtility)*(int)Math.pow(2,i);
                    if(tempNode != null) {

                        result = tempNode;
                        tempNode = tempNode.getNext();

                    } else {

                        if (result.y != y+1 && result.x != x+1)
                            partUtility = 0;


                        utility += partUtility;
                        break;
                    }

                }
            }
        }        
        return utility;
        
    }


    /**
    * Heuristic for diagonal to the left winning lines
    **/
    public int leftDiagonalUtility(int x, int y) {  
        int utility = 0;
        for(Node n : leftDiagonal) {
            Node tempNode = n;
            Node result = n;

            int partUtility = 0;
            int i = 0;
            if (playerID == n.playerID ){

                while(true) {

                    i++;
                    partUtility = (++partUtility)*(int)Math.pow(2,i);
                    if(tempNode != null) {

                        result = tempNode;
                        tempNode = tempNode.getNext();

                    } else {

                        if (result.y != y+1 && result.x != x-1)
                            partUtility = 0;


                        utility += partUtility;
                        break;
                    }

                }
            }
        }        
        return utility;
        
    }


        /**
        * Auxillary method for printing board
        **/
        public void printBoard() {

            System.out.println("Board:");
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j<board[i].length; j++) {
                    System.out.print(" " + board[i][j]);
                }
                System.out.println();
            }
        }


        /**
        * Auxillary method for printing lists
        **/
        public void printDataStructure(ArrayList<Node> list) {

            for(Node n : list) {
                System.out.print("x:" + n.x + " y:" + n.y + " playerid:" + n.playerID + ", ");
                while(n.getNext() != null) {
                    System.out.print("x:" + n.getNext().x + " y:" + n.getNext().y + " playerid:" + n.getNext().playerID + ", ");
                    n = n.getNext();
                } 
                System.out.println();
            }

        } 

    }
