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
        StringBuilder sb = new StringBuilder("Các chi nhánh hiện có:\n");
        locations.forEach(loc -> sb.append("• ")
                .append(loc.getLocationName())
                .append(" - ")
                .append(loc.getAddress())
                .append("\n"));
        return sb.toString();
    }

    public String getServiceList() {
        var services = serviceService.getAll();
        StringBuilder sb = new StringBuilder("Danh sách dịch vụ:\n");
        services.forEach(s -> sb.append("• ")
                .append(s.getServiceName())
                .append(" (")
                .append(s.getServiceFee())
                .append(" VND)\n"));
        return sb.toString();
    }

    public String getBasicPriceInfo() {
        var tables = priceTableService.getAll();
        if (tables.isEmpty()) return "Hiện chưa có bảng giá nào được công bố.";
        return "Hiện tại hệ thống đang áp dụng bảng giá có hiệu lực từ "
                + tables.get(0).getStartDate() + " đến " + tables.get(0).getEndDate()
                + ". Giá cụ thể tùy vào loại studio.";
    }

    public String getOpeningHours() {
        return "Các studio thường mở cửa từ 8:00 sáng đến 9:00 tối (từ Thứ 2 đến Chủ Nhật).";
    }
}
