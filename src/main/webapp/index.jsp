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
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestión de Planes de Viaje</title>
    <!-- Bootstrap 3.4.1 -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <!-- Estilos personalizados -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="container">
    <!-- Header -->
    <div class="header-section">
        <h1 class="text-primary">Gestión de Planes de Viaje</h1>
        <p class="lead">Crea y gestiona tus planes de viaje</p>
    </div>

    <!-- Messages -->
    <% if (error != null) { %>
        <div class="alert alert-danger alert-dismissible" role="alert">
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
            <strong>Error:</strong> <%= error %>
        </div>
    <% } %>

    <% if (success != null) { %>
        <div class="alert alert-success alert-dismissible" role="alert">
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
            <strong>Éxito:</strong> <%= success %>
        </div>
    <% } %>

    <!-- Form Section -->
    <div class="form-section">
        <h2><%= editing ? "✏️ Editar Plan" : "➕ Nuevo Plan" %></h2>

        <form method="post" action="travel-plans" class="form-horizontal">
            <input type="hidden" name="action" value="<%= editing ? "update" : "create" %>"/>

            <div class="form-group">
                <label class="col-sm-2 control-label">Nombre:</label>
                <div class="col-sm-10">
                    <input type="text" name="name" class="form-control"
                           value="<%= editing ? editPlan.getName() : "" %>"
                           <%= editing ? "readonly" : "" %>
                           required placeholder="Nombre del plan">
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">Tipo:</label>
                <div class="col-sm-10">
                    <select name="type" class="form-control" required>
                        <option value="">Seleccionar tipo...</option>
                        <option value="NORMAL" <%= editing && "NORMAL".equals(editPlan.getType().name()) ? "selected" : "" %>>Normal</option>
                        <option value="WORK" <%= editing && "WORK".equals(editPlan.getType().name()) ? "selected" : "" %>>Trabajo</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">Origen:</label>
                <div class="col-sm-10">
                    <input type="text" name="originCity" class="form-control"
                           value="<%= editing ? editPlan.getOriginCity() : "" %>"
                           required placeholder="Ciudad de origen">
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">Destino:</label>
                <div class="col-sm-10">
                    <input type="text" name="destinationCity" class="form-control"
                           value="<%= editing ? editPlan.getDestinationCity() : "" %>"
                           required placeholder="Ciudad de destino">
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">Adultos:</label>
                <div class="col-sm-10">
                    <input type="number" name="adultSeats" class="form-control"
                           value="<%= editing ? editPlan.getAdultSeats() : "" %>"
                           min="0" required placeholder="Número de adultos">
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">Niños:</label>
                <div class="col-sm-10">
                    <input type="number" name="childSeats" class="form-control"
                           value="<%= editing ? editPlan.getChildSeats() : "" %>"
                           min="0" required placeholder="Número de niños">
                </div>
            </div>

            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn <%= editing ? "btn-warning" : "btn-success" %>">
                        <%= editing ? "📝 Actualizar Plan" : "✅ Crear Plan" %>
                    </button>
                    <% if (editing) { %>
                        <a href="travel-plans" class="btn btn-default">❌ Cancelar</a>
                    <% } %>
                </div>
            </div>
        </form>
    </div>

    <!-- Grouping Buttons -->
    <div class="action-buttons text-center">
        <% if (!isGroupedView) { %>
            <form method="get" action="travel-plans" class="inline-form">
                <input type="hidden" name="view" value="grouped"/>
                <button type="submit" class="btn btn-primary btn-lg">
                    🔗 Agrupar Planes Compatibles
                </button>
            </form>
        <% } else { %>
            <form method="get" action="travel-plans" class="inline-form">
                <button type="submit" class="btn btn-info btn-lg">
                    📋 Ver Todos los Planes
                </button>
            </form>
        <% } %>
    </div>

    <!-- Tables Section -->
    <div class="table-section">
        <% if (isGroupedView) { %>
            <!-- Grouped View -->
            <div class="page-header">
                <h2>📊 Planes Compatibles Agrupados</h2>
            </div>

            <% if (groupedPlans != null && !groupedPlans.isEmpty()) { %>
                <% for (Map.Entry<String, List<TravelPlan>> group : groupedPlans.entrySet()) {
                    if (group.getValue().size() > 1) { %>
                        <div class="group-header">
                            <h4><%= group.getKey() %></h4>
                        </div>
                        <table class="table table-bordered table-hover">
                            <thead class="thead-light">
                                <tr>
                                    <th>Nombre</th>
                                    <th>Tipo</th>
                                    <th>Origen</th>
                                    <th>Destino</th>
                                    <th>Adultos</th>
                                    <th>Niños</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (TravelPlan plan : group.getValue()) { %>
                                    <tr>
                                        <td><strong><%= plan.getName() %></strong></td>
                                        <td>
                                            <span class="label <%= plan.getType().name().equals("WORK") ? "label-danger" : "label-success" %>">
                                                <%= plan.getType() %>
                                            </span>
                                        </td>
                                        <td><%= plan.getOriginCity() %></td>
                                        <td><%= plan.getDestinationCity() %></td>
                                        <td><span class="badge"><%= plan.getAdultSeats() %></span></td>
                                        <td><span class="badge"><%= plan.getChildSeats() %></span></td>
                                        <td>
                                            <form method="get" action="travel-plans" class="inline-form">
                                                <input type="hidden" name="action" value="edit"/>
                                                <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                                <button type="submit" class="btn btn-warning btn-xs">✏️ Editar</button>
                                            </form>
                                            <form method="post" action="travel-plans" class="inline-form">
                                                <input type="hidden" name="action" value="delete"/>
                                                <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                                <button type="submit" class="btn btn-danger btn-xs" onclick="return confirm('¿Eliminar este plan?')">🗑️ Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                        <br>
                    <% } %>
                <% } %>
            <% } else { %>
                <div class="alert alert-info">
                    <strong>Información:</strong> No hay grupos de planes compatibles.
                </div>
            <% } %>

            <!-- Unique Plans -->
            <div class="page-header">
                <h2>⭐ Planes Únicos</h2>
                <p class="lead">Planes sin compatibilidad con otros</p>
            </div>

            <% if (uniquePlans != null && !uniquePlans.isEmpty()) { %>
                <table class="table table-bordered table-hover">
                    <thead class="thead-light">
                        <tr>
                            <th>Nombre</th>
                            <th>Tipo</th>
                            <th>Origen</th>
                            <th>Destino</th>
                            <th>Adultos</th>
                            <th>Niños</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (TravelPlan plan : uniquePlans) { %>
                            <tr>
                                <td><strong><%= plan.getName() %></strong></td>
                                <td>
                                    <span class="label <%= plan.getType().name().equals("WORK") ? "label-danger" : "label-success" %>">
                                        <%= plan.getType() %>
                                    </span>
                                </td>
                                <td><%= plan.getOriginCity() %></td>
                                <td><%= plan.getDestinationCity() %></td>
                                <td><span class="badge"><%= plan.getAdultSeats() %></span></td>
                                <td><span class="badge"><%= plan.getChildSeats() %></span></td>
                                <td>
                                    <form method="get" action="travel-plans" class="inline-form">
                                        <input type="hidden" name="action" value="edit"/>
                                        <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                        <button type="submit" class="btn btn-warning btn-xs">✏️ Editar</button>
                                    </form>
                                    <form method="post" action="travel-plans" class="inline-form">
                                        <input type="hidden" name="action" value="delete"/>
                                        <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                        <button type="submit" class="btn btn-danger btn-xs" onclick="return confirm('¿Eliminar este plan?')">🗑️ Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <div class="alert alert-info">
                    <strong>Información:</strong> No hay planes únicos.
                </div>
            <% } %>

        <% } else { %>
            <!-- Normal View -->
            <div class="page-header">
                <h2>📋 Todos los Planes <span class="badge"><%= allPlans != null ? allPlans.size() : 0 %></span></h2>
            </div>

            <% if (allPlans != null && !allPlans.isEmpty()) { %>
                <table class="table table-bordered table-hover table-striped">
                    <thead class="thead-dark">
                        <tr>
                            <th>Nombre</th>
                            <th>Tipo</th>
                            <th>Origen</th>
                            <th>Destino</th>
                            <th>Adultos</th>
                            <th>Niños</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (TravelPlan plan : allPlans) { %>
                            <tr>
                                <td><strong><%= plan.getName() %></strong></td>
                                <td>
                                    <span class="label <%= plan.getType().name().equals("WORK") ? "label-danger" : "label-success" %>">
                                        <%= plan.getType() %>
                                    </span>
                                </td>
                                <td><%= plan.getOriginCity() %></td>
                                <td><%= plan.getDestinationCity() %></td>
                                <td><span class="badge"><%= plan.getAdultSeats() %></span></td>
                                <td><span class="badge"><%= plan.getChildSeats() %></span></td>
                                <td>
                                    <form method="get" action="travel-plans" class="inline-form">
                                        <input type="hidden" name="action" value="edit"/>
                                        <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                        <button type="submit" class="btn btn-warning btn-xs">✏️ Editar</button>
                                    </form>
                                    <form method="post" action="travel-plans" class="inline-form">
                                        <input type="hidden" name="action" value="delete"/>
                                        <input type="hidden" name="name" value="<%= plan.getName() %>"/>
                                        <button type="submit" class="btn btn-danger btn-xs" onclick="return confirm('¿Eliminar este plan?')">🗑️ Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <div class="alert alert-warning">
                    <strong>Atención:</strong> No hay planes de viaje creados. ¡Crea el primero!
                </div>
            <% } %>
        <% } %>
    </div>
</div>

<!-- Bootstrap JS and dependencies -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</body>
</html>
