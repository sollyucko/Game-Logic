package io.github.sollyucko._2048;

public class Coordinate {
	private final int row;
	private final int col;
	
	public Coordinate(final int row, final int col) {
		this.row = row;
		this.col = col;
	}
	
	@Override
	public boolean equals(final Object o) {
		if(this == o)
			return true;
		
		if(!(o instanceof Coordinate))
			return false;
		
		final Coordinate other = (Coordinate) o;
		
		return this.row == other.row && this.col == other.col;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public int getRow() {
		return this.row;
	}
	
	@Override
	public int hashCode() {
		int result = this.row;
		result = 31 * result + this.col;
		return result;
	}
}
