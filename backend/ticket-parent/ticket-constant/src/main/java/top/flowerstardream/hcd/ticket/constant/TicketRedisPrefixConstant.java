package top.flowerstardream.hcd.ticket.constant;

/**
 * @Author: 花海
 * @Date: 2024/11/11
 * @Description: 车票Redis前缀常量
 */
public interface TicketRedisPrefixConstant {

    /**
     * 车票缓存前缀
     */
    String TICKET_CACHE_PREFIX = "ticket:info:";

    /**
     * 订单车票列表缓存前缀
     */
    String ORDER_TICKET_LIST_PREFIX = "order:ticket:list:";

    /**
     * 用户车票列表缓存前缀
     */
    String USER_TICKET_LIST_PREFIX = "user:ticket:list:";

    /**
     * 余票缓存前缀
     */
    String REMAINING_TICKET_PREFIX = "ticket:remaining:";

    /**
     * 座位锁定前缀
     */
    String SEAT_LOCK_PREFIX = "seat:lock:";

    /**
     * 车票状态变更锁前缀
     */
    String TICKET_STATUS_LOCK_PREFIX = "ticket:status:lock:";
}