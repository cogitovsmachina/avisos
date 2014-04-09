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

String calcMethod = getValidValue(data, "eventCCalc");
String risk = getValidValue(data, "eventRisk");
calcMethod = calcMethod.equals("")?"forecast":calcMethod;
risk = risk.equals("")?"green":risk;

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
                    --><li class="pending">Predicción de avance</li><!--
                    --><li class="pending">Seguimiento</li><!--
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
                            <textarea name="eventDescription" value="<%=getValidValue(data, "eventDescription")%>" class="ckeditor" data-required="true" data-description="common"></textarea>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Imagen de satélite *</label>
                            <div class="input-group">
                                <span class="input-group-btn">
                                    <span class="btn btn-primary btn-file">
                                        Examinar<input type="file" name="issueSateliteImg" value="<%=getValidValue(data, "issueSateliteImg")%>" class="form-control" data-required="true" data-description="common"/>
                                    </span>
                                </span>
                                <input type="text" class="form-control" disabled/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Hora local tiempo del centro (Hora GMT) *</label>
                            <input name="issueLocalTime" type="text" value="<%=getValidValue(data, "issueLocalTime")%>" class="form-control" data-required="true" data-description="common"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Ubicación del centro del ciclón tropical</label>
                            <div class="form-inline">
                                <input name="eventCLat" type="text" value="<%=getValidValue(data, "eventCLat")%>" placeholder="Latitud norte" class="form-control"/>
                                <input name="eventCLon" type="text" value="<%=getValidValue(data, "eventCLon")%>" placeholder="Longitud oeste" class="form-control"/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Método de cálculo del centro</label><br>
                            <div class="btn-group" data-toggle="buttons">
                                <label class="btn btn-default <%=calcMethod.equalsIgnoreCase("forecast")?"active":""%>">
                                    <input type="radio" name="eventCCalc" value="forecast" <%=calcMethod.equalsIgnoreCase("forecast")?"checked":""%>>Forecast NOAA
                                </label>
                                <label class="btn btn-default <%=calcMethod.equalsIgnoreCase("interpolation")?"active":""%>">
                                    <input type="radio" name="eventCCalc" value="interpolation" <%=calcMethod.equalsIgnoreCase("interpolation")?"checked":""%>>Interpolación
                                </label>
                            </div>
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
                            <label class="control-label">Nivel de alertamiento *</label><br>
                            <div class="btn-group" data-toggle="buttons">
                                <label class="btn btn-default <%=risk.equalsIgnoreCase("green")?"active":""%>">
                                    <input type="radio" name="eventRisk" value="green" <%=risk.equalsIgnoreCase("green")?"checked":""%>>Verde
                                </label>
                                <label class="btn btn-default <%=risk.equalsIgnoreCase("yellow")?"active":""%>">
                                    <input type="radio" name="eventRisk" value="yellow" <%=risk.equalsIgnoreCase("yellow")?"checked":""%>">Amarillo
                                </label>
                                <label class="btn btn-default <%=risk.equalsIgnoreCase("orange")?"active":""%>">
                                    <input type="radio" name="eventRisk" value="orange" <%=risk.equalsIgnoreCase("orange")?"checked":""%>>Naranja
                                </label>
                                <label class="btn btn-default <%=risk.equalsIgnoreCase("red")?"active":""%>">
                                    <input type="radio" name="eventRisk" value="red" <%=risk.equalsIgnoreCase("red")?"checked":""%>>Rojo
                                </label>
                                <label class="btn btn-default <%=risk.equalsIgnoreCase("purple")?"active":""%>">
                                    <input type="radio" name="eventRisk" value="purple" <%=risk.equalsIgnoreCase("purple")?"checked":""%>>Púrpura
                                </label>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Distancia al lugar más cercano *</label>
                            <input name="eventDistance" type="text" value="<%=getValidValue(data, "eventDistance")%>" class="form-control" data-required="true" data-description="common" data-describedby="_eventDistance"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Desplazamiento actual</label>
                            <input name="eventCurrentPath" type="text" value="<%=getValidValue(data, "eventCurrentPath")%>" class="form-control"/>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Vientos máximos &lpar;Km/h&rpar; *</label>
                            <div class="form-inline">
                                <input name="eventWindSpeedSust" type="text" value="<%=getValidValue(data, "eventWindSpeedSust")%>" placeholder="Sostenidos" class="form-control" data-required="true" data-description="common"/>
                                <input name="eventWndSpeedMax" type="text" value="<%=getValidValue(data, "eventWindSpeedMax")%>" placeholder="Rachas" class="form-control" data-required="true" data-description="common"/>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Presión mínima central &lpar;hPa&rpar;</label>
                            <input name="eventMinCP" type="text" value="<%=getValidValue(data, "eventMinCP")%>" class="form-control"/>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Diámetro del ojo &lpar;Km&rpar;</label>
                            <input name="eventCDiameter" type="text" value="<%=getValidValue(data, "eventCDiameter")%>" class="form-control"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Radio de vientos de 63Km/h</label>
                            <div class="form-inline">
                                <input name="eventWind63kmNE" type="text" value="<%=getValidValue(data, "eventWind63kmSE")%>" placeholder="NE" class="form-control input-sector"/>
                                <input name="eventWind63kmSE" type="text" value="<%=getValidValue(data, "eventWind63kmNE")%>" placeholder="SE" class="form-control input-sector"/>
                                <input name="eventWind63kmSO" type="text" value="<%=getValidValue(data, "eventWind63kmSO")%>" placeholder="SO" class="form-control input-sector"/>
                                <input name="eventWind63kmNO" type="text" value="<%=getValidValue(data, "eventWind63kmNO")%>" placeholder="NO" class="form-control input-sector"/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Radio de vientos de 95Km/h</label>
                            <div class="form-inline">
                                <input name="eventWind95kmNE" type="text" value="<%=getValidValue(data, "eventWind95kmSE")%>" placeholder="NE" class="form-control input-sector"/>
                                <input name="eventWind95kmSE" type="text" value="<%=getValidValue(data, "eventWind95kmNE")%>" placeholder="SE" class="form-control input-sector"/>
                                <input name="eventWind95kmSO" type="text" value="<%=getValidValue(data, "eventWind95kmSO")%>" placeholder="SO" class="form-control input-sector"/>
                                <input name="eventWind95kmNO" type="text" value="<%=getValidValue(data, "eventWind95kmNO")%>" placeholder="NO" class="form-control input-sector"/>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Radio de vientos de 120Km/h</label>
                            <div class="form-inline">
                                <input name="eventWind120kmNE" type="text" value="<%=getValidValue(data, "eventWind120kmSE")%>" placeholder="NE" class="form-control input-sector"/>
                                <input name="eventWind120kmSE" type="text" value="<%=getValidValue(data, "eventWind120kmNE")%>" placeholder="SE" class="form-control input-sector"/>
                                <input name="eventWind120kmSO" type="text" value="<%=getValidValue(data, "eventWind120kmSO")%>" placeholder="SO" class="form-control input-sector"/>
                                <input name="eventWind120kmNO" type="text" value="<%=getValidValue(data, "eventWind120kmNO")%>" placeholder="NO" class="form-control input-sector"/>
                            </div>
                        </div>
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Oleaje 4m</label>
                            <div class="form-inline">
                                <input name="seas4mNE" value="<%=getValidValue(data, "seas4mNE")%>" type="text" placeholder="NE" class="form-control input-sector"/>
                                <input name="seas4mSE" value="<%=getValidValue(data, "seas4mSE")%>" type="text" placeholder="SE" class="form-control input-sector"/>
                                <input name="seas4mSO" value="<%=getValidValue(data, "seas4mSO")%>" type="text" placeholder="SO" class="form-control input-sector"/>
                                <input name="seas4mNO" value="<%=getValidValue(data, "seas4mNO")%>" type="text" placeholder="NO" class="form-control input-sector"/>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 form-group">
                            <label class="control-label">Diámetro promedio de fuerte convección*</label>
                            <input name="eventDiameterConvection" value="<%=getValidValue(data, "eventDiameterConvection")%>" type="text" class="form-control"/>
                        </div>
                    </div>
                        <div class="row">
                        <div class="col-lg-12 form-group">
                            <label class="control-label">Comentarios adicionales *</label>
                            <textarea name="eventComments" value="<%=getValidValue(data, "eventComments")%>" class="ckeditor" data-required="true" data-description="common"></textarea>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12 form-group">
                            <label class="control-label">Recomendaciones *</label>
                            <textarea name="eventInstructions" value="<%=getValidValue(data, "eventInstructions")%>" class="ckeditor" data-required="true" data-description="common">
                                A LA POBLACIÓN EN GENERAL EN LOS ESTADOS MENCIONADOS Y A LA NAVEGACIÓN MARÍTIMA EN LAS INMEDIACIONES DEL SISTEMA, MANTENER PRECAUCIONES Y ATENDER RECOMENDACIONES EMITIDAS POR LAS AUTORIDADES DEL SISTEMA NACIONAL DE PROTECCIÓN CIVIL
                            </textarea>
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
        <script src="/js/libs/underscore/underscore-min.js" type="text/javascript"></script>
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