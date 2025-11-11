package top.flowerstardream.hcd.trainSeat.ao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 班次数据传输对象
 */
@Data
@Schema(description = "班次数据传输对象")
public class ScheduleDTO {

    @Schema(description = "班次号")
    private Long id;

    @Schema(description = "列车号")
    private Long trainId;

    @Schema(description = "路线号")
    private Long routeId;

    @Schema(description = "列车长")
    private String conductor;




}
