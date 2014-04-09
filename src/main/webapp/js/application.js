/**
 * Shows a modal dialog and loads the content from url on it.
 * @param {String} url The URL to get the dialog content from.
 */
function showModal(url) {
    submitURL(url, "get", function(data) {
        $("#appModal").html(data);
    }, function () {
        var emsg = "Sorry, there was a problem processing the request. Try again later.";
        $("#appModal").find("#modal-content").html(" "+emsg);
    });
    
    /*var emsg = "Sorry, there was an error processing the request...";
    var jqxhr = $.get(url, function(data) {
        $("#appModal").html(data);
    })
    .fail(function() {
        $("#appModal").find("#modal-content").html(" "+emsg);
    });*/
}

/**
 * Enables forms validation using jqueri-validate plugin.
 */
function enableFormsValidation() {
    if ($('form').length) {
        $('form').validate({
            onBlur: true,
            onSubmit: true,
            invalid: function() {
                alert('Algunos de los datos no son correctos');
            },
            eachInvalidField : function() {
                $(this).closest('div').addClass('has-error');
            },
            eachValidField: function() {
                $(this).closest('div').removeClass('has-error');
            },
            description : {
                common: {
                    required : function() {
                        return $(this).data('msgrequired') || 'Este dato es requerido';
                    },
                    pattern : function() {
                        return $(this).data('msgerror') || 'El dato proporcionado no es válido';
                    }
                }
            }
        });
        
        $.validateExtend({
            nonblankSelect: {
                conditional : function(value) {
                    console.log(value);
                    return true;
                }
            }
        });
    }
}

/**
 * Makes an ajax request to a given url.
 * @param {String} url URL for the request.
 * @param {String} method GET or POST.
 * @param {function} success Handler for the success event.
 * @param {function} fail Handler for the fail event.
 */
function submitURL(url, method, success, fail) {
    var _method = method || "get";
    if (_method === "get") {
        var jqxhr = $.get(url, function(data) {
            if (success && typeof success === "function") {
                success(data);
            } else {
                console.log("OK");
            }
        })
        .fail(function() {
            if (fail && typeof fail === "function") {
                fail();
            } else {
                alert("There was a problem processing the request. Try again later.");
            }
        });
    } else if (_method === "post") {
        var jqxhr = $.post(url, function(data) {
            if (success && typeof success === "function") {
                success(data);
            }
        })
        .fail(function() {
            if (fail && typeof fail === "function") {
                fail();
            }
        });
    }
}

//Init app
$(document).ready(function () {
    //Enable bootstrap tooltips
    if ($("[data-tooltip=tooltip]").length) {
        $("[data-tooltip=tooltip]").tooltip();
    }
    
    //Destroy bootstrap modal content each time the modal closes, reset map data
    $('#appModal').on('hidden.bs.modal', function () {
        $(this).removeData('bs.modal');
        if(google && map) {
            resetMap();
        }
    });
    
    //Trigger map resize when the boostrap modal is shown
    $('#appModal').on('shown.bs.modal', function () {
        if(google && map) {
            google.maps.event.trigger(map, 'resize');
        }
    });
    
    //Enable datetimepickers
    if ($('.datetimePicker').length) {
        $('.datetimePicker').datetimepicker({
            useCurrent:true,
            useSeconds:false
        });
    }
    
    //Enable form validation
    enableFormsValidation();
});