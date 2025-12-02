package top.flowerstardream.hcd.base.ao.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: 花海
 * @Date: 2025/12/01/09:26
 * @Description: 状态统计返回结果
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "基础状态统计响应")
public class StatusRES implements Serializable {

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "数量")
    private Integer count;

    @Schema(description = "描述")
    private String description;
}
