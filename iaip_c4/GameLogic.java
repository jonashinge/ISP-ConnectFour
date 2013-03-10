import java.util.ArrayList;

public class GameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private int max = 2;
    private int min = 1;
    private int[][] board;
    private int counter;
    

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
       // System.out.println("New object " + counter);
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
        if(columns != null && columns.length>0) 
            return Winner.NOT_FINISHED;
        else
        return Winner.TIE;
    }


    public void insertCoin(int column, int playerID) {
        //TODO Write your implementation for this method
        int free = getFreeRow(column);
        if(free != -1) {
            board[column][free] = playerID;
            System.out.println("user " + playerID + " inserted at column: " + column + " at row:" + free);
        }
        else System.out.println("column is full");
    }

    public int decideNextMove() {
        //TODO Write your implementation for this method
        counter = 10;

        if (playerID == max) {
             if (terminalTest()) return utility();
            
            Integer[] columns = getFreeColumns();
            int v = Integer.MIN_VALUE;
            int bestAction = -1;

            for(int i = 0; i < columns.length; i++) {
                GameLogic otherPlayer = this.createOther(x,y,min); //Ny spiller
                otherPlayer.insertCoin(i,max); //Opdater spilleplade 
                int currentMin = otherPlayer.minValue();
                if (currentMin > v) {
                    v = currentMin;
                    bestAction = i;
                }
                
            }

return bestAction
            
        } else {
        
        }
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
        if (counter <= 0) {
            System.out.println("terminated.");
            printBoard();
            return true;
        }
        else return false;

    }

    public int utility() {

        return 1;
    }

    public void printBoard() {

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j<board[i].length; j++) {
                System.out.print(" " + board[i][j]);
            }
            System.out.println();
        }

    }

}
