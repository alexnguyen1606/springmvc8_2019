package com.laptrinhjavaweb.controller;

import com.laptrinhjavaweb.builder.BuildingSearchBuilder;
import com.laptrinhjavaweb.dto.BuildingDTO;
import com.laptrinhjavaweb.service.impl.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "buildingControllerOfAdmin")
public class BuildingController {
    @Autowired
    private BuildingService buildingService;

    @GetMapping("/admin/building")
    public ModelAndView showList(@ModelAttribute(name = "model") BuildingDTO model){
        ModelAndView mav = new ModelAndView("admin/building/list");
        BuildingSearchBuilder builder = new BuildingSearchBuilder.Builder()
                .setName(model.getName())
                .setDistrict(model.getDistrict())
                .setBuildingArea(model.getBuildingArea())
                .setNumberOfBasement(model.getNumberOfBasement())
                .setBuildingTypes(model.getBuildingTypes())
                .setCostRentForm(model.getCostRentFrom())
                .setCostRentTo(model.getCostRentTo())
                .setRentAreaFrom(model.getRentAreaFrom())
                .setRentAreaTo(model.getRentAreaTo())
                .setStaffId(model.getStaffId())
                .setManagerName(model.getManagerName())
                .setManagerPhone(model.getManagerPhone())
                .build();
        Pageable pageable = new PageRequest(1,10);
        mav.addObject("buildings",buildingService.findAll(builder,pageable));
        mav.addObject("model",model);
        mav.addObject("districts",buildingService.getDistricts());
        mav.addObject("buildingTypes",buildingService.getBuildingTypes());
        return mav;
    }
    @GetMapping("/admin/building/edit")
    public ModelAndView editBuilding(@RequestParam(value = "id",required = false) Long id){
        ModelAndView mav = new ModelAndView("admin/building/edit");
        
        return mav;
    }
}
