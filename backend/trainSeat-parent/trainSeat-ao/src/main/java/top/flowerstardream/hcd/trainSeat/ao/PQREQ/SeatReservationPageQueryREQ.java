package top.flowerstardream.hcd.trainSeat.ao.PQREQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import top.flowerstardream.hcd.base.ao.req.BasePageQueryREQ;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "座位预约请求")
public class SeatReservationPageQueryREQ extends BasePageQueryREQ {

    @Schema(description= "座位预订号")
    private Long id;

    @Schema(description= "班次id")
    private Long scheduleId;

    @Schema(description= "座位id")
    private Long seatId;

    @Schema(description= "预订状态")
    private Integer bookingStatus;
}
