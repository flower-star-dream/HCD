package top.flowerstardream.hcd.trainSeat.ao.RES;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 座位预订返回参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "座位预订返回参数")
public class SeatReservationRES {

    @Schema(description = "座位预订号")
    private Long id;

    @Schema(description = "班次id")
    private Long scheduleId;

    @Schema(description = "座位id")
    private Long seatId;

    @Schema(description = "预订状态")
    private Integer bookingStatus;

}
