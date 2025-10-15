package com.studio.booking.services;

import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.response.StudioAssignResponse;
import com.studio.booking.entities.StudioAssign;

import java.util.List;

public interface StudioAssignService {
    List<StudioAssignResponse> getAll();
    List<StudioAssignResponse> getByBooking(String bookingId);
    List<StudioAssignResponse> getByStudio(String studioId);
    StudioAssign create(StudioAssignRequest request);
    List<StudioAssign> createList(List<StudioAssignRequest> requests);
    StudioAssignResponse update(String id, StudioAssignRequest request);
    String delete(String id);
}
