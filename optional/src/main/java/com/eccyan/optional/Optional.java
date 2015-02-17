package com.eccyan.optional;

import java.util.Collections;
import java.util.Objects;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by Daisuke Sato on 2/5/15.
 */
public class Optional<T> {

    private final Observable<T> observable;

    public static <U> Optional<U> of(U data) {
        if (data == null) {
            throw new NullPointerException();
        }

        return new Optional<U>(Observable.just(data));
    }

    public static <U> Optional<U> ofNullable(U data) {
        if (data == null) {
            return empty();
        } else {
            return of(data);
        }
    }

    public static <U> Optional<U> empty() {
        return new Optional<U>(Observable.from(Collections.<U>emptyList()));
    }

    protected Optional(Observable<T> observable) {
        this.observable = observable;
    }

    public boolean isPresent() {
        return !this.observable.isEmpty().toBlocking().single();
    }

    public void ifPresent(Action1<? super T> action) {
        if (isPresent()) {
            Objects.requireNonNull(action);
            this.observable.subscribe(action);
        }
    }

    public T get() {
        return this.observable.toBlocking().single();
    }

    public T orElse(T other) {
        return this.observable.defaultIfEmpty(other).toBlocking().single();
    }

    public T orElseCall(Func0<? extends T> other) {
        return isPresent() ? get() : other.call();
    }

    public <X extends Throwable> T orElseThrow(Func0<? extends X> other) throws X {
        if (!isPresent()) {
            throw other.call();
        }

        return get();
    }

    public Optional<T> filter(final Func1<? super T, Boolean> predicate) {
        Objects.requireNonNull(predicate);

        return Optional.ofNullable(this.observable.filter(new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return predicate.call(t);
            }
        }).toBlocking().singleOrDefault(null));
    }

    public <U> Optional<U> map(final Func1<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);

        return Optional.ofNullable(this.observable.map(new Func1<T, U>() {
            @Override
            public U call(T t) {
                return mapper.call(t);
            }
        }).toBlocking().singleOrDefault(null));
    }

    public <U> Optional<U> flatMap(final Func1<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);

        final Optional<U> optional = this.observable.flatMap(new Func1<T, Observable<Optional<U>>>() {
            @Override
            public Observable<Optional<U>> call(T t) {
                return Observable.just(Objects.requireNonNull(mapper.call(t)));
            }
        }).toBlocking().singleOrDefault(Optional.<U>empty());

        return optional;
    }
}
