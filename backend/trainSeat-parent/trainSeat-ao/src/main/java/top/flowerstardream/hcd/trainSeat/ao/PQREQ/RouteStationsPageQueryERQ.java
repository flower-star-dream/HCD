package top.flowerstardream.hcd.trainSeat.ao.PQREQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.ao.req.BasePageQueryREQ;

/**
 * @Author: QAQ
 * @Date: 2025/11/11/23:00
 * @Description: 路线站点分页查询请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "路线站点分页查询请求")
public class RouteStationsPageQueryERQ extends BasePageQueryREQ {

    @Schema(description = "路线站点号")
    private Long id;

    @Schema(description = "路线号")
    private Long routeId;

    @Schema(description = "站点号")
    private Long stationId;

    @Schema(description = "站点排序")
    private Integer stationSorting;

}
