package test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Tile {
    public final char letter;
    public final int score;
    private static final int BASE = 31;
    private static final int ROOT = 19;

    private Tile(char letter, int score) {
        this.letter = letter;
        this.score = score;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Tile otherTile = (Tile) obj;
        return letter == otherTile.letter && score == otherTile.score;
    }

    @Override
    public int hashCode() {
        int result = ROOT;
        result = BASE * result + (letter - 'A');
        result = BASE * result + score;
        return result;
    }

    /**
     * Bag
     */
    public static class Bag {
        private int[] amounts;
        private Tile[] tiles;
        private static final int abcLength = 26;
        private int toalTiles;
        private static final int maxTiles = 98;
        private Set<Integer> availableTiles;
        private static final char offset = 'A';
        private static Bag singletonBag = null;

        private Bag() {
            this.amounts = new int[abcLength];
            this.init_amounts();
            this.tiles = new Tile[abcLength];
            this.init_tiles();
            this.toalTiles = maxTiles;
            this.initAvailable();
        }

        public static Bag getBag() {
            if (singletonBag == null) {
                singletonBag = new Bag();
            }
            return singletonBag;
        }

        public int[] getQuantities() {
            return this.amounts.clone();
        }

        public int size() {
            return this.toalTiles;
        }

        public void put(Tile t) {
            if (this.toalTiles < maxTiles) {
                int i = getCharIndex(t.letter);
                if (this.amounts[i] == 0) {
                    this.availableTiles.add(i);
                }
                this.amounts[i]++;
                this.toalTiles++;
            }
        }

        private void decreaseAmount(int index) {
            this.amounts[index] -= 1;
            if (this.amounts[index] == 0) {
                this.availableTiles.remove(index);
            }
            this.toalTiles -= 1;
        }

        public Tile getTile(char c) {
            int i = getCharIndex(c);
            if (i > 26 || i < 0) {
                return null;
            }
            if (this.amounts[i] == 0) {
                return null;
            } else {
                Tile requestedTile = this.tiles[i];
                this.decreaseAmount(i);
                return requestedTile;
            }
        }

        public Tile getRand() {
            if (this.availableTiles.isEmpty()) {
                return null;
            }
            Integer[] indexes = this.availableTiles.toArray(new Integer[0]);
            Random rand = new Random();
            int indexesListRandIndex = rand.nextInt(indexes.length);
            int randIndex = indexes[indexesListRandIndex];
            Tile randomTile = this.tiles[randIndex];
            this.decreaseAmount(randIndex);
            return randomTile;
        }

        private void initAvailable() {
            this.availableTiles = new HashSet<>(32);
            for (int i = 0; i < 26; i++) {
                this.availableTiles.add(i);
            }
        }

        private int getCharIndex(char c) {
            return c - offset;
        }

        private void init_tiles() {
            for (char c = 'A'; c <= 'Z'; c++) {
                int i = getCharIndex(c);
                int score = 0;
                if (c == 'B' || c == 'C' || c == 'M' || c == 'P') {
                    score = 3;
                }
                if (c == 'D' || c == 'G') {
                    score = 2;
                }
                if (c == 'F' || c == 'H' || c == 'V' || c == 'W' || c == 'Y') {
                    score = 4;
                }
                if (c == 'J' || c == 'X') {
                    score = 8;
                }
                if (c == 'K') {
                    score = 5;
                }
                if (c == 'Q' || c == 'Z') {
                    score = 10;
                }
                if (c == 'A' || c == 'E' || c == 'I' || c == 'L' || c == 'N' || c == 'O' || c == 'R' || c == 'S'
                        || c == 'T' || c == 'U') {
                    score = 1;
                }
                tiles[i] = new Tile(c, score);
            }
        }

        private void init_amounts() {
            for (char c = 'A'; c <= 'Z'; c++) {
                int i = getCharIndex(c);
                if (c == 'A' || c == 'I') {
                    amounts[i] = 9;
                }
                if (c == 'D' || c == 'L' || c == 'S' || c == 'U') {
                    amounts[i] = 4;
                }
                if (c == 'E') {
                    amounts[i] = 12;
                }
                if (c == 'G') {
                    amounts[i] = 3;
                }
                if (c == 'J' || c == 'K' || c == 'Q' || c == 'X' || c == 'Z') {
                    amounts[i] = 1;
                }
                if (c == 'N' || c == 'R' || c == 'T') {
                    amounts[i] = 6;
                }
                if (c == 'O') {
                    amounts[i] = 8;
                }
                if (c == 'B' || c == 'C' || c == 'F' || c == 'H' || c == 'M' || c == 'P' || c == 'V' || c == 'W'
                        || c == 'Y') {
                    amounts[i] = 2;
                }
            }
        }

    }
}
