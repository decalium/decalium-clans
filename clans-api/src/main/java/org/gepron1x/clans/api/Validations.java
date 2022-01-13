package org.gepron1x.clans.api;

import it.unimi.dsi.fastutil.chars.CharPredicate;
import org.jetbrains.annotations.NotNull;

public final class Validations {

    private static final int MAX_TAG_SIZE = 16;
    private static final int MIN_TAG_SIZE = 4;

    private static final int MIN_HOME_NAME_SIZE = 4;
    private static final int MAX_HOME_NAME_SIZE = 32;

    private static final CharPredicate TAG_PREDICATE = c -> c == '_' || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');

    public static boolean checkTag(@NotNull String tag) {
        return check(tag, MIN_TAG_SIZE, MAX_TAG_SIZE, TAG_PREDICATE);
    }

    public static boolean checkHomeName(@NotNull String name) {
        return check(name, MIN_HOME_NAME_SIZE, MAX_HOME_NAME_SIZE, TAG_PREDICATE);
    }

    private static boolean check(String value, int minSize, int maxSize, CharPredicate predicate) {
        int size = value.length();
        if(size < minSize || size > maxSize) return false;
        char[] chars = value.toCharArray();
        for(char c : chars) {
            if(!predicate.test(c)) return false;
        }
        return true;
    }


}
