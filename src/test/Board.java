package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Board {
    private static Board singletonBoard = null;
    private static Tile[][] matrix;
    private static final int boardDimension = 15;
    private boolean emptyBoard = true;

    private Board() {
        matrix = new Tile[boardDimension][boardDimension];
    }

    public Board getBoard() {
        if (singletonBoard == null) {
            singletonBoard = new Board();
        }
        return singletonBoard;
    }

    public boolean boardLegal(Word word) {
        if (word.getVertical()) {
            if (word.getRow() + word.getTiles().length >= boardDimension) {
                return false;
            }
        } else {
            if (word.getCol() + word.getTiles().length >= boardDimension) {
                return false;
            }
        }
        
        /* got here -> word fits in board.
         * Now, to check if word is overwritting another word.
         */
        if(isOverwritting(word)){
            return false;
        }
        
        /* got here -> word fits in board, and is not overwritting another word.
         * Now, to check if word is adjacent to another word, or
         * if it is overlapping with another word.
         * However, if board is empty, need to check that the word
         * will cover the mid point (7,7).
         */
        if(emptyBoard){
            if(!isOnMidPoint(word)){
                return false;
            }
        }

        else{
            if(!isAdjacent(word) && !isOverlapping(word)){
                return false;
            }
        }
        /*got here -> word is either overlapping or adjacent
         * to another word, and is not overwritting.
         */

        return true;
    }

    private boolean isOnMidPoint(Word word){
        int midPoint = (int)(boardDimension/2);
        if(word.getVertical()){
            if(word.getCol()==midPoint 
            && word.getRow()<=midPoint 
            && word.getRow()+word.getTiles().length >= midPoint){
                return true;
            }
        }
        else{
            if(word.getRow()==midPoint 
            && word.getCol()<=midPoint 
            && word.getCol()+word.getTiles().length >= midPoint){
                return true;
            }
        }
        return false;
    }

    private boolean isOverwritting(Word word){
        if(word.getVertical()){
            for(int i=0; i < word.getTiles().length; i++){
                Tile occupier = matrix[word.getRow()+i][word.getCol()];
                if(!word.getTiles()[i].equals(occupier)){
                    return true;
                }
            }
        }
        else{
            for(int j=0; j < word.getTiles().length; j++){
                Tile occupier = matrix[word.getRow()][word.getCol()+j];
                if(!word.getTiles()[j].equals(occupier)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAdjacentLetter(int i, int j){
        if(i+1 < boardDimension && matrix[i+1][j]!=null){
            return true;
        }
        if(i-1 >= 0 && matrix[i-1][j]!=null){
            return true;
        }
        if(j+1 < boardDimension && matrix[i][j+1]!=null){
            return true;
        }
        if(j-1 >= 0 && matrix[i][j-1]!=null){
            return true;
        }
        return false;
    }

    private boolean isAdjacent(Word word){
        if(word.getVertical()){
            for(int i=0; i < word.getTiles().length;i++){
                if(isAdjacentLetter(word.getRow() + i, word.getCol())){
                    return true;
                }
            }
        }
        else{
            for(int j=0;j<word.getTiles().length;j++){
                if(isAdjacentLetter(word.getRow(),word.getCol() + j)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOverlapping(Word word) {
        if(word.getVertical()){
            for (int i = 0; i < word.getTiles().length; i++) {
                if (matrix[word.getRow() + i][word.getCol()] != null) {
                    return true;
                }
            }
        }
        else{
            for (int j = 0; j < word.getTiles().length; j++) {
                if (matrix[word.getRow()][word.getCol()+j] != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean dictionaryLegal(){
        return true;
    }

    private int getLeft(int row,int col){
        int left = col;
        col-=1;
        while(col >= 0 && matrix[row][col]!=null){
            left = col;
            col-=1;
        }
        return left;
    }

    private int getRight(int row,int col){
        int right = col;
        col+=1;
        while(col <= boardDimension && matrix[row][col]!=null){
            right = col;
            col+=1;
        }
        return right;
    }

    private Word getHorizontalWord(int left, int right, int row){
        Tile[] tiles = new Tile[right-left+1];
        for(int j = left; j<=right;j++){
            tiles[j-left] = matrix[row][j];
        }
        return new Word(tiles, row, left, false);
    }

    
    private ArrayList<Word> getHorizontalWords(Word verticalWord){
        ArrayList<Word> horizontalComplete = new ArrayList<Word>();
        int start = verticalWord.getRow();
        int n = verticalWord.getTiles().length;
        int end = start + n;
        for(int i = start; i < end; i++){
            int horizontalStart = getLeft(i,verticalWord.getCol());
            int horizontalEnd = getRight(i,verticalWord.getCol());
            Word horizontalWord = getHorizontalWord(horizontalStart,horizontalEnd, i);
            horizontalComplete.add(horizontalWord);
        }
        return horizontalComplete;
    }

    
    private int getTop(int row,int col){
        int top = row;
        row-=1;
        while(row >= 0 && matrix[row][col]!=null){
            top = row;
            row-=1;
        }
        return top;
    }

    private int getBottom(int row,int col){
        int bottom = row;
        row+=1;
        while(row <= boardDimension && matrix[row][col]!=null){
            bottom = row;
            row+=1;
        }
        return bottom;
    }

    private Word getVerticalWord(int top, int bottom, int col){
        Tile[] tiles = new Tile[bottom-top+1];
        for(int i = top; i<=bottom;i++){
            tiles[i-top] = matrix[i][col];
        }
        return new Word(tiles, top, col, true);
    }

    private ArrayList<Word> getVerticalWords(Word horizontalWord){
        ArrayList<Word> verticalComplete = new ArrayList<Word>();
        int start = horizontalWord.getCol();
        int n = horizontalWord.getTiles().length;
        int end = start + n;
        for(int j = start; j < end; j++){
            int verticalStart = getTop(horizontalWord.getRow(), j);
            int verticalEnd = getBottom(horizontalWord.getRow(), j);
            Word verticalWord = getVerticalWord(verticalStart, verticalEnd, j);
            verticalComplete.add(verticalWord);
        }
        return verticalComplete;
    }

    public ArrayList<Word> getWords(Word word){
        ArrayList<Word> newWords = new ArrayList<Word>();
        if(!word.getVertical()){
            newWords.addAll(getVerticalWords(word));
        }
        else{
            newWords.addAll(getHorizontalWords(word));
        }
        return newWords;
    }

    public int getScore(Word word){
        int score = 0;
        HashSet<MatrixCoordinate> doubleScoreIndexes = new HashSet<MatrixCoordinate>();
        MatrixCoordinate mc1 = new MatrixCoordinate(3,0);
        MatrixCoordinate mc2 = new MatrixCoordinate(6,2);
        MatrixCoordinate mc3 = new MatrixCoordinate(7,3);
        MatrixCoordinate mc4 = new MatrixCoordinate(6,6);
        doubleScoreIndexes.addAll(mc1.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc2.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc3.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc4.getSymmertyCoordinates());
        if(word.getVertical()){
            
        }
    }

    private class MatrixCoordinate{
        private int row;
        private int col;
        public static final int n = boardDimension-1;
        private static final int base = 31;
        private static final int rootPrime = 19;
        private MatrixCoordinate(int row,int col){
            this.row = row;
            this.col = col;
        }
        private HashSet<MatrixCoordinate> getSymmertyCoordinates(){
            HashSet<MatrixCoordinate> symmetrical = new HashSet<MatrixCoordinate>(8);
            symmetrical.add(this);
            symmetrical.add(getXsym());
            symmetrical.add(getYsym());
            symmetrical.add(getMainDiagSym());
            symmetrical.add(getSecDiagSym());
            symmetrical.add(getDiagSecDiagSym());
            symmetrical.add(getDiagXsym());
            symmetrical.add(getDiagYsym());
            return symmetrical;

        }
        private MatrixCoordinate getXsym(){
            return new MatrixCoordinate(n-row, col);
        }
        private MatrixCoordinate getYsym(){
            return new MatrixCoordinate(row, n-col);
        }
        private MatrixCoordinate getMainDiagSym(){
            return new MatrixCoordinate(col, row);
        }
        private MatrixCoordinate getSecDiagSym(){
            return new MatrixCoordinate(n-col, n-row);
        }
        private MatrixCoordinate getDiagYsym(){
            return getYsym().getMainDiagSym();
        }
        private MatrixCoordinate getDiagSecDiagSym(){
            return getSecDiagSym().getMainDiagSym();
        }
        private MatrixCoordinate getDiagXsym(){
            return getXsym().getMainDiagSym();
        }
        @Override
        public int hashCode() {
            int result = rootPrime;
            result = base*result + row;
            result = base*result + col;
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof MatrixCoordinate){
                MatrixCoordinate other = (MatrixCoordinate) obj;
                return this.col == other.col && this.row == other.row;
            }
            else{
                return false;
            }
        }
    }
    
}
