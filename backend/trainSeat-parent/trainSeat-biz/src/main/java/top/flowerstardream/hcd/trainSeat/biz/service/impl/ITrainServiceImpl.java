package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import top.flowerstardream.hcd.trainSeat.ao.req.TrainREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.TrainRES;
import top.flowerstardream.hcd.trainSeat.bo.ScheduleEO;
import top.flowerstardream.hcd.trainSeat.bo.TrainEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.TrainPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.biz.mapper.ScheduleMapper;
import top.flowerstardream.hcd.trainSeat.biz.mapper.TrainMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.ITrainService;

import java.util.List;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.TRAIN_ALREADY_EXISTS;
import static top.flowerstardream.hcd.trainSeat.constant.TrainSeatExceptionEnum.TRAIN_IS_USED;

public class ITrainServiceImpl extends ServiceImpl<TrainMapper, TrainEO> implements ITrainService {

    @Lazy
    @Resource
    private ITrainServiceImpl self;

    @Resource
    private TrainMapper trainMapper;

    @Resource
    private ScheduleMapper scheduleMapper;


    @Override
    public void addTrain(TrainREQ trainREQ) {
        //参数校验
        if (trainREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //查询列车是否存在
        validateTrainIsExist(trainREQ.getTrainName());

        //打包req的属性进入EO
        TrainEO trainEO = new TrainEO();
        BeanUtil.copyProperties(trainREQ, trainEO);
        boolean insert = self.save(trainEO);
        if (!insert) {
            INSERTION_FAILED.throwException();
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTrain(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        //排查有无使用该路线
        ids.forEach(id -> {
            //获取列车信息
            TrainEO trainEO = self.getById(id);
            if (trainEO == null) {
                return;
            }

            LambdaQueryWrapper<ScheduleEO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(ScheduleEO::getTrainId,id);
            List<ScheduleEO> schedules = scheduleMapper.selectList(Wrappers.lambdaQuery());

            if (schedules != null){
                TRAIN_IS_USED.throwException();
            }
        });


    }

    @Override
    public void updateTrain(TrainREQ trainREQ) {
        //参数校验
        if (trainREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        TrainEO trainEO = new TrainEO();
        BeanUtil.copyProperties(trainREQ, trainEO);
        boolean update = self.updateById(trainEO);
        if (!update) {
            MODIFICATION_FAILED.throwException();
        }

    }

    @Override
    public PageResult<TrainEO> EmployeePageQuery(TrainPageQueryREQ trainPageQueryREQ) {
        //参数校验
        if (trainPageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //设置分页参数默认值
        if (trainPageQueryREQ.getPage() <= 0){
            trainPageQueryREQ.setPage(1);
        }
        if(trainPageQueryREQ.getPageSize() <= 0){
            trainPageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<TrainEO> page = new Page<>(trainPageQueryREQ.getPage(), trainPageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<TrainEO> queryWrapper = new LambdaQueryWrapper<>();

        //查询条件
        queryWrapper.eq(TrainEO::getTrainName, trainPageQueryREQ.getTrainName())
                .like(TrainEO::getTrainModel, trainPageQueryREQ.getTrainModel())
                .eq(TrainEO::getSeatNum, trainPageQueryREQ.getSeatNum())
                .eq(TrainEO::getServiceYears, trainPageQueryREQ.getServiceYears());

        //执行分页查询
        Page<TrainEO> trainResult = trainMapper.selectPage(page, queryWrapper);

        //封装返回结果
        PageResult<TrainEO> pageResult = new PageResult<>();
        pageResult.setTotal(trainResult.getTotal());
        pageResult.setRecords(trainResult.getRecords());
        return pageResult;
    }

    @Override
    public PageResult<TrainRES> UserPageQuery(TrainPageQueryREQ trainPageQueryREQ) {
        //参数校验
        if (trainPageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        //设置分页参数默认值
        if (trainPageQueryREQ.getPage() <= 0){
            trainPageQueryREQ.setPage(1);
        }
        if(trainPageQueryREQ.getPageSize() <= 0){
            trainPageQueryREQ.setPageSize(10);
        }

        //创建分页对象
        Page<TrainEO> page = new Page<>(trainPageQueryREQ.getPage(), trainPageQueryREQ.getPageSize());
        //创建查询条件
        LambdaQueryWrapper<TrainEO> queryWrapper = new LambdaQueryWrapper<>();

        //查询条件
        queryWrapper.like(TrainEO::getTrainName, trainPageQueryREQ.getTrainName())
                .like(TrainEO::getTrainModel, trainPageQueryREQ.getTrainModel())
                .eq(TrainEO::getSeatNum, trainPageQueryREQ.getSeatNum())
                .eq(TrainEO::getServiceYears, trainPageQueryREQ.getServiceYears());

        //执行分页查询
        Page<TrainEO> trainPage = trainMapper.selectPage(page, queryWrapper);

        //将EO转换为RES
        List<TrainRES> trainList = trainPage.getRecords().stream()
                .map(trainEO -> {
                    TrainRES res = new TrainRES();
                    BeanUtil.copyProperties(trainEO, res);
                    return res;
                })
                .collect(Collectors.toList());

        //封装返回结果
        PageResult<TrainRES> pageResult = new PageResult<>();
        pageResult.setTotal(trainPage.getTotal());
        pageResult.setRecords(trainList);
        return pageResult;
    }

    /*
    * 查询列车
    * 校验列车是否存在
    * add方法在使用
    * */

    private TrainEO getTrain(String trainName){
        LambdaQueryWrapper<TrainEO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainEO::getTrainName, trainName);
        return trainMapper.selectOne(queryWrapper);
    }
    private void validateTrainIsExist(String trainName){
        TrainEO train = getTrain(trainName);
        if (train != null){
            TRAIN_ALREADY_EXISTS.throwException();
        }
    }
}
