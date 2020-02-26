var DetectZoom = {
    mediaQueryBinarySearch: function (
      property, unit, a, b, maxIter, epsilon) {
        var matchMedia;
        var head, style, div
        if (window.matchMedia) {
            matchMedia = window.matchMedia;
        } else {
            head = document.getElementsByTagName('head')[0];
            style = document.createElement('style');
            div = document.createElement('div');
            div.className = 'mediaQueryBinarySearch';
            head.appendChild(style);
            div.style.display = 'none';
            document.body.appendChild(div);
            matchMedia = function (query) {
                style.sheet.insertRule('@media ' + query +
                               '{.mediaQueryBinarySearch ' +
                               '{text-decoration: underline} }', 0);
                var matched = getComputedStyle(div, null).textDecoration == 'underline';
                style.sheet.deleteRule(0);
                return { matches: matched };
            }
        }
        var r = binarySearch(a, b, maxIter);
        if (div) {
            head.removeChild(style);
            document.body.removeChild(div);
        }
        return r;
        function binarySearch(a, b, maxIter) {
            var mid = (a + b) / 2;
            if (maxIter == 0 || b - a < epsilon) return mid;
            var query = "(" + property + ":" + mid + unit + ")";
            if (matchMedia(query).matches) {
                return binarySearch(mid, b, maxIter - 1);
            } else {
                return binarySearch(a, mid, maxIter - 1);
            }
        }
    },
    _zoomIe7: function () {
        var rect = document.body.getBoundingClientRect();
        var z = (rect.right - rect.left) / document.body.offsetWidth;
        z = Math.round(z * 100) / 100;
        return { zoom: z, devicePxPerCssPx: z };
    },
    _zoomIe8: function () {
        var zoom = screen.deviceXDPI / screen.logicalXDPI;
        return {
            zoom: zoom,
            devicePxPerCssPx: zoom
        };
    },
    _zoomWebkitMobile: function () {
        var devicePixelRatio = window.devicePixelRatio != null ? window.devicePixelRatio : 1, deviceWidth;
        if (Math.abs(window.orientation) == 90) {
            deviceWidth = screen.height;
        } else {
            deviceWidth = screen.width;
        }
        var z = deviceWidth / window.innerWidth;
        return { zoom: z, devicePxPerCssPx: z * devicePixelRatio };
    },
    _zoomWebkit: function () {
        var devicePixelRatio = window.devicePixelRatio != null ? window.devicePixelRatio : 1;
        var container = document.createElement('div'), div = document.createElement('div');
        var important = function (str) { return str.replace(/;/g, " !important;"); };
        container.setAttribute('style', important('width:0; height:0; overflow:hidden; visibility:hidden; position: absolute;'));
        div.innerHTML = "1<br>2<br>3<br>4<br>5<br>6<br>7<br>8<br>9<br>0";
        div.setAttribute('style', important('font: 100px/1em sans-serif; -webkit-text-size-adjust: none; height: auto; width: 1em; padding: 0; overflow: visible;'));
        container.appendChild(div);
        document.body.appendChild(container);
        var z = 1000 / div.clientHeight;
        z = Math.round(z * 100) / 100;
        var r = {
            zoom: z,
            devicePxPerCssPx: devicePixelRatio * z
        };
        document.body.removeChild(container);
        return r;
    },
    _zoomFF35: function () {
        var z = screen.width / this.mediaQueryBinarySearch('min-device-width', 'px', 0, 6000, 20, .0001);
        z = Math.round(z * 100) / 100;
        return { zoom: z, devicePxPerCssPx: z };
    },
    _zoomFF36: function () {
        var container = document.createElement('div'), outerDiv = document.createElement('div');
        container.setAttribute('style', 'width:0; height:0; overflow:hidden;' + 'visibility:hidden; position: absolute');
        outerDiv.style.width = outerDiv.style.height = '500px';  // enough for all the scrollbars
        var div = outerDiv;
        for (var i = 0; i < 10; ++i) {
            var child = document.createElement('div');
            child.style.overflowY = 'scroll';
            div.appendChild(child);
            div = child;
        }
        container.appendChild(outerDiv);
        document.body.appendChild(container);
        var outerDivWidth = outerDiv.clientWidth;
        var innerDivWidth = div.clientWidth;
        var scrollbarWidthCss = (outerDivWidth - innerDivWidth) / 10;
        document.body.removeChild(container);
        var scrollbarWidthDevice = 15;  // Mac and Linux: scrollbars are 15px wide
        if (-1 != navigator.platform.indexOf('Win')) {
            scrollbarWidthDevice = 17;
        }
        var z = scrollbarWidthDevice / scrollbarWidthCss;
        var t = Math.round(z * 100) / 100;
        if (z == 1.0625) z = 1;
        else if (t == 0.67) z = 0.667;
        else {
            z = Math.round(z * 10) / 10;
        }
        if (z == 1.3) z = 1.333;
        return { zoom: z, devicePxPerCssPx: z };
    },
    _zoomFF4: function () {
        var z = this.mediaQueryBinarySearch(
            'min--moz-device-pixel-ratio',
            '', 0, 10, 20, .0001);
        z = Math.round(z * 100) / 100;
        return { zoom: z, devicePxPerCssPx: z };
    },
    _zoomOperaOlder: function () {
        var fixedDiv = document.createElement('div');
        fixedDiv.style.position = 'fixed';
        fixedDiv.style.width = '100%';
        fixedDiv.style.height = '100%';
        fixedDiv.style.top = fixedDiv.style.left = '0';
        fixedDiv.style.visibility = 'hidden';
        document.body.appendChild(fixedDiv);
        var z = window.innerWidth / fixedDiv.offsetWidth;
        document.body.removeChild(fixedDiv);
        return { zoom: z, devicePxPerCssPx: z };
    },
    _zoomOpera11: function () {
        var z = window.outerWidth / window.innerWidth;
        z = Math.round(z * 100) / 100;
        return { zoom: z, devicePxPerCssPx: z };
    },
    ratios: function () {
        var r;
        if (!isNaN(screen.logicalXDPI) && !isNaN(screen.systemXDPI)) {
            return this._zoomIe8();
        }
        else if (-1 != navigator.userAgent.indexOf('Firefox/3.5')) {
            return this._zoomFF35();
        }
        else if (-1 != navigator.userAgent.indexOf('Firefox/')) {
            return this._zoomFF36();
        }
        else if (navigator.userAgent.toLowerCase().indexOf('chrome') != -1) {
            return this._zoomOpera11();
        }
        else if ('ontouchstart' in window && document.body.style.webkitTextSizeAdjust != null) {
            return this._zoomWebkitMobile();
        } else if (document.body.style.webkitTextSizeAdjust != null) {  // webkit
            return this._zoomWebkit();
        } else if (-1 != navigator.userAgent.indexOf('Windows NT 5.2') && -1 != navigator.userAgent.indexOf('Firefox/'))//"Mozilla/5.0 (Windows NT 5.2; rv:43.0) Gecko/20100101 Firefox/43.0"
        {
            r = this._zoomFF4().zoom;
            if (r != 1.33 && r != 0.67) {
                r = Math.round(r * 10) / 10;
            }
            return { zoom: r, devicePxPerCssPx: r };
        }
       else if (-1 != navigator.appVersion.indexOf("MSIE 7.")) {
            return this._zoomIe7();
        } else if (-1 != navigator.userAgent.indexOf('Opera')) {
            var versionIdx = navigator.userAgent.indexOf('Version/');
            if (11.01 < parseFloat(navigator.userAgent.substr(versionIdx + 8)))
                return this._zoomOpera11();
            else
                return this._zoomOperaOlder();
        } else if (0.001 < (r = this._zoomFF4()).zoom) {
            return r;
        }
        else {
            return { zoom: 1, devicePxPerCssPx: 1 }
        }
    },
    zoom: function () {
        return this.ratios().zoom;
    },
    device: function () {
        return this.ratios().devicePxPerCssPx;
    }
};
