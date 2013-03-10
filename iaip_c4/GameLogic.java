import java.util.ArrayList;

public class GameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;

    private int[][] board;
    
    public GameLogic() {
        //TODO Write your implementation for this method
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
        return Winner.NOT_FINISHED;
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
        Integer[] columns = getFreeColumns();
        //System.out.println(columns.length);
        for(int i : columns)
            System.out.println(i);

        if(columns != null && columns.length>0) {
            int r = (int)(Math.random()*columns.length-1);
            return columns[r];
        }
        return -1;
    }

    // custom

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

}
