package com.sigma.travelplans.web;

import com.sigma.travelplans.TravelPlanConstants;
import com.sigma.travelplans.domain.PlanType;
import com.sigma.travelplans.domain.TravelPlan;
import com.sigma.travelplans.domain.service.TravelPlanService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class TravelPlanServlet extends HttpServlet {

    private TravelPlanService travelPlanService;

    @Override
    public void init() throws ServletException {
        this.travelPlanService = new TravelPlanService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter(TravelPlanConstants.PARAM_ACTION);
        String view = request.getParameter(TravelPlanConstants.PARAM_VIEW);

        handleEditAction(request, action);
        handleViewSelection(request, view);

        forwardToView(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter(TravelPlanConstants.PARAM_ACTION);

        if (TravelPlanConstants.ACTION_DELETE.equals(action)) {
            handleDelete(request, response);
        } else {
            handleCreateOrUpdate(request, response, action);
        }
    }

    private void handleEditAction(HttpServletRequest request, String action) {
        if (TravelPlanConstants.ACTION_EDIT.equals(action)) {
            String name = request.getParameter(TravelPlanConstants.PARAM_NAME);

            if (isBlank(name)) {
                request.setAttribute(TravelPlanConstants.ATTR_ERROR, TravelPlanConstants.MSG_NAME_REQUIRED);
            } else {
                TravelPlan plan = travelPlanService.findByName(name);
                if (plan == null) {
                    request.setAttribute(TravelPlanConstants.ATTR_ERROR, TravelPlanConstants.MSG_PLAN_NOT_FOUND + name);
                } else {
                    request.setAttribute(TravelPlanConstants.ATTR_PLAN, plan);
                }
            }
        }
    }

    private void handleViewSelection(HttpServletRequest request, String view) {
        if (TravelPlanConstants.VIEW_GROUPED.equals(view)) {
            Map<String, Object> groupedData = travelPlanService.getGroupedViewData();
            request.setAttribute(TravelPlanConstants.ATTR_GROUPED_PLANS, groupedData.get(TravelPlanConstants.KEY_GROUPED));
            request.setAttribute(TravelPlanConstants.ATTR_UNIQUE_PLANS, groupedData.get(TravelPlanConstants.KEY_UNIQUE));
        } else {
            request.setAttribute(TravelPlanConstants.ATTR_PLANS, travelPlanService.findAll());
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String name = request.getParameter(TravelPlanConstants.PARAM_NAME);

        try {
            travelPlanService.remove(name);
            redirectToMainPage(request, response);
        } catch (IllegalArgumentException e) {
            handleError(request, e.getMessage());
            forwardToView(request, response);
        }
    }

    private void handleCreateOrUpdate(HttpServletRequest request, HttpServletResponse response, String action)
            throws ServletException, IOException {

        try {
            TravelPlan travelPlan = parseTravelPlanFromRequest(request);

            if (TravelPlanConstants.ACTION_UPDATE.equals(action)) {
                travelPlanService.update(travelPlan);
                request.setAttribute(TravelPlanConstants.ATTR_SUCCESS, TravelPlanConstants.MSG_PLAN_UPDATED);
            } else {
                travelPlanService.add(travelPlan);
                request.setAttribute(TravelPlanConstants.ATTR_SUCCESS, TravelPlanConstants.MSG_PLAN_CREATED);
            }

            request.setAttribute(TravelPlanConstants.ATTR_PLANS, travelPlanService.findAll());
            forwardToView(request, response);

        } catch (IllegalArgumentException e) {
            handleError(request, e.getMessage());
            forwardToView(request, response);
        }
    }

    private TravelPlan parseTravelPlanFromRequest(HttpServletRequest request) {
        String name = getRequiredParameter(request, TravelPlanConstants.PARAM_NAME);
        String typeParam = getRequiredParameter(request, TravelPlanConstants.PARAM_TYPE);
        String originCity = getRequiredParameter(request, TravelPlanConstants.PARAM_ORIGIN_CITY);
        String destinationCity = getRequiredParameter(request, TravelPlanConstants.PARAM_DESTINATION_CITY);
        String adultSeatsStr = getRequiredParameter(request, TravelPlanConstants.PARAM_ADULT_SEATS);
        String childSeatsStr = getRequiredParameter(request, TravelPlanConstants.PARAM_CHILD_SEATS);

        int adultSeats = parseSeats(adultSeatsStr);
        int childSeats = parseSeats(childSeatsStr);
        PlanType type = PlanType.valueOf(typeParam);

        return new TravelPlan(
                name.trim(),
                type,
                adultSeats,
                childSeats,
                originCity.trim(),
                destinationCity.trim()
        );
    }

    private String getRequiredParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null) {
            throw new IllegalArgumentException(TravelPlanConstants.MSG_ALL_FIELDS_REQUIRED);
        }
        return value;
    }

    private int parseSeats(String seatsStr) {
        try {
            return Integer.parseInt(seatsStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(TravelPlanConstants.MSG_SEATS_MUST_BE_NUMBERS);
        }
    }

    private void redirectToMainPage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/travel-plans");
    }

    private void handleError(HttpServletRequest request, String errorMessage) {
        request.setAttribute(TravelPlanConstants.ATTR_ERROR, errorMessage);
        request.setAttribute(TravelPlanConstants.ATTR_PLANS, travelPlanService.findAll());
    }

    private void forwardToView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
