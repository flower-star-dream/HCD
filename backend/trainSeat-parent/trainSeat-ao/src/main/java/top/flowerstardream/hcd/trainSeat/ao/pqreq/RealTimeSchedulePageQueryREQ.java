package top.flowerstardream.hcd.trainSeat.ao.pqreq;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.ao.req.BasePageQueryREQ;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "实时班次查询请求")

public class RealTimeSchedulePageQueryREQ extends BasePageQueryREQ implements Serializable {

    @Schema(description = "用户出发时间")
    private LocalDateTime nowTime;

    @Schema(description = "用户出发站ID")
    private Long startStationId;

    @Schema(description = "用户到达站ID")
    private Long endStationId;




}
