package com.eccyan.optional;

import java.util.Collections;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
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

        return new Optional<>(new OnSubscribeForSingleItem<>(data));
    }

    public static <U> Optional<U> ofNullable(U data) {
        if (data == null) {
            return new Optional<>(new OnSubscribeFromIterable<>(Collections.<U>emptyList()));
        } else {
            return of(data);
        }
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

}
