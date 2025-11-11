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
 * @Date: 2025/11/11/15：00
 * @Description: 路线分页查询请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "路线分页查询请求")
public class RoutePageQueryREQ extends BasePageQueryREQ {



    @Schema(description = "路线名")
    private String routeName;

    @Schema(description = "起点站")
    private String startingStation;

    @Schema(description = "终点站")
    private String endStation;
}
