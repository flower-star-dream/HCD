package top.flowerstardream.hcd.trainSeat.api.v1.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.SeatReservationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.SeatReservationRES;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.ISeatReservationServiceImpl;

@RestController("appSeatReservationController")
@RequestMapping("/api/v1/app/trainSeat/seatReservation")
@Tag(name = "小程序端-座位预订管理")
@Slf4j
public class AppSeatReservationController {

    @Resource
    private ISeatReservationServiceImpl seatReservationServiceImpl;

    public Result<PageResult<SeatReservationRES>> userPageQuery(SeatReservationPageQueryREQ seatReservationPageQueryREQ) {
        log.info("【小程序端-座位预订服务】查询座位预订，参数: {}", seatReservationPageQueryREQ);
        PageResult<SeatReservationRES> result = seatReservationServiceImpl.UserPageQuery(seatReservationPageQueryREQ);
        return Result.successResult(result);
    }
}
