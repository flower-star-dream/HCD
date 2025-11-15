package top.flowerstardream.hcd.ticket.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.flowerstardream.hcd.ticket.ao.dto.TicketDTO;
import top.flowerstardream.hcd.ticket.ao.req.TicketPageQueryREQ;
import top.flowerstardream.hcd.ticket.ao.req.TicketStatusChangeREQ;
import top.flowerstardream.hcd.ticket.ao.res.TicketRES;
import top.flowerstardream.hcd.ticket.bo.TicketEO;
import top.flowerstardream.hcd.tools.result.PageResult;

import java.util.List;
import java.math.BigDecimal;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 车票服务接口
 */
public interface ITicketService extends IService<TicketEO> {

    /**
     * 新增车票
     * @param ticketDTO 车票请求信息
     */
    void createTickets(TicketDTO ticketDTO);

    /**
     * 根据用户ID查询车票列表
     * @param userId 用户ID
     * @return 车票响应列表
     */
    List<TicketRES> getTicketsByUserId(Long userId);

    /**
     * 分页查询车票列表（后管端）
     * @param req 查询条件
     * @return 分页结果
     */
    PageResult<TicketRES> pageQuery(TicketPageQueryREQ req);

    /**
     * 更新车票状态
     * @param req 状态变更请求
     */
    void updateTicketStatus(TicketStatusChangeREQ req);

    /**
     * 取消车票
     * @param ticketId 车票ID
     * @param userId 用户ID
     */
    void cancelTicket(Long ticketId, Long userId);

    /**
     * 根据订单ID查询车票列表
     * @param orderId 订单ID
     * @return 车票响应列表
     */
    List<TicketRES> getTicketsByOrderId(Long orderId);

    /**
     * 根据订单ID取消车票
     * @param orderId 订单ID
     */
    void cancelTicketByOrder(Long orderId);
}