<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>CONAGUA Bulletin Builder</title>
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
                    <a class="navbar-brand" href="/"><img class="logo img-responsive" src="css/img/CONAGUALOGO.png"/></a>
                </div>
            </div>
        </nav>
        <div class="container main-content">
            <div class="row text-center">
                <h3>Emisión de avisos de ciclones tropicales</h3>
            </div>
            <div class="row inner-container text-center">
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                        Seleccione un aviso
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu text-left" role="menu">
                        <li role="presentation" class="dropdown-header">Baja presión</li>
                        <li><a href="/ctrl/pacdp/init/new">Baja presión en el pacífico</a></li>
                        <li><a href="/ctrl/atldp/init/new">Baja presión en el atlántico</a></li>
                        <li role="presentation" class="dropdown-header">Ciclones tropicales</li>
                        <li><a href="/ctrl/pacht/init/new">Ciclón en el pacífico</a></li>
                        <li><a href="/ctrl/atlht/init/new">Ciclón en el atlántico</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <script src="/js/libs/jquery/jquery.min.js" type="text/javascript"></script>
        <script src="/js/libs/bootstrap/bootstrap.min.js" type="text/javascript"></script>
        <script src="/js/application.js"></script>
    </body>
</html>
