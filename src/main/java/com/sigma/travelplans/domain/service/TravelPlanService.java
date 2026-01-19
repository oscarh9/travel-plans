package com.sigma.travelplans.domain.service;

import com.sigma.travelplans.domain.PlanType;
import com.sigma.travelplans.domain.TravelPlan;

import java.util.*;

public class TravelPlanService {

    private static final String ERROR_PLAN_NULL = "El plan no puede ser nulo";
    private static final String ERROR_ALL_FIELDS_REQUIRED = "Todos los campos son obligatorios";
    private static final String ERROR_SEATS_POSITIVE = "Los asientos deben ser cero o un número positivo";
    private static final String ERROR_WORK_NO_CHILD_SEATS = "Los planes de trabajo no pueden tener asientos para niños";
    private static final String ERROR_DUPLICATE_NAME = "No pueden haber dos planes con el mismo nombre";
    private static final String ERROR_PLAN_NOT_EXIST = "El plan no existe";
    private static final String ERROR_NAME_REQUIRED = "El nombre del plan es obligatorio";

    private static final String KEY_GROUPED = "grouped";
    private static final String KEY_UNIQUE = "unique";

    private static final String COMPATIBILITY_KEY_SEPARATOR = "\\|";

    private final Map<String, TravelPlan> travelPlans = new HashMap<>();

    public void add(TravelPlan travelPlan) {
        validate(travelPlan);

        if (travelPlans.containsKey(travelPlan.getName())) {
            throw new IllegalArgumentException(ERROR_DUPLICATE_NAME);
        }

        travelPlans.put(travelPlan.getName(), travelPlan);
    }

    public void update(TravelPlan travelPlan) {
        validate(travelPlan);

        if (!travelPlans.containsKey(travelPlan.getName())) {
            throw new IllegalArgumentException(ERROR_PLAN_NOT_EXIST);
        }

        travelPlans.put(travelPlan.getName(), travelPlan);
    }

    public void remove(String name) {
        validateName(name);

        if (!travelPlans.containsKey(name)) {
            throw new IllegalArgumentException(ERROR_PLAN_NOT_EXIST);
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
        Map<String, List<TravelPlan>> allGroups = groupPlansByCompatibility();
        Map<String, List<TravelPlan>> grouped = new HashMap<>();
        List<TravelPlan> unique = new ArrayList<>();

        separateGroupedAndUniquePlans(allGroups, grouped, unique);

        return createResultMap(grouped, unique);
    }

    private Map<String, List<TravelPlan>> groupPlansByCompatibility() {
        Map<String, List<TravelPlan>> groups = new HashMap<>();

        for (TravelPlan plan : travelPlans.values()) {
            String key = generateCompatibilityKey(plan);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(plan);
        }

        return groups;
    }

    private void separateGroupedAndUniquePlans(Map<String, List<TravelPlan>> allGroups,
                                               Map<String, List<TravelPlan>> grouped,
                                               List<TravelPlan> unique) {
        for (Map.Entry<String, List<TravelPlan>> entry : allGroups.entrySet()) {
            List<TravelPlan> plans = entry.getValue();

            if (plans.size() > 1) {
                String displayKey = formatGroupKey(entry.getKey(), plans.size());
                grouped.put(displayKey, plans);
            } else {
                unique.add(plans.get(0));
            }
        }
    }

    private Map<String, Object> createResultMap(Map<String, List<TravelPlan>> grouped,
                                                List<TravelPlan> unique) {
        Map<String, Object> result = new HashMap<>();
        result.put(KEY_GROUPED, grouped);
        result.put(KEY_UNIQUE, unique);
        return result;
    }

    private void validate(TravelPlan travelPlan) {
        if (travelPlan == null) {
            throw new IllegalArgumentException(ERROR_PLAN_NULL);
        }

        validateRequiredFields(travelPlan);
        validateSeats(travelPlan);
        validateWorkPlanChildSeats(travelPlan);
    }

    private void validateRequiredFields(TravelPlan travelPlan) {
        if (isBlank(travelPlan.getName()) || travelPlan.getType() == null ||
                isBlank(travelPlan.getOriginCity()) || isBlank(travelPlan.getDestinationCity())) {
            throw new IllegalArgumentException(ERROR_ALL_FIELDS_REQUIRED);
        }
    }

    private void validateSeats(TravelPlan travelPlan) {
        if (travelPlan.getAdultSeats() < 0 || travelPlan.getChildSeats() < 0) {
            throw new IllegalArgumentException(ERROR_SEATS_POSITIVE);
        }
    }

    private void validateWorkPlanChildSeats(TravelPlan travelPlan) {
        if (travelPlan.getType() == PlanType.WORK && travelPlan.getChildSeats() > 0) {
            throw new IllegalArgumentException(ERROR_WORK_NO_CHILD_SEATS);
        }
    }

    private void validateName(String name) {
        if (isBlank(name)) {
            throw new IllegalArgumentException(ERROR_NAME_REQUIRED);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String generateCompatibilityKey(TravelPlan plan) {
        return String.format("%s|%s|%s",
                plan.getType().name(),
                normalizeCityName(plan.getOriginCity()),
                normalizeCityName(plan.getDestinationCity()));
    }

    private String normalizeCityName(String city) {
        return city.trim().toLowerCase();
    }

    private String formatGroupKey(String key, int count) {
        String[] parts = key.split(COMPATIBILITY_KEY_SEPARATOR);
        return String.format("%s - De %s a %s (%d planes compatibles)",
                parts[0], parts[1], parts[2], count);
    }
}
