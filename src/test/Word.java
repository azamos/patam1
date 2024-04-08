package test;


public class Word {
    private Tile[] tiles;
    private int row;
    private int col;
    private boolean vertical;
    public Word(Tile[] tiles,int row,int col, boolean vertical){
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
    public boolean getVertical(){
        return vertical;
    }
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
    
}
