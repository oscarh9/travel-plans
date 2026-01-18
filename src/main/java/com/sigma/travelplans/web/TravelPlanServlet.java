package com.sigma.travelplans.web;

import com.sigma.travelplans.domain.PlanType;
import com.sigma.travelplans.domain.TravelPlan;
import com.sigma.travelplans.domain.service.TravelPlanService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TravelPlanServlet extends HttpServlet {

    private TravelPlanService travelPlanService;

    @Override
    public void init() throws ServletException {
        this.travelPlanService = new TravelPlanService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            String name = request.getParameter("name");
            String typeParam = request.getParameter("type");
            String originCity = request.getParameter("originCity");
            String destinationCity = request.getParameter("destinationCity");
            int adultSeats = Integer.parseInt(request.getParameter("adultSeats"));
            int childSeats = Integer.parseInt(request.getParameter("childSeats"));

            PlanType type = PlanType.valueOf(typeParam);

            TravelPlan travelPlan = new TravelPlan(
                    name,
                    type,
                    adultSeats,
                    childSeats,
                    originCity,
                    destinationCity
            );

            travelPlanService.add(travelPlan);

            response.sendRedirect(request.getContextPath() + "/travel-plans");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
        }
    }
}
