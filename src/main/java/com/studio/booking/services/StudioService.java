package com.studio.booking.services;

import com.studio.booking.dtos.request.StudioRequest;
import com.studio.booking.dtos.response.StudioResponse;
import com.studio.booking.entities.Studio;

import java.time.LocalTime;
import java.util.List;

public interface StudioService {
    List<Studio> getAvailableStudios(String typeId, String locationId);
    List<StudioResponse> getAll(String studioTypeId);
    StudioResponse getById(String id);
    StudioResponse create(StudioRequest req);
    StudioResponse update(String id, StudioRequest req);
    String delete(String id);
    String restore(String id);
}
