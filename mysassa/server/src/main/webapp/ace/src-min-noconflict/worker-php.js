"no use strict";
(function (e) {
    if (typeof e.window != "undefined" && e.document)return;
    e.console = function () {
        var e = Array.prototype.slice.call(arguments, 0);
        postMessage({type: "log", data: e})
    }, e.console.error = e.console.warn = e.console.log = e.console.trace = e.console, e.window = e, e.ace = e, e.onerror = function (e, t, n, r, i) {
        console.error("Worker " + (i ? i.stack : e))
    }, e.normalizeModule = function (t, n) {
        if (n.indexOf("!") !== -1) {
            var r = n.split("!");
            return e.normalizeModule(t, r[0]) + "!" + e.normalizeModule(t, r[1])
        }
        if (n.charAt(0) == ".") {
            var i = t.split("/").slice(0, -1).join("/");
            n = (i ? i + "/" : "") + n;
            while (n.indexOf(".") !== -1 && s != n) {
                var s = n;
                n = n.replace(/^\.\//, "").replace(/\/\.\//, "/").replace(/[^\/]+\/\.\.\//, "")
            }
        }
        return n
    }, e.require = function (t, n) {
        n || (n = t, t = null);
        if (!n.charAt)throw new Error("worker.js require() accepts only (parentId, id) as arguments");
        n = e.normalizeModule(t, n);
        var r = e.require.modules[n];
        if (r)return r.initialized || (r.initialized = !0, r.exports = r.factory().exports), r.exports;
        var i = n.split("/");
        if (!e.require.tlns)return console.log("unable to load " + n);
        i[0] = e.require.tlns[i[0]] || i[0];
        var s = i.join("/") + ".js";
        return e.require.id = n, importScripts(s), e.require(t, n)
    }, e.require.modules = {}, e.require.tlns = {}, e.define = function (t, n, r) {
        arguments.length == 2 ? (r = n, typeof t != "string" && (n = t, t = e.require.id)) : arguments.length == 1 && (r = t, n = [], t = e.require.id), n.length || (n = ["require", "exports", "module"]);
        if (t.indexOf("text!") === 0)return;
        var i = function (n) {
            return e.require(t, n)
        };
        e.require.modules[t] = {exports: {}, factory: function () {
            var e = this, t = r.apply(this, n.map(function (t) {
                switch (t) {
                    case"require":
                        return i;
                    case"exports":
                        return e.exports;
                    case"module":
                        return e;
                    default:
                        return i(t)
                }
            }));
            return t && (e.exports = t), e
        }}
    }, e.define.amd = {}, e.initBaseUrls = function (e) {
        require.tlns = e
    }, e.initSender = function () {
        var t = e.require("ace/lib/event_emitter").EventEmitter, n = e.require("ace/lib/oop"), r = function () {
        };
        return function () {
            n.implement(this, t), this.callback = function (e, t) {
                postMessage({type: "call", id: t, data: e})
            }, this.emit = function (e, t) {
                postMessage({type: "event", name: e, data: t})
            }
        }.call(r.prototype), new r
    };
    var t = e.main = null, n = e.sender = null;
    e.onmessage = function (r) {
        var i = r.data;
        if (i.command) {
            if (!t[i.command])throw new Error("Unknown command:" + i.command);
            t[i.command].apply(t, i.args)
        } else if (i.init) {
            initBaseUrls(i.tlns), require("ace/lib/es5-shim"), n = e.sender = initSender();
            var s = require(i.module)[i.classname];
            t = e.main = new s(n)
        } else i.event && n && n._signal(i.event, i.data)
    }
})(this), ace.define("ace/mode/php_worker", ["require", "exports", "module", "ace/lib/oop", "ace/worker/mirror", "ace/mode/php/php"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("../worker/mirror").Mirror, s = e("./php/php").PHP, o = t.PhpWorker = function (e) {
        i.call(this, e), this.setTimeout(500)
    };
    r.inherits(o, i), function () {
        this.setOptions = function (e) {
            this.inlinePhp = e && e.inline
        }, this.onUpdate = function () {
            var e = this.doc.getValue(), t = [];
            this.inlinePhp && (e = "<?" + e + "?>");
            var n = s.Lexer(e, {short_open_tag: 1});
            try {
                new s.Parser(n)
            } catch (r) {
                t.push({row: r.line - 1, column: null, text: r.message.charAt(0).toUpperCase() + r.message.substring(1), type: "error"})
            }
            t.length ? this.sender.emit("error", t) : this.sender.emit("ok")
        }
    }.call(o.prototype)
}), ace.define("ace/lib/oop", ["require", "exports", "module"], function (e, t, n) {
    t.inherits = function (e, t) {
        e.super_ = t, e.prototype = Object.create(t.prototype, {constructor: {value: e, enumerable: !1, writable: !0, configurable: !0}})
    }, t.mixin = function (e, t) {
        for (var n in t)e[n] = t[n];
        return e
    }, t.implement = function (e, n) {
        t.mixin(e, n)
    }
}), ace.define("ace/worker/mirror", ["require", "exports", "module", "ace/document", "ace/lib/lang"], function (e, t, n) {
    var r = e("../document").Document, i = e("../lib/lang"), s = t.Mirror = function (e) {
        this.sender = e;
        var t = this.doc = new r(""), n = this.deferredUpdate = i.delayedCall(this.onUpdate.bind(this)), s = this;
        e.on("change", function (e) {
            t.applyDeltas(e.data);
            if (s.$timeout)return n.schedule(s.$timeout);
            s.onUpdate()
        })
    };
    (function () {
        this.$timeout = 500, this.setTimeout = function (e) {
            this.$timeout = e
        }, this.setValue = function (e) {
            this.doc.setValue(e), this.deferredUpdate.schedule(this.$timeout)
        }, this.getValue = function (e) {
            this.sender.callback(this.doc.getValue(), e)
        }, this.onUpdate = function () {
        }, this.isPending = function () {
            return this.deferredUpdate.isPending()
        }
    }).call(s.prototype)
}), ace.define("ace/lib/es5-shim", ["require", "exports", "module"], function (e, t, n) {
    function r() {
    }

    function i(e) {
        try {
            return Object.defineProperty(e, "sentinel", {}), "sentinel"in e
        } catch (t) {
        }
    }

    function s(e) {
        return e = +e, e !== e ? e = 0 : e !== 0 && e !== 1 / 0 && e !== -1 / 0 && (e = (e > 0 || -1) * Math.floor(Math.abs(e))), e
    }

    function o(e) {
        var t = typeof e;
        return e === null || t === "undefined" || t === "boolean" || t === "number" || t === "string"
    }

    function u(e) {
        var t, n, r;
        if (o(e))return e;
        n = e.valueOf;
        if (typeof n == "function") {
            t = n.call(e);
            if (o(t))return t
        }
        r = e.toString;
        if (typeof r == "function") {
            t = r.call(e);
            if (o(t))return t
        }
        throw new TypeError
    }

    Function.prototype.bind || (Function.prototype.bind = function (e) {
        var t = this;
        if (typeof t != "function")throw new TypeError("Function.prototype.bind called on incompatible " + t);
        var n = c.call(arguments, 1), i = function () {
            if (this instanceof i) {
                var r = t.apply(this, n.concat(c.call(arguments)));
                return Object(r) === r ? r : this
            }
            return t.apply(e, n.concat(c.call(arguments)))
        };
        return t.prototype && (r.prototype = t.prototype, i.prototype = new r, r.prototype = null), i
    });
    var a = Function.prototype.call, f = Array.prototype, l = Object.prototype, c = f.slice, h = a.bind(l.toString), p = a.bind(l.hasOwnProperty), d, v, m, g, y;
    if (y = p(l, "__defineGetter__"))d = a.bind(l.__defineGetter__), v = a.bind(l.__defineSetter__), m = a.bind(l.__lookupGetter__), g = a.bind(l.__lookupSetter__);
    if ([1, 2].splice(0).length != 2)if (!function () {
        function e(e) {
            var t = new Array(e + 2);
            return t[0] = t[1] = 0, t
        }

        var t = [], n;
        t.splice.apply(t, e(20)), t.splice.apply(t, e(26)), n = t.length, t.splice(5, 0, "XXX"), n + 1 == t.length;
        if (n + 1 == t.length)return!0
    }())Array.prototype.splice = function (e, t) {
        var n = this.length;
        e > 0 ? e > n && (e = n) : e == void 0 ? e = 0 : e < 0 && (e = Math.max(n + e, 0)), e + t < n || (t = n - e);
        var r = this.slice(e, e + t), i = c.call(arguments, 2), s = i.length;
        if (e === n)s && this.push.apply(this, i); else {
            var o = Math.min(t, n - e), u = e + o, a = u + s - o, f = n - u, l = n - o;
            if (a < u)for (var h = 0; h < f; ++h)this[a + h] = this[u + h]; else if (a > u)for (h = f; h--;)this[a + h] = this[u + h];
            if (s && e === l)this.length = l, this.push.apply(this, i); else {
                this.length = l + s;
                for (h = 0; h < s; ++h)this[e + h] = i[h]
            }
        }
        return r
    }; else {
        var b = Array.prototype.splice;
        Array.prototype.splice = function (e, t) {
            return arguments.length ? b.apply(this, [e === void 0 ? 0 : e, t === void 0 ? this.length - e : t].concat(c.call(arguments, 2))) : []
        }
    }
    Array.isArray || (Array.isArray = function (e) {
        return h(e) == "[object Array]"
    });
    var w = Object("a"), E = w[0] != "a" || !(0 in w);
    Array.prototype.forEach || (Array.prototype.forEach = function (e) {
        var t = F(this), n = E && h(this) == "[object String]" ? this.split("") : t, r = arguments[1], i = -1, s = n.length >>> 0;
        if (h(e) != "[object Function]")throw new TypeError;
        while (++i < s)i in n && e.call(r, n[i], i, t)
    }), Array.prototype.map || (Array.prototype.map = function (e) {
        var t = F(this), n = E && h(this) == "[object String]" ? this.split("") : t, r = n.length >>> 0, i = Array(r), s = arguments[1];
        if (h(e) != "[object Function]")throw new TypeError(e + " is not a function");
        for (var o = 0; o < r; o++)o in n && (i[o] = e.call(s, n[o], o, t));
        return i
    }), Array.prototype.filter || (Array.prototype.filter = function (e) {
        var t = F(this), n = E && h(this) == "[object String]" ? this.split("") : t, r = n.length >>> 0, i = [], s, o = arguments[1];
        if (h(e) != "[object Function]")throw new TypeError(e + " is not a function");
        for (var u = 0; u < r; u++)u in n && (s = n[u], e.call(o, s, u, t) && i.push(s));
        return i
    }), Array.prototype.every || (Array.prototype.every = function (e) {
        var t = F(this), n = E && h(this) == "[object String]" ? this.split("") : t, r = n.length >>> 0, i = arguments[1];
        if (h(e) != "[object Function]")throw new TypeError(e + " is not a function");
        for (var s = 0; s < r; s++)if (s in n && !e.call(i, n[s], s, t))return!1;
        return!0
    }), Array.prototype.some || (Array.prototype.some = function (e) {
        var t = F(this), n = E && h(this) == "[object String]" ? this.split("") : t, r = n.length >>> 0, i = arguments[1];
        if (h(e) != "[object Function]")throw new TypeError(e + " is not a function");
        for (var s = 0; s < r; s++)if (s in n && e.call(i, n[s], s, t))return!0;
        return!1
    }), Array.prototype.reduce || (Array.prototype.reduce = function (e) {
        var t = F(this), n = E && h(this) == "[object String]" ? this.split("") : t, r = n.length >>> 0;
        if (h(e) != "[object Function]")throw new TypeError(e + " is not a function");
        if (!r && arguments.length == 1)throw new TypeError("reduce of empty array with no initial value");
        var i = 0, s;
        if (arguments.length >= 2)s = arguments[1]; else do {
            if (i in n) {
                s = n[i++];
                break
            }
            if (++i >= r)throw new TypeError("reduce of empty array with no initial value")
        } while (!0);
        for (; i < r; i++)i in n && (s = e.call(void 0, s, n[i], i, t));
        return s
    }), Array.prototype.reduceRight || (Array.prototype.reduceRight = function (e) {
        var t = F(this), n = E && h(this) == "[object String]" ? this.split("") : t, r = n.length >>> 0;
        if (h(e) != "[object Function]")throw new TypeError(e + " is not a function");
        if (!r && arguments.length == 1)throw new TypeError("reduceRight of empty array with no initial value");
        var i, s = r - 1;
        if (arguments.length >= 2)i = arguments[1]; else do {
            if (s in n) {
                i = n[s--];
                break
            }
            if (--s < 0)throw new TypeError("reduceRight of empty array with no initial value")
        } while (!0);
        do s in this && (i = e.call(void 0, i, n[s], s, t)); while (s--);
        return i
    });
    if (!Array.prototype.indexOf || [0, 1].indexOf(1, 2) != -1)Array.prototype.indexOf = function (e) {
        var t = E && h(this) == "[object String]" ? this.split("") : F(this), n = t.length >>> 0;
        if (!n)return-1;
        var r = 0;
        arguments.length > 1 && (r = s(arguments[1])), r = r >= 0 ? r : Math.max(0, n + r);
        for (; r < n; r++)if (r in t && t[r] === e)return r;
        return-1
    };
    if (!Array.prototype.lastIndexOf || [0, 1].lastIndexOf(0, -3) != -1)Array.prototype.lastIndexOf = function (e) {
        var t = E && h(this) == "[object String]" ? this.split("") : F(this), n = t.length >>> 0;
        if (!n)return-1;
        var r = n - 1;
        arguments.length > 1 && (r = Math.min(r, s(arguments[1]))), r = r >= 0 ? r : n - Math.abs(r);
        for (; r >= 0; r--)if (r in t && e === t[r])return r;
        return-1
    };
    Object.getPrototypeOf || (Object.getPrototypeOf = function (e) {
        return e.__proto__ || (e.constructor ? e.constructor.prototype : l)
    });
    if (!Object.getOwnPropertyDescriptor) {
        var S = "Object.getOwnPropertyDescriptor called on a non-object: ";
        Object.getOwnPropertyDescriptor = function (e, t) {
            if (typeof e != "object" && typeof e != "function" || e === null)throw new TypeError(S + e);
            if (!p(e, t))return;
            var n, r, i;
            n = {enumerable: !0, configurable: !0};
            if (y) {
                var s = e.__proto__;
                e.__proto__ = l;
                var r = m(e, t), i = g(e, t);
                e.__proto__ = s;
                if (r || i)return r && (n.get = r), i && (n.set = i), n
            }
            return n.value = e[t], n
        }
    }
    Object.getOwnPropertyNames || (Object.getOwnPropertyNames = function (e) {
        return Object.keys(e)
    });
    if (!Object.create) {
        var x;
        Object.prototype.__proto__ === null ? x = function () {
            return{__proto__: null}
        } : x = function () {
            var e = {};
            for (var t in e)e[t] = null;
            return e.constructor = e.hasOwnProperty = e.propertyIsEnumerable = e.isPrototypeOf = e.toLocaleString = e.toString = e.valueOf = e.__proto__ = null, e
        }, Object.create = function (e, t) {
            var n;
            if (e === null)n = x(); else {
                if (typeof e != "object")throw new TypeError("typeof prototype[" + typeof e + "] != 'object'");
                var r = function () {
                };
                r.prototype = e, n = new r, n.__proto__ = e
            }
            return t !== void 0 && Object.defineProperties(n, t), n
        }
    }
    if (Object.defineProperty) {
        var T = i({}), N = typeof document == "undefined" || i(document.createElement("div"));
        if (!T || !N)var C = Object.defineProperty
    }
    if (!Object.defineProperty || C) {
        var k = "Property description must be an object: ", L = "Object.defineProperty called on non-object: ", A = "getters & setters can not be defined on this javascript engine";
        Object.defineProperty = function (e, t, n) {
            if (typeof e != "object" && typeof e != "function" || e === null)throw new TypeError(L + e);
            if (typeof n != "object" && typeof n != "function" || n === null)throw new TypeError(k + n);
            if (C)try {
                return C.call(Object, e, t, n)
            } catch (r) {
            }
            if (p(n, "value"))if (y && (m(e, t) || g(e, t))) {
                var i = e.__proto__;
                e.__proto__ = l, delete e[t], e[t] = n.value, e.__proto__ = i
            } else e[t] = n.value; else {
                if (!y)throw new TypeError(A);
                p(n, "get") && d(e, t, n.get), p(n, "set") && v(e, t, n.set)
            }
            return e
        }
    }
    Object.defineProperties || (Object.defineProperties = function (e, t) {
        for (var n in t)p(t, n) && Object.defineProperty(e, n, t[n]);
        return e
    }), Object.seal || (Object.seal = function (e) {
        return e
    }), Object.freeze || (Object.freeze = function (e) {
        return e
    });
    try {
        Object.freeze(function () {
        })
    } catch (O) {
        Object.freeze = function (e) {
            return function (t) {
                return typeof t == "function" ? t : e(t)
            }
        }(Object.freeze)
    }
    Object.preventExtensions || (Object.preventExtensions = function (e) {
        return e
    }), Object.isSealed || (Object.isSealed = function (e) {
        return!1
    }), Object.isFrozen || (Object.isFrozen = function (e) {
        return!1
    }), Object.isExtensible || (Object.isExtensible = function (e) {
        if (Object(e) === e)throw new TypeError;
        var t = "";
        while (p(e, t))t += "?";
        e[t] = !0;
        var n = p(e, t);
        return delete e[t], n
    });
    if (!Object.keys) {
        var M = !0, _ = ["toString", "toLocaleString", "valueOf", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable", "constructor"], D = _.length;
        for (var P in{toString: null})M = !1;
        Object.keys = function I(e) {
            if (typeof e != "object" && typeof e != "function" || e === null)throw new TypeError("Object.keys called on a non-object");
            var I = [];
            for (var t in e)p(e, t) && I.push(t);
            if (M)for (var n = 0, r = D; n < r; n++) {
                var i = _[n];
                p(e, i) && I.push(i)
            }
            return I
        }
    }
    Date.now || (Date.now = function () {
        return(new Date).getTime()
    });
    var H = "	\n\f\r   ᠎             　\u2028\u2029﻿";
    if (!String.prototype.trim || H.trim()) {
        H = "[" + H + "]";
        var B = new RegExp("^" + H + H + "*"), j = new RegExp(H + H + "*$");
        String.prototype.trim = function () {
            return String(this).replace(B, "").replace(j, "")
        }
    }
    var F = function (e) {
        if (e == null)throw new TypeError("can't convert " + e + " to object");
        return Object(e)
    }
}), ace.define("ace/lib/event_emitter", ["require", "exports", "module"], function (e, t, n) {
    var r = {}, i = function () {
        this.propagationStopped = !0
    }, s = function () {
        this.defaultPrevented = !0
    };
    r._emit = r._dispatchEvent = function (e, t) {
        this._eventRegistry || (this._eventRegistry = {}), this._defaultHandlers || (this._defaultHandlers = {});
        var n = this._eventRegistry[e] || [], r = this._defaultHandlers[e];
        if (!n.length && !r)return;
        if (typeof t != "object" || !t)t = {};
        t.type || (t.type = e), t.stopPropagation || (t.stopPropagation = i), t.preventDefault || (t.preventDefault = s), n = n.slice();
        for (var o = 0; o < n.length; o++) {
            n[o](t, this);
            if (t.propagationStopped)break
        }
        if (r && !t.defaultPrevented)return r(t, this)
    }, r._signal = function (e, t) {
        var n = (this._eventRegistry || {})[e];
        if (!n)return;
        n = n.slice();
        for (var r = 0; r < n.length; r++)n[r](t, this)
    }, r.once = function (e, t) {
        var n = this;
        t && this.addEventListener(e, function r() {
            n.removeEventListener(e, r), t.apply(null, arguments)
        })
    }, r.setDefaultHandler = function (e, t) {
        var n = this._defaultHandlers;
        n || (n = this._defaultHandlers = {_disabled_: {}});
        if (n[e]) {
            var r = n[e], i = n._disabled_[e];
            i || (n._disabled_[e] = i = []), i.push(r);
            var s = i.indexOf(t);
            s != -1 && i.splice(s, 1)
        }
        n[e] = t
    }, r.removeDefaultHandler = function (e, t) {
        var n = this._defaultHandlers;
        if (!n)return;
        var r = n._disabled_[e];
        if (n[e] == t) {
            var i = n[e];
            r && this.setDefaultHandler(e, r.pop())
        } else if (r) {
            var s = r.indexOf(t);
            s != -1 && r.splice(s, 1)
        }
    }, r.on = r.addEventListener = function (e, t, n) {
        this._eventRegistry = this._eventRegistry || {};
        var r = this._eventRegistry[e];
        return r || (r = this._eventRegistry[e] = []), r.indexOf(t) == -1 && r[n ? "unshift" : "push"](t), t
    }, r.off = r.removeListener = r.removeEventListener = function (e, t) {
        this._eventRegistry = this._eventRegistry || {};
        var n = this._eventRegistry[e];
        if (!n)return;
        var r = n.indexOf(t);
        r !== -1 && n.splice(r, 1)
    }, r.removeAllListeners = function (e) {
        this._eventRegistry && (this._eventRegistry[e] = [])
    }, t.EventEmitter = r
}), ace.define("ace/range", ["require", "exports", "module"], function (e, t, n) {
    var r = function (e, t) {
        return e.row - t.row || e.column - t.column
    }, i = function (e, t, n, r) {
        this.start = {row: e, column: t}, this.end = {row: n, column: r}
    };
    (function () {
        this.isEqual = function (e) {
            return this.start.row === e.start.row && this.end.row === e.end.row && this.start.column === e.start.column && this.end.column === e.end.column
        }, this.toString = function () {
            return"Range: [" + this.start.row + "/" + this.start.column + "] -> [" + this.end.row + "/" + this.end.column + "]"
        }, this.contains = function (e, t) {
            return this.compare(e, t) == 0
        }, this.compareRange = function (e) {
            var t, n = e.end, r = e.start;
            return t = this.compare(n.row, n.column), t == 1 ? (t = this.compare(r.row, r.column), t == 1 ? 2 : t == 0 ? 1 : 0) : t == -1 ? -2 : (t = this.compare(r.row, r.column), t == -1 ? -1 : t == 1 ? 42 : 0)
        }, this.comparePoint = function (e) {
            return this.compare(e.row, e.column)
        }, this.containsRange = function (e) {
            return this.comparePoint(e.start) == 0 && this.comparePoint(e.end) == 0
        }, this.intersects = function (e) {
            var t = this.compareRange(e);
            return t == -1 || t == 0 || t == 1
        }, this.isEnd = function (e, t) {
            return this.end.row == e && this.end.column == t
        }, this.isStart = function (e, t) {
            return this.start.row == e && this.start.column == t
        }, this.setStart = function (e, t) {
            typeof e == "object" ? (this.start.column = e.column, this.start.row = e.row) : (this.start.row = e, this.start.column = t)
        }, this.setEnd = function (e, t) {
            typeof e == "object" ? (this.end.column = e.column, this.end.row = e.row) : (this.end.row = e, this.end.column = t)
        }, this.inside = function (e, t) {
            return this.compare(e, t) == 0 ? this.isEnd(e, t) || this.isStart(e, t) ? !1 : !0 : !1
        }, this.insideStart = function (e, t) {
            return this.compare(e, t) == 0 ? this.isEnd(e, t) ? !1 : !0 : !1
        }, this.insideEnd = function (e, t) {
            return this.compare(e, t) == 0 ? this.isStart(e, t) ? !1 : !0 : !1
        }, this.compare = function (e, t) {
            return!this.isMultiLine() && e === this.start.row ? t < this.start.column ? -1 : t > this.end.column ? 1 : 0 : e < this.start.row ? -1 : e > this.end.row ? 1 : this.start.row === e ? t >= this.start.column ? 0 : -1 : this.end.row === e ? t <= this.end.column ? 0 : 1 : 0
        }, this.compareStart = function (e, t) {
            return this.start.row == e && this.start.column == t ? -1 : this.compare(e, t)
        }, this.compareEnd = function (e, t) {
            return this.end.row == e && this.end.column == t ? 1 : this.compare(e, t)
        }, this.compareInside = function (e, t) {
            return this.end.row == e && this.end.column == t ? 1 : this.start.row == e && this.start.column == t ? -1 : this.compare(e, t)
        }, this.clipRows = function (e, t) {
            if (this.end.row > t)var n = {row: t + 1, column: 0}; else if (this.end.row < e)var n = {row: e, column: 0};
            if (this.start.row > t)var r = {row: t + 1, column: 0}; else if (this.start.row < e)var r = {row: e, column: 0};
            return i.fromPoints(r || this.start, n || this.end)
        }, this.extend = function (e, t) {
            var n = this.compare(e, t);
            if (n == 0)return this;
            if (n == -1)var r = {row: e, column: t}; else var s = {row: e, column: t};
            return i.fromPoints(r || this.start, s || this.end)
        }, this.isEmpty = function () {
            return this.start.row === this.end.row && this.start.column === this.end.column
        }, this.isMultiLine = function () {
            return this.start.row !== this.end.row
        }, this.clone = function () {
            return i.fromPoints(this.start, this.end)
        }, this.collapseRows = function () {
            return this.end.column == 0 ? new i(this.start.row, 0, Math.max(this.start.row, this.end.row - 1), 0) : new i(this.start.row, 0, this.end.row, 0)
        }, this.toScreenRange = function (e) {
            var t = e.documentToScreenPosition(this.start), n = e.documentToScreenPosition(this.end);
            return new i(t.row, t.column, n.row, n.column)
        }, this.moveBy = function (e, t) {
            this.start.row += e, this.start.column += t, this.end.row += e, this.end.column += t
        }
    }).call(i.prototype), i.fromPoints = function (e, t) {
        return new i(e.row, e.column, t.row, t.column)
    }, i.comparePoints = r, i.comparePoints = function (e, t) {
        return e.row - t.row || e.column - t.column
    }, t.Range = i
}), ace.define("ace/anchor", ["require", "exports", "module", "ace/lib/oop", "ace/lib/event_emitter"], function (e, t, n) {
    var r = e("./lib/oop"), i = e("./lib/event_emitter").EventEmitter, s = t.Anchor = function (e, t, n) {
        this.$onChange = this.onChange.bind(this), this.attach(e), typeof n == "undefined" ? this.setPosition(t.row, t.column) : this.setPosition(t, n)
    };
    (function () {
        r.implement(this, i), this.getPosition = function () {
            return this.$clipPositionToDocument(this.row, this.column)
        }, this.getDocument = function () {
            return this.document
        }, this.$insertRight = !1, this.onChange = function (e) {
            var t = e.data, n = t.range;
            if (n.start.row == n.end.row && n.start.row != this.row)return;
            if (n.start.row > this.row)return;
            if (n.start.row == this.row && n.start.column > this.column)return;
            var r = this.row, i = this.column, s = n.start, o = n.end;
            if (t.action === "insertText")if (s.row === r && s.column <= i) {
                if (s.column !== i || !this.$insertRight)s.row === o.row ? i += o.column - s.column : (i -= s.column, r += o.row - s.row)
            } else s.row !== o.row && s.row < r && (r += o.row - s.row); else t.action === "insertLines" ? s.row <= r && (r += o.row - s.row) : t.action === "removeText" ? s.row === r && s.column < i ? o.column >= i ? i = s.column : i = Math.max(0, i - (o.column - s.column)) : s.row !== o.row && s.row < r ? (o.row === r && (i = Math.max(0, i - o.column) + s.column), r -= o.row - s.row) : o.row === r && (r -= o.row - s.row, i = Math.max(0, i - o.column) + s.column) : t.action == "removeLines" && s.row <= r && (o.row <= r ? r -= o.row - s.row : (r = s.row, i = 0));
            this.setPosition(r, i, !0)
        }, this.setPosition = function (e, t, n) {
            var r;
            n ? r = {row: e, column: t} : r = this.$clipPositionToDocument(e, t);
            if (this.row == r.row && this.column == r.column)return;
            var i = {row: this.row, column: this.column};
            this.row = r.row, this.column = r.column, this._signal("change", {old: i, value: r})
        }, this.detach = function () {
            this.document.removeEventListener("change", this.$onChange)
        }, this.attach = function (e) {
            this.document = e || this.document, this.document.on("change", this.$onChange)
        }, this.$clipPositionToDocument = function (e, t) {
            var n = {};
            return e >= this.document.getLength() ? (n.row = Math.max(0, this.document.getLength() - 1), n.column = this.document.getLine(n.row).length) : e < 0 ? (n.row = 0, n.column = 0) : (n.row = e, n.column = Math.min(this.document.getLine(n.row).length, Math.max(0, t))), t < 0 && (n.column = 0), n
        }
    }).call(s.prototype)
}), ace.define("ace/lib/lang", ["require", "exports", "module"], function (e, t, n) {
    t.last = function (e) {
        return e[e.length - 1]
    }, t.stringReverse = function (e) {
        return e.split("").reverse().join("")
    }, t.stringRepeat = function (e, t) {
        var n = "";
        while (t > 0) {
            t & 1 && (n += e);
            if (t >>= 1)e += e
        }
        return n
    };
    var r = /^\s\s*/, i = /\s\s*$/;
    t.stringTrimLeft = function (e) {
        return e.replace(r, "")
    }, t.stringTrimRight = function (e) {
        return e.replace(i, "")
    }, t.copyObject = function (e) {
        var t = {};
        for (var n in e)t[n] = e[n];
        return t
    }, t.copyArray = function (e) {
        var t = [];
        for (var n = 0, r = e.length; n < r; n++)e[n] && typeof e[n] == "object" ? t[n] = this.copyObject(e[n]) : t[n] = e[n];
        return t
    }, t.deepCopy = function (e) {
        if (typeof e != "object" || !e)return e;
        var n = e.constructor;
        if (n === RegExp)return e;
        var r = n();
        for (var i in e)typeof e[i] == "object" ? r[i] = t.deepCopy(e[i]) : r[i] = e[i];
        return r
    }, t.arrayToMap = function (e) {
        var t = {};
        for (var n = 0; n < e.length; n++)t[e[n]] = 1;
        return t
    }, t.createMap = function (e) {
        var t = Object.create(null);
        for (var n in e)t[n] = e[n];
        return t
    }, t.arrayRemove = function (e, t) {
        for (var n = 0; n <= e.length; n++)t === e[n] && e.splice(n, 1)
    }, t.escapeRegExp = function (e) {
        return e.replace(/([.*+?^${}()|[\]\/\\])/g, "\\$1")
    }, t.escapeHTML = function (e) {
        return e.replace(/&/g, "&#38;").replace(/"/g, "&#34;").replace(/'/g, "&#39;").replace(/</g, "&#60;")
    }, t.getMatchOffsets = function (e, t) {
        var n = [];
        return e.replace(t, function (e) {
            n.push({offset: arguments[arguments.length - 2], length: e.length})
        }), n
    }, t.deferredCall = function (e) {
        var t = null, n = function () {
            t = null, e()
        }, r = function (e) {
            return r.cancel(), t = setTimeout(n, e || 0), r
        };
        return r.schedule = r, r.call = function () {
            return this.cancel(), e(), r
        }, r.cancel = function () {
            return clearTimeout(t), t = null, r
        }, r.isPending = function () {
            return t
        }, r
    }, t.delayedCall = function (e, t) {
        var n = null, r = function () {
            n = null, e()
        }, i = function (e) {
            n == null && (n = setTimeout(r, e || t))
        };
        return i.delay = function (e) {
            n && clearTimeout(n), n = setTimeout(r, e || t)
        }, i.schedule = i, i.call = function () {
            this.cancel(), e()
        }, i.cancel = function () {
            n && clearTimeout(n), n = null
        }, i.isPending = function () {
            return n
        }, i
    }
}), ace.define("ace/mode/php/php", ["require", "exports", "module"], function (e, t, n) {
    var r = {Constants: {}};
    r.Constants.T_INCLUDE = 262, r.Constants.T_INCLUDE_ONCE = 261, r.Constants.T_EVAL = 260, r.Constants.T_REQUIRE = 259, r.Constants.T_REQUIRE_ONCE = 258, r.Constants.T_LOGICAL_OR = 263, r.Constants.T_LOGICAL_XOR = 264, r.Constants.T_LOGICAL_AND = 265, r.Constants.T_PRINT = 266, r.Constants.T_PLUS_EQUAL = 277, r.Constants.T_MINUS_EQUAL = 276, r.Constants.T_MUL_EQUAL = 275, r.Constants.T_DIV_EQUAL = 274, r.Constants.T_CONCAT_EQUAL = 273, r.Constants.T_MOD_EQUAL = 272, r.Constants.T_AND_EQUAL = 271, r.Constants.T_OR_EQUAL = 270, r.Constants.T_XOR_EQUAL = 269, r.Constants.T_SL_EQUAL = 268, r.Constants.T_SR_EQUAL = 267, r.Constants.T_BOOLEAN_OR = 278, r.Constants.T_BOOLEAN_AND = 279, r.Constants.T_IS_EQUAL = 283, r.Constants.T_IS_NOT_EQUAL = 282, r.Constants.T_IS_IDENTICAL = 281, r.Constants.T_IS_NOT_IDENTICAL = 280, r.Constants.T_IS_SMALLER_OR_EQUAL = 285, r.Constants.T_IS_GREATER_OR_EQUAL = 284, r.Constants.T_SL = 287, r.Constants.T_SR = 286, r.Constants.T_INSTANCEOF = 288, r.Constants.T_INC = 297, r.Constants.T_DEC = 296, r.Constants.T_INT_CAST = 295, r.Constants.T_DOUBLE_CAST = 294, r.Constants.T_STRING_CAST = 293, r.Constants.T_ARRAY_CAST = 292, r.Constants.T_OBJECT_CAST = 291, r.Constants.T_BOOL_CAST = 290, r.Constants.T_UNSET_CAST = 289, r.Constants.T_NEW = 299, r.Constants.T_CLONE = 298, r.Constants.T_EXIT = 300, r.Constants.T_IF = 301, r.Constants.T_ELSEIF = 302, r.Constants.T_ELSE = 303, r.Constants.T_ENDIF = 304, r.Constants.T_LNUMBER = 305, r.Constants.T_DNUMBER = 306, r.Constants.T_STRING = 307, r.Constants.T_STRING_VARNAME = 308, r.Constants.T_VARIABLE = 309, r.Constants.T_NUM_STRING = 310, r.Constants.T_INLINE_HTML = 311, r.Constants.T_CHARACTER = 312, r.Constants.T_BAD_CHARACTER = 313, r.Constants.T_ENCAPSED_AND_WHITESPACE = 314, r.Constants.T_CONSTANT_ENCAPSED_STRING = 315, r.Constants.T_ECHO = 316, r.Constants.T_DO = 317, r.Constants.T_WHILE = 318, r.Constants.T_ENDWHILE = 319, r.Constants.T_FOR = 320, r.Constants.T_ENDFOR = 321, r.Constants.T_FOREACH = 322, r.Constants.T_ENDFOREACH = 323, r.Constants.T_DECLARE = 324, r.Constants.T_ENDDECLARE = 325, r.Constants.T_AS = 326, r.Constants.T_SWITCH = 327, r.Constants.T_ENDSWITCH = 328, r.Constants.T_CASE = 329, r.Constants.T_DEFAULT = 330, r.Constants.T_BREAK = 331, r.Constants.T_CONTINUE = 332, r.Constants.T_GOTO = 333, r.Constants.T_FUNCTION = 334, r.Constants.T_CONST = 335, r.Constants.T_RETURN = 336, r.Constants.T_TRY = 337, r.Constants.T_CATCH = 338, r.Constants.T_THROW = 339, r.Constants.T_USE = 340, r.Constants.T_GLOBAL = 341, r.Constants.T_STATIC = 347, r.Constants.T_ABSTRACT = 346, r.Constants.T_FINAL = 345, r.Constants.T_PRIVATE = 344, r.Constants.T_PROTECTED = 343, r.Constants.T_PUBLIC = 342, r.Constants.T_VAR = 348, r.Constants.T_UNSET = 349, r.Constants.T_ISSET = 350, r.Constants.T_EMPTY = 351, r.Constants.T_HALT_COMPILER = 352, r.Constants.T_CLASS = 353, r.Constants.T_TRAIT = 382, r.Constants.T_INTERFACE = 354, r.Constants.T_EXTENDS = 355, r.Constants.T_IMPLEMENTS = 356, r.Constants.T_OBJECT_OPERATOR = 357,r.Constants.T_DOUBLE_ARROW = 358,r.Constants.T_LIST = 359,r.Constants.T_ARRAY = 360,r.Constants.T_CLASS_C = 361,r.Constants.T_TRAIT_C = 381,r.Constants.T_METHOD_C = 362,r.Constants.T_FUNC_C = 363,r.Constants.T_LINE = 364,r.Constants.T_FILE = 365,r.Constants.T_COMMENT = 366,r.Constants.T_DOC_COMMENT = 367,r.Constants.T_OPEN_TAG = 368,r.Constants.T_OPEN_TAG_WITH_ECHO = 369,r.Constants.T_CLOSE_TAG = 370,r.Constants.T_WHITESPACE = 371,r.Constants.T_START_HEREDOC = 372,r.Constants.T_END_HEREDOC = 373,r.Constants.T_DOLLAR_OPEN_CURLY_BRACES = 374,r.Constants.T_CURLY_OPEN = 375,r.Constants.T_PAAMAYIM_NEKUDOTAYIM = 376,r.Constants.T_DOUBLE_COLON = 376,r.Constants.T_NAMESPACE = 377,r.Constants.T_NS_C = 378,r.Constants.T_DIR = 379,r.Constants.T_NS_SEPARATOR = 380,r.Lexer = function (e, t) {
        var n, i = function (e) {
            if (e.match(/\n/) !== null) {
                var t = e.substring(0, 1);
                e = "[" + e.split(/\n/).join(t + "," + t) + '].join("\\n")'
            }
            return e
        }, s, o = t === undefined || /^(on|true|1)$/i.test(t.short_open_tag) ? /(\<\?php\s|\<\?|\<\%|\<script language\=('|")?php('|")?\>)/i : /(\<\?php\s|<\?=|\<script language\=('|")?php('|")?\>)/i, u = t === undefined || /^(on|true|1)$/i.test(t.short_open_tag) ? /^(\<\?php\s|\<\?|\<\%|\<script language\=('|")?php('|")?\>)/i : /^(\<\?php\s|<\?=|\<script language\=('|")?php('|")?\>)/i, a = [
            {value: r.Constants.T_NAMESPACE, re: /^namespace(?=\s)/i},
            {value: r.Constants.T_USE, re: /^use(?=\s)/i},
            {value: r.Constants.T_ABSTRACT, re: /^abstract(?=\s)/i},
            {value: r.Constants.T_IMPLEMENTS, re: /^implements(?=\s)/i},
            {value: r.Constants.T_INTERFACE, re: /^interface(?=\s)/i},
            {value: r.Constants.T_CONST, re: /^const(?=\s)/i},
            {value: r.Constants.T_STATIC, re: /^static(?=\s)/i},
            {value: r.Constants.T_FINAL, re: /^final(?=\s)/i},
            {value: r.Constants.T_VAR, re: /^var(?=\s)/i},
            {value: r.Constants.T_GLOBAL, re: /^global(?=\s)/i},
            {value: r.Constants.T_CLONE, re: /^clone(?=\s)/i},
            {value: r.Constants.T_THROW, re: /^throw(?=\s)/i},
            {value: r.Constants.T_EXTENDS, re: /^extends(?=\s)/i},
            {value: r.Constants.T_AND_EQUAL, re: /^&=/},
            {value: r.Constants.T_AS, re: /^as(?=\s)/i},
            {value: r.Constants.T_ARRAY_CAST, re: /^\(array\)/i},
            {value: r.Constants.T_BOOL_CAST, re: /^\((bool|boolean)\)/i},
            {value: r.Constants.T_DOUBLE_CAST, re: /^\((real|float|double)\)/i},
            {value: r.Constants.T_INT_CAST, re: /^\((int|integer)\)/i},
            {value: r.Constants.T_OBJECT_CAST, re: /^\(object\)/i},
            {value: r.Constants.T_STRING_CAST, re: /^\(string\)/i},
            {value: r.Constants.T_UNSET_CAST, re: /^\(unset\)/i},
            {value: r.Constants.T_TRY, re: /^try(?=\s*{)/i},
            {value: r.Constants.T_CATCH, re: /^catch(?=\s*\()/i},
            {value: r.Constants.T_INSTANCEOF, re: /^instanceof(?=\s)/i},
            {value: r.Constants.T_LOGICAL_OR, re: /^or(?=\s)/i},
            {value: r.Constants.T_LOGICAL_AND, re: /^and(?=\s)/i},
            {value: r.Constants.T_LOGICAL_XOR, re: /^xor(?=\s)/i},
            {value: r.Constants.T_BOOLEAN_AND, re: /^&&/},
            {value: r.Constants.T_BOOLEAN_OR, re: /^\|\|/},
            {value: r.Constants.T_CONTINUE, re: /^continue(?=\s|;)/i},
            {value: r.Constants.T_BREAK, re: /^break(?=\s|;)/i},
            {value: r.Constants.T_ENDDECLARE, re: /^enddeclare(?=\s|;)/i},
            {value: r.Constants.T_ENDFOR, re: /^endfor(?=\s|;)/i},
            {value: r.Constants.T_ENDFOREACH, re: /^endforeach(?=\s|;)/i},
            {value: r.Constants.T_ENDIF, re: /^endif(?=\s|;)/i},
            {value: r.Constants.T_ENDSWITCH, re: /^endswitch(?=\s|;)/i},
            {value: r.Constants.T_ENDWHILE, re: /^endwhile(?=\s|;)/i},
            {value: r.Constants.T_CASE, re: /^case(?=\s)/i},
            {value: r.Constants.T_DEFAULT, re: /^default(?=\s|:)/i},
            {value: r.Constants.T_SWITCH, re: /^switch(?=[ (])/i},
            {value: r.Constants.T_EXIT, re: /^(exit|die)(?=[ \(;])/i},
            {value: r.Constants.T_CLOSE_TAG, re: /^(\?\>|\%\>|\<\/script\>)\s?\s?/i, func: function (e) {
                return c = !1, e
            }},
            {value: r.Constants.T_DOUBLE_ARROW, re: /^\=\>/},
            {value: r.Constants.T_DOUBLE_COLON, re: /^\:\:/},
            {value: r.Constants.T_METHOD_C, re: /^__METHOD__/},
            {value: r.Constants.T_LINE, re: /^__LINE__/},
            {value: r.Constants.T_FILE, re: /^__FILE__/},
            {value: r.Constants.T_FUNC_C, re: /^__FUNCTION__/},
            {value: r.Constants.T_NS_C, re: /^__NAMESPACE__/},
            {value: r.Constants.T_TRAIT_C, re: /^__TRAIT__/},
            {value: r.Constants.T_DIR, re: /^__DIR__/},
            {value: r.Constants.T_CLASS_C, re: /^__CLASS__/},
            {value: r.Constants.T_INC, re: /^\+\+/},
            {value: r.Constants.T_DEC, re: /^\-\-/},
            {value: r.Constants.T_CONCAT_EQUAL, re: /^\.\=/},
            {value: r.Constants.T_DIV_EQUAL, re: /^\/\=/},
            {value: r.Constants.T_XOR_EQUAL, re: /^\^\=/},
            {value: r.Constants.T_MUL_EQUAL, re: /^\*\=/},
            {value: r.Constants.T_MOD_EQUAL, re: /^\%\=/},
            {value: r.Constants.T_SL_EQUAL, re: /^<<=/},
            {value: r.Constants.T_START_HEREDOC, re: /^<<<[A-Z_0-9]+\s/i, func: function (e) {
                return n = e.substring(3, e.length - 1), e
            }},
            {value: r.Constants.T_SL, re: /^<</},
            {value: r.Constants.T_IS_SMALLER_OR_EQUAL, re: /^<=/},
            {value: r.Constants.T_SR_EQUAL, re: /^>>=/},
            {value: r.Constants.T_SR, re: /^>>/},
            {value: r.Constants.T_IS_GREATER_OR_EQUAL, re: /^>=/},
            {value: r.Constants.T_OR_EQUAL, re: /^\|\=/},
            {value: r.Constants.T_PLUS_EQUAL, re: /^\+\=/},
            {value: r.Constants.T_MINUS_EQUAL, re: /^-\=/},
            {value: r.Constants.T_OBJECT_OPERATOR, re: /^\-\>/i},
            {value: r.Constants.T_CLASS, re: /^class(?=[\s\{])/i, afterWhitespace: !0},
            {value: r.Constants.T_TRAIT, re: /^trait(?=[\s]+[A-Za-z])/i},
            {value: r.Constants.T_PUBLIC, re: /^public(?=[\s])/i},
            {value: r.Constants.T_PRIVATE, re: /^private(?=[\s])/i},
            {value: r.Constants.T_PROTECTED, re: /^protected(?=[\s])/i},
            {value: r.Constants.T_ARRAY, re: /^array(?=\s*?\()/i},
            {value: r.Constants.T_EMPTY, re: /^empty(?=[ \(])/i},
            {value: r.Constants.T_ISSET, re: /^isset(?=[ \(])/i},
            {value: r.Constants.T_UNSET, re: /^unset(?=[ \(])/i},
            {value: r.Constants.T_RETURN, re: /^return(?=[ "'(;])/i},
            {value: r.Constants.T_FUNCTION, re: /^function(?=[ "'(;])/i},
            {value: r.Constants.T_ECHO, re: /^echo(?=[ "'(;])/i},
            {value: r.Constants.T_LIST, re: /^list(?=\s*?\()/i},
            {value: r.Constants.T_PRINT, re: /^print(?=[ "'(;])/i},
            {value: r.Constants.T_INCLUDE, re: /^include(?=[ "'(;])/i},
            {value: r.Constants.T_INCLUDE_ONCE, re: /^include_once(?=[ "'(;])/i},
            {value: r.Constants.T_REQUIRE, re: /^require(?=[ "'(;])/i},
            {value: r.Constants.T_REQUIRE_ONCE, re: /^require_once(?=[ "'(;])/i},
            {value: r.Constants.T_NEW, re: /^new(?=[ ])/i},
            {value: r.Constants.T_COMMENT, re: /^\/\*([\S\s]*?)(?:\*\/|$)/},
            {value: r.Constants.T_COMMENT, re: /^\/\/.*(\s)?/},
            {value: r.Constants.T_COMMENT, re: /^\#.*(\s)?/},
            {value: r.Constants.T_ELSEIF, re: /^elseif(?=[\s(])/i},
            {value: r.Constants.T_GOTO, re: /^goto(?=[\s(])/i},
            {value: r.Constants.T_ELSE, re: /^else(?=[\s{:])/i},
            {value: r.Constants.T_IF, re: /^if(?=[\s(])/i},
            {value: r.Constants.T_DO, re: /^do(?=[ {])/i},
            {value: r.Constants.T_WHILE, re: /^while(?=[ (])/i},
            {value: r.Constants.T_FOREACH, re: /^foreach(?=[ (])/i},
            {value: r.Constants.T_ISSET, re: /^isset(?=[ (])/i},
            {value: r.Constants.T_IS_IDENTICAL, re: /^===/},
            {value: r.Constants.T_IS_EQUAL, re: /^==/},
            {value: r.Constants.T_IS_NOT_IDENTICAL, re: /^\!==/},
            {value: r.Constants.T_IS_NOT_EQUAL, re: /^(\!=|\<\>)/},
            {value: r.Constants.T_FOR, re: /^for(?=[ (])/i},
            {value: r.Constants.T_DNUMBER, re: /^[0-9]*\.[0-9]+([eE][-]?[0-9]*)?/},
            {value: r.Constants.T_LNUMBER, re: /^(0x[0-9A-F]+|[0-9]+)/i},
            {value: r.Constants.T_OPEN_TAG_WITH_ECHO, re: /^(\<\?=|\<\%=)/i},
            {value: r.Constants.T_OPEN_TAG, re: u},
            {value: r.Constants.T_VARIABLE, re: /^\$[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*/},
            {value: r.Constants.T_WHITESPACE, re: /^\s+/},
            {value: r.Constants.T_CONSTANT_ENCAPSED_STRING, re: /^("(?:[^"\\]|\\[\s\S])*"|'(?:[^'\\]|\\[\s\S])*')/, func: function (e, t) {
                var n = 0, i, s = 0;
                if (e.substring(0, 1) === "'")return e;
                var o = e.match(/(?:[^\\]|\\.)*[^\\]\$[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*/g);
                if (o !== null) {
                    while (e.length > 0) {
                        i = e.length, o = e.match(/^[\[\]\;\:\?\(\)\!\.\,\>\<\=\+\-\/\*\|\&\@\^\%\"\'\{\}]/), o !== null && (f.push(o[0]), e = e.substring(1), n > 0 && o[0] === "}" && n--, o[0] === "[" && s++, o[0] === "]" && s--), o = e.match(/^\$[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*/);
                        if (o !== null) {
                            f.push([parseInt(r.Constants.T_VARIABLE, 10), o[0], l]), e = e.substring(o[0].length), o = e.match(/^(\-\>)\s*([a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*)\s*(\()/), o !== null && (f.push([parseInt(r.Constants.T_OBJECT_OPERATOR, 10), o[1], l]), f.push([parseInt(r.Constants.T_STRING, 10), o[2], l]), o[3] && f.push(o[3]), e = e.substring(o[0].length));
                            if (e.match(/^\[/g) !== null)continue
                        }
                        var u;
                        n > 0 ? u = /^([^\\\$"{}\]\)]|\\.)+/g : u = /^([^\\\$"{]|\\.|{[^\$]|\$(?=[^a-zA-Z_\x7f-\xff]))+/g;
                        while ((o = e.match(u)) !== null) {
                            if (e.length === 1)throw new Error(o);
                            f.push([parseInt(n > 0 ? r.Constants.T_CONSTANT_ENCAPSED_STRING : r.Constants.T_ENCAPSED_AND_WHITESPACE, 10), o[0].replace(/\n/g, "\\n").replace(/\r/g, ""), l]), l += o[0].split("\n").length - 1, e = e.substring(o[0].length)
                        }
                        e.match(/^{\$/) !== null && (f.push([parseInt(r.Constants.T_CURLY_OPEN, 10), "{", l]), e = e.substring(1), n++);
                        if (i === e.length && (o = e.match(/^(([^\\]|\\.)*?[^\\]\$[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*)/g)) !== null)return
                    }
                    return undefined
                }
                return e = e.replace(/\r/g, ""), e
            }},
            {value: r.Constants.T_NS_SEPARATOR, re: /^\\(?=[a-zA-Z_])/},
            {value: r.Constants.T_STRING, re: /^[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*/},
            {value: -1, re: /^[\[\]\;\:\?\(\)\!\.\,\>\<\=\+\-\/\*\|\&\{\}\@\^\%\"\'\$\~]/}
        ], f = [], l = 1, c = !1, h = !0;
        if (e === null)return f;
        typeof e != "string" && (e = e.toString());
        while (e.length > 0 && h === !0)if (c === !0)if (n !== undefined) {
            var p = new RegExp("([\\S\\s]*?)(\\r\\n|\\n|\\r)(" + n + ")(;|\\r\\n|\\n)", "i"), d = e.match(p);
            d !== null && (f.push([parseInt(r.Constants.T_ENCAPSED_AND_WHITESPACE, 10), d[1].replace(/^\n/g, "").replace(/\\\$/g, "$") + "\n", l]), l += d[1].split("\n").length, f.push([parseInt(r.Constants.T_END_HEREDOC, 10), d[3], l]), e = e.substring(d[1].length + d[2].length + d[3].length), n = undefined);
            if (d === null)throw Error("sup")
        } else h = a.some(function (t) {
            if (t.afterWhitespace === !0) {
                var n = f[f.length - 1];
                if (!Array.isArray(n) || n[0] !== r.Constants.T_WHITESPACE && n[0] !== r.Constants.T_OPEN_TAG && n[0] !== r.Constants.T_COMMENT)return!1
            }
            var i = e.match(t.re);
            if (i !== null) {
                if (t.value !== -1) {
                    var s = i[0];
                    t.func !== undefined && (s = t.func(s, t)), s !== undefined && (f.push([parseInt(t.value, 10), s, l]), l += s.split("\n").length - 1)
                } else f.push(i[0]);
                return e = e.substring(i[0].length), !0
            }
            return!1
        }); else {
            var d = o.exec(e);
            if (d === null)return f.push([parseInt(r.Constants.T_INLINE_HTML, 10), e.replace(/^\n/, ""), l]), f;
            if (d.index > 0) {
                var v = e.substring(0, d.index);
                f.push([parseInt(r.Constants.T_INLINE_HTML, 10), v, l]), l += v.split("\n").length - 1, e = e.substring(d.index)
            }
            c = !0
        }
        return f
    },r.Parser = function (e, t) {
        var n = this.yybase, i = this.yydefault, s = this.yycheck, o = this.yyaction, u = this.yylen, a = this.yygbase, f = this.yygcheck, l = this.yyp, c = this.yygoto, h = this.yylhs, p = this.terminals, d = this.translate, v = this.yygdefault;
        this.pos = -1, this.line = 1, this.tokenMap = this.createTokenMap(), this.dropTokens = {}, this.dropTokens[r.Constants.T_WHITESPACE] = 1, this.dropTokens[r.Constants.T_OPEN_TAG] = 1;
        var m = [];
        e.forEach(function (e, t) {
            typeof e == "object" && e[0] === r.Constants.T_OPEN_TAG_WITH_ECHO ? (m.push([r.Constants.T_OPEN_TAG, e[1], e[2]]), m.push([r.Constants.T_ECHO, e[1], e[2]])) : m.push(e)
        }), this.tokens = m;
        var g = this.TOKEN_NONE;
        this.startAttributes = {startLine: 1}, this.endAttributes = {};
        var y = [this.startAttributes], b = 0, w = [b];
        this.yyastk = [], this.stackPos = 0;
        var E, S;
        for (; ;) {
            if (n[b] === 0)E = i[b]; else {
                g === this.TOKEN_NONE && (S = this.getNextToken(), g = S >= 0 && S < this.TOKEN_MAP_SIZE ? d[S] : this.TOKEN_INVALID, y[this.stackPos] = this.startAttributes);
                if (((E = n[b] + g) >= 0 && E < this.YYLAST && s[E] === g || b < this.YY2TBLSTATE && (E = n[b + this.YYNLSTATES] + g) >= 0 && E < this.YYLAST && s[E] === g) && (E = o[E]) !== this.YYDEFAULT)if (E > 0) {
                    ++this.stackPos, w[this.stackPos] = b = E, this.yyastk[this.stackPos] = this.tokenValue, y[this.stackPos] = this.startAttributes, g = this.TOKEN_NONE;
                    if (E < this.YYNLSTATES)continue;
                    E -= this.YYNLSTATES
                } else E = -E; else E = i[b]
            }
            for (; ;) {
                if (E === 0)return this.yyval;
                if (E === this.YYUNEXPECTED) {
                    if (t !== !0) {
                        var x = [];
                        for (var T = 0; T < this.TOKEN_MAP_SIZE; ++T)if ((E = n[b] + T) >= 0 && E < this.YYLAST && s[E] == T || b < this.YY2TBLSTATE && (E = n[b + this.YYNLSTATES] + T) && E < this.YYLAST && s[E] == T)if (o[E] != this.YYUNEXPECTED) {
                            if (x.length == 4) {
                                x = [];
                                break
                            }
                            x.push(this.terminals[T])
                        }
                        var N = "";
                        throw x.length && (N = ", expecting " + x.join(" or ")), new r.ParseError("syntax error, unexpected " + p[g] + N, this.startAttributes.startLine)
                    }
                    return this.startAttributes.startLine
                }
                for (var C in this.endAttributes)y[this.stackPos - u[E]][C] = this.endAttributes[C];
                try {
                    this["yyn" + E](y[this.stackPos - u[E]])
                } catch (k) {
                    throw k
                }
                this.stackPos -= u[E], E = h[E], (l = a[E] + w[this.stackPos]) >= 0 && l < this.YYGLAST && f[l] === E ? b = c[l] : b = v[E], ++this.stackPos, w[this.stackPos] = b, this.yyastk[this.stackPos] = this.yyval, y[this.stackPos] = this.startAttributes;
                if (b < this.YYNLSTATES)break;
                E = b - this.YYNLSTATES
            }
        }
    },r.ParseError = function (e, t) {
        this.message = e, this.line = t
    },r.Parser.prototype.MODIFIER_PUBLIC = 1,r.Parser.prototype.MODIFIER_PROTECTED = 2,r.Parser.prototype.MODIFIER_PRIVATE = 4,r.Parser.prototype.MODIFIER_STATIC = 8,r.Parser.prototype.MODIFIER_ABSTRACT = 16,r.Parser.prototype.MODIFIER_FINAL = 32,r.Parser.prototype.getNextToken = function () {
        this.startAttributes = {}, this.endAttributes = {};
        var e, t;
        while (this.tokens[++this.pos] !== undefined) {
            e = this.tokens[this.pos];
            if (typeof e == "string")return this.startAttributes.startLine = this.line, this.endAttributes.endLine = this.line, 'b"' === e ? (this.tokenValue = 'b"', '"'.charCodeAt(0)) : (this.tokenValue = e, e.charCodeAt(0));
            this.line += (t = e[1].match(/\n/g)) === null ? 0 : t.length;
            if (r.Constants.T_COMMENT === e[0])Array.isArray(this.startAttributes.comments) || (this.startAttributes.comments = []), this.startAttributes.comments.push({type: "comment", comment: e[1], line: e[2]}); else if (r.Constants.T_DOC_COMMENT === e[0])this.startAttributes.comments.push(new PHPParser_Comment_Doc(e[1], e[2])); else if (this.dropTokens[e[0]] === undefined)return this.tokenValue = e[1], this.startAttributes.startLine = e[2], this.endAttributes.endLine = this.line, this.tokenMap[e[0]]
        }
        return this.startAttributes.startLine = this.line, 0
    },r.Parser.prototype.tokenName = function (e) {
        var t = ["T_INCLUDE", "T_INCLUDE_ONCE", "T_EVAL", "T_REQUIRE", "T_REQUIRE_ONCE", "T_LOGICAL_OR", "T_LOGICAL_XOR", "T_LOGICAL_AND", "T_PRINT", "T_PLUS_EQUAL", "T_MINUS_EQUAL", "T_MUL_EQUAL", "T_DIV_EQUAL", "T_CONCAT_EQUAL", "T_MOD_EQUAL", "T_AND_EQUAL", "T_OR_EQUAL", "T_XOR_EQUAL", "T_SL_EQUAL", "T_SR_EQUAL", "T_BOOLEAN_OR", "T_BOOLEAN_AND", "T_IS_EQUAL", "T_IS_NOT_EQUAL", "T_IS_IDENTICAL", "T_IS_NOT_IDENTICAL", "T_IS_SMALLER_OR_EQUAL", "T_IS_GREATER_OR_EQUAL", "T_SL", "T_SR", "T_INSTANCEOF", "T_INC", "T_DEC", "T_INT_CAST", "T_DOUBLE_CAST", "T_STRING_CAST", "T_ARRAY_CAST", "T_OBJECT_CAST", "T_BOOL_CAST", "T_UNSET_CAST", "T_NEW", "T_CLONE", "T_EXIT", "T_IF", "T_ELSEIF", "T_ELSE", "T_ENDIF", "T_LNUMBER", "T_DNUMBER", "T_STRING", "T_STRING_VARNAME", "T_VARIABLE", "T_NUM_STRING", "T_INLINE_HTML", "T_CHARACTER", "T_BAD_CHARACTER", "T_ENCAPSED_AND_WHITESPACE", "T_CONSTANT_ENCAPSED_STRING", "T_ECHO", "T_DO", "T_WHILE", "T_ENDWHILE", "T_FOR", "T_ENDFOR", "T_FOREACH", "T_ENDFOREACH", "T_DECLARE", "T_ENDDECLARE", "T_AS", "T_SWITCH", "T_ENDSWITCH", "T_CASE", "T_DEFAULT", "T_BREAK", "T_CONTINUE", "T_GOTO", "T_FUNCTION", "T_CONST", "T_RETURN", "T_TRY", "T_CATCH", "T_THROW", "T_USE", "T_INSTEADOF", "T_GLOBAL", "T_STATIC", "T_ABSTRACT", "T_FINAL", "T_PRIVATE", "T_PROTECTED", "T_PUBLIC", "T_VAR", "T_UNSET", "T_ISSET", "T_EMPTY", "T_HALT_COMPILER", "T_CLASS", "T_TRAIT", "T_INTERFACE", "T_EXTENDS", "T_IMPLEMENTS", "T_OBJECT_OPERATOR", "T_DOUBLE_ARROW", "T_LIST", "T_ARRAY", "T_CALLABLE", "T_CLASS_C", "T_TRAIT_C", "T_METHOD_C", "T_FUNC_C", "T_LINE", "T_FILE", "T_COMMENT", "T_DOC_COMMENT", "T_OPEN_TAG", "T_OPEN_TAG_WITH_ECHO", "T_CLOSE_TAG", "T_WHITESPACE", "T_START_HEREDOC", "T_END_HEREDOC", "T_DOLLAR_OPEN_CURLY_BRACES", "T_CURLY_OPEN", "T_PAAMAYIM_NEKUDOTAYIM", "T_DOUBLE_COLON", "T_NAMESPACE", "T_NS_C", "T_DIR", "T_NS_SEPARATOR"], n = "UNKNOWN";
        return t.some(function (t) {
            return r.Constants[t] === e ? (n = t, !0) : !1
        }), n
    },r.Parser.prototype.createTokenMap = function () {
        var e = {}, t, n, i = r.Constants.T_PAAMAYIM_NEKUDOTAYIM;
        for (n = 256; n < 1e3; ++n)i === n ? e[n] = this.T_PAAMAYIM_NEKUDOTAYIM : r.Constants.T_OPEN_TAG_WITH_ECHO === n ? e[n] = r.Constants.T_ECHO : r.Constants.T_CLOSE_TAG === n ? e[n] = 59 : "UNKNOWN" !== (t = this.tokenName(n)) && (e[n] = this[t]);
        return e
    };
    var i = function () {
        this.yyval = this.yyastk[this.stackPos - 0]
    };
    r.Parser.prototype.MakeArray = function (e) {
        return Array.isArray(e) ? e : [e]
    }, r.Parser.prototype.parseString = function (e) {
        var t = 0;
        return"b" === e[0] && (t = 1), "'" === e[t] ? e = e.replace(["\\\\", "\\'"], ["\\", "'"]) : e = this.parseEscapeSequences(e, '"'), e
    }, r.Parser.prototype.parseEscapeSequences = function (e, t) {
        undefined !== t && (e = e.replace(new RegExp("\\" + t, "g"), t));
        var n = {"\\": "\\", $: "$", n: "\n", r: "\r", t: "	", f: "\f", v: "", e: ""};
        return e.replace(/~\\\\([\\\\$nrtfve]|[xX][0-9a-fA-F]{1,2}|[0-7]{1,3})~/g, function (e) {
            var t = e[1];
            return n[t] !== undefined ? n[t] : "x" === t[0] || "X" === t[0] ? chr(hexdec(t)) : chr(octdec(t))
        })
    }, r.Parser.prototype.TOKEN_NONE = -1, r.Parser.prototype.TOKEN_INVALID = 149, r.Parser.prototype.TOKEN_MAP_SIZE = 384, r.Parser.prototype.YYLAST = 913, r.Parser.prototype.YY2TBLSTATE = 328, r.Parser.prototype.YYGLAST = 415, r.Parser.prototype.YYNLSTATES = 544, r.Parser.prototype.YYUNEXPECTED = 32767, r.Parser.prototype.YYDEFAULT = -32766, r.Parser.prototype.YYERRTOK = 256, r.Parser.prototype.T_INCLUDE = 257, r.Parser.prototype.T_INCLUDE_ONCE = 258, r.Parser.prototype.T_EVAL = 259, r.Parser.prototype.T_REQUIRE = 260, r.Parser.prototype.T_REQUIRE_ONCE = 261, r.Parser.prototype.T_LOGICAL_OR = 262, r.Parser.prototype.T_LOGICAL_XOR = 263, r.Parser.prototype.T_LOGICAL_AND = 264, r.Parser.prototype.T_PRINT = 265, r.Parser.prototype.T_PLUS_EQUAL = 266, r.Parser.prototype.T_MINUS_EQUAL = 267, r.Parser.prototype.T_MUL_EQUAL = 268, r.Parser.prototype.T_DIV_EQUAL = 269, r.Parser.prototype.T_CONCAT_EQUAL = 270, r.Parser.prototype.T_MOD_EQUAL = 271, r.Parser.prototype.T_AND_EQUAL = 272, r.Parser.prototype.T_OR_EQUAL = 273, r.Parser.prototype.T_XOR_EQUAL = 274, r.Parser.prototype.T_SL_EQUAL = 275, r.Parser.prototype.T_SR_EQUAL = 276, r.Parser.prototype.T_BOOLEAN_OR = 277, r.Parser.prototype.T_BOOLEAN_AND = 278, r.Parser.prototype.T_IS_EQUAL = 279, r.Parser.prototype.T_IS_NOT_EQUAL = 280, r.Parser.prototype.T_IS_IDENTICAL = 281, r.Parser.prototype.T_IS_NOT_IDENTICAL = 282, r.Parser.prototype.T_IS_SMALLER_OR_EQUAL = 283, r.Parser.prototype.T_IS_GREATER_OR_EQUAL = 284, r.Parser.prototype.T_SL = 285, r.Parser.prototype.T_SR = 286, r.Parser.prototype.T_INSTANCEOF = 287, r.Parser.prototype.T_INC = 288, r.Parser.prototype.T_DEC = 289, r.Parser.prototype.T_INT_CAST = 290, r.Parser.prototype.T_DOUBLE_CAST = 291, r.Parser.prototype.T_STRING_CAST = 292, r.Parser.prototype.T_ARRAY_CAST = 293, r.Parser.prototype.T_OBJECT_CAST = 294, r.Parser.prototype.T_BOOL_CAST = 295, r.Parser.prototype.T_UNSET_CAST = 296, r.Parser.prototype.T_NEW = 297, r.Parser.prototype.T_CLONE = 298, r.Parser.prototype.T_EXIT = 299, r.Parser.prototype.T_IF = 300, r.Parser.prototype.T_ELSEIF = 301, r.Parser.prototype.T_ELSE = 302, r.Parser.prototype.T_ENDIF = 303, r.Parser.prototype.T_LNUMBER = 304, r.Parser.prototype.T_DNUMBER = 305, r.Parser.prototype.T_STRING = 306, r.Parser.prototype.T_STRING_VARNAME = 307, r.Parser.prototype.T_VARIABLE = 308, r.Parser.prototype.T_NUM_STRING = 309, r.Parser.prototype.T_INLINE_HTML = 310, r.Parser.prototype.T_CHARACTER = 311, r.Parser.prototype.T_BAD_CHARACTER = 312, r.Parser.prototype.T_ENCAPSED_AND_WHITESPACE = 313, r.Parser.prototype.T_CONSTANT_ENCAPSED_STRING = 314, r.Parser.prototype.T_ECHO = 315, r.Parser.prototype.T_DO = 316, r.Parser.prototype.T_WHILE = 317, r.Parser.prototype.T_ENDWHILE = 318, r.Parser.prototype.T_FOR = 319, r.Parser.prototype.T_ENDFOR = 320, r.Parser.prototype.T_FOREACH = 321, r.Parser.prototype.T_ENDFOREACH = 322, r.Parser.prototype.T_DECLARE = 323, r.Parser.prototype.T_ENDDECLARE = 324, r.Parser.prototype.T_AS = 325, r.Parser.prototype.T_SWITCH = 326, r.Parser.prototype.T_ENDSWITCH = 327, r.Parser.prototype.T_CASE = 328, r.Parser.prototype.T_DEFAULT = 329, r.Parser.prototype.T_BREAK = 330, r.Parser.prototype.T_CONTINUE = 331, r.Parser.prototype.T_GOTO = 332, r.Parser.prototype.T_FUNCTION = 333, r.Parser.prototype.T_CONST = 334, r.Parser.prototype.T_RETURN = 335, r.Parser.prototype.T_TRY = 336, r.Parser.prototype.T_CATCH = 337, r.Parser.prototype.T_THROW = 338, r.Parser.prototype.T_USE = 339, r.Parser.prototype.T_INSTEADOF = 340, r.Parser.prototype.T_GLOBAL = 341, r.Parser.prototype.T_STATIC = 342, r.Parser.prototype.T_ABSTRACT = 343, r.Parser.prototype.T_FINAL = 344,r.Parser.prototype.T_PRIVATE = 345,r.Parser.prototype.T_PROTECTED = 346,r.Parser.prototype.T_PUBLIC = 347,r.Parser.prototype.T_VAR = 348,r.Parser.prototype.T_UNSET = 349,r.Parser.prototype.T_ISSET = 350,r.Parser.prototype.T_EMPTY = 351,r.Parser.prototype.T_HALT_COMPILER = 352,r.Parser.prototype.T_CLASS = 353,r.Parser.prototype.T_TRAIT = 354,r.Parser.prototype.T_INTERFACE = 355,r.Parser.prototype.T_EXTENDS = 356,r.Parser.prototype.T_IMPLEMENTS = 357,r.Parser.prototype.T_OBJECT_OPERATOR = 358,r.Parser.prototype.T_DOUBLE_ARROW = 359,r.Parser.prototype.T_LIST = 360,r.Parser.prototype.T_ARRAY = 361,r.Parser.prototype.T_CALLABLE = 362,r.Parser.prototype.T_CLASS_C = 363,r.Parser.prototype.T_TRAIT_C = 364,r.Parser.prototype.T_METHOD_C = 365,r.Parser.prototype.T_FUNC_C = 366,r.Parser.prototype.T_LINE = 367,r.Parser.prototype.T_FILE = 368,r.Parser.prototype.T_COMMENT = 369,r.Parser.prototype.T_DOC_COMMENT = 370,r.Parser.prototype.T_OPEN_TAG = 371,r.Parser.prototype.T_OPEN_TAG_WITH_ECHO = 372,r.Parser.prototype.T_CLOSE_TAG = 373,r.Parser.prototype.T_WHITESPACE = 374,r.Parser.prototype.T_START_HEREDOC = 375,r.Parser.prototype.T_END_HEREDOC = 376,r.Parser.prototype.T_DOLLAR_OPEN_CURLY_BRACES = 377,r.Parser.prototype.T_CURLY_OPEN = 378,r.Parser.prototype.T_PAAMAYIM_NEKUDOTAYIM = 379,r.Parser.prototype.T_NAMESPACE = 380,r.Parser.prototype.T_NS_C = 381,r.Parser.prototype.T_DIR = 382,r.Parser.prototype.T_NS_SEPARATOR = 383,r.Parser.prototype.terminals = ["$EOF", "error", "T_INCLUDE", "T_INCLUDE_ONCE", "T_EVAL", "T_REQUIRE", "T_REQUIRE_ONCE", "','", "T_LOGICAL_OR", "T_LOGICAL_XOR", "T_LOGICAL_AND", "T_PRINT", "'='", "T_PLUS_EQUAL", "T_MINUS_EQUAL", "T_MUL_EQUAL", "T_DIV_EQUAL", "T_CONCAT_EQUAL", "T_MOD_EQUAL", "T_AND_EQUAL", "T_OR_EQUAL", "T_XOR_EQUAL", "T_SL_EQUAL", "T_SR_EQUAL", "'?'", "':'", "T_BOOLEAN_OR", "T_BOOLEAN_AND", "'|'", "'^'", "'&'", "T_IS_EQUAL", "T_IS_NOT_EQUAL", "T_IS_IDENTICAL", "T_IS_NOT_IDENTICAL", "'<'", "T_IS_SMALLER_OR_EQUAL", "'>'", "T_IS_GREATER_OR_EQUAL", "T_SL", "T_SR", "'+'", "'-'", "'.'", "'*'", "'/'", "'%'", "'!'", "T_INSTANCEOF", "'~'", "T_INC", "T_DEC", "T_INT_CAST", "T_DOUBLE_CAST", "T_STRING_CAST", "T_ARRAY_CAST", "T_OBJECT_CAST", "T_BOOL_CAST", "T_UNSET_CAST", "'@'", "'['", "T_NEW", "T_CLONE", "T_EXIT", "T_IF", "T_ELSEIF", "T_ELSE", "T_ENDIF", "T_LNUMBER", "T_DNUMBER", "T_STRING", "T_STRING_VARNAME", "T_VARIABLE", "T_NUM_STRING", "T_INLINE_HTML", "T_ENCAPSED_AND_WHITESPACE", "T_CONSTANT_ENCAPSED_STRING", "T_ECHO", "T_DO", "T_WHILE", "T_ENDWHILE", "T_FOR", "T_ENDFOR", "T_FOREACH", "T_ENDFOREACH", "T_DECLARE", "T_ENDDECLARE", "T_AS", "T_SWITCH", "T_ENDSWITCH", "T_CASE", "T_DEFAULT", "T_BREAK", "T_CONTINUE", "T_GOTO", "T_FUNCTION", "T_CONST", "T_RETURN", "T_TRY", "T_CATCH", "T_THROW", "T_USE", "T_INSTEADOF", "T_GLOBAL", "T_STATIC", "T_ABSTRACT", "T_FINAL", "T_PRIVATE", "T_PROTECTED", "T_PUBLIC", "T_VAR", "T_UNSET", "T_ISSET", "T_EMPTY", "T_HALT_COMPILER", "T_CLASS", "T_TRAIT", "T_INTERFACE", "T_EXTENDS", "T_IMPLEMENTS", "T_OBJECT_OPERATOR", "T_DOUBLE_ARROW", "T_LIST", "T_ARRAY", "T_CALLABLE", "T_CLASS_C", "T_TRAIT_C", "T_METHOD_C", "T_FUNC_C", "T_LINE", "T_FILE", "T_START_HEREDOC", "T_END_HEREDOC", "T_DOLLAR_OPEN_CURLY_BRACES", "T_CURLY_OPEN", "T_PAAMAYIM_NEKUDOTAYIM", "T_NAMESPACE", "T_NS_C", "T_DIR", "T_NS_SEPARATOR", "';'", "'{'", "'}'", "'('", "')'", "'$'", "']'", "'`'", "'\"'", "???"],r.Parser.prototype.translate = [0, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 47, 148, 149, 145, 46, 30, 149, 143, 144, 44, 41, 7, 42, 43, 45, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 25, 140, 35, 12, 37, 24, 59, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 60, 149, 146, 29, 149, 147, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 141, 28, 142, 49, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 149, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 26, 27, 31, 32, 33, 34, 36, 38, 39, 40, 48, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 149, 149, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 149, 149, 149, 149, 149, 149, 131, 132, 133, 134, 135, 136, 137, 138, 139],r.Parser.prototype.yyaction = [61, 62, 363, 63, 64, -32766, -32766, -32766, 509, 65, 708, 709, 710, 707, 706, 705, -32766, -32766, -32766, -32766, -32766, -32766, 132, -32766, -32766, -32766, -32766, -32766, -32767, -32767, -32767, -32767, -32766, 335, -32766, -32766, -32766, -32766, -32766, 66, 67, 351, 663, 664, 40, 68, 548, 69, 232, 233, 70, 71, 72, 73, 74, 75, 76, 77, 30, 246, 78, 336, 364, -112, 0, 469, 833, 834, 365, 641, 890, 436, 590, 41, 835, 53, 27, 366, 294, 367, 687, 368, 921, 369, 923, 922, 370, -32766, -32766, -32766, 42, 43, 371, 339, 126, 44, 372, 337, 79, 297, 349, 292, 293, -32766, 918, -32766, -32766, 373, 374, 375, 376, 377, 391, 199, 361, 338, 573, 613, 378, 379, 380, 381, 845, 839, 840, 841, 842, 836, 837, 253, -32766, 87, 88, 89, 391, 843, 838, 338, 597, 519, 128, 80, 129, 273, 332, 257, 261, 47, 673, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 799, 247, 884, 108, 109, 110, 226, 247, 21, -32766, 310, -32766, -32766, -32766, 642, 548, -32766, -32766, -32766, -32766, 56, 353, -32766, -32766, -32766, 55, -32766, -32766, -32766, -32766, -32766, 58, -32766, -32766, -32766, -32766, -32766, -32766, -32766, -32766, -32766, 557, -32766, -32766, 518, -32766, 548, 890, -32766, 390, -32766, 228, 252, -32766, -32766, -32766, -32766, -32766, 275, -32766, 234, -32766, 587, 588, -32766, -32766, -32766, -32766, -32766, -32766, -32766, 46, 236, -32766, -32766, 281, -32766, 682, 348, -32766, 390, -32766, 346, 333, 521, -32766, -32766, -32766, 271, 911, 262, 237, 446, 911, -32766, 894, 59, 700, 358, 135, 548, 123, 538, 35, -32766, 333, 122, -32766, -32766, -32766, 271, -32766, 124, -32766, 692, -32766, -32766, -32766, -32766, 700, 273, 22, -32766, -32766, -32766, -32766, 239, -32766, -32766, 612, -32766, 548, 134, -32766, 390, -32766, 462, 354, -32766, -32766, -32766, -32766, -32766, 227, -32766, 238, -32766, 845, 542, -32766, 856, 611, 200, -32766, -32766, -32766, 259, 280, -32766, -32766, 201, -32766, 855, 129, -32766, 390, 130, 202, 333, 206, -32766, -32766, -32766, 271, -32766, -32766, -32766, 125, 601, -32766, 136, 299, 700, 489, 28, 548, 105, 106, 107, -32766, 498, 499, -32766, -32766, -32766, 207, -32766, 133, -32766, 525, -32766, -32766, -32766, -32766, 663, 664, 527, -32766, -32766, -32766, -32766, 528, -32766, -32766, 610, -32766, 548, 427, -32766, 390, -32766, 532, 539, -32766, -32766, -32766, -32766, -32766, 240, -32766, 247, -32766, 697, 543, -32766, 554, 523, 608, -32766, -32766, -32766, 686, 535, -32766, -32766, 54, -32766, 57, 60, -32766, 390, 246, -155, 278, 345, -32766, -32766, -32766, 506, 347, -152, 471, 402, 403, -32766, 405, 404, 272, 493, 416, 548, 318, 417, 505, -32766, 517, 548, -32766, -32766, -32766, 549, -32766, 562, -32766, 916, -32766, -32766, -32766, -32766, 564, 826, 848, -32766, -32766, -32766, -32766, 694, -32766, -32766, 485, -32766, 548, 487, -32766, 390, -32766, 504, 802, -32766, -32766, -32766, -32766, -32766, 279, -32766, 911, -32766, 502, 492, -32766, 413, 483, 269, -32766, -32766, -32766, 243, 337, -32766, -32766, 418, -32766, 454, 229, -32766, 390, 274, 373, 374, 344, -32766, -32766, -32766, 360, 614, -32766, 573, 613, 378, 379, -274, 548, 615, -332, 844, -32766, 258, 51, -32766, -32766, -32766, 270, -32766, 346, -32766, 52, -32766, 260, 0, -32766, -333, -32766, -32766, -32766, -32766, -32766, -32766, 205, -32766, -32766, 49, -32766, 548, 424, -32766, 390, -32766, -266, 264, -32766, -32766, -32766, -32766, -32766, 409, -32766, 343, -32766, 265, 312, -32766, 470, 513, -275, -32766, -32766, -32766, 920, 337, -32766, -32766, 530, -32766, 531, 600, -32766, 390, 592, 373, 374, 578, 581, -32766, -32766, 644, 629, -32766, 573, 613, 378, 379, 635, 548, 636, 576, 627, -32766, 625, 693, -32766, -32766, -32766, 691, -32766, 591, -32766, 582, -32766, 203, 204, -32766, 584, 583, -32766, -32766, -32766, -32766, 586, 599, -32766, -32766, 589, -32766, 690, 558, -32766, 390, 197, 683, 919, 86, 520, 522, -32766, 524, 833, 834, 529, 533, -32766, 534, 537, 541, 835, 48, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 127, 31, 633, 337, 330, 634, 585, -32766, 32, 291, 337, 330, 478, 373, 374, 917, 291, 891, 889, 875, 373, 374, 553, 613, 378, 379, 737, 739, 887, 553, 613, 378, 379, 824, 451, 675, 839, 840, 841, 842, 836, 837, 320, 895, 277, 885, 23, 33, 843, 838, 556, 277, 337, 330, -32766, 34, -32766, 555, 291, 36, 37, 38, 373, 374, 39, 45, 50, 81, 82, 83, 84, 553, 613, 378, 379, -32767, -32767, -32767, -32767, 103, 104, 105, 106, 107, 337, 85, 131, 137, 337, 138, 198, 224, 225, 277, 373, 374, -332, 230, 373, 374, 24, 337, 231, 573, 613, 378, 379, 573, 613, 378, 379, 373, 374, 235, 248, 249, 250, 337, 251, 0, 573, 613, 378, 379, 276, 329, 331, 373, 374, -32766, 337, 574, 490, 792, 337, 609, 573, 613, 378, 379, 373, 374, 25, 300, 373, 374, 319, 337, 795, 573, 613, 378, 379, 573, 613, 378, 379, 373, 374, 516, 355, 359, 445, 482, 796, 507, 573, 613, 378, 379, 508, 548, 337, 890, 775, 791, 337, 604, 803, 808, 806, 698, 373, 374, 888, 807, 373, 374, -32766, -32766, -32766, 573, 613, 378, 379, 573, 613, 378, 379, 873, 832, 804, 872, 851, -32766, 809, -32766, -32766, -32766, -32766, 805, 20, 26, 29, 298, 480, 515, 770, 778, 827, 457, 0, 900, 455, 774, 0, 0, 0, 874, 870, 886, 823, 915, 852, 869, 488, 0, 391, 793, 0, 338, 0, 0, 0, 340, 0, 273],r.Parser.prototype.yycheck = [2, 3, 4, 5, 6, 8, 9, 10, 70, 11, 104, 105, 106, 107, 108, 109, 8, 9, 10, 8, 9, 24, 60, 26, 27, 28, 29, 30, 31, 32, 33, 34, 24, 7, 26, 27, 28, 29, 30, 41, 42, 7, 123, 124, 7, 47, 70, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 144, 0, 75, 68, 69, 70, 25, 72, 70, 74, 7, 76, 77, 78, 79, 7, 81, 142, 83, 70, 85, 72, 73, 88, 8, 9, 10, 92, 93, 94, 95, 7, 97, 98, 95, 100, 7, 7, 103, 104, 24, 142, 26, 27, 105, 106, 111, 112, 113, 136, 7, 7, 139, 114, 115, 116, 117, 122, 123, 132, 125, 126, 127, 128, 129, 130, 131, 8, 8, 9, 10, 136, 137, 138, 139, 140, 141, 25, 143, 141, 145, 142, 147, 148, 24, 72, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 144, 48, 72, 44, 45, 46, 30, 48, 144, 64, 72, 8, 9, 10, 140, 70, 8, 9, 10, 74, 60, 25, 77, 78, 79, 60, 81, 24, 83, 26, 85, 60, 24, 88, 26, 27, 28, 92, 93, 94, 64, 140, 97, 98, 70, 100, 70, 72, 103, 104, 74, 145, 7, 77, 78, 79, 111, 81, 7, 83, 30, 85, 140, 140, 88, 8, 9, 10, 92, 93, 94, 133, 134, 97, 98, 145, 100, 140, 7, 103, 104, 24, 139, 96, 141, 140, 141, 111, 101, 75, 75, 30, 70, 75, 64, 70, 60, 110, 121, 12, 70, 141, 25, 143, 74, 96, 141, 77, 78, 79, 101, 81, 141, 83, 140, 85, 140, 141, 88, 110, 145, 144, 92, 93, 94, 64, 7, 97, 98, 142, 100, 70, 141, 103, 104, 74, 145, 141, 77, 78, 79, 111, 81, 7, 83, 30, 85, 132, 25, 88, 132, 142, 12, 92, 93, 94, 120, 60, 97, 98, 12, 100, 148, 141, 103, 104, 141, 12, 96, 12, 140, 141, 111, 101, 8, 9, 10, 141, 25, 64, 90, 91, 110, 65, 66, 70, 41, 42, 43, 74, 65, 66, 77, 78, 79, 12, 81, 25, 83, 25, 85, 140, 141, 88, 123, 124, 25, 92, 93, 94, 64, 25, 97, 98, 142, 100, 70, 120, 103, 104, 74, 25, 25, 77, 78, 79, 111, 81, 30, 83, 48, 85, 140, 141, 88, 140, 141, 30, 92, 93, 94, 140, 141, 97, 98, 60, 100, 60, 60, 103, 104, 61, 72, 75, 70, 140, 141, 111, 67, 70, 87, 99, 70, 70, 64, 70, 72, 102, 89, 70, 70, 71, 70, 70, 74, 70, 70, 77, 78, 79, 70, 81, 70, 83, 70, 85, 140, 141, 88, 70, 144, 70, 92, 93, 94, 64, 70, 97, 98, 72, 100, 70, 72, 103, 104, 74, 72, 72, 77, 78, 79, 111, 81, 75, 83, 75, 85, 89, 86, 88, 79, 101, 118, 92, 93, 94, 87, 95, 97, 98, 87, 100, 87, 87, 103, 104, 118, 105, 106, 95, 140, 141, 111, 95, 115, 64, 114, 115, 116, 117, 135, 70, 115, 120, 132, 74, 120, 140, 77, 78, 79, 119, 81, 139, 83, 140, 85, 120, -1, 88, 120, 140, 141, 92, 93, 94, 64, 121, 97, 98, 121, 100, 70, 122, 103, 104, 74, 135, 135, 77, 78, 79, 111, 81, 139, 83, 139, 85, 135, 135, 88, 135, 135, 135, 92, 93, 94, 142, 95, 97, 98, 140, 100, 140, 140, 103, 104, 140, 105, 106, 140, 140, 141, 111, 140, 140, 64, 114, 115, 116, 117, 140, 70, 140, 140, 140, 74, 140, 140, 77, 78, 79, 140, 81, 140, 83, 140, 85, 41, 42, 88, 140, 140, 141, 92, 93, 94, 140, 140, 97, 98, 140, 100, 140, 140, 103, 104, 60, 140, 142, 141, 141, 141, 111, 141, 68, 69, 141, 141, 72, 141, 141, 141, 76, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 141, 143, 142, 95, 96, 142, 140, 141, 143, 101, 95, 96, 142, 105, 106, 142, 101, 142, 142, 142, 105, 106, 114, 115, 116, 117, 50, 51, 142, 114, 115, 116, 117, 142, 123, 142, 125, 126, 127, 128, 129, 130, 131, 142, 136, 142, 144, 143, 137, 138, 142, 136, 95, 96, 143, 143, 145, 142, 101, 143, 143, 143, 105, 106, 143, 143, 143, 143, 143, 143, 143, 114, 115, 116, 117, 35, 36, 37, 38, 39, 40, 41, 42, 43, 95, 143, 143, 143, 95, 143, 143, 143, 143, 136, 105, 106, 120, 143, 105, 106, 144, 95, 143, 114, 115, 116, 117, 114, 115, 116, 117, 105, 106, 143, 143, 143, 143, 95, 143, -1, 114, 115, 116, 117, 143, 143, 143, 105, 106, 143, 95, 142, 80, 146, 95, 142, 114, 115, 116, 117, 105, 106, 144, 144, 105, 106, 144, 95, 142, 114, 115, 116, 117, 114, 115, 116, 117, 105, 106, 82, 144, 144, 144, 144, 142, 84, 114, 115, 116, 117, 144, 70, 95, 72, 144, 144, 95, 142, 144, 146, 144, 142, 105, 106, 146, 144, 105, 106, 8, 9, 10, 114, 115, 116, 117, 114, 115, 116, 117, 144, 144, 144, 144, 144, 24, 104, 26, 27, 28, 29, 144, 144, 144, 144, 144, 144, 144, 144, 144, 144, 144, -1, 144, 144, 144, -1, -1, -1, 146, 146, 146, 146, 146, 146, 146, 146, -1, 136, 147, -1, 139, -1, -1, -1, 143, -1, 145],r.Parser.prototype.yybase = [0, 574, 581, 623, 655, 2, 718, 402, 747, 659, 672, 688, 743, 701, 705, 483, 483, 483, 483, 483, 351, 356, 366, 366, 367, 366, 344, -2, -2, -2, 200, 200, 231, 231, 231, 231, 231, 231, 231, 231, 200, 231, 451, 482, 532, 316, 370, 115, 146, 285, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 401, 44, 474, 429, 476, 481, 487, 488, 739, 740, 741, 734, 733, 416, 736, 539, 541, 342, 542, 543, 552, 557, 559, 536, 567, 737, 755, 569, 735, 738, 123, 123, 123, 123, 123, 123, 123, 123, 123, 122, 11, 336, 336, 336, 336, 336, 336, 336, 336, 336, 336, 336, 336, 336, 336, 336, 227, 227, 173, 577, 577, 577, 577, 577, 577, 577, 577, 577, 577, 577, 79, 178, 846, 8, -3, -3, -3, -3, 642, 706, 706, 706, 706, 157, 179, 242, 431, 431, 360, 431, 525, 368, 767, 767, 767, 767, 767, 767, 767, 767, 767, 767, 767, 767, 350, 375, 315, 315, 652, 652, -81, -81, -81, -81, 251, 185, 188, 184, -62, 348, 195, 195, 195, 408, 392, 410, 1, 192, 129, 129, 129, -24, -24, -24, -24, 499, -24, -24, -24, 113, 108, 108, 12, 161, 349, 526, 271, 398, 529, 438, 130, 206, 265, 427, 76, 414, 427, 288, 295, 76, 166, 44, 262, 422, 141, 491, 372, 494, 413, 71, 92, 93, 267, 135, 100, 34, 415, 745, 746, 742, -38, 420, -10, 135, 147, 744, 498, 107, 26, 493, 144, 377, 363, 369, 332, 363, 400, 377, 588, 377, 376, 377, 360, 37, 582, 376, 377, 374, 376, 388, 363, 364, 412, 369, 377, 441, 443, 390, 106, 332, 377, 390, 377, 400, 64, 590, 591, 323, 592, 589, 593, 649, 608, 362, 500, 399, 407, 620, 625, 636, 365, 354, 614, 524, 425, 359, 355, 423, 570, 578, 357, 406, 414, 394, 352, 403, 531, 433, 403, 653, 434, 385, 417, 411, 444, 310, 318, 501, 425, 668, 757, 380, 637, 684, 403, 609, 387, 87, 325, 638, 382, 403, 639, 403, 696, 503, 615, 403, 697, 384, 435, 425, 352, 352, 352, 700, 66, 699, 583, 702, 707, 704, 748, 721, 749, 584, 750, 358, 583, 722, 751, 682, 215, 613, 422, 436, 389, 447, 221, 257, 752, 403, 403, 506, 499, 403, 395, 685, 397, 426, 753, 392, 391, 647, 683, 403, 418, 754, 221, 723, 587, 724, 450, 568, 507, 648, 509, 327, 725, 353, 497, 610, 454, 622, 455, 461, 404, 510, 373, 732, 612, 247, 361, 664, 463, 405, 692, 641, 464, 465, 511, 343, 437, 335, 409, 396, 665, 293, 467, 468, 472, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, 0, 0, 0, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 0, 0, 0, 0, 0, 0, 0, 0, 0, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 123, 767, 767, 767, 767, 767, 767, 767, 767, 767, 767, 767, 123, 123, 123, 123, 123, 123, 123, 123, 0, 129, 129, 129, 129, -94, -94, -94, 767, 767, 767, 767, 767, 767, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -94, -94, 129, 129, 767, 767, -24, -24, -24, -24, -24, 108, 108, 108, -24, 108, 145, 145, 145, 108, 108, 108, 100, 100, 0, 0, 0, 0, 0, 0, 0, 145, 0, 0, 0, 376, 0, 0, 0, 145, 260, 260, 221, 260, 260, 135, 0, 0, 425, 376, 0, 364, 376, 0, 0, 0, 0, 0, 0, 531, 0, 87, 637, 241, 425, 0, 0, 0, 0, 0, 0, 0, 425, 289, 289, 306, 0, 358, 0, 0, 0, 306, 241, 0, 0, 221],r.Parser.prototype.yydefault = [3, 32767, 32767, 1, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 104, 96, 110, 95, 106, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 358, 358, 122, 122, 122, 122, 122, 122, 122, 122, 316, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 173, 173, 173, 32767, 348, 348, 348, 348, 348, 348, 348, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 363, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 232, 233, 235, 236, 172, 125, 349, 362, 171, 199, 201, 250, 200, 177, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 176, 229, 228, 197, 313, 313, 316, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 198, 202, 204, 203, 219, 220, 217, 218, 175, 221, 222, 223, 224, 157, 157, 157, 357, 357, 32767, 357, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 158, 32767, 211, 212, 276, 276, 117, 117, 117, 117, 117, 32767, 32767, 32767, 32767, 284, 32767, 32767, 32767, 32767, 32767, 286, 32767, 32767, 206, 207, 205, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 285, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 334, 321, 272, 32767, 32767, 32767, 265, 32767, 107, 109, 32767, 32767, 32767, 32767, 302, 339, 32767, 32767, 32767, 17, 32767, 32767, 32767, 370, 334, 32767, 32767, 19, 32767, 32767, 32767, 32767, 227, 32767, 338, 332, 32767, 32767, 32767, 32767, 32767, 32767, 63, 32767, 32767, 32767, 32767, 32767, 63, 281, 63, 32767, 63, 32767, 315, 287, 32767, 63, 74, 32767, 72, 32767, 32767, 76, 32767, 63, 93, 93, 254, 315, 54, 63, 254, 63, 32767, 32767, 32767, 32767, 4, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 267, 32767, 323, 32767, 337, 336, 324, 32767, 265, 32767, 215, 194, 266, 32767, 196, 32767, 32767, 270, 273, 32767, 32767, 32767, 134, 32767, 268, 180, 32767, 32767, 32767, 32767, 365, 32767, 32767, 174, 32767, 32767, 32767, 130, 32767, 61, 332, 32767, 32767, 355, 32767, 32767, 332, 269, 208, 209, 210, 32767, 121, 32767, 310, 32767, 32767, 32767, 32767, 32767, 32767, 327, 32767, 333, 32767, 32767, 32767, 32767, 111, 32767, 302, 32767, 32767, 32767, 75, 32767, 32767, 178, 126, 32767, 32767, 364, 32767, 32767, 32767, 320, 32767, 32767, 32767, 32767, 32767, 62, 32767, 32767, 77, 32767, 32767, 32767, 32767, 332, 32767, 32767, 32767, 115, 32767, 169, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 332, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 4, 32767, 151, 32767, 32767, 32767, 32767, 32767, 32767, 32767, 25, 25, 3, 137, 3, 137, 25, 101, 25, 25, 137, 93, 93, 25, 25, 25, 144, 25, 25, 25, 25, 25, 25, 25, 25],r.Parser.prototype.yygoto = [141, 141, 173, 173, 173, 173, 173, 173, 173, 173, 141, 173, 142, 143, 144, 148, 153, 155, 181, 175, 172, 172, 172, 172, 174, 174, 174, 174, 174, 174, 174, 168, 169, 170, 171, 179, 757, 758, 392, 760, 781, 782, 783, 784, 785, 786, 787, 789, 725, 145, 146, 147, 149, 150, 151, 152, 154, 177, 178, 180, 196, 208, 209, 210, 211, 212, 213, 214, 215, 217, 218, 219, 220, 244, 245, 266, 267, 268, 430, 431, 432, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 156, 157, 158, 159, 176, 160, 194, 161, 162, 163, 164, 195, 165, 193, 139, 166, 167, 452, 452, 452, 452, 452, 452, 452, 452, 452, 452, 452, 453, 453, 453, 453, 453, 453, 453, 453, 453, 453, 453, 551, 551, 551, 464, 491, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 394, 407, 552, 552, 552, 810, 810, 662, 662, 662, 662, 662, 594, 283, 595, 510, 399, 399, 567, 679, 632, 849, 850, 863, 660, 714, 426, 222, 622, 622, 622, 622, 223, 617, 623, 494, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 395, 465, 472, 514, 904, 398, 398, 425, 425, 459, 425, 419, 322, 421, 421, 393, 396, 412, 422, 428, 460, 463, 473, 481, 501, 5, 476, 284, 327, 1, 15, 2, 6, 7, 550, 550, 550, 8, 9, 10, 668, 16, 11, 17, 12, 18, 13, 19, 14, 704, 328, 881, 881, 643, 628, 626, 626, 624, 626, 526, 401, 652, 647, 847, 847, 847, 847, 847, 847, 847, 847, 847, 847, 847, 437, 438, 441, 447, 477, 479, 497, 290, 910, 910, 400, 400, 486, 880, 880, 263, 913, 910, 303, 255, 723, 306, 822, 821, 306, 896, 896, 896, 861, 304, 323, 410, 913, 913, 897, 316, 420, 769, 658, 559, 879, 671, 536, 324, 466, 565, 311, 311, 311, 801, 241, 676, 496, 439, 440, 442, 444, 448, 475, 631, 858, 311, 285, 286, 603, 495, 712, 0, 406, 321, 0, 0, 0, 314, 0, 0, 429, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 411],r.Parser.prototype.yygcheck = [15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 6, 6, 6, 21, 21, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 71, 7, 7, 7, 35, 35, 35, 35, 35, 35, 35, 29, 44, 29, 35, 86, 86, 12, 12, 12, 12, 12, 12, 12, 12, 75, 40, 35, 35, 35, 35, 40, 35, 35, 35, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 36, 36, 36, 104, 82, 82, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 13, 42, 42, 42, 2, 13, 2, 13, 13, 5, 5, 5, 13, 13, 13, 54, 13, 13, 13, 13, 13, 13, 13, 13, 67, 67, 83, 83, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 52, 52, 52, 52, 52, 52, 52, 4, 105, 105, 89, 89, 94, 84, 84, 92, 105, 105, 26, 92, 71, 4, 91, 91, 4, 84, 84, 84, 97, 30, 70, 30, 105, 105, 102, 27, 30, 72, 50, 10, 84, 55, 46, 9, 30, 11, 90, 90, 90, 80, 30, 56, 30, 85, 85, 85, 85, 85, 85, 43, 96, 90, 44, 44, 34, 77, 69, -1, 4, 90, -1, -1, -1, 4, -1, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 71],r.Parser.prototype.yygbase = [0, 0, -286, 0, 10, 239, 130, 154, 0, -10, 25, -23, -29, -289, 0, -30, 0, 0, 0, 0, 0, 83, 0, 0, 0, 0, 245, 84, -11, 142, -28, 0, 0, 0, -13, -88, -42, 0, 0, 0, -344, 0, -38, -12, -188, 0, 23, 0, 0, 0, 66, 0, 247, 0, 205, 24, -18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, -15, 85, 74, 70, 0, 0, 148, 0, -14, 0, 0, -6, 0, -35, 11, 47, 278, -77, 0, 0, 44, 68, 43, 38, 72, 94, 0, -16, 109, 0, 0, 0, 0, 87, 0, 170, 34, 0],r.Parser.prototype.yygdefault = [-32768, 362, 3, 546, 382, 570, 571, 572, 307, 305, 560, 566, 467, 4, 568, 140, 295, 575, 296, 500, 577, 414, 579, 580, 308, 309, 415, 315, 216, 593, 503, 313, 596, 357, 602, 301, 449, 383, 350, 461, 221, 423, 456, 630, 282, 638, 540, 646, 649, 450, 657, 352, 433, 434, 667, 672, 677, 680, 334, 325, 474, 684, 685, 256, 689, 511, 512, 703, 242, 711, 317, 724, 342, 788, 790, 397, 408, 484, 797, 326, 800, 384, 385, 386, 387, 435, 818, 815, 289, 866, 287, 443, 254, 853, 468, 356, 903, 862, 288, 388, 389, 302, 898, 341, 905, 912, 458],r.Parser.prototype.yylhs = [0, 1, 2, 2, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 8, 8, 10, 10, 10, 10, 9, 9, 11, 13, 13, 14, 14, 14, 14, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 33, 33, 34, 27, 27, 30, 30, 6, 7, 7, 7, 37, 37, 37, 38, 38, 41, 41, 39, 39, 42, 42, 22, 22, 29, 29, 32, 32, 31, 31, 43, 23, 23, 23, 23, 44, 44, 45, 45, 46, 46, 20, 20, 16, 16, 47, 18, 18, 48, 17, 17, 19, 19, 36, 36, 49, 49, 50, 50, 51, 51, 51, 51, 52, 52, 53, 53, 54, 54, 24, 24, 55, 55, 55, 25, 25, 56, 56, 40, 40, 57, 57, 57, 57, 62, 62, 63, 63, 64, 64, 64, 64, 65, 66, 66, 61, 61, 58, 58, 60, 60, 68, 68, 67, 67, 67, 67, 67, 67, 59, 59, 69, 69, 26, 26, 21, 21, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 71, 77, 77, 79, 79, 80, 81, 81, 81, 81, 81, 81, 86, 86, 35, 35, 35, 72, 72, 87, 87, 82, 82, 88, 88, 88, 88, 88, 73, 73, 73, 76, 76, 76, 78, 78, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 12, 12, 12, 12, 12, 12, 74, 74, 74, 74, 94, 94, 96, 96, 95, 95, 97, 97, 28, 28, 28, 28, 99, 99, 98, 98, 98, 98, 98, 100, 100, 84, 84, 89, 89, 83, 83, 101, 101, 101, 101, 90, 90, 90, 90, 85, 85, 91, 91, 91, 70, 70, 102, 102, 102, 75, 75, 103, 103, 104, 104, 104, 104, 92, 92, 92, 92, 105, 105, 105, 105, 105, 105, 105, 106, 106, 106],r.Parser.prototype.yylen = [1, 1, 2, 0, 1, 3, 1, 1, 1, 1, 3, 5, 4, 3, 3, 3, 1, 1, 3, 2, 4, 3, 1, 3, 2, 0, 1, 1, 1, 1, 3, 7, 10, 5, 7, 9, 5, 2, 3, 2, 3, 2, 3, 3, 3, 3, 1, 2, 5, 7, 8, 10, 5, 1, 5, 3, 3, 2, 1, 2, 8, 1, 3, 0, 1, 9, 7, 6, 5, 1, 2, 2, 0, 2, 0, 2, 0, 2, 1, 3, 1, 4, 1, 4, 1, 4, 1, 3, 3, 3, 4, 4, 5, 0, 2, 4, 3, 1, 1, 1, 4, 0, 2, 5, 0, 2, 6, 0, 2, 0, 3, 1, 0, 1, 3, 3, 5, 0, 1, 1, 1, 1, 0, 1, 3, 1, 2, 3, 1, 1, 2, 4, 3, 1, 1, 3, 2, 0, 3, 3, 8, 3, 1, 3, 0, 2, 4, 5, 4, 4, 3, 1, 1, 1, 3, 1, 1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 3, 1, 0, 1, 1, 6, 3, 4, 4, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 5, 4, 4, 4, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 4, 3, 3, 2, 9, 10, 3, 0, 4, 1, 3, 2, 4, 6, 8, 4, 4, 4, 1, 1, 1, 2, 3, 1, 1, 1, 1, 1, 1, 0, 3, 3, 4, 4, 0, 2, 3, 0, 1, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 2, 1, 1, 3, 2, 2, 4, 3, 1, 3, 3, 3, 0, 2, 0, 1, 3, 1, 3, 1, 1, 1, 1, 1, 6, 4, 3, 6, 4, 4, 4, 1, 3, 1, 2, 1, 1, 4, 1, 3, 6, 4, 4, 4, 4, 1, 4, 0, 1, 1, 3, 1, 3, 1, 1, 4, 0, 0, 2, 3, 1, 3, 1, 4, 2, 2, 2, 1, 2, 1, 4, 3, 3, 3, 6, 3, 1, 1, 1],r.Parser.prototype.yyn0 = function () {
        this.yyval = this.yyastk[this.stackPos]
    },r.Parser.prototype.yyn1 = function (e) {
        this.yyval = this.Stmt_Namespace_postprocess(this.yyastk[this.stackPos - 0])
    },r.Parser.prototype.yyn2 = function (e) {
        Array.isArray(this.yyastk[this.stackPos - 0]) ? this.yyval = this.yyastk[this.stackPos - 1].concat(this.yyastk[this.stackPos - 0]) : (this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1])
    },r.Parser.prototype.yyn3 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn4 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn5 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn6 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn7 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn8 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn9 = function (e) {
        this.yyval = this.Node_Stmt_HaltCompiler(e)
    },r.Parser.prototype.yyn10 = function (e) {
        this.yyval = this.Node_Stmt_Namespace(this.Node_Name(this.yyastk[this.stackPos - 1], e), null, e)
    },r.Parser.prototype.yyn11 = function (e) {
        this.yyval = this.Node_Stmt_Namespace(this.Node_Name(this.yyastk[this.stackPos - 3], e), this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn12 = function (e) {
        this.yyval = this.Node_Stmt_Namespace(null, this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn13 = function (e) {
        this.yyval = this.Node_Stmt_Use(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn14 = function (e) {
        this.yyval = this.Node_Stmt_Const(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn15 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn16 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn17 = function (e) {
        this.yyval = this.Node_Stmt_UseUse(this.Node_Name(this.yyastk[this.stackPos - 0], e), null, e)
    },r.Parser.prototype.yyn18 = function (e) {
        this.yyval = this.Node_Stmt_UseUse(this.Node_Name(this.yyastk[this.stackPos - 2], e), this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn19 = function (e) {
        this.yyval = this.Node_Stmt_UseUse(this.Node_Name(this.yyastk[this.stackPos - 0], e), null, e)
    },r.Parser.prototype.yyn20 = function (e) {
        this.yyval = this.Node_Stmt_UseUse(this.Node_Name(this.yyastk[this.stackPos - 2], e), this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn21 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn22 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn23 = function (e) {
        this.yyval = this.Node_Const(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn24 = function (e) {
        Array.isArray(this.yyastk[this.stackPos - 0]) ? this.yyval = this.yyastk[this.stackPos - 1].concat(this.yyastk[this.stackPos - 0]) : (this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1])
    },r.Parser.prototype.yyn25 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn26 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn27 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn28 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn29 = function (e) {
        throw new Error("__halt_compiler() can only be used from the outermost scope")
    },r.Parser.prototype.yyn30 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn31 = function (e) {
        this.yyval = this.Node_Stmt_If(this.yyastk[this.stackPos - 4], {stmts: Array.isArray(this.yyastk[this.stackPos - 2]) ? this.yyastk[this.stackPos - 2] : [this.yyastk[this.stackPos - 2]], elseifs: this.yyastk[this.stackPos - 1], Else: this.yyastk[this.stackPos - 0]}, e)
    },r.Parser.prototype.yyn32 = function (e) {
        this.yyval = this.Node_Stmt_If(this.yyastk[this.stackPos - 7], {stmts: this.yyastk[this.stackPos - 4], elseifs: this.yyastk[this.stackPos - 3], "else": this.yyastk[this.stackPos - 2]}, e)
    },r.Parser.prototype.yyn33 = function (e) {
        this.yyval = this.Node_Stmt_While(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn34 = function (e) {
        this.yyval = this.Node_Stmt_Do(this.yyastk[this.stackPos - 2], Array.isArray(this.yyastk[this.stackPos - 5]) ? this.yyastk[this.stackPos - 5] : [this.yyastk[this.stackPos - 5]], e)
    },r.Parser.prototype.yyn35 = function (e) {
        this.yyval = this.Node_Stmt_For({init: this.yyastk[this.stackPos - 6], cond: this.yyastk[this.stackPos - 4], loop: this.yyastk[this.stackPos - 2], stmts: this.yyastk[this.stackPos - 0]}, e)
    },r.Parser.prototype.yyn36 = function (e) {
        this.yyval = this.Node_Stmt_Switch(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn37 = function (e) {
        this.yyval = this.Node_Stmt_Break(null, e)
    },r.Parser.prototype.yyn38 = function (e) {
        this.yyval = this.Node_Stmt_Break(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn39 = function (e) {
        this.yyval = this.Node_Stmt_Continue(null, e)
    },r.Parser.prototype.yyn40 = function (e) {
        this.yyval = this.Node_Stmt_Continue(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn41 = function (e) {
        this.yyval = this.Node_Stmt_Return(null, e)
    },r.Parser.prototype.yyn42 = function (e) {
        this.yyval = this.Node_Stmt_Return(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn43 = function (e) {
        this.yyval = this.Node_Stmt_Global(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn44 = function (e) {
        this.yyval = this.Node_Stmt_Static(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn45 = function (e) {
        this.yyval = this.Node_Stmt_Echo(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn46 = function (e) {
        this.yyval = this.Node_Stmt_InlineHTML(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn47 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn48 = function (e) {
        this.yyval = this.Node_Stmt_Unset(this.yyastk[this.stackPos - 2], e)
    },r.Parser.prototype.yyn49 = function (e) {
        this.yyval = this.Node_Stmt_Foreach(this.yyastk[this.stackPos - 4], this.yyastk[this.stackPos - 2], {keyVar: null, byRef: !1, stmts: this.yyastk[this.stackPos - 0]}, e)
    },r.Parser.prototype.yyn50 = function (e) {
        this.yyval = this.Node_Stmt_Foreach(this.yyastk[this.stackPos - 5], this.yyastk[this.stackPos - 2], {keyVar: null, byRef: !0, stmts: this.yyastk[this.stackPos - 0]}, e)
    },r.Parser.prototype.yyn51 = function (e) {
        this.yyval = this.Node_Stmt_Foreach(this.yyastk[this.stackPos - 7], this.yyastk[this.stackPos - 2], {keyVar: this.yyastk[this.stackPos - 5], byRef: this.yyastk[this.stackPos - 3], stmts: this.yyastk[this.stackPos - 0]}, e)
    },r.Parser.prototype.yyn52 = function (e) {
        this.yyval = this.Node_Stmt_Declare(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn53 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn54 = function (e) {
        this.yyval = this.Node_Stmt_TryCatch(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn55 = function (e) {
        this.yyval = this.Node_Stmt_Throw(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn56 = function (e) {
        this.yyval = this.Node_Stmt_Goto(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn57 = function (e) {
        this.yyval = this.Node_Stmt_Label(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn58 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn59 = function (e) {
        this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn60 = function (e) {
        this.yyval = this.Node_Stmt_Catch(this.yyastk[this.stackPos - 5], this.yyastk[this.stackPos - 4].substring(1), this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn61 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn62 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn63 = function (e) {
        this.yyval = !1
    },r.Parser.prototype.yyn64 = function (e) {
        this.yyval = !0
    },r.Parser.prototype.yyn65 = function (e) {
        this.yyval = this.Node_Stmt_Function(this.yyastk[this.stackPos - 6], {byRef: this.yyastk[this.stackPos - 7], params: this.yyastk[this.stackPos - 4], stmts: this.yyastk[this.stackPos - 1]}, e)
    },r.Parser.prototype.yyn66 = function (e) {
        this.yyval = this.Node_Stmt_Class(this.yyastk[this.stackPos - 5], {type: this.yyastk[this.stackPos - 6], Extends: this.yyastk[this.stackPos - 4], Implements: this.yyastk[this.stackPos - 3], stmts: this.yyastk[this.stackPos - 1]}, e)
    },r.Parser.prototype.yyn67 = function (e) {
        this.yyval = this.Node_Stmt_Interface(this.yyastk[this.stackPos - 4], {Extends: this.yyastk[this.stackPos - 3], stmts: this.yyastk[this.stackPos - 1]}, e)
    },r.Parser.prototype.yyn68 = function (e) {
        this.yyval = this.Node_Stmt_Trait(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn69 = function (e) {
        this.yyval = 0
    },r.Parser.prototype.yyn70 = function (e) {
        this.yyval = this.MODIFIER_ABSTRACT
    },r.Parser.prototype.yyn71 = function (e) {
        this.yyval = this.MODIFIER_FINAL
    },r.Parser.prototype.yyn72 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn73 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn74 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn75 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn76 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn77 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn78 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn79 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn80 = function (e) {
        this.yyval = Array.isArray(this.yyastk[this.stackPos - 0]) ? this.yyastk[this.stackPos - 0] : [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn81 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn82 = function (e) {
        this.yyval = Array.isArray(this.yyastk[this.stackPos - 0]) ? this.yyastk[this.stackPos - 0] : [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn83 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn84 = function (e) {
        this.yyval = Array.isArray(this.yyastk[this.stackPos - 0]) ? this.yyastk[this.stackPos - 0] : [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn85 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn86 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn87 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn88 = function (e) {
        this.yyval = this.Node_Stmt_DeclareDeclare(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn89 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn90 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn91 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn92 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn93 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn94 = function (e) {
        this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn95 = function (e) {
        this.yyval = this.Node_Stmt_Case(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn96 = function (e) {
        this.yyval = this.Node_Stmt_Case(null, this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn97 = function () {
        this.yyval = this.yyastk[this.stackPos]
    },r.Parser.prototype.yyn98 = function () {
        this.yyval = this.yyastk[this.stackPos]
    },r.Parser.prototype.yyn99 = function (e) {
        this.yyval = Array.isArray(this.yyastk[this.stackPos - 0]) ? this.yyastk[this.stackPos - 0] : [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn100 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn101 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn102 = function (e) {
        this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn103 = function (e) {
        this.yyval = this.Node_Stmt_ElseIf(this.yyastk[this.stackPos - 2], Array.isArray(this.yyastk[this.stackPos - 0]) ? this.yyastk[this.stackPos - 0] : [this.yyastk[this.stackPos - 0]], e)
    },r.Parser.prototype.yyn104 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn105 = function (e) {
        this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn106 = function (e) {
        this.yyval = this.Node_Stmt_ElseIf(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn107 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn108 = function (e) {
        this.yyval = this.Node_Stmt_Else(Array.isArray(this.yyastk[this.stackPos - 0]) ? this.yyastk[this.stackPos - 0] : [this.yyastk[this.stackPos - 0]], e)
    },r.Parser.prototype.yyn109 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn110 = function (e) {
        this.yyval = this.Node_Stmt_Else(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn111 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn112 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn113 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn114 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn115 = function (e) {
        this.yyval = this.Node_Param(this.yyastk[this.stackPos - 0].substring(1), null, this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn116 = function (e) {
        this.yyval = this.Node_Param(this.yyastk[this.stackPos - 2].substring(1), this.yyastk[this.stackPos - 0], this.yyastk[this.stackPos - 4], this.yyastk[this.stackPos - 3], e)
    },r.Parser.prototype.yyn117 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn118 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn119 = function (e) {
        this.yyval = "array"
    },r.Parser.prototype.yyn120 = function (e) {
        this.yyval = "callable"
    },r.Parser.prototype.yyn121 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn122 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn123 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn124 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn125 = function (e) {
        this.yyval = this.Node_Arg(this.yyastk[this.stackPos - 0], !1, e)
    },r.Parser.prototype.yyn126 = function (e) {
        this.yyval = this.Node_Arg(this.yyastk[this.stackPos - 0], !0, e)
    },r.Parser.prototype.yyn127 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn128 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn129 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 0].substring(1), e)
    },r.Parser.prototype.yyn130 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn131 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn132 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn133 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn134 = function (e) {
        this.yyval = this.Node_Stmt_StaticVar(this.yyastk[this.stackPos - 0].substring(1), null, e)
    },r.Parser.prototype.yyn135 = function (e) {
        this.yyval = this.Node_Stmt_StaticVar(this.yyastk[this.stackPos - 2].substring(1), this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn136 = function (e) {
        this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn137 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn138 = function (e) {
        this.yyval = this.Node_Stmt_Property(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn139 = function (e) {
        this.yyval = this.Node_Stmt_ClassConst(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn140 = function (e) {
        this.yyval = this.Node_Stmt_ClassMethod(this.yyastk[this.stackPos - 4], {type: this.yyastk[this.stackPos - 7], byRef: this.yyastk[this.stackPos - 5], params: this.yyastk[this.stackPos - 2], stmts: this.yyastk[this.stackPos - 0]}, e)
    },r.Parser.prototype.yyn141 = function (e) {
        this.yyval = this.Node_Stmt_TraitUse(this.yyastk[this.stackPos - 1], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn142 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn143 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn144 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn145 = function (e) {
        this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn146 = function (e) {
        this.yyval = this.Node_Stmt_TraitUseAdaptation_Precedence(this.yyastk[this.stackPos - 3][0], this.yyastk[this.stackPos - 3][1], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn147 = function (e) {
        this.yyval = this.Node_Stmt_TraitUseAdaptation_Alias(this.yyastk[this.stackPos - 4][0], this.yyastk[this.stackPos - 4][1], this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn148 = function (e) {
        this.yyval = this.Node_Stmt_TraitUseAdaptation_Alias(this.yyastk[this.stackPos - 3][0], this.yyastk[this.stackPos - 3][1], this.yyastk[this.stackPos - 1], null, e)
    },r.Parser.prototype.yyn149 = function (e) {
        this.yyval = this.Node_Stmt_TraitUseAdaptation_Alias(this.yyastk[this.stackPos - 3][0], this.yyastk[this.stackPos - 3][1], null, this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn150 = function (e) {
        this.yyval = array(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0])
    },r.Parser.prototype.yyn151 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn152 = function (e) {
        this.yyval = array(null, this.yyastk[this.stackPos - 0])
    },r.Parser.prototype.yyn153 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn154 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn155 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn156 = function (e) {
        this.yyval = this.MODIFIER_PUBLIC
    },r.Parser.prototype.yyn157 = function (e) {
        this.yyval = this.MODIFIER_PUBLIC
    },r.Parser.prototype.yyn158 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn159 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn160 = function (e) {
        this.Stmt_Class_verifyModifier(this.yyastk[this.stackPos - 1], this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1] | this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn161 = function (e) {
        this.yyval = this.MODIFIER_PUBLIC
    },r.Parser.prototype.yyn162 = function (e) {
        this.yyval = this.MODIFIER_PROTECTED
    },r.Parser.prototype.yyn163 = function (e) {
        this.yyval = this.MODIFIER_PRIVATE
    },r.Parser.prototype.yyn164 = function (e) {
        this.yyval = this.MODIFIER_STATIC
    },r.Parser.prototype.yyn165 = function (e) {
        this.yyval = this.MODIFIER_ABSTRACT
    },r.Parser.prototype.yyn166 = function (e) {
        this.yyval = this.MODIFIER_FINAL
    },r.Parser.prototype.yyn167 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn168 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn169 = function (e) {
        this.yyval = this.Node_Stmt_PropertyProperty(this.yyastk[this.stackPos - 0].substring(1), null, e)
    },r.Parser.prototype.yyn170 = function (e) {
        this.yyval = this.Node_Stmt_PropertyProperty(this.yyastk[this.stackPos - 2].substring(1), this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn171 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn172 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn173 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn174 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn175 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn176 = function (e) {
        this.yyval = this.Node_Expr_AssignList(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn177 = function (e) {
        this.yyval = this.Node_Expr_Assign(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn178 = function (e) {
        this.yyval = this.Node_Expr_AssignRef(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn179 = function (e) {
        this.yyval = this.Node_Expr_AssignRef(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn180 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn181 = function (e) {
        this.yyval = this.Node_Expr_Clone(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn182 = function (e) {
        this.yyval = this.Node_Expr_AssignPlus(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn183 = function (e) {
        this.yyval = this.Node_Expr_AssignMinus(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn184 = function (e) {
        this.yyval = this.Node_Expr_AssignMul(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn185 = function (e) {
        this.yyval = this.Node_Expr_AssignDiv(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn186 = function (e) {
        this.yyval = this.Node_Expr_AssignConcat(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn187 = function (e) {
        this.yyval = this.Node_Expr_AssignMod(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn188 = function (e) {
        this.yyval = this.Node_Expr_AssignBitwiseAnd(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn189 = function (e) {
        this.yyval = this.Node_Expr_AssignBitwiseOr(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn190 = function (e) {
        this.yyval = this.Node_Expr_AssignBitwiseXor(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn191 = function (e) {
        this.yyval = this.Node_Expr_AssignShiftLeft(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn192 = function (e) {
        this.yyval = this.Node_Expr_AssignShiftRight(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn193 = function (e) {
        this.yyval = this.Node_Expr_PostInc(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn194 = function (e) {
        this.yyval = this.Node_Expr_PreInc(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn195 = function (e) {
        this.yyval = this.Node_Expr_PostDec(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn196 = function (e) {
        this.yyval = this.Node_Expr_PreDec(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn197 = function (e) {
        this.yyval = this.Node_Expr_BooleanOr(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn198 = function (e) {
        this.yyval = this.Node_Expr_BooleanAnd(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn199 = function (e) {
        this.yyval = this.Node_Expr_LogicalOr(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn200 = function (e) {
        this.yyval = this.Node_Expr_LogicalAnd(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn201 = function (e) {
        this.yyval = this.Node_Expr_LogicalXor(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn202 = function (e) {
        this.yyval = this.Node_Expr_BitwiseOr(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn203 = function (e) {
        this.yyval = this.Node_Expr_BitwiseAnd(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn204 = function (e) {
        this.yyval = this.Node_Expr_BitwiseXor(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn205 = function (e) {
        this.yyval = this.Node_Expr_Concat(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn206 = function (e) {
        this.yyval = this.Node_Expr_Plus(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn207 = function (e) {
        this.yyval = this.Node_Expr_Minus(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn208 = function (e) {
        this.yyval = this.Node_Expr_Mul(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn209 = function (e) {
        this.yyval = this.Node_Expr_Div(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn210 = function (e) {
        this.yyval = this.Node_Expr_Mod(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn211 = function (e) {
        this.yyval = this.Node_Expr_ShiftLeft(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn212 = function (e) {
        this.yyval = this.Node_Expr_ShiftRight(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn213 = function (e) {
        this.yyval = this.Node_Expr_UnaryPlus(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn214 = function (e) {
        this.yyval = this.Node_Expr_UnaryMinus(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn215 = function (e) {
        this.yyval = this.Node_Expr_BooleanNot(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn216 = function (e) {
        this.yyval = this.Node_Expr_BitwiseNot(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn217 = function (e) {
        this.yyval = this.Node_Expr_Identical(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn218 = function (e) {
        this.yyval = this.Node_Expr_NotIdentical(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn219 = function (e) {
        this.yyval = this.Node_Expr_Equal(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn220 = function (e) {
        this.yyval = this.Node_Expr_NotEqual(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn221 = function (e) {
        this.yyval = this.Node_Expr_Smaller(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn222 = function (e) {
        this.yyval = this.Node_Expr_SmallerOrEqual(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn223 = function (e) {
        this.yyval = this.Node_Expr_Greater(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn224 = function (e) {
        this.yyval = this.Node_Expr_GreaterOrEqual(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn225 = function (e) {
        this.yyval = this.Node_Expr_Instanceof(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn226 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn227 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn228 = function (e) {
        this.yyval = this.Node_Expr_Ternary(this.yyastk[this.stackPos - 4], this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn229 = function (e) {
        this.yyval = this.Node_Expr_Ternary(this.yyastk[this.stackPos - 3], null, this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn230 = function (e) {
        this.yyval = this.Node_Expr_Isset(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn231 = function (e) {
        this.yyval = this.Node_Expr_Empty(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn232 = function (e) {
        this.yyval = this.Node_Expr_Include(this.yyastk[this.stackPos - 0], "Node_Expr_Include", e)
    },r.Parser.prototype.yyn233 = function (e) {
        this.yyval = this.Node_Expr_Include(this.yyastk[this.stackPos - 0], "Node_Expr_IncludeOnce", e)
    },r.Parser.prototype.yyn234 = function (e) {
        this.yyval = this.Node_Expr_Eval(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn235 = function (e) {
        this.yyval = this.Node_Expr_Include(this.yyastk[this.stackPos - 0], "Node_Expr_Require", e)
    },r.Parser.prototype.yyn236 = function (e) {
        this.yyval = this.Node_Expr_Include(this.yyastk[this.stackPos - 0], "Node_Expr_RequireOnce", e)
    },r.Parser.prototype.yyn237 = function (e) {
        this.yyval = this.Node_Expr_Cast_Int(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn238 = function (e) {
        this.yyval = this.Node_Expr_Cast_Double(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn239 = function (e) {
        this.yyval = this.Node_Expr_Cast_String(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn240 = function (e) {
        this.yyval = this.Node_Expr_Cast_Array(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn241 = function (e) {
        this.yyval = this.Node_Expr_Cast_Object(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn242 = function (e) {
        this.yyval = this.Node_Expr_Cast_Bool(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn243 = function (e) {
        this.yyval = this.Node_Expr_Cast_Unset(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn244 = function (e) {
        this.yyval = this.Node_Expr_Exit(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn245 = function (e) {
        this.yyval = this.Node_Expr_ErrorSuppress(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn246 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn247 = function (e) {
        this.yyval = this.Node_Expr_Array(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn248 = function (e) {
        this.yyval = this.Node_Expr_Array(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn249 = function (e) {
        this.yyval = this.Node_Expr_ShellExec(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn250 = function (e) {
        this.yyval = this.Node_Expr_Print(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn251 = function (e) {
        this.yyval = this.Node_Expr_Closure({"static": !1, byRef: this.yyastk[this.stackPos - 7], params: this.yyastk[this.stackPos - 5], uses: this.yyastk[this.stackPos - 3], stmts: this.yyastk[this.stackPos - 1]}, e)
    },r.Parser.prototype.yyn252 = function (e) {
        this.yyval = this.Node_Expr_Closure({"static": !0, byRef: this.yyastk[this.stackPos - 7], params: this.yyastk[this.stackPos - 5], uses: this.yyastk[this.stackPos - 3], stmts: this.yyastk[this.stackPos - 1]}, e)
    },r.Parser.prototype.yyn253 = function (e) {
        this.yyval = this.Node_Expr_New(this.yyastk[this.stackPos - 1], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn254 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn255 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn256 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn257 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn258 = function (e) {
        this.yyval = this.Node_Expr_ClosureUse(this.yyastk[this.stackPos - 0].substring(1), this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn259 = function (e) {
        this.yyval = this.Node_Expr_FuncCall(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn260 = function (e) {
        this.yyval = this.Node_Expr_StaticCall(this.yyastk[this.stackPos - 5], this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn261 = function (e) {
        this.yyval = this.Node_Expr_StaticCall(this.yyastk[this.stackPos - 7], this.yyastk[this.stackPos - 4], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn262 = function (e) {
        if (this.yyastk[this.stackPos - 3].type === "Node_Expr_StaticPropertyFetch")this.yyval = this.Node_Expr_StaticCall(this.yyastk[this.stackPos - 3].Class, this.Node_Expr_Variable(this.yyastk[this.stackPos - 3].name, e), this.yyastk[this.stackPos - 1], e); else {
            if (this.yyastk[this.stackPos - 3].type !== "Node_Expr_ArrayDimFetch")throw new Exception;
            var t = this.yyastk[this.stackPos - 3];
            while (t.variable.type === "Node_Expr_ArrayDimFetch")t = t.variable;
            this.yyval = this.Node_Expr_StaticCall(t.variable.Class, this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e), t.variable = this.Node_Expr_Variable(t.variable.name, e)
        }
    },r.Parser.prototype.yyn263 = function (e) {
        this.yyval = this.Node_Expr_FuncCall(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn264 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn265 = function (e) {
        this.yyval = this.Node_Name("static", e)
    },r.Parser.prototype.yyn266 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn267 = function (e) {
        this.yyval = this.Node_Name(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn268 = function (e) {
        this.yyval = this.Node_Name_FullyQualified(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn269 = function (e) {
        this.yyval = this.Node_Name_Relative(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn270 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn271 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn272 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn273 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn274 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn275 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn276 = function () {
        this.yyval = this.yyastk[this.stackPos]
    },r.Parser.prototype.yyn277 = function (e) {
        this.yyval = this.Node_Expr_PropertyFetch(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn278 = function (e) {
        this.yyval = this.Node_Expr_PropertyFetch(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn279 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn280 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn281 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn282 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn283 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn284 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn285 = function (e) {
        this.yyval = [this.Scalar_String_parseEscapeSequences(this.yyastk[this.stackPos - 0], "`")]
    },r.Parser.prototype.yyn286 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn287 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn288 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn289 = function (e) {
        this.yyval = this.Node_Scalar_LNumber(this.Scalar_LNumber_parse(this.yyastk[this.stackPos - 0]), e)
    },r.Parser.prototype.yyn290 = function (e) {
        this.yyval = this.Node_Scalar_DNumber(this.Scalar_DNumber_parse(this.yyastk[this.stackPos - 0]), e)
    },r.Parser.prototype.yyn291 = function (e) {
        this.yyval = this.Scalar_String_create(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn292 = function (e) {
        this.yyval = {type: "Node_Scalar_LineConst", attributes: e}
    },r.Parser.prototype.yyn293 = function (e) {
        this.yyval = {type: "Node_Scalar_FileConst", attributes: e}
    },r.Parser.prototype.yyn294 = function (e) {
        this.yyval = {type: "Node_Scalar_DirConst", attributes: e}
    },r.Parser.prototype.yyn295 = function (e) {
        this.yyval = {type: "Node_Scalar_ClassConst", attributes: e}
    },r.Parser.prototype.yyn296 = function (e) {
        this.yyval = {type: "Node_Scalar_TraitConst", attributes: e}
    },r.Parser.prototype.yyn297 = function (e) {
        this.yyval = {type: "Node_Scalar_MethodConst", attributes: e}
    },r.Parser.prototype.yyn298 = function (e) {
        this.yyval = {type: "Node_Scalar_FuncConst", attributes: e}
    },r.Parser.prototype.yyn299 = function (e) {
        this.yyval = {type: "Node_Scalar_NSConst", attributes: e}
    },r.Parser.prototype.yyn300 = function (e) {
        this.yyval = this.Node_Scalar_String(this.Scalar_String_parseDocString(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 1]), e)
    },r.Parser.prototype.yyn301 = function (e) {
        this.yyval = this.Node_Scalar_String("", e)
    },r.Parser.prototype.yyn302 = function (e) {
        this.yyval = this.Node_Expr_ConstFetch(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn303 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn304 = function (e) {
        this.yyval = this.Node_Expr_ClassConstFetch(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn305 = function (e) {
        this.yyval = this.Node_Expr_UnaryPlus(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn306 = function (e) {
        this.yyval = this.Node_Expr_UnaryMinus(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn307 = function (e) {
        this.yyval = this.Node_Expr_Array(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn308 = function (e) {
        this.yyval = this.Node_Expr_Array(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn309 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn310 = function (e) {
        this.yyval = this.Node_Expr_ClassConstFetch(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn311 = function (e) {
        this.yyval = this.Node_Scalar_Encapsed(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn312 = function (e) {
        this.yyval = this.Node_Scalar_Encapsed(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn313 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn314 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn315 = function () {
        this.yyval = this.yyastk[this.stackPos]
    },r.Parser.prototype.yyn316 = function () {
        this.yyval = this.yyastk[this.stackPos]
    },r.Parser.prototype.yyn317 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn318 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn319 = function (e) {
        this.yyval = this.Node_Expr_ArrayItem(this.yyastk[this.stackPos - 0], this.yyastk[this.stackPos - 2], !1, e)
    },r.Parser.prototype.yyn320 = function (e) {
        this.yyval = this.Node_Expr_ArrayItem(this.yyastk[this.stackPos - 0], null, !1, e)
    },r.Parser.prototype.yyn321 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn322 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn323 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn324 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn325 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 4], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn326 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn327 = function (e) {
        this.yyval = this.Node_Expr_PropertyFetch(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn328 = function (e) {
        this.yyval = this.Node_Expr_MethodCall(this.yyastk[this.stackPos - 5], this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn329 = function (e) {
        this.yyval = this.Node_Expr_FuncCall(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn330 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn331 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn332 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn333 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn334 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn335 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn336 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn337 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn338 = function (e) {
        this.yyval = this.Node_Expr_StaticPropertyFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn339 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn340 = function (e) {
        this.yyval = this.Node_Expr_StaticPropertyFetch(this.yyastk[this.stackPos - 2], this.yyastk[this.stackPos - 0].substring(1), e)
    },r.Parser.prototype.yyn341 = function (e) {
        this.yyval = this.Node_Expr_StaticPropertyFetch(this.yyastk[this.stackPos - 5], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn342 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn343 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn344 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn345 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.yyastk[this.stackPos - 3], this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn346 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 0].substring(1), e)
    },r.Parser.prototype.yyn347 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn348 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn349 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn350 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn351 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn352 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn353 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn354 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn355 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 0]
    },r.Parser.prototype.yyn356 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn357 = function (e) {
        this.yyval = null
    },r.Parser.prototype.yyn358 = function (e) {
        this.yyval = []
    },r.Parser.prototype.yyn359 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn360 = function (e) {
        this.yyastk[this.stackPos - 2].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 2]
    },r.Parser.prototype.yyn361 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn362 = function (e) {
        this.yyval = this.Node_Expr_ArrayItem(this.yyastk[this.stackPos - 0], this.yyastk[this.stackPos - 2], !1, e)
    },r.Parser.prototype.yyn363 = function (e) {
        this.yyval = this.Node_Expr_ArrayItem(this.yyastk[this.stackPos - 0], null, !1, e)
    },r.Parser.prototype.yyn364 = function (e) {
        this.yyval = this.Node_Expr_ArrayItem(this.yyastk[this.stackPos - 0], this.yyastk[this.stackPos - 3], !0, e)
    },r.Parser.prototype.yyn365 = function (e) {
        this.yyval = this.Node_Expr_ArrayItem(this.yyastk[this.stackPos - 0], null, !0, e)
    },r.Parser.prototype.yyn366 = function (e) {
        this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn367 = function (e) {
        this.yyastk[this.stackPos - 1].push(this.yyastk[this.stackPos - 0]), this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn368 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn369 = function (e) {
        this.yyval = [this.yyastk[this.stackPos - 1], this.yyastk[this.stackPos - 0]]
    },r.Parser.prototype.yyn370 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 0].substring(1), e)
    },r.Parser.prototype.yyn371 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.Node_Expr_Variable(this.yyastk[this.stackPos - 3].substring(1), e), this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn372 = function (e) {
        this.yyval = this.Node_Expr_PropertyFetch(this.Node_Expr_Variable(this.yyastk[this.stackPos - 2].substring(1), e), this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn373 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn374 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 1], e)
    },r.Parser.prototype.yyn375 = function (e) {
        this.yyval = this.Node_Expr_ArrayDimFetch(this.Node_Expr_Variable(this.yyastk[this.stackPos - 4], e), this.yyastk[this.stackPos - 2], e)
    },r.Parser.prototype.yyn376 = function (e) {
        this.yyval = this.yyastk[this.stackPos - 1]
    },r.Parser.prototype.yyn377 = function (e) {
        this.yyval = this.Node_Scalar_String(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn378 = function (e) {
        this.yyval = this.Node_Scalar_String(this.yyastk[this.stackPos - 0], e)
    },r.Parser.prototype.yyn379 = function (e) {
        this.yyval = this.Node_Expr_Variable(this.yyastk[this.stackPos - 0].substring(1), e)
    },r.Parser.prototype.Stmt_Namespace_postprocess = function (e) {
        return e
    },r.Parser.prototype.Node_Stmt_Echo = function () {
        return{type: "Node_Stmt_Echo", exprs: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_If = function () {
        return{type: "Node_Stmt_If", cond: arguments[0], stmts: arguments[1].stmts, elseifs: arguments[1].elseifs, Else: arguments[1].Else || null, attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_For = function () {
        return{type: "Node_Stmt_For", init: arguments[0].init, cond: arguments[0].cond, loop: arguments[0].loop, stmts: arguments[0].stmts, attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_Function = function () {
        return{type: "Node_Stmt_Function", name: arguments[0], byRef: arguments[1].byRef, params: arguments[1].params, stmts: arguments[1].stmts, attributes: arguments[2]}
    },r.Parser.prototype.Stmt_Class_verifyModifier = function () {
    },r.Parser.prototype.Node_Stmt_Namespace = function () {
        return{type: "Node_Stmt_Namespace", name: arguments[0], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Use = function () {
        return{type: "Node_Stmt_Use", name: arguments[0], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_UseUse = function () {
        return{type: "Node_Stmt_UseUse", name: arguments[0], as: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_TraitUseAdaptation_Precedence = function () {
        return{type: "Node_Stmt_TraitUseAdaptation_Precedence", name: arguments[0], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_TraitUseAdaptation_Alias = function () {
        return{type: "Node_Stmt_TraitUseAdaptation_Alias", name: arguments[0], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Trait = function () {
        return{type: "Node_Stmt_Trait", name: arguments[0], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_TraitUse = function () {
        return{type: "Node_Stmt_TraitUse", name: arguments[0], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Class = function () {
        return{type: "Node_Stmt_Class", name: arguments[0], Type: arguments[1].type, Extends: arguments[1].Extends, Implements: arguments[1].Implements, stmts: arguments[1].stmts, attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_ClassMethod = function () {
        return{type: "Node_Stmt_ClassMethod", name: arguments[0], Type: arguments[1].type, byRef: arguments[1].byRef, params: arguments[1].params, stmts: arguments[1].stmts, attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_ClassConst = function () {
        return{type: "Node_Stmt_ClassConst", consts: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_Interface = function () {
        return{type: "Node_Stmt_Interface", name: arguments[0], Extends: arguments[1].Extends, stmts: arguments[1].stmts, attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Throw = function () {
        return{type: "Node_Stmt_Throw", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_Catch = function () {
        return{type: "Node_Stmt_Catch", Type: arguments[0], variable: arguments[1], stmts: arguments[2], attributes: arguments[3]}
    },r.Parser.prototype.Node_Stmt_TryCatch = function () {
        return{type: "Node_Stmt_TryCatch", stmts: arguments[0], catches: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Foreach = function () {
        return{type: "Node_Stmt_Foreach", expr: arguments[0], valueVar: arguments[1], keyVar: arguments[2].keyVar, byRef: arguments[2].byRef, stmts: arguments[2].stmts, attributes: arguments[3]}
    },r.Parser.prototype.Node_Stmt_While = function () {
        return{type: "Node_Stmt_While", cond: arguments[0], stmts: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Do = function () {
        return{type: "Node_Stmt_Do", cond: arguments[0], stmts: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Break = function () {
        return{type: "Node_Stmt_Break", num: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_Continue = function () {
        return{type: "Node_Stmt_Continue", num: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_Return = function () {
        return{type: "Node_Stmt_Return", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_Case = function () {
        return{type: "Node_Stmt_Case", cond: arguments[0], stmts: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Switch = function () {
        return{type: "Node_Stmt_Switch", cond: arguments[0], cases: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Else = function () {
        return{type: "Node_Stmt_Else", stmts: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_ElseIf = function () {
        return{type: "Node_Stmt_ElseIf", cond: arguments[0], stmts: arguments[1], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_InlineHTML = function () {
        return{type: "Node_Stmt_InlineHTML", value: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_StaticVar = function () {
        return{type: "Node_Stmt_StaticVar", name: arguments[0], def: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Static = function () {
        return{type: "Node_Stmt_Static", vars: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_Global = function () {
        return{type: "Node_Stmt_Global", vars: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Stmt_PropertyProperty = function () {
        return{type: "Node_Stmt_PropertyProperty", name: arguments[0], def: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Property = function () {
        return{type: "Node_Stmt_Property", Type: arguments[0], props: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Stmt_Unset = function () {
        return{type: "Node_Stmt_Unset", variables: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Variable = function (e) {
        return{type: "Node_Expr_Variable", name: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_FuncCall = function () {
        return{type: "Node_Expr_FuncCall", func: arguments[0], args: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_MethodCall = function () {
        return{type: "Node_Expr_MethodCall", variable: arguments[0], name: arguments[1], args: arguments[2], attributes: arguments[3]}
    },r.Parser.prototype.Node_Expr_StaticCall = function () {
        return{type: "Node_Expr_StaticCall", Class: arguments[0], func: arguments[1], args: arguments[2], attributes: arguments[3]}
    },r.Parser.prototype.Node_Expr_Ternary = function () {
        return{type: "Node_Expr_Ternary", cond: arguments[0], If: arguments[1], Else: arguments[2], attributes: arguments[3]}
    },r.Parser.prototype.Node_Expr_AssignList = function () {
        return{type: "Node_Expr_AssignList", assignList: arguments[0], expr: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Assign = function () {
        return{type: "Node_Expr_Assign", variable: arguments[0], expr: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_AssignConcat = function () {
        return{type: "Node_Expr_AssignConcat", variable: arguments[0], expr: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_AssignMinus = function () {
        return{type: "Node_Expr_AssignMinus", variable: arguments[0], expr: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_AssignPlus = function () {
        return{type: "Node_Expr_AssignPlus", variable: arguments[0], expr: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_AssignDiv = function () {
        return{type: "Node_Expr_AssignDiv", variable: arguments[0], expr: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_AssignRef = function () {
        return{type: "Node_Expr_AssignRef", variable: arguments[0], refVar: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_AssignMul = function () {
        return{type: "Node_Expr_AssignMul", variable: arguments[0], expr: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_AssignMod = function () {
        return{type: "Node_Expr_AssignMod", variable: arguments[0], expr: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Plus = function () {
        return{type: "Node_Expr_Plus", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Minus = function () {
        return{type: "Node_Expr_Minus", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Mul = function () {
        return{type: "Node_Expr_Mul", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Div = function () {
        return{type: "Node_Expr_Div", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Mod = function () {
        return{type: "Node_Expr_Mod", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Greater = function () {
        return{type: "Node_Expr_Greater", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Equal = function () {
        return{type: "Node_Expr_Equal", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_NotEqual = function () {
        return{type: "Node_Expr_NotEqual", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Identical = function () {
        return{type: "Node_Expr_Identical", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_NotIdentical = function () {
        return{type: "Node_Expr_NotIdentical", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_GreaterOrEqual = function () {
        return{type: "Node_Expr_GreaterOrEqual", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_SmallerOrEqual = function () {
        return{type: "Node_Expr_SmallerOrEqual", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Concat = function () {
        return{type: "Node_Expr_Concat", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Smaller = function () {
        return{type: "Node_Expr_Smaller", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_PostInc = function () {
        return{type: "Node_Expr_PostInc", variable: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_PostDec = function () {
        return{type: "Node_Expr_PostDec", variable: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_PreInc = function () {
        return{type: "Node_Expr_PreInc", variable: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_PreDec = function () {
        return{type: "Node_Expr_PreDec", variable: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Include = function () {
        return{expr: arguments[0], type: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_ArrayDimFetch = function () {
        return{type: "Node_Expr_ArrayDimFetch", variable: arguments[0], dim: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_StaticPropertyFetch = function () {
        return{type: "Node_Expr_StaticPropertyFetch", Class: arguments[0], name: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_ClassConstFetch = function () {
        return{type: "Node_Expr_ClassConstFetch", Class: arguments[0], name: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_StaticPropertyFetch = function () {
        return{type: "Node_Expr_StaticPropertyFetch", Class: arguments[0], name: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_ConstFetch = function () {
        return{type: "Node_Expr_ConstFetch", name: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_ArrayItem = function () {
        return{type: "Node_Expr_ArrayItem", value: arguments[0], key: arguments[1], byRef: arguments[2], attributes: arguments[3]}
    },r.Parser.prototype.Node_Expr_Array = function () {
        return{type: "Node_Expr_Array", items: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_PropertyFetch = function () {
        return{type: "Node_Expr_PropertyFetch", variable: arguments[0], name: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_New = function () {
        return{type: "Node_Expr_New", Class: arguments[0], args: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Print = function () {
        return{type: "Node_Expr_Print", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Exit = function () {
        return{type: "Node_Expr_Exit", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Cast_Bool = function () {
        return{type: "Node_Expr_Cast_Bool", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Cast_Int = function () {
        return{type: "Node_Expr_Cast_Int", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Cast_String = function () {
        return{type: "Node_Expr_Cast_String", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Cast_Double = function () {
        return{type: "Node_Expr_Cast_Double", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Cast_Array = function () {
        return{type: "Node_Expr_Cast_Array", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Cast_Object = function () {
        return{type: "Node_Expr_Cast_Object", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_ErrorSuppress = function () {
        return{type: "Node_Expr_ErrorSuppress", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Isset = function () {
        return{type: "Node_Expr_Isset", variables: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_UnaryMinus = function () {
        return{type: "Node_Expr_UnaryMinus", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_UnaryPlus = function () {
        return{type: "Node_Expr_UnaryPlus", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_Empty = function () {
        return{type: "Node_Expr_Empty", variable: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_BooleanOr = function () {
        return{type: "Node_Expr_BooleanOr", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_LogicalOr = function () {
        return{type: "Node_Expr_LogicalOr", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_LogicalAnd = function () {
        return{type: "Node_Expr_LogicalAnd", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_LogicalXor = function () {
        return{type: "Node_Expr_LogicalXor", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_BitwiseAnd = function () {
        return{type: "Node_Expr_BitwiseAnd", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_BitwiseOr = function () {
        return{type: "Node_Expr_BitwiseOr", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_BitwiseNot = function () {
        return{type: "Node_Expr_BitwiseNot", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_BooleanNot = function () {
        return{type: "Node_Expr_BooleanNot", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Expr_BooleanAnd = function () {
        return{type: "Node_Expr_BooleanAnd", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Instanceof = function () {
        return{type: "Node_Expr_Instanceof", left: arguments[0], right: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Expr_Clone = function () {
        return{type: "Node_Expr_Clone", expr: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Scalar_LNumber_parse = function (e) {
        return e
    },r.Parser.prototype.Scalar_DNumber_parse = function (e) {
        return e
    },r.Parser.prototype.Scalar_String_parseDocString = function () {
        return'"' + arguments[1].replace(/([^"\\]*(?:\\.[^"\\]*)*)"/g, '$1\\"') + '"'
    },r.Parser.prototype.Node_Scalar_String = function () {
        return{type: "Node_Scalar_String", value: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Scalar_String_create = function () {
        return{type: "Node_Scalar_String", value: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Scalar_LNumber = function () {
        return{type: "Node_Scalar_LNumber", value: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Scalar_DNumber = function () {
        return{type: "Node_Scalar_DNumber", value: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Scalar_Encapsed = function () {
        return{type: "Node_Scalar_Encapsed", parts: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Name = function () {
        return{type: "Node_Name", parts: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Name_FullyQualified = function () {
        return{type: "Node_Name_FullyQualified", parts: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Name_Relative = function () {
        return{type: "Node_Name_Relative", parts: arguments[0], attributes: arguments[1]}
    },r.Parser.prototype.Node_Param = function () {
        return{type: "Node_Param", name: arguments[0], def: arguments[1], Type: arguments[2], byRef: arguments[3], attributes: arguments[4]}
    },r.Parser.prototype.Node_Arg = function () {
        return{type: "Node_Name", value: arguments[0], byRef: arguments[1], attributes: arguments[2]}
    },r.Parser.prototype.Node_Const = function () {
        return{type: "Node_Const", name: arguments[0], value: arguments[1], attributes: arguments[2]}
    },t.PHP = r
}), ace.define("ace/document", ["require", "exports", "module", "ace/lib/oop", "ace/lib/event_emitter", "ace/range", "ace/anchor"], function (e, t, n) {
    var r = e("./lib/oop"), i = e("./lib/event_emitter").EventEmitter, s = e("./range").Range, o = e("./anchor").Anchor, u = function (e) {
        this.$lines = [], e.length === 0 ? this.$lines = [""] : Array.isArray(e) ? this._insertLines(0, e) : this.insert({row: 0, column: 0}, e)
    };
    (function () {
        r.implement(this, i), this.setValue = function (e) {
            var t = this.getLength();
            this.remove(new s(0, 0, t, this.getLine(t - 1).length)), this.insert({row: 0, column: 0}, e)
        }, this.getValue = function () {
            return this.getAllLines().join(this.getNewLineCharacter())
        }, this.createAnchor = function (e, t) {
            return new o(this, e, t)
        }, "aaa".split(/a/).length === 0 ? this.$split = function (e) {
            return e.replace(/\r\n|\r/g, "\n").split("\n")
        } : this.$split = function (e) {
            return e.split(/\r\n|\r|\n/)
        }, this.$detectNewLine = function (e) {
            var t = e.match(/^.*?(\r\n|\r|\n)/m);
            this.$autoNewLine = t ? t[1] : "\n", this._signal("changeNewLineMode")
        }, this.getNewLineCharacter = function () {
            switch (this.$newLineMode) {
                case"windows":
                    return"\r\n";
                case"unix":
                    return"\n";
                default:
                    return this.$autoNewLine || "\n"
            }
        }, this.$autoNewLine = "", this.$newLineMode = "auto", this.setNewLineMode = function (e) {
            if (this.$newLineMode === e)return;
            this.$newLineMode = e, this._signal("changeNewLineMode")
        }, this.getNewLineMode = function () {
            return this.$newLineMode
        }, this.isNewLine = function (e) {
            return e == "\r\n" || e == "\r" || e == "\n"
        }, this.getLine = function (e) {
            return this.$lines[e] || ""
        }, this.getLines = function (e, t) {
            return this.$lines.slice(e, t + 1)
        }, this.getAllLines = function () {
            return this.getLines(0, this.getLength())
        }, this.getLength = function () {
            return this.$lines.length
        }, this.getTextRange = function (e) {
            if (e.start.row == e.end.row)return this.getLine(e.start.row).substring(e.start.column, e.end.column);
            var t = this.getLines(e.start.row, e.end.row);
            t[0] = (t[0] || "").substring(e.start.column);
            var n = t.length - 1;
            return e.end.row - e.start.row == n && (t[n] = t[n].substring(0, e.end.column)), t.join(this.getNewLineCharacter())
        }, this.$clipPosition = function (e) {
            var t = this.getLength();
            return e.row >= t ? (e.row = Math.max(0, t - 1), e.column = this.getLine(t - 1).length) : e.row < 0 && (e.row = 0), e
        }, this.insert = function (e, t) {
            if (!t || t.length === 0)return e;
            e = this.$clipPosition(e), this.getLength() <= 1 && this.$detectNewLine(t);
            var n = this.$split(t), r = n.splice(0, 1)[0], i = n.length == 0 ? null : n.splice(n.length - 1, 1)[0];
            return e = this.insertInLine(e, r), i !== null && (e = this.insertNewLine(e), e = this._insertLines(e.row, n), e = this.insertInLine(e, i || "")), e
        }, this.insertLines = function (e, t) {
            return e >= this.getLength() ? this.insert({row: e, column: 0}, "\n" + t.join("\n")) : this._insertLines(Math.max(e, 0), t)
        }, this._insertLines = function (e, t) {
            if (t.length == 0)return{row: e, column: 0};
            while (t.length > 61440) {
                var n = this._insertLines(e, t.slice(0, 61440));
                t = t.slice(61440), e = n.row
            }
            var r = [e, 0];
            r.push.apply(r, t), this.$lines.splice.apply(this.$lines, r);
            var i = new s(e, 0, e + t.length, 0), o = {action: "insertLines", range: i, lines: t};
            return this._signal("change", {data: o}), i.end
        }, this.insertNewLine = function (e) {
            e = this.$clipPosition(e);
            var t = this.$lines[e.row] || "";
            this.$lines[e.row] = t.substring(0, e.column), this.$lines.splice(e.row + 1, 0, t.substring(e.column, t.length));
            var n = {row: e.row + 1, column: 0}, r = {action: "insertText", range: s.fromPoints(e, n), text: this.getNewLineCharacter()};
            return this._signal("change", {data: r}), n
        }, this.insertInLine = function (e, t) {
            if (t.length == 0)return e;
            var n = this.$lines[e.row] || "";
            this.$lines[e.row] = n.substring(0, e.column) + t + n.substring(e.column);
            var r = {row: e.row, column: e.column + t.length}, i = {action: "insertText", range: s.fromPoints(e, r), text: t};
            return this._signal("change", {data: i}), r
        }, this.remove = function (e) {
            e instanceof s || (e = s.fromPoints(e.start, e.end)), e.start = this.$clipPosition(e.start), e.end = this.$clipPosition(e.end);
            if (e.isEmpty())return e.start;
            var t = e.start.row, n = e.end.row;
            if (e.isMultiLine()) {
                var r = e.start.column == 0 ? t : t + 1, i = n - 1;
                e.end.column > 0 && this.removeInLine(n, 0, e.end.column), i >= r && this._removeLines(r, i), r != t && (this.removeInLine(t, e.start.column, this.getLine(t).length), this.removeNewLine(e.start.row))
            } else this.removeInLine(t, e.start.column, e.end.column);
            return e.start
        }, this.removeInLine = function (e, t, n) {
            if (t == n)return;
            var r = new s(e, t, e, n), i = this.getLine(e), o = i.substring(t, n), u = i.substring(0, t) + i.substring(n, i.length);
            this.$lines.splice(e, 1, u);
            var a = {action: "removeText", range: r, text: o};
            return this._signal("change", {data: a}), r.start
        }, this.removeLines = function (e, t) {
            return e < 0 || t >= this.getLength() ? this.remove(new s(e, 0, t + 1, 0)) : this._removeLines(e, t)
        }, this._removeLines = function (e, t) {
            var n = new s(e, 0, t + 1, 0), r = this.$lines.splice(e, t - e + 1), i = {action: "removeLines", range: n, nl: this.getNewLineCharacter(), lines: r};
            return this._signal("change", {data: i}), r
        }, this.removeNewLine = function (e) {
            var t = this.getLine(e), n = this.getLine(e + 1), r = new s(e, t.length, e + 1, 0), i = t + n;
            this.$lines.splice(e, 2, i);
            var o = {action: "removeText", range: r, text: this.getNewLineCharacter()};
            this._signal("change", {data: o})
        }, this.replace = function (e, t) {
            e instanceof s || (e = s.fromPoints(e.start, e.end));
            if (t.length == 0 && e.isEmpty())return e.start;
            if (t == this.getTextRange(e))return e.end;
            this.remove(e);
            if (t)var n = this.insert(e.start, t); else n = e.start;
            return n
        }, this.applyDeltas = function (e) {
            for (var t = 0; t < e.length; t++) {
                var n = e[t], r = s.fromPoints(n.range.start, n.range.end);
                n.action == "insertLines" ? this.insertLines(r.start.row, n.lines) : n.action == "insertText" ? this.insert(r.start, n.text) : n.action == "removeLines" ? this._removeLines(r.start.row, r.end.row - 1) : n.action == "removeText" && this.remove(r)
            }
        }, this.revertDeltas = function (e) {
            for (var t = e.length - 1; t >= 0; t--) {
                var n = e[t], r = s.fromPoints(n.range.start, n.range.end);
                n.action == "insertLines" ? this._removeLines(r.start.row, r.end.row - 1) : n.action == "insertText" ? this.remove(r) : n.action == "removeLines" ? this._insertLines(r.start.row, n.lines) : n.action == "removeText" && this.insert(r.start, n.text)
            }
        }, this.indexToPosition = function (e, t) {
            var n = this.$lines || this.getAllLines(), r = this.getNewLineCharacter().length;
            for (var i = t || 0, s = n.length; i < s; i++) {
                e -= n[i].length + r;
                if (e < 0)return{row: i, column: e + n[i].length + r}
            }
            return{row: s - 1, column: n[s - 1].length}
        }, this.positionToIndex = function (e, t) {
            var n = this.$lines || this.getAllLines(), r = this.getNewLineCharacter().length, i = 0, s = Math.min(e.row, n.length);
            for (var o = t || 0; o < s; ++o)i += n[o].length + r;
            return i + e.column
        }
    }).call(u.prototype), t.Document = u
})