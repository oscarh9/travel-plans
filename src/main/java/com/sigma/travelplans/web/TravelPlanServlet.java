package com.sigma.travelplans.web;

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

    private static final String PARAM_ACTION = "action";
    private static final String PARAM_VIEW = "view";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_ORIGIN_CITY = "originCity";
    private static final String PARAM_DESTINATION_CITY = "destinationCity";
    private static final String PARAM_ADULT_SEATS = "adultSeats";
    private static final String PARAM_CHILD_SEATS = "childSeats";

    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_UPDATE = "update";
    private static final String VIEW_GROUPED = "grouped";

    private static final String ATTR_ERROR = "error";
    private static final String ATTR_SUCCESS = "success";
    private static final String ATTR_PLAN = "plan";
    private static final String ATTR_PLANS = "plans";
    private static final String ATTR_GROUPED_PLANS = "groupedPlans";
    private static final String ATTR_UNIQUE_PLANS = "uniquePlans";

    private static final String MSG_NAME_REQUIRED = "Nombre requerido para editar";
    private static final String MSG_PLAN_NOT_FOUND = "Plan no encontrado: ";
    private static final String MSG_ALL_FIELDS_REQUIRED = "Todos los campos son obligatorios";
    private static final String MSG_SEATS_MUST_BE_NUMBERS = "Los asientos deben ser números válidos";
    private static final String MSG_PLAN_UPDATED = "Plan actualizado correctamente";
    private static final String MSG_PLAN_CREATED = "Plan creado correctamente";

    private TravelPlanService travelPlanService;

    @Override
    public void init() throws ServletException {
        this.travelPlanService = new TravelPlanService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter(PARAM_ACTION);
        String view = request.getParameter(PARAM_VIEW);

        handleEditAction(request, action);
        handleViewSelection(request, view);

        forwardToView(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter(PARAM_ACTION);

        if (ACTION_DELETE.equals(action)) {
            handleDelete(request, response);
        } else {
            handleCreateOrUpdate(request, response, action);
        }
    }

    private void handleEditAction(HttpServletRequest request, String action) {
        if (ACTION_EDIT.equals(action)) {
            String name = request.getParameter(PARAM_NAME);

            if (isBlank(name)) {
                request.setAttribute(ATTR_ERROR, MSG_NAME_REQUIRED);
            } else {
                TravelPlan plan = travelPlanService.findByName(name);
                if (plan == null) {
                    request.setAttribute(ATTR_ERROR, MSG_PLAN_NOT_FOUND + name);
                } else {
                    request.setAttribute(ATTR_PLAN, plan);
                }
            }
        }
    }

    private void handleViewSelection(HttpServletRequest request, String view) {
        if (VIEW_GROUPED.equals(view)) {
            Map<String, Object> groupedData = travelPlanService.getGroupedViewData();
            request.setAttribute(ATTR_GROUPED_PLANS, groupedData.get("grouped"));
            request.setAttribute(ATTR_UNIQUE_PLANS, groupedData.get("unique"));
        } else {
            request.setAttribute(ATTR_PLANS, travelPlanService.findAll());
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String name = request.getParameter(PARAM_NAME);

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

            if (ACTION_UPDATE.equals(action)) {
                travelPlanService.update(travelPlan);
                request.setAttribute(ATTR_SUCCESS, MSG_PLAN_UPDATED);
            } else {
                travelPlanService.add(travelPlan);
                request.setAttribute(ATTR_SUCCESS, MSG_PLAN_CREATED);
            }

            request.setAttribute(ATTR_PLANS, travelPlanService.findAll());
            forwardToView(request, response);

        } catch (IllegalArgumentException e) {
            handleError(request, e.getMessage());
            forwardToView(request, response);
        }
    }

    private TravelPlan parseTravelPlanFromRequest(HttpServletRequest request) {
        String name = getRequiredParameter(request, PARAM_NAME);
        String typeParam = getRequiredParameter(request, PARAM_TYPE);
        String originCity = getRequiredParameter(request, PARAM_ORIGIN_CITY);
        String destinationCity = getRequiredParameter(request, PARAM_DESTINATION_CITY);
        String adultSeatsStr = getRequiredParameter(request, PARAM_ADULT_SEATS);
        String childSeatsStr = getRequiredParameter(request, PARAM_CHILD_SEATS);

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
            throw new IllegalArgumentException(MSG_ALL_FIELDS_REQUIRED);
        }
        return value;
    }

    private int parseSeats(String seatsStr) {
        try {
            return Integer.parseInt(seatsStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MSG_SEATS_MUST_BE_NUMBERS);
        }
    }

    private void redirectToMainPage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/travel-plans");
    }

    private void handleError(HttpServletRequest request, String errorMessage) {
        request.setAttribute(ATTR_ERROR, errorMessage);
        request.setAttribute(ATTR_PLANS, travelPlanService.findAll());
    }

    private void forwardToView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
