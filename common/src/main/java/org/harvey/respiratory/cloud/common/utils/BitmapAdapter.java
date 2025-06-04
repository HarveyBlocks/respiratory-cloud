package org.harvey.respiratory.cloud.common.utils;

import org.harvey.respiratory.cloud.common.exception.ServerException;
import org.harvey.respiratory.cloud.common.pojo.follow.enums.AllergenIndividualExamination;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:16
 */
public class BitmapAdapter {
    private static final short[] MASK = new short[]{
            0x80,
            0x40,
            0x20,
            0x10,
            0x08,
            0x04,
            0x02,
            0x01,
    };

    public static byte[] toBitmap(Set<? extends Enum<?>> parameter) {
        int length = AllergenIndividualExamination.values().length;
        byte[] bitmap = new byte[length / 8 + (length % 8 == 0 ? 0 : 1)];
        for (Enum<?> each : parameter) {
            int ordinal = each.ordinal();
            bitmap[ordinal / 8] = (byte) (bitmap[ordinal / 8] | MASK[ordinal % 8]);
        }
        return bitmap;
    }

    public static <T extends Enum<T>> Set<T> toEnumSet(byte[] bitmap, Class<T> type) {
        T[] enumConstants = type.getEnumConstants();
        if (enumConstants == null || enumConstants.length == 0) {
            throw new ServerException("illegal for not a enum type");
        }
        int bitLength = enumConstants.length;
        Set<T> result = new HashSet<>();
        for (int ordinal = 0; ordinal < bitLength; ordinal++) {
            boolean hit = (bitmap[ordinal / 8] & MASK[ordinal % 8]) != 0;
            if (hit) {
                T enumConstant = enumConstants[ordinal];
                result.add(enumConstant);
            }
        }
        return result;
    }
}
