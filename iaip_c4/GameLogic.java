import java.util.ArrayList;
import java.util.*;

public class GameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private int min = 1;
    private int max = 2;
    private int[][] board;
    private int counter;
    private int alpha;
    private int beta;
    
    private ArrayList<Node> vertical = new ArrayList<Node>();
    private ArrayList<Node> horizontal = new ArrayList<Node>();
    private ArrayList<Node> rightDiagonal = new ArrayList<Node>();
    private ArrayList<Node> leftDiagonal = new ArrayList<Node>();

    private ArrayList<ArrayList<Node>> lists = new ArrayList<ArrayList<Node>>();



    public GameLogic() {
        
    }

    /**
    * Constructor used to create a new 
    **/
    public GameLogic(int x, int y, int playerID, int[][] board,int counter, ArrayList<Node> vertical,ArrayList<Node> horizontal,ArrayList<Node> rightDiagonal,ArrayList<Node> leftDiagonal,int alpha, int beta) {
        //TODO Write your implementation for this method
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        this.board = new int[x][y];
        this.counter = counter;
        //TODO Write your implementation for this method
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j<board[i].length; j++) {
                this.board[i][j] = board[i][j];
                
            }
        }

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

        this.alpha = alpha;
        this.beta = beta;

    }
    

    public GameLogic createOther(int x, int y, int playerID) {
        return new GameLogic(x,  y, playerID , this.board,this.counter-1, vertical, horizontal,rightDiagonal,leftDiagonal, alpha, beta);
    }
	
    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        //TODO Write your implementation for this method
        board = new int[x][y];
        lists.add(vertical);
        lists.add(horizontal);
        lists.add(rightDiagonal);
        lists.add(leftDiagonal);
    }
	
    public Winner gameFinished() {
        //TODO Write your implementation for this method

        Integer[] columns = getFreeColumns();

        
        for (ArrayList<Node> list : lists ) {
            Winner result = LineHelper.finalLineExistsIn(list);
            if (result != Winner.NOT_FINISHED && result != Winner.TIE) {
                System.out.println();
                printBoard();
                System.out.println();
                System.out.println("Game status:");
                System.out.println("WINNER");
                return result;
            }
        
        }

        if(columns != null && columns.length>0) {
            printBoard();
            System.out.println();
            System.out.println("Game status:");
            System.out.println("NOT FINISHED");
            
            return Winner.NOT_FINISHED;
        }
        else {
            System.out.println();
            printBoard();
            System.out.println();
            System.out.println("Game status:");
            System.out.println("TIE");
            return Winner.TIE;
        }




        
    }

    public void insertCoin(int column, int playerID) {
        //TODO Write your implementation for this method
        int free = getFreeRow(column);
        if(free != -1) {
            board[column][free] = playerID;

            LineHelper.insertHorizontal(horizontal,column,free,playerID);
            LineHelper.insertVertical(vertical,column,free,playerID);
            LineHelper.insertLeftDiagonal(leftDiagonal,column,free,playerID);
            LineHelper.insertRightDiagonal(rightDiagonal,column,free,playerID);
        }
        else System.out.println("column is full - column: " + column + " row: " + free);
    }



    public int decideNextMove() {
        //TODO Write your implementation for this method
        counter = 8;

        Integer[] columns = getFreeColumns();
        
        int bestAction = -1;
        int currentMin = Integer.MIN_VALUE;
        int currentMax = Integer.MAX_VALUE;
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        if (playerID == max) { //PLayer is MAX
            int v = Integer.MIN_VALUE;
            for(int i = 0; i < columns.length; i++) {

                GameLogic otherPlayer = this.createOther(x,y,min); //Ny spiller
                otherPlayer.insertCoin(columns[i],max); //Opdater spilleplade 
                
                 currentMin = otherPlayer.minValue();
       
                if (currentMin > v) {
                    v = currentMin;
                    bestAction = columns[i];
                }   else if (currentMin == v && Math.random() >=0.5d) {
                    v = currentMin;
                    bestAction = columns[i];

                }

            }
        System.out.println();
        } else { //Player is MIN
            int v = Integer.MAX_VALUE;
            for(int i = 0; i < columns.length; i++) {

                GameLogic otherPlayer = this.createOther(x,y,max); //Ny spiller
                otherPlayer.insertCoin(columns[i],min); //Opdater spilleplade 
                
                currentMax = otherPlayer.maxValue();
   
                if (currentMax < v) {
                    v = currentMax;
                    bestAction = columns[i];
                } else if (currentMax == v && Math.random() >=0.5d) {
                    v = currentMax;
                    bestAction = columns[i];

                }

            }
        
        }
        return bestAction;
    }

    
    public int maxValue() {
        if (terminalTest()) return utility();
        Integer[] columns = getFreeColumns();
        int v = Integer.MIN_VALUE;

        for(int i : columns) {
            GameLogic otherPlayer = this.createOther(x,y,max); //New Player
            otherPlayer.insertCoin(i,max); //Update board
            v = Math.max(otherPlayer.minValue(),v);
            if (v>= beta)
                return v;
            alpha = Math.max(alpha,v);
        }        
        return v;

        }
         

    public int minValue() {
        if (terminalTest()) return utility();

        Integer[] columns = getFreeColumns();
        int v = Integer.MAX_VALUE;

        for(int i : columns) {
            GameLogic otherPlayer = this.createOther(x,y,min); //New Player
            otherPlayer.insertCoin(i,min); //Update Board
            v = Math.min(otherPlayer.maxValue(),v);
            if (v<= alpha)
                return v;
            beta = Math.min(beta,v);
        }
        return v;
    }
             

    private Integer[] getFreeColumns() {
        ArrayList<Integer> columns = new ArrayList<Integer>();
        for(int i=0; i<board.length; i++) {
            if(getFreeRow(i) != -1)
                columns.add(i);
        }
        return columns.toArray(new Integer[columns.size()]);
    }

    private int getFreeRow(int column) {
        int i = 0;
        while(board[column][i]!=0) {
            i++;
            if(i >= board[column].length) return -1;
            
        }
        return i;
    }

    public boolean terminalTest() {

        if (counter <= 0) {
            return true;
        } else if (getFreeColumns().length <= 0)
            return true;

        else return false;


    }

    public int utility() {
        

        Integer[] columns = getFreeColumns();
        int[] utilities = new int[columns.length];
        int utility = 0;

        for (int x = 0; x < columns.length; x++) {
            int y = getFreeRow(x);
            utilities[x] += horizontalUtility(x,y);
            utilities[x] += verticalUtility(x,y);
            utilities[x] += rightDiagonalUtility(x,y);
            utilities[x] += leftDiagonalUtility(x,y);
        }
        for (int i = 0; i < utilities.length; i++)
            utility += utilities[i];

        return utility;


    }

    public int horizontalUtility(int x, int y) {  
            int utility = 0;
            for(Node n : horizontal) {
            Node tempNode = n;
            Node result = n;
            int partUtility = 0;
            int i = 0;
            if (playerID == n.playerID  && n.y == y){

            while(true) {
                i++;
            partUtility = (++partUtility)*(int)Math.pow(2,i);
                if(tempNode != null) {
                    result = tempNode;
                    tempNode = tempNode.getNext();
                     
                    } else {
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

    public void printBoard() {

        System.out.println("Board:");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j<board[i].length; j++) {
                System.out.print(" " + board[i][j]);
            }
            System.out.println();
        }
    }

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
