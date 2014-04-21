<%@page import="mx.org.cedn.avisosconagua.util.Utils"%>
<%@page contentType="text/html" pageEncoding="utf-8"%>
<%
    String titulo = (String)request.getAttribute("titulo");
    String capURL = (String)request.getAttribute("capURL");
    String htmlUrl = (String)request.getAttribute("htmlUrl");
    String type = (String)request.getAttribute("bulletinType");
    boolean isdp = (Boolean)request.getAttribute("isdp");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
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
            <div class="row text-center">
                <h3 class="text-center"><%=Utils.getTituloBoletin(type)%></h3>
                <div class="row progress-indicator-container text-center">
                <ol class="progress-indicator">
                    <li class="done">Situación actual</li><!--
                    <%
                        if (!isdp){ %>--><li class="done">Predicción de avance</li><!--
                    <% }
                    %>--><li class="done">Información de emisión</li><!--
                    --><li class="current">Vista previa</li><!--
                    --><li class="pending">Publicación</li>
                </ol>
            </div>
            </div>
            <div class="row inner-container">
                <hr>
                <p class="text-center">
                    Se han generado los archivos necesarios para la publicación de su boletín<br>
                    <strong><%=titulo%></strong><br>
                    Podrá descargarlos haciendo click en los botones correspondientes
                </p>
                <p class="text-center">
                    <a class="btn btn-lg btn-default" href="<%=capURL%>"><span class="fa fa-code fa-2x"></span><br>Archivo CAP</a>
                    <a class="btn btn-lg btn-default" href="<%=htmlUrl%>"><span class="fa fa-file-text fa-2x"></span><br>Archivo HTML</a>
                </p>
                <hr>
                <p class="text-center">
                    <button onclick="window.location='init';" class="btn btn-default">Regresar al inicio</button>
                    <button onclick="window.location='/';" class="btn btn-primary">Emitir otro boletín</button>
                </p>
            </div>
        </div>
        <script src="/js/libs/jquery/jquery.min.js"></script>
        <script src="/js/libs/bootstrap/bootstrap.min.js"></script>
        <script src="/js/application.js"></script>
    </body>
</html>