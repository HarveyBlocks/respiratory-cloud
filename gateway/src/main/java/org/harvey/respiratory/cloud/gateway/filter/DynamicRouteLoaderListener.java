package org.harvey.respiratory.cloud.gateway.filter;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.common.utils.JacksonUtil;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 监听器
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2024-01-12 15:24
 */
@Configuration
@Slf4j
public class DynamicRouteLoaderListener {
    // 路由配置文件的id和分组
    private static final String DATA_ID = "gateway-routes.json";
    private static final String GROUP = "SECURITY";
    // 保存更新过的路由id
    private final Set<String> routeIds = new HashSet<>();
    @Resource
    private RouteDefinitionWriter writer;
    @Resource
    private NacosConfigManager nacosConfigManager;
    @Resource
    private JacksonUtil jacksonUtil;

    @PostConstruct// javax注解
    public void initRouteConfigListener() throws NacosException {
        // 1.注册监听器并首次拉取配置
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(DATA_ID, GROUP, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        updateConfigInfo(configInfo);
                    }
                });
        // 2.首次启动时，更新一次配置
        updateConfigInfo(configInfo);
    }

    private void updateConfigInfo(String configInfo) {
        log.debug("监听到路由配置变更: \n{}", configInfo);
        // 1.反序列化
        List<RouteDefinition> routeDefinitions;
        if (configInfo != null) {
            routeDefinitions = jacksonUtil.toBeanList(configInfo, new TypeReference<>() {
            });
        } else {
            routeDefinitions = null;
        }
        // 2.更新前先清空旧路由
        // 2.1.清除旧路由
        for (String routeId : routeIds) {
            // 在RouteDefinitionWriter里删除
            writer.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        // 2.2.判断是否有新的路由要更新
        if (routeDefinitions == null || routeDefinitions.isEmpty()) {
            // 无新路由配置，直接结束
            return;
        }
        // 3.更新路由
        routeDefinitions.forEach(routeDefinition -> {
            // 3.1.更新路由
            writer.save(Mono.just(routeDefinition)).subscribe();
            // 3.2.记录路由id，方便将来删除
            routeIds.add(routeDefinition.getId());
        });
    }
}
