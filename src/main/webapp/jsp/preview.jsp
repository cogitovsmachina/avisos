<%@page import="mx.org.cepdn.avisosconagua.util.Utils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

    String generatedHTML = (String)request.getAttribute("generatedHTML");
    boolean isdp = (Boolean)request.getAttribute("isdp");
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
        <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=drawing"></script>
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
            <div class="row inner-container">
                
                <%=
                        generatedHTML
                %>
                
            </div>
                <div class="row inner-container text-right">
                <div class="col-lg-12">
                    <form>
                        <button onclick="window.location = 'init';
                                return false;" class="btn btn-default"><span class="fa fa-pencil fa-fw"></span>Modificar</button>
                        <button onclick="window.location = 'generate';
                                return false;" class="btn btn-primary"><span class="fa fa-arrow-right fa-fw"></span>Continuar</button>
                    </form>
                </div>
            </div>
        </div>
        
        <script src="/js/libs/jquery/jquery.min.js" type="text/javascript"></script>
        <script src="/js/libs/bootstrap/bootstrap.min.js" type="text/javascript"></script>
        <script src="/js/application.js"></script>
        
    </body>
</html>