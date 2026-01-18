<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.sigma.travelplans.domain.TravelPlan" %>
<html>
<head>
    <title>Planes de viaje</title>
</head>
<body>
<h2>Planes de viaje</h2>
<table border="1">
    <tr>
        <th>Nombre</th>
        <th>Tipo</th>
        <th>Origen</th>
        <th>Destino</th>
        <th>Adultos</th>
        <th>Niños</th>
    </tr>
<%
    Collection<TravelPlan> plans =
            (Collection<TravelPlan>) request.getAttribute("plans");

    if (plans != null) {
        for (TravelPlan plan : plans) {
%>
    <tr>
        <td><%= plan.getName() %></td>
        <td><%= plan.getType() %></td>
        <td><%= plan.getOriginCity() %></td>
        <td><%= plan.getDestinationCity() %></td>
        <td><%= plan.getAdultSeats() %></td>
        <td><%= plan.getChildSeats() %></td>
    </tr>
<%
        }
    }
%>
</table>
<a href="<%= request.getContextPath() %>/">Volver</a>
</body>
</html>
