package top.flowerstardream.hcd.trainSeat.ao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 列车数据传输对象
 */
@Data
@Schema(description = "列车数据传输对象")
public class TrainDTO {

    @Schema(description = "列车号")
    private Long id;

    @Schema(description = "列车名称")
    private String trainName;

    @Schema(description = "列车型号")
    private String trainModel;

    @Schema(description = "座位数")
    private Integer seatCount;

    @Schema(description = "服务年数")
    private Integer serviceYears;
}
