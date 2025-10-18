package com.studio.booking.repositories;

import com.studio.booking.entities.PriceTable;
import com.studio.booking.enums.PriceTableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PriceTableRepo extends JpaRepository<PriceTable, String> {
    List<PriceTable> findAllByStatusNot(PriceTableStatus status);

    @Query("""
            SELECT pt
            FROM PriceTable pt
            JOIN PriceTableItem pi ON pt.id = pi.priceTable.id
            JOIN StudioType st ON st.id = pi.studioType.id
            WHERE st.id = :studioTypeId
            AND pt.status = com.studio.booking.enums.PriceTableStatus.COMING_SOON
            OR pt.status = com.studio.booking.enums.PriceTableStatus.IS_HAPPENING
            """)
    List<PriceTable> findAllByStudioType(String studioTypeId);

    // Thao tác thay đổi dữ liệu
    // Thực hiện xóa Cache lỗi thời
    @Modifying(clearAutomatically = true)
    // Để thực hiện thao tác
    @Transactional
    @Query("""
            UPDATE PriceTable pt
            SET pt.status = com.studio.booking.enums.PriceTableStatus.IS_HAPPENING
            WHERE pt.status = com.studio.booking.enums.PriceTableStatus.COMING_SOON
            AND pt.startDate = :date
            """)
    int updateStatusIsReadyNow(LocalDate date);
    // Số lượng bản ghi đã cập nhập

    @Modifying(clearAutomatically = true)
    // Để thực hiện thao tác
    @Transactional
    @Query("""
            UPDATE PriceTable pt
            SET pt.status = com.studio.booking.enums.PriceTableStatus.ENDED
            WHERE pt.status = com.studio.booking.enums.PriceTableStatus.IS_HAPPENING
            AND pt.endDate = :date
            """)
    int updateStatusIsEndNow(LocalDate date);
    // Số lượng bản ghi đã cập nhập
}

