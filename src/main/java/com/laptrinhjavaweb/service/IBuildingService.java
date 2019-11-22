package com.laptrinhjavaweb.service;

import com.laptrinhjavaweb.builder.BuildingSearchBuilder;
import com.laptrinhjavaweb.dto.BuildingDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IBuildingService {
    List<BuildingDTO> findAll(BuildingSearchBuilder fieldSearch, Pageable pageable);
    Map<String,String> getBuildingTypes();
    Map<String,String> getDistricts ();
    void setType(BuildingDTO buildingDTO);
}
