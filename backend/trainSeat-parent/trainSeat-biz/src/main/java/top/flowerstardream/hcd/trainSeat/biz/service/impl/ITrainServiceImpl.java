package top.flowerstardream.hcd.trainSeat.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import top.flowerstardream.hcd.bo.eo.TrainEO;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.trainSeat.ao.PQREQ.TrainPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.REQ.TrainREQ;
import top.flowerstardream.hcd.trainSeat.ao.RES.TrainRES;
import top.flowerstardream.hcd.trainSeat.biz.mapper.TrainMapper;
import top.flowerstardream.hcd.trainSeat.biz.service.ITrainService;

import java.util.List;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.THE_QUERY_PARAMETER_CANNOT_BE_EMPTY;

public class ITrainServiceImpl extends ServiceImpl<TrainMapper, TrainEO> implements ITrainService {

    @Lazy
    @Resource
    private ITrainServiceImpl self;

    @Resource
    private TrainMapper trainMapper;


    @Override
    public void addTrain(TrainREQ trainREQ) {

    }

    @Override
    public void deleteTrain(List<Long> ids) {

    }

    @Override
    public void updateTrain(TrainREQ trainREQ) {

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
        return null;
    }
}
