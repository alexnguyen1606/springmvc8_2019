package com.laptrinhjavaweb.service.impl;

import com.laptrinhjavaweb.builder.BuildingSearchBuilder;
import com.laptrinhjavaweb.converter.BuildingConverter;
import com.laptrinhjavaweb.dto.BuildingDTO;
import com.laptrinhjavaweb.enums.BuildingTypesEnum;
import com.laptrinhjavaweb.enums.DistrictsEnum;
import com.laptrinhjavaweb.repository.BuildingRepository;
import com.laptrinhjavaweb.service.IBuildingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuildingService implements IBuildingService {

    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private BuildingConverter converter;
    @Override
    public List<BuildingDTO> findAll(BuildingSearchBuilder fieldSearch, Pageable pageable) {
        Map<String,Object> params = convertToMapProperties(fieldSearch);

        return buildingRepository.findAll(params,pageable,fieldSearch).stream().
                map(item-> converter.convertToDTO(item)).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getBuildingTypes() {
        Map<String,String> buildingTypes = new HashMap<>();
        for(BuildingTypesEnum item : BuildingTypesEnum.values()){
            buildingTypes.put(item.toString(),item.getValue());
        }
        return buildingTypes;
    }

    @Override
    public Map<String, String> getDistricts() {
        Map<String,String> districts = new HashMap<>();
        for(DistrictsEnum item : DistrictsEnum.values()){
            districts.put(item.name(),item.getValue());
        }
        return districts;
    }

    @Override
    public void setType(BuildingDTO buildingDTO) {
        StringBuilder type = new StringBuilder("");
        type.append(String.join(",",buildingDTO.getBuildingTypes()));
        buildingDTO.setType(type.toString());
    }
    private Map<String,Object> convertToMapProperties(BuildingSearchBuilder fieldSearch) {
        Map<String, Object> properties = new HashMap<>();
        try {
            Field[] fields = BuildingSearchBuilder.class.getDeclaredFields();
            for (Field field:fields) {
                if ( !field.getName().equals("buildingTypes") && !field.getName().startsWith("costRent")
                        && !field.getName().startsWith("rentArea") && !field.getName().equals("staffId")) {
                    field.setAccessible(true);
                    if (field.getName().equals("buildingArea") || field.getName().equals("numberOfBasement")) {
                        if (field.get(fieldSearch) != null && StringUtils.isNotEmpty((String) field.get(fieldSearch))) {
                            properties.put(field.getName().toLowerCase(), Integer.parseInt((String) field.get(fieldSearch)));
                        }
                    } else {
                        properties.put(field.getName(), field.get(fieldSearch));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return properties;
    }
    private String convertBuildingTypes(String[] buildingTypes){
        StringBuilder result = new StringBuilder("");
        if (buildingTypes.length>0 && buildingTypes!=null){
            for (String type : buildingTypes){
                if (result.length()>0){
                    result.append(","+type);
                }else {
                    result.append(type);
                }
            }
        }
        return result.toString();
    }
}
