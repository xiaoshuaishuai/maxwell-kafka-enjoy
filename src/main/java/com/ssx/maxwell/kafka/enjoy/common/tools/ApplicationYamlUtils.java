package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-13 21:59:32
 * @description:
 */
@Slf4j
public class ApplicationYamlUtils {
    private static final String[] DB_KEY_ARRAY = {"master", "business_"};

    /**
     * 查询动态数据源db_key
     *
     * @return
     */
    public static List<DynamicDsInfo> readDynamicConfig() {
        List<DynamicDsInfo> res = Lists.newArrayList();
        try {
            Yaml yaml = new Yaml();

            URL url = ApplicationYamlUtils.class.getClassLoader().getResource("application.yml");
            if (url != null) {
                Map map = yaml.load(new FileInputStream(url.getFile()));
                log.info("读取application.yml=={}", map);
                Map map_spring = (Map) map.get("spring");
                Map map_datasource = (Map) map_spring.get("datasource");
                Map map_dynamic = (Map) map_datasource.get("dynamic");
                Map map_dynamic_datasource = (Map) map_dynamic.get("datasource");
                Set<String> stringSet = map_dynamic_datasource.keySet();
                for (String key : stringSet) {
                    if (null != key && !"".equals(key)) {
                        if (key.startsWith(DB_KEY_ARRAY[0]) || key.startsWith(DB_KEY_ARRAY[1])) {
                            res.add(new DynamicDsInfo(key));
                        } else {
                            log.info("排除++++++,{}", key);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("读取动态数据源配置key异常, e={}", e);
        }
        return res;
    }

}
