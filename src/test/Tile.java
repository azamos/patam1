package test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Tile {
	public final char letter;
	public final int score;

	private Tile(char letter, int score) {
		this.letter = letter;
		this.score = score;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	/**
	 * Bag
	 */
	public static class Bag {
		private int[] amounts;
		private Tile[] tiles;
		private static final int abcLength = 26;
		private int toalTiles;
		private final int maxTiles = 98;
		private Set<Integer> availableTiles;

		public Bag() {
			this.amounts = new int[abcLength];
			this.init_amounts();
			this.tiles = new Tile[abcLength];
			this.init_tiles();
			this.toalTiles = maxTiles;
			this.initAvailable();
		}

		private boolean isEmpty() {
			return this.toalTiles == 0;
		}

		private void decreaseAmount(int index) {
			this.amounts[index] -= 1;
			if (this.amounts[index] == 0) {
				this.availableTiles.remove(index);
			}
			this.toalTiles -= 1;
		}

		public Tile getRand() {
			if(this.availableTiles.isEmpty()){
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

		private void init_tiles() {
			for (char c = 'A'; c <= 'Z'; c++) {
				int i = c - 'A';
				int score;
				switch (c) {
					// case 'A','E','I','L','N','O','R','S','T','U':
					// score = 1;
					// break;

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
				int i = c - 'A';
				switch (c) {
					case 'A', 'I':
						this.amounts[i] = 9;
						break;
					// case 'B', 'C', 'F', 'H', 'M', 'P', 'V', 'W', 'Y':
					// 	this.amounts[i] = 2;
					// 	break;
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
