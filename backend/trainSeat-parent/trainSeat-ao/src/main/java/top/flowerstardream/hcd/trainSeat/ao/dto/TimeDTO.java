package top.flowerstardream.hcd.trainSeat.ao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "计算时间返回参数")
public class TimeDTO{
    private LocalDateTime startStationTime;
    private LocalDateTime endStationTime;
}