package test;

public class Word {
    private Tile[] tiles;
    private int row;
    private int col;
    private boolean vertical;
    private static final int BASE = 3;
    private static final int ROOT = 7;

    public Word(Tile[] tiles, int row, int col, boolean vertical) {
        this.tiles = tiles;
        this.row = row;
        this.col = col;
        this.vertical = vertical;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public boolean getVertical() {
        return vertical;
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
        Word otheWord = (Word) obj;
        if (vertical != otheWord.vertical || col != otheWord.col || row != otheWord.row
                || tiles.length != otheWord.getTiles().length) {
            return false;
        }
        for (int i = 0; i < tiles.length; i++) {
            if (!tiles[i].equals(otheWord.tiles[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = ROOT;
        result = BASE * result + row;
        result = BASE * result + col;
        result = BASE * result + (vertical ? 11 : 5);
        for (int i = 0; i < tiles.length; i++) {
            int tileHash;
            if (tiles[i] == null) {
                tileHash = 0;
            } else {
                tileHash = tiles[i].hashCode();
            }
            result = BASE * result + tileHash;
        }
        return result;
    }

}
