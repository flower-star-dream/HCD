package top.flowerstardream.hcd.trainSeat.ao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "列车数据传输对象")
public class trainDTO {

    @Schema(description = "列车号")
    private Long id;

    @Schema(description = "列车型号")
    private String trainModel;

    @Schema(description = "座位数")
    private Integer seatCount;

    @Schema(description = "服务年数")
    private Integer serviceYears;
}
