package com.eccyan.optional;

import org.junit.Test;

import java.util.NoSuchElementException;

import rx.functions.Func1;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Daisuke Sato on 2/10/15.
 */
public class OptionalTest {

    @Test
    public void thisAlwaysPasses() {
        assertTrue(true);
    }

    @Test(expected = NoSuchElementException.class)
    public void optionalEmpty_get_throwsNoSuchElementException() {
        final Optional<Integer> actual = Optional.empty();
        Optional.empty().get();
    }

    @Test
    public void isPresent_returnsTrueWhenValuePresent() {
        final Optional<Integer> actual = Optional.ofNullable(1);
        assertTrue(actual.isPresent());
    }

    @Test
    public void isPresent_returnsFalseWhenValuePresent() {
        final Optional<Integer> actual = Optional.ofNullable(null);
        assertFalse(actual.isPresent());
    }

    @Test
    public void filter_returnsOptionalDescribedValueWhenPredicateReturnsTrue() {
        final int expected = 12345;
        final Optional<Integer> actual = Optional.ofNullable(expected)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(
                            Integer integer) {
                        return true;
                    }
                });

        assertThat(actual.get(), is(expected));
    }

    @Test
    public void filter_returnsEmptyWhenPredicateReturnsFalse() {
        final Optional<Integer> actual = Optional.ofNullable(12345)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return false;
                    }
                });

        assertFalse(actual.isPresent());
    }

    @Test
    public void filter_returnsEmptyWhenValueIsEmpty() {
        final Optional<Integer> empty = Optional.ofNullable(null);
        final Optional<Integer> actual = empty.filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return true;
            }
        });

        assertFalse(actual.isPresent());
    }

    @Test
    public void map_returnsOptionalDescribedValueWhenValuePresent() {
        final Optional<Integer> actual = Optional.ofNullable(1)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer + 1;
                    }
                });
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(2));
    }

    @Test
    public void map_returnsEmptyWhenValueNotPresent() {
        final Optional<Integer> empty = Optional.ofNullable(null);
        final Optional<Integer> actual = empty.map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer + 1;
            }
        });

        assertFalse(actual.isPresent());
    }

    @Test
    public void flatMap_returnsOptionalDescribedValueWhenValuePresent() {
        final Optional<Integer> actual = Optional.ofNullable(1)
                .flatMap(new Func1<Integer, Optional<Integer>>() {
                    @Override
                    public Optional<Integer> call(Integer integer) {
                        return Optional.ofNullable(integer + 2);
                    }
                });

        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(3));
    }

    @Test
    public void flatMap_returnsEmptyWhenValueNotPresent() {
        final Optional<Integer> empty = Optional.ofNullable(null);
        final Optional<Integer> actual = empty
                .flatMap(new Func1<Integer, Optional<Integer>>() {
                    @Override
                    public Optional<Integer> call(Integer integer) {
                        return Optional.ofNullable(integer + 1);
                    }
                });

        assertFalse(actual.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void flatMap_throwsNullPointerExceptionWhenMapperReturnsNull() {
        final Optional<Integer> actual = Optional.ofNullable(1)
                .flatMap(new Func1<Integer, Optional<Integer>>() {
                    @Override
                    public Optional<Integer> call(Integer integer) {
                        return null;
                    }
                });
    }
}
