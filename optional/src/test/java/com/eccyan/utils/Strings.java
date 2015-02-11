package com.eccyan.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by Daisuke Sato on 2/11/15.
 */
public final class Strings {

    public static String join(String separator, String... strings) {
        final Iterator iterator = Arrays.asList(strings).iterator();

        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first);
        }

        final StringBuilder builder = new StringBuilder();
        if (first != null) {
            builder.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                builder.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                builder.append(obj);
            }
        }
        return builder.toString();
    }

}
