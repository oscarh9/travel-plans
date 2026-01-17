package com.sigma.travelplans.domain.service;

import com.sigma.travelplans.domain.TravelPlan;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TravelPlanService {

    private final Map<String, TravelPlan> travelPlans = new HashMap<>();

    public void add(TravelPlan travelPlan) {
        if (travelPlans.containsKey(travelPlan.getName())) {
            throw new IllegalArgumentException(
                    "No pueden haber dos planes con el mismo nombre"
            );
        }
        travelPlans.put(travelPlan.getName(), travelPlan);
    }

    public Collection<TravelPlan> findAll() {
        return travelPlans.values();
    }
}
