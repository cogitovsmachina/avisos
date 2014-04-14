<%@page import="mx.org.cepdn.avisosconagua.util.Utils"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
HashMap<String,String> data = (HashMap<String,String>)request.getAttribute("data");
String type = (String)request.getAttribute("bulletinType");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
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
            <h4 class="text-center text-muted hidden-lg hidden-md">Seguimiento</h4>
            <div class="row progress-indicator-container text-center visible-lg visible-md">
                <ol class="progress-indicator">
                    <li class="done">Situación actual</li><!--
                    --><li class="done">Predicción de avance</li><!--
                    --><li class="current">Seguimiento</li><!--
                    --><li class="pending">Información de emisión</li><!--
                    --><li class="pending">Vista previa</li><!--
                    --><li class="pending">Publicación</li>
                </ol>
            </div>
            <div class="row inner-container">
                <form role="form" action="" method="post">
                    <div class="row">
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">En seguimiento a</label>
                            <div class="input-group">
                                <select name="previousIssue" class="form-control">
                                    <option value=""></option>
                                </select>
                                <span class="input-group-btn">
                                    <button class="btn btn-default" type="button">Cargar</button>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12 col-md-12 table-responsive">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th class="text-center">Aviso No.</th>
                                        <th class="text-center">Fecha/Hora local<br>CDT</th>
                                        <th class="text-center">Lat. norte</th>
                                        <th class="text-center">Long. oeste</th>
                                        <th class="text-center">Distancia más cercana<br>(Km)</th>
                                        <th class="text-center">Viento máx./rachas</th>
                                        <th class="text-center">Categoría</th>
                                        <th class="text-center">Avance</th>
                                    </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
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