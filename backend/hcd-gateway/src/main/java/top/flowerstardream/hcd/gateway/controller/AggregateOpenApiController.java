package top.flowerstardream.hcd.gateway.controller;

import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @Author: 花海
 * @Date: 2025/11/06/02:16
 * @Description: 聚合 OpenAPI
 */
@Slf4j
@RestController
@RequestMapping("/aggregate")
@RequiredArgsConstructor
public class AggregateOpenApiController {

    private final WebClient webClient = WebClient.create();
    private final DiscoveryClient discoveryClient;   // ← 注入注册中心客户端

    /* 实时拉取服务列表 */
    private Flux<Map.Entry<String,String>> serviceInstances() {
    return Flux.fromIterable(discoveryClient.getServices())
               .filter(s -> s.startsWith("hcd-"))          // 只留 hcd- 开头的服务
               .flatMap(service -> {
                   log.info("正在获取服务实例：{}", discoveryClient.getInstances(service));
                   return Flux.fromIterable(discoveryClient.getInstances(service));
               })
               .map(instance -> {
                   String shortName = instance.getServiceId().replaceFirst("^hcd-", "");
                   return Map.entry(shortName,
                                    "http://" + instance.getHost() + ":" + instance.getPort());
               });
}


    /* 聚合 /v3/api-docs */
    @GetMapping(value = "/v3/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> aggregate() {
        return serviceInstances()
                .flatMap(e -> webClient
                        .get()
                        .uri(e.getValue() + "/" + e.getKey() + "/v3/api-docs")
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorResume(ex -> Mono.just("{}"))
                        .map(json -> Map.entry(e.getKey(), json)))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(map -> new JSONObject(map).toString());
    }

}
