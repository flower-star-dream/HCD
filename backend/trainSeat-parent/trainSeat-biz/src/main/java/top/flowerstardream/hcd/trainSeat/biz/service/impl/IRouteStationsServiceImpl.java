package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.flowerstardream.hcd.trainSeat.ao.dto.CalcTicketPriceDTO;
import top.flowerstardream.hcd.trainSeat.ao.dto.ReserveSeatDTO;
import top.flowerstardream.hcd.trainSeat.ao.req.RouteStationsREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RouteStationsRES;
import top.flowerstardream.hcd.trainSeat.biz.mapper.ScheduleMapper;
import top.flowerstardream.hcd.trainSeat.biz.tool.Calculation;
import top.flowerstardream.hcd.trainSeat.bo.RouteStationsEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RouteStationsPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteStationsMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IRouteStationsService;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.trainSeat.constant.Common.PRICE_EACH_STATION;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.*;

@Slf4j
@Service
public class IRouteStationsServiceImpl extends ServiceImpl<RouteStationsMapper, RouteStationsEO> implements IRouteStationsService {

    @Resource
    private RouteStationsMapper routeStationsMapper;

    @Resource
    private ScheduleMapper scheduleMapper;

    @Lazy
    @Resource
    private IRouteStationsServiceImpl self;


    @Override
    public void addRouteStations(RouteStationsREQ routeStationsREQ) {
        //参数校验
        if (routeStationsREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        //判断路线站点存在，存在则中断
        validateRouteStationsIsExist(routeStationsREQ.getRouteId(), routeStationsREQ.getStationId());


        RouteStationsEO routeStationsEO = new RouteStationsEO();
        BeanUtil.copyProperties(routeStationsREQ, routeStationsEO);
        boolean insert = self.save(routeStationsEO);
        if (!insert) {
            INSERTION_FAILED.throwException();
        }
    }


    @Override
    @Transactional
    public void deleteRouteStations(List<Long> ids) {
        if(CollUtil.isEmpty(ids)){
            return;
        }

        //不存在路线站点占用

        //批量删除路线站点
        boolean delete = self.removeByIds(ids);
        if (!delete) {
            DELETION_FAILED.throwException();
        }
    }

    @Override
    public void updateRouteStations(RouteStationsREQ routeStationsREQ) {
        //参数校验
        if (routeStationsREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        RouteStationsEO routeStationsEO = new RouteStationsEO();
        BeanUtil.copyProperties(routeStationsREQ, routeStationsEO);
        boolean update = self.updateById(routeStationsEO);
        if (!update) {
            MODIFICATION_FAILED.throwException();
        }
    }

    @Override
    public PageResult<RouteStationsEO> EmployeePageQuery(RouteStationsPageQueryREQ routeStationsPageQueryREQ) {
        //参数校验
        if (routeStationsPageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //设置分页参数默认值
        if (routeStationsPageQueryREQ.getPage() <= 0) {
            routeStationsPageQueryREQ.setPage(1);
        }
        if (routeStationsPageQueryREQ.getPageSize() <= 0) {
            routeStationsPageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<RouteStationsEO> page = new Page<>(routeStationsPageQueryREQ.getPage(), routeStationsPageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<RouteStationsEO> queryWrapper = Wrappers.lambdaQuery();

        //查询条件
        queryWrapper.eq(RouteStationsEO::getRouteId, routeStationsPageQueryREQ.getRouteId())
                .eq(RouteStationsEO::getStationId, routeStationsPageQueryREQ.getStationId())
                .like(RouteStationsEO::getStationSorting, routeStationsPageQueryREQ.getStationSorting());

        //执行分页查询
        Page<RouteStationsEO> routeStationsResult = routeStationsMapper.selectPage(page, queryWrapper);

        //封装返回结果
        PageResult<RouteStationsEO> pageResult = new PageResult<>();
        pageResult.setTotal(routeStationsResult.getTotal());
        pageResult.setRecords(routeStationsResult.getRecords());
        return pageResult;

    }

    @Override
    public PageResult<RouteStationsRES> UserPageQuery(RouteStationsPageQueryREQ routeStationsPageQueryREQ) {
        //参数校验
        if (routeStationsPageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        // 设置分页参数默认值
        if (routeStationsPageQueryREQ.getPage() <= 0) {
            routeStationsPageQueryREQ.setPage(1);
        }
        if (routeStationsPageQueryREQ.getPageSize() <= 0) {
            routeStationsPageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<RouteStationsEO> page = new Page<>(routeStationsPageQueryREQ.getPage(), routeStationsPageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<RouteStationsEO> queryWrapper = Wrappers.lambdaQuery();

        //查询条件
        queryWrapper.eq(RouteStationsEO::getRouteId, routeStationsPageQueryREQ.getRouteId())
                .eq(RouteStationsEO::getStationId, routeStationsPageQueryREQ.getStationId())
                .like(RouteStationsEO::getStationSorting, routeStationsPageQueryREQ.getStationSorting());


        //执行分页查询
        Page<RouteStationsEO> routeStationsPage = routeStationsMapper.selectPage(page, queryWrapper);

        //将EO转换为RES
        List<RouteStationsRES> resList = routeStationsPage.getRecords().stream()
                .map(eo -> {
                    RouteStationsRES res = new RouteStationsRES();
                    BeanUtil.copyProperties(eo, res);
                    return res;
                })
                .collect(Collectors.toList());

        //封装返回结果
        PageResult<RouteStationsRES> pageResult = new PageResult<>();
        pageResult.setTotal(routeStationsPage.getTotal());
        pageResult.setRecords(resList);
        return pageResult ;
    }

    /*
    * 查询路线站点
    * 校验路线站点是否存在
    * add方法在使用
    */
    private RouteStationsEO getRouteStationsEO(Long routeId, Long stationId) {
        LambdaQueryWrapper<RouteStationsEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RouteStationsEO::getRouteId, routeId)
                .eq(RouteStationsEO::getStationId, stationId);
        return routeStationsMapper.selectOne(queryWrapper);
    }

    private void validateRouteStationsIsExist(Long routeId, Long stationId) {

        RouteStationsEO routeStationsEO = getRouteStationsEO(routeId, stationId);
        if(routeStationsEO != null){
            ROUTE_STATIONS_ALREADY_EXISTS.throwException();
        }
    }

    /**
     * 外部调用
    * */
    public BigDecimal calcTicketPrice(CalcTicketPriceDTO calcTicketPriceDTO) {
        //参数校验
        if (calcTicketPriceDTO == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        Calculation calculation = new Calculation();

        return calculation.ticketPriceCalculation(calcTicketPriceDTO);
    }
    /**
     * 外部调用
     * */
    public BigDecimal calcTicketPrice(ReserveSeatDTO reserveSeatDTO){
        //参数校验
        if (reserveSeatDTO == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //Calculation的票价计算传入参数是CalcTicketPriceDTO，俩DTO参数一致，所以直接调用
        CalcTicketPriceDTO calcTicketPriceDTO = new CalcTicketPriceDTO(
                reserveSeatDTO.getScheduleId(),
                reserveSeatDTO.getStartStationId(),
                reserveSeatDTO.getEndStationId()
        );
        Calculation calculation = new Calculation();
        return calculation.ticketPriceCalculation(calcTicketPriceDTO);
    }

}
