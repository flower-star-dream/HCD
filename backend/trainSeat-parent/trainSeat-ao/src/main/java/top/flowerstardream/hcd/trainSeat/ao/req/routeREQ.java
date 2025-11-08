package top.flowerstardream.hcd.trainSeat.ao.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: QAQ
 * @Date: 2025/11/06/23:00
 * @Description: 路线请求
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "路线请求")
public class routeREQ {

    @Schema(description = "路线号")
    private Long id;

    @Schema(description = "起点站")
    private String startingStation;

    @Schema(description = "终点站")
    private String endStation;


}
