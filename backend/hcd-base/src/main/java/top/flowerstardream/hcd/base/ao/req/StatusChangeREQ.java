package top.flowerstardream.hcd.base.ao.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @Author: 花海
 * @Date: 2025/11/06/21:32
 * @Description: 启用或禁用账号请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "状态更改请求")
public class StatusChangeREQ {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
