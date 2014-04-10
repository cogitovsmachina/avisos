<%@page import="mx.org.cepdn.avisosconagua.util.Utils"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
HashMap<String,String> data = (HashMap<String,String>)request.getAttribute("data");
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
                    <a class="navbar-brand" href="#">CONAGUA</a>
                </div>
                <div class="collapse navbar-collapse">
                    <p class="navbar-text navbar-right"><span class="fa fa-user fa-fw"></span>Jaime Albarr&aacute;n Ascencio&nbsp;<span class="caret"></span></p>
                </div>
            </div>
        </nav>
        <div class="container main-content">
            <h3>Emitir nuevo boletín</h3>
            <div class="row progress-indicator-container text-center">
                <ol class="progress-indicator">
                    <li class="done">Situación actual</li><!--
                    --><li class="current">Predicción de avance</li><!--
                    --><li class="pending">Seguimiento</li><!--
                    --><li class="pending">Información de emisión</li><!--
                    --><li class="pending">Vista previa</li><!--
                    --><li class="pending">Publicación</li>
                </ol>
            </div>
            <div class="row inner-container">
                <form role="form" action="" method="post" enctype="multipart/form-data">
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Mapa de localización*</label>
                            <div class="input-group">
                                <span class="input-group-btn">
                                    <span class="btn btn-primary btn-file">
                                        Examinar<input type="file" name="issueSateliteLocationImg" value="<%=Utils.getValidFieldFromHash(data, "issueSateliteLocationImg")%>" class="form-control" data-required="true" data-description="common"/>
                                    </span>
                                </span>
                                <input type="text" class="form-control" disabled/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Enlace al forecast de NHC</label>
                            <div class="input-group">
                                <input type="text" name="nhcLink" class="form-control">
                                <span class="input-group-btn">
                                    <button class="btn btn-default" type="button">Cargar</button>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Pronóstico válido<br>al día/hora local<br>tiempo del centro</th>
                                    <th class="text-center">Latitud norte</th>
                                    <th class="text-center">Longitud oeste</th>
                                    <th class="text-center">Vientos (Km/h)<br>SOST./RACHAS</th>
                                    <th class="text-center">Categoría</th>
                                    <th class="text-center">Ubicación (Km)</th>
                                </tr>
                            </thead>
                            <tbody>
                                
                            </tbody>
                        </table>
                    </div>
                    <div class="row text-right">
                        <div class="col-lg-12">
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