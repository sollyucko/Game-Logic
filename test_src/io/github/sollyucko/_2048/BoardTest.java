package io.github.sollyucko._2048;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.function.Function;

import static io.github.sollyucko._2048.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
	@Test
	public void testEmptyBoardHasNoValidMove() {
		assertDoesNotThrow(() -> {
			final Board board = new Board((byte) 2, (byte) 3, (short) 0);
			assertThrows(InvalidMoveException.class, () -> board.tick(UP));
			assertThrows(InvalidMoveException.class, () -> board.tick(RIGHT));
			assertThrows(InvalidMoveException.class, () -> board.tick(DOWN));
			assertThrows(InvalidMoveException.class, () -> board.tick(LEFT));
		});
	}
	
	@Test
	public void testEmptyBoardIsCorrectSizeAndEmpty() {
		assertDoesNotThrow(() -> {
			final Board board = new Board((byte) 2, (byte) 3, (short) 0);
			final int[][] data = board.getData();
			assertEquals(data.length, 2);
			assertArrayEquals(data[0], new int[]{0, 0, 0});
			assertArrayEquals(data[1], new int[]{0, 0, 0});
		});
	}
	
	@Test
	public void testMergeUp() {
		assertDoesNotThrow(() -> {
			//@formatter:off
			final BoardWithoutNewTiles board = new BoardWithoutNewTiles(new int[][]{
					{0, 2, 2, 0, 2, 2},
					{2, 0, 2, 0, 2, 2},
					{0, 2, 0, 2, 2, 2},
					{0, 2, 0, 2, 0, 2}
			});
			//@formatter:on
			board.tick(UP);
			final int[][] data = board.getData();
			assertEquals(data.length, 4);
			assertArrayEquals(new int[]{2, 4, 4, 4, 4, 4}, data[0]);
			assertArrayEquals(new int[]{0, 2, 0, 0, 2, 4}, data[1]);
			assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, data[2]);
			assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, data[3]);
		});
	}
	
	public static class BoardWithoutNewTiles extends Board {
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
		
		public BoardWithoutNewTiles(final int[][] data) {
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
