package top.flowerstardream.hcd.trainSeat.ao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点数据传输对象")
public class stationDTO {

    @Schema(description = "站点号")
    private Long id;

    @Schema(description = "站点名称")
    private String name;

    @Schema(description = "地址")
    private String address;

}
