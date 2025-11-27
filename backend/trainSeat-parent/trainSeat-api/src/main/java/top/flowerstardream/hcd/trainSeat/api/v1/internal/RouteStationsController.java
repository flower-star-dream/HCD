package top.flowerstardream.hcd.trainSeat.api.v1.internal;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.dto.CalcTicketPriceDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.ReserveSeatDTO;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.IRouteStationsServiceImpl;

import java.math.BigDecimal;

@RestController("internalRouteStationsController")
@RequestMapping("/api/v1/internal/routeStations")
@Tag(name = "路线站点接口服务")
@Slf4j
public class RouteStationsController {

    @Resource
    private IRouteStationsServiceImpl routeStationsServiceImpl;

    /**
     * 计算车票价格
     * @param calcTicketPriceDTO
     * @return
     */
    @GetMapping("/routeStations/calc")
    public Result<BigDecimal> calcTicketPrice(@RequestBody CalcTicketPriceDTO calcTicketPriceDTO){
        log.info("【路线站点接口服务】计算车票价格，参数: {}", calcTicketPriceDTO);
        BigDecimal price = routeStationsServiceImpl.calcTicketPrice(calcTicketPriceDTO);
        Result<BigDecimal> result = new Result<>();
        result.setData(price);
        return result;
    };

    /**
     * 计算车票价格
     * @param reserveSeatDTO
     * @return
     */
    @GetMapping("/routeStations/calc")
    public Result<BigDecimal> calcTicketPrice(@RequestBody ReserveSeatDTO reserveSeatDTO){
        log.info("【路线站点接口服务】计算车票价格，参数: {}", reserveSeatDTO);
        BigDecimal price = routeStationsServiceImpl.calcTicketPrice(reserveSeatDTO);
        Result<BigDecimal> result = new Result<>();
        result.setData(price);
        return result;
    };
}
