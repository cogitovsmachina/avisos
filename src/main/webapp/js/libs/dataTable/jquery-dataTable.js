/*
 * Plugin for loading data into a table v1.0
 */
(function($) {
    $.fn.dataTable = function(options) {
        //Main vars
        $table = $(this);
        $tbody = $table.children("tbody") || $(document.createElement("tbody"));
        rowCont = $tbody.children().length || 0;
        //Merge options
        _settings = $.extend({}, $.fn.dataTable.defaults, options);
        _cols = _settings.columns || [];
        
        if(_settings.data) {
            if (typeof _settings.data === "function") {
                _data = _settings.data.call(this);
            } else {
                _data = _settings.data || [];
            }
        }
        _colAlign = _settings.columnAlign || "center";
        
        switch(_colAlign) {
            case "center":
                _colAlign = "text-center";
                break;
            case "left":
                _colAlign = "text-left";
                break;
            case "right":
                _colAlign = "text-right";
                break;
        }
        
        function valFunction (col, data) {
            var ret = "";
            //check compound fields
            if (col.field.indexOf("|") === -1) {
                ret = data[col.field] || "";
            } else {
                var fields = col.field.split("|");
                for(var i = 0; i < fields.length; i++) {
                    ret += (data[fields[i]] || "");
                    if (i < fields.length - 1 ) {
                        ret += col.separator;
                    }
                }
            }
            return ret;
        }
        
        function renderColHeaders() {
            //Create header
            var _header = $(document.createElement("thead"));
            var _hrow = $(document.createElement("tr"));
            $.each(_cols, function(i, d) {
                var _domcol = $(document.createElement("th"));
                _domcol.html(d.title || "");
                
                _domcol.addClass("text-center");
                
                _hrow.append(_domcol);
                _header.append(_hrow);
            });
            $table.append(_header);
        }
        
        function renderData() {
            //Create body
            $.each(_data, function(i, d) {
                var _domrow = $(document.createElement("tr"));
                var _tdata = d;
                
                $.each(_cols, function(i, d) {
                    var _domcell = $(document.createElement("td"));
                    _domcell.addClass(_colAlign);
                    var _inputcell;
                    var _val;
                    
                    if (d.valFunction && typeof d.valFunction === "function") {
                        _val = d.valFunction.call(this, d, _tdata);
                    } else {
                        _val = valFunction(d, _tdata);
                    }
                    
                    if (d.postProcess && typeof d.postProcess === "function") {
                        _val = d.postProcess.call(this, _val);
                    }
                    
                    var cssClass = "form-control";
                    switch(d.formElement) {
                        case "textBox":
                            _inputcell = $(document.createElement("input"));
                            _inputcell.attr({
                                type: "text",
                                value: _val
                            });
                            break;
                        case "textArea":
                            _inputcell = $(document.createElement("textarea"));
                            _inputcell.text(_val);
                            break;
                        case "span":
                            _inputcell = $(document.createElement("span"));
                            _inputcell.text(_val);
                            cssClass = "";
                            break;
                    }
                    
                    if (d.required && d.required==="true") {
                        _inputcell.attr("data-required","true").attr("data-description","common");
                    }
                    //_inputcell.attr("data-tooltip", "tooltip").attr("data-placement","top").attr("title",_val).attr("data-original-title",_val);
                    if (cssClass !== "") {
                        _inputcell.attr({
                            class: cssClass,
                        });
                    }
                    
                    _inputcell.addClass(_colAlign);
                    _inputcell.addClass("data-table-input");
                    
                    _domcell.append(_inputcell);
                    _domrow.append(_domcell);
                });
                $tbody.append(_domrow);
            });
            
            $table.append($tbody);
        }
        
        if (_settings.renderCols) { renderColHeaders(); }
        if (_settings.data) { renderData(); }
        return this;
    };
    
    $.fn.dataTable.defaults = {
        thClass: "text-center",
        renderCols: false,
        idPrefix: "dataTableInput"
    };
})(jQuery);