package top.flowerstardream.hcd.trainSeat.ao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * @Author: QAQ
 * @Date: 2025/11/09/23:00
 * @Description: 站点数据传输对象
 */
@Data
@Schema(description = "站点数据传输对象")
public class StationDTO {

    @Schema(description = "站点号")
    private Long id;

    @Schema(description = "站点名称")
    private String stationName;

    @Schema(description = "地址")
    private String address;

}
