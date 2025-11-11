package top.flowerstardream.hcd.trainSeat.api.v1.app;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: QAQ
 * @Date: 2025/11/10 16:00
 * @Description: 用户接口
 */
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flowerstardream.hcd.trainSeat.biz.service.IRouteService;

@RestController
@RequestMapping("/api/v1/app/route")
@Tag(name = "路线相关接口", description = "路线相关接口")
@Slf4j
public class RouteController {

    @Resource
    private IRouteService IRouteService;

    /**
     * 获取路线信息
     * @return
     */

}
