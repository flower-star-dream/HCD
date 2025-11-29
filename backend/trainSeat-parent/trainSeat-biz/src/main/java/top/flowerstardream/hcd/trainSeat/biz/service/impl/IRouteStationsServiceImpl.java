package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.ScheduleMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.StationMapper;
import top.flowerstardream.hcd.trainSeat.biz.tool.Calculation;
import top.flowerstardream.hcd.trainSeat.bo.RouteEO;
import top.flowerstardream.hcd.trainSeat.bo.RouteStationsEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RouteStationsPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteStationsMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IRouteStationsService;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.trainSeat.bo.StationEO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    private RouteMapper routeMapper;

    @Resource
    private StationMapper stationMapper;

    @Resource
    private Calculation calculation;

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
    public PageResult<RouteStationsRES> EmployeePageQuery(RouteStationsPageQueryREQ routeStationsPageQueryREQ) {
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
        if (StrUtil.isNotBlank(routeStationsPageQueryREQ.getRouteName())) {
            LambdaQueryWrapper<RouteEO> routeQueryWrapper = Wrappers.lambdaQuery();
            routeQueryWrapper.like(RouteEO::getRouteName, routeStationsPageQueryREQ.getRouteName());
            List<Long> routeIds = routeMapper.selectList(routeQueryWrapper).stream().map(RouteEO::getId).toList();
            queryWrapper.in(RouteStationsEO::getRouteId, routeIds);
        }
        if (StrUtil.isNotBlank(routeStationsPageQueryREQ.getStationName())) {
            // 查询站点名
            LambdaQueryWrapper<StationEO> stationQueryWrapper = Wrappers.lambdaQuery();
            stationQueryWrapper.like(StationEO::getStationName, routeStationsPageQueryREQ.getStationName());
            List<Long> stationIds = stationMapper.selectList(stationQueryWrapper).stream().map(StationEO::getId).toList();
            queryWrapper.in(RouteStationsEO::getStationId, stationIds);
        }
        if (routeStationsPageQueryREQ.getStationSorting() != null) {
            queryWrapper.like(RouteStationsEO::getStationSorting, routeStationsPageQueryREQ.getStationSorting());
        }

        //执行分页查询
        Page<RouteStationsEO> routeStationsResult = routeStationsMapper.selectPage(page, queryWrapper);

        List<RouteStationsEO> records = routeStationsResult.getRecords();
        // 提取所有路线ID和站点ID
        List<Long> recordsRouteIds = records.stream()
                .map(RouteStationsEO::getRouteId)
                .distinct()
                .toList();
        List<Long> recordsStationIds = records.stream()
                .map(RouteStationsEO::getStationId)
                .distinct()
                .toList();

        // 批量查询路线和站点信息
        List<RouteEO> routeList = recordsRouteIds.isEmpty() ? CollUtil.newArrayList() : routeMapper.selectBatchIds(recordsRouteIds);
        List<StationEO> stationList = recordsStationIds.isEmpty() ? CollUtil.newArrayList() : stationMapper.selectBatchIds(recordsStationIds);

        // 构建Map便于快速查找
        Map<Long, String> routeNameMap = routeList.stream()
                .collect(Collectors.toMap(RouteEO::getId, RouteEO::getRouteName));
        Map<Long, String> stationNameMap = stationList.stream()
                .collect(Collectors.toMap(StationEO::getId, StationEO::getStationName));

        // 转换为RES对象并设置名称属性
        List<RouteStationsRES> routeStationsRESList = records.stream()
            .map(record -> convertToRES(record, routeNameMap, stationNameMap))
            .toList();
        //封装返回结果
        PageResult<RouteStationsRES> pageResult = new PageResult<>();
        pageResult.setTotal(routeStationsResult.getTotal());
        pageResult.setRecords(routeStationsRESList);
        return pageResult;

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

        return calculation.ticketPriceCalculation(calcTicketPriceDTO);
    }

    private RouteStationsRES convertToRES(RouteStationsEO routeStationsEO,
                                     Map<Long, String> routeNameMap,
                                     Map<Long, String> stationNameMap) {
        RouteStationsRES res = new RouteStationsRES();
        BeanUtil.copyProperties(routeStationsEO, res);
        res.setRouteName(routeNameMap.get(routeStationsEO.getRouteId()));
        res.setStationName(stationNameMap.get(routeStationsEO.getStationId()));
        return res;
    }

}
