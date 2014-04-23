<%@page import="mx.org.cedn.avisosconagua.util.Utils"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.regex.Matcher"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
HashMap<String,String> data = (HashMap<String,String>)request.getAttribute("data");
String type = (String)request.getAttribute("bulletinType");
/*String trackData = data.get("forecastData")!= null?data.get("forecastData"):"";
ArrayList<String> _trackData = Utils.tokenize(trackData, "\\{(.*?)\\}");
for(String d : _trackData) {
            String []parts = d.split("\\|");
            for(int i = 0; i< parts.length; i++) {
                System.out.println("part:\""+parts[i]+"\"");
            }
}*/
%>
<!DOCTYPE html>
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
            <h4 class="text-center text-muted hidden-lg hidden-md">Predicción de avance</h4>
            <div class="row progress-indicator-container text-center visible-lg visible-md">
                <ol class="progress-indicator">
                    <li class="done">Situación actual</li><!--
                    --><li class="current">Predicción de avance</li><!--
                    --><li class="pending">Información de emisión</li><!--
                    --><li class="pending">Vista previa</li><!--
                    --><li class="pending">Publicación</li>
                </ol>
            </div>
            <div class="row inner-container">
                <form role="form" action="" method="post" enctype="multipart/form-data" onsubmit="return saveGridData(this);">
                    <div class="row">
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Mapa de localización*</label>
                            <div class="input-group">
                                <span class="input-group-btn">
                                    <span class="btn btn-primary btn-file">
                                        Examinar<input type="file" name="issueSateliteLocationImg" class="form-control"/>
                                    </span>
                                </span>
                                <%
                                String fileName = Utils.getValidFieldFromHash(data, "issueSateliteLocationImg");
                                fileName = fileName.substring(fileName.indexOf("_")+1, fileName.length());
                                %>
                                <input type="text" class="form-control" value="<%=fileName%>" disabled data-required="true" data-description="common" />
                            </div>
                        </div>
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Pie de la imagen*</label>
                            <input type="text" name="issueSateliteLocationImgFooter" class="form-control" data-required="true" data-description="common" value="<%=Utils.getValidFieldFromHash(data, "issueSateliteLocationImgFooter")%>" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Enlace al <a target="_blank" href="http://www.nhc.noaa.gov/">forecast de NHC</a></label>
                            <div class="input-group">
                                <input type="text" id="nhcLink" name="nhcLink" value="<%=Utils.getValidFieldFromHash(data, "nhcLink")%>" class="form-control">
                                <span class="input-group-btn">
                                    <button id="loadButton" class="btn btn-default" data-loading-text="Cargando..." type="button">Precargar</button>
                                </span>
                            </div>
                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-lg-12 col-md-12 table-responsive">
                            <table class="table data-table" id="dataTable">
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
                                <tbody></tbody>
                            </table>
                            <input type="hidden" name="forecastData" id="forecastData" />
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
        <script src="/js/libs/dataTable/jquery-dataTable.js"></script>
        <script type="text/javascript">
            function saveGridData(form) {
                var $form = $(form);
                var val = "";
                $.each($form.find("tbody").children(), function(i,d) {
                    $row = $(d);
                    val+="{";
                    $.each($row.children("td"), function(i, d) {
                       var v = $.trim($(d).find(".data-table-input").val());
			if (v==="") v = "_";
                       val+= v+"|";
                    });
                    val = val.substring(0, val.length-1);
                    val+="}";
                });
                
                $("#forecastData").val(val);
                return true;
            }
            
            function loadDataFromUrl(url) {
                if (url && url !== undefined) {
                    var btn = $("#loadButton");
                    btn.button('loading');
                    $.ajax({
                        url: "http://weatherman.herokuapp.com/forecast",
                        jsonp: "callback",
                        dataType: "jsonp",
                        data: {
                            format: 'jsonp',
                            url: url
                        },
                        success: function (response) {
                            $("#dataTable tbody").empty();
                            $("#dataTable").dataTable({
                                columnAlign: "center",
                                renderCols:false,
                                columns: [
                                    {
                                        title:"Pronóstico válido<br>al día/hora local<br>tiempo del centro",
                                        field:"id",
                                        formElement:"textBox",
                                        required: "true"
                                    },
                                    {
                                        title:"Latitud norte",
                                        field:"north",
                                        formElement:"textBox",
                                        postProcess:function(val) {
                                            //Quitar N
                                            return val.replace(/N/g,'');
                                        },
                                        required: "true"
                                    },
                                    {
                                        title:"Longitud oeste",
                                        field:"west",
                                        formElement:"textBox",
                                        postProcess:function(val) {
                                            //Quitar W
                                            return val.replace(/W/g,'');
                                        },
                                        required: "true"
                                    },
                                    {
                                        title:"Vientos (Km/h)<br>SOST./RACHAS",
                                        field:"max|gusts",
                                        separator:"/",
                                        formElement:"textBox",
                                        postProcess:function(val) {
                                            //Elimina KT y convierte a Km/h
                                            var ret = val.replace(/\s/g,'').replace(/KT/g,'');
                                            var vals = ret.split("/");
                                            return (parseFloat(vals[0])*1.852).toFixed(2) + "/" + (parseFloat(vals[1])*1.852).toFixed(2);
                                        },
                                        required: "true"
                                    },
                                    {
                                        title:"Categoría",
                                        field:"category",
                                        formElement:"textBox",
                                        required: "true"
                                    },
                                    {
                                        title:"Ubicación (Km)",
                                        field:"location",
                                        formElement:"textArea",
                                        required: "true"
                                    }
                                ],
                                data: response
                            });
                            btn.button('reset');
                        }
                    });
                }
            }
            
            $(document).ready(function() {
                $("#loadButton").on("click", function(event) {
                    var url = $("#nhcLink").val();
                    loadDataFromUrl(url);
                    event.preventDefault();
                });
            });
        </script>
    </body>
</html>
