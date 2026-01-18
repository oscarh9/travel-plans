<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Crear plan de viaje</title>
</head>
<body>
<h2>Crear plan de viaje</h2>
<%
    String error = (String) request.getAttribute("error");
    if (error != null) {
%>
    <p style="color:red"><%= error %></p>
<%
    }
%>
<form method="post" action="<%= request.getContextPath() %>/travel-plans">
    Nombre:<br/>
    <input type="text" name="name"/><br/><br/>
    Tipo:<br/>
    <select name="type">
        <option value="NORMAL">Normal</option>
        <option value="WORK">Trabajo</option>
    </select><br/><br/>
    Ciudad de origen:<br/>
    <input type="text" name="originCity"/><br/><br/>
    Ciudad de destino:<br/>
    <input type="text" name="destinationCity"/><br/><br/>
    Asientos de adultos:<br/>
    <input type="number" name="adultSeats"/><br/><br/>
    Asientos de niños:<br/>
    <input type="number" name="childSeats"/><br/><br/>
    <button type="submit">Crear</button>
</form>
</body>
</html>
