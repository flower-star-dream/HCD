package top.flowerstardream.hcd.trainSeat.ao.RES;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 路线返回参数
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "路线返回参数")
public class RouteRES {

    @Schema(description = "路线号")
    private Long id;

    @Schema(description = "路线名")
    private String routeName;

    @Schema(description = "起始站")
    private String startStation;

    @Schema(description = "终点站")
    private String endStation;

}
