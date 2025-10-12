package com.studio.booking.services;

import com.studio.booking.dtos.request.StudioRequest;
import com.studio.booking.dtos.response.StudioResponse;

import java.util.List;

public interface StudioService {
    List<StudioResponse> getAll();
    StudioResponse getById(String id);
    StudioResponse create(StudioRequest req);
    StudioResponse update(String id, StudioRequest req);
    String delete(String id);
    String restore(String id);
}
