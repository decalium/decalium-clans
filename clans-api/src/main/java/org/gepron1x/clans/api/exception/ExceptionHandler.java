package org.gepron1x.clans.api.exception;

import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ExceptionHandler<T> extends Function<Throwable, T> {

	static <T> ExceptionHandler<T> catchException(Consumer<Throwable> consumer) {
		return e -> {
			if(e instanceof CompletionException exception) e = exception.getCause();
			consumer.accept(e);
			return null;
		};
	}

	static <E extends Throwable, T> ExceptionHandler<T> catchException(Class<E> exceptionClass, Consumer<E> consumer) {
		return catchException(e -> {
			if(exceptionClass.isInstance(e)) consumer.accept(exceptionClass.cast(e));
		});
	}

	default <E extends Throwable, T> ExceptionHandler<T> exception(Class<E> exceptionClass, Consumer<E> consumer) {
		return e -> {
			if(exceptionClass.isInstance(e)) consumer.accept(exceptionClass.cast(e));
			this.apply(e);
			return null;
		};
	}

}
