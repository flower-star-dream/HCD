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
@Schema(description = "座位预约请求")
public class SeatReservationPageQueryREQ extends BasePageQueryREQ implements Serializable {

    @Schema(description= "座位预订号")
    private Long id;

    @Schema(description= "班次id")
    private Long scheduleId;

    @Schema(description= "座位号")
    private Integer seatNum;

    @Schema(description= "预订状态")
    private Integer bookingStatus;
}
