package top.flowerstardream.hcd.trainSeat.ao.pqreq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.ao.req.BasePageQueryREQ;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "列车分页查询请求")
public class TrainPageQueryREQ extends BasePageQueryREQ implements Serializable {

    @Schema(description = "列车号")
    private Long id;

    @Schema(description = "列车名")
    private String trainName;

    @Schema(description = "列车型号")
    private String trainModel;

    @Schema(description = "服务年数")
    private Integer serviceYears;
}
