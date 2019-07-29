package io.github.sollyucko;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Blank spaces are represented as 0. Spaces should always contain either 0 or a power of 2 not less than 2.
 */
public class _2048 {
	private final int[][] grid;
	private final int numRows;
	private final int numCols;
	private final Function<Random, Integer> tileSelector;
	private final Random random = new Random();
	private final long score = 0;
	private int bestTile = 0;
	
	public _2048() throws GameOverException {
		this(4, 4, 2, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public _2048(final byte numRows, final byte numCols) throws GameOverException {
		this(numRows, numCols, 2, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public _2048(final short numStartingTiles, final Function<Random, Integer> tileSelector) throws GameOverException {
		this(4, 4, numStartingTiles, tileSelector);
	}
	
	public _2048(final Function<Random, Integer> tileSelector) throws GameOverException {
		this(4, 4, 2, tileSelector);
	}
	
	public _2048(final byte numRows, final byte numCols, final short numStartingTiles) throws GameOverException {
		this(numRows, numCols, numStartingTiles, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public _2048(final byte numRows, final byte numCols, final Function<Random, Integer> tileSelector) throws GameOverException {
		this(numRows, numCols, 2, tileSelector);
	}
	
	public _2048(final short numStartingTiles) throws GameOverException {
		this(4, 4, numStartingTiles, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public _2048(final int numRows, final int numCols, final int numStartingTiles,
	             final Function<Random, Integer> tileSelector) throws GameOverException {
		this.numRows = numRows;
		this.numCols = numCols;
		this.grid = new int[numRows][numCols];
		this.tileSelector = tileSelector;
		for(short i = 0; i < numStartingTiles; ++i) {
			this.addTile();
		}
	}
	
	public _2048(final int[][] grid) {
		this(grid, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public _2048(final int[][] grid, final Function<Random, Integer> tileSelector) {
		this.numRows = grid.length;
		this.numCols = grid[0].length;
		this.grid = grid.clone();
		this.tileSelector = tileSelector;
	}
	
	protected void addTile() throws GameOverException {
		this.placeTile(this.pickTile());
	}
	
	private int get(final Coordinate coord) {
		return this.grid[coord.row][coord.col];
	}
	
	public int getBestTile() {
		return this.bestTile;
	}
	
	//	private Map<? super Direction, Iterable<? extends Coordinate>> coordinates = new HashMap<>();
	//
	//	private Iterable<? extends Coordinate> getCoordinates(final Direction direction) {
	//		return this.coordinates.computeIfAbsent(direction, k -> cache(() -> new Generator<>(yield -> {
	//			if(direction.numRows == 0) {
	//				for(final int col : this.getCols(direction)) {
	//					for(final int row : this.getRows(direction)) {
	//						yield.accept(new Coordinate(row, col));
	//					}
	//				}
	//			} else {
	//				for(final int row : this.getRows(direction)) {
	//					for(final int col : this.getCols(direction)) {
	//						yield.accept(new Coordinate(row, col));
	//					}
	//				}
	//			}
	//		})));
	//	}
	
	private IntStream getCols(final Direction direction) {
		if(direction.numCols == 1) {
			return IntStream.range(0, this.numCols).map(x -> this.numCols - 1 - x);
		} else {
			return IntStream.range(0, this.numCols);
		}
	}
	
	private Stream<Stream<Coordinate>> getCoordinates(final Direction direction) {
		//@formatter:off
		return direction.numRows==0
		       ? this.getRows(direction).mapToObj(row -> this.getCols(direction).mapToObj(col -> new Coordinate(row, col)))
		       : this.getCols(direction).mapToObj(col -> this.getRows(direction).mapToObj(row -> new Coordinate(row, col)));
		//@formatter:on
	}
	
	public int[][] getGrid() {
		return this.grid.clone();
	}
	
	public int getNumCols() {
		return this.numCols;
	}
	
	public int getNumRows() {
		return this.numRows;
	}
	
	private IntStream getRows(final Direction direction) {
		if(direction.numRows == 1) {
			return IntStream.range(0, this.numRows).map(x -> this.numRows - 1 - x);
		} else {
			return IntStream.range(0, this.numRows);
		}
	}
	
	public long getScore() {
		return this.score;
	}
	
	protected void move(final Direction direction) throws InvalidMoveException {
		boolean hasChanged = false;
		for(final Stream<Coordinate> group : (Iterable<Stream<Coordinate>>) this.getCoordinates(direction)::iterator) {
			Coordinate prevCoord = null;
			int prevValue = 0;
			Coordinate nextAvailableCoord = null;
			for(final Coordinate coord : (Iterable<Coordinate>) group::iterator) {
				//				System.out.println(nextAvailableCoord);
				
				if(nextAvailableCoord == null) nextAvailableCoord = coord;
				
				final int value = this.get(coord);
				if(value != 0) {
					if(value == prevValue) {
						this.set(prevCoord, prevValue + value);
						this.set(coord, 0);
						prevCoord = null;
						prevValue = 0;
						hasChanged = true;
					} else {
						prevCoord = nextAvailableCoord;
						prevValue = value;
						if(!coord.equals(nextAvailableCoord)) {
							this.set(nextAvailableCoord, value);
							this.set(coord, 0);
							hasChanged = true;
						}
						nextAvailableCoord = nextAvailableCoord.minus(direction);
					}
				}
				
				for(final int[] row : this.getGrid()) {
					System.out.println(Arrays.toString(row));
				}
				System.out.println();
			}
		}
		if(!hasChanged) {
			throw new InvalidMoveException();
		}
	}
	
	protected int pickTile() {
		return this.tileSelector.apply(this.random);
	}
	
	protected void placeTile(final int tile) throws GameOverException {
		final List<int[]> availableCoordinates = new ArrayList<>();
		for(int row = 0; row < this.numRows; ++row) {
			for(int col = 0; col < this.numCols; ++col) {
				if(this.grid[row][col] == 0) {
					availableCoordinates.add(new int[]{row, col});
				}
			}
		}
		if(availableCoordinates.isEmpty()) {
			throw new GameOverException();
		}
		final int[] coordinate = availableCoordinates.get(this.random.nextInt(availableCoordinates.size()));
		this.grid[coordinate[0]][coordinate[1]] = tile;
		this.bestTile = Math.max(this.bestTile, tile);
	}
	
	private void set(final Coordinate coord, final int value) {
		this.grid[coord.row][coord.col] = value;
	}
	
	public void tick(final Direction direction) throws GameOverException, InvalidMoveException {
		this.move(direction);
		this.addTile();
	}
	
	public enum Direction {
		UP(-1, 0), RIGHT(0, 1), DOWN(1, 0), LEFT(0, -1);
		
		private final int numRows;
		private final int numCols;
		
		Direction(final int numRows, final int cols) {
			this.numRows = numRows;
			this.numCols = cols;
		}
	}
	
	private static class Coordinate {
		private final int row;
		private final int col;
		
		public Coordinate(final int row, final int col) {
			this.row = row;
			this.col = col;
		}
		
		public Coordinate minus(final Direction other) {
			return new Coordinate(this.row - other.numRows, this.col - other.numCols);
		}
		
		@Override
		public String toString() {
			return "new Coordinate(" + this.row + ", " + this.col + ")";
		}
	}
	
	public static class GameOverException extends Exception {}
	
	public static class InvalidMoveException extends Exception {}
}
