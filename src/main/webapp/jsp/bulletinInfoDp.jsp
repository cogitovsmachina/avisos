<%@page import="mx.org.cedn.avisosconagua.util.Utils"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
HashMap<String,String> data = (HashMap<String,String>)request.getAttribute("data");
String issueFooter = Utils.getValidFieldFromHash(data, "issueFooter");
String type = (String)request.getAttribute("bulletinType");
issueFooter=issueFooter.equals("")?"EL SIGUIENTE AVISO SE EMITIRÁ A LAS 19:00HRS TIEMPO DEL CENTRO O ANTES SI OCURREN CAMBIOS SIGNIFICATIVOS":issueFooter;
%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
        <title>CAP Architect v1</title>
        <link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
        <link rel="stylesheet" href="/css/font-awesome/font-awesome.min.css">
        <link rel="stylesheet" href="/css/bootstrap-datetimepicker.min.css">
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
                    <li class="done">Situación actual</li><!--
                    --><li class="current">Información de emisión</li><!--
                    --><li class="pending">Vista previa</li><!--
                    --><li class="pending">Publicación</li>
                </ol>
            </div>
            <div class="row inner-container">
                <form role="form" action="" method="post">
                    <div class="row">
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Número de aviso*</label>
                            <input type="text" name="issueNumber" value="<%=Utils.getValidFieldFromHash(data, "issueNumber")%>" class="form-control" data-required="true" data-description="common">
                        </div>
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Fecha y hora de emisión*</label>
                            <div class="form-inline">
                                <div class="input-group date datePicker" data-date-format="DD/MM/YYYY">
                                    <input name="issueDate" type="text" value="<%=Utils.getValidFieldFromHash(data, "issueDate")%>" class="form-control" data-required="true" data-description="common"/><span class="input-group-addon"><span class="fa fa-calendar"></span>
                                </div>
                                <div class="input-group date timePicker" data-date-format="HH:mm" data-date-use24hours="true">
                                    <input name="issueTime" type="text" value="<%=Utils.getValidFieldFromHash(data, "issueTime")%>" class="form-control" data-required="true" data-description="common"/><span class="input-group-addon"><span class="fa fa-clock-o"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Fecha y hora de siguiente emisión*</label>
                            <div class="form-inline">
                                <div class="input-group date datePicker" data-date-format="DD/MM/YYYY">
                                    <input name="issueNextDate" type="text" value="<%=Utils.getValidFieldFromHash(data, "issueNextDate")%>" class="form-control" data-required="true" data-description="common"/><span class="input-group-addon"><span class="fa fa-calendar"></span>
                                </div>
                                <div class="input-group date timePicker" data-date-format="HH:mm" data-date-use24hours="true">
                                    <input name="issueNextTime" type="text" value="<%=Utils.getValidFieldFromHash(data, "issueNextTime")%>" class="form-control" data-required="true" data-description="common"/><span class="input-group-addon"><span class="fa fa-clock-o"></span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Síntesis (cintillo)*</label>
                            <input name="eventHeadline" type="text" value="<%=Utils.getValidFieldFromHash(data, "issueLocalTime")%>" class="form-control" data-required="true" data-description="common"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Meteorólogo que elabora*</label>
                            <input name="issueMetheorologist" type="text" value="<%=Utils.getValidFieldFromHash(data, "issueMetheorologist")%>" class="form-control" data-required="true" data-description="common"/>
                        </div>
                        <div class="col-lg-6 col-md-6 form-group">
                            <label class="control-label">Meteorólogo que revisa*</label>
                            <input name="issueShiftBoss" type="text" value="<%=Utils.getValidFieldFromHash(data, "issueShiftBoss")%>" class="form-control" data-required="true" data-description="common"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12 col-md-12 form-group">
                            <label class="control-label">Pie del aviso</label>
                            <textarea name="issueFooter" rows="7" class="form-control"><%=issueFooter%></textarea>
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
        <script src="/js/libs/moment/moment.min.js" type="text/javascript"></script>
        <script src="/js/libs/jquery/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
        <script src="/js/application.js"></script>
    </body>
</html>