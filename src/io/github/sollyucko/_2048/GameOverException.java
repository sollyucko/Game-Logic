package io.github.sollyucko._2048;

@SuppressWarnings("UnnecessaryCallToSuper")
public class GameOverException extends Exception {
	public GameOverException() {
		super();
	}
	
	public GameOverException(final String s) {
		super(s);
	}
	
	public GameOverException(final String s, final Throwable throwable) {
		super(s, throwable);
	}
	
	public GameOverException(final Throwable throwable) {
		super(throwable);
	}
}
