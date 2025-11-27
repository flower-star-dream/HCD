package top.flowerstardream.hcd.trainSeat.api.v1.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.trainSeat.ao.pqreq.TrainPageQueryREQ;
import top.flowerstardream.hcd.trainSeat.ao.res.TrainRES;
import top.flowerstardream.hcd.trainSeat.biz.service.impl.ITrainServiceImpl;

@RestController("appTrainController")
@RequestMapping("/api/v1/app/trainSeat/train")
@Tag(name = "小程序端-列车管理")
@Slf4j
public class AppTrainController {

    @Resource
    private ITrainServiceImpl trainServiceImpl;

    @GetMapping("/getTrains")
    public Result<PageResult<TrainRES>> UserPageQuery(TrainPageQueryREQ trainPageQueryREQ) {
        log.info("【小程序端-列车服务】查询列车，参数: {}", trainPageQueryREQ);
        PageResult<TrainRES> result = trainServiceImpl.UserPageQuery(trainPageQueryREQ);
        return Result.successResult(result);
    }
}
