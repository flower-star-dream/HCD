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
import top.flowerstardream.hcd.bo.eo.RouteEO;
import top.flowerstardream.hcd.bo.eo.ScheduleEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.RoutePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.RouteREQ;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.ScheduleMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IRouteService;

import java.util.List;

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

    @Lazy
    @Resource
    private IRouteServiceImpl self;

    @Resource
    private ScheduleMapper scheduleMapper;

    @Override
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
        boolean insert = self.save(routeEO);
        if (!insert) {
            INSERTION_FAILED.throwException();
        }

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
            List<ScheduleEO> schedules = scheduleMapper.selectList(Wrappers.lambdaQuery());

            if (schedules != null){
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
    public PageResult<RouteEO> PageQuery(RoutePageQueryREQ routePageQueryREQ) {
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

        queryWrapper.like(RouteEO::getRouteName, routePageQueryREQ.getRouteName())
                .like(RouteEO::getStartStation, routePageQueryREQ.getStartStation())
                .like(RouteEO::getEndStation, routePageQueryREQ.getEndStation())
                .like(RouteEO::getStationCount, routePageQueryREQ.getStationCount());


        //执行分页查询
        Page<RouteEO> routePage = routeMapper.selectPage(page, queryWrapper);

        //封装返回结果
        PageResult<RouteEO> pageResult = new PageResult<>();
        pageResult.setTotal(routePage.getTotal());
        pageResult.setRecords(routePage.getRecords());
        return pageResult;
    }





    //查询路线
    private RouteEO getRoute(String routeName) {
        LambdaQueryWrapper<RouteEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(RouteEO::getRouteName, routeName);
        return routeMapper.selectOne(queryWrapper);
    }
    //校验路线是否存在
    private void validateRouteIsExist(String routeName) {

        RouteEO routeEO = getRoute(routeName);
        if (routeEO == null) {
            ROUTE_AlREADY_EXISTS.throwException();
        }
    }
}