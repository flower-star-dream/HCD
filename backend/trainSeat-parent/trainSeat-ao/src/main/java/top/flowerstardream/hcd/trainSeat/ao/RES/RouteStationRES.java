package top.flowerstardream.hcd.trainSeat.ao.RES;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 路线站点返回参数
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "路线站点返回参数")
public class RouteStationRES {

    @Schema(description = "路线站点号")
    private Long id;

    @Schema(description = "路线号")
    private Long routeId;

    @Schema(description = "站点号")
    private Long stationId;

    @Schema(description = "站点排序")
    private Integer stationSorting;
}
