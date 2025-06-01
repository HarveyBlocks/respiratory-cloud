package org.harvey.respiratory.cloud.common.constants;


import java.util.function.Function;

/**
 * 创建RedisKey
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-31 22:26
 */
public interface KeyGenerator<T> {
    static <T> KeyGenerator<T> ofPrefix(String prefix) {
        return t -> prefix + t;
    }

    default KeyGenerator<T> addPrefix(String prefix) {
        return t -> prefix + this.generate(t);
    }

    /**
     * 从别的变成这个
     */
    default <OUTER> KeyGenerator<OUTER> warp(Function<OUTER, T> mapper) {
        return outer -> {
            T t = mapper.apply(outer);
            return this.generate(t);
        };
    }

    String generate(T t);
}
