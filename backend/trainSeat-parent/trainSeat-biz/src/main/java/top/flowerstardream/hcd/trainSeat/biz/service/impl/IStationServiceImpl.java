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
import top.flowerstardream.hcd.trainSeat.ao.dto.StationsDTO;
import top.flowerstardream.hcd.trainSeat.ao.req.StationREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.StationMgmtRES;
import top.flowerstardream.hcd.trainSeat.ao.res.StationRES;
import top.flowerstardream.hcd.trainSeat.bo.RouteStationsEO;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.trainSeat.bo.StationEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.StationPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.mapper.RouteStationsMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.StationMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.IStationService;

import java.util.List;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.base.constant.CommonConstant.PAGE_TOTAL;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.STATION_ALREADY_EXISTS;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.STATION_IS_USED;

@Slf4j
@Service
public class IStationServiceImpl extends ServiceImpl<StationMapper, StationEO> implements IStationService {

    @Resource
    private StationMapper stationMapper;

    @Lazy
    @Resource
    private IStationServiceImpl self;

    @Resource
    private RouteStationsMapper routeStationsMapper;

    @Override
    public void addStation(StationREQ stationREQ) {
        //参数校验
        if (stationREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
            return;
        }

        //判断站点存在，存在则中断
        validateStationIsExist(stationREQ.getStationName());

        StationEO stationEO = new StationEO();
        BeanUtil.copyProperties(stationREQ, stationEO);
        boolean save = self.save(stationEO);
        if (!save) {
            INSERTION_FAILED.throwException();
        }

    }

    @Override
    public void deleteStation(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }

        //排查有无使用站点
        ids.forEach(id -> {
            //获取站点信息
            StationEO stationEO = self.getById(id);
            if (stationEO != null) {
                return;
            }

            LambdaQueryWrapper<RouteStationsEO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(RouteStationsEO::getStationId, id);
            List<RouteStationsEO> routeStations = routeStationsMapper.selectList(queryWrapper);

            if(CollUtil.isNotEmpty(routeStations)){
                STATION_IS_USED.throwException();
            }
        });

        //批量删除站点
        boolean delete = self.removeByIds(ids);
        if (!delete) {
            DELETION_FAILED.throwException();
        }

    }

    @Override
    public void updateStation(StationREQ stationREQ) {
        //参数校验
        if (stationREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        StationEO stationEO = new StationEO();
        BeanUtil.copyProperties(stationREQ, stationEO);
        boolean update = self.updateById(stationEO);
        if (!update) {
            MODIFICATION_FAILED.throwException();
        }
    }

    @Override
    public PageResult<StationMgmtRES> EmployeePageQuery(StationPageQueryREQ stationPageQueryREQ) {
        //参数校验
        if (stationPageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //设置分页参数默认值
        if (stationPageQueryREQ.getPage() <= 0) {
            stationPageQueryREQ.setPage(1);
        }
        if (stationPageQueryREQ.getPageSize() <= 0) {
            stationPageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<StationEO> page = new Page<>(stationPageQueryREQ.getPage(), stationPageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<StationEO> queryWrapper = Wrappers.lambdaQuery();
        //查询条件
        if (stationPageQueryREQ.getId() != null) {
            queryWrapper.eq(StationEO::getId, stationPageQueryREQ.getId());
        }
        if (StrUtil.isNotBlank(stationPageQueryREQ.getStationName())) {
            queryWrapper.like(StationEO::getStationName, stationPageQueryREQ.getStationName());
        }
        if (StrUtil.isNotBlank(stationPageQueryREQ.getAddress())) {
            queryWrapper.like(StationEO::getAddress, stationPageQueryREQ.getAddress());
        }

        //执行分页查询
        Page<StationEO> stationPage = self.page(page, queryWrapper);

        //将EO转换为RES
        List<StationMgmtRES> resList = stationPage.getRecords().stream()
                .map(stationEO -> {
                    StationMgmtRES res = new StationMgmtRES();
                    BeanUtil.copyProperties(stationEO, res);
                    return res;
                }).toList();

        //封装返回结果
        PageResult<StationMgmtRES> pageResult = new PageResult<>();
        Long total = stationMapper.selectCount(Wrappers.lambdaQuery(StationEO.class));
        pageResult.setTotal(total > PAGE_TOTAL ? PAGE_TOTAL : total);
        pageResult.setRecords(resList);
        return pageResult;
    }

    @Override
    public PageResult<StationRES> UserPageQuery(StationPageQueryREQ stationPageQueryREQ) {
        if (stationPageQueryREQ == null){
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        if (stationPageQueryREQ.getPage() <= 0){
            stationPageQueryREQ.setPage(1);
        }
        if (stationPageQueryREQ.getPageSize() <= 0){
            stationPageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<StationEO> page = new Page<>(stationPageQueryREQ.getPage(), stationPageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<StationEO> queryWrapper = Wrappers.lambdaQuery();

        //查询条件
        if (stationPageQueryREQ.getId() != null) {
            queryWrapper.eq(StationEO::getId, stationPageQueryREQ.getId());
        }
        if (StrUtil.isNotBlank(stationPageQueryREQ.getStationName())) {
            queryWrapper.like(StationEO::getStationName, stationPageQueryREQ.getStationName());
        }
        if (StrUtil.isNotBlank(stationPageQueryREQ.getAddress())) {
            queryWrapper.like(StationEO::getAddress, stationPageQueryREQ.getAddress());
        }

        //执行分页查询
        Page<StationEO> stationPage = stationMapper.selectPage(page, queryWrapper);

        //将EO转换为RES
        List<StationRES> resList = stationPage.getRecords().stream()
                .map(stationEO -> {
                    StationRES res = new StationRES();
                    BeanUtil.copyProperties(stationEO, res);
                    return res;
                }).toList();

        //封装返回结果
        PageResult<StationRES> pageResult = new PageResult<>();
        Long total = stationMapper.selectCount(Wrappers.lambdaQuery(StationEO.class));
        pageResult.setTotal(total > PAGE_TOTAL ? PAGE_TOTAL : total);
        pageResult.setRecords(resList);
        return pageResult;
    }

    private StationEO getStationEO(String stationName){
        LambdaQueryWrapper<StationEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StationEO::getStationName, stationName);
        return stationMapper.selectOne(queryWrapper);
    }

    private void validateStationIsExist(String stationName){
        StationEO stationEO = getStationEO(stationName);
        if (stationEO != null) {
            STATION_ALREADY_EXISTS.throwException();
        }
    }



    /**
    * 外部调用
    */
    public List<Long> getStationIdsByName(String stationName) {

        //参数检验
        if (stationName == null || stationName.isEmpty()) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        LambdaQueryWrapper<StationEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StationEO::getStationName, stationName);

        return stationMapper.selectList(queryWrapper)
                .stream().map(StationEO::getId)
                .toList();
    }
    /**
    * 外部调用
    */
    public List<StationsDTO> getStationDTOsByStationIds(List<Long> stationIds){
        //参数检验
        if (stationIds == null || stationIds.isEmpty()) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        LambdaQueryWrapper<StationEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(StationEO::getId, stationIds);
        List<StationEO> stations = stationMapper.selectList(queryWrapper);

        return stations.stream().map(stationEO -> StationsDTO.builder()
                .id(stationEO.getId())
                .name(stationEO.getStationName())
                .build()).toList();

    }

}
