package org.gepron1x.clans.api;

import it.unimi.dsi.fastutil.chars.CharPredicate;
import org.jetbrains.annotations.NotNull;

public final class Validations {

    private static final int MAX_TAG_SIZE = 16;
    private static final int MIN_TAG_SIZE = 4;

    private static final CharPredicate TAG_PREDICATE = c -> c == '_' || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');

    public static boolean checkTag(@NotNull String tag) {
        int size = tag.length();
        if(size < MIN_TAG_SIZE || size > MAX_TAG_SIZE) return false;
        char[] chars = tag.toCharArray();
        for(char c : chars) {
            if(!TAG_PREDICATE.test(c)) return false;
        }
        return true;
    }


}
