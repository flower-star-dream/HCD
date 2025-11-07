package top.flowerstardream.hcd.base.ao.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 花海
 * @Date: 2025/11/06/21:32
 * @Description: 启用或禁用账号请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "启用或禁用账号请求")
public class StartOrStopREQ {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "状态")
    private Integer status;
}
