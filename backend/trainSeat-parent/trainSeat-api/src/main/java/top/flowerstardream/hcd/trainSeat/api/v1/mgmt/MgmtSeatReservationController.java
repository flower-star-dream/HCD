package top.flowerstardream.hcd.trainSeat.api.v1.mgmt;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.base.ao.res.StatusRES;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SeatReservationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.SeatReservationREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.SeatReservationRES;
import top.flowerstardream.hcd.trainSeat.biz.service.ISeatReservationService;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.ISeatReservationServiceImpl;
import top.flowerstardream.hcd.trainSeat.bo.SeatReservationEO;

import java.util.List;

@RestController("mgmtSeatReservationController")
@RequestMapping("/api/v1/mgmt/trainSeat/seatReservation")
@Tag(name = "后管端-座位预订管理")
@Slf4j
public class MgmtSeatReservationController {

    @Resource
    private ISeatReservationService seatReservationService;

    @PostMapping("/addSeatReservation")
    public Result<Void> addSeatReservation(@RequestBody SeatReservationREQ seatReservationREQ) {
        log.info("【管理端-座位预订服务】添加座位预订，参数: {}", seatReservationREQ);
        seatReservationService.addSeatReservation(seatReservationREQ);
        return Result.successResult();
    }

    @PutMapping("/updateSeatReservation")
    public Result<Void> updateSeatReservation(@RequestBody SeatReservationREQ seatReservationREQ) {
        log.info("【管理端-座位预订服务】更新座位预订，参数: {}", seatReservationREQ);
        seatReservationService.updateSeatReservation(seatReservationREQ);
        return Result.successResult();
    }

    @DeleteMapping("/deleteSeatReservation")
    public Result<Void> deleteSeatReservation(@RequestBody List<Long> seatReservationIds) {
        log.info("【管理端-座位预订服务】删除座位预订，参数: {}", seatReservationIds);
        seatReservationService.deleteSeatReservation(seatReservationIds);
        return Result.successResult();
    }

    @GetMapping("/getSeatReservation")
    public Result<PageResult<SeatReservationRES>> EmployeePageQuery(SeatReservationPageQueryREQ seatReservationPageQueryREQ) {
        log.info("【管理端-座位预订服务】获取座位预订，参数: {}", seatReservationPageQueryREQ);
        PageResult<SeatReservationRES> pageResult = seatReservationService.EmployeePageQuery(seatReservationPageQueryREQ);
        return Result.successResult(pageResult);
    }

    @GetMapping("/getStatus")
    public Result<List<StatusRES>> getStatus() {
        log.info("【管理端-座位预订服务】获取座位预订状态");
        List<StatusRES> statusRES = seatReservationService.getStatus();
        return Result.successResult(statusRES);
    }
}
