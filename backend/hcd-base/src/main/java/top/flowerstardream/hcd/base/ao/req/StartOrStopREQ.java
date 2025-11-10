package top.flowerstardream.hcd.base.ao.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "状态（0-禁用，1-启用）")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
