<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sigma.travelplans.domain.TravelPlan" %>
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    TravelPlan editPlan = (TravelPlan) request.getAttribute("plan");
    boolean editing = editPlan != null;

    Collection<TravelPlan> allPlans = (Collection<TravelPlan>) request.getAttribute("plans");
    Map<String, List<TravelPlan>> groupedPlans = (Map<String, List<TravelPlan>>) request.getAttribute("groupedPlans");
    List<TravelPlan> uniquePlans = (List<TravelPlan>) request.getAttribute("uniquePlans");
    boolean isGroupedView = groupedPlans != null;
%>
<html>
<head>
    <title>Gestión de Planes de Viaje</title>
</head>
<body>
    <h1>Gestión de Planes de Viaje</h1>

    <!-- Formulario -->
    <h2><%= editing ? "Editar Plan" : "Nuevo Plan" %></h2>

    <% if (error != null) { %>
        <p style="color: red;"><%= error %></p>
    <% } %>

    <% if (success != null) { %>
        <p style="color: green;"><%= success %></p>
    <% } %>

    <form method="post" action="travel-plans">
        <input type="hidden" name="action" value="<%= editing ? "update" : "create" %>"/>

        <div>
            <label>Nombre:</label><br>
            <input type="text" name="name" value="<%= editing ? editPlan.getName() : "" %>" <%= editing ? "readonly" : "" %> required>
        </div>

        <div>
            <label>Tipo:</label><br>
            <select name="type" required>
                <option value="">Seleccionar...</option>
                <option value="NORMAL" <%= editing && "NORMAL".equals(editPlan.getType().name()) ? "selected" : "" %>>Normal</option>
                <option value="WORK" <%= editing && "WORK".equals(editPlan.getType().name()) ? "selected" : "" %>>Trabajo</option>
            </select>
        </div>

        <div>
            <label>Origen:</label><br>
            <input type="text" name="originCity" value="<%= editing ? editPlan.getOriginCity() : "" %>" required>
        </div>

        <div>
            <label>Destino:</label><br>
            <input type="text" name="destinationCity" value="<%= editing ? editPlan.getDestinationCity() : "" %>" required>
        </div>

        <div>
            <label>Adultos:</label><br>
            <input type="number" name="adultSeats" value="<%= editing ? editPlan.getAdultSeats() : "" %>" min="0" required>
        </div>

        <div>
            <label>Niños:</label><br>
            <input type="number" name="childSeats" value="<%= editing ? editPlan.getChildSeats() : "" %>" min="0" required>
        </div>

        <div>
            <button type="submit"><%= editing ? "Actualizar" : "Crear" %></button>
            <% if (editing) { %>
                <a href="travel-plans">Cancelar</a>
            <% } %>
        </div>
    </form>

    <!-- Botones de Agrupación -->
    <div>
        <% if (!isGroupedView) { %>
            <form method="get" action="travel-plans" style="display: inline;">
                <input type="hidden" name="view" value="grouped"/>
                <button type="submit">Agrupar Planes Compatibles</button>
            </form>
        <% } else { %>
            <form method="get" action="travel-plans" style="display: inline;">
                <button type="submit">Ver Todos los Planes</button>
            </form>
        <% } %>
    </div>

    <!-- Tablas -->
    <% if (isGroupedView) { %>
        <!-- Vista Agrupada -->
        <h2>Planes Compatibles Agrupados</h2>

        <% if (groupedPlans != null && !groupedPlans.isEmpty()) { %>
            <% for (Map.Entry<String, List<TravelPlan>> group : groupedPlans.entrySet()) {
                if (group.getValue().size() > 1) { %>
                    <h3>Grupo: <%= group.getKey() %> (<%= group.getValue().size() %> planes)</h3>
                    <table border="1">
                        <tr>
                            <th>Nombre</th>
                            <th>Tipo</th>
                            <th>Origen</th>
                            <th>Destino</th>
                            <th>Adultos</th>
                            <th>Niños</th>
                            <th>Acciones</th>
                        </tr>
                        <% for (TravelPlan plan : group.getValue()) { %>
                            <tr>
                                <td><%= plan.getName() %></td>
                                <td><%= plan.getType() %></td>
                                <td><%= plan.getOriginCity() %></td>
                                <td><%= plan.getDestinationCity() %></td>
                                <td><%= plan.getAdultSeats() %></td>
                                <td><%= plan.getChildSeats() %></td>
                                <td>
                                    <form method="get" action="travel-plans" style="display: inline;">
                                        <input type="hidden" name="action" value="edit"/>
                                        <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                        <button type="submit">Editar</button>
                                    </form>
                                    <form method="post" action="travel-plans" style="display: inline;">
                                        <input type="hidden" name="action" value="delete"/>
                                        <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                        <button type="submit" onclick="return confirm('¿Eliminar?')">Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    </table>
                    <br>
                <% } %>
            <% } %>
        <% } else { %>
            <p>No hay grupos de planes compatibles.</p>
        <% } %>

        <!-- Planes Únicos -->
        <h2>Planes Únicos</h2>
        <% if (uniquePlans != null && !uniquePlans.isEmpty()) { %>
            <table border="1">
                <tr>
                    <th>Nombre</th>
                    <th>Tipo</th>
                    <th>Origen</th>
                    <th>Destino</th>
                    <th>Adultos</th>
                    <th>Niños</th>
                    <th>Acciones</th>
                </tr>
                <% for (TravelPlan plan : uniquePlans) { %>
                    <tr>
                        <td><%= plan.getName() %></td>
                        <td><%= plan.getType() %></td>
                        <td><%= plan.getOriginCity() %></td>
                        <td><%= plan.getDestinationCity() %></td>
                        <td><%= plan.getAdultSeats() %></td>
                        <td><%= plan.getChildSeats() %></td>
                        <td>
                            <form method="get" action="travel-plans" style="display: inline;">
                                <input type="hidden" name="action" value="edit"/>
                                <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                <button type="submit">Editar</button>
                            </form>
                            <form method="post" action="travel-plans" style="display: inline;">
                                <input type="hidden" name="action" value="delete"/>
                                <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                <button type="submit" onclick="return confirm('¿Eliminar?')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } else { %>
            <p>No hay planes únicos.</p>
        <% } %>

    <% } else { %>
        <!-- Vista Normal -->
        <h2>Todos los Planes (<%= allPlans != null ? allPlans.size() : 0 %>)</h2>

        <% if (allPlans != null && !allPlans.isEmpty()) { %>
            <table border="1">
                <tr>
                    <th>Nombre</th>
                    <th>Tipo</th>
                    <th>Origen</th>
                    <th>Destino</th>
                    <th>Adultos</th>
                    <th>Niños</th>
                    <th>Acciones</th>
                </tr>
                <% for (TravelPlan plan : allPlans) { %>
                    <tr>
                        <td><%= plan.getName() %></td>
                        <td><%= plan.getType() %></td>
                        <td><%= plan.getOriginCity() %></td>
                        <td><%= plan.getDestinationCity() %></td>
                        <td><%= plan.getAdultSeats() %></td>
                        <td><%= plan.getChildSeats() %></td>
                        <td>
                            <form method="get" action="travel-plans" style="display: inline;">
                                <input type="hidden" name="action" value="edit"/>
                                <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                <button type="submit">Editar</button>
                            </form>
                            <form method="post" action="travel-plans" style="display: inline;">
                                <input type="hidden" name="action" value="delete"/>
                                <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                <button type="submit" onclick="return confirm('¿Eliminar?')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } else { %>
            <p>No hay planes creados.</p>
        <% } %>
    <% } %>
</body>
</html>
