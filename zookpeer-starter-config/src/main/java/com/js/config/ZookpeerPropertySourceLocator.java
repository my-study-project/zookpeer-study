package com.js.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.js.listener.NodeDataChangeCuratorCacheListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;

public class ZookpeerPropertySourceLocator implements PropertySourceLocator {
    private final CuratorFramework curatorFramework;
    private final String DATA_NODE = "/data";

    public ZookpeerPropertySourceLocator() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(20000)
                .connectionTimeoutMs(20000)
                .retryPolicy(new ExponentialBackoffRetry(20000, 3))
                .namespace("config")
                .build();
        curatorFramework.start();

    }

    @Override
    public PropertySource<?> locate(Environment environment, ConfigurableApplicationContext applicationContext) {
        //加载远程Zookeeper的配置保存到一个PropertySource
        System.out.println("开始加载外部化配置");
        CompositePropertySource composite = new CompositePropertySource("configService");
        try {
            Map<String, Object> dataMap = getRemoteEnvironment();
            MapPropertySource mapPropertySource = new MapPropertySource("configService", dataMap);
            composite.addPropertySource(mapPropertySource);
            //添加节点的数据变更的事件监听
            addListener(environment, applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return composite;
    }

    private Map<String, Object> getRemoteEnvironment() throws Exception {
        String data = new String(curatorFramework.getData().forPath(DATA_NODE));
        //支持JSON格式
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(data, Map.class);
        return map;
    }

    /**
     * 添加监听
     */
    private void addListener(Environment environment, ConfigurableApplicationContext applicationContext) {
        NodeDataChangeCuratorCacheListener ndc = new NodeDataChangeCuratorCacheListener(environment, applicationContext);
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, DATA_NODE, CuratorCache.Options.SINGLE_NODE_CACHE);
        CuratorCacheListener listener = CuratorCacheListener
                .builder()
                .forChanges(ndc).build();
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
    }
}
