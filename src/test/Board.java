package test;

import java.util.ArrayList;
import java.util.HashSet;
// import java.util.Set;

public class Board {
    private static Board singletonBoard = null;
    private static Tile[][] matrix;
    private static final int boardDimension = 15;
    private boolean emptyBoard = true;
    private HashSet<MatrixCoordinate> doubleScoreIndexes = new HashSet<MatrixCoordinate>();
    private HashSet<MatrixCoordinate> tripleScoreIndexes = new HashSet<MatrixCoordinate>();
    private HashSet<MatrixCoordinate> tripleScoreWordIndexes = new HashSet<MatrixCoordinate>();
    private HashSet<MatrixCoordinate> doubleScoreWordIndexes = new HashSet<MatrixCoordinate>();
    
    private Board() {
        matrix = new Tile[boardDimension][boardDimension];
        initiateBoardScores();
    }

    public static Board getBoard() {
        if (singletonBoard == null) {
            singletonBoard = new Board();
        }
        return singletonBoard;
    }

    private void placeOnBoard(Word word){
        /*Assumes that prior checks have been made,
         * and word can indeed be placed in the specified coordinates
         * and in the specified direction.
         */
        Tile[] wordTiles = word.getTiles();
        int row = word.getRow();
        int col = word.getCol();
        if(word.getVertical()){
            for(int i =0;i<wordTiles.length;i++){
                matrix[row+i][col] = wordTiles[i];
            }
        }
        else{
            for(int j=0; j< wordTiles.length; j++){
                matrix[row][col+j] = wordTiles[j];
            }
        }
    }

    public int tryPlaceWord(Word word){
        boolean allLegal = true;
        ArrayList<Word> newWords = null;
        if(boardLegal(word)){
            newWords = getWords(word);
            for (Word newWord : newWords) {
                if(!dictionaryLegal(newWord)){
                    allLegal = false;
                    break;
                }
            }
        }
        if(allLegal && newWords!=null){
            int score = 0;
            for (Word newWord : newWords) {
                score += getScore(newWord);
                placeOnBoard(word);
            }
            return score;
        }
        else{
            return 0;
        }
    }

    private void initiateBoardScores(){
        inititateDoubleLetters();
        inititateTripleLetters();
        inititateDoubleWords();
        inititateTripleWords();
    }

    // private void inititateScoresSet(HashSet<MatrixCoordinate> scoreSet,
    // HashSet<MatrixCoordinate> seedCoordinates){
    //     for (MatrixCoordinate matrixCoordinate : seedCoordinates) {
    //         scoreSet.addAll(matrixCoordinate.getSymmertyCoordinates());
    //     }
    // }

    private void inititateDoubleLetters(){
        MatrixCoordinate mc1 = new MatrixCoordinate(3,0);
        MatrixCoordinate mc2 = new MatrixCoordinate(6,2);
        MatrixCoordinate mc3 = new MatrixCoordinate(7,3);
        MatrixCoordinate mc4 = new MatrixCoordinate(6,6);
        doubleScoreIndexes.addAll(mc1.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc2.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc3.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc4.getSymmertyCoordinates());
    }

    private void inititateTripleLetters(){
        MatrixCoordinate triple1 = new MatrixCoordinate(5, 1);
        MatrixCoordinate triple2 = new MatrixCoordinate(5, 5);
        tripleScoreIndexes.addAll(triple1.getSymmertyCoordinates());
        tripleScoreIndexes.addAll(triple2.getSymmertyCoordinates());
    }

    private void inititateTripleWords(){
        MatrixCoordinate tripleWordCoord1 = new MatrixCoordinate(0, 0);
        MatrixCoordinate tripleWordCoord2 = new MatrixCoordinate(7, 0);
        tripleScoreWordIndexes.addAll(tripleWordCoord1.getSymmertyCoordinates());
        tripleScoreWordIndexes.addAll(tripleWordCoord2.getSymmertyCoordinates());
    }

    private void inititateDoubleWords(){
        for(int k=1;k<5;k++){
            MatrixCoordinate doubleWordIndex = new MatrixCoordinate(k, k);
            doubleScoreWordIndexes.addAll(doubleWordIndex.getSymmertyCoordinates());
        }
    }

    public boolean boardLegal(Word word) {
        int row = word.getRow();
        int col = word.getCol();
        if(row<0 || row >= boardDimension || col < 0 || col >= boardDimension){
            return false;
        }
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
        Tile[] tiles =  word.getTiles();
        if(word.getVertical()){
            for(int i=0; i < tiles.length; i++){
                Tile occupier = matrix[word.getRow()+i][word.getCol()];
                if(!tiles[i].equals(occupier)){
                    return true;
                }
            }
        }
        else{
            for(int j=0; j < tiles.length; j++){
                Tile occupier = matrix[word.getRow()][word.getCol()+j];
                if(tiles[j]!=null&&!tiles[j].equals(occupier)){
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
    
    public boolean dictionaryLegal(Word word){
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
        Tile[] wordTiles = word.getTiles();
        int row = word.getRow();
        int col = word.getCol();
        if(word.getVertical()){
            for(int i =0; i < wordTiles.length; i++ ){
                MatrixCoordinate curr = new MatrixCoordinate(i, col);
                Tile currTile = wordTiles[i];
                if(doubleScoreIndexes.contains(curr)){
                    score += 2 * currTile.score;
                }
                else{
                    if (tripleScoreIndexes.contains(curr)){
                        score += 3 * currTile.score;
                    }
                    else{
                        score += currTile.score;
                    }
                }
            }
        }
        else{
            for(int j =0; j < wordTiles.length; j++ ){
                MatrixCoordinate curr = new MatrixCoordinate(row, j);
                Tile currTile = wordTiles[j];
                if(doubleScoreIndexes.contains(curr)){
                    score += 2 * currTile.score;
                }
                else{
                    if (tripleScoreIndexes.contains(curr)){
                        score += 3 * currTile.score;
                    }
                    else{
                        score += currTile.score;
                    }
                }
            }
        }
        /*Now that we have the total letters score,
         * It is time to check if word sits on any
         *  WORD MULTIPLYING indexes.
         */

         for (MatrixCoordinate mc : doubleScoreWordIndexes) {
            if(mc.isWithinWord(word)){
                score *= 2;
            }
         }
        
         for (MatrixCoordinate mc : tripleScoreWordIndexes) {
            if(mc.isWithinWord(word)){
                score *= 3;
            }
         }
        
        if(emptyBoard && new MatrixCoordinate(7, 7).isWithinWord(word)){
            score*=2;
        }
         return score;
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
        private boolean isWithinWord(Word word){
            int n = word.getTiles().length;
            int wordRow = word.getRow();
            int wordCol = word.getCol();
            if(word.getVertical()){
                if(col==wordCol
                && row >= wordRow
                && row < wordRow + n){
                        return true;
                    }
            }
            else{
                if(row==wordRow
                && col >= wordCol
                && col < wordCol + n){
                    return true;
                }
            }
            return false;
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
