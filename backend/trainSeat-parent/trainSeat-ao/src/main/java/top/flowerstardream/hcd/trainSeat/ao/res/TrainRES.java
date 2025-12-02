package top.flowerstardream.hcd.trainSeat.ao.res;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.ao.res.BaseMgmtRES;

import java.io.Serializable;

/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 列车返回参数
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "列车返回参数")
public class TrainRES extends BaseMgmtRES implements Serializable {

    @Schema(description = "列车名称")
    private String trainName;

    @Schema(description = "列车型号")
    private String trainModel;

    @Schema(description = "座位数")
    private Integer seatNum;

    @Schema(description = "服务年数")
    private Integer serviceYears;

}
