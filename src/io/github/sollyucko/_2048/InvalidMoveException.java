package io.github.sollyucko._2048;

@SuppressWarnings("UnnecessaryCallToSuper")
public class InvalidMoveException extends Exception {
	public InvalidMoveException() {
		super();
	}
	
	public InvalidMoveException(final String s) {
		super(s);
	}
	
	public InvalidMoveException(final String s, final Throwable throwable) {
		super(s, throwable);
	}
	
	public InvalidMoveException(final Throwable throwable) {
		super(throwable);
	}
}
