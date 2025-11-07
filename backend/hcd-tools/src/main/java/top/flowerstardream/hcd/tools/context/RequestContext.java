package top.flowerstardream.hcd.tools.context;

import lombok.Data;

/**
 * @Author: 花海
 * @Date: 2025/11/06/22:14
 * @Description: 请求上下文
 */
@Data
public class RequestContext {
    private String traceId;
    private Long tenantId;
    private String token;
}