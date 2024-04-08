package test;

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
        /* TODO: finish later */
        return true;
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
}
