package com.studio.booking.services;

import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.request.UpdateAdditionalTimeRequest;
import com.studio.booking.dtos.request.UpdateStatusRequest;
import com.studio.booking.dtos.response.StudioAssignAdditionTimeResponse;
import com.studio.booking.dtos.response.StudioAssignResponse;
import com.studio.booking.dtos.response.StudioResponse;
import com.studio.booking.entities.StudioAssign;

import java.util.List;

public interface StudioAssignService {
    List<StudioAssignResponse> getAll();
    List<StudioAssignResponse> getByBooking(String bookingId);
    List<StudioAssignResponse> getByStudio(String studioId);
    StudioAssign create(StudioAssignRequest request);
    StudioAssignResponse update(String id, StudioAssignRequest request);
    String delete(String id);

    StudioAssignResponse updateStatus(String id, UpdateStatusRequest request);
    StudioAssignAdditionTimeResponse addAdditionTime(String assignId, UpdateAdditionalTimeRequest req);
//    StudioAssignResponse attachStudioToExistingAssign(String assignId, String studioId);
//    StudioAssign assignStudio(String studioId, StudioAssignRequest request);
}
