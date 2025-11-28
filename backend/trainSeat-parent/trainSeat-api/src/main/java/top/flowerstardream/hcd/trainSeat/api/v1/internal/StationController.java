package top.flowerstardream.hcd.trainSeat.api.v1.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.dto.StationsDTO;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IStationServiceImpl;

import java.util.List;

/**
 * @Author: QAQ
 * @Date: 2025/11/26 17:19
 * @Description: 站点数据服务接口（供其他服务调用）
 */

@RestController("internalStationController")
@RequestMapping("/api/v1/internal/trainSeat/station")
@Tag(name = "站点数据服务")
@Slf4j
public class StationController {

    @Resource
    private IStationServiceImpl stationServiceImpl;


    @Operation(summary = "根据站名获取站ID", description = "站点数据服务接口，根据站名获取站ID")
    @GetMapping("/stations/by-name")
    public Result<List<Long>> getStationIdsByName(@RequestParam("stationName") String stationName){
        log.info("【站点数据服务】根据站名获取站ID，站名: {}", stationName);
        List<Long> stationIds = stationServiceImpl.getStationIdsByName(stationName);
        return Result.successResult(stationIds);
    }

    /*
     * 功能是用于查站名，但是要求返回的是DTO
     */
    @Operation(summary = "根据站ID获取站名", description = "站点数据服务接口，根据站ID获取站名")
    @GetMapping("/seatReservation/by-ids")
    public Result<List<StationsDTO>> getStationNamesByStationIds( List<Long> stationIds){
        log.info("【站点数据服务】根据站ID获取站名，站ID: {}", stationIds);
        List<StationsDTO> stationDTOs = stationServiceImpl.getStationDTOsByStationIds(stationIds);
        return Result.successResult(stationDTOs);
    }
}
