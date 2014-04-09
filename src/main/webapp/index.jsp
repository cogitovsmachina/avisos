<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Conagua Bulleting Builder</title>
        <link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
        <link rel="stylesheet" href="/css/bootstrap-datetimepicker.min.css">
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
                    <a class="navbar-brand" href="/">CONAGUA</a>
                </div>
            </div>
        </nav>
        <div class="container main-content">
            <div class="row text-center">
                <h3>Emisión de boletines</h3>
            </div>
            <div class="row inner-container">
                <fieldset>
                    <div class="row text-center">
                        <div class="col-xs-5"></div>
                        <div class="col-xs-2 form-group">
                                <label class="control-label">Tipo de boletín a generar:</label>
                                <select name="boltype" class="form-control" onchange="if(this.value){ window.location.href='/ctrl/'+this.value+'/init';}">
                                    <option value="">Elija:</option>
                                    <option value="pacdp">Depresión tropical en el Pacífico</option>
                                    <option value="pacht">Ciclón en el Pacífico</option>
                                    <option value="atldp">Depresión tropical en el Atlántico</option>
                                    <option value="atlht">Ciclón en el Atlántico</option>
                                </select>
                        </div>
                    </div>
                </fieldset>
            </div>
        </div>
        <script src="/js/libs/jquery/jquery.min.js" type="text/javascript"></script>
        <script src="/js/libs/bootstrap/bootstrap.min.js" type="text/javascript"></script>
        <script src="/js/application.js"></script>
    </body>
</html>