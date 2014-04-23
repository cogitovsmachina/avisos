<%@page import="com.mongodb.BasicDBObject"%>
<%@page import="java.util.ArrayList"%>
<%@page import="mx.org.cedn.avisosconagua.mongo.MongoInterface"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
        <title>CONAGUA Bulletin Builder</title>
        <link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
        <link rel="stylesheet" href="/css/font-awesome/font-awesome.min.css">
        <link rel="stylesheet" href="/css/application.css">
        <!--[if lt IE 9]>
          <script src="/js/html5shiv.js"></script>
          <script src="/js/respond.min.js"></script>
        <![endif]-->
    </head>
    <body>
        <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <a class="navbar-brand" href="/"><img class="logo img-responsive" src="css/img/CONAGUALOGO.png"/></a>
                </div>
            </div>
        </nav>
        <div class="container main-content">
            <div class="row text-center">
                <h3>Emisión de avisos de ciclones tropicales</h3>
            </div>
            <div class="row inner-container">
                <div class="row">
                    <div class="btn-group">
                        <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                            <span class="fa fa-plus fa-fw"></span>Nuevo
                        </button>
                        <ul class="dropdown-menu text-left" role="menu">
                            <li role="presentation" class="dropdown-header">Baja presión</li>
                            <li><a href="/ctrl/pacdp/init/new">Baja presión en el pacífico</a></li>
                            <li><a href="/ctrl/atldp/init/new">Baja presión en el atlántico</a></li>
                            <li role="presentation" class="dropdown-header">Ciclones tropicales</li>
                            <li><a href="/ctrl/pacht/init/new">Ciclón en el pacífico</a></li>
                            <li><a href="/ctrl/atlht/init/new">Ciclón en el atlántico</a></li>
                        </ul>
                    </div>
                </div>
                <br>
                <div class="row visible-md visible-lg">
                    <%
                    ArrayList<String> lista = MongoInterface.getInstance().listPublishedAdvices(20);
                    if (!lista.isEmpty()) {
                        %>
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>Título</th>
                                        <th>Fecha de emisión</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                    for(String aviso:lista){
                                        BasicDBObject avisoData = (BasicDBObject)MongoInterface.getInstance().getPublishedAdvice(aviso);
                                        String title = avisoData.getString("generatedTitle");
                                        String issueDate = avisoData.getString("issueDate");

                                        if (title==null)title="--";
                                        if (issueDate==null)issueDate="--";
                                        %>
                                        <tr>
                                            <td><%=title%></td>
                                            <td><%=issueDate%></td>
                                            <td>
                                                <a href="/getFile/<%=aviso%>_cap.xml" class="btn btn-sm btn-default" data-tooltip="tooltip" data-placement="bottom" data-original-title="Descargar CAP" title="Descargar CAP"><span class="fa fa-code fa-fw"></span></a>&nbsp;
                                                <a href="/getFile/<%=aviso%>.zip" class="btn btn-sm btn-default" data-tooltip="tooltip" data-placement="bottom" data-original-title="Descargar ZIP" title="Descargar ZIP"><span class="fa fa-archive fa-fw"></span></a>&nbsp;
                                                <a href="/ctrl/<%=avisoData.getString("adviceType")%>/generate/<%=aviso%>" class="btn btn-sm btn-default" data-tooltip="tooltip" data-placement="bottom" data-original-title="Regenerar aviso" title="Regenerar aviso"><span class="fa fa-refresh fa-fw"></span></a>&nbsp;
                                            </td>
                                        </tr>
                                        <%
                                    }
                                    %>
                                </tbody>
                            </table>
                        </div>
                        <%
                    } else {
                        %>
                        <div class="alert alert-warning">Aun no existen avisos de ciclones</div>
                        <%
                    }
                    %>
                </div>
            </div>
        </div>
        <script src="/js/libs/jquery/jquery.min.js" type="text/javascript"></script>
        <script src="/js/libs/bootstrap/bootstrap.min.js" type="text/javascript"></script>
        <script src="/js/application.js"></script>
    </body>
</html>
