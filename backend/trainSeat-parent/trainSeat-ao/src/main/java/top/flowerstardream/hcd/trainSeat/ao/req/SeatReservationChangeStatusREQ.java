package top.flowerstardream.hcd.trainSeat.ao.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 花海
 * @Date: 2025/12/02/00:06
 * @Description: 座位预订状态修改请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "座位预订状态修改请求参数")
public class SeatReservationChangeStatusREQ implements Serializable {
    @Schema(description = "座位预订ids")
    private List<Long> ids;

    @Schema(description = "座位预订状态")
    private Integer status;
}
