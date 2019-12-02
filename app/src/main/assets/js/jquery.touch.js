(function ($) {
    $.fn.touch = function (options) {
        var that = this;
        that.options = {
            onLeft: null,
            onRight: null,
            onUp: null,
            onDown: null,
            onEnd: null,
            beforeZoom: null,
            onZoom: null,
            onZoomEnd: null,
            EnableVertical: false,
            MoveInterval: 250
        };
        //that.css("position", "absolute");
        for (var i in options) that.options[i] = options[i];
        that._startinfo = {};
        that._startzoominfo = {};
        that._midPoint = [0, 0];
        that._moveinfo = {};
        that._direction = null;
        that._isndroid = location.href.toLowerCase().indexOf("android_asset") > 0;
        that.bindEvent = function (obj, evt, fun) {
            if (typeof obj.attachEvent != 'undefined') {
                obj.attachEvent('on' + evt, fun);
            } else {
                obj.addEventListener(evt, fun, false);
            }
            return that;
        }
        that.unbindEvent = function (obj, evt, fun) {
            if (typeof obj.detachEvent != 'undefined') {
                obj.detachEvent('on' + evt, fun);
            } else {
                obj.removeEventListener(evt, fun, false);
            }
            return that;
        }
        that.ontouchstart = function (evt) {
            try {
                if (evt['touches'].length == 1) {
                    if (!CanScroll) {
                        //console.log(44);
                        return;
                    }
                }
                else if (evt['touches'].length != 2)
                {
                    return;
                }
                var tch = evt['touches'][0];
                //console.log(tch.target.tagName);
                //if (tch.target.tagName == "DIV") {
                //    return;
                //}
                if (evt['touches'].length == 1)
                {
                    that._startinfo[evt.currentTarget.id] = [tch.clientX, tch.clientY, tch.clientX, tch.clientY, $("#main").scrollLeft(), $("#main").scrollTop()];
                }
                else
                {
                    var tch2 = evt['touches'][1];
                    that._startzoominfo[evt.currentTarget.id] = [tch.clientX, tch.clientY, tch2.clientX, tch2.clientY];
                    that._midPoint = that.getMidPoint(tch.clientX, tch.clientY, tch2.clientX, tch2.clientY);
                    if (that.options.beforeZoom) that.options.beforeZoom(that._midPoint,evt['touches'].length);
                    
                }
                //evt.preventDefault();
            } catch (e) {

            }
        }
        that.ontouchend = function (evt) {
            try
            {
                that._direction = null;
                
                if (typeof (evt.changedTouches) == 'undefined' || evt.changedTouches.length < 1) {
                    if (that.options.onEnd) that.options.onEnd(evt);
                    return;
                }
                else
                {
                    if (that.options.onEndEx) that.options.onEndEx(evt);
                }
                return;
                var id = evt.changedTouches[0].target.id;
                var pid = evt.currentTarget.id;
                var now = that._moveinfo[pid];
                var start = that._startinfo[pid];
                var xdiff = now[0] - start[0];
                var ydiff = now[1] - start[1];

                //evt.preventDefault();
                if (that._direction == "x" && (Math.abs(xdiff) > Math.abs(ydiff) || that.options.EnableVertical == false)) {
                    if (xdiff < 0) {
                        if (Math.abs(xdiff) > that.options.MoveInterval) {
                            if (that.options.onLeft) that.options.onLeft();
                        }
                        else {
                            if (that.options.onEnd) that.options.onEnd(evt);
                        }
                    }
                    if (xdiff > 0) {
                        if (Math.abs(xdiff) > that.options.MoveInterval) {
                            if (that.options.onRight) that.options.onRight();
                        }
                        else {
                            if (that.options.onEnd) that.options.onEnd(evt);
                        }
                    }
                }
                else {
                    if (ydiff < 0) {
                        if (Math.abs(ydiff) > that.options.MoveInterval) {
                            if (that.options.onUp) that.options.onRight();
                        }
                        else {
                            if (that.options.onEnd) that.options.onEnd(evt);
                        }

                    }
                    if (ydiff > 0) {
                        if (Math.abs(ydiff) > that.options.MoveInterval) {
                            if (that.options.onDown) that.options.onRight();
                        }
                        else {
                            if (that.options.onEnd) that.options.onEnd(evt);
                        }
                    }
                }
                that._direction = null;
            } catch (e) {

            }
        }
        that.ontouchmove = function (evt) {
            try
            {
                if (evt['touches'].length == 1) {
                    if (!CanScroll) {
                        //console.log(139);
                        return;
                    }
                }
                else if (evt['touches'].length != 2)
                {
                    return;
                }                
                var tch = evt['touches'][0];
                //ShowMsg(tch.target.tagName + evt['touches'].length);
                //if (tch.target.tagName == "DIV") {
                //    return;
                //}
                if (evt['touches'].length == 1)
                {
                    //ShowMsg("ontouchmove" + tch.clientX);
                    var now = [tch.clientX, tch.clientY];
                    var id = evt.currentTarget.id;
                    that._moveinfo[id] = now;
                    var start = that._startinfo[id];
                    var xdiff = start[2] - now[0];
                    var ydif = start[3] - now[1];
                    //start[3] = now[1];
                    if (that._direction == null)
                    {
                        if (Math.abs(xdiff) > Math.abs(ydif))
                        {
                            that._direction = "x";
                        }
                        else
                        {
                            that._direction = "y";
                        }
                    }
                    if (that._direction == "x")
                    {
                        //$("#" + id).css("left", -xdiff);
                        var movediff = start[4] + xdiff;
                        //ShowMsg("scrollLeft:" + movediff);
                        $("#main").scrollLeft(Math.max(movediff, 0));
                    }
                    else
                    {
                        var movediff = start[5] + ydif;
                        //ShowMsg("scrollTop:" + now[1] + "," + start[3]);
                        $("#main").scrollTop(Math.max(movediff, 0))
                    }
                    _cancelDefault(evt);
                    _cancelBubble(evt);
                }
                else if (evt['touches'].length == 2)
                {
                    var id = evt.currentTarget.id;
                    var tch2 = evt['touches'][1];
                    var start = that._startzoominfo[id];
                    var end = [tch.clientX, tch.clientY, tch2.clientX, tch2.clientY];
                    var zoom = that.getLength(end[0], end[1], end[2], end[3]) / that.getLength(start[0], start[1], start[2], start[3]);
                    if (that.options.onZoom) that.options.onZoom(zoom, that._midPoint);
                    //ShowMsg("cancel");
                    _cancelDefault(evt);
                    _cancelBubble(evt);
                }
                //evt.preventDefault();
            } catch (e) {
                //alert('touchmove error=' + e);
            }
        }
        that.getLength = function (x1, y1, x2, y2)
        {
            var calX = x2 - x1;
            var calY = y2 - y1;
            return Math.pow((calX * calX + calY * calY), 0.5);
        }
        that.getMidPoint = function (x1, y1, x2, y2)
        {
            return [x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2];
        }
        that.each(function (i) {
            that.unbindEvent($(this)[0], "touchstart", that.ontouchstart);            
            that.unbindEvent($(this)[0], "touchend", that.ontouchend);            
            that.unbindEvent($(this)[0], "touchmove", that.ontouchmove);
            
            that.bindEvent($(this)[0], "touchstart", that.ontouchstart);
            that.bindEvent($(this)[0], "touchend", that.ontouchend);
            that.bindEvent($(this)[0], "touchmove", that.ontouchmove);            
        });
        return that;
    }
})(jQuery);