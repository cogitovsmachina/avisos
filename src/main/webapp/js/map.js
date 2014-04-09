/**
 * A library to draw overlays on top of Google Maps to get geospatial info
 * Author: @rodowi
 * Updated: 2014-03
 * TODO: draggable, editable
 */

var mapOverlays = [],
        mapCanvasId = 'map-canvas',
        mapDataId = 'map-data',
        map = null,
        mapOverlayStyle = {
            fillColor: '#21CCCA',
            fillOpacity: 0.25,
            strokeColor: '#21CCCA',
            strokeWeight: 0.75
        };

/**
 * Upon page load, setup map and bind listeners
 *
 */
$(document).ready(function() {
    // Variables and definitions
    map = new google.maps.Map(document.getElementById(mapCanvasId), {
        center: new google.maps.LatLng(23, -102),
        streetViewControl: false,
        zoom: 4,
        zoomControlOptions: {
            style: google.maps.ZoomControlStyle.LARGE,
            position: google.maps.ControlPosition.LEFT_CENTER
        }
    });

    // Setup drawing manager
    var drawingManager = new google.maps.drawing.DrawingManager({
        drawingControl: true,
        drawingControlOptions: {
            position: google.maps.ControlPosition.TOP_CENTER,
            drawingModes: [
                google.maps.drawing.OverlayType.CIRCLE,
                google.maps.drawing.OverlayType.POLYGON
            ]
        },
        circleOptions: mapOverlayStyle,
        polygonOptions: mapOverlayStyle
    });
    drawingManager.setMap(map);

    // Add custom clear button
    var resetControl = $('<div>Reiniciar</div>').css({
        backgroundColor: 'white',
        borderColor: '#AAA',
        borderStyle: 'solid',
        borderWidth: '1px',
        color: '#333',
        cursor: 'pointer',
        margin: '5px',
        padding: '5px'
    })[0];
    map.controls[google.maps.ControlPosition.TOP_CENTER].push(resetControl);
    google.maps.event.addDomListener(resetControl, 'click', function() {
        resetMap();
    });

    // Insert a DIV container to hold geospatial data from the map
    /*var $mapData = $('<div></div>')
            .attr('id', mapDataId)
            .css('padding', '0 10px 10px 10px');
    $('#' + mapCanvasId).after($mapData);*/

    // Events to be trigger when drawing completes
    google.maps.event.addListener(drawingManager, 'overlaycomplete', function(event) {
        // Get bounds in CAP <area> format
        var bounds = event.overlay.toCapArea();
        // Store geo data in DOM
        var overlayCounter = mapOverlays.length;
        insertBoundsIntoDOM(bounds);
        // Store overlay in global array
        mapOverlays.push(event.overlay);
        // Get states intersecting the bounds
        getTerritoriesFromGeom(event.overlay.toGeom(), 'los_estados', function(states, error) {
            if (error) {
                console.log(error);
                return false;
            }
            // Store states in DOM
            insertTerritoriesIntoDOM(states.toString(), 'states-' + overlayCounter, mapDataId, 'Estados');
        });
        // Get municipalities intersecting the bounds
        getTerritoriesFromGeom(event.overlay.toGeom(), 'los_municipios', function(municipalities, error) {
            if (error) {
                console.log(error);
                return false;
            }
            // Store municipalities in DOM
            insertTerritoriesIntoDOM(municipalities.toString(), 'municipalities-' + overlayCounter, mapDataId, 'Municipios');
        });
    });
});

/******************
 *
 * ALL THINGS DOM
 *
 ******************/
/**
 * Inserts geo-data into DOM.
 * <div id='map-data'>
 *   <input name='area-1' value='28.381735043223106,-106.875 591.9281072234887'>
 *   <input name='area-2' value='23.40,-98.34 18.47,-103.27 20.87,-92.81'>
 * </div>
 *
 */
function insertBoundsIntoDOM(bounds) {
    console.log('insertBoundsIntoDOM("' + bounds + '")');
    var $input = $(document.createElement('input')).attr({
        name: 'area-' + mapOverlays.length,
        value: bounds,
        type: 'hidden'
    });
    $('#' + mapDataId).append($input);
}

/**
 * Inserts mexican geopolitical data into DOM.
 * <div id='map-data'>
 *   <input name='states' value='Baja California, Sonora'>
 *   <input name='municipalities' value='Mexicali, Ensenada, Hermosillo'>
 * </div>
 *
 */
function insertTerritoriesIntoDOM(territories, key, domElementId, label) {
    //console.log('insertTerritoriesIntoDOM("' + territories + '")');
    var $input = $(document.createElement('input')).attr({
      name: key,
      value: territories,
      type: "hidden"
    });
    
    $('#' + domElementId).append($input); 
    /*if (label) {
        var $h6 = $(document.createElement('h6')).html("<strong>"+label+"</strong>");
        $('#' + domElementId).append($h6);
    }*/

    /*var divElement = $(document.createElement('div')).addClass("mapdata-container");
    var _territories = territories.split(",");
    $.each(_territories, function(index, value) {
     var $span = $(document.createElement('span')).addClass("label label-default").html(value);
      //$('#' + domElementId).append($span).append(document.createTextNode(" "));
      divElement.append($span).append(document.createTextNode(" "));
    });

    //divElement.append("<p>Este es el div</p>");
    $('#' + domElementId).append(divElement);*/
}

/**
 * Draws polygons on a map based on a CAP <area> string,
 * e.g. circle "32,-115 200"
 * e.g. polygon "32.73,-115.22 31.05,-114.91 23.07,-108.85 22.10,-110.78 32.36,-117.73 32.73,-115.22"
 * Note: Maps API expect a radius value in meters whereas CAP in kilometers
 * https://developers.google.com/maps/documentation/javascript/reference#Circle
 *
 */
function insertCircleIntoMap(bounds, map) {
    var matches = bounds.match(/(-?\d+\.?\d*)/g);
    var coordinates = _.map(matches, function(e) {
        return parseFloat(e);
    });
    var options = $.extend(mapOverlayStyle, {
        center: new google.maps.LatLng(coordinates[0], coordinates[1]),
        radius: parseFloat(coordinates[2]) * 1000,
        map: map
    });
    var circle = new google.maps.Circle(options);
    mapOverlays.push(circle);
}

function insertPolygonIntoMap(bounds, map) {
    var coordinates = _.map(bounds.split(" "), function(element) {
        var points = _.map(element.split(","), function(e) {
            return parseFloat(e);
        });
        return new google.maps.LatLng(points[0], points[1]);
    });
    var options = $.extend(mapOverlayStyle, {
        paths: coordinates,
        map: map
    });
    var polygon = new google.maps.Polygon(options);
    mapOverlays.push(polygon);
}

/**
 * Removes map overlays and DOM elements with map data
 */
function resetMap() {
    removeMapOverlays();
    removeMapData();
}

/**
 * Removes map overlays (global variable)
 *
 */
function removeMapOverlays() {
    while (mapOverlays[0])
        mapOverlays.pop().setMap(null);
}

/**
 * Removes DOM elements with map data
 *
 */
function removeMapData() {
    $('#' + mapDataId).empty()
}


/******************
 *
 * External API calls
 *
 ******************/

/**
 * Requests CartoDB API to retrieve a list of mexican territories intersecting a geometry.
 * Levels supported are 'los_estados' (states) and 'los_municipios' (municipalities).
 *
 */
function getTerritoriesFromGeom(geom, level, callback) {
    console.log('getTerritoriesFromGeom("' + geom + '")');
    var baseUrl = 'http://rodowi.cartodb.com/api/v2/sql?q=';
    var query = "SELECT nombre FROM " + level + " WHERE ST_Intersects(the_geom," + geom + ")";
    $.ajax({
        url: baseUrl + query,
        dataType: 'json',
        success: function(data) {
            var territories = [];
            data.rows.forEach(function(element, index) {
                territories.push(element.nombre);
            });
            callback(territories.toString());
        },
        error: function(error) {
            callback(null, error);
        }
    });
}

/******************
 *
 * Maps prototypes
 *
 ******************/

/**
 * Both methods return a string with geospatial info of a map overlay formatted for CAP <area> elements
 * Note: As of Maps API v3.exp radius is given in meters and CAP v1.2 use KM
 * https://developers.google.com/maps/documentation/javascript/reference#Circle
 * http://docs.oasis-open.org/emergency/cap/v1.2/CAP-v1.2-os.html
 * Note: first and last pair of coordinates should be equal
 *
 */
google.maps.Polygon.prototype.toCapArea = function() {
    var capArea = '';
    this.getPath().forEach(function(element, index) {
        capArea += element.lat() + ',' + element.lng() + ' ';
    });
    var start = this.getPath().getAt(0);
    capArea += start.lat() + ',' + start.lng();
    return capArea;
};

google.maps.Circle.prototype.toCapArea = function() {
    return this.getCenter().lat() + ',' + this.getCenter().lng() + ' ' + this.getRadius() / 1000;
};

/**
 * Both methods return a string representing a PostGIS geometry of a map overlay
 * Note: EWKT format (used in PostGIS) inverts lat/lng order, e.g. SRID=4326;POINT(-44.3 60.1)
 * to locate a longitude/latitude coordinate using the WGS 84 reference coordinate system.
 * http://en.wikipedia.org/wiki/Well-known_text
 * Note: first and last pair of coordinates should be equal
 * Note: PostGIS doesn't support WKT for circles yet
 * http://postgis.refractions.net/documentation/manual-1.4/ch04.html
 * http://gis.stackexchange.com/questions/10352/how-to-create-a-circle-in-postgis
 *
 */
google.maps.Polygon.prototype.toGeom = function() {
    var wkt = "ST_GeomFromText('POLYGON ((";
    this.getPath().forEach(function(element, index) {
        wkt += element.lng() + ' ' + element.lat() + ',';
    });
    var start = this.getPath().getAt(0);
    wkt += start.lng() + ' ' + start.lat();
    return wkt + "))', 4326)";
};

google.maps.Circle.prototype.toGeom = function() {
    var center = this.getCenter().lng() + ' ' + this.getCenter().lat();
    return "ST_Buffer(geography(ST_GeomFromText('POINT(" + center + ")', 4326)), " + this.getRadius() + ")";
};