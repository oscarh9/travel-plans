package com.sigma.travelplans.domain;

public class TravelPlan {

    private String name;
    private PlanType type;
    private int adultSeats;
    private int childSeats;
    private String originCity;
    private String destinationCity;

    public TravelPlan(
            String name,
            PlanType type,
            int adultSeats,
            int childSeats,
            String originCity,
            String destinationCity
    ) {
        this.name = name;
        this.type = type;
        this.adultSeats = adultSeats;
        this.childSeats = childSeats;
        this.originCity = originCity;
        this.destinationCity = destinationCity;
    }

    public String getName() {
        return name;
    }

    public PlanType getType() {
        return type;
    }

    public int getAdultSeats() {
        return adultSeats;
    }

    public int getChildSeats() {
        return childSeats;
    }

    public String getOriginCity() {
        return originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }
}
