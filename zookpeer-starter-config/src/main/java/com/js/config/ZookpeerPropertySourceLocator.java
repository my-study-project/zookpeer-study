package com.js.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
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
        curatorFramework = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").sessionTimeoutMs(20000).connectionTimeoutMs(20000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).namespace("config").build();
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
}
