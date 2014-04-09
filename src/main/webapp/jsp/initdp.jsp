<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%!
private String getValidValue(HashMap<String, String> map, String key) {
    String ret = map.get(key);
    if (ret == null) {
        ret = "";
    }
    return ret;
}
%>
<%
String regex = "((\\+|\\-)?\\d+.?\\d*),\\s*((\\+|\\-)?\\d+.?\\d*)\\s\\d+.?\\d*";
HashMap<String,String> data = (HashMap<String,String>)request.getAttribute("data");
ArrayList<String> areas = new ArrayList<String>();

String actualState = getValidValue(data, "eventDescription");
String satImg = getValidValue(data, "issueSateliteImg");
String localTime = getValidValue(data, "issueLocalTime");
String eventCLat = getValidValue(data, "eventCLat");
String description = getValidValue(data, "eventDescription");
String eventCLon = getValidValue(data, "eventCLon");
String distance = getValidValue(data, "eventDistance");
String curPath = getValidValue(data, "eventCurrentPath");
String windSust = getValidValue(data, "eventWindSpeedSust");
String windGust = getValidValue(data, "eventWndSpeedMax");
String minCP = getValidValue(data, "eventMinCP");
String forecast48 = getValidValue(data, "eventForecast48h");
String forecast5d = getValidValue(data, "eventForecast5d");
String rain = getValidValue(data, "eventRainForecast");

//Get area keys
Iterator<String> keys = data.keySet().iterator();
while(keys.hasNext()) {
    String key = keys.next();
    if (key.startsWith("area")) {
        areas.add(key);
    }
}
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
                            <textarea name="eventDescription" value="<%=description%>" class="ckeditor" data-required="true" data-description="common"></textarea>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Imagen de satélite *</label>
                            <div class="input-group">
                                <span class="input-group-btn">
                                    <span class="btn btn-primary btn-file">
                                        Examinar<input type="file" name="issueSateliteImg" value="<%=satImg%>" class="form-control" data-required="true" data-description="common"/>
                                    </span>
                                </span>
                                <input type="text" class="form-control" disabled/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Hora local tiempo del centro (Hora GMT) *</label>
                            <input name="issueLocalTime" type="text" value="<%=localTime%>" class="form-control" data-required="true" data-description="common"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Ubicación del centro de la baja presión</label>
                            <div class="form-inline">
                                <input name="eventCLat" type="text" value="<%=eventCLat%>" placeholder="Latitud norte" class="form-control"/>
                                <input name="eventCLon" type="text" value="<%=eventCLon%>" placeholder="Longitud oeste" class="form-control"/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Distancia al lugar más cercano *</label>
                            <input name="eventDistance" type="text" value="<%=distance%>" class="form-control" data-required="true" data-description="common" data-describedby="_eventDistance"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12 form-group">
                            <label class="control-label" for="area">Zona de alerta *</label>
                            <div id="map-canvas"></div>
                            <div id="map-data">
                                <%
                                if (areas != null && !areas.isEmpty()) {
                                    for(String area : areas) {
                                        %>
                                        <input name="<%=area%>" type="hidden" value="<%=data.get(area)%>"/>
                                        <%
                                    }
                                }
                                %>
                            </div>
                            <br>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Desplazamiento actual</label>
                            <input name="eventCurrentPath" type="text" value="<%=curPath%>" class="form-control"/>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Vientos máximos &lpar;Km/h&rpar; *</label>
                            <div class="form-inline">
                                <input name="eventWindSpeedSust" type="text" value="<%=windSust%>" placeholder="Sostenidos" class="form-control" data-required="true" data-description="common"/>
                                <input name="eventWndSpeedMax" type="text" value="<%=windGust%>" placeholder="Rachas" class="form-control" data-required="true" data-description="common"/>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Presión mínima central &lpar;hPa&rpar;</label>
                            <input name="eventMinCP" type="text" value="<%=minCP%>" class="form-control"/>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Potencial de desarrollo &lpar;%&rpar; *</label>
                            <div class="form-inline">
                                <input name="eventForecast48h" type="text" value="<%=forecast48%>" placeholder="48 horas" class="form-control" data-required="true" data-description="common"/>
                                <input name="eventForecast5d" type="text"  value="<%=forecast5d%>" placeholder="5 días" class="form-control" data-required="true" data-description="common"/>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12 form-group">
                            <label class="control-label">Pronóstico de lluvia *</label>
                            <textarea name="eventRainForecast" value="<%=rain%>" rows="7" class="form-control" data-required="true" data-description="common"></textarea>
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
            
            $(document).ready(function(){
                <%
                if (areas != null && !areas.isEmpty()) {
                    %>
                    if (google && map) {
                        <%
                        for(String area : areas) {
                            String _a = data.get(area);
                            if (_a.matches(regex)) {
                                %>insertCircleIntoMap('<%=_a%>', map);<%
                            } else {
                                %>insertPolygonIntoMap('<%=_a%>', map);<%
                            }
                        }
                        %>
                    }
                    <%
                }
            %>
            });
        </script>
    </body>
</html>