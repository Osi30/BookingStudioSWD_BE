package com.studio.booking.ai;
import com.studio.booking.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ChatDataProvider {
    private final LocationService locationService;
    private final PriceTableService priceTableService;
    private final StudioService studioService;
    private final ServiceService serviceService;

    public String getAllLocations() {
        var locations = locationService.getAll(null);
        if (locations.isEmpty()) return "Hiện chưa có chi nhánh nào.";
        StringBuilder sb = new StringBuilder("Các chi nhánh hiện có:\n");
        locations.forEach(loc -> sb.append("• ")
                .append(loc.getLocationName())
                .append(" - ").append(loc.getAddress())
                .append("\n"));
        return sb.toString();
    }

    public String getServiceList() {
        var services = serviceService.getAll();
        if (services.isEmpty()) return "Hiện chưa có dịch vụ nào.";
        StringBuilder sb = new StringBuilder("Danh sách dịch vụ:\n");
        services.forEach(s -> sb.append("• ")
                .append(s.getServiceName())
                .append(" - Phí: ").append(s.getServiceFee()).append(" VND\n"));
        return sb.toString();
    }

    public String getBasicPriceInfo() {
        var tables = priceTableService.getAll();
        if (tables.isEmpty()) return "Hiện chưa có bảng giá nào được công bố.";
        return "Bảng giá hiện hành áp dụng từ "
                + tables.get(0).getStartDate() + " đến " + tables.get(0).getEndDate()
                + ". Giá có thể thay đổi tùy loại studio.";
    }

    public String getOpeningHours() {
        return "Các studio thường mở cửa từ 8:00 sáng đến 9:00 tối (Thứ 2 - Chủ Nhật).";
    }

    // ✅ Bổ sung mới — sử dụng StudioResponse
    public String getStudioList() {
        var studios = studioService.getAll(null);
        if (studios.isEmpty()) {
            return "Hiện tại chưa có studio nào được đăng tải.";
        }

        StringBuilder sb = new StringBuilder("Danh sách các studio khả dụng:\n");
        studios.forEach(s -> {
            sb.append("• ")
                    .append(s.getStudioName());

            if (s.getStudioTypeName() != null) {
                sb.append(" - Loại: ").append(s.getStudioTypeName());
            }

            sb.append(", Diện tích: ").append(s.getAcreage()).append(" m²");

            if (s.getLocationName() != null) {
                sb.append(", Chi nhánh: ").append(s.getLocationName());
            }

            String statusVi = switch (s.getStatus()) {
                case AVAILABLE -> "Đang hoạt động";
                case MAINTENANCE -> "Bảo trì";
                case DELETED -> "Ngưng hoạt động";
            };

            sb.append(", Trạng thái: ").append(statusVi);

            if (s.getStartTime() != null && s.getEndTime() != null) {
                sb.append(", Giờ mở cửa: ")
                        .append(s.getStartTime()).append(" - ").append(s.getEndTime());
            }

            sb.append("\n");
        });

        return sb.toString();
    }
}
