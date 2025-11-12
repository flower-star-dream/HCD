package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import kotlin.jvm.internal.Lambda;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import top.flowerstardream.hcd.bo.eo.RouteEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.RoutePageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.RouteREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.RouteRES;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IRouteService;

import java.util.List;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.PARAM_ERROR;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.THE_QUERY_PARAMETER_CANNOT_BE_EMPTY;

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


    @Override
    public void addRoute(RouteREQ routeREQ) {
        //参数校验
        if (routeREQ == null) {
            //判断REQ是否存在
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        //判断是否存在
        RouteEO routeEO = self.getOne(Wrappers.<RouteEO>lambdaQuery()
                .eq(RouteEO::getRouteName, routeREQ.getRouteName()));

    }

    @Override
    public void deleteRoute(List<Long> ids) {

    }

    @Override
    public void updateRoute(RouteREQ routeREQ) {

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

        /* *
        *
         */
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

    private void validateRouteIsNotEmpty(RouteEO routeEO) {
        if (routeEO == null) {
            PARAM_ERROR.throwException();
        }
    }
}