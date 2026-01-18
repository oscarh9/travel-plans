package com.sigma.travelplans.web;

import com.sigma.travelplans.domain.PlanType;
import com.sigma.travelplans.domain.TravelPlan;
import com.sigma.travelplans.domain.service.TravelPlanService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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

        String action = request.getParameter("action");
        String view = request.getParameter("view");

        if ("edit".equals(action)) {
            String name = request.getParameter("name");

            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "Nombre requerido para editar");
            } else {
                TravelPlan plan = travelPlanService.findByName(name);
                if (plan == null) {
                    request.setAttribute("error", "Plan no encontrado: " + name);
                } else {
                    request.setAttribute("plan", plan);
                }
            }
        }

        if ("grouped".equals(view)) {
            Map<String, Object> groupedData = travelPlanService.getGroupedViewData();

            @SuppressWarnings("unchecked")
            Map<String, List<TravelPlan>> groupedPlans = (Map<String, List<TravelPlan>>) groupedData.get("grouped");

            @SuppressWarnings("unchecked")
            List<TravelPlan> uniquePlans = (List<TravelPlan>) groupedData.get("unique");

            request.setAttribute("groupedPlans", groupedPlans);
            request.setAttribute("uniquePlans", uniquePlans);
        } else {
            request.setAttribute("plans", travelPlanService.findAll());
        }

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            handleDelete(request, response);
            return;
        }

        handleCreateOrUpdate(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String name = request.getParameter("name");

        try {
            travelPlanService.remove(name);
            response.sendRedirect(request.getContextPath() + "/travel-plans");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("plans", travelPlanService.findAll());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    private void handleCreateOrUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            String name = request.getParameter("name");
            String typeParam = request.getParameter("type");
            String originCity = request.getParameter("originCity");
            String destinationCity = request.getParameter("destinationCity");
            String adultSeatsStr = request.getParameter("adultSeats");
            String childSeatsStr = request.getParameter("childSeats");

            if (name == null || typeParam == null || originCity == null ||
                    destinationCity == null || adultSeatsStr == null || childSeatsStr == null) {
                throw new IllegalArgumentException("Todos los campos son obligatorios");
            }

            int adultSeats;
            int childSeats;
            try {
                adultSeats = Integer.parseInt(adultSeatsStr.trim());
                childSeats = Integer.parseInt(childSeatsStr.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Los asientos deben ser números válidos");
            }

            PlanType type = PlanType.valueOf(typeParam);

            TravelPlan travelPlan = new TravelPlan(
                    name.trim(),
                    type,
                    adultSeats,
                    childSeats,
                    originCity.trim(),
                    destinationCity.trim()
            );

            if ("update".equals(action)) {
                travelPlanService.update(travelPlan);
                request.setAttribute("success", "Plan actualizado correctamente");
            } else {
                travelPlanService.add(travelPlan);
                request.setAttribute("success", "Plan creado correctamente");
            }

            request.setAttribute("plans", travelPlanService.findAll());
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("plans", travelPlanService.findAll());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
