package io.github.sollyucko;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static io.github.sollyucko._2048.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class _2048Test {
	@Test
	void testEmptyBoardHasNoValidMove() {
		assertDoesNotThrow(() -> {
			final _2048 board = new _2048((byte) 2, (byte) 3, (short) 0);
			assertThrows(_2048.InvalidMoveException.class, () -> board.tick(UP));
			assertThrows(_2048.InvalidMoveException.class, () -> board.tick(RIGHT));
			assertThrows(_2048.InvalidMoveException.class, () -> board.tick(DOWN));
			assertThrows(_2048.InvalidMoveException.class, () -> board.tick(LEFT));
		});
	}
	
	@Test
	void testEmptyBoardIsCorrectSizeAndEmpty() {
		assertDoesNotThrow(() -> {
			final _2048 board = new _2048((byte) 2, (byte) 3, (short) 0);
			final int[][] data = board.getGrid();
			assertEquals(data.length, 2);
			assertArrayEquals(data[0], new int[]{0, 0, 0});
			assertArrayEquals(data[1], new int[]{0, 0, 0});
		});
	}
	
	@Test
	void testMergeUp() {
		assertDoesNotThrow(() -> {
			//@formatter:off
			final BoardWithoutNewTiles board = new BoardWithoutNewTiles(new int[][]{
					{0, 2, 2, 0, 2, 2},
					{2, 0, 2, 0, 2, 2},
					{0, 2, 0, 2, 2, 2},
					{0, 2, 0, 2, 0, 2}
			});
			//@formatter:on
			for(final int[] row : board.getGrid()) {
				System.out.println(Arrays.toString(row));
			}
			System.out.println();
			board.tick(UP);
			final int[][] data = board.getGrid();
			for(final int[] row : data) {
				System.out.println(Arrays.toString(row));
			}
			assertEquals(data.length, 4);
			assertArrayEquals(new int[]{2, 4, 4, 4, 4, 4}, data[0]);
			assertArrayEquals(new int[]{0, 2, 0, 0, 2, 4}, data[1]);
			assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, data[2]);
			assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, data[3]);
		});
	}
	
	public static class BoardWithoutNewTiles extends _2048 {
		public BoardWithoutNewTiles() throws GameOverException {
			super();
		}
		
		public BoardWithoutNewTiles(final byte rows, final byte cols) throws GameOverException {
			super(rows, cols);
		}
		
		public BoardWithoutNewTiles(final short numStartingTiles, final Function<Random, Integer> tileSelector) throws GameOverException {
			super(numStartingTiles, tileSelector);
		}
		
		public BoardWithoutNewTiles(final Function<Random, Integer> tileSelector) throws GameOverException {
			super(tileSelector);
		}
		
		public BoardWithoutNewTiles(final byte rows, final byte cols, final short numStartingTiles) throws GameOverException {
			super(rows, cols, numStartingTiles);
		}
		
		public BoardWithoutNewTiles(final byte rows, final byte cols, final Function<Random, Integer> tileSelector) throws GameOverException {
			super(rows, cols, tileSelector);
		}
		
		public BoardWithoutNewTiles(final short numStartingTiles) throws GameOverException {
			super(numStartingTiles);
		}
		
		public BoardWithoutNewTiles(final byte rows, final byte cols, final short numStartingTiles, final Function<Random,
				Integer> tileSelector) throws GameOverException {
			super(rows, cols, numStartingTiles, tileSelector);
		}
		
		private BoardWithoutNewTiles(final int[][] data) {
			super(data);
		}
		
		public BoardWithoutNewTiles(final int[][] data, final Function<Random, Integer> tileSelector) {
			super(data, tileSelector);
		}
		
		@Override
		public void tick(final Direction direction) throws InvalidMoveException {
			this.move(direction);
		}
	}
}
