package top.flowerstardream.hcd.trainSeat.ao.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户数据传输对象
 *
 * @author QAQ
 * @date 2025-11-06
 */
@Data
@Schema(description = "路线数据传输对象")
public class routeDTO {

    @Schema(description = "路线id")
    private Long id;

    @Schema(description = "起点站")
    private String startStation;

    @Schema(description = "终点站")
    private String endStation;

    @Schema(description = "站点数")
    private Integer stationCount;




}
