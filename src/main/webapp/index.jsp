<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.sigma.travelplans.domain.TravelPlan" %>
<html>
<head>
    <title>Plan de viaje</title>
</head>
<body>
<h2>Plan de viaje</h2>
<%
    String error = (String) request.getAttribute("error");
    TravelPlan plan = (TravelPlan) request.getAttribute("plan");
    boolean editing = plan != null;
%>
<% if (error != null) { %>
<p style="color:red"><%= error %></p>
<% } %>
<form method="post" action="<%= request.getContextPath() %>/travel-plans">
    <input type="hidden" name="action" value="<%= editing ? "update" : "" %>"/>
    Nombre:<br/>
    <input type="text" name="name"
           value="<%= editing ? plan.getName() : "" %>"
           <%= editing ? "readonly" : "" %>/><br/><br/>
    Tipo:<br/>
    <select name="type">
        <option value="NORMAL" <%= editing && plan.getType().name().equals("NORMAL") ? "selected" : "" %>>Normal</option>
        <option value="WORK" <%= editing && plan.getType().name().equals("WORK") ? "selected" : "" %>>Trabajo</option>
    </select><br/><br/>
    Ciudad de origen:<br/>
    <input type="text" name="originCity"
           value="<%= editing ? plan.getOriginCity() : "" %>"/><br/><br/>
    Ciudad de destino:<br/>
    <input type="text" name="destinationCity"
           value="<%= editing ? plan.getDestinationCity() : "" %>"/><br/><br/>
    Asientos de adultos:<br/>
    <input type="number" name="adultSeats"
           value="<%= editing ? plan.getAdultSeats() : "" %>"/><br/><br/>
    Asientos de niños:<br/>
    <input type="number" name="childSeats"
           value="<%= editing ? plan.getChildSeats() : "" %>"/><br/><br/>
    <button type="submit"><%= editing ? "Actualizar" : "Crear" %></button>
</form>
</body>
</html>
