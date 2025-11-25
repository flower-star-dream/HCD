package top.flowerstardream.hcd.trainSeat.ao.REQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 班次请求
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "班次请求")
public class ScheduleREQ {

    @Schema(description = "班次号")
    private Long id;

    @Schema(description = "列车号")
    private Long trainId;

    @Schema(description = "路线号")
    private Long routeId;

    @Schema(description = "列车长")
    private String conductor;

    @Schema(description = "出发时间")
    private String startTime;

    @Schema(description = "到达时间")
    private String endTime;
}
