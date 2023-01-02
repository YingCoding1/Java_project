package com.skillup.infrastructure;

import com.skillup.domain.order.OrderDomain;
import com.skillup.domain.order.OrderRepository;
import com.skillup.domain.order.util.OrderStatus;
import com.skillup.infrastructure.jooq.tables.Orders;
import com.skillup.infrastructure.jooq.tables.records.OrdersRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JooqOrderRepo implements OrderRepository {
    @Autowired
    DSLContext dslContext;


    public static final Orders ORDERS_T = new Orders();

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createOrder(OrderDomain orderDomain) {
        dslContext.executeInsert(toRecord(orderDomain));
    }


    @Override
    public OrderDomain getOrderById(Long id) {
        return dslContext.selectFrom(ORDERS_T).where(ORDERS_T.ORDER_NUMBER.eq(id)).fetchOptional(this::toDomain).orElse(null);
    }

    @Override
    public void updateOrder(OrderDomain orderDomain) {
        dslContext.executeUpdate(toRecord(orderDomain));
    }

    public OrderDomain toDomain(OrdersRecord record) {
        return OrderDomain.builder()
                .orderNumber(record.getOrderNumber())
                .orderStatus(OrderStatus.cachedStatus.get(record.getOrderStatus()))
                .promotionId(record.getPromotionId())
                .promotionName(record.getPromotionName())
                .userId(record.getUserId())
                .orderAmount(record.getOrderAmount())
                .createTime(record.getCreateTime())
                .payTime(record.getPayTime())
                .build();
    }

    public OrdersRecord toRecord(OrderDomain domain) {
        return new OrdersRecord(
                domain.getOrderNumber(),
                domain.getOrderStatus().code,
                domain.getPromotionId(),
                domain.getPromotionName(),
                domain.getUserId(),
                domain.getOrderAmount(),
                domain.getCreateTime(),
                domain.getPayTime()
        );
    }
}
