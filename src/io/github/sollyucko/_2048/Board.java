package io.github.sollyucko._2048;

import java.util.*;

/**
 * Blank spaces are represented as 0. Spaces should always contain either 0 or a power of 2 not less than 2.
 */
public class Board {
	private final int[][] data;
	private final byte rows;
	private final byte cols;
	private final Random random;
	private long score;
	private int bestTile;
	
	public Board(final byte rows, final byte cols, final short startingTiles) throws GameOverException {
		this.rows = rows;
		this.cols = cols;
		this.data = new int[rows][cols];
		this.score = 0;
		this.random = new Random();
		this.bestTile = 0;
		for(short i = 0; i < startingTiles; ++i) {
			this.addTile();
		}
	}
	
	private void addTile() throws GameOverException {
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
	
	private void move(Direction direction) {
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
									this.data[rowTo][col] = from;
									this.data[rowFrom][col] = 0;
									break;
								}
							} else if(from == to) {
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
									row[colTo] = from;
									row[colFrom] = 0;
									break;
								}
							} else if(from == to) {
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
									this.data[rowTo][col] = from;
									this.data[rowFrom][col] = 0;
									break;
								}
							} else if(from == to) {
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
										row[colTo] = from;
										row[colFrom] = 0;
										break;
									}
								} else if(from == to) {
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
	}
	
	private int pickTile() {
		return this.random.nextInt(10) == 0 ? 4 : 2;
	}
	
	private void placeTile(int tile) throws GameOverException {
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
	
	public void tick(final Direction direction) throws GameOverException {
		this.move(direction);
		this.addTile();
	}
}
