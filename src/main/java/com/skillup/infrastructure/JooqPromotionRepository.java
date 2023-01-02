package com.skillup.infrastructure;

import com.skillup.domain.promotion.PromotionDomain;
import com.skillup.domain.promotion.PromotionRepository;
import com.skillup.domain.promotion.PromotionStockOperation;
import com.skillup.infrastructure.jooq.tables.Promotion;
import com.skillup.infrastructure.jooq.tables.records.PromotionRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Repository(value = "optimistic-lock")
public class JooqPromotionRepository implements PromotionRepository, PromotionStockOperation,RecordDomainMapping<PromotionRecord,PromotionDomain> {
    @Autowired
    DSLContext dslContext;
    private static final Promotion P_T = new Promotion();
    @Override
    public void createPromotion(PromotionDomain promotionDomain) {
        dslContext.executeInsert(toRecord(promotionDomain));
    }

    @Override
    public PromotionDomain getPromotionByPromotionId(String promotionId) {
        return dslContext.selectFrom(P_T).where(P_T.PROMOTION_ID.eq(promotionId)).fetchOptional(this::toDomain).orElse(null);
    }

    @Override
    public List<PromotionDomain> getPromotionByPromotionStatus(Integer status) {
        return dslContext.selectFrom(P_T).where(P_T.STATUS.eq(status)).fetch(this::toDomain);

    }

    @Override
    public void updatePromotion(PromotionDomain promotionDomain) {
        dslContext.executeUpdate(toRecord(promotionDomain));
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean lockStock(String id) {
        /**
         * update promotion
         * set available_stock = available_stock-1, lock_stock = lock_stock + 1
         * where id = promotion_id and available_stock >0
         */
        log.info("optimistic stock,try to lock");
        int isLocked = dslContext.update(P_T)
                .set(P_T.AVAILABLE_STOCK,P_T.AVAILABLE_STOCK.subtract(1))
                .set(P_T.LOCK_STOCK,P_T.LOCK_STOCK.add(1))
                .where(P_T.PROMOTION_ID.eq(id).and(P_T.AVAILABLE_STOCK.greaterThan(0L)))
                .execute();
        return isLocked == 1;
    }

    @Override
    public boolean deductStock(String id) {
        /**
         * update promotion
         * set  lock_stock = lock_stock - 1,
         * where id = promotion_id and lock_stock >0
         */
        log.info("optimistic stock,try to deduct");
        int isDeducted = dslContext.update(P_T)
                .set(P_T.LOCK_STOCK,P_T.LOCK_STOCK.subtract(1))
                .where(P_T.PROMOTION_ID.eq(id).and(P_T.LOCK_STOCK.greaterThan(0L)))
                .execute();
        return isDeducted == 1;
    }

    @Override
    public boolean reverseStock(String id) {
        /**
         * update promotion
         * set available_stock = available_stock+1, lock_stock = lock_stock - 1
         * where id = promotion_id and locked_stock >0
         */
        log.info("optimistic stock,try to revert");
        int isReverted = dslContext.update(P_T)
                .set(P_T.AVAILABLE_STOCK,P_T.AVAILABLE_STOCK.add(1))
                .set(P_T.LOCK_STOCK,P_T.LOCK_STOCK.subtract(1))
                .where(P_T.PROMOTION_ID.eq(id).and(P_T.LOCK_STOCK.greaterThan(0L)))
                .execute();
        return isReverted == 1;
    }
    @Override
    public PromotionDomain toDomain(PromotionRecord record) {
        return PromotionDomain.builder()
                .promotionId(record.getPromotionId())
                .promotionName(record.getPromotionName())
                .commodityId(record.getCommodityId())
                .originalPrice(record.getOriginalPrice())
                .promotionalPrice(record.getPromotionPrice())
                .startTime(record.getStartTime())
                .endTime(record.getEndTime())
                .status(record.getStatus())
                .totalStock(record.getTotalStock())
                .availableStock(record.getAvailableStock())
                .lockStock(record.getLockStock())
                .imageUrl(record.getImageUrl())
                .build();
    }

    @Override
    public PromotionRecord toRecord(PromotionDomain domain) {
        return new PromotionRecord(
                domain.getPromotionId(),
                domain.getPromotionName(),
                domain.getCommodityId(),
                domain.getOriginalPrice(),
                domain.getPromotionalPrice(),
                domain.getStartTime(),
                domain.getEndTime(),
                domain.getStatus(),
                domain.getTotalStock(),
                domain.getAvailableStock(),
                domain.getLockStock(),
                domain.getImageUrl()
        );
    }
}
