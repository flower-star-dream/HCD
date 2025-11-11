package top.flowerstardream.hcd.trainSeat.ao.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户数据传输对象
 *
 * @author QAQ
 * @date 2025-11-06
 */
@Data
@Schema(description = "路线数据传输对象")
public class RouteDTO {

    @Schema(description = "路线id")
    private Long id;

    @Schema(description = "路线名")
    private String routeName;

    @Schema(description = "起点站")
    private String startStation;

    @Schema(description = "终点站")
    private String endStation;

    @Schema(description = "站点数")
    private Integer stationCount;




}
