package top.flowerstardream.hcd.trainSeat.ao.PQREQ;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import top.flowerstardream.hcd.base.ao.req.BasePageQueryREQ;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "班次分页查询请求")
public class SchedulePageQueryREQ extends BasePageQueryREQ {

    @Schema(description = "班次号")
    private Long id;

    @Schema(description = "列车号")
    private Long trainId;

    @Schema(description = "路线号")
    private Long routeId;

    @Schema(description = "列车长")
    private String conductor;

    @TableField("availing_tickets")
    private Integer availingTickets;

    @TableField("start_time")
    private String startTime;

    @TableField("end_time")
    private String endTime;


}
