package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import top.flowerstardream.hcd.bo.eo.RouteStationsEO;
import top.flowerstardream.hcd.bo.eo.ScheduleEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.RouteStationsPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.SchedulePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.RouteStationsREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.RouteStationsRES;
import top.flowerstardream.hcd.trainSeat.ao.RES.ScheduleRES;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteStationsMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IRouteStationsService;
import java.util.List;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.INSERTION_FAILED;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.THE_QUERY_PARAMETER_CANNOT_BE_EMPTY;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.ROUTESTATIONS_ALREADY_EXISTS;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.SCHEDULE_ALREADY_EXISTS;

@Slf4j
@Service
public class IRouteStationsServiceImpl extends ServiceImpl<RouteStationsMapper, RouteStationsEO> implements IRouteStationsService {

    @Resource
    private RouteStationsMapper routeStationsMapper;

    @Lazy
    @Resource
    private IRouteStationsServiceImpl self;

    @Resource



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
    public void deleteRouteStations(List<Long> ids) {

    }

    @Override
    public void updateRouteStations(RouteStationsREQ routeStationsREQ) {

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

        queryWrapper.like(RouteStationsEO::getId, routeStationsPageQueryREQ.getId())
                .like(RouteStationsEO::getRouteId, routeStationsPageQueryREQ.getRouteId())
                .like(RouteStationsEO::getStationId, routeStationsPageQueryREQ.getStationId())
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

        queryWrapper.like(RouteStationsEO::getId, routeStationsPageQueryREQ.getId())
                .like(RouteStationsEO::getRouteId, routeStationsPageQueryREQ.getRouteId())
                .like(RouteStationsEO::getStationId, routeStationsPageQueryREQ.getStationId())
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

    private RouteStationsEO getRouteStationsEO(Long routeId, Long stationId) {
        LambdaQueryWrapper<RouteStationsEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RouteStationsEO::getRouteId, routeId)
                .eq(RouteStationsEO::getStationId, stationId);
        return routeStationsMapper.selectOne(queryWrapper);
    }
    private void validateRouteStationsIsExist(Long routeId, Long stationId) {

        RouteStationsEO routeStationsEO = getRouteStationsEO(routeId, stationId);
        if(routeStationsEO != null){
            ROUTESTATIONS_ALREADY_EXISTS.throwException();
        }
    }




}
