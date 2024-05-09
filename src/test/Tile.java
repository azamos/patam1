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
                int score;
                switch (c) {
                    case 'B', 'C', 'M', 'P':
                        score = 3;
                        break;

                    case 'D', 'G':
                        score = 2;
                        break;

                    case 'F', 'H', 'V', 'W', 'Y':
                        score = 4;
                        break;

                    case 'J', 'X':
                        score = 8;
                        break;

                    case 'K':
                        score = 5;
                        break;

                    case 'Q', 'Z':
                        score = 10;
                        break;

                    default:
                        score = 1;
                        break;
                }

                this.tiles[i] = new Tile(c, score);
            }
        }

        private void init_amounts() {
            for (char c = 'A'; c <= 'Z'; c++) {
                int i = getCharIndex(c);
                switch (c) {
                    case 'A', 'I':
                        this.amounts[i] = 9;
                        break;
                    case 'D', 'L', 'S', 'U':
                        this.amounts[i] = 4;
                        break;
                    case 'E':
                        this.amounts[i] = 12;
                        break;
                    case 'G':
                        this.amounts[i] = 3;
                        break;
                    case 'J', 'K', 'Q', 'X', 'Z':
                        this.amounts[i] = 1;
                        break;
                    case 'N', 'R', 'T':
                        this.amounts[i] = 6;
                        break;
                    case 'O':
                        this.amounts[i] = 8;
                        break;
                    default:
                        this.amounts[i] = 2;
                        break;
                }
            }
        }

    }
}
