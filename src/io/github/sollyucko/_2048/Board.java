package io.github.sollyucko._2048;

import java.util.*;
import java.util.function.Function;

/**
 * Blank spaces are represented as 0. Spaces should always contain either 0 or a power of 2 not less than 2.
 */
public class Board {
	private final int[][] data;
	private final byte rows;
	private final byte cols;
	private final Function<Random, Integer> tileSelector;
	private final Random random = new Random();
	private long score = 0;
	private int bestTile = 0;
	
	public Board() throws GameOverException {
		this((byte) 4, (byte) 4, (short) 2, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public Board(final byte rows, final byte cols) throws GameOverException {
		this(rows, cols, (short) 2, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public Board(final short numStartingTiles, final Function<Random, Integer> tileSelector) throws GameOverException {
		this((byte) 4, (byte) 4, numStartingTiles, tileSelector);
	}
	
	public Board(final Function<Random, Integer> tileSelector) throws GameOverException {
		this((byte) 4, (byte) 4, (short) 2, tileSelector);
	}
	
	public Board(final byte rows, final byte cols, final short numStartingTiles) throws GameOverException {
		this(rows, cols, numStartingTiles, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public Board(final byte rows, final byte cols, final Function<Random, Integer> tileSelector) throws GameOverException {
		this(rows, cols, (short) 2, tileSelector);
	}
	
	public Board(final short numStartingTiles) throws GameOverException {
		this((byte) 4, (byte) 4, numStartingTiles, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public Board(final byte rows, final byte cols, final short numStartingTiles,
	             final Function<Random, Integer> tileSelector) throws GameOverException {
		this.rows = rows;
		this.cols = cols;
		this.data = new int[rows][cols];
		this.tileSelector = tileSelector;
		for(short i = 0; i < numStartingTiles; ++i) {
			this.addTile();
		}
	}
	
	public Board(final int[][] data) {
		this(data, random -> random.nextInt(10) == 0 ? 4 : 2);
	}
	
	public Board(final int[][] data, final Function<Random, Integer> tileSelector) {
		this.rows = (byte) data.length;
		this.cols = (byte) data[0].length;
		this.data = data.clone();
		this.tileSelector = tileSelector;
	}
	
	protected void addTile() throws GameOverException {
		this.placeTile(this.pickTile());
	}
	
	public int getBestTile() {
		return this.bestTile;
	}
	
	public int[][] getData() {
		return this.data.clone();
	}
	
	public long getScore() {
		return this.score;
	}
	
	protected void move(final Direction direction) throws InvalidMoveException {
		boolean hasChanged = false;
		switch(direction) {
			case UP:
				for(int col = 0; col < this.cols; ++col) {
					int rowFrom = 0;
					rowTo:
					for(int rowTo = 0; rowTo < this.rows; ++rowTo) {
						int to = this.data[rowTo][col];
						rowFrom = Math.max(rowFrom, rowTo + 1);
						for(; ; ++rowFrom) {
							if(rowFrom == this.rows) {
								break rowTo;
							}
							final int from = this.data[rowFrom][col];
							if(to == 0) {
								if(from != 0) {
									hasChanged = true;
									this.data[rowTo][col] = from;
									this.data[rowFrom][col] = 0;
									break;
								}
							} else if(from == to) {
								hasChanged = true;
								to += from;
								this.score += to;
								this.data[rowTo][col] = to;
								this.bestTile = Math.max(this.bestTile, to);
								this.data[rowFrom][col] = 0;
								break;
							}
						}
					}
				}
				break;
			case RIGHT:
				for(final int[] row : this.data) {
					int colFrom = 0;
					colTo:
					for(int colTo = this.cols - 1; colTo >= 0; --colTo) {
						int to = row[colTo];
						colFrom = Math.min(colFrom, colTo - 1);
						for(; ; --colFrom) {
							if(colFrom < 0) {
								break colTo;
							}
							final int from = row[colFrom];
							if(to == 0) {
								if(from != 0) {
									hasChanged = true;
									row[colTo] = from;
									row[colFrom] = 0;
									break;
								}
							} else if(from == to) {
								hasChanged = true;
								to += from;
								this.score += to;
								row[colTo] = to;
								this.bestTile = Math.max(this.bestTile, to);
								row[colFrom] = 0;
								break;
							}
						}
					}
				}
				break;
			case DOWN:
				for(int col = 0; col < this.cols; ++col) {
					int rowFrom = this.rows - 1;
					rowTo:
					for(int rowTo = this.rows - 1; rowTo >= 0; --rowTo) {
						int to = this.data[rowTo][col];
						rowFrom = Math.min(rowFrom, rowTo - 1);
						for(; ; --rowFrom) {
							if(rowFrom < 0) {
								break rowTo;
							}
							final int from = this.data[rowFrom][col];
							if(to == 0) {
								if(from != 0) {
									hasChanged = true;
									this.data[rowTo][col] = from;
									this.data[rowFrom][col] = 0;
									break;
								}
							} else if(from == to) {
								hasChanged = true;
								to += from;
								this.score += to;
								this.data[rowTo][col] = to;
								this.bestTile = Math.max(this.bestTile, to);
								this.data[rowFrom][col] = 0;
								break;
							}
						}
					}
				}
				break;
			case LEFT:
				for(final int[] row : this.data) {
					int colFrom = 0;
					colTo:
					for(int colTo = 0; colTo < this.cols; ++colTo) {
						int to = row[colTo];
						if(row[colTo] == 0) {
							colFrom = Math.max(colFrom, colTo + 1);
							for(; ; ++colFrom) {
								if(colFrom == this.cols) {
									break colTo;
								}
								final int from = row[colFrom];
								if(to == 0) {
									if(from != 0) {
										hasChanged = true;
										row[colTo] = from;
										row[colFrom] = 0;
										break;
									}
								} else if(from == to) {
									hasChanged = true;
									to += from;
									this.score += to;
									row[colTo] = to;
									this.bestTile = Math.max(this.bestTile, to);
									row[colFrom] = 0;
									break;
								}
							}
						}
					}
				}
				break;
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
		for(int row = 0; row < this.rows; ++row) {
			for(int col = 0; col < this.cols; ++col) {
				if(this.data[row][col] == 0) {
					availableCoordinates.add(new int[]{row, col});
				}
			}
		}
		if(availableCoordinates.isEmpty()) {
			throw new GameOverException();
		}
		final int[] coordinate = availableCoordinates.get(this.random.nextInt(availableCoordinates.size()));
		this.data[coordinate[0]][coordinate[1]] = tile;
		this.bestTile = Math.max(this.bestTile, tile);
	}
	
	public void tick(final Direction direction) throws GameOverException, InvalidMoveException {
		this.move(direction);
		this.addTile();
	}
}
