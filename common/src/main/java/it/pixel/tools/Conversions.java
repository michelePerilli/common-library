package it.pixel.tools;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Conversions.
 * 22/01/2022
 */
public class Conversions {

    private Conversions() {
    }

    /**
     * Number to string string.
     *
     * @param <T>    the type parameter
     * @param number the number
     * @return the string
     */
    @Nullable
    public static <T extends Number> String numberToString(@Nullable T number) {
        if (number == null) return null;
        else return number.toString();
    }

    /**
     * String to long long.
     *
     * @param source the source
     * @return the long
     */
    @Nullable
    public static Long stringToLong(@Nullable String source) {
        return source == null ? null : Long.valueOf(source);
    }

    /**
     * String to float float.
     *
     * @param source the source
     * @return the float
     */
    @Nullable
    public static Float stringToFloat(@Nullable String source) {
        return source == null ? null : Float.valueOf(source);
    }

    /**
     * Single item list list.
     *
     * @param <T>  the type parameter
     * @param item the item
     * @return the list
     */
    public static <T> List<T> singleItemList(T item) {
        return new ArrayList<>(List.of(item));
    }
}
