package top.flowerstardream.hcd.ticket.biz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.ticket.ao.dto.ReserveSeatDTO;
import top.flowerstardream.hcd.ticket.ao.dto.ReserveSeatResultDTO;
import top.flowerstardream.hcd.ticket.ao.dto.SeatReservationDTO;
import top.flowerstardream.hcd.ticket.ao.dto.StationsDTO;
import top.flowerstardream.hcd.tools.result.Result;

import java.awt.image.RenderedImage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 列车座位服务Feign客户端
 */
@FeignClient(name = "hcd-trainSeat")
@RequestMapping("/api/v1/internal/trainSeat")
public interface TrainSeatClient {

    /**
     * 预订座位
     * @param reserveSeatDTO 预订座位参数
     * @return 座位预订号
     */
    @PostMapping("/seatReservation/reserve")
    Result<ReserveSeatResultDTO> reserveSeat(@RequestBody ReserveSeatDTO reserveSeatDTO);

    /**
     * 释放座位
     * @param seatReservationIds 座位预订号列表
     * @return 是否成功
     */
    @PostMapping("/seatReservation/release")
    Result<Void> releaseSeat(@RequestParam("seatReservationIds") List<Long> seatReservationIds);

    /**
     * 查询余票数量
     * @param scheduleId 班次ID
     * @return 余票数量
     */
    @GetMapping("/trainSeat/remaining-count")
    Result<Integer> getRemainingTicketCount(@RequestParam("scheduleId") Integer scheduleId);

    /**
     * 计算车票价格
     * @param reserveSeatDTO
     * @return
     */
    @GetMapping("/routeStations/calc")
    Result<BigDecimal> calcTicketPrice(@RequestBody ReserveSeatDTO reserveSeatDTO);

    /**
     * 根据站名获取站ID
     * @param stationName
     * @return
     */
    @GetMapping("/stations/by-name")
    Result<List<Long>> getStationIdsByName(@RequestParam("stationName") String stationName);

    /**
     * 根据座位预订ID获取座位预订号
     * @param seatReservationIds
     * @return
     */
    @PostMapping("/seatReservation/by-ids")
    Result<List<SeatReservationDTO>> getSeatReservationByIds(List<Long> seatReservationIds);

    /**
     * 根据站ID获取站名
     * @param stationIds
     * @return
     */
    @PostMapping("/stations/by-ids")
    Result<List<StationsDTO>> getStationNamesByStationIds(List<Long> stationIds);
}