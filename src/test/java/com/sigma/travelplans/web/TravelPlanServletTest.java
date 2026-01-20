package com.sigma.travelplans.web;

import com.sigma.travelplans.TravelPlanConstants;
import com.sigma.travelplans.domain.PlanType;
import com.sigma.travelplans.domain.TravelPlan;
import com.sigma.travelplans.domain.service.TravelPlanService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class TravelPlanServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private TravelPlanService travelPlanService;

    private TravelPlanServlet servlet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new TravelPlanServlet();
        java.lang.reflect.Field field = TravelPlanServlet.class.getDeclaredField("travelPlanService");
        field.setAccessible(true);
        field.set(servlet, travelPlanService);
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGetListAllPlans() throws ServletException, IOException {
        Collection<TravelPlan> mockPlans = Arrays.asList(
                new TravelPlan("Plan 1", PlanType.NORMAL, 2, 1, "Barcelona", "Madrid"),
                new TravelPlan("Plan 2", PlanType.WORK, 1, 0, "Madrid", "Barcelona")
        );

        when(travelPlanService.findAll()).thenReturn(mockPlans);
        servlet.doGet(request, response);
        verify(travelPlanService).findAll();
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLANS, mockPlans);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithEditAction() throws ServletException, IOException {
        String planName = "Vacaciones Madrid";
        TravelPlan mockPlan = new TravelPlan(
                planName, PlanType.NORMAL, 2, 1, "Barcelona", "Madrid"
        );

        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn(TravelPlanConstants.ACTION_EDIT);
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn(planName);
        when(travelPlanService.findByName(planName)).thenReturn(mockPlan);
        servlet.doGet(request, response);
        verify(travelPlanService).findByName(planName);
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLAN, mockPlan);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithEditActionPlanNotFound() throws ServletException, IOException {
        String planName = "Plan Inexistente";
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn(TravelPlanConstants.ACTION_EDIT);
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn(planName);
        when(travelPlanService.findByName(planName)).thenReturn(null);
        servlet.doGet(request, response);
        verify(request).setAttribute(TravelPlanConstants.ATTR_ERROR, TravelPlanConstants.MSG_PLAN_NOT_FOUND + planName);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithEditActionEmptyName() throws ServletException, IOException {
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn(TravelPlanConstants.ACTION_EDIT);
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn("");
        servlet.doGet(request, response);
        verify(request).setAttribute(TravelPlanConstants.ATTR_ERROR, TravelPlanConstants.MSG_NAME_REQUIRED);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithGroupedView() throws ServletException, IOException {
        when(request.getParameter(TravelPlanConstants.PARAM_VIEW)).thenReturn(TravelPlanConstants.VIEW_GROUPED);
        Map<String, Object> mockGroupedData = new HashMap<>();
        mockGroupedData.put(TravelPlanConstants.KEY_GROUPED, new HashMap<String, java.util.List<TravelPlan>>());
        mockGroupedData.put(TravelPlanConstants.KEY_UNIQUE, Arrays.asList(
                new TravelPlan("Plan 1", PlanType.NORMAL, 2, 1, "Barcelona", "Madrid")
        ));
        when(travelPlanService.getGroupedViewData()).thenReturn(mockGroupedData);
        servlet.doGet(request, response);
        verify(travelPlanService).getGroupedViewData();
        verify(request).setAttribute(TravelPlanConstants.ATTR_GROUPED_PLANS, mockGroupedData.get(TravelPlanConstants.KEY_GROUPED));
        verify(request).setAttribute(TravelPlanConstants.ATTR_UNIQUE_PLANS, mockGroupedData.get(TravelPlanConstants.KEY_UNIQUE));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostCreatePlan() throws ServletException, IOException {
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn("");
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn("Vacaciones");
        when(request.getParameter(TravelPlanConstants.PARAM_TYPE)).thenReturn("NORMAL");
        when(request.getParameter(TravelPlanConstants.PARAM_ORIGIN_CITY)).thenReturn("Barcelona");
        when(request.getParameter(TravelPlanConstants.PARAM_DESTINATION_CITY)).thenReturn("Madrid");
        when(request.getParameter(TravelPlanConstants.PARAM_ADULT_SEATS)).thenReturn("2");
        when(request.getParameter(TravelPlanConstants.PARAM_CHILD_SEATS)).thenReturn("1");
        Collection<TravelPlan> mockPlans = Arrays.asList(
                new TravelPlan("Vacaciones", PlanType.NORMAL, 2, 1, "Barcelona", "Madrid")
        );
        when(travelPlanService.findAll()).thenReturn(mockPlans);
        servlet.doPost(request, response);
        verify(travelPlanService).add(any(TravelPlan.class));
        verify(request).setAttribute(TravelPlanConstants.ATTR_SUCCESS, TravelPlanConstants.MSG_PLAN_CREATED);
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLANS, mockPlans);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostUpdatePlan() throws ServletException, IOException {
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn(TravelPlanConstants.ACTION_UPDATE);
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn("Vacaciones");
        when(request.getParameter(TravelPlanConstants.PARAM_TYPE)).thenReturn("NORMAL");
        when(request.getParameter(TravelPlanConstants.PARAM_ORIGIN_CITY)).thenReturn("Barcelona");
        when(request.getParameter(TravelPlanConstants.PARAM_DESTINATION_CITY)).thenReturn("Madrid");
        when(request.getParameter(TravelPlanConstants.PARAM_ADULT_SEATS)).thenReturn("3");
        when(request.getParameter(TravelPlanConstants.PARAM_CHILD_SEATS)).thenReturn("2");
        Collection<TravelPlan> mockPlans = Arrays.asList(
                new TravelPlan("Vacaciones", PlanType.NORMAL, 3, 2, "Barcelona", "Madrid")
        );
        when(travelPlanService.findAll()).thenReturn(mockPlans);
        servlet.doPost(request, response);
        verify(travelPlanService).update(any(TravelPlan.class));
        verify(request).setAttribute(TravelPlanConstants.ATTR_SUCCESS, TravelPlanConstants.MSG_PLAN_UPDATED);
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLANS, mockPlans);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostDeletePlan() throws ServletException, IOException {
        String planName = "Vacaciones";
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn(TravelPlanConstants.ACTION_DELETE);
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn(planName);
        when(request.getContextPath()).thenReturn("/travel-plans-app");
        servlet.doPost(request, response);
        verify(travelPlanService).remove(planName);
        verify(response).sendRedirect("/travel-plans-app/travel-plans");
    }

    @Test
    public void testDoPostDeletePlanWithError() throws ServletException, IOException {
        String planName = "Plan Inexistente";
        String errorMessage = TravelPlanConstants.ERROR_PLAN_NOT_EXIST;
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn(TravelPlanConstants.ACTION_DELETE);
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn(planName);
        doThrow(new IllegalArgumentException(errorMessage)).when(travelPlanService).remove(planName);
        Collection<TravelPlan> mockPlans = Arrays.asList();
        when(travelPlanService.findAll()).thenReturn(mockPlans);
        servlet.doPost(request, response);
        verify(travelPlanService).remove(planName);
        verify(request).setAttribute(TravelPlanConstants.ATTR_ERROR, errorMessage);
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLANS, mockPlans);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostCreatePlanWithValidationError() throws ServletException, IOException {
        String errorMessage = TravelPlanConstants.ERROR_ALL_FIELDS_REQUIRED;
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn("");
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn("");
        when(request.getParameter(TravelPlanConstants.PARAM_TYPE)).thenReturn("NORMAL");
        when(request.getParameter(TravelPlanConstants.PARAM_ORIGIN_CITY)).thenReturn("Barcelona");
        when(request.getParameter(TravelPlanConstants.PARAM_DESTINATION_CITY)).thenReturn("Madrid");
        when(request.getParameter(TravelPlanConstants.PARAM_ADULT_SEATS)).thenReturn("2");
        when(request.getParameter(TravelPlanConstants.PARAM_CHILD_SEATS)).thenReturn("1");
        doThrow(new IllegalArgumentException(errorMessage)).when(travelPlanService).add(any(TravelPlan.class));
        Collection<TravelPlan> mockPlans = Arrays.asList();
        when(travelPlanService.findAll()).thenReturn(mockPlans);
        servlet.doPost(request, response);
        verify(request).setAttribute(TravelPlanConstants.ATTR_ERROR, errorMessage);
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLANS, mockPlans);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithInvalidSeatsFormat() throws ServletException, IOException {
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn("");
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn("Vacaciones");
        when(request.getParameter(TravelPlanConstants.PARAM_TYPE)).thenReturn("NORMAL");
        when(request.getParameter(TravelPlanConstants.PARAM_ORIGIN_CITY)).thenReturn("Barcelona");
        when(request.getParameter(TravelPlanConstants.PARAM_DESTINATION_CITY)).thenReturn("Madrid");
        when(request.getParameter(TravelPlanConstants.PARAM_ADULT_SEATS)).thenReturn("dos");
        when(request.getParameter(TravelPlanConstants.PARAM_CHILD_SEATS)).thenReturn("1");
        Collection<TravelPlan> mockPlans = Arrays.asList();
        when(travelPlanService.findAll()).thenReturn(mockPlans);
        servlet.doPost(request, response);
        verify(request).setAttribute(TravelPlanConstants.ATTR_ERROR, TravelPlanConstants.MSG_SEATS_MUST_BE_NUMBERS);
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLANS, mockPlans);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithMissingRequiredField() throws ServletException, IOException {
        when(request.getParameter(TravelPlanConstants.PARAM_ACTION)).thenReturn("");
        when(request.getParameter(TravelPlanConstants.PARAM_NAME)).thenReturn("Vacaciones");
        when(request.getParameter(TravelPlanConstants.PARAM_TYPE)).thenReturn(null);
        when(request.getParameter(TravelPlanConstants.PARAM_ORIGIN_CITY)).thenReturn("Barcelona");
        when(request.getParameter(TravelPlanConstants.PARAM_DESTINATION_CITY)).thenReturn("Madrid");
        when(request.getParameter(TravelPlanConstants.PARAM_ADULT_SEATS)).thenReturn("2");
        when(request.getParameter(TravelPlanConstants.PARAM_CHILD_SEATS)).thenReturn("1");
        Collection<TravelPlan> mockPlans = Arrays.asList();
        when(travelPlanService.findAll()).thenReturn(mockPlans);
        servlet.doPost(request, response);
        verify(request).setAttribute(TravelPlanConstants.ATTR_ERROR, TravelPlanConstants.MSG_ALL_FIELDS_REQUIRED);
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLANS, mockPlans);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithoutParameters() throws ServletException, IOException {
        Collection<TravelPlan> mockPlans = Arrays.asList(
                new TravelPlan("Plan 1", PlanType.NORMAL, 2, 1, "Barcelona", "Madrid")
        );
        when(travelPlanService.findAll()).thenReturn(mockPlans);
        servlet.doGet(request, response);
        verify(travelPlanService).findAll();
        verify(request).setAttribute(TravelPlanConstants.ATTR_PLANS, mockPlans);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithGroupedViewEmptyGroups() throws ServletException, IOException {
        when(request.getParameter(TravelPlanConstants.PARAM_VIEW)).thenReturn(TravelPlanConstants.VIEW_GROUPED);
        Map<String, Object> mockGroupedData = new HashMap<>();
        mockGroupedData.put(TravelPlanConstants.KEY_GROUPED, new HashMap<String, java.util.List<TravelPlan>>());
        mockGroupedData.put(TravelPlanConstants.KEY_UNIQUE, Arrays.asList());
        when(travelPlanService.getGroupedViewData()).thenReturn(mockGroupedData);
        servlet.doGet(request, response);
        verify(travelPlanService).getGroupedViewData();
        verify(request).setAttribute(TravelPlanConstants.ATTR_GROUPED_PLANS, mockGroupedData.get(TravelPlanConstants.KEY_GROUPED));
        verify(request).setAttribute(TravelPlanConstants.ATTR_UNIQUE_PLANS, mockGroupedData.get(TravelPlanConstants.KEY_UNIQUE));
        verify(requestDispatcher).forward(request, response);
    }
}
