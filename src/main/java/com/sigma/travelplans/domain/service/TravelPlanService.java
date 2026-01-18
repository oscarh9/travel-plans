package com.sigma.travelplans.domain.service;

import com.sigma.travelplans.domain.PlanType;
import com.sigma.travelplans.domain.TravelPlan;

import java.util.*;

public class TravelPlanService {
    private final Map<String, TravelPlan> travelPlans = new HashMap<>();

    public void add(TravelPlan travelPlan) {
        validate(travelPlan);

        if (travelPlans.containsKey(travelPlan.getName())) {
            throw new IllegalArgumentException("No pueden haber dos planes con el mismo nombre");
        }

        travelPlans.put(travelPlan.getName(), travelPlan);
    }

    public void update(TravelPlan travelPlan) {
        validate(travelPlan);

        if (!travelPlans.containsKey(travelPlan.getName())) {
            throw new IllegalArgumentException("El plan no existe");
        }

        travelPlans.put(travelPlan.getName(), travelPlan);
    }

    public void remove(String name) {
        if (isBlank(name)) {
            throw new IllegalArgumentException("El nombre del plan es obligatorio");
        }

        if (!travelPlans.containsKey(name)) {
            throw new IllegalArgumentException("El plan no existe");
        }

        travelPlans.remove(name);
    }

    public Collection<TravelPlan> findAll() {
        return travelPlans.values();
    }

    public TravelPlan findByName(String name) {
        return travelPlans.get(name);
    }

    public Map<String, Object> getGroupedViewData() {
        Map<String, Object> result = new HashMap<>();
        Map<String, List<TravelPlan>> grouped = new HashMap<>();
        List<TravelPlan> unique = new ArrayList<>();
        Map<String, List<TravelPlan>> allGroups = new HashMap<>();

        for (TravelPlan plan : travelPlans.values()) {
            String key = generateCompatibilityKey(plan);
            allGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(plan);
        }

        for (Map.Entry<String, List<TravelPlan>> entry : allGroups.entrySet()) {
            if (entry.getValue().size() > 1) {
                String displayKey = formatGroupKey(entry.getKey(), entry.getValue().size());
                grouped.put(displayKey, entry.getValue());
            } else {
                unique.add(entry.getValue().get(0));
            }
        }

        result.put("grouped", grouped);
        result.put("unique", unique);
        return result;
    }

    private void validate(TravelPlan travelPlan) {
        if (travelPlan == null) {
            throw new IllegalArgumentException("El plan no puede ser nulo");
        }

        if (isBlank(travelPlan.getName()) || travelPlan.getType() == null ||
                isBlank(travelPlan.getOriginCity()) || isBlank(travelPlan.getDestinationCity())) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }

        if (travelPlan.getAdultSeats() < 0 || travelPlan.getChildSeats() < 0) {
            throw new IllegalArgumentException("Los asientos deben ser cero o un número positivo");
        }

        if (travelPlan.getType() == PlanType.WORK && travelPlan.getChildSeats() > 0) {
            throw new IllegalArgumentException("Los planes de trabajo no pueden tener asientos para niños");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String generateCompatibilityKey(TravelPlan plan) {
        return String.format("%s|%s|%s",
                plan.getType().name(),
                plan.getOriginCity().trim().toLowerCase(),
                plan.getDestinationCity().trim().toLowerCase());
    }

    private String formatGroupKey(String key, int count) {
        String[] parts = key.split("\\|");
        return String.format("%s - De %s a %s (%d planes compatibles)",
                parts[0], parts[1], parts[2], count);
    }
}
