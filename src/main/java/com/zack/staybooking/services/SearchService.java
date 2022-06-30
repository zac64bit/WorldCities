package com.zack.staybooking.services;

import com.zack.staybooking.models.Stay;
import com.zack.staybooking.repos.LocRepo;
import com.zack.staybooking.repos.StayRepo;
import com.zack.staybooking.repos.StayReservationDateRepo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SearchService {
    private StayRepo stayRepository;
    private StayReservationDateRepo stayReservationDateRepository;
    private LocRepo locationRepository;

    @Autowired
    public SearchService(StayRepo stayRepository, StayReservationDateRepo stayReservationDateRepository, LocRepo locationRepository) {
        this.stayRepository = stayRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
        this.locationRepository = locationRepository;
    }

    public List<Stay> search(int guestNumber, LocalDate checkinDate, LocalDate checkoutDate, double lat, double lon, String distance) {

        List<Long> stayIds = locationRepository.searchByDistance(lat, lon, distance);
        if (stayIds == null || stayIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> reservedStayIds = stayReservationDateRepository.findByIdInAndDateBetween(stayIds, checkinDate, checkoutDate.minusDays(1));

        List<Long> filteredStayIds = new ArrayList<>();
        for (Long stayId : stayIds) {
            if (!reservedStayIds.contains(stayId)) {
                filteredStayIds.add(stayId);
            }
        }
        return stayRepository.findByIdInAndGuestNumberGreaterThanEqual(filteredStayIds, guestNumber);
    }
}