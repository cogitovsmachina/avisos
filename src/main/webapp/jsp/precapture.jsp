<%@page import="mx.org.cedn.avisosconagua.util.Utils"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
HashMap<String,String> data = (HashMap<String,String>)request.getAttribute("data");
String type = (String)request.getAttribute("bulletinType");
ArrayList<String> advicesList = (ArrayList<String>)request.getAttribute("advicesList");
%>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
        <title>CAP Architect v1</title>
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
                    <a class="navbar-brand" href="/"><img class="logo img-responsive" src="/css/img/CONAGUALOGO.png"/></a>
                </div>
            </div>
        </nav>
        <div class="container main-content">
            <h3 class="text-center"><%=Utils.getTituloBoletin(type)%></h3>
            <h4 class="text-center text-muted hidden-lg hidden-md">Información de emisión</h4>
            <div class="row progress-indicator-container text-center visible-lg visible-md">
                <ol class="progress-indicator">
                    <li class="current">Inicio</li><!--
                    --><li class="pending">Situación actual</li><!--
                    --><li class="pending">Predicción de avance</li><!--
                    --><li class="pending">Información de emisión</li><!--
                    --><li class="pending">Vista previa</li><!--
                    --><li class="pending">Publicación</li>
                </ol>
            </div>
            <div class="row inner-container">
                <form role="form" action="" method="post">
                    <div class="row">
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Liga al forecast advisory de <a target="_blank" href="http://www.nhc.noaa.gov/">NOAA&nbsp;<span class="fa fa-external-link"></span></a></label>
                            <input type="text" id="nhcLink" name="nhcForecastLink" value="<%=Utils.getValidFieldFromHash(data, "nhcForecastLink")%>" class="form-control" data-required="true" data-description="common">
                        </div>
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Liga al public advisory de <a target="_blank" href="http://www.nhc.noaa.gov/">NOAA&nbsp;<span class="fa fa-external-link"></span></a></label>
                            <input type="text" id="nhcLink" name="nhcPublicLink" value="<%=Utils.getValidFieldFromHash(data, "nhcPublicLink")%>" class="form-control" data-required="true" data-description="common">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 col-md-6 form-group">
                            <%
                            String selectedValue = Utils.getValidFieldFromHash(data, "previousIssue");
                            %>
                            <label class="control-label">En seguimiento al aviso</label>
                            <div class="input-group">
                                <select id="previousIssue" name="previousIssue" class="form-control">
                                    <option value="">Sin seguimiento</option>
                                    <%
                                    for(String adv : advicesList) {
                                        String pairs[] = adv.split("\\|");
                                        if (pairs.length==2) {
                                            %>
                                            <option value="<%=pairs[0]%>" <%=selectedValue.equalsIgnoreCase(pairs[0])?"selected":""%>><%=pairs[1]%></option>
                                            <%
                                        }
                                    }
                                    %>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row text-right">
                        <div class="col-lg-12 col-md-12">
                            <button class="btn btn-default"><span class="fa fa-times fa-fw"></span>Cancelar</button>
                            <button type="submit" class="btn btn-primary"><span class="fa fa-arrow-right fa-fw"></span>Continuar</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <div id="appModal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display:none;"></div>
        <script src="/js/libs/jquery/jquery.min.js" type="text/javascript"></script>
        <script src="/js/libs/jquery/jquery-validate.min.js" type="text/javascript"></script>
        <script src="/js/libs/bootstrap/bootstrap.min.js" type="text/javascript"></script>
        <script src="/js/application.js"></script>
    </body>
</html>