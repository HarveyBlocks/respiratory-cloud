package org.harvey.respiratory.cloud.common.utils;

import java.util.List;
import java.util.Random;

/**
 * 生产随机数的工具
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-22 01:46
 */
public class RandomUtil {
    public static final char[] STRING_POOL = {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z'};
    private final Random random;

    public RandomUtil() {
        random = new Random(System.currentTimeMillis());
    }

    /**
     * @param left  包含
     * @param right 不包含
     */
    public int uniform(int left, int right) {
        return random.nextInt(right - left) + left;
    }

    public String randomString(int shortest, int longest) {
        int length = uniform(shortest, longest);
        int poolSize = STRING_POOL.length;
        char[] data = new char[length];
        for (int i = 0; i < length; i++) {
            data[i] = STRING_POOL[uniform(0, poolSize)];
        }
        return String.valueOf(data);
    }

    public <T> T chose(List<T> list) {
        return list.get(uniform(0, list.size() - 1));
    }

    public <T> T chose(T[] array) {
        return array[uniform(0, array.length - 1)];
    }

    /**
     * @param probability to create 1
     * @return true for 1;
     */
    public boolean bit(double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("概率差超范围[0,1]: " + probability);
        }
        return random.nextDouble() < probability;
    }


}
