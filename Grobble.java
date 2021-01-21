import java.util.Scanner;
import java.io.FileReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.List;
import java.util.Iterator;
import java.util.TreeSet;

/**
*Grant Martin's Boggle word search game.
*
*@author Grant Martin
*@version 11/21/2019
*
*/


public class Grobble implements WordSearchGame {
   private int height;
   private int width;
   private TreeSet<String> lexicon;
   private String[][] board;
   private boolean[][] visited;
   private boolean loadLexicon;
   private final int MAX_NEIGHBORS = 8;
   private ArrayList<Integer> path; //tracking int path of words
   private String foundWords; //tracking words found 
   private SortedSet<String> words; //set holding all words found
 
   
   //default board instantiated.   
   public Grobble() {
      lexicon = new TreeSet<String>();
      path = new ArrayList<Integer>();
   }
   
    /**
    * Loads the lexicon into a data structure for later use. 
    * 
    * @param fileName A string containing the name of the file to be opened.
    * @throws IllegalArgumentException if fileName is null
    * @throws IllegalArgumentException if fileName cannot be opened.
    */
   public void loadLexicon(String fileName) {
      
      try {
         Scanner fileScan;
         Scanner rowScan;
         String row;
         
         if (fileName == null) {
            throw new IllegalArgumentException();
         }
         
         fileScan = new Scanner(new FileReader(fileName));
         while (fileScan.hasNext()) {
            row = fileScan.nextLine(); //scanning/storing each line for characters
            rowScan = new Scanner(row); //scanning the row for chars
            rowScan.useDelimiter(" "); //parsing by delimiter
            while (rowScan.hasNext()) {
               lexicon.add(rowScan.next().toUpperCase()); //adds chars, turns to lower case and stores in lexicon
               
            }
            
         }
        
      }
      
      catch (Exception e) {
         throw new IllegalArgumentException();
      }
      
      loadLexicon = true;
   }
   

    /**
    * Stores the incoming array of Strings in a data structure that will make
    * it convenient to find words.
    * 
    * @param letterArray This array of length N^2 stores the contents of the
    *     game board in row-major order. Thus, index 0 stores the contents of board
    *     position (0,0) and index length-1 stores the contents of board position
    *     (N-1,N-1). Note that the board must be square and that the strings inside
    *     may be longer than one character.
    * @throws IllegalArgumentException if letterArray is null, or is  not
    *     square.
    */
   public void setBoard(String[] letterArray) {
   
      if (letterArray == null) {
         throw new IllegalArgumentException();
      }
      
      for(int row = 0; row < letterArray.length; row++) {
      
      }
      
      //if dimension isnt square, throw exception
      double square = (int) Math.sqrt(letterArray.length);  
      if (Math.pow(square, square) != letterArray.length) {             
         throw new IllegalArgumentException();
      }
      
        
      
      else {
         
         board = new String[width][height];
         int pointer = 0;
         //two loops for length and height
         for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
               board[i][j] = letterArray[pointer]; //populating the board
               pointer++;
            }
         } 
      
      }
   
   
   }
   
   //checks if setBoard is square
   public boolean isSquare() {
     
      if (width == height) {
         return true;
      }
      
      return false;
      
   }
   
   
   /**
    * Creates a String representation of the board, suitable for printing to
    *   standard out. Note that this method can always be called since
    *   implementing classes should have a default board.
    */
   public String getBoard() {
      
      String boardPrint = "";
      
      //loops through 2d array, printing out the board.
      for (int length = 0; length < board.length; length++) {
         for (int height = 0; height < board[length].length; height++) {
            boardPrint += board[length][height];
         }
      }
   
      return boardPrint;
   }
   
   
    /**
    * Retrieves all valid words on the game board, according to the stated game
    * rules.
    * 
    * @param minimumWordLength The minimum allowed length (i.e., number of
    *     characters) for any word found on the board.
    * @return java.util.SortedSet which contains all the words of minimum length
    *     found on the game board and in the lexicon.
    * @throws IllegalArgumentException if minimumWordLength < 1
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public SortedSet<String> getAllValidWords(int minimumWordLength) {
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException();
      }
      
      if (!loadLexicon) {
         throw new IllegalStateException();
      }
     
      foundWords = "";
      
      //need to start a x,y position, check letter and compare against prefixes. 
      //if match is found, DFS further to match against lexicon. Store words in an array. return array.
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
         
            if (isValidPrefix(foundWords) && foundWords.length() <= minimumWordLength && lexicon.contains(foundWords)) {
               words.add(foundWords);
            }
            validWordsDFS(minimumWordLength, i, j); //run through the dfs
         
         }
      } 
      
      return words;
   }
   
   //getAllValidWords dfs
   //WHY IS THIS NOT WORKING
   public void validWordsDFS(int minimumWordLength, int x, int y) {
      Position nStart = new Position(x,y); //create starting position 
      for (Position p: nStart.neighbors()) {
         if (!isVisited(p)) {
            if (isValidPrefix(foundWords + board[p.x][p.y])) { //checks if the prefix + current position on board == part of lexicon word
               foundWords += board[p.x][p.y]; //adds next letter to string
               if (lexicon.contains(foundWords)) { //if the found word equals a valid word in lexicon
                  words.add(foundWords); //add to array
                  String foundWords = ""; //reset foundWords string to find next word.
               }
            }
            validWordsDFS(minimumWordLength, p.x, p.y);
         }
      }
   
   }


   /**
   * Computes the cummulative score for the scorable words in the given set.
   * To be scorable, a word must (1) have at least the minimum number of characters,
   * (2) be in the lexicon, and (3) be on the board. Each scorable word is
   * awarded one point for the minimum number of characters, and one point for 
   * each character beyond the minimum number.
   *
   * @param words The set of words that are to be scored.
   * @param minimumWordLength The minimum number of characters required per word
   * @return the cummulative score of all scorable words in the set
   * @throws IllegalArgumentException if minimumWordLength < 1
   * @throws IllegalStateException if loadLexicon has not been called.
   */  
   public int getScoreForWords(SortedSet<String> words, int minimumWordLength) {
   
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException();
      }
   
      if (!loadLexicon) {
         throw new IllegalStateException();
      }
      int score = 0;
      
      Iterator<String> iterator = words.iterator();
      String wordItr = iterator.next();
      //counts length of word, subtracts from minWordLength to get past threshold IFF word length < minLength
      while (iterator.hasNext()) {
         if (wordItr.length() > minimumWordLength) {
            score += wordItr.length() - minimumWordLength;
         }
      }
   
   
      return score;
   }
   
   
   /**
    * Determines if the given word is in the lexicon.
    * 
    * @param wordToCheck The word to validate
    * @return true if wordToCheck appears in lexicon, false otherwise.
    * @throws IllegalArgumentException if wordToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public boolean isValidWord(String wordToCheck) {
      if (wordToCheck == null) {
         throw new IllegalArgumentException();
      }
      
      if (!loadLexicon) {
         throw new IllegalStateException();
      }
      
      return lexicon.contains(wordToCheck.toUpperCase());
   }
   
   
   /**
    * Determines if there is at least one word in the lexicon with the 
    * given prefix.
    * 
    * @param prefixToCheck The prefix to validate
    * @return true if prefixToCheck appears in lexicon, false otherwise.
    * @throws IllegalArgumentException if prefixToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public boolean isValidPrefix(String prefixToCheck) {
   
      if (prefixToCheck == null) {
         throw new IllegalArgumentException();
      }
      
      if (!loadLexicon) {
         throw new IllegalStateException();
      }
   
      return lexicon.ceiling(prefixToCheck.toUpperCase()).startsWith(prefixToCheck.toUpperCase());
   }
   
   
   /**
    * Determines if the given word is in on the game board. If so, it returns
    * the path that makes up the word.
    * @param wordToCheck The word to validate
    * @return java.util.List containing java.lang.Integer objects with  the path
    *     that makes up the word on the game board. If word is not on the game
    *     board, return an empty list. Positions on the board are numbered from zero
    *     top to bottom, left to right (i.e., in row-major order). Thus, on an NxN
    *     board, the upper left position is numbered 0 and the lower right position
    *     is numbered N^2 - 1.
    * @throws IllegalArgumentException if wordToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */

   public List<Integer> isOnBoard(String wordToCheck) {
   
      if (wordToCheck == null) {
         throw new IllegalArgumentException();
      }
      
      if (!loadLexicon) {
         throw new IllegalStateException();
      }
      
   //need to find path Array<Integer> for given word. scan board x,y for first letter.
   //if letter == found, perform dfs to find matching second letter, add to array, perform another scan, etc.
   //return true if word found && return path Array<Integer>
   
      path = new ArrayList<Integer>(); //stores the int path of the search
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
            
            //checking for same characters between passed in vs x,y of board.
            if (board[i][j].charAt(0) == wordToCheck.charAt(0)) {
                //need to conduct DFS to find valid words on board, add to path, then add to foundWords
               foundWords = board[i][j];
               depthFirstSearch(wordToCheck, i, j); //recursively run through DFS
               
            }
         
         }
      }
   
   
      return path;
   }

   //needs to search the board for word on board
   //adapt lecture algorithm??
   private void depthFirstSearch(String wordToCheck, int x, int y) {
      
      Position nStart = new Position(x,y); //create starting position 
      int i = 0;
      for (Position p: nStart.neighbors()) {
         if (!isVisited(p)) {
            visit(p);
            i++;
            //check to compare wordSoFar == board[x][y]
            if (wordToCheck == foundWords + board[p.x][p.y]) { //how to check if == ??
               foundWords += board[p.x][p.y];
               depthFirstSearch(wordToCheck, p.x, p.y);
               if (foundWords == wordToCheck) {
                  return;
               }
            }
         }
      }
   
   }
   

   //marks this valid position as having been seen
   private void visit(Position p) {
      visited[p.x][p.y] = true;
   }
   //has this valid position been visited?   
   private boolean isVisited(Position p) {
      return visited[p.x][p.y];
   }



   /**
   *from lecture notes - HOW DO I IMPLEMENT????
   */
   
   //modelling an x,y position on the grid
   private class Position {
   
      int x;
      int y;
      
      //constructs a position with corrdinates (x,y)
      public Position(int x, int y) {
         this.x = x;
         this.y = y;
      }
   
      //returns a string representation of this position
      public String toString() {
         return "(" + x + ", " + y + ")";
      }
   
      //is the position valid in the search area
      private boolean isValid(Position p) {
         return (p.x >= 0) && (p.x < width) &&
            (p.y >= 0) && (p.y < height);
      }
   
      
      
      //returns all neighbors of this position
      public Position[] neighbors() {
         Position[] nbrs = new Position[MAX_NEIGHBORS];
         int count = 0;
         Position p;
         //generate all eight neighbot positions
         //add to return value if valid
         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               if (!((i == 0) && (j == 0))) {
                  p = new Position(x + i, y + j);
                  if (isValid(p)) {
                     nbrs[count++] = p;
                  }
               }
            }
         }
         return Arrays.copyOf(nbrs, count);
      }
   
   
   }






}