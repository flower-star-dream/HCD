package top.flowerstardream.hcd.trainSeat.ao.PQREQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import top.flowerstardream.hcd.base.ao.req.BasePageQueryREQ;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "站点分页查询请求")
public class StationPageQueryREQ extends BasePageQueryREQ {

    @Schema(description = "站点号")
    private Long id;

    @Schema(description = "站点名称")
    private String stationName;

    @Schema(description = "地址")
    private String address;

}
