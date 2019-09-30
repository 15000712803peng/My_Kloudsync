function gid(id) { return document.getElementById(id); }
function createEl(name) { return document.createElementNS("http://www.w3.org/2000/svg", name); }
function _attachEvent(obj, evt, func) {
    if (obj.addEventListener) {
        obj.addEventListener(evt, func, true);
    } else if (obj.attachEvent) {
        obj.attachEvent("on" + evt, func);
    } else {
        eval("var old" + func + "=" + obj + ".on" + evt + ";");
        eval(obj + ".on" + evt + "=" + func + ";");
    }
}
function _detachEvent(obj, evt, func) {
    if (obj.removeEventListener) {
        obj.removeEventListener(evt, func, true);
    } else if (obj.detachEvent) {
        obj.detachEvent("on" + evt, func);
    } else {
        eval(obj + ".on" + evt + "=old" + func + ";");
    }
}
function _cancelDefault(e) {
    if (e.preventDefault) e.preventDefault();
    else e.returnValue = false;
}
function _cancelBubble(e) {
    if (e.stopPropagation) e.stopPropagation();
    else e.cancelBubble = true;
}
var __sto = setTimeout;
window.setTimeoutEx = function (callback, timeout, param) {
    var args = Array.prototype.slice.call(arguments, 2);
    var _cb = function () { callback.apply(null, args); }
    __sto(_cb, timeout);
}
String.prototype.endsWith = function String$endsWith(suffix) { return (this.substr(this.length - suffix.length) === suffix); }
String.prototype.startsWith = function String$startsWith(prefix) { return (this.substr(0, prefix.length) === prefix); }
String.prototype.trim = function String$trim() { return this.replace(/^\s+|\s+$/g, ''); }
String.prototype.trimEnd = function String$trimEnd() { return this.replace(/\s+$/, ''); }
String.prototype.trimStart = function String$trimStart() { return this.replace(/^\s+/, ''); }
String.format = function String$format(format, args) { return String._toFormattedString(false, arguments); }
String.localeFormat = function String$localeFormat(format, args) { return String._toFormattedString(true, arguments); }
String._toFormattedString = function String$_toFormattedString(useLocale, args) {
    var result = ''; var format = args[0];
    for (var i = 0; ;) {
        var open = format.indexOf('{', i); var close = format.indexOf('}', i); if ((open < 0) && (close < 0)) { result += format.slice(i); break; }
        if ((close > 0) && ((close < open) || (open < 0))) { result += format.slice(i, close + 1); i = close + 2; continue; }
        result += format.slice(i, open); i = open + 1; if (format.charAt(i) === '{') { result += '{'; i++; continue; }
        var brace = format.substring(i, close); var colonIndex = brace.indexOf(':'); var argNumber = parseInt((colonIndex < 0) ? brace : brace.substring(0, colonIndex)) + 1;
        var argFormat = (colonIndex < 0) ? '' : brace.substring(colonIndex + 1); var arg = args[argNumber];
        if (typeof (arg) === "undefined" || arg === null) { arg = ''; }
        if (arg.toFormattedString) { result += arg.toFormattedString(argFormat); }
        else if (useLocale && arg.localeFormat) { result += arg.localeFormat(argFormat); }
        else if (arg.format) { result += arg.format(argFormat); }
        else result += arg.toString();
        i = close + 1;
    } return result;
}

function GUID() {
    var guid = "";
    for (var i = 1; i <= 32; i++) {
        var n = Math.floor(Math.random() * 16.0).toString(16);
        guid += n;
        if ((i == 8) || (i == 12) || (i == 16) || (i == 20))
            guid += "-";
    }
    return guid;
}
function getAngle(x1, y1, x2, y2) {
    var x = Math.abs(x1 - x2);
    var y = Math.abs(y1 - y2);
    var z = Math.sqrt(x * x + y * y);
    var result = Math.round((Math.asin(y / z) / Math.PI * 180));
    if (x2 >= x1) {
        if (y2 >= y1) return result;
        else return -result;
    }
    else {
        if (y2 >= y1) return 180 - result;
        else return 180 + result;
    }
}
function Trim(s) { if (s == null) return null; var m = s.match(/^\s*(\S+(\s+\S+)*)\s*$/); return (m == null) ? "" : m[1]; }
function IsExistInArr(arr, id) {
    var j = arr.length;
    if (j == 0) return false;
    for (var i = 0; i < j; i++) {
        if (arr[i] == id) return true;
    }
    return false;
}
function FindItemInArr(arr, str) {
    var j = arr.length;
    if (j == 0) return -1;
    for (var i = 0; i < j; i++) {
        if (arr[i] == str) return i;
    }
    return -1;
}
var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
var base64DecodeChars = new Array(
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
    -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1);

function base64encode(str) {
    var out, i, len;
    var c1, c2, c3;

    len = str.length;
    i = 0;
    out = "";
    while (i < len) {
        c1 = str.charCodeAt(i++) & 0xff;
        if (i == len) {
            out += base64EncodeChars.charAt(c1 >> 2);
            out += base64EncodeChars.charAt((c1 & 0x3) << 4);
            out += "==";
            break;
        }
        c2 = str.charCodeAt(i++);
        if (i == len) {
            out += base64EncodeChars.charAt(c1 >> 2);
            out += base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));
            out += base64EncodeChars.charAt((c2 & 0xF) << 2);
            out += "=";
            break;
        }
        c3 = str.charCodeAt(i++);
        out += base64EncodeChars.charAt(c1 >> 2);
        out += base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));
        out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >> 6));
        out += base64EncodeChars.charAt(c3 & 0x3F);
    }
    return out;
}

function base64decode(str) {
    var c1, c2, c3, c4;
    var i, len, out;

    len = str.length;
    i = 0;
    out = "";
    while (i < len) {
        /* c1 */
        do {
            c1 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
        } while (i < len && c1 == -1);
        if (c1 == -1)
            break;

        /* c2 */
        do {
            c2 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
        } while (i < len && c2 == -1);
        if (c2 == -1)
            break;

        out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));

        /* c3 */
        do {
            c3 = str.charCodeAt(i++) & 0xff;
            if (c3 == 61)
                return out;
            c3 = base64DecodeChars[c3];
        } while (i < len && c3 == -1);
        if (c3 == -1)
            break;

        out += String.fromCharCode(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2));

        /* c4 */
        do {
            c4 = str.charCodeAt(i++) & 0xff;
            if (c4 == 61)
                return out;
            c4 = base64DecodeChars[c4];
        } while (i < len && c4 == -1);
        if (c4 == -1)
            break;
        out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
    }
    return out;
}

function utf16to8(str) {
    var out, i, len, c;

    out = "";
    len = str.length;
    for (i = 0; i < len; i++) {
        c = str.charCodeAt(i);
        if ((c >= 0x0001) && (c <= 0x007F)) {
            out += str.charAt(i);
        } else if (c > 0x07FF) {
            out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
            out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));
            out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
        } else {
            out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));
            out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
        }
    }
    return out;
}

function utf8to16(str) {
    var out, i, len, c;
    var char2, char3;

    out = "";
    len = str.length;
    i = 0;
    while (i < len) {
        c = str.charCodeAt(i++);
        switch (c >> 4) {
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                // 0xxxxxxx
                out += str.charAt(i - 1);
                break;
            case 12: case 13:
                // 110x xxxx   10xx xxxx
                char2 = str.charCodeAt(i++);
                out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
                break;
            case 14:
                // 1110 xxxx  10xx xxxx  10xx xxxx
                char2 = str.charCodeAt(i++);
                char3 = str.charCodeAt(i++);
                out += String.fromCharCode(((c & 0x0F) << 12) |
                    ((char2 & 0x3F) << 6) |
                    ((char3 & 0x3F) << 0));
                break;
        }
    }

    return out;
}

function CharToHex(str) {
    var out, i, len, c, h;

    out = "";
    len = str.length;
    i = 0;
    while (i < len) {
        c = str.charCodeAt(i++);
        h = c.toString(16);
        if (h.length < 2)
            h = "0" + h;

        out += "\\x" + h + " ";
        if (i > 0 && i % 8 == 0)
            out += "\r\n";
    }

    return out;
}


function JsMap() {
    this.container = {};
    this.set = function (key, value) {
        try {
            if (key != null && key != "")
                this.container[key] = value;
            return true;
        } catch (e) {
            return false;
        }
    };
    this.get = function (key) {
        try {
            return this.container[key];
        } catch (e) {
            return null;
        }
    };
    this.containsKey = function (key) {
        try {
            for (var p in this.container) {
                if (p == key)
                    return true;
            }
            return false;

        } catch (e) {
            return false;
        }
    };
    this.containsValue = function (value) {
        try {
            for (var p in this.container) {
                if (this.container[p] === value)
                    return true;
            }
            return false;

        } catch (e) {
            return false;
        }
    };
    this.remove = function (key) {
        try {
            delete this.container[key];
            return true;
        } catch (e) {
            return false;
        }
    };
    this.clear = function () {
        try {
            delete this.container;
            this.container = {};
            return true;

        } catch (e) {
            return false;
        }
    };
    this.isEmpty = function () {
        if (this.keySet().length == 0)
            return true;
        else
            return false;
    };
    this.size = function () {
        return this.keySet().length;
    };
    //返回map中的key值数组  
    this.keySet = function () {
        var keys = new Array();
        for (var p in this.container) {
            keys.push(p);
        }

        return keys;
    };
    this.values = function () {
        var valuesArray = new Array();
        var keys = this.keySet();
        for (var i = 0; i < keys.length; i++) {
            valuesArray.push(this.container[keys[i]]);
        }
        return valuesArray;
    };
    //返回 map 中的 entrySet 对象
    this.entrySet = function () {
        var array = new Array();
        var keys = this.keySet();
        for (var i = 0; i < keys.length; i++) {
            array.push(keys[i], this.container[keys[i]]);
        }
        return array;
    };

    //返回 map 中的 value值的和(当值是 Nunmber 类型时有效)
    this.sumValues = function () {
        var values = this.values();
        var result = 0;
        for (var i = 0; i < values.length; i++) {
            result += Number(values[i]);
        }
        return result;
    };
}

var Drag = {
    obj: null,
    bindEvent: function (obj, evt, fun) {
        if (obj.attachEvent && typeof obj.attachEvent != 'undefined') {
            obj.attachEvent('on' + evt, fun);
        } else {
            obj.addEventListener(evt, fun, true);
        }
    },
    unbindEvent: function (obj, evt, fun) {
        if (obj.detachEvent && typeof obj.detachEvent != 'undefined') {
            obj.detachEvent('on' + evt, fun);
        } else {
            obj.removeEventListener(evt, fun, true);
        }
    },
    init: function (o, oRoot, minX, maxX, minY, maxY, bSwapHorzRef, bSwapVertRef, fXMapper, fYMapper) {
        //o.ontouchstart = Drag.start;
        Drag.unbindEvent(o, "touchstart", Drag.start);
        Drag.bindEvent(o, "touchstart", Drag.start);

        o.hmode = bSwapHorzRef ? false : true;
        o.vmode = bSwapVertRef ? false : true;

        o.root = oRoot && oRoot != null ? oRoot : o;

        if (o.hmode && isNaN(parseInt(o.root.style.left))) o.root.style.left = "0px";
        if (o.vmode && isNaN(parseInt(o.root.style.top))) o.root.style.top = "0px";
        if (!o.hmode && isNaN(parseInt(o.root.style.right))) o.root.style.right = "0px";
        if (!o.vmode && isNaN(parseInt(o.root.style.bottom))) o.root.style.bottom = "0px";

        o.minX = typeof minX != 'undefined' ? minX : null;
        o.minY = typeof minY != 'undefined' ? minY : null;
        o.maxX = typeof maxX != 'undefined' ? maxX : null;
        o.maxY = typeof maxY != 'undefined' ? maxY : null;

        o.xMapper = fXMapper ? fXMapper : null;
        o.yMapper = fYMapper ? fYMapper : null;

        o.root.onDragStart = new Function();
        o.root.onDragEnd = new Function();
        o.root.onDrag = new Function();
    },

    start: function (e) {
        var o = Drag.obj = this;
        var evt = e;
        e = Drag.fixE(e);
        var y = parseInt(o.vmode ? o.root.style.top : o.root.style.bottom);
        var x = parseInt(o.hmode ? o.root.style.left : o.root.style.right);
        o.root.onDragStart(x, y);

        o.lastMouseX = e.clientX;
        o.lastMouseY = e.clientY;

        if (o.hmode) {
            if (o.minX != null) o.minMouseX = e.clientX - x + o.minX;
            if (o.maxX != null) o.maxMouseX = o.minMouseX + o.maxX - o.minX;
        } else {
            if (o.minX != null) o.maxMouseX = -o.minX + e.clientX + x;
            if (o.maxX != null) o.minMouseX = -o.maxX + e.clientX + x;
        }

        if (o.vmode) {
            if (o.minY != null) o.minMouseY = e.clientY - y + o.minY;
            if (o.maxY != null) o.maxMouseY = o.minMouseY + o.maxY - o.minY;
        } else {
            if (o.minY != null) o.maxMouseY = -o.minY + e.clientY + y;
            if (o.maxY != null) o.minMouseY = -o.maxY + e.clientY + y;
        }

        //document.ontouchmove = Drag.drag;
        //document.ontouchend = Drag.end;
        Drag.unbindEvent(document, "touchmove", Drag.drag);
        Drag.bindEvent(document, "touchmove", Drag.drag);
        Drag.unbindEvent(document, "touchend", Drag.end);
        Drag.bindEvent(document, "touchend", Drag.end);
        //evt.preventDefault();
        //evt.stopPropagation();
        return false;
    },

    drag: function (e) {
        if (Drag.obj.root.canDrag && Drag.obj.root.canDrag() == false) return false;
        var evt = e;
        e = Drag.fixE(e);
        var o = Drag.obj;

        var ey = e.clientY;
        var ex = e.clientX;
        var y = parseInt(o.vmode ? o.root.style.top : o.root.style.bottom);
        var x = parseInt(o.hmode ? o.root.style.left : o.root.style.right);
        var nx, ny;

        if (o.minX != null) ex = o.hmode ? Math.max(ex, o.minMouseX) : Math.min(ex, o.maxMouseX);
        if (o.maxX != null) ex = o.hmode ? Math.min(ex, o.maxMouseX) : Math.max(ex, o.minMouseX);
        if (o.minY != null) ey = o.vmode ? Math.max(ey, o.minMouseY) : Math.min(ey, o.maxMouseY);
        if (o.maxY != null) ey = o.vmode ? Math.min(ey, o.maxMouseY) : Math.max(ey, o.minMouseY);

        nx = x + ((ex - o.lastMouseX) * (o.hmode ? 1 : -1));
        ny = y + ((ey - o.lastMouseY) * (o.vmode ? 1 : -1));

        if (o.xMapper) nx = o.xMapper(y)
        else if (o.yMapper) ny = o.yMapper(x)

        Drag.obj.root.style[o.hmode ? "left" : "right"] = nx + "px";
        Drag.obj.root.style[o.vmode ? "top" : "bottom"] = ny + "px";
        Drag.obj.lastMouseX = ex;
        Drag.obj.lastMouseY = ey;

        Drag.obj.root.onDrag(nx, ny);
        evt.preventDefault();
        evt.stopPropagation();
        return false;
    },

    end: function () {
        //document.ontouchmove = null;
        //document.ontouchend = null;
        Drag.unbindEvent(document, "touchmove", Drag.drag);
        Drag.unbindEvent(document, "touchend", Drag.end);
        Drag.obj.root.onDragEnd(parseInt(Drag.obj.root.style[Drag.obj.hmode ? "left" : "right"]),
            parseInt(Drag.obj.root.style[Drag.obj.vmode ? "top" : "bottom"]));
        Drag.obj = null;
    },

    fixE: function (e) {
        var result = null;
        if (typeof e == 'undefined') e = window.event;
        result = e['touches'][0];
        if (typeof result.layerX == 'undefined') result.layerX = result.offsetX;
        if (typeof result.layerY == 'undefined') result.layerY = result.offsetY;
        return result;
    }
};

function Resize(resizeObj, event, direction, minWidth, minHeight, maxX, maxY) {
    if (typeof (resizeObj.CanResize) != 'undefined' && !resizeObj.CanResize) return;
    resizeObj.style.width = resizeObj.offsetWidth;
    resizeObj.style.height = resizeObj.offsetHeight;
    resizeObj.ResizeState = true;

    var oldX = event['touches'][0].clientX;
    var oldY = event['touches'][0].clientY;
    var oldWidth = resizeObj.offsetWidth;
    var oldHeight = resizeObj.offsetHeight;
    oldOffsetRight = screen.width - resizeObj.offsetLeft - resizeObj.offsetWidth;
    oldOffsetBottom = screen.height - resizeObj.offsetTop - resizeObj.offsetHeight;

    var isChanged = false;
    var newHeight = 0, newWidth = 0;

    _attachEvent(document, "touchmove", moveHandler);
    _attachEvent(document, 'touchend', resizeEnd);
    _cancelBubble(event);
    _cancelDefault(event);
    function moveHandler(e) {
        var evt = e;
        e = fixE(e);
        var chgX = 1;
        var chgY = 1;
        switch (direction) {
            case 'e':
                chgX = 1;
                chgY = 0;
                break;
            case 'n':
                chgX = 0;
                chgY = -1;
                break;
            case 'ne':
                chgX = 1;
                chgY = -1;
                break;
            case 'nw':
                chgX = -1;
                chgY = -1;
                break;
            case 's':
                chgX = 0;
                chgY = 1;
                break;
            case 'se':
                chgX = 1;
                chgY = 1;
                break;
            case 'sw':
                chgX = -1;
                chgY = 1;
                break;
            case 'w':
                chgX = -1;
                chgY = 0;
                break;
        }
        if (chgX < 0) {
            newWidth = (oldWidth + oldX - e.clientX);
        }
        else if (chgX > 0) {
            newWidth = (oldWidth + e.clientX - oldX);
        }
        if (chgY < 0) {
            newHeight = (oldHeight + oldY - e.clientY);
        }
        else if (chgY > 0) {
            newHeight = (oldHeight + e.clientY - oldY);
        }
        if (chgX != 0 && minWidth && newWidth < minWidth)
            newWidth = minWidth;

        if (chgY != 0 && minHeight && newHeight < minHeight)
            newHeight = minHeight;

        if (chgX < 0) {
            resizeObj.style.left = screen.width - oldOffsetRight - newWidth + 'px';
        }

        if (chgY < 0) {
            resizeObj.style.top = screen.height - oldOffsetBottom - newHeight + 'px';
        }

        if (newWidth > 0)
            resizeObj.style.width = newWidth + "px";
        if (newHeight > 0)
            resizeObj.style.height = newHeight + "px";

        if (resizeObj.onSizeChange) resizeObj.onSizeChange();
        if (resizeObj.onSizeChanging) resizeObj.onSizeChanging();
        _cancelDefault(evt);
        _cancelBubble(evt);
    }

    function resizeEnd() {
        if (resizeObj.offsetWidth != oldWidth || resizeObj.offsetHeight != oldHeight)
            isChanged = true;

        if (isChanged && resizeObj.getAttributeNode("onsizechanged"))
            eval(resizeObj.getAttributeNode("onsizechanged").nodeValue);
        else if (isChanged && resizeObj.getAttributeNode("onSizeChanged"))
            eval(resizeObj.resizeObj.getAttributeNode("onSizeChanged").nodeValue);


        if (resizeObj.getAttributeNode("onResizeCompleted"))
            eval(resizeObj.getAttributeNode("onResizeCompleted").nodeValue);
        if (resizeObj.onSizeChanged) resizeObj.onSizeChanged();

        resizeObj.ResizeState = false;
        _detachEvent(document, 'touchmove', moveHandler);
        _detachEvent(document, 'touchend', resizeEnd);

        _cancelBubble(event);
        if (resizeObj.layerid)
            ChangeLayer(resizeObj);
    }
    function fixE(e) {
        var result = null;
        if (typeof e == 'undefined') e = window.event;
        result = e['touches'][0];
        if (typeof result.layerX == 'undefined') result.layerX = result.offsetX;
        if (typeof result.layerY == 'undefined') result.layerY = result.offsetY;
        return result;
    }
}