package com.sigma.travelplans.web;

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

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
