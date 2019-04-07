package minesweeper;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

/******************************************************************************
 *  Compilation:  javac Minesweeper.java
 *  Execution:    java Minesweeper m n p filename
 *  
 *  Creates an MxN minesweeper game where each cell is a bomb with
 *  probability p. Prints out the m-by-n game and the neighboring bomb
 *  counts.
 *  
 *  Tien-Ping Tan Modification:
 *  Modified from the original code at http://introcs.cs.princeton.edu/java/14array/Minesweeper.java.html 
 *  to include 2 extra columns at the left and right, and also 2 extra rows top and bottom. 
 *  Reason? You are allowed to "open up" all "0" squares and their surrounding. 
 *  Just like when you play the game interactively, when you click on a "0", it will open up 
 *  the surrounding for you. Here, we open up all "0" to you, so you have something to start with
 *  when solving the puzzle.
 *  NOTE:
 *  1. Use the gameMap[][] for solving the puzzle.
 *  2. Use the mineMap[][] for reference.
 *  3. Use the openSquare() to open up a square.
 *
 *  Sample execution:
 *
 *      % java Minesweeper  5 10 0.3 minemap.txt
 *      * . . . . . . . . * 
 *      . . . . . . * . . . 
 *      . . . . . . . . * * 
 *      . . . * * * . . * . 
 *      . . . * . . . . . . 
 *
 *      * 1 0 0 0 1 1 1 1 * 
 *      1 1 0 0 0 1 * 2 3 3 
 *      0 0 1 2 3 3 2 3 * * 
 *      0 0 2 * * * 1 2 * 3 
 *      0 0 2 * 4 2 1 1 1 1 
 *
 *
 ******************************************************************************/

public class Minesweeper { 
	
	public final int MINE=9;
	public final int CLOSE=-1;  
	public final int BLANK=0;
	
	private int[][] mineMap;
	private int[][] gameMap;
	private TreeSet<String> mineList;
	
	private static boolean debug = false;

	
	
	/**
	 * Create a new minemap and save the minemap to a file
	 * @param m
	 * @param n
	 * @param p
	 * @param filename
	 */
	public Minesweeper(int m, int n, double p, String filename) {

		mineMap = generateMineMap(m, n, p);
		gameMap = createGameMap(mineMap);
		mineList = getAllMineLocation(mineMap);
		//System.out.println("Total number of mines: " + mineList.size());
		
		saveMineMap(mineMap, filename);
		
		
		//System.out.println("MINE MAP");
		//printMap(mineMap);
		//System.out.println("GAME MAP");
		//printMap(gameMap);
	}
	
	
	/**
	 * Load minemap from file
	 * @param filename
	 */
	public Minesweeper(String filename) {
		
		mineMap = loadMineMap(filename);
		printMineMap();
		
		gameMap = createGameMap(mineMap);
		printGameMap();
		
		mineList = getAllMineLocation(mineMap);
		System.out.println("Total mines: " + mineList.size());
		

	}
	
	/**
	 * Generate a random mine map. Mine is tag with the letter 9.
	 * @param m row
	 * @param n columns
	 * @param p probability of mine. If m=10,n=10, total size=100. p=0.1, so total mines = 10.
	 * Special value in the map: 9 => mine, -1 => close 
	 * @return
	 */
    public int[][] generateMineMap(int m, int n, double p) { 
        //int m = Integer.parseInt(args[0]);
        //int n = Integer.parseInt(args[1]);
        //double p = Double.parseDouble(args[2]);
      
        // game grid is [1..m][1..n], border is used to handle boundary cases
        boolean[][] bombs = new boolean[m+2][n+2];
        int[][] mineMap = new int[m+4][n+4];
        
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++)
                bombs[i][j] = (Math.random() < p);

        /* sytan close this
        // print game
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++)
                if (bombs[i][j]) System.out.print("* ");
                else             System.out.print(". ");
            System.out.println();
        }
        */
        
        // sol[i][j] = # bombs adjacent to cell (i, j)
        int[][] sol = new int[m+2][n+2];
        for (int i = 0; i <= m+1; i++)
            for (int j = 0; j <= n+1; j++)
                // (ii, jj) indexes neighboring cells
                for (int ii = i - 1; ii <= i + 1; ii++)
                    for (int jj = j - 1; jj <= j + 1; jj++)
                        if (ii>=0 && jj>=0 && ii<m+2 && jj<n+2 && bombs[ii][jj]) sol[i][j]++;

        // print solution
        //System.out.println(); //sytan
        for (int i = 0; i <= m+1; i++) {
            for (int j = 0; j <= n+1; j++) {
                if (bombs[i][j]){ 
                	//System.out.print("* "); 
                	mineMap[i+1][j+1] = 9;
                } else{    
                	mineMap[i+1][j+1] = sol[i][j];
                	//System.out.print(sol[i][j] + " ");
                }
            }
            //System.out.println(); //sytan
        }
        
        //printMap(mineMap);
        
        return mineMap;

    }
    
    public int[][] createGameMap(int[][] mineMap){
    	
    	int m = mineMap.length;
    	int n = mineMap[0].length;
    	int[][] gameMap = new int[m][n];
    	
    	//initialize game map, close all square
    	for(int i=0; i<m; i++){
    		for(int j=0; j<n; j++){
    			//Set the square to close 
    			gameMap[i][j] = CLOSE;
    		}
    	}
    	

    	
    	
    	//open all square=0
    	for(int i=0; i<m; i++){
    		for(int j=0; j<n; j++){
    			if (mineMap[i][j] == BLANK)
    				openSquare(i, j, gameMap);
    		}
    	}
    	
    	//uncomments for printout and verify
    //printMap(gameMap);
    	

    	
    	return gameMap;
    }
    
    /**
     * Open a square in the map. If the square is a blank, it will open up neighboring squares.
     * @param x row
     * @param y column
     * @return if return true, it is not a mine. If it is false, you open up a mine!
     */
    private boolean openSquare(int x, int y, int[][] gameMap){
    	

    	if (gameMap[x][y] == CLOSE){
    		
    		//open the square
    		gameMap[x][y] = mineMap[x][y];
    		
    		if (gameMap[x][y] == BLANK){
    			//recursively open the neighboring squares
    			for(int i=x-1; i<=x+1; i++){
    				for(int j=y-1; j<=y+1; j++){
    					if (i>=0 && j>=0 && i<mineMap.length && j<mineMap[0].length){
    						openSquare(i, j, gameMap);
    					} 
    				}
    			}
    		} else if (gameMap[x][y] != MINE){
    			//continue the game
    			return true;
    		} else{
    			//you open up a mine!!!
    			return false;	
    		}
    	} else{
    		//the square already open. Do nothing
    		return true;
    	}
    	
    	return true;
    }
    
    
    public void saveMineMap(int[][] mineMap, String filename) {
    		try {
    			FileWriter fw = new FileWriter(filename);
    			
    			//keep the row and column information. We need it when we are loading it.
    			fw.write(mineMap.length + " " + mineMap[0].length + "\n");
    			
    			for (int i = 0; i < mineMap.length; i++) {
    	            for (int j = 0; j < mineMap[i].length; j++) {
    	                fw.write(mineMap[i][j] + "\t");
    	            }
    	            fw.write("\n");;
    	        }
    			
    			fw.close();
    		} catch(IOException e) {
    			System.out.println(e);
    		}
    }
    
    public void printGameMap() {
        if (!debug)
        {
    		System.out.println("GAME MAP");
    		printMap(gameMap);
    		System.out.println();
        }
    }
    
    public void printMineMap() {
    	    System.out.println("MINE MAP");
    		printMap(mineMap);
    		System.out.println();
    }
    
    public void printMap(int[][] mineMap) {
    	
    		for (int i = 0; i < mineMap.length; i++) {
            for (int j = 0; j < mineMap[i].length; j++) {
                System.out.print(mineMap[i][j] + "\t");
            }
            System.out.println();
        }
    }
    
    public TreeSet<String> getAllMineLocation(int[][] mineMap) {
    	
    	    TreeSet<String> mineList = new TreeSet<String>();
    	
		for (int i = 0; i < mineMap.length; i++) {
			for (int j = 0; j < mineMap[i].length; j++) {
				if (mineMap[i][j] == MINE) {
					String mine = i + " " + j;
					mineList.add(mine);
					//System.out.println(i + " " + j);
				}
			}
		}
		
		return mineList;
    }
    
    
    protected Vector<String> extractWords(String sentence) {
	    Vector<String> words = new Vector<String>();
	    String exp = "\\S+";

	    //floating point
	    if (sentence != null) {
	      Pattern p = Pattern.compile(exp);
	      Matcher m = p.matcher(sentence);

	      //we only need the first 3 strings
	      while (m.find()) {
	        words.add(sentence.substring(m.start(), m.end()));
	      }
	    }

	    return words;
	  }
    
    
    public int[][] loadMineMap(String filename) {
    		
    		int[][] mineMap=null;
    		
    		try{
			FileReader fr = new FileReader(filename);
			LineNumberReader lnr = new LineNumberReader(fr);
			
			String line = lnr.readLine();
			int pos = line.indexOf(" ");
			int m = Integer.parseInt(line.substring(0, pos));
			int n = Integer.parseInt(line.substring(pos+1));
			
			//initialize mineMap
			mineMap = new int[m][n];
			
			line = lnr.readLine();
			int mCounter = 0;
			
			while(line != null) {

				Vector<String> words = extractWords(line);
				
				for(int i=0; i<words.size(); i++) {
					mineMap[mCounter][i] = Integer.parseInt(words.get(i));
				}
				mCounter++;
				line = lnr.readLine();
			}

			
			lnr.close();
			fr.close();
			
    		} catch(IOException e) {
			System.out.println(e);
    		}
    		
    		
		
		return mineMap;
    }
    
    public boolean solution()
    {
        //printMineMap();
        /* Randomly open 1 box */
        Random rand = new Random();
        openSquare(getRandomNumber(), getRandomNumber(), gameMap);

        reduceMine();
        //System.out.println("****** FINAL SOLUTION *******");
        //printGameMap();
        
        if (getResult() && getMineLeft()==0)
        {
            //System.out.println("You win!");
            return true;
        }
        else
        {
            //System.out.println("You lose!");
            //debug purpose only;
            printMineMap();
            printGameMap();
            return false;
        }
        //printGameMap();
    }
    
    /* Validate the gamemap result see if we tag correctly? */
    public boolean getResult()
    {
        boolean flag = false;
        for (int i=0;i<gameMap.length;i++)
        {
            for(int j=0;j<gameMap.length;j++)
            {
                if (gameMap[i][j] == MINE)
                {
                    /* Hi, i am bomb */
                    flag = tagMine(i,j);
                    if (!flag)
                    {
                        // if solution is wrong,label the wrong answer for debugging, terminate and return false
                        gameMap[i][j] = 1000;
                        return flag;
                    }
                }
            }
        }
        return flag;
    }

    /* Random num generator */
    public int getRandomNumber()
    {
        Random rand = new Random();
        return rand.nextInt(mineMap.length);
    }
    
    /* Based on the tagMine, try to solve the array by checking potential mine only */
    public void reduceMine()
    {
        for (int i=0;i<mineMap.length;i++)
        {
            for (int j=0;j<mineMap.length;j++)
            {
                // Just check at each single cell, if potential bomb only evaluate
                if (gameMap[i][j] == -1)
                {
                    int newMineX = i-1;
                    int newMineY = j-1;
                    
                    while (true)
                    {
                        // start of boundary checking
                        if (newMineX < 0 || newMineY < 0)
                            break;
                        if (newMineX >= gameMap.length || newMineY >= gameMap.length)
                            break;
                        // end of boundary checking

                        // Logic start at here!
                        if (gameMap[newMineX][newMineY] == 0)
                        {
                            // if the centre is 0, this is confirm not a bomb
                            //gameMap[i][j] = mineMap[i][j];
                            openSquare(i,j,gameMap);
                        }
                        else if(gameMap[newMineX][newMineY] != 0 && gameMap[newMineX][newMineY] != MINE)
                        {
                            //check if any bomb discover
                            checkPotentialMine(newMineX, newMineY, gameMap[newMineX][newMineY]);
                        }
                        
                        // this part does the shifting for row and check if we done checking neighbour
                        //if (newMineX <= i+1 && newMineY <= j+1)
                        if (newMineX <= i+1 && newMineY <= j)
                        {
                            // if we done checking row, then advance to next row
                            // start from column 0
                            if (newMineX == i+1)
                            {
                                newMineY += 1;
                                newMineX = i - 1;
                            }
                            else
                            {
                                // just keep checking to right handside
                                newMineX++;
                            }
                        }
                        else
                        {
                            // end of loop, just end it
                            break;
                        }
                    }
                }
            }
        }
    }

    // recursively checking bomb
    public void checkPotentialMine(int x, int y, int bomb)
    {
        int count = 0;

        // check surrounding see if any bomb found
        for (int i=x-1;i<=x+1;i++)
        {
            for (int j=y-1;j<=y+1;j++)
            {
                if (gameMap[i][j] == MINE)
                {
                    count += 1;
                }
            }
        }
        
        // Start labelling -1 to be bomb or just click on it
        for (int i=x-1;i<=x;i++)
        {
            for (int j=y-1;j<=y;j++)
            {
                if (gameMap[i][j] == -1)
                {
                    // if bomb ady found in square, just open it
                    if (count >= bomb)
                    {
                        //gameMap[i][j] = mineMap[i][j];
                        openSquare(i,j,gameMap);
                        printGameMap();
                    }
                    else
                    {
                        gameMap[i][j] = MINE;
                        count += 1;
                        printGameMap(); //debug purpose only
                    }
                }
            }
        }
    }

    /* return current minelist size */
    public int getMineLeft()
    {
        return mineList.size();
    }
    /**
     * Tag a mine
     * @param i i row
     * @param j j column
     * @return
     */
    public boolean tagMine(int i, int j) {
    	
    		String mine = new String(i + " " + j);

    		if (mineList.contains(mine)) {
    			mineList.remove(mine);
    			//System.out.println("CORRECT ANSWER!");
    			//System.out.println("Number of mines: " + mineList.size());
    			return true;
    		} else {
    			//System.out.println("WRONG ANSWER! @ " +j + " "+i);
    			return false;
    		}
    	
    }
    
    public static void main(String[] args){
    	debug = true;
        int count_win = 0;
        for (int j=0;j<100;j++)
        {
            //Generate a new map
            Minesweeper m = new Minesweeper(10, 10, 0.5, "minemap.txt");

            //For testing, you may want to generate the map once, and save & load it again later 
            //Minesweeper m = new Minesweeper("minemap.txt");
            //m.printGameMap();
            // System.out.println("***** GAME STARTS *******");

            //m.printMineMap();
            if (m.solution())
            {
                // count winning rate
                count_win += 1;
            }
            else
            {
                return;
            }
            
            //boolean flag = m.openSquare(3, 8);
            //System.out.println(flag);
            //m.printGameMap();
        }
        System.out.println(count_win);
    }
}
