package test;

import java.util.ArrayList;
import java.util.HashSet;

public class Board {
    private static Board singletonBoard = null;
    private static Tile[][] matrix;
    private HashSet<Word> scoredWords = new HashSet<Word>();
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

    private void placeOnBoard(Word word) {
        /*
         * Assumes that prior checks have been made,
         * and word can indeed be placed in the specified coordinates
         * and in the specified direction.
         */
        Tile[] wordTiles = word.getTiles();
        int row = word.getRow();
        int col = word.getCol();
        if (word.getVertical()) {
            for (int i = 0; i < wordTiles.length; i++) {
                matrix[row + i][col] = wordTiles[i];
            }
        } else {
            for (int j = 0; j < wordTiles.length; j++) {
                matrix[row][col + j] = wordTiles[j];
            }
        }
    }

    private void placeWord(Word word) {
        boolean isVertical = word.getVertical();
        int row = word.getRow();
        int col = word.getCol();
        Tile[] tiles = word.getTiles();
        for (int i = 0; i < tiles.length; i++) {
            if (isVertical) {
                if (matrix[row + i][col] == null) {
                    matrix[row + i][col] = tiles[i];
                }
            } else {
                if (matrix[row][col + i] == null) {
                    matrix[row][col + i] = tiles[i];
                }
            }
        }
    }

    public int tryPlaceWord(Word word) {
        boolean allLegal = true;
        ArrayList<Word> newWords = null;
        boolean boardIsLegal = boardLegal(word);
        if (boardIsLegal) {
            newWords = getWords(word);
            for (Word newWord : newWords) {
                if (!dictionaryLegal(newWord)) {
                    allLegal = false;
                    break;
                }
            }
        }
        if (allLegal && newWords != null) {
            int score = 0;
            for (Word newWord : newWords) {
                score += getScore(newWord);
            }
            int res = getScore(word);
            if (emptyBoard) {
                emptyBoard = false;
            }
            res += score;
            for (Word newWord : newWords) {
                placeOnBoard(newWord);
            }
            placeWord(word);
            return res;
        } else {
            return 0;
        }
    }

    private void initiateBoardScores() {
        inititateDoubleLetters();
        inititateTripleLetters();
        inititateDoubleWords();
        inititateTripleWords();
    }

    private void inititateDoubleLetters() {
        MatrixCoordinate mc1 = new MatrixCoordinate(3, 0);
        MatrixCoordinate mc2 = new MatrixCoordinate(6, 2);
        MatrixCoordinate mc3 = new MatrixCoordinate(7, 3);
        MatrixCoordinate mc4 = new MatrixCoordinate(6, 6);
        doubleScoreIndexes.addAll(mc1.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc2.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc3.getSymmertyCoordinates());
        doubleScoreIndexes.addAll(mc4.getSymmertyCoordinates());
    }

    private void inititateTripleLetters() {
        MatrixCoordinate triple1 = new MatrixCoordinate(5, 1);
        MatrixCoordinate triple2 = new MatrixCoordinate(5, 5);
        tripleScoreIndexes.addAll(triple1.getSymmertyCoordinates());
        tripleScoreIndexes.addAll(triple2.getSymmertyCoordinates());
    }

    private void inititateTripleWords() {
        MatrixCoordinate tripleWordCoord1 = new MatrixCoordinate(0, 0);
        MatrixCoordinate tripleWordCoord2 = new MatrixCoordinate(7, 0);
        tripleScoreWordIndexes.addAll(tripleWordCoord1.getSymmertyCoordinates());
        tripleScoreWordIndexes.addAll(tripleWordCoord2.getSymmertyCoordinates());
    }

    private void inititateDoubleWords() {
        for (int k = 1; k < 5; k++) {
            MatrixCoordinate doubleWordIndex = new MatrixCoordinate(k, k);
            doubleScoreWordIndexes.addAll(doubleWordIndex.getSymmertyCoordinates());
        }
    }

    public boolean boardLegal(Word word) {
        int row = word.getRow();
        int col = word.getCol();
        Tile[] tiles = word.getTiles();
        if (row < 0 || row >= boardDimension || col < 0 || col >= boardDimension) {
            return false;
        }
        if (word.getVertical()) {
            if (row + tiles.length >= boardDimension) {
                return false;
            }
        } else {
            if (col + tiles.length >= boardDimension) {
                return false;
            }
        }

        /*
         * got here -> word fits in board.
         * Now, to check if word is overwritting another word.
         */
        if (isOverwritting(word)) {
            return false;
        }

        /*
         * got here -> word fits in board, and is not overwritting another word.
         * Now, to check if word is adjacent to another word, or
         * if it is overlapping with another word.
         * However, if board is empty, need to check that the word
         * will cover the mid point (7,7).
         */
        if (emptyBoard) {
            return isOnMidPoint(word);
        }

        else {
            return isAdjacent(word) || isOverlapping(word);
        }
    }

    private boolean isOnMidPoint(Word word) {
        int midPoint = (int) (boardDimension / 2);
        int row = word.getRow();
        int col = word.getCol();
        Tile[] tiles = word.getTiles();
        boolean isVertical = word.getVertical();
        if (isVertical) {
            if (col == midPoint
                    && row <= midPoint
                    && row + tiles.length >= midPoint) {
                return true;
            }
        } else {
            if (row == midPoint
                    && col <= midPoint
                    && col + tiles.length >= midPoint) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverwritting(Word word) {
        Tile[] tiles = word.getTiles();
        int row = word.getRow();
        int col = word.getCol();
        boolean isVertical = word.getVertical();
        if (isVertical) {
            for (int i = 0; i < tiles.length; i++) {
                Tile occupier = matrix[row + i][col];
                Tile newTile = tiles[i];
                if (occupier != null && newTile != null && !newTile.equals(occupier)) {
                    return true;
                }
            }
        } else {
            for (int j = 0; j < tiles.length; j++) {
                Tile occupier = matrix[row][col + j];
                Tile newTile = tiles[j];
                if (occupier != null && newTile != null && !newTile.equals(occupier)) {
                    return true;
                }
            }
        }
        return false;
    }

    // private boolean isAdjacentLetter(int i, int j) {
    // if (i + 1 < boardDimension && matrix[i + 1][j] != null) {
    // return true;
    // }
    // if (i - 1 >= 0 && matrix[i - 1][j] != null) {
    // return true;
    // }
    // if (j + 1 < boardDimension && matrix[i][j + 1] != null) {
    // return true;
    // }
    // if (j - 1 >= 0 && matrix[i][j - 1] != null) {
    // return true;
    // }
    // return false;
    // }

    private boolean isAdjacent(Word word) {
        boolean isVertical = word.getVertical();
        Tile[] tiles = word.getTiles();
        int row = word.getRow();
        int col = word.getCol();
        int length = tiles.length;
        if (isVertical) {
            boolean adjTop = row > 0 && matrix[row - 1][col] != null;
            boolean adjBottom = row + length < boardDimension
                    && matrix[row + length][col] != null;
            if (adjTop || adjBottom) {
                return true;
            }
            for (int i = 0; i < tiles.length; i++) {
                boolean adjLeft = col > 0 && matrix[row + i][col - 1] != null;
                boolean adjRight = col + 1 < boardDimension && matrix[row + i][col + 1] != null;
                if (adjLeft || adjRight) {
                    return true;
                }
            }
        } else {
            boolean adjLeft = col > 0 && matrix[row][col - 1] != null;
            boolean adjRight = col + length < boardDimension
                    && matrix[row][col + length] != null;
            if (adjLeft || adjRight) {
                return true;
            }
            for (int j = 0; j < tiles.length; j++) {
                boolean adjTop = row > 0 && matrix[row - 1][col + j] != null;
                boolean adjBottom = row + 1 < boardDimension && matrix[row + 1][col + j] != null;
                if (adjTop || adjBottom) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOverlapping(Word word) {
        boolean isVertical = word.getVertical();
        Tile[] tiles = word.getTiles();
        int row = word.getRow();
        int col = word.getCol();
        if (isVertical) {
            for (int i = 0; i < tiles.length; i++) {
                if (matrix[row + i][col] != null) {
                    return true;
                }
            }
        } else {
            for (int j = 0; j < tiles.length; j++) {
                if (matrix[row][col + j] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean dictionaryLegal(Word word) {
        return true;
    }

    private int getLeft(int row, int col) {
        int left = col;
        col -= 1;
        while (col >= 0 && matrix[row][col] != null) {
            left = col;
            col -= 1;
        }
        return left;
    }

    private int getRight(int row, int col) {
        int right = col;
        col += 1;
        while (col <= boardDimension && matrix[row][col] != null) {
            right = col;
            col += 1;
        }
        return right;
    }

    private Word getHorizontalWord(int left, int right, int row) {
        Tile[] tiles = new Tile[right - left + 1];
        for (int j = left; j <= right; j++) {
            tiles[j - left] = matrix[row][j];
        }
        return new Word(tiles, row, left, false);
    }

    private ArrayList<Word> getHorizontalWords(Word verticalWord) {
        ArrayList<Word> horizontalComplete = new ArrayList<Word>();
        int start = verticalWord.getRow();
        int n = verticalWord.getTiles().length;
        int end = start + n;
        int col = verticalWord.getCol();
        for (int i = start; i < end; i++) {
            int horizontalStart = getLeft(i, col);
            int horizontalEnd = getRight(i, col);
            if (horizontalEnd > horizontalStart && matrix[i][col] == null) {
                Word horizontalWord = getHorizontalWord(horizontalStart, horizontalEnd, i);
                horizontalComplete.add(horizontalWord);
            }
        }
        return horizontalComplete;
    }

    private int getTop(int row, int col) {
        int top = row;
        row -= 1;
        while (row >= 0 && matrix[row][col] != null) {
            top = row;
            row -= 1;
        }
        return top;
    }

    private int getBottom(int row, int col) {
        int bottom = row;
        row += 1;
        while (row <= boardDimension && matrix[row][col] != null) {
            bottom = row;
            row += 1;
        }
        return bottom;
    }

    private Word getVerticalWord(int top, int bottom, int col, Tile replacement) {
        Tile[] tiles = new Tile[bottom - top + 1];
        for (int i = top; i <= bottom; i++) {
            if (matrix[i][col] == null) {
                tiles[i - top] = replacement;
            } else {
                tiles[i - top] = matrix[i][col];
            }
        }
        return new Word(tiles, top, col, true);
    }

    private ArrayList<Word> getVerticalWords(Word horizontalWord) {
        ArrayList<Word> verticalComplete = new ArrayList<Word>();
        int col = horizontalWord.getCol();
        int start = col;
        int n = horizontalWord.getTiles().length;
        int end = start + n;
        int row = horizontalWord.getRow();
        Tile[] tiles = horizontalWord.getTiles();
        for (int j = start; j < end; j++) {
            int verticalStart = getTop(row, j);
            int verticalEnd = getBottom(row, j);
            Tile currTile = tiles[j - col];
            if (verticalStart < verticalEnd && (matrix[row][j] != null || currTile != null)) {
                Word verticalWord = getVerticalWord(verticalStart, verticalEnd, j, currTile);
                verticalComplete.add(verticalWord);
            }
        }
        return verticalComplete;
    }

    public ArrayList<Word> getWords(Word word) {
        ArrayList<Word> newWords = new ArrayList<Word>();
        boolean isVertical = word.getVertical();
        if (!isVertical) {
            newWords.addAll(getVerticalWords(word));
        } else {
            newWords.addAll(getHorizontalWords(word));
        }
        return newWords;
    }

    public int getScore(Word word) {
        int score = 0;
        Tile[] wordTiles = word.getTiles();
        int row = word.getRow();
        int col = word.getCol();
        if (word.getVertical()) {
            for (int i = 0; i < wordTiles.length; i++) {
                MatrixCoordinate curr = new MatrixCoordinate(row + i, col);
                Tile currTile = wordTiles[i];
                int baseLetterScore = 0;
                if (currTile == null && matrix[curr.row][curr.col] != null) {
                    baseLetterScore = matrix[curr.row][curr.col].score;
                }
                if (currTile != null) {
                    baseLetterScore = currTile.score;
                }
                if (doubleScoreIndexes.contains(curr)) {
                    score += 2 * baseLetterScore;
                } else {
                    if (tripleScoreIndexes.contains(curr)) {
                        score += 3 * baseLetterScore;
                    } else {
                        score += baseLetterScore;
                    }
                }

            }
        } else {
            for (int j = 0; j < wordTiles.length; j++) {
                MatrixCoordinate curr = new MatrixCoordinate(row, col + j);
                Tile currTile = wordTiles[j];
                int baseLetterScore = 0;
                if (currTile == null && matrix[curr.row][curr.col] != null) {
                    baseLetterScore = matrix[curr.row][curr.col].score;
                }
                if (currTile != null) {
                    baseLetterScore = currTile.score;
                }
                if (doubleScoreIndexes.contains(curr)) {
                    score += 2 * baseLetterScore;
                } else {
                    if (tripleScoreIndexes.contains(curr)) {
                        score += 3 * baseLetterScore;
                    } else {
                        score += baseLetterScore;
                    }
                }

            }
        }

        for (MatrixCoordinate mc : doubleScoreWordIndexes) {
            if (mc.isWithinWord(word)) {
                score *= 2;
            }
        }
        for (MatrixCoordinate mc : tripleScoreWordIndexes) {
            if (mc.isWithinWord(word)) {
                score *= 3;
            }
        }
        if (emptyBoard && new MatrixCoordinate(7, 7).isWithinWord(word)) {
            score *= 2;
        }
        return score;
    }

    private class MatrixCoordinate {
        private int row;
        private int col;
        public static final int n = boardDimension - 1;
        private static final int base = 31;
        private static final int rootPrime = 19;

        private MatrixCoordinate(int row, int col) {
            this.row = row;
            this.col = col;
        }

        private boolean isWithinWord(Word word) {
            int n = word.getTiles().length;
            int wordRow = word.getRow();
            int wordCol = word.getCol();
            if (word.getVertical()) {
                if (col == wordCol
                        && row >= wordRow
                        && row < wordRow + n) {
                    return true;
                }
            } else {
                if (row == wordRow
                        && col >= wordCol
                        && col < wordCol + n) {
                    return true;
                }
            }
            return false;
        }

        private HashSet<MatrixCoordinate> getSymmertyCoordinates() {
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

        private MatrixCoordinate getXsym() {
            return new MatrixCoordinate(n - row, col);
        }

        private MatrixCoordinate getYsym() {
            return new MatrixCoordinate(row, n - col);
        }

        private MatrixCoordinate getMainDiagSym() {
            return new MatrixCoordinate(col, row);
        }

        private MatrixCoordinate getSecDiagSym() {
            return new MatrixCoordinate(n - col, n - row);
        }

        private MatrixCoordinate getDiagYsym() {
            return getYsym().getMainDiagSym();
        }

        private MatrixCoordinate getDiagSecDiagSym() {
            return getSecDiagSym().getMainDiagSym();
        }

        private MatrixCoordinate getDiagXsym() {
            return getXsym().getMainDiagSym();
        }

        @Override
        public int hashCode() {
            int result = rootPrime;
            result = base * result + row;
            result = base * result + col;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MatrixCoordinate) {
                MatrixCoordinate other = (MatrixCoordinate) obj;
                return this.col == other.col && this.row == other.row;
            } else {
                return false;
            }
        }
    }

}
