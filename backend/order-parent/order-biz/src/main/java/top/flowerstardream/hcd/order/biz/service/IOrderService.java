package top.flowerstardream.hcd.order.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.flowerstardream.hcd.base.ao.req.StatusChangeREQ;
import top.flowerstardream.hcd.order.ao.dto.OrderDTO;
import top.flowerstardream.hcd.order.ao.req.OrderPageQueryREQ;
import top.flowerstardream.hcd.order.ao.req.OrderREQ;
import top.flowerstardream.hcd.order.ao.req.OrderStatusREQ;
import top.flowerstardream.hcd.order.ao.req.OrdersPaymentREQ;
import top.flowerstardream.hcd.order.ao.res.OrderMgmtRES;
import top.flowerstardream.hcd.order.ao.res.OrderPaymentRES;
import top.flowerstardream.hcd.order.ao.res.OrderRES;
import top.flowerstardream.hcd.order.bo.OrderEO;
import top.flowerstardream.hcd.tools.result.PageResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: 订单服务接口
 */
public interface IOrderService extends IService<OrderEO> {

    /**
     * 创建订单
     * @param req 订单创建请求
     * @return 订单ID
     */
    void createOrder(OrderREQ req);

    /**
     * 查询订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    OrderMgmtRES getOrderById(Long id);

    /**
     * 更新订单状态
     * @param req 订单状态更新请求
     */
    void updateOrderStatus(OrderStatusREQ req) throws Exception;

    /**
     * 取消订单（退订）
     * @param orderId 订单ID
     * @param userId 用户ID
     */
    void cancelOrder(Long orderId, Long userId) throws Exception;

    /**
     * 分页查询订单列表
     * @param req 查询条件
     * @return 分页结果
     */
    PageResult<OrderMgmtRES> pageQuery(OrderPageQueryREQ req);

    /**
     * 查询用户的订单列表
     * @param userId 用户ID
     * @return 分页结果
     */
    List<OrderRES> getUserOrders(Long userId);

    /**
     * 更新订单总价
     * @param orderId 订单ID
     * @param newTotalPrice 新的总价
     */
    void updateTotalPrice(Long orderId, BigDecimal newTotalPrice);

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(Long outTradeNo);

    /**
     * 订单支付
     * @param ordersPaymentREQ
     * @return
     */
    OrderPaymentRES payment(OrdersPaymentREQ ordersPaymentREQ) throws Exception;

    /**
     * 订单退款
     * @param orderId
     */
    void orderRefund(Long orderId) throws Exception;

    /**
     * 获取订单状态
     * @param orderId
     * @return
     */
    Integer getOrderStatus(Long orderId);
}