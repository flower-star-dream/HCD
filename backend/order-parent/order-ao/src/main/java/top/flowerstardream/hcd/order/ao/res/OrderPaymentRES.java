package top.flowerstardream.hcd.order.ao.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: 订单支付结果
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentRES implements Serializable {

    private String nonceStr; //随机字符串
    private String paySign; //签名
    private String timeStamp; //时间戳
    private String signType; //签名算法
    private String packageStr; //统一下单接口返回的 prepay_id 参数值

}
