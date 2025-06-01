package org.harvey.respiratory.cloud.gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;
import java.util.Arrays;

/**
 * IP解析->归属地
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-13 10:47
 */
@Slf4j
public class IpTool {
    private static Searcher searcher;

    static {
        // 1、创建 searcher 对象
        String dbPath = "ip2region.xdb";
        // 1、从 dbPath 中预先加载 VectorIndex 缓存，并且把这个得到的数据作为全局变量，后续反复使用。
        byte[] vIndex = null;
        try {
            vIndex = Searcher.loadVectorIndexFromFile(dbPath);
        } catch (Exception e) {
            log.error(String.format("failed to load vector index from `%s`: %s", dbPath, e));
        }

        // 2、使用全局的 vIndex 创建带 VectorIndex 缓存的查询对象。
        if (vIndex != null) {
            try {
                searcher = Searcher.newWithVectorIndex(dbPath, vIndex);
            } catch (IOException e) {
                log.error("failed to create searcher with `{}`: ", dbPath, e);
            }
        }
    }

    public static String[] map(String ip) {

        // 2、查询
        String[] region = new String[]{};
        if (searcher == null) {
            return region;
        }
        if (ip == null || ip.isEmpty()) {
            return region;
        }
        if (ip.split(":").length == 8) {
            log.warn("({})似乎是IPV6?", ip);
            return region;
        }
        try {
            region = searcher.search(ip).split("\\|");
            if (region.length > 10) {
                return new String[]{};
            }
            log.debug("{region: {}, ioCount: {}}", Arrays.toString(region), searcher.getIOCount());
        } catch (Exception e) {
            log.error("failed to search({}): {}", ip, e.getMessage());

        }
        return region;
        // 备注：并发使用，每个线程需要创建一个独立的 searcher 对象单独使用。
    }

    public static void close() {
        // 3、关闭资源
        try {
            searcher.close();
        } catch (IOException e) {
            log.error("failed to close searcher : %s\n", e);
        }
    }

    public static void main(String[] args) {
        Arrays.stream(IpTool.map("210.34.59.73")).forEach(System.out::println);
        Arrays.stream(IpTool.map("127.0.0.1")).forEach(System.out::println);
        Arrays.stream(IpTool.map("localhost")).forEach(System.out::println);
    }
}
