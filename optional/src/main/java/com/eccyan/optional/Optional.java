package com.eccyan.optional;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.operators.OnSubscribeFromIterable;

/**
 * Created by Daisuke Sato on 2/5/15.
 */
public class Optional<T> extends Observable<T> {

    public static class OnSubscribeForSingleItem<T> implements OnSubscribe<T> {

        private final T item;

        public OnSubscribeForSingleItem(final T item) {
            this.item = item;
        }

        @Override
        public void call(Subscriber<? super T> subscriber) {
            subscriber.onNext(this.item);
            subscriber.onCompleted();
        }
    }

    public static <U> Optional<U> of(U data) {
        if (data == null) {
            throw new NullPointerException();
        }

        return new Optional<U>(new OnSubscribeForSingleItem<U>(data));
    }

    public static <U> Optional<U> ofNullable(U data) {
        if (data == null) {
            return optionalEmpty();
        } else {
            return of(data);
        }
    }

    protected static <U> Optional<U> optionalEmpty() {
        return new Optional<U>(new OnSubscribeFromIterable<U>(Collections.<U>emptyList()));
    }

    protected Optional(OnSubscribe<T> f) {
        super(f);
    }

    public boolean isPresent() {
        return isEmpty().toBlocking().single();
    }

    public void ifPresent(Action1<? super T> action) {
        subscribe(action);
    }

    public T get() {
        return toBlocking().single();
    }

    public T orElse(T other) {
        return defaultIfEmpty(other).toBlocking().single();
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

    public Optional<T> filter(final Predicate<? super T> predicate) {
        return Optional.ofNullable(filter(new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return predicate.test(t);
            }
        }).toBlocking().singleOrDefault(null));
    }

    public <U> Optional<U> map(final Function<? super T,? extends U> mapper) {
        return Optional.ofNullable(map(new Func1<T, U>() {
            @Override
            public U call(T t) {
                return mapper.apply(t);
            }
        }).toBlocking().singleOrDefault(null));
    }

    public <U> Optional<U> flatMap(final Function<? super T,Optional<U>> mapper) {
        return (Optional<U>)flatMap(new Func1<T, Optional<U>>() {
            @Override
            public Optional<U> call(T t) {
                return mapper.apply(t);
            }
        }).single();
    }
}
