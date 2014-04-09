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
        <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=drawing"></script>
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
                    <li class="current">Situación actual</li><!--
                    --><li class="pending">Información de emisión</li><!--
                    --><li class="pending">Vista previa</li><!--
                    --><li class="pending">Publicación</li>
                </ol>
            </div>
            <div class="row inner-container">
                <form role="form" action="" method="post" enctype="multipart/form-data">
                    <div class="row">
                        <div class="col-lg-12 form-group">
                            <label class="control-label">Situación actual *</label>
                            <textarea name="eventDescription" class="ckeditor" data-required="true" data-description="common"></textarea>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Imagen de satélite *</label>
                            <div class="input-group">
                                <span class="input-group-btn">
                                    <span class="btn btn-success btn-file">
                                        Examinar<input type="file" name="issueSateliteImg" class="form-control" data-required="true" data-description="common"/>
                                    </span>
                                </span>
                                <input type="text" class="form-control" disabled/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Hora local tiempo del centro (Hora GMT) *</label>
                            <input name="issueLocalTime" type="text" class="form-control" data-required="true" data-description="common"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Ubicación del centro de la baja presión</label>
                            <div class="form-inline">
                                <input name="eventCLat" type="text" placeholder="Latitud norte" class="form-control"/>
                                <input name="eventCLon" type="text" placeholder="Longitud oeste" class="form-control"/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Distancia al lugar más cercano *</label>
                            <input name="eventDistance" type="text" class="form-control" data-required="true" data-description="common" data-describedby="_eventDistance"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <label class="control-label" for="area">Zona de alerta *</label>
                            <div id="map-canvas"></div>
                            <div id="map-data"></div>
                            <br>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Desplazamiento actual</label>
                            <input name="eventCurrentPath" type="text" class="form-control"/>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Vientos máximos &lpar;Km/h&rpar; *</label>
                            <div class="form-inline">
                                <input name="eventWindSpeedSust" type="text" placeholder="Sostenidos" class="form-control" data-required="true" data-description="common"/>
                                <input name="eventWndSpeedMax" type="text" placeholder="Rachas" class="form-control" data-required="true" data-description="common"/>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Presión mínima central &lpar;hPa&rpar;</label>
                            <input name="eventMinCP" type="text" class="form-control"/>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Potencial de desarrollo &lpar;%&rpar; *</label>
                            <div class="form-inline">
                                <input name="eventForecast48h" type="text" placeholder="48 horas" class="form-control" data-required="true" data-description="common"/>
                                <input name="eventForecast5d" type="text" placeholder="5 días" class="form-control" data-required="true" data-description="common"/>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12 form-group">
                            <label class="control-label">Pronóstico de lluvia *</label>
                            <textarea name="eventRainForecast" rows="7" class="form-control" data-required="true" data-description="common"></textarea>
                        </div>
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
        <script src="/js/libs/ckeditor/ckeditor.js" type="text/javascript"></script>
        <script src="/js/libs/ckeditor/adapters/jquery.js" type="text/javascript"></script>
        <script src="/js/map.js" type="text/javascript"></script>
        <script src="/js/application.js"></script>
        <script type="text/javascript">
            CKEDITOR.on('instanceReady', function() {
                $.each( CKEDITOR.instances, function(instance) {
                CKEDITOR.instances[instance].on("change", function(e) {
                    for ( instance in CKEDITOR.instances )
                        CKEDITOR.instances[instance].updateElement();
                    });
                });
            });
        </script>
    </body>
</html>