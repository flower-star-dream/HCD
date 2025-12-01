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
import top.flowerstardream.hcd.trainSeat.ao.req.RouteREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.RouteStationsREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.RouteRES;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteStationsMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.StationMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IRouteStationsService;
import top.flowerstardream.hcd.trainSeat.bo.RouteEO;
import top.flowerstardream.hcd.trainSeat.bo.RouteStationsEO;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.RoutePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.ScheduleMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IRouteService;
import top.flowerstardream.hcd.trainSeat.bo.StationEO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.base.constant.CommonConstant.PAGE_TOTAL;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.ROUTE_AlREADY_EXISTS;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.ROUTE_IS_USED;

/**
 * @Author: QAQ
 * @Date: 2025/11/10 16:01
 * @Description: 路线服务实现类
 */

@Slf4j
@Service
public class IRouteServiceImpl extends ServiceImpl<RouteMapper, RouteEO> implements IRouteService {

    @Resource
    private RouteMapper routeMapper;

    @Resource
    private StationMapper stationMapper;

    @Resource
    private IRouteStationsService routeStationsService;

    @Lazy
    @Resource
    private IRouteServiceImpl self;

    @Resource
    private ScheduleMapper scheduleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoute(RouteREQ routeREQ) {
        //参数校验
        if (routeREQ == null) {
            //判断REQ是否存在
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        //判断路线存在,存在则中断
        validateRouteIsExist(routeREQ.getRouteName());

        //打包req的属性进入EO，然后插入数据库
        RouteEO routeEO = new RouteEO();
        BeanUtil.copyProperties(routeREQ, routeEO);
        routeEO.setStationCount(2);
        boolean insert = self.save(routeEO);
        if (!insert) {
            INSERTION_FAILED.throwException();
        }
        RouteStationsREQ startRouteStationsREQ = RouteStationsREQ.builder()
                .routeId(routeEO.getId())
                .stationId(routeEO.getStartStationId())
                .stationSorting(1)
                .init(true)
                .build();
        RouteStationsREQ endRouteStationsREQ =  RouteStationsREQ.builder()
                .routeId(routeEO.getId())
                .stationId(routeEO.getEndStationId())
                .stationSorting(2)
                .init(true)
                .build();
        routeStationsService.addRouteStations(startRouteStationsREQ);
        routeStationsService.addRouteStations(endRouteStationsREQ);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoute(List<Long> ids) {
        if(CollUtil.isEmpty(ids)){
            return;
        }
        //排查有无使用该路线
        ids.forEach(id -> {
            //获取路线信息
            RouteEO routeEO = self.getById(id);
            if (routeEO == null) {
                return;
            }

            LambdaQueryWrapper<ScheduleEO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(ScheduleEO::getRouteId,id);
            List<ScheduleEO> schedules = scheduleMapper.selectList(queryWrapper);

            if (CollUtil.isNotEmpty(schedules)){
                ROUTE_IS_USED.throwException();
            }
        });
        //批量删除员工
        boolean delete = self.removeByIds(ids);
        if (!delete) {
            DELETION_FAILED.throwException();
        }
    }

    @Override
    public void updateRoute(RouteREQ routeREQ) {
        //参数校验
        if (routeREQ == null) {
            //判断REQ是否存在
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        RouteEO routeEO = new RouteEO();
        BeanUtil.copyProperties(routeREQ, routeEO);
        boolean update = self.updateById(routeEO);
        if (!update) {
            MODIFICATION_FAILED.throwException();
        }
    }

    @Override
    public PageResult<RouteRES> EmployeePageQuery(RoutePageQueryREQ routePageQueryREQ) {
        //参数校验
        if (routePageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        // 设置分页参数默认值
        if (routePageQueryREQ.getPage() <= 0) {
            routePageQueryREQ.setPage(1);
        }
        if (routePageQueryREQ.getPageSize() <= 0) {
            routePageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<RouteEO> page = new Page<>(routePageQueryREQ.getPage(), routePageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<RouteEO> queryWrapper = Wrappers.lambdaQuery();

        //模糊查询
        if (routePageQueryREQ.getId() != null) {
            queryWrapper.eq(RouteEO::getId, routePageQueryREQ.getId());
        }
        if (StrUtil.isNotBlank(routePageQueryREQ.getRouteName())) {
            queryWrapper.like(RouteEO::getRouteName, routePageQueryREQ.getRouteName());
        }
        if (routePageQueryREQ.getStartStationId() != null) {
            queryWrapper.eq(RouteEO::getStartStationId, routePageQueryREQ.getStartStationId());
        }
        if (routePageQueryREQ.getEndStationId() != null) {
            queryWrapper.eq(RouteEO::getEndStationId, routePageQueryREQ.getEndStationId());
        }

        //执行分页查询
        Page<RouteEO> routePage = routeMapper.selectPage(page, queryWrapper);
        List<RouteEO> records = routePage.getRecords();
        // 提取所有起始站点ID和终点站点ID
        List<Long> startStationIds = records.stream()
                .map(RouteEO::getStartStationId)
                .distinct()
                .toList();
        List<Long> endStationIds = records.stream()
                .map(RouteEO::getEndStationId)
                .distinct()
                .toList();


        List<StationEO> startStations = CollUtil.isEmpty(startStationIds) ? Collections.emptyList() : stationMapper.selectBatchIds(startStationIds);
        List<StationEO> endStations = CollUtil.isEmpty(endStationIds) ? Collections.emptyList() : stationMapper.selectBatchIds(endStationIds);
        Map<Long, String> startStationMap = startStations.stream()
                .collect(Collectors.toMap(StationEO::getId, StationEO::getStationName));
        Map<Long, String> endStationMap = endStations.stream()
                .collect(Collectors.toMap(StationEO::getId, StationEO::getStationName));

        //封装返回结果
        PageResult<RouteRES> pageResult = new PageResult<>();
        Long total = routeMapper.selectCount(Wrappers.lambdaQuery(RouteEO.class));
        pageResult.setTotal(total > PAGE_TOTAL ? PAGE_TOTAL : total);
        pageResult.setRecords(routePage.getRecords().stream().map(eo -> {
            RouteRES res = new RouteRES();
            BeanUtil.copyProperties(eo, res);
            res.setStartStationName(startStationMap.get(eo.getStartStationId()));
            res.setEndStationName(endStationMap.get(eo.getEndStationId()));
            return res;
        }).toList());
        return pageResult;
    }

    /*
    * 查询路线
    * 校验路线是否存在
    * add方法使用
    */
    private RouteEO getRoute(String routeName) {
        LambdaQueryWrapper<RouteEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(RouteEO::getRouteName, routeName);
        return routeMapper.selectOne(queryWrapper);
    }
    private void validateRouteIsExist(String routeName) {

        RouteEO routeEO = getRoute(routeName);
        if (routeEO != null) {
            ROUTE_AlREADY_EXISTS.throwException();
        }
    }
}