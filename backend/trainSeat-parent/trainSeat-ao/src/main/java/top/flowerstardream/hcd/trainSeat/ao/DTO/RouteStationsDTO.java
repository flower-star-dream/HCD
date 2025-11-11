package top.flowerstardream.hcd.trainSeat.ao.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 路线站点数据传输对象
 */
@Data
@Schema(description = "路线站点数据传输对象")
public class RouteStationsDTO {

    @Schema(description = "路线站点号")
    private Long id;

    @Schema(description = "路线号")
    private Long routeId;

    @Schema(description = "站点号")
    private Long stationId;

    @Schema(description = "站点排序")
    private Integer stationSorting;


}

