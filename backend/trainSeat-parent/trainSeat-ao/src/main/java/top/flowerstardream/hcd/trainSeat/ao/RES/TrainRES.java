package top.flowerstardream.hcd.trainSeat.ao.RES;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 列车返回参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "列车返回参数")
public class TrainRES {

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
