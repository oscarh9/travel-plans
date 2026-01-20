package com.sigma.travelplans.domain.service;

import com.sigma.travelplans.TravelPlanConstants;
import com.sigma.travelplans.domain.PlanType;
import com.sigma.travelplans.domain.TravelPlan;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TravelPlanServiceTest {

    private TravelPlanService service;

    @Before
    public void setUp() {
        service = new TravelPlanService();
    }

    @Test
    public void testAddValidPlan() {
        TravelPlan plan = new TravelPlan(
                "Vacaciones Madrid",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        service.add(plan);
        Collection<TravelPlan> allPlans = service.findAll();
        assertEquals(1, allPlans.size());
        assertTrue(allPlans.contains(plan));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPlanWithDuplicateName() {
        TravelPlan plan1 = new TravelPlan(
                "Viaje Negocios",
                PlanType.WORK,
                1,
                0,
                "Madrid",
                "Barcelona"
        );
        TravelPlan plan2 = new TravelPlan(
                "Viaje Negocios",
                PlanType.NORMAL,
                2,
                2,
                "Valencia",
                "Sevilla"
        );
        service.add(plan1);
        service.add(plan2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPlanWithNullPlan() {
        service.add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPlanWithMissingRequiredFields() {
        TravelPlan plan = new TravelPlan(
                "",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        service.add(plan);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddWorkPlanWithChildSeats() {
        TravelPlan plan = new TravelPlan(
                "Viaje Trabajo",
                PlanType.WORK,
                1,
                1,
                "Madrid",
                "Barcelona"
        );
        service.add(plan);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPlanWithNegativeAdultSeats() {
        TravelPlan plan = new TravelPlan(
                "Vacaciones",
                PlanType.NORMAL,
                -1,
                1,
                "Barcelona",
                "Madrid"
        );
        service.add(plan);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPlanWithNegativeChildSeats() {
        TravelPlan plan = new TravelPlan(
                "Vacaciones",
                PlanType.NORMAL,
                2,
                -1,
                "Barcelona",
                "Madrid"
        );
        service.add(plan);
    }

    @Test
    public void testUpdateExistingPlan() {
        TravelPlan originalPlan = new TravelPlan(
                "Vacaciones",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        service.add(originalPlan);
        TravelPlan updatedPlan = new TravelPlan(
                "Vacaciones",
                PlanType.NORMAL,
                3,
                2,
                "Barcelona",
                "Madrid"
        );
        service.update(updatedPlan);
        TravelPlan retrieved = service.findByName("Vacaciones");
        assertEquals(3, retrieved.getAdultSeats());
        assertEquals(2, retrieved.getChildSeats());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNonExistingPlan() {
        TravelPlan plan = new TravelPlan(
                "Plan Inexistente",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        service.update(plan);
    }

    @Test
    public void testRemoveExistingPlan() {
        TravelPlan plan = new TravelPlan(
                "Vacaciones",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        service.add(plan);
        service.remove("Vacaciones");
        Collection<TravelPlan> allPlans = service.findAll();
        assertTrue(allPlans.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistingPlan() {
        service.remove("Plan Inexistente");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemovePlanWithEmptyName() {
        service.remove("");
    }

    @Test
    public void testFindAllReturnsAllPlans() {
        TravelPlan plan1 = new TravelPlan(
                "Vacaciones 1",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        TravelPlan plan2 = new TravelPlan(
                "Viaje Negocios",
                PlanType.WORK,
                1,
                0,
                "Madrid",
                "Barcelona"
        );
        service.add(plan1);
        service.add(plan2);
        Collection<TravelPlan> allPlans = service.findAll();
        assertEquals(2, allPlans.size());
        assertTrue(allPlans.contains(plan1));
        assertTrue(allPlans.contains(plan2));
    }

    @Test
    public void testFindByNameExistingPlan() {
        TravelPlan plan = new TravelPlan(
                "Vacaciones",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        service.add(plan);
        TravelPlan found = service.findByName("Vacaciones");
        assertNotNull(found);
        assertEquals("Vacaciones", found.getName());
    }

    @Test
    public void testFindByNameNonExistingPlan() {
        TravelPlan found = service.findByName("Plan Inexistente");
        assertNull(found);
    }

    @Test
    public void testGetGroupedViewDataWithCompatiblePlans() {
        TravelPlan plan1 = new TravelPlan(
                "Vacaciones 1",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        TravelPlan plan2 = new TravelPlan(
                "Vacaciones 2",
                PlanType.NORMAL,
                3,
                0,
                "Barcelona",
                "Madrid"
        );
        TravelPlan plan3 = new TravelPlan(
                "Viaje Solitario",
                PlanType.WORK,
                1,
                0,
                "Valencia",
                "Sevilla"
        );
        service.add(plan1);
        service.add(plan2);
        service.add(plan3);
        Map<String, Object> groupedData = service.getGroupedViewData();
        Map<String, List<TravelPlan>> grouped = (Map<String, List<TravelPlan>>) groupedData.get(TravelPlanConstants.KEY_GROUPED);
        List<TravelPlan> unique = (List<TravelPlan>) groupedData.get(TravelPlanConstants.KEY_UNIQUE);
        assertEquals(1, grouped.size());
        assertEquals(1, unique.size());
    }

    @Test
    public void testGetGroupedViewDataNoCompatiblePlans() {
        TravelPlan plan1 = new TravelPlan(
                "Plan 1",
                PlanType.NORMAL,
                2,
                1,
                "Barcelona",
                "Madrid"
        );
        TravelPlan plan2 = new TravelPlan(
                "Plan 2",
                PlanType.WORK,
                1,
                0,
                "Madrid",
                "Barcelona"
        );
        TravelPlan plan3 = new TravelPlan(
                "Plan 3",
                PlanType.NORMAL,
                3,
                2,
                "Valencia",
                "Sevilla"
        );
        service.add(plan1);
        service.add(plan2);
        service.add(plan3);
        Map<String, Object> groupedData = service.getGroupedViewData();
        Map<String, List<TravelPlan>> grouped = (Map<String, List<TravelPlan>>) groupedData.get(TravelPlanConstants.KEY_GROUPED);
        List<TravelPlan> unique = (List<TravelPlan>) groupedData.get(TravelPlanConstants.KEY_UNIQUE);
        assertTrue(grouped.isEmpty());
        assertEquals(3, unique.size());
    }

    @Test
    public void testCaseInsensitiveCityNamesInCompatibility() {
        TravelPlan plan1 = new TravelPlan(
                "Plan 1",
                PlanType.NORMAL,
                2,
                1,
                "barcelona",
                "MADRID"
        );
        TravelPlan plan2 = new TravelPlan(
                "Plan 2",
                PlanType.NORMAL,
                3,
                0,
                "Barcelona",
                "madrid"
        );
        service.add(plan1);
        service.add(plan2);
        Map<String, Object> groupedData = service.getGroupedViewData();
        Map<String, List<TravelPlan>> grouped = (Map<String, List<TravelPlan>>) groupedData.get(TravelPlanConstants.KEY_GROUPED);
        assertEquals(1, grouped.size());
    }

    @Test
    public void testTrimCityNamesInCompatibility() {
        TravelPlan plan1 = new TravelPlan(
                "Plan 1",
                PlanType.NORMAL,
                2,
                1,
                "  Barcelona  ",
                "Madrid   "
        );
        TravelPlan plan2 = new TravelPlan(
                "Plan 2",
                PlanType.NORMAL,
                3,
                0,
                "Barcelona",
                "  Madrid"
        );
        service.add(plan1);
        service.add(plan2);
        Map<String, Object> groupedData = service.getGroupedViewData();
        Map<String, List<TravelPlan>> grouped = (Map<String, List<TravelPlan>>) groupedData.get(TravelPlanConstants.KEY_GROUPED);
        assertEquals(1, grouped.size());
    }

    @Test
    public void testWorkPlanWithZeroChildSeatsIsValid() {
        TravelPlan plan = new TravelPlan(
                "Viaje Trabajo",
                PlanType.WORK,
                1,
                0,
                "Madrid",
                "Barcelona"
        );
        service.add(plan);
        TravelPlan found = service.findByName("Viaje Trabajo");
        assertNotNull(found);
        assertEquals(PlanType.WORK, found.getType());
        assertEquals(0, found.getChildSeats());
    }

    @Test
    public void testNormalPlanWithZeroSeatsIsValid() {
        TravelPlan plan = new TravelPlan(
                "Viaje Vacío",
                PlanType.NORMAL,
                0,
                0,
                "Madrid",
                "Barcelona"
        );
        service.add(plan);
        TravelPlan found = service.findByName("Viaje Vacío");
        assertNotNull(found);
        assertEquals(0, found.getAdultSeats());
        assertEquals(0, found.getChildSeats());
    }
}
