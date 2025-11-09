package top.flowerstardream.hcd.trainSeat.ao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "座位预订数据传输对象")
public class seatReservationDTO {

    @Schema(description= "座位预订号")
    private Long id;

    @Schema(description= "班次id")
    private Long scheduleId;

    @Schema(description= "座位id")
    private Long seatId;

    @Schema(description= "预订状态")
    private Integer bookingStatus;


}
