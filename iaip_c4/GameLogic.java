import java.util.ArrayList;
import java.util.*;

public class GameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private int max = 2;
    private int min = 1;
    private int[][] board;
    private int counter;
    
    private ArrayList<Node> vertical = new ArrayList<Node>();
    private ArrayList<Node> horizontal = new ArrayList<Node>();
    private ArrayList<Node> rightDiagonal = new ArrayList<Node>();
    private ArrayList<Node> leftDiagonal = new ArrayList<Node>();



    public GameLogic() {
        //TODO Write your implementation for this method
    }

    public GameLogic(int x, int y, int playerID, int[][] board,int counter) {
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
    }
    

    public GameLogic createOther(int x, int y, int playerID) {
        return new GameLogic(x,  y,  playerID, this.board,this.counter);
    }
	
    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        //TODO Write your implementation for this method
        board = new int[x][y];
    }
	
    public Winner gameFinished() {
        //TODO Write your implementation for this method

        Integer[] columns = getFreeColumns();

        // test print datastructure
        System.out.println("Horizontal:");
        printDataStructure(horizontal);
        System.out.println();

        System.out.println("Vertical:");
        printDataStructure(vertical);
        System.out.println();

        System.out.println("Right diagonal:");
        printDataStructure(rightDiagonal);
        System.out.println();

        System.out.println("Left diagonal:");
        printDataStructure(leftDiagonal);
        System.out.println();

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
        counter = 10;

        Integer[] columns = getFreeColumns();
        
        int bestAction = -1;
        int currentMin = Integer.MIN_VALUE;
        int currentMax = Integer.MAX_VALUE;
        if (playerID == max) { //PLayer is MAX
            int v = Integer.MIN_VALUE;
            for(int i = 0; i < columns.length; i++) {

                GameLogic otherPlayer = this.createOther(x,y,min); //Ny spiller
                //otherPlayer.insertCoin(columns[i],max); //Opdater spilleplade 
                
                 currentMin = otherPlayer.minValue();
       
                if (currentMin > v) {
                    v = currentMin;
                    bestAction = columns[i];
                } else if (currentMin == v && Math.random() >=0.5d) {
                    v = currentMin;
                    bestAction = columns[i];

                }

            }
        System.out.println();
        } else { //Player is MIN
            int v = Integer.MAX_VALUE;
            for(int i = 0; i < columns.length; i++) {

                GameLogic otherPlayer = this.createOther(x,y,max); //Ny spiller
                //otherPlayer.insertCoin(columns[i],min); //Opdater spilleplade 
                
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
        if (bestAction <0)
            System.out.println("FEJL!!!");
        System.out.println("decideNextMove bestAction is " + bestAction);
        System.out.println();
        return bestAction;
    }

    
    public int maxValue() {
        if (terminalTest()) return utility();
        Integer[] columns = getFreeColumns();
        int v = Integer.MIN_VALUE;

        for(int i : columns) {
            GameLogic otherPlayer = this.createOther(x,y,min); //Ny spiller
            otherPlayer.insertCoin(i,max); //Opdater spilleplade 
            v = Math.max(otherPlayer.minValue(),v);
        }
    
        return v;

        }
         

    public int minValue() {
        if (terminalTest()) return utility();

        Integer[] columns = getFreeColumns();
        int v = Integer.MAX_VALUE;

        for(int i : columns) {
            GameLogic otherPlayer = this.createOther(x,y,max); //Ny spiller
            otherPlayer.insertCoin(i,min); //Opdater spilleplade 
            v = Math.min(otherPlayer.maxValue(),v);
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
        counter -= 1;

        if (getFreeColumns().length <= 0)
            return true;
        
        if (counter <= 0) {
            return true;
        }
        else return false;


    }

    public int utility() {
        return 1;
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
