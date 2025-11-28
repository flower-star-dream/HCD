package top.flowerstardream.hcd.trainSeat.api.v1.mgmt;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.TrainPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.req.TrainREQ;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.ITrainServiceImpl;
import top.flowerstardream.hcd.trainSeat.bo.TrainEO;

import java.util.List;

@RestController("mgmtTrainController")
@RequestMapping("/api/v1/mgmt/trainSeat/train")
@Tag(name = "管理端-列车接口服务")
@Slf4j
public class MgmtTrainController {

    @Resource
    private ITrainServiceImpl trainServiceImpl;

    @PostMapping("/addTrain")
    public Result<Void> addTrain(TrainREQ trainREQ) {
        log.info("【管理端-列车接口】添加列车，参数: {}", trainREQ);
        trainServiceImpl.addTrain(trainREQ);
        return Result.successResult();
    }

    @PutMapping("/updateTrain")
    public Result<Void> updateTrain(TrainREQ trainREQ) {
        log.info("【管理端-列车接口】更新列车，参数: {}", trainREQ);
        trainServiceImpl.updateTrain(trainREQ);
        return Result.successResult();
    }
    @DeleteMapping("/deleteTrain")
    public Result<Void> deleteTrain(@RequestParam("ids") List<Long> ids) {
        log.info("【管理端-列车接口】删除列车，参数: {}", ids);
        trainServiceImpl.deleteTrain(ids);
        return Result.successResult();
    }
    @GetMapping("/getTrain")
    public Result<PageResult<TrainEO>> EmployeePageQuery(TrainPageQueryREQ trainPageQueryREQ) {
        log.info("【管理端-列车接口】查询列车，参数: {}", trainPageQueryREQ);
        PageResult<TrainEO> result = trainServiceImpl.EmployeePageQuery(trainPageQueryREQ);
        return Result.successResult(result);
    }
}
