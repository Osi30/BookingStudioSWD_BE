package com.studio.booking.services;

import com.studio.booking.dtos.request.StudioRequest;
import com.studio.booking.dtos.request.UpdateStatusRequest;
import com.studio.booking.dtos.response.StudioResponse;

import java.io.IOException;
import java.util.List;

public interface StudioService {
    List<StudioResponse> getAll(String studioTypeId);
    StudioResponse getById(String id);
    StudioResponse create(StudioRequest req) throws IOException;
    StudioResponse update(String id, StudioRequest req);
    String delete(String id);
    String restore(String id);
    List<StudioResponse> getForStaff(String employeeAccountId);
    StudioResponse updateStatus(String id, UpdateStatusRequest request);
}
