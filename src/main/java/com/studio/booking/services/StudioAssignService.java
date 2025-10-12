package com.studio.booking.services;

import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.response.StudioAssignResponse;

import java.util.List;

public interface StudioAssignService {
    List<StudioAssignResponse> getAll();
    List<StudioAssignResponse> getByBooking(String bookingId);
    List<StudioAssignResponse> getByStudio(String studioId);
    StudioAssignResponse create(StudioAssignRequest request);
    StudioAssignResponse update(String id, StudioAssignRequest request);
    String delete(String id);
}
