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
})(this), ace.define("ace/mode/html_worker", ["require", "exports", "module", "ace/lib/oop", "ace/lib/lang", "ace/worker/mirror", "ace/mode/html/saxparser"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("../lib/lang"), s = e("../worker/mirror").Mirror, o = e("./html/saxparser").SAXParser, u = {"expected-doctype-but-got-start-tag": "info", "expected-doctype-but-got-chars": "info", "non-html-root": "info"}, a = t.Worker = function (e) {
        s.call(this, e), this.setTimeout(400), this.context = null
    };
    r.inherits(a, s), function () {
        this.setOptions = function (e) {
            this.context = e.context
        }, this.onUpdate = function () {
            var e = this.doc.getValue();
            if (!e)return;
            var t = new o, n = [], r = function () {
            };
            t.contentHandler = {startDocument: r, endDocument: r, startElement: r, endElement: r, characters: r}, t.errorHandler = {error: function (e, t, r) {
                n.push({row: t.line, column: t.column, text: e, type: u[r] || "error"})
            }}, this.context ? t.parseFragment(e, this.context) : t.parse(e), this.sender.emit("error", n)
        }
    }.call(a.prototype)
}), ace.define("ace/lib/oop", ["require", "exports", "module"], function (e, t, n) {
    t.inherits = function (e, t) {
        e.super_ = t, e.prototype = Object.create(t.prototype, {constructor: {value: e, enumerable: !1, writable: !0, configurable: !0}})
    }, t.mixin = function (e, t) {
        for (var n in t)e[n] = t[n];
        return e
    }, t.implement = function (e, n) {
        t.mixin(e, n)
    }
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
}), ace.define("ace/mode/html/saxparser", ["require", "exports", "module"], function (e, t, n) {
    var r = e = function (t, n, r) {
        function i(r, o) {
            if (!n[r]) {
                if (!t[r]) {
                    var u = typeof e == "function" && e;
                    if (!o && u)return u(r, !0);
                    if (s)return s(r, !0);
                    throw new Error("Cannot find module '" + r + "'")
                }
                var a = n[r] = {exports: {}};
                t[r][0].call(a.exports, function (e) {
                    var n = t[r][1][e];
                    return i(n ? n : e)
                }, a, a.exports)
            }
            return n[r].exports
        }

        var s = typeof e == "function" && e;
        for (var o = 0; o < r.length; o++)i(r[o]);
        return i
    }({1: [function (e, t, n) {
        function r(e) {
            if (e.namespaceURI === "http://www.w3.org/1999/xhtml")return e.localName === "applet" || e.localName === "caption" || e.localName === "marquee" || e.localName === "object" || e.localName === "table" || e.localName === "td" || e.localName === "th";
            if (e.namespaceURI === "http://www.w3.org/1998/Math/MathML")return e.localName === "mi" || e.localName === "mo" || e.localName === "mn" || e.localName === "ms" || e.localName === "mtext" || e.localName === "annotation-xml";
            if (e.namespaceURI === "http://www.w3.org/2000/svg")return e.localName === "foreignObject" || e.localName === "desc" || e.localName === "title"
        }

        function i(e) {
            return r(e) || e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "ol" || e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "ul"
        }

        function s(e) {
            return e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "table" || e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "html"
        }

        function o(e) {
            return e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "tbody" || e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "tfoot" || e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "thead" || e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "html"
        }

        function u(e) {
            return e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "tr" || e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "html"
        }

        function a(e) {
            return r(e) || e.namespaceURI === "http://www.w3.org/1999/xhtml" && e.localName === "button"
        }

        function f(e) {
            return(e.namespaceURI !== "http://www.w3.org/1999/xhtml" || e.localName !== "optgroup") && (e.namespaceURI !== "http://www.w3.org/1999/xhtml" || e.localName !== "option")
        }

        function l() {
            this.elements = [], this.rootNode = null, this.headElement = null, this.bodyElement = null
        }

        l.prototype._inScope = function (e, t) {
            for (var n = this.elements.length - 1; n >= 0; n--) {
                var r = this.elements[n];
                if (r.localName === e)return!0;
                if (t(r))return!1
            }
        }, l.prototype.push = function (e) {
            this.elements.push(e)
        }, l.prototype.pushHtmlElement = function (e) {
            this.rootNode = e.node, this.push(e)
        }, l.prototype.pushHeadElement = function (e) {
            this.headElement = e.node, this.push(e)
        }, l.prototype.pushBodyElement = function (e) {
            this.bodyElement = e.node, this.push(e)
        }, l.prototype.pop = function () {
            return this.elements.pop()
        }, l.prototype.remove = function (e) {
            this.elements.splice(this.elements.indexOf(e), 1)
        }, l.prototype.popUntilPopped = function (e) {
            var t;
            do t = this.pop(); while (t.localName != e)
        }, l.prototype.popUntilTableScopeMarker = function () {
            while (!s(this.top))this.pop()
        }, l.prototype.popUntilTableBodyScopeMarker = function () {
            while (!o(this.top))this.pop()
        }, l.prototype.popUntilTableRowScopeMarker = function () {
            while (!u(this.top))this.pop()
        }, l.prototype.item = function (e) {
            return this.elements[e]
        }, l.prototype.contains = function (e) {
            return this.elements.indexOf(e) !== -1
        }, l.prototype.inScope = function (e) {
            return this._inScope(e, r)
        }, l.prototype.inListItemScope = function (e) {
            return this._inScope(e, i)
        }, l.prototype.inTableScope = function (e) {
            return this._inScope(e, s)
        }, l.prototype.inButtonScope = function (e) {
            return this._inScope(e, a)
        }, l.prototype.inSelectScope = function (e) {
            return this._inScope(e, f)
        }, l.prototype.hasNumberedHeaderElementInScope = function () {
            for (var e = this.elements.length - 1; e >= 0; e--) {
                var t = this.elements[e];
                if (t.isNumberedHeader())return!0;
                if (r(t))return!1
            }
        }, l.prototype.furthestBlockForFormattingElement = function (e) {
            var t = null;
            for (var n = this.elements.length - 1; n >= 0; n--) {
                var r = this.elements[n];
                if (r.node === e)return t;
                r.isSpecial() && (t = r)
            }
        }, l.prototype.findIndex = function (e) {
            for (var t = this.elements.length - 1; t >= 0; t--)if (this.elements[t].localName == e)return t
        }, l.prototype.remove_openElements_until = function (e) {
            var t = !1, n;
            while (!t)n = this.elements.pop(), t = e(n);
            return n
        }, Object.defineProperty(l.prototype, "top", {get: function () {
            return this.elements[this.elements.length - 1]
        }}), Object.defineProperty(l.prototype, "length", {get: function () {
            return this.elements.length
        }}), n.ElementStack = l
    }, {}], 2: [function (e, t, n) {
        function r(e) {
            return e >= "0" && e <= "9" || e >= "a" && e <= "z" || e >= "A" && e <= "Z"
        }

        function i(e) {
            return e >= "0" && e <= "9" || e >= "a" && e <= "f" || e >= "A" && e <= "F"
        }

        function s(e) {
            return e >= "0" && e <= "9"
        }

        var o = e("html5-entities"), u = e("./InputStream").InputStream, a = 55232, f = {};
        Object.keys(o).forEach(function (e) {
            for (var t = 0; t < e.length; t++)f[e.substring(0, t + 1)] = !0
        });
        var l = {};
        l.consumeEntity = function (e, t, n) {
            var l = "", c = "", h = e.char();
            if (h === u.EOF)return!1;
            c += h;
            if (h == "	" || h == "\n" || h == "" || h == " " || h == "<" || h == "&")return e.unget(c), !1;
            if (n === h)return e.unget(c), !1;
            if (h == "#") {
                h = e.shift(1);
                if (h === u.EOF)return t._parseError("expected-numeric-entity-but-got-eof"), e.unget(c), !1;
                c += h;
                var p = 10, d = s;
                if (h == "x" || h == "X") {
                    p = 16, d = i, h = e.shift(1);
                    if (h === u.EOF)return t._parseError("expected-numeric-entity-but-got-eof"), e.unget(c), !1;
                    c += h
                }
                if (d(h)) {
                    var v = "";
                    while (h !== u.EOF && d(h))v += h, h = e.char();
                    v = parseInt(v, p);
                    var m = this.replaceEntityNumbers(v);
                    m && (t._parseError("invalid-numeric-entity-replaced"), v = m);
                    if (v > 65535 && v < 1114111) {
                        var g = "";
                        g += String.fromCharCode(a + (v >> 10)), g += String.fromCharCode(56320 + (v & 1023)), l = g
                    } else l = String.fromCharCode(v);
                    return h !== ";" && (t._parseError("numeric-entity-without-semicolon"), e.unget(h)), l
                }
                return e.unget(c), t._parseError("expected-numeric-entity"), !1
            }
            if (h >= "a" && h <= "z" || h >= "A" && h <= "Z") {
                var y = "";
                while (f[c]) {
                    o[c] && (y = c);
                    if (h == ";")break;
                    h = e.char();
                    if (h === u.EOF)break;
                    c += h
                }
                return y ? (l = o[y], h === ";" || !n || !r(h) && h !== "=" ? (c.length > y.length && e.unget(c.substring(y.length)), h !== ";" && t._parseError("named-entity-without-semicolon"), l) : (e.unget(c), !1)) : (t._parseError("expected-named-entity"), e.unget(c), !1)
            }
        }, l.replaceEntityNumbers = function (e) {
            switch (e) {
                case 0:
                    return 65533;
                case 19:
                    return 16;
                case 128:
                    return 8364;
                case 129:
                    return 129;
                case 130:
                    return 8218;
                case 131:
                    return 402;
                case 132:
                    return 8222;
                case 133:
                    return 8230;
                case 134:
                    return 8224;
                case 135:
                    return 8225;
                case 136:
                    return 710;
                case 137:
                    return 8240;
                case 138:
                    return 352;
                case 139:
                    return 8249;
                case 140:
                    return 338;
                case 141:
                    return 141;
                case 142:
                    return 381;
                case 143:
                    return 143;
                case 144:
                    return 144;
                case 145:
                    return 8216;
                case 146:
                    return 8217;
                case 147:
                    return 8220;
                case 148:
                    return 8221;
                case 149:
                    return 8226;
                case 150:
                    return 8211;
                case 151:
                    return 8212;
                case 152:
                    return 732;
                case 153:
                    return 8482;
                case 154:
                    return 353;
                case 155:
                    return 8250;
                case 156:
                    return 339;
                case 157:
                    return 157;
                case 158:
                    return 382;
                case 159:
                    return 376;
                default:
                    if (e >= 55296 && e <= 57343 || e >= 1114111)return 65533;
                    if (e >= 1 && e <= 8 || e >= 14 && e <= 31 || e >= 127 && e <= 159 || e >= 64976 && e <= 65007 || e == 11 || e == 65534 || e == 131070 || e == 3145726 || e == 196607 || e == 262142 || e == 262143 || e == 327678 || e == 327679 || e == 393214 || e == 393215 || e == 458750 || e == 458751 || e == 524286 || e == 524287 || e == 589822 || e == 589823 || e == 655358 || e == 655359 || e == 720894 || e == 720895 || e == 786430 || e == 786431 || e == 851966 || e == 851967 || e == 917502 || e == 917503 || e == 983038 || e == 983039 || e == 1048574 || e == 1048575 || e == 1114110 || e == 1114111)return e
            }
        }, n.EntityParser = l
    }, {"./InputStream": 3, "html5-entities": 12}], 3: [function (e, t, n) {
        function r() {
            this.data = "", this.start = 0, this.committed = 0, this.eof = !1, this.lastLocation = {line: 0, column: 0}
        }

        r.EOF = -1, r.DRAIN = -2, r.prototype = {slice: function () {
            if (this.start >= this.data.length) {
                if (!this.eof)throw r.DRAIN;
                return r.EOF
            }
            return this.data.slice(this.start, this.data.length)
        }, "char": function () {
            if (!this.eof && this.start >= this.data.length - 1)throw r.DRAIN;
            return this.start >= this.data.length ? r.EOF : this.data[this.start++]
        }, advance: function (e) {
            this.start += e;
            if (this.start >= this.data.length) {
                if (!this.eof)throw r.DRAIN;
                return r.EOF
            }
            this.committed > this.data.length / 2 && (this.lastLocation = this.location(), this.data = this.data.slice(this.committed), this.start = this.start - this.committed, this.committed = 0)
        }, matchWhile: function (e) {
            if (this.eof && this.start >= this.data.length)return"";
            var t = new RegExp("^" + e + "+"), n = t.exec(this.slice());
            if (n) {
                if (!this.eof && n[0].length == this.data.length - this.start)throw r.DRAIN;
                return this.advance(n[0].length), n[0]
            }
            return""
        }, matchUntil: function (e) {
            var t, n;
            n = this.slice();
            if (n === r.EOF)return"";
            if (t = (new RegExp(e + (this.eof ? "|$" : ""))).exec(n)) {
                var i = this.data.slice(this.start, this.start + t.index);
                return this.advance(t.index), i.toString()
            }
            throw r.DRAIN
        }, append: function (e) {
            this.data += e
        }, shift: function (e) {
            if (!this.eof && this.start + e >= this.data.length)throw r.DRAIN;
            if (this.eof && this.start >= this.data.length)return r.EOF;
            var t = this.data.slice(this.start, this.start + e).toString();
            return this.advance(Math.min(e, this.data.length - this.start)), t
        }, peek: function (e) {
            if (!this.eof && this.start + e >= this.data.length)throw r.DRAIN;
            return this.eof && this.start >= this.data.length ? r.EOF : this.data.slice(this.start, Math.min(this.start + e, this.data.length)).toString()
        }, length: function () {
            return this.data.length - this.start - 1
        }, unget: function (e) {
            if (e === r.EOF)return;
            this.start -= e.length
        }, undo: function () {
            this.start = this.committed
        }, commit: function () {
            this.committed = this.start
        }, location: function () {
            var e = this.lastLocation.line, t = this.lastLocation.column, n = this.data.slice(0, this.committed), r = n.match(/\n/g), i = r ? e + r.length : e, s = r ? n.length - n.lastIndexOf("\n") - 1 : t + n.length;
            return{line: i, column: s}
        }}, n.InputStream = r
    }, {}], 4: [function (e, t, n) {
        function r(e, t, n, r) {
            this.localName = t, this.namespaceURI = e, this.attributes = n, this.node = r
        }

        function i(e, t) {
            for (var n = 0; n < e.attributes.length; n++)if (e.attributes[n].nodeName == t)return e.attributes[n].nodeValue;
            return null
        }

        var s = {"http://www.w3.org/1999/xhtml": ["address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", "br", "button", "caption", "center", "col", "colgroup", "dd", "details", "dir", "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", "img", "input", "isindex", "li", "link", "listing", "main", "marquee", "menu", "menuitem", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", "p", "param", "plaintext", "pre", "script", "section", "select", "source", "style", "summary", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "track", "ul", "wbr", "xmp"], "http://www.w3.org/1998/Math/MathML": ["mi", "mo", "mn", "ms", "mtext", "annotation-xml"], "http://www.w3.org/2000/svg": ["foreignObject", "desc", "title"]};
        r.prototype.isSpecial = function () {
            return this.namespaceURI in s && s[this.namespaceURI].indexOf(this.localName) > -1
        }, r.prototype.isFosterParenting = function () {
            return this.namespaceURI === "http://www.w3.org/1999/xhtml" ? this.localName === "table" || this.localName === "tbody" || this.localName === "tfoot" || this.localName === "thead" || this.localName === "tr" : !1
        }, r.prototype.isNumberedHeader = function () {
            return this.namespaceURI === "http://www.w3.org/1999/xhtml" ? this.localName === "h1" || this.localName === "h2" || this.localName === "h3" || this.localName === "h4" || this.localName === "h5" || this.localName === "h6" : !1
        }, r.prototype.isForeign = function () {
            return this.namespaceURI != "http://www.w3.org/1999/xhtml"
        }, r.prototype.isHtmlIntegrationPoint = function () {
            if (this.namespaceURI === "http://www.w3.org/1998/Math/MathML") {
                if (this.localName !== "annotation-xml")return!1;
                var e = i(this, "encoding");
                return e ? (e = e.toLowerCase(), e === "text/html" || e === "application/xhtml+xml") : !1
            }
            return this.namespaceURI === "http://www.w3.org/2000/svg" ? this.localName === "foreignObject" || this.localName === "desc" || this.localName === "title" : !1
        }, r.prototype.isMathMLTextIntegrationPoint = function () {
            return this.namespaceURI === "http://www.w3.org/1998/Math/MathML" ? this.localName === "mi" || this.localName === "mo" || this.localName === "mn" || this.localName === "ms" || this.localName === "mtext" : !1
        }, n.StackItem = r
    }, {}], 5: [function (e, t, n) {
        function r(e) {
            return e === " " || e === "\n" || e === "	" || e === "\r" || e === "\f"
        }

        function i(e) {
            return e >= "A" && e <= "Z" || e >= "a" && e <= "z"
        }

        function s(e) {
            this._tokenHandler = e, this._state = s.DATA, this._inputStream = new o, this._currentToken = null, this._temporaryBuffer = "", this._additionalAllowedCharacter = ""
        }

        var o = e("./InputStream").InputStream, u = e("./EntityParser").EntityParser;
        s.prototype._parseError = function (e, t) {
            this._tokenHandler.parseError(e, t)
        }, s.prototype._emitToken = function (e) {
            if (e.type === "StartTag")for (var t = 1; t < e.data.length; t++)e.data[t].nodeName || e.data.splice(t--, 1); else e.type === "EndTag" && (e.selfClosing && this._parseError("self-closing-flag-on-end-tag"), e.data.length !== 0 && this._parseError("attributes-in-end-tag"));
            this._tokenHandler.processToken(e), e.type === "StartTag" && e.selfClosing && !this._tokenHandler.isSelfClosingFlagAcknowledged() && this._parseError("non-void-element-with-trailing-solidus", {name: e.name})
        }, s.prototype._emitCurrentToken = function () {
            this._state = s.DATA, this._emitToken(this._currentToken)
        }, s.prototype._currentAttribute = function () {
            return this._currentToken.data[this._currentToken.data.length - 1]
        }, s.prototype.setState = function (e) {
            this._state = e
        }, s.prototype.tokenize = function (e) {
            function t(e) {
                var t = e.char();
                if (t === o.EOF)return wt._emitToken({type: "EOF", data: null}), !1;
                if (t === "&")wt.setState(n); else if (t === "<")wt.setState(B); else if (t === "\0")wt._emitToken({type: "Characters", data: t}), e.commit(); else {
                    var r = e.matchUntil("&|<|\0");
                    wt._emitToken({type: "Characters", data: t + r}), e.commit()
                }
                return!0
            }

            function n(e) {
                var n = u.consumeEntity(e, wt);
                return wt.setState(t), wt._emitToken({type: "Characters", data: n || "&"}), !0
            }

            function a(e) {
                var t = e.char();
                if (t === o.EOF)return wt._emitToken({type: "EOF", data: null}), !1;
                if (t === "&")wt.setState(f); else if (t === "<")wt.setState(p); else if (t === "\0")wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), e.commit(); else {
                    var n = e.matchUntil("&|<|\0");
                    wt._emitToken({type: "Characters", data: t + n}), e.commit()
                }
                return!0
            }

            function f(e) {
                var t = u.consumeEntity(e, wt);
                return wt.setState(a), wt._emitToken({type: "Characters", data: t || "&"}), !0
            }

            function l(e) {
                var t = e.char();
                if (t === o.EOF)return wt._emitToken({type: "EOF", data: null}), !1;
                if (t === "<")wt.setState(m); else if (t === "\0")wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), e.commit(); else {
                    var n = e.matchUntil("<|\0");
                    wt._emitToken({type: "Characters", data: t + n})
                }
                return!0
            }

            function c(e) {
                var t = e.char();
                if (t === o.EOF)return wt._emitToken({type: "EOF", data: null}), !1;
                if (t === "\0")wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), e.commit(); else {
                    var n = e.matchUntil("\0");
                    wt._emitToken({type: "Characters", data: t + n})
                }
                return!0
            }

            function h(e) {
                var t = e.char();
                if (t === o.EOF)return wt._emitToken({type: "EOF", data: null}), !1;
                if (t === "<")wt.setState(b); else if (t === "\0")wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), e.commit(); else {
                    var n = e.matchUntil("<|\0");
                    wt._emitToken({type: "Characters", data: t + n})
                }
                return!0
            }

            function p(e) {
                var t = e.char();
                return t === "/" ? (this._temporaryBuffer = "", wt.setState(d)) : (wt._emitToken({type: "Characters", data: "<"}), e.unget(t), wt.setState(a)), !0
            }

            function d(e) {
                var t = e.char();
                return i(t) ? (this._temporaryBuffer += t, wt.setState(v)) : (wt._emitToken({type: "Characters", data: "</"}), e.unget(t), wt.setState(a)), !0
            }

            function v(e) {
                var n = wt._currentToken && wt._currentToken.name === this._temporaryBuffer.toLowerCase(), s = e.char();
                return r(s) && n ? (wt._currentToken = {type: "EndTag", name: this._temporaryBuffer, data: [], selfClosing: !1}, wt.setState(I)) : s === "/" && n ? (wt._currentToken = {type: "EndTag", name: this._temporaryBuffer, data: [], selfClosing: !1}, wt.setState(J)) : s === ">" && n ? (wt._currentToken = {type: "EndTag", name: this._temporaryBuffer, data: [], selfClosing: !1}, wt._emitCurrentToken(), wt.setState(t)) : i(s) ? (this._temporaryBuffer += s, e.commit()) : (wt._emitToken({type: "Characters", data: "</" + this._temporaryBuffer}), e.unget(s), wt.setState(a)), !0
            }

            function m(e) {
                var t = e.char();
                return t === "/" ? (this._temporaryBuffer = "", wt.setState(g)) : (wt._emitToken({type: "Characters", data: "<"}), e.unget(t), wt.setState(l)), !0
            }

            function g(e) {
                var t = e.char();
                return i(t) ? (this._temporaryBuffer += t, wt.setState(y)) : (wt._emitToken({type: "Characters", data: "</"}), e.unget(t), wt.setState(l)), !0
            }

            function y(e) {
                var n = wt._currentToken && wt._currentToken.name === this._temporaryBuffer.toLowerCase(), s = e.char();
                return r(s) && n ? (wt._currentToken = {type: "EndTag", name: this._temporaryBuffer, data: [], selfClosing: !1}, wt.setState(I)) : s === "/" && n ? (wt._currentToken = {type: "EndTag", name: this._temporaryBuffer, data: [], selfClosing: !1}, wt.setState(J)) : s === ">" && n ? (wt._currentToken = {type: "EndTag", name: this._temporaryBuffer, data: [], selfClosing: !1}, wt._emitCurrentToken(), wt.setState(t)) : i(s) ? (this._temporaryBuffer += s, e.commit()) : (wt._emitToken({type: "Characters", data: "</" + this._temporaryBuffer}), e.unget(s), wt.setState(l)), !0
            }

            function b(e) {
                var t = e.char();
                return t === "/" ? (this._temporaryBuffer = "", wt.setState(w)) : t === "!" ? (wt._emitToken({type: "Characters", data: "<!"}), wt.setState(S)) : (wt._emitToken({type: "Characters", data: "<"}), e.unget(t), wt.setState(h)), !0
            }

            function w(e) {
                var t = e.char();
                return i(t) ? (this._temporaryBuffer += t, wt.setState(E)) : (wt._emitToken({type: "Characters", data: "</"}), e.unget(t), wt.setState(h)), !0
            }

            function E(e) {
                var t = wt._currentToken && wt._currentToken.name === this._temporaryBuffer.toLowerCase(), n = e.char();
                return r(n) && t ? (wt._currentToken = {type: "EndTag", name: "script", data: [], selfClosing: !1}, wt.setState(I)) : n === "/" && t ? (wt._currentToken = {type: "EndTag", name: "script", data: [], selfClosing: !1}, wt.setState(J)) : n === ">" && t ? (wt._currentToken = {type: "EndTag", name: "script", data: [], selfClosing: !1}, wt._emitCurrentToken()) : i(n) ? (this._temporaryBuffer += n, e.commit()) : (wt._emitToken({type: "Characters", data: "</" + this._temporaryBuffer}), e.unget(n), wt.setState(h)), !0
            }

            function S(e) {
                var t = e.char();
                return t === "-" ? (wt._emitToken({type: "Characters", data: "-"}), wt.setState(x)) : (e.unget(t), wt.setState(h)), !0
            }

            function x(e) {
                var t = e.char();
                return t === "-" ? (wt._emitToken({type: "Characters", data: "-"}), wt.setState(C)) : (e.unget(t), wt.setState(h)), !0
            }

            function T(e) {
                var n = e.char();
                if (n === o.EOF)e.unget(n), wt.setState(t); else if (n === "-")wt._emitToken({type: "Characters", data: "-"}), wt.setState(N); else if (n === "<")wt.setState(k); else if (n === "\0")wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), e.commit(); else {
                    var r = e.matchUntil("<|-|\0");
                    wt._emitToken({type: "Characters", data: n + r})
                }
                return!0
            }

            function N(e) {
                var n = e.char();
                return n === o.EOF ? (e.unget(n), wt.setState(t)) : n === "-" ? (wt._emitToken({type: "Characters", data: "-"}), wt.setState(C)) : n === "<" ? wt.setState(k) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), wt.setState(T)) : (wt._emitToken({type: "Characters", data: n}), wt.setState(T)), !0
            }

            function C(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-script"), e.unget(n), wt.setState(t)) : n === "<" ? wt.setState(k) : n === ">" ? (wt._emitToken({type: "Characters", data: ">"}), wt.setState(h)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), wt.setState(T)) : (wt._emitToken({type: "Characters", data: n}), wt.setState(T)), !0
            }

            function k(e) {
                var t = e.char();
                return t === "/" ? (this._temporaryBuffer = "", wt.setState(L)) : i(t) ? (wt._emitToken({type: "Characters", data: "<" + t}), this._temporaryBuffer = t, wt.setState(O)) : (wt._emitToken({type: "Characters", data: "<"}), e.unget(t), wt.setState(T)), !0
            }

            function L(e) {
                var t = e.char();
                return i(t) ? (this._temporaryBuffer = t, wt.setState(A)) : (wt._emitToken({type: "Characters", data: "</"}), e.unget(t), wt.setState(T)), !0
            }

            function A(e) {
                var n = wt._currentToken && wt._currentToken.name === this._temporaryBuffer.toLowerCase(), s = e.char();
                return r(s) && n ? (wt._currentToken = {type: "EndTag", name: "script", data: [], selfClosing: !1}, wt.setState(I)) : s === "/" && n ? (wt._currentToken = {type: "EndTag", name: "script", data: [], selfClosing: !1}, wt.setState(J)) : s === ">" && n ? (wt._currentToken = {type: "EndTag", name: "script", data: [], selfClosing: !1}, wt.setState(t), wt._emitCurrentToken()) : i(s) ? (this._temporaryBuffer += s, e.commit()) : (wt._emitToken({type: "Characters", data: "</" + this._temporaryBuffer}), e.unget(s), wt.setState(T)), !0
            }

            function O(e) {
                var t = e.char();
                return r(t) || t === "/" || t === ">" ? (wt._emitToken({type: "Characters", data: t}), this._temporaryBuffer.toLowerCase() === "script" ? wt.setState(M) : wt.setState(T)) : i(t) ? (wt._emitToken({type: "Characters", data: t}), this._temporaryBuffer += t, e.commit()) : (e.unget(t), wt.setState(T)), !0
            }

            function M(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-script"), e.unget(n), wt.setState(t)) : n === "-" ? (wt._emitToken({type: "Characters", data: "-"}), wt.setState(_)) : n === "<" ? (wt._emitToken({type: "Characters", data: "<"}), wt.setState(P)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), e.commit()) : (wt._emitToken({type: "Characters", data: n}), e.commit()), !0
            }

            function _(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-script"), e.unget(n), wt.setState(t)) : n === "-" ? (wt._emitToken({type: "Characters", data: "-"}), wt.setState(D)) : n === "<" ? (wt._emitToken({type: "Characters", data: "<"}), wt.setState(P)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), wt.setState(M)) : (wt._emitToken({type: "Characters", data: n}), wt.setState(M)), !0
            }

            function D(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-script"), e.unget(n), wt.setState(t)) : n === "-" ? (wt._emitToken({type: "Characters", data: "-"}), e.commit()) : n === "<" ? (wt._emitToken({type: "Characters", data: "<"}), wt.setState(P)) : n === ">" ? (wt._emitToken({type: "Characters", data: ">"}), wt.setState(h)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._emitToken({type: "Characters", data: "�"}), wt.setState(M)) : (wt._emitToken({type: "Characters", data: n}), wt.setState(M)), !0
            }

            function P(e) {
                var t = e.char();
                return t === "/" ? (wt._emitToken({type: "Characters", data: "/"}), this._temporaryBuffer = "", wt.setState(H)) : (e.unget(t), wt.setState(M)), !0
            }

            function H(e) {
                var t = e.char();
                return r(t) || t === "/" || t === ">" ? (wt._emitToken({type: "Characters", data: t}), this._temporaryBuffer.toLowerCase() === "script" ? wt.setState(T) : wt.setState(M)) : i(t) ? (wt._emitToken({type: "Characters", data: t}), this._temporaryBuffer += t, e.commit()) : (e.unget(t), wt.setState(M)), !0
            }

            function B(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("bare-less-than-sign-at-eof"), wt._emitToken({type: "Characters", data: "<"}), e.unget(n), wt.setState(t)) : i(n) ? (wt._currentToken = {type: "StartTag", name: n.toLowerCase(), data: []}, wt.setState(F)) : n === "!" ? wt.setState(Q) : n === "/" ? wt.setState(j) : n === ">" ? (wt._parseError("expected-tag-name-but-got-right-bracket"), wt._emitToken({type: "Characters", data: "<>"}), wt.setState(t)) : n === "?" ? (wt._parseError("expected-tag-name-but-got-question-mark"), e.unget(n), wt.setState(K)) : (wt._parseError("expected-tag-name"), wt._emitToken({type: "Characters", data: "<"}), e.unget(n), wt.setState(t)), !0
            }

            function j(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("expected-closing-tag-but-got-eof"), wt._emitToken({type: "Characters", data: "</"}), e.unget(n), wt.setState(t)) : i(n) ? (wt._currentToken = {type: "EndTag", name: n.toLowerCase(), data: []}, wt.setState(F)) : n === ">" ? (wt._parseError("expected-closing-tag-but-got-right-bracket"), wt.setState(t)) : (wt._parseError("expected-closing-tag-but-got-char", {data: n}), e.unget(n), wt.setState(K)), !0
            }

            function F(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-tag-name"), e.unget(n), wt.setState(t)) : r(n) ? wt.setState(I) : i(n) ? wt._currentToken.name += n.toLowerCase() : n === ">" ? wt._emitCurrentToken() : n === "/" ? wt.setState(J) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentToken.name += "�") : wt._currentToken.name += n, e.commit(), !0
            }

            function I(e) {
                var n = e.char();
                if (n === o.EOF)wt._parseError("expected-attribute-name-but-got-eof"), e.unget(n), wt.setState(t); else {
                    if (r(n))return!0;
                    i(n) ? (wt._currentToken.data.push({nodeName: n.toLowerCase(), nodeValue: ""}), wt.setState(q)) : n === ">" ? wt._emitCurrentToken() : n === "/" ? wt.setState(J) : n === "'" || n === '"' || n === "=" || n === "<" ? (wt._parseError("invalid-character-in-attribute-name"), wt._currentToken.data.push({nodeName: n, nodeValue: ""}), wt.setState(q)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentToken.data.push({nodeName: "�", nodeValue: ""})) : (wt._currentToken.data.push({nodeName: n, nodeValue: ""}), wt.setState(q))
                }
                return!0
            }

            function q(e) {
                var n = e.char(), s = !0, u = !1;
                n === o.EOF ? (wt._parseError("eof-in-attribute-name"), e.unget(n), wt.setState(t), u = !0) : n === "=" ? wt.setState(U) : i(n) ? (wt._currentAttribute().nodeName += n.toLowerCase(), s = !1) : n === ">" ? u = !0 : r(n) ? wt.setState(R) : n === "/" ? wt.setState(J) : n === "'" || n === '"' ? (wt._parseError("invalid-character-in-attribute-name"), wt._currentAttribute().nodeName += n, s = !1) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentAttribute().nodeName += "�") : (wt._currentAttribute().nodeName += n, s = !1);
                if (s) {
                    var a = wt._currentToken.data, f = a[a.length - 1];
                    for (var l = a.length - 2; l >= 0; l--)if (f.nodeName === a[l].nodeName) {
                        wt._parseError("duplicate-attribute", {name: f.nodeName}), f.nodeName = null;
                        break
                    }
                    u && wt._emitCurrentToken()
                } else e.commit();
                return!0
            }

            function R(e) {
                var n = e.char();
                if (n === o.EOF)wt._parseError("expected-end-of-tag-but-got-eof"), e.unget(n), wt.setState(t); else {
                    if (r(n))return!0;
                    n === "=" ? wt.setState(U) : n === ">" ? wt._emitCurrentToken() : i(n) ? (wt._currentToken.data.push({nodeName: n, nodeValue: ""}), wt.setState(q)) : n === "/" ? wt.setState(J) : n === "'" || n === '"' || n === "<" ? (wt._parseError("invalid-character-after-attribute-name"), wt._currentToken.data.push({nodeName: n, nodeValue: ""}), wt.setState(q)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentToken.data.push({nodeName: "�", nodeValue: ""})) : (wt._currentToken.data.push({nodeName: n, nodeValue: ""}), wt.setState(q))
                }
                return!0
            }

            function U(e) {
                var n = e.char();
                if (n === o.EOF)wt._parseError("expected-attribute-value-but-got-eof"), e.unget(n), wt.setState(t); else {
                    if (r(n))return!0;
                    n === '"' ? wt.setState(z) : n === "&" ? (wt.setState(X), e.unget(n)) : n === "'" ? wt.setState(W) : n === ">" ? (wt._parseError("expected-attribute-value-but-got-right-bracket"), wt._emitCurrentToken()) : n === "=" || n === "<" || n === "`" ? (wt._parseError("unexpected-character-in-unquoted-attribute-value"), wt._currentAttribute().nodeValue += n, wt.setState(X)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentAttribute().nodeValue += "�") : (wt._currentAttribute().nodeValue += n, wt.setState(X))
                }
                return!0
            }

            function z(e) {
                var n = e.char();
                if (n === o.EOF)wt._parseError("eof-in-attribute-value-double-quote"), e.unget(n), wt.setState(t); else if (n === '"')wt.setState($); else if (n === "&")this._additionalAllowedCharacter = '"', wt.setState(V); else if (n === "\0")wt._parseError("invalid-codepoint"), wt._currentAttribute().nodeValue += "�"; else {
                    var r = e.matchUntil('[\0"&]');
                    n += r, wt._currentAttribute().nodeValue += n
                }
                return!0
            }

            function W(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-attribute-value-single-quote"), e.unget(n), wt.setState(t)) : n === "'" ? wt.setState($) : n === "&" ? (this._additionalAllowedCharacter = "'", wt.setState(V)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentAttribute().nodeValue += "�") : wt._currentAttribute().nodeValue += n + e.matchUntil("\0|['&]"), !0
            }

            function X(e) {
                var n = e.char();
                if (n === o.EOF)wt._parseError("eof-after-attribute-value"), e.unget(n), wt.setState(t); else if (r(n))wt.setState(I); else if (n === "&")this._additionalAllowedCharacter = ">", wt.setState(V); else if (n === ">")wt._emitCurrentToken(); else if (n === '"' || n === "'" || n === "=" || n === "`" || n === "<")wt._parseError("unexpected-character-in-unquoted-attribute-value"), wt._currentAttribute().nodeValue += n, e.commit(); else if (n === "\0")wt._parseError("invalid-codepoint"), wt._currentAttribute().nodeValue += "�"; else {
                    var i = e.matchUntil("\0|[	\n\f \r&<>\"'=`]");
                    i === o.EOF && (wt._parseError("eof-in-attribute-value-no-quotes"), wt._emitCurrentToken()), e.commit(), wt._currentAttribute().nodeValue += n + i
                }
                return!0
            }

            function V(e) {
                var t = u.consumeEntity(e, wt, this._additionalAllowedCharacter);
                return this._currentAttribute().nodeValue += t || "&", this._additionalAllowedCharacter === '"' ? wt.setState(z) : this._additionalAllowedCharacter === "'" ? wt.setState(W) : this._additionalAllowedCharacter === ">" && wt.setState(X), !0
            }

            function $(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-after-attribute-value"), e.unget(n), wt.setState(t)) : r(n) ? wt.setState(I) : n === ">" ? (wt.setState(t), wt._emitCurrentToken()) : n === "/" ? wt.setState(J) : (wt._parseError("unexpected-character-after-attribute-value"), e.unget(n), wt.setState(I)), !0
            }

            function J(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("unexpected-eof-after-solidus-in-tag"), e.unget(n), wt.setState(t)) : n === ">" ? (wt._currentToken.selfClosing = !0, wt.setState(t), wt._emitCurrentToken()) : (wt._parseError("unexpected-character-after-solidus-in-tag"), e.unget(n), wt.setState(I)), !0
            }

            function K(e) {
                var n = e.matchUntil(">");
                return n = n.replace(/\u0000/g, "�"), e.char(), wt._emitToken({type: "Comment", data: n}), wt.setState(t), !0
            }

            function Q(e) {
                var t = e.shift(2);
                if (t === "--")wt._currentToken = {type: "Comment", data: ""}, wt.setState(Y); else {
                    var n = e.shift(5);
                    if (n === o.EOF || t === o.EOF)return wt._parseError("expected-dashes-or-doctype"), wt.setState(K), e.unget(t), !0;
                    t += n, t.toUpperCase() === "DOCTYPE" ? (wt._currentToken = {type: "Doctype", name: "", publicId: null, systemId: null, forceQuirks: !1}, wt.setState(it)) : wt._tokenHandler.isCdataSectionAllowed() && t === "[CDATA[" ? wt.setState(G) : (wt._parseError("expected-dashes-or-doctype"), e.unget(t), wt.setState(K))
                }
                return!0
            }

            function G(e) {
                var n = e.matchUntil("]]>");
                return e.shift(3), n && wt._emitToken({type: "Characters", data: n}), wt.setState(t), !0
            }

            function Y(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-comment"), wt._emitToken(wt._currentToken), e.unget(n), wt.setState(t)) : n === "-" ? wt.setState(Z) : n === ">" ? (wt._parseError("incorrect-comment"), wt._emitToken(wt._currentToken), wt.setState(t)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentToken.data += "�") : (wt._currentToken.data += n, wt.setState(et)), !0
            }

            function Z(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-comment"), wt._emitToken(wt._currentToken), e.unget(n), wt.setState(t)) : n === "-" ? wt.setState(nt) : n === ">" ? (wt._parseError("incorrect-comment"), wt._emitToken(wt._currentToken), wt.setState(t)) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentToken.data += "�") : (wt._currentToken.data += "-" + n, wt.setState(et)), !0
            }

            function et(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-comment"), wt._emitToken(wt._currentToken), e.unget(n), wt.setState(t)) : n === "-" ? wt.setState(tt) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentToken.data += "�") : (wt._currentToken.data += n, e.commit()), !0
            }

            function tt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-comment-end-dash"), wt._emitToken(wt._currentToken), e.unget(n), wt.setState(t)) : n === "-" ? wt.setState(nt) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentToken.data += "-�", wt.setState(et)) : (wt._currentToken.data += "-" + n + e.matchUntil("\0|-"), e.char()), !0
            }

            function nt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-comment-double-dash"), wt._emitToken(wt._currentToken), e.unget(n), wt.setState(t)) : n === ">" ? (wt._emitToken(wt._currentToken), wt.setState(t)) : n === "!" ? (wt._parseError("unexpected-bang-after-double-dash-in-comment"), wt.setState(rt)) : n === "-" ? (wt._parseError("unexpected-dash-after-double-dash-in-comment"), wt._currentToken.data += n) : n === "\0" ? (wt._parseError("invalid-codepoint"), wt._currentToken.data += "--�", wt.setState(et)) : (wt._parseError("unexpected-char-in-comment"), wt._currentToken.data += "--" + n, wt.setState(et)), !0
            }

            function rt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-comment-end-bang-state"), wt._emitToken(wt._currentToken), e.unget(n), wt.setState(t)) : n === ">" ? (wt._emitToken(wt._currentToken), wt.setState(t)) : n === "-" ? (wt._currentToken.data += "--!", wt.setState(tt)) : (wt._currentToken.data += "--!" + n, wt.setState(et)), !0
            }

            function it(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("expected-doctype-name-but-got-eof"), wt._currentToken.forceQuirks = !0, e.unget(n), wt.setState(t), wt._emitCurrentToken()) : r(n) ? wt.setState(st) : (wt._parseError("need-space-after-doctype"), e.unget(n), wt.setState(st)), !0
            }

            function st(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("expected-doctype-name-but-got-eof"), wt._currentToken.forceQuirks = !0, e.unget(n), wt.setState(t), wt._emitCurrentToken()) : r(n) || (n === ">" ? (wt._parseError("expected-doctype-name-but-got-right-bracket"), wt._currentToken.forceQuirks = !0, wt.setState(t), wt._emitCurrentToken()) : (i(n) && (n = n.toLowerCase()), wt._currentToken.name = n, wt.setState(ot))), !0
            }

            function ot(e) {
                var n = e.char();
                return n === o.EOF ? (wt._currentToken.forceQuirks = !0, e.unget(n), wt._parseError("eof-in-doctype-name"), wt.setState(t), wt._emitCurrentToken()) : r(n) ? wt.setState(ut) : n === ">" ? (wt.setState(t), wt._emitCurrentToken()) : (i(n) && (n = n.toLowerCase()), wt._currentToken.name += n, e.commit()), !0
            }

            function ut(e) {
                var n = e.char();
                if (n === o.EOF)wt._currentToken.forceQuirks = !0, e.unget(n), wt._parseError("eof-in-doctype"), wt.setState(t), wt._emitCurrentToken(); else if (!r(n))if (n === ">")wt.setState(t), wt._emitCurrentToken(); else {
                    if (["p", "P"].indexOf(n) > -1) {
                        var i = [
                            ["u", "U"],
                            ["b", "B"],
                            ["l", "L"],
                            ["i", "I"],
                            ["c", "C"]
                        ], s = i.every(function (t) {
                            return n = e.char(), t.indexOf(n) > -1
                        });
                        if (s)return wt.setState(at), !0
                    } else if (["s", "S"].indexOf(n) > -1) {
                        var i = [
                            ["y", "Y"],
                            ["s", "S"],
                            ["t", "T"],
                            ["e", "E"],
                            ["m", "M"]
                        ], s = i.every(function (t) {
                            return n = e.char(), t.indexOf(n) > -1
                        });
                        if (s)return wt.setState(dt), !0
                    }
                    e.unget(n), wt._currentToken.forceQuirks = !0, n === o.EOF ? (wt._parseError("eof-in-doctype"), e.unget(n), wt.setState(t), wt._emitCurrentToken()) : (wt._parseError("expected-space-or-right-bracket-in-doctype", {data: n}), wt.setState(bt))
                }
                return!0
            }

            function at(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, e.unget(n), wt.setState(t), wt._emitCurrentToken()) : r(n) ? wt.setState(ft) : n === "'" || n === '"' ? (wt._parseError("unexpected-char-in-doctype"), e.unget(n), wt.setState(ft)) : (e.unget(n), wt.setState(ft)), !0
            }

            function ft(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, e.unget(n), wt.setState(t), wt._emitCurrentToken()) : r(n) || (n === '"' ? (wt._currentToken.publicId = "", wt.setState(lt)) : n === "'" ? (wt._currentToken.publicId = "", wt.setState(ct)) : n === ">" ? (wt._parseError("unexpected-end-of-doctype"), wt._currentToken.forceQuirks = !0, wt.setState(t), wt._emitCurrentToken()) : (wt._parseError("unexpected-char-in-doctype"), wt._currentToken.forceQuirks = !0, wt.setState(bt))), !0
            }

            function lt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, e.unget(n), wt.setState(t), wt._emitCurrentToken()) : n === '"' ? wt.setState(ht) : n === ">" ? (wt._parseError("unexpected-end-of-doctype"), wt._currentToken.forceQuirks = !0, wt.setState(t), wt._emitCurrentToken()) : wt._currentToken.publicId += n, !0
            }

            function ct(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, e.unget(n), wt.setState(t), wt._emitCurrentToken()) : n === "'" ? wt.setState(ht) : n === ">" ? (wt._parseError("unexpected-end-of-doctype"), wt._currentToken.forceQuirks = !0, wt.setState(t), wt._emitCurrentToken()) : wt._currentToken.publicId += n, !0
            }

            function ht(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), e.unget(n), wt.setState(t)) : r(n) ? wt.setState(pt) : n === ">" ? (wt.setState(t), wt._emitCurrentToken()) : n === '"' ? (wt._parseError("unexpected-char-in-doctype"), wt._currentToken.systemId = "", wt.setState(mt)) : n === "'" ? (wt._parseError("unexpected-char-in-doctype"), wt._currentToken.systemId = "", wt.setState(gt)) : (wt._parseError("unexpected-char-in-doctype"), wt._currentToken.forceQuirks = !0, wt.setState(bt)), !0
            }

            function pt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), e.unget(n), wt.setState(t)) : r(n) || (n === ">" ? (wt._emitCurrentToken(), wt.setState(t)) : n === '"' ? (wt._currentToken.systemId = "", wt.setState(mt)) : n === "'" ? (wt._currentToken.systemId = "", wt.setState(gt)) : (wt._parseError("unexpected-char-in-doctype"), wt._currentToken.forceQuirks = !0, wt.setState(bt))), !0
            }

            function dt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), e.unget(n), wt.setState(t)) : r(n) ? wt.setState(vt) : n === "'" || n === '"' ? (wt._parseError("unexpected-char-in-doctype"), e.unget(n), wt.setState(vt)) : (e.unget(n), wt.setState(vt)), !0
            }

            function vt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), e.unget(n), wt.setState(t)) : r(n) || (n === '"' ? (wt._currentToken.systemId = "", wt.setState(mt)) : n === "'" ? (wt._currentToken.systemId = "", wt.setState(gt)) : n === ">" ? (wt._parseError("unexpected-end-of-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), wt.setState(t)) : (wt._parseError("unexpected-char-in-doctype"), wt._currentToken.forceQuirks = !0, wt.setState(bt))), !0
            }

            function mt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), e.unget(n), wt.setState(t)) : n === '"' ? wt.setState(yt) : n === ">" ? (wt._parseError("unexpected-end-of-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), wt.setState(t)) : wt._currentToken.systemId += n, !0
            }

            function gt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), e.unget(n), wt.setState(t)) : n === "'" ? wt.setState(yt) : n === ">" ? (wt._parseError("unexpected-end-of-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), wt.setState(t)) : wt._currentToken.systemId += n, !0
            }

            function yt(e) {
                var n = e.char();
                return n === o.EOF ? (wt._parseError("eof-in-doctype"), wt._currentToken.forceQuirks = !0, wt._emitCurrentToken(), e.unget(n), wt.setState(t)) : r(n) || (n === ">" ? (wt._emitCurrentToken(), wt.setState(t)) : (wt._parseError("unexpected-char-in-doctype"), wt.setState(bt))), !0
            }

            function bt(e) {
                var n = e.char();
                return n === o.EOF ? (e.unget(n), wt._emitCurrentToken(), wt.setState(t)) : n === ">" && (wt._emitCurrentToken(), wt.setState(t)), !0
            }

            s.DATA = t, s.RCDATA = a, s.RAWTEXT = l, s.SCRIPT_DATA = h, s.PLAINTEXT = c, this._state = s.DATA, this._inputStream.append(e), this._tokenHandler.startTokenization(this), this._inputStream.eof = !0;
            var wt = this;
            while (this._state.call(this, this._inputStream));
        }, n.Tokenizer = s
    }, {"./EntityParser": 2, "./InputStream": 3}], 6: [function (e, t, n) {
        (function () {
            function t(e) {
                return e === " " || e === "\n" || e === "	" || e === "\r" || e === "\f"
            }

            function r(e) {
                return t(e) || e === "�"
            }

            function i(e) {
                for (var n = 0; n < e.length; n++) {
                    var r = e[n];
                    if (!t(r))return!1
                }
                return!0
            }

            function s(e) {
                for (var t = 0; t < e.length; t++) {
                    var n = e[t];
                    if (!r(n))return!1
                }
                return!0
            }

            function o(e, t) {
                for (var n = 0; n < e.attributes.length; n++) {
                    var r = e.attributes[n];
                    if (r.nodeName === t)return r
                }
                return null
            }

            function u(e) {
                this.characters = e, this.current = 0, this.end = this.characters.length
            }

            function a() {
                this.tokenizer = null, this.errorHandler = null, this.scriptingEnabled = !1, this.document = null, this.head = null, this.form = null, this.openElements = new v, this.activeFormattingElements = [], this.insertionMode = null, this.insertionModeName = "", this.originalInsertionMode = "", this.inQuirksMode = !1, this.compatMode = "no quirks", this.framesetOk = !0, this.redirectAttachToFosterParent = !1, this.selfClosingFlagAcknowledged = !1, this.context = "", this.firstStartTag = !1, this.pendingTableCharacters = [], this.shouldSkipLeadingNewline = !1;
                var e = this, n = this.insertionModes = {};
                n.base = {end_tag_handlers: {"-default": "endTagOther"}, start_tag_handlers: {"-default": "startTagOther"}, processEOF: function () {
                    e.generateImpliedEndTags(), e.openElements.length > 2 ? e.parseError("expected-closing-tag-but-got-eof") : e.openElements.length == 2 && e.openElements.item(1).localName != "body" ? e.parseError("expected-closing-tag-but-got-eof") : e.context && e.openElements.length > 1
                }, processComment: function (t) {
                    e.insertComment(t, e.currentStackItem().node)
                }, processDoctype: function (t, n, r, i) {
                    e.parseError("unexpected-doctype")
                }, processStartTag: function (e, t, n) {
                    if (this[this.start_tag_handlers[e]])this[this.start_tag_handlers[e]](e, t, n); else {
                        if (!this[this.start_tag_handlers["-default"]])throw new Error("No handler found for " + e);
                        this[this.start_tag_handlers["-default"]](e, t, n)
                    }
                }, processEndTag: function (e) {
                    if (this[this.end_tag_handlers[e]])this[this.end_tag_handlers[e]](e); else {
                        if (!this[this.end_tag_handlers["-default"]])throw new Error("No handler found for " + e);
                        this[this.end_tag_handlers["-default"]](e)
                    }
                }, startTagHtml: function (t, n) {
                    !e.firstStartTag && t == "html" && e.parseError("non-html-root"), e.addAttributesToElement(e.openElements.rootNode, n), e.firstStartTag = !1
                }}, n.initial = Object.create(n.base), n.initial.processEOF = function () {
                    e.parseError("expected-doctype-but-got-eof"), this.anythingElse(), e.insertionMode.processEOF()
                }, n.initial.processComment = function (t) {
                    e.insertComment(t, e.document)
                }, n.initial.processDoctype = function (t, n, r, i) {
                    function s(e) {
                        return n.toLowerCase().indexOf(e) === 0
                    }

                    e.insertDoctype(t || "", n || "", r || ""), i || t != "html" || n != null && (["+//silmaril//dtd html pro v0r11 19970101//", "-//advasoft ltd//dtd html 3.0 aswedit + extensions//", "-//as//dtd html 3.0 aswedit + extensions//", "-//ietf//dtd html 2.0 level 1//", "-//ietf//dtd html 2.0 level 2//", "-//ietf//dtd html 2.0 strict level 1//", "-//ietf//dtd html 2.0 strict level 2//", "-//ietf//dtd html 2.0 strict//", "-//ietf//dtd html 2.0//", "-//ietf//dtd html 2.1e//", "-//ietf//dtd html 3.0//", "-//ietf//dtd html 3.0//", "-//ietf//dtd html 3.2 final//", "-//ietf//dtd html 3.2//", "-//ietf//dtd html 3//", "-//ietf//dtd html level 0//", "-//ietf//dtd html level 0//", "-//ietf//dtd html level 1//", "-//ietf//dtd html level 1//", "-//ietf//dtd html level 2//", "-//ietf//dtd html level 2//", "-//ietf//dtd html level 3//", "-//ietf//dtd html level 3//", "-//ietf//dtd html strict level 0//", "-//ietf//dtd html strict level 0//", "-//ietf//dtd html strict level 1//", "-//ietf//dtd html strict level 1//", "-//ietf//dtd html strict level 2//", "-//ietf//dtd html strict level 2//", "-//ietf//dtd html strict level 3//", "-//ietf//dtd html strict level 3//", "-//ietf//dtd html strict//", "-//ietf//dtd html strict//", "-//ietf//dtd html strict//", "-//ietf//dtd html//", "-//ietf//dtd html//", "-//ietf//dtd html//", "-//metrius//dtd metrius presentational//", "-//microsoft//dtd internet explorer 2.0 html strict//", "-//microsoft//dtd internet explorer 2.0 html//", "-//microsoft//dtd internet explorer 2.0 tables//", "-//microsoft//dtd internet explorer 3.0 html strict//", "-//microsoft//dtd internet explorer 3.0 html//", "-//microsoft//dtd internet explorer 3.0 tables//", "-//netscape comm. corp.//dtd html//", "-//netscape comm. corp.//dtd strict html//", "-//o'reilly and associates//dtd html 2.0//", "-//o'reilly and associates//dtd html extended 1.0//", "-//spyglass//dtd html 2.0 extended//", "-//sq//dtd html 2.0 hotmetal + extensions//", "-//sun microsystems corp.//dtd hotjava html//", "-//sun microsystems corp.//dtd hotjava strict html//", "-//w3c//dtd html 3 1995-03-24//", "-//w3c//dtd html 3.2 draft//", "-//w3c//dtd html 3.2 final//", "-//w3c//dtd html 3.2//", "-//w3c//dtd html 3.2s draft//", "-//w3c//dtd html 4.0 frameset//", "-//w3c//dtd html 4.0 transitional//", "-//w3c//dtd html experimental 19960712//", "-//w3c//dtd html experimental 970421//", "-//w3c//dtd w3 html//", "-//w3o//dtd w3 html 3.0//", "-//webtechs//dtd mozilla html 2.0//", "-//webtechs//dtd mozilla html//", "html"].some(s) || ["-//w3o//dtd w3 html strict 3.0//en//", "-/w3c/dtd html 4.0 transitional/en", "html"].indexOf(n.toLowerCase()) > -1 || r == null && ["-//w3c//dtd html 4.01 transitional//", "-//w3c//dtd html 4.01 frameset//"].some(s)) || r != null && r.toLowerCase() == "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd" ? (e.compatMode = "quirks", e.parseError("quirky-doctype")) : n != null && (["-//w3c//dtd xhtml 1.0 transitional//", "-//w3c//dtd xhtml 1.0 frameset//"].some(s) || r != null && ["-//w3c//dtd html 4.01 transitional//", "-//w3c//dtd html 4.01 frameset//"].indexOf(n.toLowerCase()) > -1) ? (e.compatMode = "limited quirks", e.parseError("almost-standards-doctype")) : n == "-//W3C//DTD HTML 4.0//EN" && (r == null || r == "http://www.w3.org/TR/REC-html40/strict.dtd") || n == "-//W3C//DTD HTML 4.01//EN" && (r == null || r == "http://www.w3.org/TR/html4/strict.dtd") || n == "-//W3C//DTD XHTML 1.0 Strict//EN" && r == "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" || n == "-//W3C//DTD XHTML 1.1//EN" && r == "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd" || (r != null && r != "about:legacy-compat" || n != null) && e.parseError("unknown-doctype"), e.setInsertionMode("beforeHTML")
                }, n.initial.processCharacters = function (t) {
                    t.skipLeadingWhitespace();
                    if (!t.length)return;
                    e.parseError("expected-doctype-but-got-chars"), this.anythingElse(), e.insertionMode.processCharacters(t)
                }, n.initial.processStartTag = function (t, n, r) {
                    e.parseError("expected-doctype-but-got-start-tag", {name: t}), this.anythingElse(), e.insertionMode.processStartTag(t, n, r)
                }, n.initial.processEndTag = function (t) {
                    e.parseError("expected-doctype-but-got-end-tag", {name: t}), this.anythingElse(), e.insertionMode.processEndTag(t)
                }, n.initial.anythingElse = function () {
                    e.compatMode = "quirks", e.setInsertionMode("beforeHTML")
                }, n.beforeHTML = Object.create(n.base), n.beforeHTML.start_tag_handlers = {html: "startTagHtml", "-default": "startTagOther"}, n.beforeHTML.processEOF = function () {
                    this.anythingElse(), e.insertionMode.processEOF()
                }, n.beforeHTML.processComment = function (t) {
                    e.insertComment(t, e.document)
                }, n.beforeHTML.processCharacters = function (t) {
                    t.skipLeadingWhitespace();
                    if (!t.length)return;
                    this.anythingElse(), e.insertionMode.processCharacters(t)
                }, n.beforeHTML.startTagHtml = function (t, n, r) {
                    e.firstStartTag = !0, e.insertHtmlElement(n), e.setInsertionMode("beforeHead")
                }, n.beforeHTML.startTagOther = function (t, n, r) {
                    this.anythingElse(), e.insertionMode.processStartTag(t, n, r)
                }, n.beforeHTML.processEndTag = function (t) {
                    this.anythingElse(), e.insertionMode.processEndTag(t)
                }, n.beforeHTML.anythingElse = function () {
                    e.insertHtmlElement(), e.setInsertionMode("beforeHead")
                }, n.afterAfterBody = Object.create(n.base), n.afterAfterBody.start_tag_handlers = {html: "startTagHtml", "-default": "startTagOther"}, n.afterAfterBody.processComment = function (t) {
                    e.insertComment(t, e.document)
                }, n.afterAfterBody.processDoctype = function (e) {
                    n.inBody.processDoctype(e)
                }, n.afterAfterBody.startTagHtml = function (e, t) {
                    n.inBody.startTagHtml(e, t)
                }, n.afterAfterBody.startTagOther = function (t, n, r) {
                    e.parseError("unexpected-start-tag", {name: t}), e.setInsertionMode("inBody"), e.insertionMode.processStartTag(t, n, r)
                }, n.afterAfterBody.endTagOther = function (t) {
                    e.parseError("unexpected-end-tag", {name: t}), e.setInsertionMode("inBody"), e.insertionMode.processEndTag(t)
                }, n.afterAfterBody.processCharacters = function (t) {
                    if (!i(t.characters))return e.parseError("unexpected-char-after-body"), e.setInsertionMode("inBody"), e.insertionMode.processCharacters(t);
                    n.inBody.processCharacters(t)
                }, n.afterBody = Object.create(n.base), n.afterBody.end_tag_handlers = {html: "endTagHtml", "-default": "endTagOther"}, n.afterBody.processComment = function (t) {
                    e.insertComment(t, e.openElements.rootNode)
                }, n.afterBody.processCharacters = function (t) {
                    if (!i(t.characters))return e.parseError("unexpected-char-after-body"), e.setInsertionMode("inBody"), e.insertionMode.processCharacters(t);
                    n.inBody.processCharacters(t)
                }, n.afterBody.processStartTag = function (t, n, r) {
                    e.parseError("unexpected-start-tag-after-body", {name: t}), e.setInsertionMode("inBody"), e.insertionMode.processStartTag(t, n, r)
                }, n.afterBody.endTagHtml = function (t) {
                    e.context ? e.parseError("end-html-in-innerhtml") : e.setInsertionMode("afterAfterBody")
                }, n.afterBody.endTagOther = function (t) {
                    e.parseError("unexpected-end-tag-after-body", {name: t}), e.setInsertionMode("inBody"), e.insertionMode.processEndTag(t)
                }, n.afterFrameset = Object.create(n.base), n.afterFrameset.start_tag_handlers = {html: "startTagHtml", noframes: "startTagNoframes", "-default": "startTagOther"}, n.afterFrameset.end_tag_handlers = {html: "endTagHtml", "-default": "endTagOther"}, n.afterFrameset.processCharacters = function (n) {
                    var r = n.takeRemaining(), i = "";
                    for (var s = 0; s < r.length; s++) {
                        var o = r[s];
                        t(o) && (i += o)
                    }
                    i && e.insertText(i), i.length < r.length && e.parseError("expected-eof-but-got-char")
                }, n.afterFrameset.startTagNoframes = function (e, t) {
                    n.inHead.processStartTag(e, t)
                }, n.afterFrameset.startTagOther = function (t, n) {
                    e.parseError("unexpected-start-tag-after-frameset", {name: t})
                }, n.afterFrameset.endTagHtml = function (t) {
                    e.setInsertionMode("afterAfterFrameset")
                }, n.afterFrameset.endTagOther = function (t) {
                    e.parseError("unexpected-end-tag-after-frameset", {name: t})
                }, n.beforeHead = Object.create(n.base), n.beforeHead.start_tag_handlers = {html: "startTagHtml", head: "startTagHead", "-default": "startTagOther"}, n.beforeHead.end_tag_handlers = {html: "endTagImplyHead", head: "endTagImplyHead", body: "endTagImplyHead", br: "endTagImplyHead", "-default": "endTagOther"}, n.beforeHead.processEOF = function () {
                    this.startTagHead("head", []), e.insertionMode.processEOF()
                }, n.beforeHead.processCharacters = function (t) {
                    t.skipLeadingWhitespace();
                    if (!t.length)return;
                    this.startTagHead("head", []), e.insertionMode.processCharacters(t)
                }, n.beforeHead.startTagHead = function (t, n) {
                    e.insertHeadElement(n), e.setInsertionMode("inHead")
                }, n.beforeHead.startTagOther = function (t, n, r) {
                    this.startTagHead("head", []), e.insertionMode.processStartTag(t, n, r)
                }, n.beforeHead.endTagImplyHead = function (t) {
                    this.startTagHead("head", []), e.insertionMode.processEndTag(t)
                }, n.beforeHead.endTagOther = function (t) {
                    e.parseError("end-tag-after-implied-root", {name: t})
                }, n.inHead = Object.create(n.base), n.inHead.start_tag_handlers = {html: "startTagHtml", head: "startTagHead", title: "startTagTitle", script: "startTagScript", style: "startTagNoFramesStyle", noscript: "startTagNoScript", noframes: "startTagNoFramesStyle", base: "startTagBaseLinkCommand", basefont: "startTagBaseLinkCommand", bgsound: "startTagBaseLinkCommand", command: "startTagBaseLinkCommand", link: "startTagBaseLinkCommand", meta: "startTagMeta", "-default": "startTagOther"}, n.inHead.end_tag_handlers = {head: "endTagHead", html: "endTagHtmlBodyBr", body: "endTagHtmlBodyBr", br: "endTagHtmlBodyBr", "-default": "endTagOther"}, n.inHead.processEOF = function () {
                    var t = e.currentStackItem().localName;
                    ["title", "style", "script"].indexOf(t) != -1 && (e.parseError("expected-named-closing-tag-but-got-eof", {name: t}), e.popElement()), this.anythingElse(), e.insertionMode.processEOF()
                }, n.inHead.processCharacters = function (t) {
                    var n = t.takeLeadingWhitespace();
                    n && e.insertText(n);
                    if (!t.length)return;
                    this.anythingElse(), e.insertionMode.processCharacters(t)
                }, n.inHead.startTagHtml = function (e, t) {
                    n.inBody.processStartTag(e, t)
                }, n.inHead.startTagHead = function (t, n) {
                    e.parseError("two-heads-are-not-better-than-one")
                }, n.inHead.startTagTitle = function (t, n) {
                    e.processGenericRCDATAStartTag(t, n)
                }, n.inHead.startTagNoScript = function (t, n) {
                    if (e.scriptingEnabled)return e.processGenericRawTextStartTag(t, n);
                    e.insertElement(t, n), e.setInsertionMode("inHeadNoscript")
                }, n.inHead.startTagNoFramesStyle = function (t, n) {
                    e.processGenericRawTextStartTag(t, n)
                }, n.inHead.startTagScript = function (t, n) {
                    e.insertElement(t, n), e.tokenizer.setState(d.SCRIPT_DATA), e.originalInsertionMode = e.insertionModeName, e.setInsertionMode("text")
                }, n.inHead.startTagBaseLinkCommand = function (t, n) {
                    e.insertSelfClosingElement(t, n)
                }, n.inHead.startTagMeta = function (t, n) {
                    e.insertSelfClosingElement(t, n)
                }, n.inHead.startTagOther = function (t, n, r) {
                    this.anythingElse(), e.insertionMode.processStartTag(t, n, r)
                }, n.inHead.endTagHead = function (t) {
                    e.openElements.item(e.openElements.length - 1).localName == "head" ? e.openElements.pop() : e.parseError("unexpected-end-tag", {name: "head"}), e.setInsertionMode("afterHead")
                }, n.inHead.endTagHtmlBodyBr = function (t) {
                    this.anythingElse(), e.insertionMode.processEndTag(t)
                }, n.inHead.endTagOther = function (t) {
                    e.parseError("unexpected-end-tag", {name: t})
                }, n.inHead.anythingElse = function () {
                    this.endTagHead("head")
                }, n.afterHead = Object.create(n.base), n.afterHead.start_tag_handlers = {html: "startTagHtml", head: "startTagHead", body: "startTagBody", frameset: "startTagFrameset", base: "startTagFromHead", link: "startTagFromHead", meta: "startTagFromHead", script: "startTagFromHead", style: "startTagFromHead", title: "startTagFromHead", "-default": "startTagOther"}, n.afterHead.end_tag_handlers = {body: "endTagBodyHtmlBr", html: "endTagBodyHtmlBr", br: "endTagBodyHtmlBr", "-default": "endTagOther"}, n.afterHead.processEOF = function () {
                    this.anythingElse(), e.insertionMode.processEOF()
                }, n.afterHead.processCharacters = function (t) {
                    var n = t.takeLeadingWhitespace();
                    n && e.insertText(n);
                    if (!t.length)return;
                    this.anythingElse(), e.insertionMode.processCharacters(t)
                }, n.afterHead.startTagHtml = function (e, t) {
                    n.inBody.processStartTag(e, t)
                }, n.afterHead.startTagBody = function (t, n) {
                    e.framesetOk = !1, e.insertBodyElement(n), e.setInsertionMode("inBody")
                }, n.afterHead.startTagFrameset = function (t, n) {
                    e.insertElement(t, n), e.setInsertionMode("inFrameset")
                }, n.afterHead.startTagFromHead = function (t, r, i) {
                    e.parseError("unexpected-start-tag-out-of-my-head", {name: t}), e.openElements.push(e.head), n.inHead.processStartTag(t, r, i), e.openElements.remove(e.head)
                }, n.afterHead.startTagHead = function (t, n, r) {
                    e.parseError("unexpected-start-tag", {name: t})
                }, n.afterHead.startTagOther = function (t, n, r) {
                    this.anythingElse(), e.insertionMode.processStartTag(t, n, r)
                }, n.afterHead.endTagBodyHtmlBr = function (t) {
                    this.anythingElse(), e.insertionMode.processEndTag(t)
                }, n.afterHead.endTagOther = function (t) {
                    e.parseError("unexpected-end-tag", {name: t})
                }, n.afterHead.anythingElse = function () {
                    e.insertBodyElement([]), e.setInsertionMode("inBody"), e.framesetOk = !0
                }, n.inBody = Object.create(n.base), n.inBody.start_tag_handlers = {html: "startTagHtml", head: "startTagMisplaced", base: "startTagProcessInHead", basefont: "startTagProcessInHead", bgsound: "startTagProcessInHead", command: "startTagProcessInHead", link: "startTagProcessInHead", meta: "startTagProcessInHead", noframes: "startTagProcessInHead", script: "startTagProcessInHead", style: "startTagProcessInHead", title: "startTagProcessInHead", body: "startTagBody", form: "startTagForm", plaintext: "startTagPlaintext", a: "startTagA", button: "startTagButton", xmp: "startTagXmp", table: "startTagTable", hr: "startTagHr", image: "startTagImage", input: "startTagInput", textarea: "startTagTextarea", select: "startTagSelect", isindex: "startTagIsindex", applet: "startTagAppletMarqueeObject", marquee: "startTagAppletMarqueeObject", object: "startTagAppletMarqueeObject", li: "startTagListItem", dd: "startTagListItem", dt: "startTagListItem", address: "startTagCloseP", article: "startTagCloseP", aside: "startTagCloseP", blockquote: "startTagCloseP", center: "startTagCloseP", details: "startTagCloseP", dir: "startTagCloseP", div: "startTagCloseP", dl: "startTagCloseP", fieldset: "startTagCloseP", figcaption: "startTagCloseP", figure: "startTagCloseP", footer: "startTagCloseP", header: "startTagCloseP", hgroup: "startTagCloseP", main: "startTagCloseP", menu: "startTagCloseP", nav: "startTagCloseP", ol: "startTagCloseP", p: "startTagCloseP", section: "startTagCloseP", summary: "startTagCloseP", ul: "startTagCloseP", listing: "startTagPreListing", pre: "startTagPreListing", b: "startTagFormatting", big: "startTagFormatting", code: "startTagFormatting", em: "startTagFormatting", font: "startTagFormatting", i: "startTagFormatting", s: "startTagFormatting", small: "startTagFormatting", strike: "startTagFormatting", strong: "startTagFormatting", tt: "startTagFormatting", u: "startTagFormatting", nobr: "startTagNobr", area: "startTagVoidFormatting", br: "startTagVoidFormatting", embed: "startTagVoidFormatting", img: "startTagVoidFormatting", keygen: "startTagVoidFormatting", wbr: "startTagVoidFormatting", param: "startTagParamSourceTrack", source: "startTagParamSourceTrack", track: "startTagParamSourceTrack", iframe: "startTagIFrame", noembed: "startTagRawText", noscript: "startTagRawText", h1: "startTagHeading", h2: "startTagHeading", h3: "startTagHeading", h4: "startTagHeading", h5: "startTagHeading", h6: "startTagHeading", caption: "startTagMisplaced", col: "startTagMisplaced", colgroup: "startTagMisplaced", frame: "startTagMisplaced", frameset: "startTagFrameset", tbody: "startTagMisplaced", td: "startTagMisplaced", tfoot: "startTagMisplaced", th: "startTagMisplaced", thead: "startTagMisplaced", tr: "startTagMisplaced", option: "startTagOptionOptgroup", optgroup: "startTagOptionOptgroup", math: "startTagMath", svg: "startTagSVG", rt: "startTagRpRt", rp: "startTagRpRt", "-default": "startTagOther"}, n.inBody.end_tag_handlers = {p: "endTagP", body: "endTagBody", html: "endTagHtml", address: "endTagBlock", article: "endTagBlock", aside: "endTagBlock", blockquote: "endTagBlock", button: "endTagBlock", center: "endTagBlock", details: "endTagBlock", dir: "endTagBlock", div: "endTagBlock", dl: "endTagBlock", fieldset: "endTagBlock", figcaption: "endTagBlock", figure: "endTagBlock", footer: "endTagBlock", header: "endTagBlock", hgroup: "endTagBlock", listing: "endTagBlock", main: "endTagBlock", menu: "endTagBlock", nav: "endTagBlock", ol: "endTagBlock", pre: "endTagBlock", section: "endTagBlock", summary: "endTagBlock", ul: "endTagBlock", form: "endTagForm", applet: "endTagAppletMarqueeObject", marquee: "endTagAppletMarqueeObject", object: "endTagAppletMarqueeObject", dd: "endTagListItem", dt: "endTagListItem", li: "endTagListItem", h1: "endTagHeading", h2: "endTagHeading", h3: "endTagHeading", h4: "endTagHeading", h5: "endTagHeading", h6: "endTagHeading", a: "endTagFormatting", b: "endTagFormatting", big: "endTagFormatting", code: "endTagFormatting", em: "endTagFormatting", font: "endTagFormatting", i: "endTagFormatting", nobr: "endTagFormatting", s: "endTagFormatting", small: "endTagFormatting", strike: "endTagFormatting", strong: "endTagFormatting", tt: "endTagFormatting", u: "endTagFormatting", br: "endTagBr", "-default": "endTagOther"}, n.inBody.processCharacters = function (t) {
                    e.shouldSkipLeadingNewline && (e.shouldSkipLeadingNewline = !1, t.skipAtMostOneLeadingNewline()), e.reconstructActiveFormattingElements();
                    var n = t.takeRemaining();
                    n = n.replace(/\u0000/g, function (t, n) {
                        return e.parseError("invalid-codepoint"), ""
                    });
                    if (!n)return;
                    e.insertText(n), e.framesetOk && !s(n) && (e.framesetOk = !1)
                }, n.inBody.startTagProcessInHead = function (e, t) {
                    n.inHead.processStartTag(e, t)
                }, n.inBody.startTagBody = function (t, n) {
                    e.parseError("unexpected-start-tag", {name: "body"}), e.openElements.length == 1 || e.openElements.item(1).localName != "body" ? l.ok(e.context) : (e.framesetOk = !1, e.addAttributesToElement(e.openElements.bodyElement, n))
                }, n.inBody.startTagFrameset = function (t, n) {
                    e.parseError("unexpected-start-tag", {name: "frameset"});
                    if (e.openElements.length == 1 || e.openElements.item(1).localName != "body")l.ok(e.context); else if (e.framesetOk) {
                        e.detachFromParent(e.openElements.bodyElement);
                        while (e.openElements.length > 1)e.openElements.pop();
                        e.insertElement(t, n), e.setInsertionMode("inFrameset")
                    }
                }, n.inBody.startTagCloseP = function (t, n) {
                    e.openElements.inButtonScope("p") && this.endTagP("p"), e.insertElement(t, n)
                }, n.inBody.startTagPreListing = function (t, n) {
                    e.openElements.inButtonScope("p") && this.endTagP("p"), e.insertElement(t, n), e.framesetOk = !1, e.shouldSkipLeadingNewline = !0
                }, n.inBody.startTagForm = function (t, n) {
                    e.form ? e.parseError("unexpected-start-tag", {name: t}) : (e.openElements.inButtonScope("p") && this.endTagP("p"), e.insertElement(t, n), e.form = e.currentStackItem())
                }, n.inBody.startTagRpRt = function (t, n) {
                    e.openElements.inScope("ruby") && (e.generateImpliedEndTags(), e.currentStackItem().localName != "ruby" && e.parseError("unexpected-start-tag", {name: t})), e.insertElement(t, n)
                }, n.inBody.startTagListItem = function (t, n) {
                    var r = {li: ["li"], dd: ["dd", "dt"], dt: ["dd", "dt"]}, i = r[t], s = e.openElements;
                    for (var o = s.length - 1; o >= 0; o--) {
                        var u = s.item(o);
                        if (i.indexOf(u.localName) != -1) {
                            e.insertionMode.processEndTag(u.localName);
                            break
                        }
                        if (u.isSpecial() && u.localName !== "p" && u.localName !== "address" && u.localName !== "div")break
                    }
                    e.openElements.inButtonScope("p") && this.endTagP("p"), e.insertElement(t, n), e.framesetOk = !1
                }, n.inBody.startTagPlaintext = function (t, n) {
                    e.openElements.inButtonScope("p") && this.endTagP("p"), e.insertElement(t, n), e.tokenizer.setState(d.PLAINTEXT)
                }, n.inBody.startTagHeading = function (t, n) {
                    e.openElements.inButtonScope("p") && this.endTagP("p"), e.currentStackItem().isNumberedHeader() && (e.parseError("unexpected-start-tag", {name: t}), e.popElement()), e.insertElement(t, n)
                }, n.inBody.startTagA = function (t, n) {
                    var r = e.elementInActiveFormattingElements("a");
                    r && (e.parseError("unexpected-start-tag-implies-end-tag", {startName: "a", endName: "a"}), e.adoptionAgencyEndTag("a"), e.openElements.contains(r) && e.openElements.remove(r), e.removeElementFromActiveFormattingElements(r)), e.reconstructActiveFormattingElements(), e.insertFormattingElement(t, n)
                }, n.inBody.startTagFormatting = function (t, n) {
                    e.reconstructActiveFormattingElements(), e.insertFormattingElement(t, n)
                }, n.inBody.startTagNobr = function (t, n) {
                    e.reconstructActiveFormattingElements(), e.openElements.inScope("nobr") && (e.parseError("unexpected-start-tag-implies-end-tag", {startName: "nobr", endName: "nobr"}), this.processEndTag("nobr"), e.reconstructActiveFormattingElements()), e.insertFormattingElement(t, n)
                }, n.inBody.startTagButton = function (t, n) {
                    e.openElements.inScope("button") ? (e.parseError("unexpected-start-tag-implies-end-tag", {startName: "button", endName: "button"}), this.processEndTag("button"), e.insertionMode.processStartTag(t, n)) : (e.framesetOk = !1, e.reconstructActiveFormattingElements(), e.insertElement(t, n))
                }, n.inBody.startTagAppletMarqueeObject = function (t, n) {
                    e.reconstructActiveFormattingElements(), e.insertElement(t, n), e.activeFormattingElements.push(g), e.framesetOk = !1
                },n.inBody.endTagAppletMarqueeObject = function (t) {
                    e.openElements.inScope(t) ? (e.generateImpliedEndTags(), e.currentStackItem().localName != t && e.parseError("end-tag-too-early", {name: t}), e.openElements.popUntilPopped(t), e.clearActiveFormattingElements()) : e.parseError("unexpected-end-tag", {name: t})
                },n.inBody.startTagXmp = function (t, n) {
                    e.openElements.inButtonScope("p") && this.processEndTag("p"), e.reconstructActiveFormattingElements(), e.processGenericRawTextStartTag(t, n), e.framesetOk = !1
                },n.inBody.startTagTable = function (t, n) {
                    e.compatMode !== "quirks" && e.openElements.inButtonScope("p") && this.processEndTag("p"), e.insertElement(t, n), e.setInsertionMode("inTable"), e.framesetOk = !1
                },n.inBody.startTagVoidFormatting = function (t, n) {
                    e.reconstructActiveFormattingElements(), e.insertSelfClosingElement(t, n), e.framesetOk = !1
                },n.inBody.startTagParamSourceTrack = function (t, n) {
                    e.insertSelfClosingElement(t, n)
                },n.inBody.startTagHr = function (t, n) {
                    e.openElements.inButtonScope("p") && this.endTagP("p"), e.insertSelfClosingElement(t, n), e.framesetOk = !1
                },n.inBody.startTagImage = function (t, n) {
                    e.parseError("unexpected-start-tag-treated-as", {originalName: "image", newName: "img"}), this.processStartTag("img", n)
                },n.inBody.startTagInput = function (t, n) {
                    var r = e.framesetOk;
                    this.startTagVoidFormatting(t, n);
                    for (var i in n)if (n[i].nodeName == "type") {
                        n[i].nodeValue.toLowerCase() == "hidden" && (e.framesetOk = r);
                        break
                    }
                },n.inBody.startTagIsindex = function (t, n) {
                    e.parseError("deprecated-tag", {name: "isindex"}), e.selfClosingFlagAcknowledged = !0;
                    if (e.form)return;
                    var r = [], i = [], s = "This is a searchable index. Enter search keywords: ";
                    for (var o in n)switch (n[o].nodeName) {
                        case"action":
                            r.push({nodeName: "action", nodeValue: n[o].nodeValue});
                            break;
                        case"prompt":
                            s = n[o].nodeValue;
                            break;
                        case"name":
                            break;
                        default:
                            i.push({nodeName: n[o].nodeName, nodeValue: n[o].nodeValue})
                    }
                    i.push({nodeName: "name", nodeValue: "isindex"}), this.processStartTag("form", r), this.processStartTag("hr"), this.processStartTag("label"), this.processCharacters(new u(s)), this.processStartTag("input", i), this.processEndTag("label"), this.processStartTag("hr"), this.processEndTag("form")
                },n.inBody.startTagTextarea = function (t, n) {
                    e.insertElement(t, n), e.tokenizer.setState(d.RCDATA), e.originalInsertionMode = e.insertionModeName, e.shouldSkipLeadingNewline = !0, e.framesetOk = !1, e.setInsertionMode("text")
                },n.inBody.startTagIFrame = function (t, n) {
                    e.framesetOk = !1, this.startTagRawText(t, n)
                },n.inBody.startTagRawText = function (t, n) {
                    e.processGenericRawTextStartTag(t, n)
                },n.inBody.startTagSelect = function (t, n) {
                    e.reconstructActiveFormattingElements(), e.insertElement(t, n), e.framesetOk = !1;
                    var r = e.insertionModeName;
                    r == "inTable" || r == "inCaption" || r == "inColumnGroup" || r == "inTableBody" || r == "inRow" || r == "inCell" ? e.setInsertionMode("inSelectInTable") : e.setInsertionMode("inSelect")
                },n.inBody.startTagMisplaced = function (t, n) {
                    e.parseError("unexpected-start-tag-ignored", {name: t})
                },n.inBody.endTagMisplaced = function (t) {
                    e.parseError("unexpected-end-tag", {name: t})
                },n.inBody.endTagBr = function (t) {
                    e.parseError("unexpected-end-tag-treated-as", {originalName: "br", newName: "br element"}), e.reconstructActiveFormattingElements(), e.insertElement(t, []), e.popElement()
                },n.inBody.startTagOptionOptgroup = function (t, n) {
                    e.currentStackItem().localName == "option" && e.popElement(), e.reconstructActiveFormattingElements(), e.insertElement(t, n)
                },n.inBody.startTagOther = function (t, n) {
                    e.reconstructActiveFormattingElements(), e.insertElement(t, n)
                },n.inBody.endTagOther = function (t) {
                    var n;
                    for (var r = e.openElements.length - 1; r > 0; r--) {
                        n = e.openElements.item(r);
                        if (n.localName == t) {
                            e.generateImpliedEndTags(t), e.currentStackItem().localName != t && e.parseError("unexpected-end-tag", {name: t}), e.openElements.remove_openElements_until(function (e) {
                                return e === n
                            });
                            break
                        }
                        if (n.isSpecial()) {
                            e.parseError("unexpected-end-tag", {name: t});
                            break
                        }
                    }
                },n.inBody.startTagMath = function (t, n, r) {
                    e.reconstructActiveFormattingElements(), n = e.adjustMathMLAttributes(n), n = e.adjustForeignAttributes(n), e.insertForeignElement(t, n, "http://www.w3.org/1998/Math/MathML", r)
                },n.inBody.startTagSVG = function (t, n, r) {
                    e.reconstructActiveFormattingElements(), n = e.adjustSVGAttributes(n), n = e.adjustForeignAttributes(n), e.insertForeignElement(t, n, "http://www.w3.org/2000/svg", r)
                },n.inBody.endTagP = function (t) {
                    e.openElements.inButtonScope("p") ? (e.generateImpliedEndTags("p"), e.currentStackItem().localName != "p" && e.parseError("unexpected-end-tag", {name: "p"}), e.openElements.popUntilPopped(t)) : (e.parseError("unexpected-end-tag", {name: "p"}), this.startTagCloseP("p", []), this.endTagP("p"))
                },n.inBody.endTagBody = function (t) {
                    if (!e.openElements.inScope("body")) {
                        e.parseError("unexpected-end-tag", {name: t});
                        return
                    }
                    e.currentStackItem().localName != "body" && e.parseError("expected-one-end-tag-but-got-another", {expectedName: e.currentStackItem().localName, gotName: t}), e.setInsertionMode("afterBody")
                },n.inBody.endTagHtml = function (t) {
                    if (!e.openElements.inScope("body")) {
                        e.parseError("unexpected-end-tag", {name: t});
                        return
                    }
                    e.currentStackItem().localName != "body" && e.parseError("expected-one-end-tag-but-got-another", {expectedName: e.currentStackItem().localName, gotName: t}), e.setInsertionMode("afterBody"), e.insertionMode.processEndTag(t)
                },n.inBody.endTagBlock = function (t) {
                    e.openElements.inScope(t) ? (e.generateImpliedEndTags(), e.currentStackItem().localName != t && e.parseError("end-tag-too-early", {name: t}), e.openElements.popUntilPopped(t)) : e.parseError("unexpected-end-tag", {name: t})
                },n.inBody.endTagForm = function (t) {
                    var n = e.form;
                    e.form = null, !n || !e.openElements.inScope(t) ? e.parseError("unexpected-end-tag", {name: t}) : (e.generateImpliedEndTags(), e.currentStackItem() != n && e.parseError("end-tag-too-early-ignored", {name: "form"}), e.openElements.remove(n))
                },n.inBody.endTagListItem = function (t) {
                    e.openElements.inListItemScope(t) ? (e.generateImpliedEndTags(t), e.currentStackItem().localName != t && e.parseError("end-tag-too-early", {name: t}), e.openElements.popUntilPopped(t)) : e.parseError("unexpected-end-tag", {name: t})
                },n.inBody.endTagHeading = function (t) {
                    if (!e.openElements.hasNumberedHeaderElementInScope()) {
                        e.parseError("unexpected-end-tag", {name: t});
                        return
                    }
                    e.generateImpliedEndTags(), e.currentStackItem().localName != t && e.parseError("end-tag-too-early", {name: t}), e.openElements.remove_openElements_until(function (e) {
                        return e.isNumberedHeader()
                    })
                },n.inBody.endTagFormatting = function (t, n) {
                    e.adoptionAgencyEndTag(t) || this.endTagOther(t, n)
                },n.inCaption = Object.create(n.base),n.inCaption.start_tag_handlers = {html: "startTagHtml", caption: "startTagTableElement", col: "startTagTableElement", colgroup: "startTagTableElement", tbody: "startTagTableElement", td: "startTagTableElement", tfoot: "startTagTableElement", thead: "startTagTableElement", tr: "startTagTableElement", "-default": "startTagOther"},n.inCaption.end_tag_handlers = {caption: "endTagCaption", table: "endTagTable", body: "endTagIgnore", col: "endTagIgnore", colgroup: "endTagIgnore", html: "endTagIgnore", tbody: "endTagIgnore", td: "endTagIgnore", tfood: "endTagIgnore", thead: "endTagIgnore", tr: "endTagIgnore", "-default": "endTagOther"},n.inCaption.processCharacters = function (e) {
                    n.inBody.processCharacters(e)
                },n.inCaption.startTagTableElement = function (t, n) {
                    e.parseError("unexpected-end-tag", {name: t});
                    var r = !e.openElements.inTableScope("caption");
                    e.insertionMode.processEndTag("caption"), r || e.insertionMode.processStartTag(t, n)
                },n.inCaption.startTagOther = function (e, t, r) {
                    n.inBody.processStartTag(e, t, r)
                },n.inCaption.endTagCaption = function (t) {
                    e.openElements.inTableScope("caption") ? (e.generateImpliedEndTags(), e.currentStackItem().localName != "caption" && e.parseError("expected-one-end-tag-but-got-another", {gotName: "caption", expectedName: e.currentStackItem().localName}), e.openElements.popUntilPopped("caption"), e.clearActiveFormattingElements(), e.setInsertionMode("inTable")) : (l.ok(e.context), e.parseError("unexpected-end-tag", {name: t}))
                },n.inCaption.endTagTable = function (t) {
                    e.parseError("unexpected-end-table-in-caption");
                    var n = !e.openElements.inTableScope("caption");
                    e.insertionMode.processEndTag("caption"), n || e.insertionMode.processEndTag(t)
                },n.inCaption.endTagIgnore = function (t) {
                    e.parseError("unexpected-end-tag", {name: t})
                },n.inCaption.endTagOther = function (e) {
                    n.inBody.processEndTag(e)
                },n.inCell = Object.create(n.base),n.inCell.start_tag_handlers = {html: "startTagHtml", caption: "startTagTableOther", col: "startTagTableOther", colgroup: "startTagTableOther", tbody: "startTagTableOther", td: "startTagTableOther", tfoot: "startTagTableOther", th: "startTagTableOther", thead: "startTagTableOther", tr: "startTagTableOther", "-default": "startTagOther"},n.inCell.end_tag_handlers = {td: "endTagTableCell", th: "endTagTableCell", body: "endTagIgnore", caption: "endTagIgnore", col: "endTagIgnore", colgroup: "endTagIgnore", html: "endTagIgnore", table: "endTagImply", tbody: "endTagImply", tfoot: "endTagImply", thead: "endTagImply", tr: "endTagImply", "-default": "endTagOther"},n.inCell.processCharacters = function (e) {
                    n.inBody.processCharacters(e)
                },n.inCell.startTagTableOther = function (t, n, r) {
                    e.openElements.inTableScope("td") || e.openElements.inTableScope("th") ? (this.closeCell(), e.insertionMode.processStartTag(t, n, r)) : e.parseError("unexpected-start-tag", {name: t})
                },n.inCell.startTagOther = function (e, t, r) {
                    n.inBody.processStartTag(e, t, r)
                },n.inCell.endTagTableCell = function (t) {
                    e.openElements.inTableScope(t) ? (e.generateImpliedEndTags(t), e.currentStackItem().localName != t.toLowerCase() ? (e.parseError("unexpected-cell-end-tag", {name: t}), e.openElements.popUntilPopped(t)) : e.popElement(), e.clearActiveFormattingElements(), e.setInsertionMode("inRow")) : e.parseError("unexpected-end-tag", {name: t})
                },n.inCell.endTagIgnore = function (t) {
                    e.parseError("unexpected-end-tag", {name: t})
                },n.inCell.endTagImply = function (t) {
                    e.openElements.inTableScope(t) ? (this.closeCell(), e.insertionMode.processEndTag(t)) : e.parseError("unexpected-end-tag", {name: t})
                },n.inCell.endTagOther = function (e) {
                    n.inBody.processEndTag(e)
                },n.inCell.closeCell = function () {
                    e.openElements.inTableScope("td") ? this.endTagTableCell("td") : e.openElements.inTableScope("th") && this.endTagTableCell("th")
                },n.inColumnGroup = Object.create(n.base),n.inColumnGroup.start_tag_handlers = {html: "startTagHtml", col: "startTagCol", "-default": "startTagOther"},n.inColumnGroup.end_tag_handlers = {colgroup: "endTagColgroup", col: "endTagCol", "-default": "endTagOther"},n.inColumnGroup.ignoreEndTagColgroup = function () {
                    return e.currentStackItem().localName == "html"
                },n.inColumnGroup.processCharacters = function (t) {
                    var n = t.takeLeadingWhitespace();
                    n && e.insertText(n);
                    if (!t.length)return;
                    var r = this.ignoreEndTagColgroup();
                    this.endTagColgroup("colgroup"), r || e.insertionMode.processCharacters(t)
                },n.inColumnGroup.startTagCol = function (t, n) {
                    e.insertSelfClosingElement(t, n)
                },n.inColumnGroup.startTagOther = function (t, n, r) {
                    var i = this.ignoreEndTagColgroup();
                    this.endTagColgroup("colgroup"), i || e.insertionMode.processStartTag(t, n, r)
                },n.inColumnGroup.endTagColgroup = function (t) {
                    this.ignoreEndTagColgroup() ? (l.ok(e.context), e.parseError("unexpected-end-tag", {name: t})) : (e.popElement(), e.setInsertionMode("inTable"))
                },n.inColumnGroup.endTagCol = function (t) {
                    e.parseError("no-end-tag", {name: "col"})
                },n.inColumnGroup.endTagOther = function (t) {
                    var n = this.ignoreEndTagColgroup();
                    this.endTagColgroup("colgroup"), n || e.insertionMode.processEndTag(t)
                },n.inForeignContent = Object.create(n.base),n.inForeignContent.processStartTag = function (t, n, r) {
                    if (["b", "big", "blockquote", "body", "br", "center", "code", "dd", "div", "dl", "dt", "em", "embed", "h1", "h2", "h3", "h4", "h5", "h6", "head", "hr", "i", "img", "li", "listing", "menu", "meta", "nobr", "ol", "p", "pre", "ruby", "s", "small", "span", "strong", "strike", "sub", "sup", "table", "tt", "u", "ul", "var"].indexOf(t) != -1 || t == "font" && n.some(function (e) {
                        return["color", "face", "size"].indexOf(e.nodeName) >= 0
                    })) {
                        e.parseError("unexpected-html-element-in-foreign-content", {name: t});
                        while (e.currentStackItem().isForeign() && !e.currentStackItem().isHtmlIntegrationPoint() && !e.currentStackItem().isMathMLTextIntegrationPoint())e.openElements.pop();
                        e.insertionMode.processStartTag(t, n, r);
                        return
                    }
                    e.currentStackItem().namespaceURI == "http://www.w3.org/1998/Math/MathML" && (n = e.adjustMathMLAttributes(n)), e.currentStackItem().namespaceURI == "http://www.w3.org/2000/svg" && (t = e.adjustSVGTagNameCase(t), n = e.adjustSVGAttributes(n)), n = e.adjustForeignAttributes(n), e.insertForeignElement(t, n, e.currentStackItem().namespaceURI, r)
                },n.inForeignContent.processEndTag = function (t) {
                    var n = e.currentStackItem(), r = e.openElements.length - 1;
                    n.localName.toLowerCase() != t && e.parseError("unexpected-end-tag", {name: t});
                    for (; ;) {
                        if (r === 0)break;
                        if (n.localName.toLowerCase() == t) {
                            while (e.openElements.pop() != n);
                            break
                        }
                        r -= 1, n = e.openElements.item(r);
                        if (n.isForeign())continue;
                        e.insertionMode.processEndTag(t);
                        break
                    }
                },n.inForeignContent.processCharacters = function (t) {
                    var n = t.takeRemaining();
                    n = n.replace(/\u0000/g, function (t, n) {
                        return e.parseError("invalid-codepoint"), "�"
                    }), e.framesetOk && !s(n) && (e.framesetOk = !1), e.insertText(n)
                },n.inHeadNoscript = Object.create(n.base),n.inHeadNoscript.start_tag_handlers = {html: "startTagHtml", basefont: "startTagBasefontBgsoundLinkMetaNoframesStyle", bgsound: "startTagBasefontBgsoundLinkMetaNoframesStyle", link: "startTagBasefontBgsoundLinkMetaNoframesStyle", meta: "startTagBasefontBgsoundLinkMetaNoframesStyle", noframes: "startTagBasefontBgsoundLinkMetaNoframesStyle", style: "startTagBasefontBgsoundLinkMetaNoframesStyle", head: "startTagHeadNoscript", noscript: "startTagHeadNoscript", "-default": "startTagOther"},n.inHeadNoscript.end_tag_handlers = {noscript: "endTagNoscript", br: "endTagBr", "-default": "endTagOther"},n.inHeadNoscript.processCharacters = function (t) {
                    var n = t.takeLeadingWhitespace();
                    n && e.insertText(n);
                    if (!t.length)return;
                    e.parseError("unexpected-char-in-frameset"), this.anythingElse(), e.insertionMode.processCharacters(t)
                },n.inHeadNoscript.processComment = function (e) {
                    n.inHead.processComment(e)
                },n.inHeadNoscript.startTagHtml = function (e, t) {
                    n.inBody.processStartTag(e, t)
                },n.inHeadNoscript.startTagBasefontBgsoundLinkMetaNoframesStyle = function (e, t) {
                    n.inHead.processStartTag(e, t)
                },n.inHeadNoscript.startTagHeadNoscript = function (t, n) {
                    e.parseError("unexpected-start-tag-in-frameset", {name: t})
                },n.inHeadNoscript.startTagOther = function (t, n) {
                    e.parseError("unexpected-start-tag-in-frameset", {name: t}), this.anythingElse(), e.insertionMode.processStartTag(t, n)
                },n.inHeadNoscript.endTagBr = function (t, n) {
                    e.parseError("unexpected-end-tag-in-frameset", {name: t}), this.anythingElse(), e.insertionMode.processEndTag(t, n)
                },n.inHeadNoscript.endTagNoscript = function (t, n) {
                    e.popElement(), e.setInsertionMode("inHead")
                },n.inHeadNoscript.endTagOther = function (t, n) {
                    e.parseError("unexpected-end-tag-in-frameset", {name: t})
                },n.inHeadNoscript.anythingElse = function () {
                    e.popElement(), e.setInsertionMode("inHead")
                },n.inFrameset = Object.create(n.base),n.inFrameset.start_tag_handlers = {html: "startTagHtml", frameset: "startTagFrameset", frame: "startTagFrame", noframes: "startTagNoframes", "-default": "startTagOther"},n.inFrameset.end_tag_handlers = {frameset: "endTagFrameset", noframes: "endTagNoframes", "-default": "endTagOther"},n.inFrameset.processCharacters = function (t) {
                    e.parseError("unexpected-char-in-frameset")
                },n.inFrameset.startTagFrameset = function (t, n) {
                    e.insertElement(t, n)
                },n.inFrameset.startTagFrame = function (t, n) {
                    e.insertSelfClosingElement(t, n)
                },n.inFrameset.startTagNoframes = function (e, t) {
                    n.inBody.processStartTag(e, t)
                },n.inFrameset.startTagOther = function (t, n) {
                    e.parseError("unexpected-start-tag-in-frameset", {name: t})
                },n.inFrameset.endTagFrameset = function (t, n) {
                    e.currentStackItem().localName == "html" ? e.parseError("unexpected-frameset-in-frameset-innerhtml") : e.popElement(), !e.context && e.currentStackItem().localName != "frameset" && e.setInsertionMode("afterFrameset")
                },n.inFrameset.endTagNoframes = function (e) {
                    n.inBody.processEndTag(e)
                },n.inFrameset.endTagOther = function (t) {
                    e.parseError("unexpected-end-tag-in-frameset", {name: t})
                },n.inTable = Object.create(n.base),n.inTable.start_tag_handlers = {html: "startTagHtml", caption: "startTagCaption", colgroup: "startTagColgroup", col: "startTagCol", table: "startTagTable", tbody: "startTagRowGroup", tfoot: "startTagRowGroup", thead: "startTagRowGroup", td: "startTagImplyTbody", th: "startTagImplyTbody", tr: "startTagImplyTbody", style: "startTagStyleScript", script: "startTagStyleScript", input: "startTagInput", form: "startTagForm", "-default": "startTagOther"},n.inTable.end_tag_handlers = {table: "endTagTable", body: "endTagIgnore", caption: "endTagIgnore", col: "endTagIgnore", colgroup: "endTagIgnore", html: "endTagIgnore", tbody: "endTagIgnore", td: "endTagIgnore", tfoot: "endTagIgnore", th: "endTagIgnore", thead: "endTagIgnore", tr: "endTagIgnore", "-default": "endTagOther"},n.inTable.processCharacters = function (t) {
                    if (e.currentStackItem().isFosterParenting()) {
                        var r = e.insertionModeName;
                        e.setInsertionMode("inTableText"), e.originalInsertionMode = r, e.insertionMode.processCharacters(t)
                    } else e.redirectAttachToFosterParent = !0, n.inBody.processCharacters(t), e.redirectAttachToFosterParent = !1
                },n.inTable.startTagCaption = function (t, n) {
                    e.openElements.popUntilTableScopeMarker(), e.activeFormattingElements.push(g), e.insertElement(t, n), e.setInsertionMode("inCaption")
                },n.inTable.startTagColgroup = function (t, n) {
                    e.openElements.popUntilTableScopeMarker(), e.insertElement(t, n), e.setInsertionMode("inColumnGroup")
                },n.inTable.startTagCol = function (t, n) {
                    this.startTagColgroup("colgroup", []), e.insertionMode.processStartTag(t, n)
                },n.inTable.startTagRowGroup = function (t, n) {
                    e.openElements.popUntilTableScopeMarker(), e.insertElement(t, n), e.setInsertionMode("inTableBody")
                },n.inTable.startTagImplyTbody = function (t, n) {
                    this.startTagRowGroup("tbody", []), e.insertionMode.processStartTag(t, n)
                },n.inTable.startTagTable = function (t, n) {
                    e.parseError("unexpected-start-tag-implies-end-tag", {startName: "table", endName: "table"}), e.insertionMode.processEndTag("table"), e.context || e.insertionMode.processStartTag(t, n)
                },n.inTable.startTagStyleScript = function (e, t) {
                    n.inHead.processStartTag(e, t)
                },n.inTable.startTagInput = function (t, n) {
                    for (var r in n)if (n[r].nodeName.toLowerCase() == "type") {
                        if (n[r].nodeValue.toLowerCase() == "hidden") {
                            e.parseError("unexpected-hidden-input-in-table"), e.insertElement(t, n), e.openElements.pop();
                            return
                        }
                        break
                    }
                    this.startTagOther(t, n)
                },n.inTable.startTagForm = function (t, n) {
                    e.parseError("unexpected-form-in-table"), e.form || (e.insertElement(t, n), e.form = e.currentStackItem(), e.openElements.pop())
                },n.inTable.startTagOther = function (t, r, i) {
                    e.parseError("unexpected-start-tag-implies-table-voodoo", {name: t}), e.redirectAttachToFosterParent = !0, n.inBody.processStartTag(t, r, i), e.redirectAttachToFosterParent = !1
                },n.inTable.endTagTable = function (t) {
                    e.openElements.inTableScope(t) ? (e.generateImpliedEndTags(), e.currentStackItem().localName != t && e.parseError("end-tag-too-early-named", {gotName: "table", expectedName: e.currentStackItem().localName}), e.openElements.popUntilPopped("table"), e.resetInsertionMode()) : (l.ok(e.context), e.parseError("unexpected-end-tag", {name: t}))
                },n.inTable.endTagIgnore = function (t) {
                    e.parseError("unexpected-end-tag", {name: t})
                },n.inTable.endTagOther = function (t) {
                    e.parseError("unexpected-end-tag-implies-table-voodoo", {name: t}), e.redirectAttachToFosterParent = !0, n.inBody.processEndTag(t), e.redirectAttachToFosterParent = !1
                },n.inTableText = Object.create(n.base),n.inTableText.flushCharacters = function () {
                    var t = e.pendingTableCharacters.join("");
                    i(t) ? e.insertText(t) : (e.redirectAttachToFosterParent = !0, e.reconstructActiveFormattingElements(), e.insertText(t), e.framesetOk = !1, e.redirectAttachToFosterParent = !1), e.pendingTableCharacters = []
                },n.inTableText.processComment = function (t) {
                    this.flushCharacters(), e.setInsertionMode(e.originalInsertionMode), e.insertionMode.processComment(t)
                },n.inTableText.processEOF = function (t) {
                    this.flushCharacters(), e.setInsertionMode(e.originalInsertionMode), e.insertionMode.processEOF()
                },n.inTableText.processCharacters = function (t) {
                    var n = t.takeRemaining();
                    n = n.replace(/\u0000/g, function (t, n) {
                        return e.parseError("invalid-codepoint"), ""
                    });
                    if (!n)return;
                    e.pendingTableCharacters.push(n)
                },n.inTableText.processStartTag = function (t, n, r) {
                    this.flushCharacters(), e.setInsertionMode(e.originalInsertionMode), e.insertionMode.processStartTag(t, n, r)
                },n.inTableText.processEndTag = function (t, n) {
                    this.flushCharacters(), e.setInsertionMode(e.originalInsertionMode), e.insertionMode.processEndTag(t, n)
                },n.inTableBody = Object.create(n.base),n.inTableBody.start_tag_handlers = {html: "startTagHtml", tr: "startTagTr", td: "startTagTableCell", th: "startTagTableCell", caption: "startTagTableOther", col: "startTagTableOther", colgroup: "startTagTableOther", tbody: "startTagTableOther", tfoot: "startTagTableOther", thead: "startTagTableOther", "-default": "startTagOther"},n.inTableBody.end_tag_handlers = {table: "endTagTable", tbody: "endTagTableRowGroup", tfoot: "endTagTableRowGroup", thead: "endTagTableRowGroup", body: "endTagIgnore", caption: "endTagIgnore", col: "endTagIgnore", colgroup: "endTagIgnore", html: "endTagIgnore", td: "endTagIgnore", th: "endTagIgnore", tr: "endTagIgnore", "-default": "endTagOther"},n.inTableBody.processCharacters = function (e) {
                    n.inTable.processCharacters(e)
                },n.inTableBody.startTagTr = function (t, n) {
                    e.openElements.popUntilTableBodyScopeMarker(), e.insertElement(t, n), e.setInsertionMode("inRow")
                },n.inTableBody.startTagTableCell = function (t, n) {
                    e.parseError("unexpected-cell-in-table-body", {name: t}), this.startTagTr("tr", []), e.insertionMode.processStartTag(t, n)
                },n.inTableBody.startTagTableOther = function (t, n) {
                    e.openElements.inTableScope("tbody") || e.openElements.inTableScope("thead") || e.openElements.inTableScope("tfoot") ? (e.openElements.popUntilTableBodyScopeMarker(), this.endTagTableRowGroup(e.currentStackItem().localName), e.insertionMode.processStartTag(t, n)) : e.parseError("unexpected-start-tag", {name: t})
                },n.inTableBody.startTagOther = function (e, t) {
                    n.inTable.processStartTag(e, t)
                },n.inTableBody.endTagTableRowGroup = function (t) {
                    e.openElements.inTableScope(t) ? (e.openElements.popUntilTableBodyScopeMarker(), e.popElement(), e.setInsertionMode("inTable")) : e.parseError("unexpected-end-tag-in-table-body", {name: t})
                },n.inTableBody.endTagTable = function (t) {
                    e.openElements.inTableScope("tbody") || e.openElements.inTableScope("thead") || e.openElements.inTableScope("tfoot") ? (e.openElements.popUntilTableBodyScopeMarker(), this.endTagTableRowGroup(e.currentStackItem().localName), e.insertionMode.processEndTag(t)) : e.parseError("unexpected-end-tag", {name: t})
                },n.inTableBody.endTagIgnore = function (t) {
                    e.parseError("unexpected-end-tag-in-table-body", {name: t})
                },n.inTableBody.endTagOther = function (e) {
                    n.inTable.processEndTag(e)
                },n.inSelect = Object.create(n.base),n.inSelect.start_tag_handlers = {html: "startTagHtml", option: "startTagOption", optgroup: "startTagOptgroup", select: "startTagSelect", input: "startTagInput", keygen: "startTagInput", textarea: "startTagInput", script: "startTagScript", "-default": "startTagOther"},n.inSelect.end_tag_handlers = {option: "endTagOption", optgroup: "endTagOptgroup", select: "endTagSelect", caption: "endTagTableElements", table: "endTagTableElements", tbody: "endTagTableElements", tfoot: "endTagTableElements", thead: "endTagTableElements", tr: "endTagTableElements", td: "endTagTableElements", th: "endTagTableElements", "-default": "endTagOther"},n.inSelect.processCharacters = function (t) {
                    var n = t.takeRemaining();
                    n = n.replace(/\u0000/g, function (t, n) {
                        return e.parseError("invalid-codepoint"), ""
                    });
                    if (!n)return;
                    e.insertText(n)
                },n.inSelect.startTagOption = function (t, n) {
                    e.currentStackItem().localName == "option" && e.popElement(), e.insertElement(t, n)
                },n.inSelect.startTagOptgroup = function (t, n) {
                    e.currentStackItem().localName == "option" && e.popElement(), e.currentStackItem().localName == "optgroup" && e.popElement(), e.insertElement(t, n)
                },n.inSelect.endTagOption = function (t) {
                    if (e.currentStackItem().localName !== "option") {
                        e.parseError("unexpected-end-tag-in-select", {name: t});
                        return
                    }
                    e.popElement()
                },n.inSelect.endTagOptgroup = function (t) {
                    e.currentStackItem().localName == "option" && e.openElements.item(e.openElements.length - 2).localName == "optgroup" && e.popElement(), e.currentStackItem().localName == "optgroup" ? e.popElement() : e.parseError("unexpected-end-tag-in-select", {name: "optgroup"})
                },n.inSelect.startTagSelect = function (t) {
                    e.parseError("unexpected-select-in-select"), this.endTagSelect("select")
                },n.inSelect.endTagSelect = function (t) {
                    e.openElements.inTableScope("select") ? (e.openElements.popUntilPopped("select"), e.resetInsertionMode()) : e.parseError("unexpected-end-tag", {name: t})
                },n.inSelect.startTagInput = function (t, n) {
                    e.parseError("unexpected-input-in-select"), e.openElements.inSelectScope("select") && (this.endTagSelect("select"), e.insertionMode.processStartTag(t, n))
                },n.inSelect.startTagScript = function (e, t) {
                    n.inHead.processStartTag(e, t)
                },n.inSelect.endTagTableElements = function (t) {
                    e.parseError("unexpected-end-tag-in-select", {name: t}), e.openElements.inTableScope(t) && (this.endTagSelect("select"), e.insertionMode.processEndTag(t))
                },n.inSelect.startTagOther = function (t, n) {
                    e.parseError("unexpected-start-tag-in-select", {name: t})
                },n.inSelect.endTagOther = function (t) {
                    e.parseError("unexpected-end-tag-in-select", {name: t})
                },n.inSelectInTable = Object.create(n.base),n.inSelectInTable.start_tag_handlers = {caption: "startTagTable", table: "startTagTable", tbody: "startTagTable", tfoot: "startTagTable", thead: "startTagTable", tr: "startTagTable", td: "startTagTable", th: "startTagTable", "-default": "startTagOther"},n.inSelectInTable.end_tag_handlers = {caption: "endTagTable", table: "endTagTable", tbody: "endTagTable", tfoot: "endTagTable", thead: "endTagTable", tr: "endTagTable", td: "endTagTable", th: "endTagTable", "-default": "endTagOther"},n.inSelectInTable.processCharacters = function (e) {
                    n.inSelect.processCharacters(e)
                },n.inSelectInTable.startTagTable = function (t, n) {
                    e.parseError("unexpected-table-element-start-tag-in-select-in-table", {name: t}), this.endTagOther("select"), e.insertionMode.processStartTag(t, n)
                },n.inSelectInTable.startTagOther = function (e, t, r) {
                    n.inSelect.processStartTag(e, t, r)
                },n.inSelectInTable.endTagTable = function (t) {
                    e.parseError("unexpected-table-element-end-tag-in-select-in-table", {name: t}), e.openElements.inTableScope(t) && (this.endTagOther("select"), e.insertionMode.processEndTag(t))
                },n.inSelectInTable.endTagOther = function (e) {
                    n.inSelect.processEndTag(e)
                },n.inRow = Object.create(n.base),n.inRow.start_tag_handlers = {html: "startTagHtml", td: "startTagTableCell", th: "startTagTableCell", caption: "startTagTableOther", col: "startTagTableOther", colgroup: "startTagTableOther", tbody: "startTagTableOther", tfoot: "startTagTableOther", thead: "startTagTableOther", tr: "startTagTableOther", "-default": "startTagOther"},n.inRow.end_tag_handlers = {tr: "endTagTr", table: "endTagTable", tbody: "endTagTableRowGroup", tfoot: "endTagTableRowGroup", thead: "endTagTableRowGroup", body: "endTagIgnore", caption: "endTagIgnore", col: "endTagIgnore", colgroup: "endTagIgnore", html: "endTagIgnore", td: "endTagIgnore", th: "endTagIgnore", "-default": "endTagOther"},n.inRow.processCharacters = function (e) {
                    n.inTable.processCharacters(e)
                },n.inRow.startTagTableCell = function (t, n) {
                    e.openElements.popUntilTableRowScopeMarker(), e.insertElement(t, n), e.setInsertionMode("inCell"), e.activeFormattingElements.push(g)
                },n.inRow.startTagTableOther = function (t, n) {
                    var r = this.ignoreEndTagTr();
                    this.endTagTr("tr"), r || e.insertionMode.processStartTag(t, n)
                },n.inRow.startTagOther = function (e, t, r) {
                    n.inTable.processStartTag(e, t, r)
                },n.inRow.endTagTr = function (t) {
                    this.ignoreEndTagTr() ? (l.ok(e.context), e.parseError("unexpected-end-tag", {name: t})) : (e.openElements.popUntilTableRowScopeMarker(), e.popElement(), e.setInsertionMode("inTableBody"))
                },n.inRow.endTagTable = function (t) {
                    var n = this.ignoreEndTagTr();
                    this.endTagTr("tr"), n || e.insertionMode.processEndTag(t)
                },n.inRow.endTagTableRowGroup = function (t) {
                    e.openElements.inTableScope(t) ? (this.endTagTr("tr"), e.insertionMode.processEndTag(t)) : e.parseError("unexpected-end-tag", {name: t})
                },n.inRow.endTagIgnore = function (t) {
                    e.parseError("unexpected-end-tag-in-table-row", {name: t})
                },n.inRow.endTagOther = function (e) {
                    n.inTable.processEndTag(e)
                },n.inRow.ignoreEndTagTr = function () {
                    return!e.openElements.inTableScope("tr")
                },n.afterAfterFrameset = Object.create(n.base),n.afterAfterFrameset.start_tag_handlers = {html: "startTagHtml", noframes: "startTagNoFrames", "-default": "startTagOther"},n.afterAfterFrameset.processEOF = function () {
                },n.afterAfterFrameset.processComment = function (t) {
                    e.insertComment(t, e.document)
                },n.afterAfterFrameset.processCharacters = function (n) {
                    var r = n.takeRemaining(), i = "";
                    for (var s = 0; s < r.length; s++) {
                        var o = r[s];
                        t(o) && (i += o)
                    }
                    i && (e.reconstructActiveFormattingElements(), e.insertText(i)), i.length < r.length && e.parseError("expected-eof-but-got-char")
                },n.afterAfterFrameset.startTagHtml = function (e, t) {
                    n.inBody.processStartTag(e, t)
                },n.afterAfterFrameset.startTagNoFrames = function (e, t) {
                    n.inHead.processStartTag(e, t)
                },n.afterAfterFrameset.startTagOther = function (t, n, r) {
                    e.parseError("expected-eof-but-got-start-tag", {name: t})
                },n.afterAfterFrameset.processEndTag = function (t, n) {
                    e.parseError("expected-eof-but-got-end-tag", {name: t})
                },n.text = Object.create(n.base),n.text.start_tag_handlers = {"-default": "startTagOther"},n.text.end_tag_handlers = {script: "endTagScript", "-default": "endTagOther"},n.text.processCharacters = function (t) {
                    e.shouldSkipLeadingNewline && (e.shouldSkipLeadingNewline = !1, t.skipAtMostOneLeadingNewline());
                    var n = t.takeRemaining();
                    if (!n)return;
                    e.insertText(n)
                },n.text.processEOF = function () {
                    e.parseError("expected-named-closing-tag-but-got-eof", {name: e.currentStackItem().localName}), e.openElements.pop(), e.setInsertionMode(e.originalInsertionMode), e.insertionMode.processEOF()
                },n.text.startTagOther = function (e) {
                    throw"Tried to getEditPanel start tag " + e + " in RCDATA/RAWTEXT mode"
                },n.text.endTagScript = function (t) {
                    var n = e.openElements.pop();
                    l.ok(n.localName == "script"), e.setInsertionMode(e.originalInsertionMode)
                },n.text.endTagOther = function (t) {
                    e.openElements.pop(), e.setInsertionMode(e.originalInsertionMode)
                }
            }

            function f(e, t) {
                return e.replace(new RegExp("{[0-9a-z-]+}", "gi"), function (e) {
                    return t[e.slice(1, -1)] || e
                })
            }

            var l = e("assert"), c = e("./messages.json"), h = e("./constants"), p = e("events").EventEmitter, d = e("./Tokenizer").Tokenizer, v = e("./ElementStack").ElementStack, m = e("./StackItem").StackItem, g = {};
            u.prototype.skipAtMostOneLeadingNewline = function () {
                this.characters[this.current] === "\n" && this.current++
            }, u.prototype.skipLeadingWhitespace = function () {
                while (t(this.characters[this.current]))if (++this.current == this.end)return
            }, u.prototype.skipLeadingNonWhitespace = function () {
                while (!t(this.characters[this.current]))if (++this.current == this.end)return
            }, u.prototype.takeRemaining = function () {
                return this.characters.substring(this.current)
            }, u.prototype.takeLeadingWhitespace = function () {
                var e = this.current;
                return this.skipLeadingWhitespace(), e === this.current ? "" : this.characters.substring(e, this.current - e)
            }, Object.defineProperty(u.prototype, "length", {get: function () {
                return this.end - this.current
            }}), a.prototype.setInsertionMode = function (e) {
                this.insertionMode = this.insertionModes[e], this.insertionModeName = e
            }, a.prototype.adoptionAgencyEndTag = function (e) {
                function t(e) {
                    return e === i
                }

                var n = 8, r = 3, i, s = 0;
                while (s++ < n) {
                    i = this.elementInActiveFormattingElements(e);
                    if (!i || this.openElements.contains(i) && !this.openElements.inScope(i.localName))return this.parseError("adoption-agency-1.1", {name: e}), !1;
                    if (!this.openElements.contains(i))return this.parseError("adoption-agency-1.2", {name: e}), this.removeElementFromActiveFormattingElements(i), !0;
                    this.openElements.inScope(i.localName) || this.parseError("adoption-agency-4.4", {name: e}), i != this.currentStackItem() && this.parseError("adoption-agency-1.3", {name: e});
                    var o = this.openElements.furthestBlockForFormattingElement(i.node);
                    if (!o)return this.openElements.remove_openElements_until(t), this.removeElementFromActiveFormattingElements(i), !0;
                    var u = this.openElements.elements.indexOf(i), a = this.openElements.item(u - 1), f = this.activeFormattingElements.indexOf(i), l = o, c = o, h = this.openElements.elements.indexOf(l), p = 0;
                    while (p++ < r) {
                        h -= 1, l = this.openElements.item(h);
                        if (this.activeFormattingElements.indexOf(l) < 0) {
                            this.openElements.elements.splice(h, 1);
                            continue
                        }
                        if (l == i)break;
                        c == o && (f = this.activeFormattingElements.indexOf(l) + 1);
                        var d = this.createElement(l.namespaceURI, l.localName, l.attributes), v = new m(l.namespaceURI, l.localName, l.attributes, d);
                        this.activeFormattingElements[this.activeFormattingElements.indexOf(l)] = v, this.openElements.elements[this.openElements.elements.indexOf(l)] = v, l = v, this.detachFromParent(c.node), this.attachNode(c.node, l.node), c = l
                    }
                    this.detachFromParent(c.node), a.isFosterParenting() ? this.insertIntoFosterParent(c.node) : this.attachNode(c.node, a.node);
                    var d = this.createElement("http://www.w3.org/1999/xhtml", i.localName, i.attributes), g = new m(i.namespaceURI, i.localName, i.attributes, d);
                    this.reparentChildren(o.node, d), this.attachNode(d, o.node), this.removeElementFromActiveFormattingElements(i), this.activeFormattingElements.splice(Math.min(f, this.activeFormattingElements.length), 0, g), this.openElements.remove(i), this.openElements.elements.splice(this.openElements.elements.indexOf(o) + 1, 0, g)
                }
                return!0
            }, a.prototype.start = function () {
                throw"Not mplemented"
            }, a.prototype.startTokenization = function (e) {
                this.tokenizer = e, this.compatMode = "no quirks", this.originalInsertionMode = "initial", this.framesetOk = !0, this.openElements = new v, this.activeFormattingElements = [], this.start();
                if (this.context) {
                    switch (this.context) {
                        case"title":
                        case"textarea":
                            this.tokenizer.setState(d.RCDATA);
                            break;
                        case"style":
                        case"xmp":
                        case"iframe":
                        case"noembed":
                        case"noframes":
                            this.tokenizer.setState(d.RAWTEXT);
                            break;
                        case"script":
                            this.tokenizer.setState(d.SCRIPT_DATA);
                            break;
                        case"noscript":
                            this.scriptingEnabled && this.tokenizer.setState(d.RAWTEXT);
                            break;
                        case"plaintext":
                            this.tokenizer.setState(d.PLAINTEXT)
                    }
                    this.insertHtmlElement(), this.resetInsertionMode()
                } else this.setInsertionMode("initial")
            }, a.prototype.processToken = function (e) {
                this.selfClosingFlagAcknowledged = !1;
                var t = this.openElements.top || null, n;
                !t || !t.isForeign() || t.isMathMLTextIntegrationPoint() && (e.type == "StartTag" && !(e.name in{mglyph: 0, malignmark: 0}) || e.type === "Characters") || t.namespaceURI == "http://www.w3.org/1998/Math/MathML" && t.localName == "annotation-xml" && e.type == "StartTag" && e.name == "svg" || t.isHtmlIntegrationPoint() && e.type in{StartTag: 0, Characters: 0} || e.type == "EOF" ? n = this.insertionMode : n = this.insertionModes.inForeignContent;
                switch (e.type) {
                    case"Characters":
                        var r = new u(e.data);
                        n.processCharacters(r);
                        break;
                    case"Comment":
                        n.processComment(e.data);
                        break;
                    case"StartTag":
                        n.processStartTag(e.name, e.data, e.selfClosing);
                        break;
                    case"EndTag":
                        n.processEndTag(e.name);
                        break;
                    case"Doctype":
                        n.processDoctype(e.name, e.publicId, e.systemId, e.forceQuirks);
                        break;
                    case"EOF":
                        n.processEOF()
                }
            }, a.prototype.isCdataSectionAllowed = function () {
                return this.openElements.length > 0 && this.currentStackItem().isForeign()
            }, a.prototype.isSelfClosingFlagAcknowledged = function () {
                return this.selfClosingFlagAcknowledged
            }, a.prototype.createElement = function (e, t, n) {
                throw new Error("Not implemented")
            }, a.prototype.attachNode = function (e, t) {
                throw new Error("Not implemented")
            }, a.prototype.attachNodeToFosterParent = function (e, t, n) {
                throw new Error("Not implemented")
            }, a.prototype.detachFromParent = function (e) {
                throw new Error("Not implemented")
            }, a.prototype.addAttributesToElement = function (e, t) {
                throw new Error("Not implemented")
            }, a.prototype.insertHtmlElement = function (e) {
                var t = this.createElement("http://www.w3.org/1999/xhtml", "html", e);
                return this.attachNode(t, this.document), this.openElements.pushHtmlElement(new m("http://www.w3.org/1999/xhtml", "html", e, t)), t
            }, a.prototype.insertHeadElement = function (e) {
                var t = this.createElement("http://www.w3.org/1999/xhtml", "head", e);
                return this.head = new m("http://www.w3.org/1999/xhtml", "head", e, t), this.attachNode(t, this.openElements.top.node), this.openElements.pushHeadElement(this.head), t
            }, a.prototype.insertBodyElement = function (e) {
                var t = this.createElement("http://www.w3.org/1999/xhtml", "body", e);
                return this.attachNode(t, this.openElements.top.node), this.openElements.pushBodyElement(new m("http://www.w3.org/1999/xhtml", "body", e, t)), t
            }, a.prototype.insertIntoFosterParent = function (e) {
                var t = this.openElements.findIndex("table"), n = this.openElements.item(t).node;
                if (t === 0)return this.attachNode(e, n);
                this.attachNodeToFosterParent(e, n, this.openElements.item(t - 1).node)
            }, a.prototype.insertElement = function (e, t, n, r) {
                n || (n = "http://www.w3.org/1999/xhtml");
                var i = this.createElement(n, e, t);
                this.shouldFosterParent() ? this.insertIntoFosterParent(i) : this.attachNode(i, this.openElements.top.node), r || this.openElements.push(new m(n, e, t, i))
            }, a.prototype.insertFormattingElement = function (e, t) {
                this.insertElement(e, t, "http://www.w3.org/1999/xhtml"), this.appendElementToActiveFormattingElements(this.currentStackItem())
            }, a.prototype.insertSelfClosingElement = function (e, t) {
                this.selfClosingFlagAcknowledged = !0, this.insertElement(e, t, "http://www.w3.org/1999/xhtml", !0)
            }, a.prototype.insertForeignElement = function (e, t, n, r) {
                r && (this.selfClosingFlagAcknowledged = !0), this.insertElement(e, t, n, r)
            }, a.prototype.insertComment = function (e, t) {
                throw new Error("Not implemented")
            }, a.prototype.insertDoctype = function (e, t, n) {
                throw new Error("Not implemented")
            }, a.prototype.insertText = function (e) {
                throw new Error("Not implemented")
            }, a.prototype.currentStackItem = function () {
                return this.openElements.top
            }, a.prototype.popElement = function () {
                return this.openElements.pop()
            }, a.prototype.shouldFosterParent = function () {
                return this.redirectAttachToFosterParent && this.currentStackItem().isFosterParenting()
            }, a.prototype.generateImpliedEndTags = function (e) {
                var t = this.openElements.top.localName;
                ["dd", "dt", "li", "option", "optgroup", "p", "rp", "rt"].indexOf(t) != -1 && t != e && (this.popElement(), this.generateImpliedEndTags(e))
            }, a.prototype.reconstructActiveFormattingElements = function () {
                if (this.activeFormattingElements.length === 0)return;
                var e = this.activeFormattingElements.length - 1, t = this.activeFormattingElements[e];
                if (t == g || this.openElements.contains(t))return;
                while (t != g && !this.openElements.contains(t)) {
                    e -= 1, t = this.activeFormattingElements[e];
                    if (!t)break
                }
                for (; ;) {
                    e += 1, t = this.activeFormattingElements[e], this.insertElement(t.localName, t.attributes);
                    var n = this.currentStackItem();
                    this.activeFormattingElements[e] = n;
                    if (n == this.activeFormattingElements[this.activeFormattingElements.length - 1])break
                }
            }, a.prototype.ensureNoahsArkCondition = function (e) {
                var t = 3;
                if (this.activeFormattingElements.length < t)return;
                var n = [], r = e.attributes.length;
                for (var i = this.activeFormattingElements.length - 1; i >= 0; i--) {
                    var s = this.activeFormattingElements[i];
                    if (s === g)break;
                    if (e.localName !== s.localName || e.namespaceURI !== s.namespaceURI)continue;
                    if (s.attributes.length != r)continue;
                    n.push(s)
                }
                if (n.length < t)return;
                var u = [], a = e.attributes;
                for (var i = 0; i < a.length; i++) {
                    var f = a[i];
                    for (var l = 0; l < n.length; l++) {
                        var s = n[l], c = o(s, f.nodeName);
                        c && c.nodeValue === f.nodeValue && u.push(s)
                    }
                    if (u.length < t)return;
                    n = u, u = []
                }
                for (var i = t - 1; i < n.length; i++)this.removeElementFromActiveFormattingElements(n[i])
            }, a.prototype.appendElementToActiveFormattingElements = function (e) {
                this.ensureNoahsArkCondition(e), this.activeFormattingElements.push(e)
            }, a.prototype.removeElementFromActiveFormattingElements = function (e) {
                var t = this.activeFormattingElements.indexOf(e);
                t >= 0 && this.activeFormattingElements.splice(t, 1)
            }, a.prototype.elementInActiveFormattingElements = function (e) {
                var t = this.activeFormattingElements;
                for (var n = t.length - 1; n >= 0; n--) {
                    if (t[n] == g)break;
                    if (t[n].localName == e)return t[n]
                }
                return!1
            }, a.prototype.reparentChildren = function (e, t) {
                throw new Error("Not implemented")
            }, a.prototype.clearActiveFormattingElements = function () {
                while (this.activeFormattingElements.length !== 0 && this.activeFormattingElements.pop() != g);
            }, a.prototype.setFragmentContext = function (e) {
                this.context = e
            }, a.prototype.parseError = function (e, t, n) {
                if (!this.errorHandler)return;
                var r = f(c[e], t);
                this.errorHandler.error(r, this.tokenizer._inputStream.location(), e)
            }, a.prototype.resetInsertionMode = function () {
                var e = !1, t = null;
                for (var n = this.openElements.length - 1; n >= 0; n--) {
                    t = this.openElements.item(n), n === 0 && (l.ok(this.context), e = !0, t = new m("http://www.w3.org/1999/xhtml", this.context, [], null));
                    if (t.namespaceURI === "http://www.w3.org/1999/xhtml") {
                        if (t.localName === "select")return this.setInsertionMode("inSelect");
                        if (t.localName === "td" || t.localName === "th")return this.setInsertionMode("inCell");
                        if (t.localName === "tr")return this.setInsertionMode("inRow");
                        if (t.localName === "tbody" || t.localName === "thead" || t.localName === "tfoot")return this.setInsertionMode("inTableBody");
                        if (t.localName === "caption")return this.setInsertionMode("inCaption");
                        if (t.localName === "colgroup")return this.setInsertionMode("inColumnGroup");
                        if (t.localName === "table")return this.setInsertionMode("inTable");
                        if (t.localName === "head" && !e)return this.setInsertionMode("inHead");
                        if (t.localName === "body")return this.setInsertionMode("inBody");
                        if (t.localName === "frameset")return this.setInsertionMode("inFrameset");
                        if (t.localName === "html")return this.openElements.headElement ? this.setInsertionMode("afterHead") : this.setInsertionMode("beforeHead")
                    }
                    if (e)return this.setInsertionMode("inBody")
                }
            }, a.prototype.processGenericRCDATAStartTag = function (e, t) {
                this.insertElement(e, t), this.tokenizer.setState(d.RCDATA), this.originalInsertionMode = this.insertionModeName, this.setInsertionMode("text")
            }, a.prototype.processGenericRawTextStartTag = function (e, t) {
                this.insertElement(e, t), this.tokenizer.setState(d.RAWTEXT), this.originalInsertionMode = this.insertionModeName, this.setInsertionMode("text")
            }, a.prototype.adjustMathMLAttributes = function (e) {
                return e.forEach(function (e) {
                    e.namespaceURI = "http://www.w3.org/1998/Math/MathML", h.MATHMLAttributeMap[e.nodeName] && (e.nodeName = h.MATHMLAttributeMap[e.nodeName])
                }), e
            }, a.prototype.adjustSVGTagNameCase = function (e) {
                return h.SVGTagMap[e] || e
            }, a.prototype.adjustSVGAttributes = function (e) {
                return e.forEach(function (e) {
                    e.namespaceURI = "http://www.w3.org/2000/svg", h.SVGAttributeMap[e.nodeName] && (e.nodeName = h.SVGAttributeMap[e.nodeName])
                }), e
            }, a.prototype.adjustForeignAttributes = function (e) {
                for (var t = 0; t < e.length; t++) {
                    var n = e[t], r = h.ForeignAttributeMap[n.nodeName];
                    r && (n.nodeName = r.localName, n.prefix = r.prefix, n.namespaceURI = r.namespaceURI)
                }
                return e
            }, n.TreeBuilder = a
        })()
    }, {"./ElementStack": 1, "./StackItem": 4, "./Tokenizer": 5, "./constants": 7, "./messages.json": 8, assert: 13, events: 14}], 7: [function (e, t, n) {
        n.SVGTagMap = {altglyph: "altGlyph", altglyphdef: "altGlyphDef", altglyphitem: "altGlyphItem", animatecolor: "animateColor", animatemotion: "animateMotion", animatetransform: "animateTransform", clippath: "clipPath", feblend: "feBlend", fecolormatrix: "feColorMatrix", fecomponenttransfer: "feComponentTransfer", fecomposite: "feComposite", feconvolvematrix: "feConvolveMatrix", fediffuselighting: "feDiffuseLighting", fedisplacementmap: "feDisplacementMap", fedistantlight: "feDistantLight", feflood: "feFlood", fefunca: "feFuncA", fefuncb: "feFuncB", fefuncg: "feFuncG", fefuncr: "feFuncR", fegaussianblur: "feGaussianBlur", feimage: "feImage", femerge: "feMerge", femergenode: "feMergeNode", femorphology: "feMorphology", feoffset: "feOffset", fepointlight: "fePointLight", fespecularlighting: "feSpecularLighting", fespotlight: "feSpotLight", fetile: "feTile", feturbulence: "feTurbulence", foreignobject: "foreignObject", glyphref: "glyphRef", lineargradient: "linearGradient", radialgradient: "radialGradient", textpath: "textPath"}, n.MATHMLAttributeMap = {definitionurl: "definitionURL"}, n.SVGAttributeMap = {attributename: "attributeName", attributetype: "attributeType", basefrequency: "baseFrequency", baseprofile: "baseProfile", calcmode: "calcMode", clippathunits: "clipPathUnits", contentscripttype: "contentScriptType", contentstyletype: "contentStyleType", diffuseconstant: "diffuseConstant", edgemode: "edgeMode", externalresourcesrequired: "externalResourcesRequired", filterres: "filterRes", filterunits: "filterUnits", glyphref: "glyphRef", gradienttransform: "gradientTransform", gradientunits: "gradientUnits", kernelmatrix: "kernelMatrix", kernelunitlength: "kernelUnitLength", keypoints: "keyPoints", keysplines: "keySplines", keytimes: "keyTimes", lengthadjust: "lengthAdjust", limitingconeangle: "limitingConeAngle", markerheight: "markerHeight", markerunits: "markerUnits", markerwidth: "markerWidth", maskcontentunits: "maskContentUnits", maskunits: "maskUnits", numoctaves: "numOctaves", pathlength: "pathLength", patterncontentunits: "patternContentUnits", patterntransform: "patternTransform", patternunits: "patternUnits", pointsatx: "pointsAtX", pointsaty: "pointsAtY", pointsatz: "pointsAtZ", preservealpha: "preserveAlpha", preserveaspectratio: "preserveAspectRatio", primitiveunits: "primitiveUnits", refx: "refX", refy: "refY", repeatcount: "repeatCount", repeatdur: "repeatDur", requiredextensions: "requiredExtensions", requiredfeatures: "requiredFeatures", specularconstant: "specularConstant", specularexponent: "specularExponent", spreadmethod: "spreadMethod", startoffset: "startOffset", stddeviation: "stdDeviation", stitchtiles: "stitchTiles", surfacescale: "surfaceScale", systemlanguage: "systemLanguage", tablevalues: "tableValues", targetx: "targetX", targety: "targetY", textlength: "textLength", viewbox: "viewBox", viewtarget: "viewTarget", xchannelselector: "xChannelSelector", ychannelselector: "yChannelSelector", zoomandpan: "zoomAndPan"}, n.ForeignAttributeMap = {"xlink:actuate": {prefix: "xlink", localName: "actuate", namespaceURI: "http://www.w3.org/1999/xlink"}, "xlink:arcrole": {prefix: "xlink", localName: "arcrole", namespaceURI: "http://www.w3.org/1999/xlink"}, "xlink:href": {prefix: "xlink", localName: "href", namespaceURI: "http://www.w3.org/1999/xlink"}, "xlink:role": {prefix: "xlink", localName: "role", namespaceURI: "http://www.w3.org/1999/xlink"}, "xlink:show": {prefix: "xlink", localName: "show", namespaceURI: "http://www.w3.org/1999/xlink"}, "xlink:title": {prefix: "xlink", localName: "title", namespaceURI: "http://www.w3.org/1999/xlink"}, "xlink:type": {prefix: "xlink", localName: "title", namespaceURI: "http://www.w3.org/1999/xlink"}, "xml:base": {prefix: "xml", localName: "base", namespaceURI: "http://www.w3.org/XML/1998/namespace"}, "xml:lang": {prefix: "xml", localName: "lang", namespaceURI: "http://www.w3.org/XML/1998/namespace"}, "xml:space": {prefix: "xml", localName: "space", namespaceURI: "http://www.w3.org/XML/1998/namespace"}, xmlns: {prefix: null, localName: "xmlns", namespaceURI: "http://www.w3.org/2000/xmlns/"}, "xmlns:xlink": {prefix: "xmlns", localName: "xlink", namespaceURI: "http://www.w3.org/2000/xmlns/"}}
    }, {}], 8: [function (e, t, n) {
        t.exports = {"null-character": "Null character in input stream, replaced with U+FFFD.", "invalid-codepoint": "Invalid codepoint in stream", "incorrectly-placed-solidus": "Solidus (/) incorrectly placed in tag.", "incorrect-cr-newline-entity": "Incorrect CR newline entity, replaced with LF.", "illegal-windows-1252-entity": "Entity used with illegal number (windows-1252 reference).", "cant-convert-numeric-entity": "Numeric entity couldn't be converted to character (codepoint U+{charAsInt}).", "invalid-numeric-entity-replaced": "Numeric entity represents an illegal codepoint. Expanded to the C1 controls range.", "numeric-entity-without-semicolon": "Numeric entity didn't end with ';'.", "expected-numeric-entity-but-got-eof": "Numeric entity expected. Got end of file instead.", "expected-numeric-entity": "Numeric entity expected but none found.", "named-entity-without-semicolon": "Named entity didn't end with ';'.", "expected-named-entity": "Named entity expected. Got none.", "attributes-in-end-tag": "End tag contains unexpected attributes.", "self-closing-flag-on-end-tag": "End tag contains unexpected self-closing flag.", "bare-less-than-sign-at-eof": "End of file after <.", "expected-tag-name-but-got-right-bracket": "Expected tag name. Got '>' instead.", "expected-tag-name-but-got-question-mark": "Expected tag name. Got '?' instead. (HTML doesn't support processing instructions.)", "expected-tag-name": "Expected tag name. Got something else instead.", "expected-closing-tag-but-got-right-bracket": "Expected closing tag. Got '>' instead. Ignoring '</>'.", "expected-closing-tag-but-got-eof": "Expected closing tag. Unexpected end of file.", "expected-closing-tag-but-got-char": "Expected closing tag. Unexpected character '{data}' found.", "eof-in-tag-name": "Unexpected end of file in the tag name.", "expected-attribute-name-but-got-eof": "Unexpected end of file. Expected attribute name instead.", "eof-in-attribute-name": "Unexpected end of file in attribute name.", "invalid-character-in-attribute-name": "Invalid character in attribute name.", "duplicate-attribute": "Dropped duplicate attribute '{name}' on tag.", "expected-end-of-tag-but-got-eof": "Unexpected end of file. Expected = or end of tag.", "expected-attribute-value-but-got-eof": "Unexpected end of file. Expected attribute value.", "expected-attribute-value-but-got-right-bracket": "Expected attribute value. Got '>' instead.", "unexpected-character-in-unquoted-attribute-value": "Unexpected character in unquoted attribute", "invalid-character-after-attribute-name": "Unexpected character after attribute name.", "unexpected-character-after-attribute-value": "Unexpected character after attribute value.", "eof-in-attribute-value-double-quote": 'Unexpected end of file in attribute value (").', "eof-in-attribute-value-single-quote": "Unexpected end of file in attribute value (').", "eof-in-attribute-value-no-quotes": "Unexpected end of file in attribute value.", "eof-after-attribute-value": "Unexpected end of file after attribute value.", "unexpected-eof-after-solidus-in-tag": "Unexpected end of file in tag. Expected >.", "unexpected-character-after-solidus-in-tag": "Unexpected character after / in tag. Expected >.", "expected-dashes-or-doctype": "Expected '--' or 'DOCTYPE'. Not found.", "unexpected-bang-after-double-dash-in-comment": "Unexpected ! after -- in comment.", "incorrect-comment": "Incorrect comment.", "eof-in-comment": "Unexpected end of file in comment.", "eof-in-comment-end-dash": "Unexpected end of file in comment (-).", "unexpected-dash-after-double-dash-in-comment": "Unexpected '-' after '--' found in comment.", "eof-in-comment-double-dash": "Unexpected end of file in comment (--).", "eof-in-comment-end-bang-state": "Unexpected end of file in comment.", "unexpected-char-in-comment": "Unexpected character in comment found.", "need-space-after-doctype": "No space after literal string 'DOCTYPE'.", "expected-doctype-name-but-got-right-bracket": "Unexpected > character. Expected DOCTYPE name.", "expected-doctype-name-but-got-eof": "Unexpected end of file. Expected DOCTYPE name.", "eof-in-doctype-name": "Unexpected end of file in DOCTYPE name.", "eof-in-doctype": "Unexpected end of file in DOCTYPE.", "expected-space-or-right-bracket-in-doctype": "Expected space or '>'. Got '{data}'.", "unexpected-end-of-doctype": "Unexpected end of DOCTYPE.", "unexpected-char-in-doctype": "Unexpected character in DOCTYPE.", "eof-in-bogus-doctype": "Unexpected end of file in bogus doctype.", "eof-in-innerhtml": "Unexpected EOF in inner html mode.", "unexpected-doctype": "Unexpected DOCTYPE. Ignored.", "non-html-root": "html needs to be the first start tag.", "expected-doctype-but-got-eof": "Unexpected End of file. Expected DOCTYPE.", "unknown-doctype": "Erroneous DOCTYPE. Expected <!DOCTYPE html>.", "quirky-doctype": "Quirky doctype. Expected <!DOCTYPE html>.", "almost-standards-doctype": "Almost standards mode doctype. Expected <!DOCTYPE html>.", "obsolete-doctype": "Obsolete doctype. Expected <!DOCTYPE html>.", "expected-doctype-but-got-chars": "Non-space characters found without seeing a doctype first. Expected e.g. <!DOCTYPE html>.", "expected-doctype-but-got-start-tag": "Start tag seen without seeing a doctype first. Expected e.g. <!DOCTYPE html>.", "expected-doctype-but-got-end-tag": "End tag seen without seeing a doctype first. Expected e.g. <!DOCTYPE html>.", "end-tag-after-implied-root": "Unexpected end tag ({name}) after the (implied) root element.", "expected-named-closing-tag-but-got-eof": "Unexpected end of file. Expected end tag ({name}).", "two-heads-are-not-better-than-one": "Unexpected start tag head in existing head. Ignored.", "unexpected-end-tag": "Unexpected end tag ({name}). Ignored.", "unexpected-start-tag-out-of-my-head": "Unexpected start tag ({name}) that can be in head. Moved.", "unexpected-start-tag": "Unexpected start tag ({name}).", "missing-end-tag": "Missing end tag ({name}).", "missing-end-tags": "Missing end tags ({name}).", "unexpected-start-tag-implies-end-tag": "Unexpected start tag ({startName}) implies end tag ({endName}).", "unexpected-start-tag-treated-as": "Unexpected start tag ({originalName}). Treated as {newName}.", "deprecated-tag": "Unexpected start tag {name}. Don't use it!", "unexpected-start-tag-ignored": "Unexpected start tag {name}. Ignored.", "expected-one-end-tag-but-got-another": "Unexpected end tag ({gotName}). Missing end tag ({expectedName}).", "end-tag-too-early": "End tag ({name}) seen too early. Expected other end tag.", "end-tag-too-early-named": "Unexpected end tag ({gotName}). Expected end tag ({expectedName}.", "end-tag-too-early-ignored": "End tag ({name}) seen too early. Ignored.", "adoption-agency-1.1": "End tag ({name}) violates step 1, paragraph 1 of the adoption agency algorithm.", "adoption-agency-1.2": "End tag ({name}) violates step 1, paragraph 2 of the adoption agency algorithm.", "adoption-agency-1.3": "End tag ({name}) violates step 1, paragraph 3 of the adoption agency algorithm.", "adoption-agency-4.4": "End tag ({name}) violates step 4, paragraph 4 of the adoption agency algorithm.", "unexpected-end-tag-treated-as": "Unexpected end tag ({originalName}). Treated as {newName}.", "no-end-tag": "This element ({name}) has no end tag.", "unexpected-implied-end-tag-in-table": "Unexpected implied end tag ({name}) in the table phase.", "unexpected-implied-end-tag-in-table-body": "Unexpected implied end tag ({name}) in the table body phase.", "unexpected-char-implies-table-voodoo": "Unexpected non-space characters in table context caused voodoo mode.", "unexpected-hidden-input-in-table": "Unexpected input with type hidden in table context.", "unexpected-form-in-table": "Unexpected form in table context.", "unexpected-start-tag-implies-table-voodoo": "Unexpected start tag ({name}) in table context caused voodoo mode.", "unexpected-end-tag-implies-table-voodoo": "Unexpected end tag ({name}) in table context caused voodoo mode.", "unexpected-cell-in-table-body": "Unexpected table cell start tag ({name}) in the table body phase.", "unexpected-cell-end-tag": "Got table cell end tag ({name}) while required end tags are missing.", "unexpected-end-tag-in-table-body": "Unexpected end tag ({name}) in the table body phase. Ignored.", "unexpected-implied-end-tag-in-table-row": "Unexpected implied end tag ({name}) in the table row phase.", "unexpected-end-tag-in-table-row": "Unexpected end tag ({name}) in the table row phase. Ignored.", "unexpected-select-in-select": "Unexpected select start tag in the select phase treated as select end tag.", "unexpected-input-in-select": "Unexpected input start tag in the select phase.", "unexpected-start-tag-in-select": "Unexpected start tag token ({name}) in the select phase. Ignored.", "unexpected-end-tag-in-select": "Unexpected end tag ({name}) in the select phase. Ignored.", "unexpected-table-element-start-tag-in-select-in-table": "Unexpected table element start tag ({name}) in the select in table phase.", "unexpected-table-element-end-tag-in-select-in-table": "Unexpected table element end tag ({name}) in the select in table phase.", "unexpected-char-after-body": "Unexpected non-space characters in the after body phase.", "unexpected-start-tag-after-body": "Unexpected start tag token ({name}) in the after body phase.", "unexpected-end-tag-after-body": "Unexpected end tag token ({name}) in the after body phase.", "unexpected-char-in-frameset": "Unepxected characters in the frameset phase. Characters ignored.", "unexpected-start-tag-in-frameset": "Unexpected start tag token ({name}) in the frameset phase. Ignored.", "unexpected-frameset-in-frameset-innerhtml": "Unexpected end tag token (frameset in the frameset phase (innerHTML).", "unexpected-end-tag-in-frameset": "Unexpected end tag token ({name}) in the frameset phase. Ignored.", "unexpected-char-after-frameset": "Unexpected non-space characters in the after frameset phase. Ignored.", "unexpected-start-tag-after-frameset": "Unexpected start tag ({name}) in the after frameset phase. Ignored.", "unexpected-end-tag-after-frameset": "Unexpected end tag ({name}) in the after frameset phase. Ignored.", "expected-eof-but-got-char": "Unexpected non-space characters. Expected end of file.", "expected-eof-but-got-start-tag": "Unexpected start tag ({name}). Expected end of file.", "expected-eof-but-got-end-tag": "Unexpected end tag ({name}). Expected end of file.", "unexpected-end-table-in-caption": "Unexpected end table tag in caption. Generates implied end caption.", "end-html-in-innerhtml": "Unexpected html end tag in inner html mode.", "eof-in-table": "Unexpected end of file. Expected table content.", "eof-in-script": "Unexpected end of file. Expected script content.", "non-void-element-with-trailing-solidus": "Trailing solidus not allowed on element {name}.", "unexpected-html-element-in-foreign-content": 'HTML start tag "{name}" in a foreign namespace context.', "unexpected-start-tag-in-table": "Unexpected {name}. Expected table content."}
    }, {}], DaboPu: [function (e, t, n) {
        function r() {
            this.contentHandler = null, this._errorHandler = null, this._treeBuilder = new i, this._tokenizer = new s(this._treeBuilder), this._scriptingEnabled = !1
        }

        var i = e("./SAXTreeBuilder").SAXTreeBuilder, s = e("../Tokenizer").Tokenizer, o = e("./TreeParser").TreeParser;
        r.prototype.parse = function (e) {
            this._tokenizer.tokenize(e);
            var t = this._treeBuilder.document;
            t && (new o(this.contentHandler)).parse(t)
        }, r.prototype.parseFragment = function (e, t) {
            this._treeBuilder.setFragmentContext(t), this._tokenizer.tokenize(e);
            var n = this._treeBuilder.getFragment();
            n && (new o(this.contentHandler)).parse(n)
        }, Object.defineProperty(r.prototype, "scriptingEnabled", {get: function () {
            return this._scriptingEnabled
        }, set: function (e) {
            this._scriptingEnabled = e, this._treeBuilder.scriptingEnabled = e
        }}), Object.defineProperty(r.prototype, "errorHandler", {get: function () {
            return this._errorHandler
        }, set: function (e) {
            this._errorHandler = e, this._treeBuilder.errorHandler = e
        }}), n.SAXParser = r
    }, {"../Tokenizer": 5, "./SAXTreeBuilder": 10, "./TreeParser": 11}], 10: [function (e, t, n) {
        function r() {
            b.call(this)
        }

        function i(e, t) {
            for (var n = 0; n < e.attributes.length; n++) {
                var r = e.attributes[n];
                if (r.nodeName === t)return r.nodeValue
            }
        }

        function s() {
            this.parentNode = null, this.nextSibling = null, this.firstChild = null
        }

        function o() {
            s.call(this), this.lastChild = null
        }

        function u() {
            s.call(this), this.nodeType = w.DOCUMENT
        }

        function a() {
            o.call(this), this.nodeType = w.DOCUMENT_FRAGMENT
        }

        function f(e, t, n, r, i) {
            o.call(this), this.uri = e, this.localName = t, this.qName = n, this.attributes = r, this.prefixMappings = i, this.nodeType = w.ELEMENT
        }

        function l(e) {
            s.call(this), this.data = e, this.nodeType = w.CHARACTERS
        }

        function c(e) {
            s.call(this), this.data = e, this.nodeType = w.IGNORABLE_WHITESPACE
        }

        function h(e) {
            s.call(this), this.data = e, this.nodeType = w.COMMENT
        }

        function p() {
            o.call(this), this.nodeType = w.CDATA
        }

        function d(e) {
            o.call(this), this.name = e, this.nodeType = w.ENTITY
        }

        function v(e) {
            s.call(this), this.name = e, this.nodeType = w.SKIPPED_ENTITY
        }

        function m(e, t) {
            s.call(this), this.target = e, this.data = t
        }

        function g(e, t, n) {
            o.call(this), this.name = e, this.publicIdentifier = t, this.systemIdentifier = n, this.nodeType = w.DTD
        }

        var y = e("util"), b = e("../TreeBuilder").TreeBuilder;
        y.inherits(r, b), r.prototype.start = function (e) {
            this.document = new u
        }, r.prototype.insertDoctype = function (e, t, n) {
            var r = new g(e, t, n);
            this.document.appendChild(r)
        }, r.prototype.createElement = function (e, t, n) {
            var r = new f(e, t, t, n);
            return r
        }, r.prototype.insertComment = function (e, t) {
            t || (t = this.currentStackItem());
            var n = new h(e);
            t.appendChild(n)
        }, r.prototype.appendCharacters = function (e, t) {
            var n = new l(t);
            e.appendChild(n)
        }, r.prototype.insertText = function (e) {
            if (this.redirectAttachToFosterParent && this.openElements.top.isFosterParenting()) {
                var t = this.openElements.findIndex("table"), n = this.openElements.item(t), r = n.node;
                if (t === 0)return this.appendCharacters(r, e);
                var i = new l(e), s = r.parentNode;
                if (s) {
                    s.insertBetween(i, r.previousSibling, r);
                    return
                }
                var o = this.openElements.item(t - 1).node;
                o.appendChild(i);
                return
            }
            this.appendCharacters(this.currentStackItem().node, e)
        }, r.prototype.attachNode = function (e, t) {
            t.appendChild(e)
        }, r.prototype.attachNodeToFosterParent = function (e, t, n) {
            var r = t.parentNode;
            r ? r.insertBetween(e, t.previousSibling, t) : n.appendChild(e)
        }, r.prototype.detachFromParent = function (e) {
            e.detach()
        }, r.prototype.reparentChildren = function (e, t) {
            t.appendChildren(e.firstChild)
        }, r.prototype.getFragment = function () {
            var e = new a;
            return this.reparentChildren(this.openElements.rootNode, e), e
        }, r.prototype.addAttributesToElement = function (e, t) {
            for (var n = 0; n < t.length; n++) {
                var r = t[n];
                i(e, r.nodeName) || e.attributes.push(r)
            }
        };
        var w = {CDATA: 1, CHARACTERS: 2, COMMENT: 3, DOCUMENT: 4, DOCUMENT_FRAGMENT: 5, DTD: 6, ELEMENT: 7, ENTITY: 8, IGNORABLE_WHITESPACE: 9, PROCESSING_INSTRUCTION: 10, SKIPPED_ENTITY: 11};
        s.prototype.visit = function (e) {
            throw new Error("Not Implemented")
        }, s.prototype.revisit = function (e) {
            return
        }, s.prototype.detach = function () {
            this.parentNode !== null && (this.parentNode.removeChild(this), this.parentNode = null)
        }, Object.defineProperty(s.prototype, "previousSibling", {get: function () {
            var e = null, t = this.parentNode.firstChild;
            for (; ;) {
                if (this == t)return e;
                e = t, t = t.nextSibling
            }
        }}), o.prototype = Object.create(s.prototype), o.prototype.insertBefore = function (e, t) {
            if (!t)return this.appendChild(e);
            e.detach(), e.parentNode = this;
            if (this.firstChild == t)e.nextSibling = t, this.firstChild = e; else {
                var n = this.firstChild, r = this.firstChild.nextSibling;
                while (r != t)n = r, r = r.nextSibling;
                n.nextSibling = e, e.nextSibling = r
            }
            return e
        }, o.prototype.insertBetween = function (e, t, n) {
            return n ? (e.detach(), e.parentNode = this, e.nextSibling = n, t ? t.nextSibling = e : firstChild = e, e) : this.appendChild(e)
        }, o.prototype.appendChild = function (e) {
            return e.detach(), e.parentNode = this, this.firstChild ? this.lastChild.nextSibling = e : this.firstChild = e, this.lastChild = e, e
        }, o.prototype.appendChildren = function (e) {
            var t = e.firstChild;
            if (!t)return;
            var n = e;
            this.firstChild ? this.lastChild.nextSibling = t : this.firstChild = t, this.lastChild = n.lastChild;
            do t.parentNode = this; while (t = t.nextSibling);
            n.firstChild = null, n.lastChild = null
        }, o.prototype.removeChild = function (e) {
            if (this.firstChild == e)this.firstChild = e.nextSibling, this.lastChild == e && (this.lastChild = null); else {
                var t = this.firstChild, n = this.firstChild.nextSibling;
                while (n != e)t = n, n = n.nextSibling;
                t.nextSibling = e.nextSibling, this.lastChild == e && (this.lastChild = t)
            }
            return e.parentNode = null, e
        }, u.prototype = Object.create(o.prototype), u.prototype.visit = function (e) {
            e.startDocument(this)
        }, u.prototype.revisit = function (e) {
            e.endDocument()
        }, a.prototype = Object.create(o.prototype), a.prototype.visit = function (e) {
        }, f.prototype = Object.create(o.prototype), f.prototype.visit = function (e) {
            if (this.prefixMappings)for (var t in prefixMappings) {
                var n = prefixMappings[t];
                e.startPrefixMapping(n.getPrefix(), n.getUri(), this)
            }
            e.startElement(this.uri, this.localName, this.qName, this.attributes, this)
        }, f.prototype.revisit = function (e) {
            e.endElement(this.uri, this.localName, this.qName);
            if (this.prefixMappings)for (var t in prefixMappings) {
                var n = prefixMappings[t];
                e.endPrefixMapping(n.getPrefix())
            }
        }, l.prototype = Object.create(s.prototype), l.prototype.visit = function (e) {
            e.characters(this.data, 0, this.data.length, this)
        }, c.prototype = Object.create(s.prototype), c.prototype.visit = function (e) {
            e.ignorableWhitespace(this.data, 0, this.data.length, this)
        }, h.prototype = Object.create(s.prototype), h.prototype.visit = function (e) {
            e.comment(this.data, 0, this.data.length, this)
        }, p.prototype = Object.create(o.prototype), p.prototype.visit = function (e) {
            e.startCDATA(this)
        }, p.prototype.revisit = function (e) {
            e.endCDATA()
        }, d.prototype = Object.create(o.prototype), d.prototype.visit = function (e) {
            e.startEntity(this.name, this)
        }, d.prototype.revisit = function (e) {
            e.endEntity(this.name)
        }, v.prototype = Object.create(s.prototype), v.prototype.visit = function (e) {
            e.skippedEntity(this.name, this)
        }, m.prototype = Object.create(s.prototype), m.prototype.visit = function (e) {
            e.processingInstruction(this.target, this.data, this)
        }, m.prototype.getNodeType = function () {
            return w.PROCESSING_INSTRUCTION
        }, g.prototype = Object.create(o.prototype), g.prototype.visit = function (e) {
            e.startDTD(this.name, this.publicIdentifier, this.systemIdentifier, this)
        }, g.prototype.revisit = function (e) {
            e.endDTD()
        }, n.SAXTreeBuilder = r
    }, {"../TreeBuilder": 6, util: 15}], 11: [function (e, t, n) {
        function r(e, t) {
            this.contentHandler, this.lexicalHandler, this.locatorDelegate;
            if (!e)throw new IllegalArgumentException("contentHandler was null.");
            this.contentHandler = e, t ? this.lexicalHandler = t : this.lexicalHandler = new i
        }

        function i() {
        }

        r.prototype.parse = function (e) {
            var t = e, n;
            for (; ;) {
                t.visit(this);
                if ((n = t.firstChild) != null) {
                    t = n;
                    continue
                }
                for (; ;) {
                    t.revisit(this);
                    if (t == e)return;
                    if ((n = t.nextSibling) != null) {
                        t = n;
                        break
                    }
                    t = t.parentNode
                }
            }
        }, r.prototype.characters = function (e, t, n, r) {
            this.locatorDelegate = r, this.contentHandler.characters(e, t, n)
        }, r.prototype.endDocument = function (e) {
            this.locatorDelegate = e, this.contentHandler.endDocument()
        }, r.prototype.endElement = function (e, t, n, r) {
            this.locatorDelegate = r, this.contentHandler.endElement(e, t, n)
        }, r.prototype.endPrefixMapping = function (e, t) {
            this.locatorDelegate = t, this.contentHandler.endPrefixMapping(e)
        }, r.prototype.ignorableWhitespace = function (e, t, n, r) {
            this.locatorDelegate = r, this.contentHandler.ignorableWhitespace(e, t, n)
        }, r.prototype.processingInstruction = function (e, t, n) {
            this.locatorDelegate = n, this.contentHandler.processingInstruction(e, t)
        }, r.prototype.skippedEntity = function (e, t) {
            this.locatorDelegate = t, this.contentHandler.skippedEntity(e)
        }, r.prototype.startDocument = function (e) {
            this.locatorDelegate = e, this.contentHandler.startDocument()
        }, r.prototype.startElement = function (e, t, n, r, i) {
            this.locatorDelegate = i, this.contentHandler.startElement(e, t, n, r)
        }, r.prototype.startPrefixMapping = function (e, t, n) {
            this.locatorDelegate = n, this.contentHandler.startPrefixMapping(e, t)
        }, r.prototype.comment = function (e, t, n, r) {
            this.locatorDelegate = r, this.lexicalHandler.comment(e, t, n)
        }, r.prototype.endCDATA = function (e) {
            this.locatorDelegate = e, this.lexicalHandler.endCDATA()
        }, r.prototype.endDTD = function (e) {
            this.locatorDelegate = e, this.lexicalHandler.endDTD()
        }, r.prototype.endEntity = function (e, t) {
            this.locatorDelegate = t, this.lexicalHandler.endEntity(e)
        }, r.prototype.startCDATA = function (e) {
            this.locatorDelegate = e, this.lexicalHandler.startCDATA()
        }, r.prototype.startDTD = function (e, t, n, r) {
            this.locatorDelegate = r, this.lexicalHandler.startDTD(e, t, n)
        }, r.prototype.startEntity = function (e, t) {
            this.locatorDelegate = t, this.lexicalHandler.startEntity(e)
        }, i.prototype.comment = function () {
        }, i.prototype.endCDATA = function () {
        }, i.prototype.endDTD = function () {
        }, i.prototype.endEntity = function () {
        }, i.prototype.startCDATA = function () {
        }, i.prototype.startDTD = function () {
        }, i.prototype.startEntity = function () {
        }, n.TreeParser = r
    }, {}], 12: [function (e, t, n) {
        t.exports = {AElig: "Æ", "AElig;": "Æ", AMP: "&", "AMP;": "&", Aacute: "Á", "Aacute;": "Á", "Abreve;": "Ă", Acirc: "Â", "Acirc;": "Â", "Acy;": "А", "Afr;": "ᵐ4", Agrave: "À", "Agrave;": "À", "Alpha;": "Α", "Amacr;": "Ā", "And;": "⩓", "Aogon;": "Ą", "Aopf;": "ᵓ8", "ApplyFunction;": "⁡", Aring: "Å", "Aring;": "Å", "Ascr;": "ᵉC", "Assign;": "≔", Atilde: "Ã", "Atilde;": "Ã", Auml: "Ä", "Auml;": "Ä", "Backslash;": "∖", "Barv;": "⫧", "Barwed;": "⌆", "Bcy;": "Б", "Because;": "∵", "Bernoullis;": "ℬ", "Beta;": "Β", "Bfr;": "ᵐ5", "Bopf;": "ᵓ9", "Breve;": "˘", "Bscr;": "ℬ", "Bumpeq;": "≎", "CHcy;": "Ч", COPY: "©", "COPY;": "©", "Cacute;": "Ć", "Cap;": "⋒", "CapitalDifferentialD;": "ⅅ", "Cayleys;": "ℭ", "Ccaron;": "Č", Ccedil: "Ç", "Ccedil;": "Ç", "Ccirc;": "Ĉ", "Cconint;": "∰", "Cdot;": "Ċ", "Cedilla;": "¸", "CenterDot;": "·", "Cfr;": "ℭ", "Chi;": "Χ", "CircleDot;": "⊙", "CircleMinus;": "⊖", "CirclePlus;": "⊕", "CircleTimes;": "⊗", "ClockwiseContourIntegral;": "∲", "CloseCurlyDoubleQuote;": "”", "CloseCurlyQuote;": "’", "Colon;": "∷", "Colone;": "⩴", "Congruent;": "≡", "Conint;": "∯", "ContourIntegral;": "∮", "Copf;": "ℂ", "Coproduct;": "∐", "CounterClockwiseContourIntegral;": "∳", "Cross;": "⨯", "Cscr;": "ᵉE", "Cup;": "⋓", "CupCap;": "≍", "DD;": "ⅅ", "DDotrahd;": "⤑", "DJcy;": "Ђ", "DScy;": "Ѕ", "DZcy;": "Џ", "Dagger;": "‡", "Darr;": "↡", "Dashv;": "⫤", "Dcaron;": "Ď", "Dcy;": "Д", "Del;": "∇", "Delta;": "Δ", "Dfr;": "ᵐ7", "DiacriticalAcute;": "´", "DiacriticalDot;": "˙", "DiacriticalDoubleAcute;": "˝", "DiacriticalGrave;": "`", "DiacriticalTilde;": "˜", "Diamond;": "⋄", "DifferentialD;": "ⅆ", "Dopf;": "ᵓB", "Dot;": "¨", "DotDot;": "⃜", "DotEqual;": "≐", "DoubleContourIntegral;": "∯", "DoubleDot;": "¨", "DoubleDownArrow;": "⇓", "DoubleLeftArrow;": "⇐", "DoubleLeftRightArrow;": "⇔", "DoubleLeftTee;": "⫤", "DoubleLongLeftArrow;": "⟸", "DoubleLongLeftRightArrow;": "⟺", "DoubleLongRightArrow;": "⟹", "DoubleRightArrow;": "⇒", "DoubleRightTee;": "⊨", "DoubleUpArrow;": "⇑", "DoubleUpDownArrow;": "⇕", "DoubleVerticalBar;": "∥", "DownArrow;": "↓", "DownArrowBar;": "⤓", "DownArrowUpArrow;": "⇵", "DownBreve;": "̑", "DownLeftRightVector;": "⥐", "DownLeftTeeVector;": "⥞", "DownLeftVector;": "↽", "DownLeftVectorBar;": "⥖", "DownRightTeeVector;": "⥟", "DownRightVector;": "⇁", "DownRightVectorBar;": "⥗", "DownTee;": "⊤", "DownTeeArrow;": "↧", "Downarrow;": "⇓", "Dscr;": "ᵉF", "Dstrok;": "Đ", "ENG;": "Ŋ", ETH: "Ð", "ETH;": "Ð", Eacute: "É", "Eacute;": "É", "Ecaron;": "Ě", Ecirc: "Ê", "Ecirc;": "Ê", "Ecy;": "Э", "Edot;": "Ė", "Efr;": "ᵐ8", Egrave: "È", "Egrave;": "È", "Element;": "∈", "Emacr;": "Ē", "EmptySmallSquare;": "◻", "EmptyVerySmallSquare;": "▫", "Eogon;": "Ę", "Eopf;": "ᵓC", "Epsilon;": "Ε", "Equal;": "⩵", "EqualTilde;": "≂", "Equilibrium;": "⇌", "Escr;": "ℰ", "Esim;": "⩳", "Eta;": "Η", Euml: "Ë", "Euml;": "Ë", "Exists;": "∃", "ExponentialE;": "ⅇ", "Fcy;": "Ф", "Ffr;": "ᵐ9", "FilledSmallSquare;": "◼", "FilledVerySmallSquare;": "▪", "Fopf;": "ᵓD", "ForAll;": "∀", "Fouriertrf;": "ℱ", "Fscr;": "ℱ", "GJcy;": "Ѓ", GT: ">", "GT;": ">", "Gamma;": "Γ", "Gammad;": "Ϝ", "Gbreve;": "Ğ", "Gcedil;": "Ģ", "Gcirc;": "Ĝ", "Gcy;": "Г", "Gdot;": "Ġ", "Gfr;": "ᵐA", "Gg;": "⋙", "Gopf;": "ᵓE", "GreaterEqual;": "≥", "GreaterEqualLess;": "⋛", "GreaterFullEqual;": "≧", "GreaterGreater;": "⪢", "GreaterLess;": "≷", "GreaterSlantEqual;": "⩾", "GreaterTilde;": "≳", "Gscr;": "ᵊ2", "Gt;": "≫", "HARDcy;": "Ъ", "Hacek;": "ˇ", "Hat;": "^", "Hcirc;": "Ĥ", "Hfr;": "ℌ", "HilbertSpace;": "ℋ", "Hopf;": "ℍ", "HorizontalLine;": "─", "Hscr;": "ℋ", "Hstrok;": "Ħ", "HumpDownHump;": "≎", "HumpEqual;": "≏", "IEcy;": "Е", "IJlig;": "Ĳ", "IOcy;": "Ё", Iacute: "Í", "Iacute;": "Í", Icirc: "Î", "Icirc;": "Î", "Icy;": "И", "Idot;": "İ", "Ifr;": "ℑ", Igrave: "Ì", "Igrave;": "Ì", "Im;": "ℑ", "Imacr;": "Ī", "ImaginaryI;": "ⅈ", "Implies;": "⇒", "Int;": "∬", "Integral;": "∫", "Intersection;": "⋂", "InvisibleComma;": "⁣", "InvisibleTimes;": "⁢", "Iogon;": "Į", "Iopf;": "ᵔ0", "Iota;": "Ι", "Iscr;": "ℐ", "Itilde;": "Ĩ", "Iukcy;": "І", Iuml: "Ï", "Iuml;": "Ï", "Jcirc;": "Ĵ", "Jcy;": "Й", "Jfr;": "ᵐD", "Jopf;": "ᵔ1", "Jscr;": "ᵊ5", "Jsercy;": "Ј", "Jukcy;": "Є", "KHcy;": "Х", "KJcy;": "Ќ", "Kappa;": "Κ", "Kcedil;": "Ķ", "Kcy;": "К", "Kfr;": "ᵐE", "Kopf;": "ᵔ2", "Kscr;": "ᵊ6", "LJcy;": "Љ", LT: "<", "LT;": "<", "Lacute;": "Ĺ", "Lambda;": "Λ", "Lang;": "⟪", "Laplacetrf;": "ℒ", "Larr;": "↞", "Lcaron;": "Ľ", "Lcedil;": "Ļ", "Lcy;": "Л", "LeftAngleBracket;": "⟨", "LeftArrow;": "←", "LeftArrowBar;": "⇤", "LeftArrowRightArrow;": "⇆", "LeftCeiling;": "⌈", "LeftDoubleBracket;": "⟦", "LeftDownTeeVector;": "⥡", "LeftDownVector;": "⇃", "LeftDownVectorBar;": "⥙", "LeftFloor;": "⌊", "LeftRightArrow;": "↔", "LeftRightVector;": "⥎", "LeftTee;": "⊣", "LeftTeeArrow;": "↤", "LeftTeeVector;": "⥚", "LeftTriangle;": "⊲", "LeftTriangleBar;": "⧏", "LeftTriangleEqual;": "⊴", "LeftUpDownVector;": "⥑", "LeftUpTeeVector;": "⥠", "LeftUpVector;": "↿", "LeftUpVectorBar;": "⥘", "LeftVector;": "↼", "LeftVectorBar;": "⥒", "Leftarrow;": "⇐", "Leftrightarrow;": "⇔", "LessEqualGreater;": "⋚", "LessFullEqual;": "≦", "LessGreater;": "≶", "LessLess;": "⪡", "LessSlantEqual;": "⩽", "LessTilde;": "≲", "Lfr;": "ᵐF", "Ll;": "⋘", "Lleftarrow;": "⇚", "Lmidot;": "Ŀ", "LongLeftArrow;": "⟵", "LongLeftRightArrow;": "⟷", "LongRightArrow;": "⟶", "Longleftarrow;": "⟸", "Longleftrightarrow;": "⟺", "Longrightarrow;": "⟹", "Lopf;": "ᵔ3", "LowerLeftArrow;": "↙", "LowerRightArrow;": "↘", "Lscr;": "ℒ", "Lsh;": "↰", "Lstrok;": "Ł", "Lt;": "≪", "Map;": "⤅", "Mcy;": "М", "MediumSpace;": " ", "Mellintrf;": "ℳ", "Mfr;": "ᵑ0", "MinusPlus;": "∓", "Mopf;": "ᵔ4", "Mscr;": "ℳ", "Mu;": "Μ", "NJcy;": "Њ", "Nacute;": "Ń", "Ncaron;": "Ň", "Ncedil;": "Ņ", "Ncy;": "Н", "NegativeMediumSpace;": "​", "NegativeThickSpace;": "​", "NegativeThinSpace;": "​", "NegativeVeryThinSpace;": "​", "NestedGreaterGreater;": "≫", "NestedLessLess;": "≪", "NewLine;": "\n", "Nfr;": "ᵑ1", "NoBreak;": "⁠", "NonBreakingSpace;": " ", "Nopf;": "ℕ", "Not;": "⫬", "NotCongruent;": "≢", "NotCupCap;": "≭", "NotDoubleVerticalBar;": "∦", "NotElement;": "∉", "NotEqual;": "≠", "NotEqualTilde;": "≂̸", "NotExists;": "∄", "NotGreater;": "≯", "NotGreaterEqual;": "≱", "NotGreaterFullEqual;": "≧̸", "NotGreaterGreater;": "≫̸", "NotGreaterLess;": "≹", "NotGreaterSlantEqual;": "⩾̸", "NotGreaterTilde;": "≵", "NotHumpDownHump;": "≎̸", "NotHumpEqual;": "≏̸", "NotLeftTriangle;": "⋪", "NotLeftTriangleBar;": "⧏̸", "NotLeftTriangleEqual;": "⋬", "NotLess;": "≮", "NotLessEqual;": "≰", "NotLessGreater;": "≸", "NotLessLess;": "≪̸", "NotLessSlantEqual;": "⩽̸", "NotLessTilde;": "≴", "NotNestedGreaterGreater;": "⪢̸", "NotNestedLessLess;": "⪡̸", "NotPrecedes;": "⊀", "NotPrecedesEqual;": "⪯̸", "NotPrecedesSlantEqual;": "⋠", "NotReverseElement;": "∌", "NotRightTriangle;": "⋫", "NotRightTriangleBar;": "⧐̸", "NotRightTriangleEqual;": "⋭", "NotSquareSubset;": "⊏̸", "NotSquareSubsetEqual;": "⋢", "NotSquareSuperset;": "⊐̸", "NotSquareSupersetEqual;": "⋣", "NotSubset;": "⊂⃒", "NotSubsetEqual;": "⊈", "NotSucceeds;": "⊁", "NotSucceedsEqual;": "⪰̸", "NotSucceedsSlantEqual;": "⋡", "NotSucceedsTilde;": "≿̸", "NotSuperset;": "⊃⃒", "NotSupersetEqual;": "⊉", "NotTilde;": "≁", "NotTildeEqual;": "≄", "NotTildeFullEqual;": "≇", "NotTildeTilde;": "≉", "NotVerticalBar;": "∤", "Nscr;": "ᵊ9", Ntilde: "Ñ", "Ntilde;": "Ñ", "Nu;": "Ν", "OElig;": "Œ", Oacute: "Ó", "Oacute;": "Ó", Ocirc: "Ô", "Ocirc;": "Ô", "Ocy;": "О", "Odblac;": "Ő", "Ofr;": "ᵑ2", Ograve: "Ò", "Ograve;": "Ò", "Omacr;": "Ō", "Omega;": "Ω", "Omicron;": "Ο", "Oopf;": "ᵔ6", "OpenCurlyDoubleQuote;": "“", "OpenCurlyQuote;": "‘", "Or;": "⩔", "Oscr;": "ᵊA", Oslash: "Ø", "Oslash;": "Ø", Otilde: "Õ", "Otilde;": "Õ", "Otimes;": "⨷", Ouml: "Ö", "Ouml;": "Ö", "OverBar;": "‾", "OverBrace;": "⏞", "OverBracket;": "⎴", "OverParenthesis;": "⏜", "PartialD;": "∂", "Pcy;": "П", "Pfr;": "ᵑ3", "Phi;": "Φ", "Pi;": "Π", "PlusMinus;": "±", "Poincareplane;": "ℌ", "Popf;": "ℙ", "Pr;": "⪻", "Precedes;": "≺", "PrecedesEqual;": "⪯", "PrecedesSlantEqual;": "≼", "PrecedesTilde;": "≾", "Prime;": "″", "Product;": "∏", "Proportion;": "∷", "Proportional;": "∝", "Pscr;": "ᵊB", "Psi;": "Ψ", QUOT: '"', "QUOT;": '"', "Qfr;": "ᵑ4", "Qopf;": "ℚ", "Qscr;": "ᵊC", "RBarr;": "⤐", REG: "®", "REG;": "®", "Racute;": "Ŕ", "Rang;": "⟫", "Rarr;": "↠", "Rarrtl;": "⤖", "Rcaron;": "Ř", "Rcedil;": "Ŗ", "Rcy;": "Р", "Re;": "ℜ", "ReverseElement;": "∋", "ReverseEquilibrium;": "⇋", "ReverseUpEquilibrium;": "⥯", "Rfr;": "ℜ", "Rho;": "Ρ", "RightAngleBracket;": "⟩", "RightArrow;": "→", "RightArrowBar;": "⇥", "RightArrowLeftArrow;": "⇄", "RightCeiling;": "⌉", "RightDoubleBracket;": "⟧", "RightDownTeeVector;": "⥝", "RightDownVector;": "⇂", "RightDownVectorBar;": "⥕", "RightFloor;": "⌋", "RightTee;": "⊢", "RightTeeArrow;": "↦", "RightTeeVector;": "⥛", "RightTriangle;": "⊳", "RightTriangleBar;": "⧐", "RightTriangleEqual;": "⊵", "RightUpDownVector;": "⥏", "RightUpTeeVector;": "⥜", "RightUpVector;": "↾", "RightUpVectorBar;": "⥔", "RightVector;": "⇀", "RightVectorBar;": "⥓", "Rightarrow;": "⇒", "Ropf;": "ℝ", "RoundImplies;": "⥰", "Rrightarrow;": "⇛", "Rscr;": "ℛ", "Rsh;": "↱", "RuleDelayed;": "⧴", "SHCHcy;": "Щ", "SHcy;": "Ш", "SOFTcy;": "Ь", "Sacute;": "Ś", "Sc;": "⪼", "Scaron;": "Š", "Scedil;": "Ş", "Scirc;": "Ŝ", "Scy;": "С", "Sfr;": "ᵑ6", "ShortDownArrow;": "↓", "ShortLeftArrow;": "←", "ShortRightArrow;": "→", "ShortUpArrow;": "↑", "Sigma;": "Σ", "SmallCircle;": "∘", "Sopf;": "ᵔA", "Sqrt;": "√", "Square;": "□", "SquareIntersection;": "⊓", "SquareSubset;": "⊏", "SquareSubsetEqual;": "⊑", "SquareSuperset;": "⊐", "SquareSupersetEqual;": "⊒", "SquareUnion;": "⊔", "Sscr;": "ᵊE", "Star;": "⋆", "Sub;": "⋐", "Subset;": "⋐", "SubsetEqual;": "⊆", "Succeeds;": "≻", "SucceedsEqual;": "⪰", "SucceedsSlantEqual;": "≽", "SucceedsTilde;": "≿", "SuchThat;": "∋", "Sum;": "∑", "Sup;": "⋑", "Superset;": "⊃", "SupersetEqual;": "⊇", "Supset;": "⋑", THORN: "Þ", "THORN;": "Þ", "TRADE;": "™", "TSHcy;": "Ћ", "TScy;": "Ц", "Tab;": "	", "Tau;": "Τ", "Tcaron;": "Ť", "Tcedil;": "Ţ", "Tcy;": "Т", "Tfr;": "ᵑ7", "Therefore;": "∴", "Theta;": "Θ", "ThickSpace;": "  ", "ThinSpace;": " ", "Tilde;": "∼", "TildeEqual;": "≃", "TildeFullEqual;": "≅", "TildeTilde;": "≈", "Topf;": "ᵔB", "TripleDot;": "⃛", "Tscr;": "ᵊF", "Tstrok;": "Ŧ", Uacute: "Ú", "Uacute;": "Ú", "Uarr;": "↟", "Uarrocir;": "⥉", "Ubrcy;": "Ў", "Ubreve;": "Ŭ", Ucirc: "Û", "Ucirc;": "Û", "Ucy;": "У", "Udblac;": "Ű", "Ufr;": "ᵑ8", Ugrave: "Ù", "Ugrave;": "Ù", "Umacr;": "Ū", "UnderBar;": "_", "UnderBrace;": "⏟", "UnderBracket;": "⎵", "UnderParenthesis;": "⏝", "Union;": "⋃", "UnionPlus;": "⊎", "Uogon;": "Ų", "Uopf;": "ᵔC", "UpArrow;": "↑", "UpArrowBar;": "⤒", "UpArrowDownArrow;": "⇅", "UpDownArrow;": "↕", "UpEquilibrium;": "⥮", "UpTee;": "⊥", "UpTeeArrow;": "↥", "Uparrow;": "⇑", "Updownarrow;": "⇕", "UpperLeftArrow;": "↖", "UpperRightArrow;": "↗", "Upsi;": "ϒ", "Upsilon;": "Υ", "Uring;": "Ů", "Uscr;": "ᵋ0", "Utilde;": "Ũ", Uuml: "Ü", "Uuml;": "Ü", "VDash;": "⊫", "Vbar;": "⫫", "Vcy;": "В", "Vdash;": "⊩", "Vdashl;": "⫦", "Vee;": "⋁", "Verbar;": "‖", "Vert;": "‖", "VerticalBar;": "∣", "VerticalLine;": "|", "VerticalSeparator;": "❘", "VerticalTilde;": "≀", "VeryThinSpace;": " ", "Vfr;": "ᵑ9", "Vopf;": "ᵔD", "Vscr;": "ᵋ1", "Vvdash;": "⊪", "Wcirc;": "Ŵ", "Wedge;": "⋀", "Wfr;": "ᵑA", "Wopf;": "ᵔE", "Wscr;": "ᵋ2", "Xfr;": "ᵑB", "Xi;": "Ξ", "Xopf;": "ᵔF", "Xscr;": "ᵋ3", "YAcy;": "Я", "YIcy;": "Ї", "YUcy;": "Ю", Yacute: "Ý", "Yacute;": "Ý", "Ycirc;": "Ŷ", "Ycy;": "Ы", "Yfr;": "ᵑC", "Yopf;": "ᵕ0", "Yscr;": "ᵋ4", "Yuml;": "Ÿ", "ZHcy;": "Ж", "Zacute;": "Ź", "Zcaron;": "Ž", "Zcy;": "З", "Zdot;": "Ż", "ZeroWidthSpace;": "​", "Zeta;": "Ζ", "Zfr;": "ℨ", "Zopf;": "ℤ", "Zscr;": "ᵋ5", aacute: "á", "aacute;": "á", "abreve;": "ă", "ac;": "∾", "acE;": "∾̳", "acd;": "∿", acirc: "â", "acirc;": "â", acute: "´", "acute;": "´", "acy;": "а", aelig: "æ", "aelig;": "æ", "af;": "⁡", "afr;": "ᵑE", agrave: "à", "agrave;": "à", "alefsym;": "ℵ", "aleph;": "ℵ", "alpha;": "α", "amacr;": "ā", "amalg;": "⨿", amp: "&", "amp;": "&", "and;": "∧", "andand;": "⩕", "andd;": "⩜", "andslope;": "⩘", "andv;": "⩚", "ang;": "∠", "ange;": "⦤", "angle;": "∠", "angmsd;": "∡", "angmsdaa;": "⦨", "angmsdab;": "⦩", "angmsdac;": "⦪", "angmsdad;": "⦫", "angmsdae;": "⦬", "angmsdaf;": "⦭", "angmsdag;": "⦮", "angmsdah;": "⦯", "angrt;": "∟", "angrtvb;": "⊾", "angrtvbd;": "⦝", "angsph;": "∢", "angst;": "Å", "angzarr;": "⍼", "aogon;": "ą", "aopf;": "ᵕ2", "ap;": "≈", "apE;": "⩰", "apacir;": "⩯", "ape;": "≊", "apid;": "≋", "apos;": "'", "approx;": "≈", "approxeq;": "≊", aring: "å", "aring;": "å", "ascr;": "ᵋ6", "ast;": "*", "asymp;": "≈", "asympeq;": "≍", atilde: "ã", "atilde;": "ã", auml: "ä", "auml;": "ä", "awconint;": "∳", "awint;": "⨑", "bNot;": "⫭", "backcong;": "≌", "backepsilon;": "϶", "backprime;": "‵", "backsim;": "∽", "backsimeq;": "⋍", "barvee;": "⊽", "barwed;": "⌅", "barwedge;": "⌅", "bbrk;": "⎵", "bbrktbrk;": "⎶", "bcong;": "≌", "bcy;": "б", "bdquo;": "„", "becaus;": "∵", "because;": "∵", "bemptyv;": "⦰", "bepsi;": "϶", "bernou;": "ℬ", "beta;": "β", "beth;": "ℶ", "between;": "≬", "bfr;": "ᵑF", "bigcap;": "⋂", "bigcirc;": "◯", "bigcup;": "⋃", "bigodot;": "⨀", "bigoplus;": "⨁", "bigotimes;": "⨂", "bigsqcup;": "⨆", "bigstar;": "★", "bigtriangledown;": "▽", "bigtriangleup;": "△", "biguplus;": "⨄", "bigvee;": "⋁", "bigwedge;": "⋀", "bkarow;": "⤍", "blacklozenge;": "⧫", "blacksquare;": "▪", "blacktriangle;": "▴", "blacktriangledown;": "▾", "blacktriangleleft;": "◂", "blacktriangleright;": "▸", "blank;": "␣", "blk12;": "▒", "blk14;": "░", "blk34;": "▓", "block;": "█", "bne;": "=⃥", "bnequiv;": "≡⃥", "bnot;": "⌐", "bopf;": "ᵕ3", "bot;": "⊥", "bottom;": "⊥", "bowtie;": "⋈", "boxDL;": "╗", "boxDR;": "╔", "boxDl;": "╖", "boxDr;": "╓", "boxH;": "═", "boxHD;": "╦", "boxHU;": "╩", "boxHd;": "╤", "boxHu;": "╧", "boxUL;": "╝", "boxUR;": "╚", "boxUl;": "╜", "boxUr;": "╙", "boxV;": "║", "boxVH;": "╬", "boxVL;": "╣", "boxVR;": "╠", "boxVh;": "╫", "boxVl;": "╢", "boxVr;": "╟", "boxbox;": "⧉", "boxdL;": "╕", "boxdR;": "╒", "boxdl;": "┐", "boxdr;": "┌", "boxh;": "─", "boxhD;": "╥", "boxhU;": "╨", "boxhd;": "┬", "boxhu;": "┴", "boxminus;": "⊟", "boxplus;": "⊞", "boxtimes;": "⊠", "boxuL;": "╛", "boxuR;": "╘", "boxul;": "┘", "boxur;": "└", "boxv;": "│", "boxvH;": "╪", "boxvL;": "╡", "boxvR;": "╞", "boxvh;": "┼", "boxvl;": "┤", "boxvr;": "├", "bprime;": "‵", "breve;": "˘", brvbar: "¦", "brvbar;": "¦", "bscr;": "ᵋ7", "bsemi;": "⁏", "bsim;": "∽", "bsime;": "⋍", "bsol;": "\\", "bsolb;": "⧅", "bsolhsub;": "⟈", "bull;": "•", "bullet;": "•", "bump;": "≎", "bumpE;": "⪮", "bumpe;": "≏", "bumpeq;": "≏", "cacute;": "ć", "cap;": "∩", "capand;": "⩄", "capbrcup;": "⩉", "capcap;": "⩋", "capcup;": "⩇", "capdot;": "⩀", "caps;": "∩︀", "caret;": "⁁", "caron;": "ˇ", "ccaps;": "⩍", "ccaron;": "č", ccedil: "ç", "ccedil;": "ç", "ccirc;": "ĉ", "ccups;": "⩌", "ccupssm;": "⩐", "cdot;": "ċ", cedil: "¸", "cedil;": "¸", "cemptyv;": "⦲", cent: "¢", "cent;": "¢", "centerdot;": "·", "cfr;": "ᵒ0", "chcy;": "ч", "check;": "✓", "checkmark;": "✓", "chi;": "χ", "cir;": "○", "cirE;": "⧃", "circ;": "ˆ", "circeq;": "≗", "circlearrowleft;": "↺", "circlearrowright;": "↻", "circledR;": "®", "circledS;": "Ⓢ", "circledast;": "⊛", "circledcirc;": "⊚", "circleddash;": "⊝", "cire;": "≗", "cirfnint;": "⨐", "cirmid;": "⫯", "cirscir;": "⧂", "clubs;": "♣", "clubsuit;": "♣", "colon;": ":", "colone;": "≔", "coloneq;": "≔", "comma;": ",", "commat;": "@", "comp;": "∁", "compfn;": "∘", "complement;": "∁", "complexes;": "ℂ", "cong;": "≅", "congdot;": "⩭", "conint;": "∮", "copf;": "ᵕ4", "coprod;": "∐", copy: "©", "copy;": "©", "copysr;": "℗", "crarr;": "↵", "cross;": "✗", "cscr;": "ᵋ8", "csub;": "⫏", "csube;": "⫑", "csup;": "⫐", "csupe;": "⫒", "ctdot;": "⋯", "cudarrl;": "⤸", "cudarrr;": "⤵", "cuepr;": "⋞", "cuesc;": "⋟", "cularr;": "↶", "cularrp;": "⤽", "cup;": "∪", "cupbrcap;": "⩈", "cupcap;": "⩆", "cupcup;": "⩊", "cupdot;": "⊍", "cupor;": "⩅", "cups;": "∪︀", "curarr;": "↷", "curarrm;": "⤼", "curlyeqprec;": "⋞", "curlyeqsucc;": "⋟", "curlyvee;": "⋎", "curlywedge;": "⋏", curren: "¤", "curren;": "¤", "curvearrowleft;": "↶", "curvearrowright;": "↷", "cuvee;": "⋎", "cuwed;": "⋏", "cwconint;": "∲", "cwint;": "∱", "cylcty;": "⌭", "dArr;": "⇓", "dHar;": "⥥", "dagger;": "†", "daleth;": "ℸ", "darr;": "↓", "dash;": "‐", "dashv;": "⊣", "dbkarow;": "⤏", "dblac;": "˝", "dcaron;": "ď", "dcy;": "д", "dd;": "ⅆ", "ddagger;": "‡", "ddarr;": "⇊", "ddotseq;": "⩷", deg: "°", "deg;": "°", "delta;": "δ", "demptyv;": "⦱", "dfisht;": "⥿", "dfr;": "ᵒ1", "dharl;": "⇃", "dharr;": "⇂", "diam;": "⋄", "diamond;": "⋄", "diamondsuit;": "♦", "diams;": "♦", "die;": "¨", "digamma;": "ϝ", "disin;": "⋲", "div;": "÷", divide: "÷", "divide;": "÷", "divideontimes;": "⋇", "divonx;": "⋇", "djcy;": "ђ", "dlcorn;": "⌞", "dlcrop;": "⌍", "dollar;": "$", "dopf;": "ᵕ5", "dot;": "˙", "doteq;": "≐", "doteqdot;": "≑", "dotminus;": "∸", "dotplus;": "∔", "dotsquare;": "⊡", "doublebarwedge;": "⌆", "downarrow;": "↓", "downdownarrows;": "⇊", "downharpoonleft;": "⇃", "downharpoonright;": "⇂", "drbkarow;": "⤐", "drcorn;": "⌟", "drcrop;": "⌌", "dscr;": "ᵋ9", "dscy;": "ѕ", "dsol;": "⧶", "dstrok;": "đ", "dtdot;": "⋱", "dtri;": "▿", "dtrif;": "▾", "duarr;": "⇵", "duhar;": "⥯", "dwangle;": "⦦", "dzcy;": "џ", "dzigrarr;": "⟿", "eDDot;": "⩷", "eDot;": "≑", eacute: "é", "eacute;": "é", "easter;": "⩮", "ecaron;": "ě", "ecir;": "≖", ecirc: "ê", "ecirc;": "ê", "ecolon;": "≕", "ecy;": "э", "edot;": "ė", "ee;": "ⅇ", "efDot;": "≒", "efr;": "ᵒ2", "eg;": "⪚", egrave: "è", "egrave;": "è", "egs;": "⪖", "egsdot;": "⪘", "el;": "⪙", "elinters;": "⏧", "ell;": "ℓ", "els;": "⪕", "elsdot;": "⪗", "emacr;": "ē", "empty;": "∅", "emptyset;": "∅", "emptyv;": "∅", "emsp13;": " ", "emsp14;": " ", "emsp;": " ", "eng;": "ŋ", "ensp;": " ", "eogon;": "ę", "eopf;": "ᵕ6", "epar;": "⋕", "eparsl;": "⧣", "eplus;": "⩱", "epsi;": "ε", "epsilon;": "ε", "epsiv;": "ϵ", "eqcirc;": "≖", "eqcolon;": "≕", "eqsim;": "≂", "eqslantgtr;": "⪖", "eqslantless;": "⪕", "equals;": "=", "equest;": "≟", "equiv;": "≡", "equivDD;": "⩸", "eqvparsl;": "⧥", "erDot;": "≓", "erarr;": "⥱", "escr;": "ℯ", "esdot;": "≐", "esim;": "≂", "eta;": "η", eth: "ð", "eth;": "ð", euml: "ë", "euml;": "ë", "euro;": "€", "excl;": "!", "exist;": "∃", "expectation;": "ℰ", "exponentiale;": "ⅇ", "fallingdotseq;": "≒", "fcy;": "ф", "female;": "♀", "ffilig;": "ﬃ", "fflig;": "ﬀ", "ffllig;": "ﬄ", "ffr;": "ᵒ3", "filig;": "ﬁ", "fjlig;": "f", "flat;": "♭", "fllig;": "ﬂ", "fltns;": "▱", "fnof;": "ƒ", "fopf;": "ᵕ7", "forall;": "∀", "fork;": "⋔", "forkv;": "⫙", "fpartint;": "⨍", frac12: "½", "frac12;": "½", "frac13;": "⅓", frac14: "¼", "frac14;": "¼", "frac15;": "⅕", "frac16;": "⅙", "frac18;": "⅛", "frac23;": "⅔", "frac25;": "⅖", frac34: "¾", "frac34;": "¾", "frac35;": "⅗", "frac38;": "⅜", "frac45;": "⅘", "frac56;": "⅚", "frac58;": "⅝", "frac78;": "⅞", "frasl;": "⁄", "frown;": "⌢", "fscr;": "ᵋB", "gE;": "≧", "gEl;": "⪌", "gacute;": "ǵ", "gamma;": "γ", "gammad;": "ϝ", "gap;": "⪆", "gbreve;": "ğ", "gcirc;": "ĝ", "gcy;": "г", "gdot;": "ġ", "ge;": "≥", "gel;": "⋛", "geq;": "≥", "geqq;": "≧", "geqslant;": "⩾", "ges;": "⩾", "gescc;": "⪩", "gesdot;": "⪀", "gesdoto;": "⪂", "gesdotol;": "⪄", "gesl;": "⋛︀", "gesles;": "⪔", "gfr;": "ᵒ4", "gg;": "≫", "ggg;": "⋙", "gimel;": "ℷ", "gjcy;": "ѓ", "gl;": "≷", "glE;": "⪒", "gla;": "⪥", "glj;": "⪤", "gnE;": "≩", "gnap;": "⪊", "gnapprox;": "⪊", "gne;": "⪈", "gneq;": "⪈", "gneqq;": "≩", "gnsim;": "⋧", "gopf;": "ᵕ8", "grave;": "`", "gscr;": "ℊ", "gsim;": "≳", "gsime;": "⪎", "gsiml;": "⪐", gt: ">", "gt;": ">", "gtcc;": "⪧", "gtcir;": "⩺", "gtdot;": "⋗", "gtlPar;": "⦕", "gtquest;": "⩼", "gtrapprox;": "⪆", "gtrarr;": "⥸", "gtrdot;": "⋗", "gtreqless;": "⋛", "gtreqqless;": "⪌", "gtrless;": "≷", "gtrsim;": "≳", "gvertneqq;": "≩︀", "gvnE;": "≩︀", "hArr;": "⇔", "hairsp;": " ", "half;": "½", "hamilt;": "ℋ", "hardcy;": "ъ", "harr;": "↔", "harrcir;": "⥈", "harrw;": "↭", "hbar;": "ℏ", "hcirc;": "ĥ", "hearts;": "♥", "heartsuit;": "♥", "hellip;": "…", "hercon;": "⊹", "hfr;": "ᵒ5", "hksearow;": "⤥", "hkswarow;": "⤦", "hoarr;": "⇿", "homtht;": "∻", "hookleftarrow;": "↩", "hookrightarrow;": "↪", "hopf;": "ᵕ9", "horbar;": "―", "hscr;": "ᵋD", "hslash;": "ℏ", "hstrok;": "ħ", "hybull;": "⁃", "hyphen;": "‐", iacute: "í", "iacute;": "í", "ic;": "⁣", icirc: "î", "icirc;": "î", "icy;": "и", "iecy;": "е", iexcl: "¡", "iexcl;": "¡", "iff;": "⇔", "ifr;": "ᵒ6", igrave: "ì", "igrave;": "ì", "ii;": "ⅈ", "iiiint;": "⨌", "iiint;": "∭", "iinfin;": "⧜", "iiota;": "℩", "ijlig;": "ĳ", "imacr;": "ī", "image;": "ℑ", "imagline;": "ℐ", "imagpart;": "ℑ", "imath;": "ı", "imof;": "⊷", "imped;": "Ƶ", "in;": "∈", "incare;": "℅", "infin;": "∞", "infintie;": "⧝", "inodot;": "ı", "int;": "∫", "intcal;": "⊺", "integers;": "ℤ", "intercal;": "⊺", "intlarhk;": "⨗", "intprod;": "⨼", "iocy;": "ё", "iogon;": "į", "iopf;": "ᵕA", "iota;": "ι", "iprod;": "⨼", iquest: "¿", "iquest;": "¿", "iscr;": "ᵋE", "isin;": "∈", "isinE;": "⋹", "isindot;": "⋵", "isins;": "⋴", "isinsv;": "⋳", "isinv;": "∈", "it;": "⁢", "itilde;": "ĩ", "iukcy;": "і", iuml: "ï", "iuml;": "ï", "jcirc;": "ĵ", "jcy;": "й", "jfr;": "ᵒ7", "jmath;": "ȷ", "jopf;": "ᵕB", "jscr;": "ᵋF", "jsercy;": "ј", "jukcy;": "є", "kappa;": "κ", "kappav;": "ϰ", "kcedil;": "ķ", "kcy;": "к", "kfr;": "ᵒ8", "kgreen;": "ĸ", "khcy;": "х", "kjcy;": "ќ", "kopf;": "ᵕC", "kscr;": "ᵌ0", "lAarr;": "⇚", "lArr;": "⇐", "lAtail;": "⤛", "lBarr;": "⤎", "lE;": "≦", "lEg;": "⪋", "lHar;": "⥢", "lacute;": "ĺ", "laemptyv;": "⦴", "lagran;": "ℒ", "lambda;": "λ", "lang;": "⟨", "langd;": "⦑", "langle;": "⟨", "lap;": "⪅", laquo: "«", "laquo;": "«", "larr;": "←", "larrb;": "⇤", "larrbfs;": "⤟", "larrfs;": "⤝", "larrhk;": "↩", "larrlp;": "↫", "larrpl;": "⤹", "larrsim;": "⥳", "larrtl;": "↢", "lat;": "⪫", "latail;": "⤙", "late;": "⪭", "lates;": "⪭︀", "lbarr;": "⤌", "lbbrk;": "❲", "lbrace;": "{", "lbrack;": "[", "lbrke;": "⦋", "lbrksld;": "⦏", "lbrkslu;": "⦍", "lcaron;": "ľ", "lcedil;": "ļ", "lceil;": "⌈", "lcub;": "{", "lcy;": "л", "ldca;": "⤶", "ldquo;": "“", "ldquor;": "„", "ldrdhar;": "⥧", "ldrushar;": "⥋", "ldsh;": "↲", "le;": "≤", "leftarrow;": "←", "leftarrowtail;": "↢", "leftharpoondown;": "↽", "leftharpoonup;": "↼", "leftleftarrows;": "⇇", "leftrightarrow;": "↔", "leftrightarrows;": "⇆", "leftrightharpoons;": "⇋", "leftrightsquigarrow;": "↭", "leftthreetimes;": "⋋", "leg;": "⋚", "leq;": "≤", "leqq;": "≦", "leqslant;": "⩽", "les;": "⩽", "lescc;": "⪨", "lesdot;": "⩿", "lesdoto;": "⪁", "lesdotor;": "⪃", "lesg;": "⋚︀", "lesges;": "⪓", "lessapprox;": "⪅", "lessdot;": "⋖", "lesseqgtr;": "⋚", "lesseqqgtr;": "⪋", "lessgtr;": "≶", "lesssim;": "≲", "lfisht;": "⥼", "lfloor;": "⌊", "lfr;": "ᵒ9", "lg;": "≶", "lgE;": "⪑", "lhard;": "↽", "lharu;": "↼", "lharul;": "⥪", "lhblk;": "▄", "ljcy;": "љ", "ll;": "≪", "llarr;": "⇇", "llcorner;": "⌞", "llhard;": "⥫", "lltri;": "◺", "lmidot;": "ŀ", "lmoust;": "⎰", "lmoustache;": "⎰", "lnE;": "≨", "lnap;": "⪉", "lnapprox;": "⪉", "lne;": "⪇", "lneq;": "⪇", "lneqq;": "≨", "lnsim;": "⋦", "loang;": "⟬", "loarr;": "⇽", "lobrk;": "⟦", "longleftarrow;": "⟵", "longleftrightarrow;": "⟷", "longmapsto;": "⟼", "longrightarrow;": "⟶", "looparrowleft;": "↫", "looparrowright;": "↬", "lopar;": "⦅", "lopf;": "ᵕD", "loplus;": "⨭", "lotimes;": "⨴", "lowast;": "∗", "lowbar;": "_", "loz;": "◊", "lozenge;": "◊", "lozf;": "⧫", "lpar;": "(", "lparlt;": "⦓", "lrarr;": "⇆", "lrcorner;": "⌟", "lrhar;": "⇋", "lrhard;": "⥭", "lrm;": "‎", "lrtri;": "⊿", "lsaquo;": "‹", "lscr;": "ᵌ1", "lsh;": "↰", "lsim;": "≲", "lsime;": "⪍", "lsimg;": "⪏", "lsqb;": "[", "lsquo;": "‘", "lsquor;": "‚", "lstrok;": "ł", lt: "<", "lt;": "<", "ltcc;": "⪦", "ltcir;": "⩹", "ltdot;": "⋖", "lthree;": "⋋", "ltimes;": "⋉", "ltlarr;": "⥶", "ltquest;": "⩻", "ltrPar;": "⦖", "ltri;": "◃", "ltrie;": "⊴", "ltrif;": "◂", "lurdshar;": "⥊", "luruhar;": "⥦", "lvertneqq;": "≨︀", "lvnE;": "≨︀", "mDDot;": "∺", macr: "¯", "macr;": "¯", "male;": "♂", "malt;": "✠", "maltese;": "✠", "map;": "↦", "mapsto;": "↦", "mapstodown;": "↧", "mapstoleft;": "↤", "mapstoup;": "↥", "marker;": "▮", "mcomma;": "⨩", "mcy;": "м", "mdash;": "—", "measuredangle;": "∡", "mfr;": "ᵒA", "mho;": "℧", micro: "µ", "micro;": "µ", "mid;": "∣", "midast;": "*", "midcir;": "⫰", middot: "·", "middot;": "·", "minus;": "−", "minusb;": "⊟", "minusd;": "∸", "minusdu;": "⨪", "mlcp;": "⫛", "mldr;": "…", "mnplus;": "∓", "models;": "⊧", "mopf;": "ᵕE", "mp;": "∓", "mscr;": "ᵌ2", "mstpos;": "∾", "mu;": "μ", "multimap;": "⊸", "mumap;": "⊸", "nGg;": "⋙̸", "nGt;": "≫⃒", "nGtv;": "≫̸", "nLeftarrow;": "⇍", "nLeftrightarrow;": "⇎", "nLl;": "⋘̸", "nLt;": "≪⃒", "nLtv;": "≪̸", "nRightarrow;": "⇏", "nVDash;": "⊯", "nVdash;": "⊮", "nabla;": "∇", "nacute;": "ń", "nang;": "∠⃒", "nap;": "≉", "napE;": "⩰̸", "napid;": "≋̸", "napos;": "ŉ", "napprox;": "≉", "natur;": "♮", "natural;": "♮", "naturals;": "ℕ", nbsp: " ", "nbsp;": " ", "nbump;": "≎̸", "nbumpe;": "≏̸", "ncap;": "⩃", "ncaron;": "ň", "ncedil;": "ņ", "ncong;": "≇̸", "ncongdot;": "⩭", "ncup;": "⩂", "ncy;": "н", "ndash;": "–", "ne;": "≠", "neArr;": "⇗", "nearhk;": "⤤", "nearr;": "↗", "nearrow;": "↗", "nedot;": "≐̸", "nequiv;": "≢", "nesear;": "⤨", "nesim;": "≂̸", "nexist;": "∄", "nexists;": "∄", "nfr;": "ᵒB", "ngE;": "≧̸", "nge;": "≱", "ngeq;": "≱", "ngeqq;": "≧̸", "ngeqslant;": "⩾̸", "nges;": "⩾̸", "ngsim;": "≵", "ngt;": "≯", "ngtr;": "≯", "nhArr;": "⇎", "nharr;": "↮", "nhpar;": "⫲", "ni;": "∋", "nis;": "⋼", "nisd;": "⋺", "niv;": "∋", "njcy;": "њ", "nlArr;": "⇍", "nlE;": "≦̸", "nlarr;": "↚", "nldr;": "‥", "nle;": "≰", "nleftarrow;": "↚", "nleftrightarrow;": "↮", "nleq;": "≰", "nleqq;": "≦̸", "nleqslant;": "⩽̸", "nles;": "⩽̸", "nless;": "≮", "nlsim;": "≴", "nlt;": "≮", "nltri;": "⋪", "nltrie;": "⋬", "nmid;": "∤", "nopf;": "ᵕF", not: "¬", "not;": "¬", "notin;": "∉", "notinE;": "⋹̸", "notindot;": "⋵̸", "notinva;": "∉", "notinvb;": "⋷", "notinvc;": "⋶", "notni;": "∌", "notniva;": "∌", "notnivb;": "⋾", "notnivc;": "⋽", "npar;": "∦", "nparallel;": "∦", "nparsl;": "⫽⃥", "npart;": "∂̸", "npolint;": "⨔", "npr;": "⊀", "nprcue;": "⋠", "npre;": "⪯", "nprec;": "⊀", "npreceq;": "⪯", "nrArr;": "⇏", "nrarr;": "↛", "nrarrc;": "⤳̸", "nrarrw;": "↝̸", "nrightarrow;": "↛", "nrtri;": "⋫", "nrtrie;": "⋭", "nsc;": "⊁", "nsccue;": "⋡", "nsce;": "⪰̸", "nscr;": "ᵌ3", "nshortmid;": "∤", "nshortparallel;": "∦", "nsim;": "≁", "nsime;": "≄", "nsimeq;": "≄", "nsmid;": "∤", "nspar;": "∦", "nsqsube;": "⋢", "nsqsupe;": "⋣", "nsub;": "⊄", "nsubE;": "⫅̸", "nsube;": "⊈", "nsubset;": "⊄", "nsubseteq;": "⊈", "nsubseteqq;": "⫅̸", "nsucc;": "⊁", "nsucceq;": "⪰̸", "nsup;": "⊅", "nsupE;": "⫆", "nsupe;": "⊉", "nsupset;": "⊅", "nsupseteq;": "⊉", "nsupseteqq;": "⫆̸", "ntgl;": "≹", ntilde: "ñ", "ntilde;": "ñ", "ntlg;": "≸", "ntriangleleft;": "⋪", "ntrianglelefteq;": "⋬", "ntriangleright;": "⋫", "ntrianglerighteq;": "⋭", "nu;": "ν", "num;": "#", "numero;": "№", "numsp;": " ", "nvDash;": "⊭", "nvHarr;": "⤄", "nvap;": "≍⃒", "nvdash;": "⊬", "nvge;": "≥⃒", "nvgt;": ">⃒", "nvinfin;": "⧞", "nvlArr;": "⤂", "nvle;": "≤⃒", "nvlt;": "<⃒", "nvltrie;": "⊴⃒", "nvrArr;": "⤃", "nvrtrie;": "⊵⃒", "nvsim;": "∼⃒", "nwArr;": "⇖", "nwarhk;": "⤣", "nwarr;": "↖", "nwarrow;": "↖", "nwnear;": "⤧", "oS;": "Ⓢ", oacute: "ó", "oacute;": "ó", "oast;": "⊛", "ocir;": "⊚", ocirc: "ô", "ocirc;": "ô", "ocy;": "о", "odash;": "⊝", "odblac;": "ő", "odiv;": "⨸", "odot;": "⊙", "odsold;": "⦼", "oelig;": "œ", "ofcir;": "⦿", "ofr;": "ᵒC", "ogon;": "˛", ograve: "ò", "ograve;": "ò", "ogt;": "⧁", "ohbar;": "⦵", "ohm;": "Ω", "oint;": "∮", "olarr;": "↺", "olcir;": "⦾", "olcross;": "⦻", "oline;": "‾", "olt;": "⧀", "omacr;": "ō", "omega;": "ω", "omicron;": "ο", "omid;": "⦶", "ominus;": "⊖", "oopf;": "ᵖ0", "opar;": "⦷", "operp;": "⦹", "oplus;": "⊕", "or;": "∨", "orarr;": "↻", "ord;": "⩝", "order;": "ℴ", "orderof;": "ℴ", ordf: "ª", "ordf;": "ª", ordm: "º", "ordm;": "º", "origof;": "⊶", "oror;": "⩖", "orslope;": "⩗", "orv;": "⩛", "oscr;": "ℴ", oslash: "ø", "oslash;": "ø", "osol;": "⊘", otilde: "õ", "otilde;": "õ", "otimes;": "⊗", "otimesas;": "⨶", ouml: "ö", "ouml;": "ö", "ovbar;": "⌽", "par;": "∥", para: "¶", "para;": "¶", "parallel;": "∥", "parsim;": "⫳", "parsl;": "⫽", "part;": "∂", "pcy;": "п", "percnt;": "%", "period;": ".", "permil;": "‰", "perp;": "⊥", "pertenk;": "‱", "pfr;": "ᵒD", "phi;": "φ", "phiv;": "ϕ", "phmmat;": "ℳ", "phone;": "☎", "pi;": "π", "pitchfork;": "⋔", "piv;": "ϖ", "planck;": "ℏ", "planckh;": "ℎ", "plankv;": "ℏ", "plus;": "+", "plusacir;": "⨣", "plusb;": "⊞", "pluscir;": "⨢", "plusdo;": "∔", "plusdu;": "⨥", "pluse;": "⩲", plusmn: "±", "plusmn;": "±", "plussim;": "⨦", "plustwo;": "⨧", "pm;": "±", "pointint;": "⨕", "popf;": "ᵖ1", pound: "£", "pound;": "£", "pr;": "≺", "prE;": "⪳", "prap;": "⪷", "prcue;": "≼", "pre;": "⪯", "prec;": "≺", "precapprox;": "⪷", "preccurlyeq;": "≼", "preceq;": "⪯", "precnapprox;": "⪹", "precneqq;": "⪵", "precnsim;": "⋨", "precsim;": "≾", "prime;": "′", "primes;": "ℙ", "prnE;": "⪵", "prnap;": "⪹", "prnsim;": "⋨", "prod;": "∏", "profalar;": "⌮", "profline;": "⌒", "profsurf;": "⌓", "prop;": "∝", "propto;": "∝", "prsim;": "≾", "prurel;": "⊰", "pscr;": "ᵌ5", "psi;": "ψ", "puncsp;": " ", "qfr;": "ᵒE", "qint;": "⨌", "qopf;": "ᵖ2", "qprime;": "⁗", "qscr;": "ᵌ6", "quaternions;": "ℍ", "quatint;": "⨖", "quest;": "?", "questeq;": "≟", quot: '"', "quot;": '"', "rAarr;": "⇛", "rArr;": "⇒", "rAtail;": "⤜", "rBarr;": "⤏", "rHar;": "⥤", "race;": "∽̱", "racute;": "ŕ", "radic;": "√", "raemptyv;": "⦳", "rang;": "⟩", "rangd;": "⦒", "range;": "⦥", "rangle;": "⟩", raquo: "»", "raquo;": "»", "rarr;": "→", "rarrap;": "⥵", "rarrb;": "⇥", "rarrbfs;": "⤠", "rarrc;": "⤳", "rarrfs;": "⤞", "rarrhk;": "↪", "rarrlp;": "↬", "rarrpl;": "⥅", "rarrsim;": "⥴", "rarrtl;": "↣", "rarrw;": "↝", "ratail;": "⤚", "ratio;": "∶", "rationals;": "ℚ", "rbarr;": "⤍", "rbbrk;": "❳", "rbrace;": "}", "rbrack;": "]", "rbrke;": "⦌", "rbrksld;": "⦎", "rbrkslu;": "⦐", "rcaron;": "ř", "rcedil;": "ŗ", "rceil;": "⌉", "rcub;": "}", "rcy;": "р", "rdca;": "⤷", "rdldhar;": "⥩", "rdquo;": "”", "rdquor;": "”", "rdsh;": "↳", "real;": "ℜ", "realine;": "ℛ", "realpart;": "ℜ", "reals;": "ℝ", "rect;": "▭", reg: "®", "reg;": "®", "rfisht;": "⥽", "rfloor;": "⌋", "rfr;": "ᵒF", "rhard;": "⇁", "rharu;": "⇀", "rharul;": "⥬", "rho;": "ρ", "rhov;": "ϱ", "rightarrow;": "→", "rightarrowtail;": "↣", "rightharpoondown;": "⇁", "rightharpoonup;": "⇀", "rightleftarrows;": "⇄", "rightleftharpoons;": "⇌", "rightrightarrows;": "⇉", "rightsquigarrow;": "↝", "rightthreetimes;": "⋌", "ring;": "˚", "risingdotseq;": "≓", "rlarr;": "⇄", "rlhar;": "⇌", "rlm;": "‏", "rmoust;": "⎱", "rmoustache;": "⎱", "rnmid;": "⫮", "roang;": "⟭", "roarr;": "⇾", "robrk;": "⟧", "ropar;": "⦆", "ropf;": "ᵖ3", "roplus;": "⨮", "rotimes;": "⨵", "rpar;": ")", "rpargt;": "⦔", "rppolint;": "⨒", "rrarr;": "⇉", "rsaquo;": "›", "rscr;": "ᵌ7", "rsh;": "↱", "rsqb;": "]", "rsquo;": "’", "rsquor;": "’", "rthree;": "⋌", "rtimes;": "⋊", "rtri;": "▹", "rtrie;": "⊵", "rtrif;": "▸", "rtriltri;": "⧎", "ruluhar;": "⥨", "rx;": "℞", "sacute;": "ś", "sbquo;": "‚", "sc;": "≻", "scE;": "⪴", "scap;": "⪸", "scaron;": "š", "sccue;": "≽", "sce;": "⪰", "scedil;": "ş", "scirc;": "ŝ", "scnE;": "⪶", "scnap;": "⪺", "scnsim;": "⋩", "scpolint;": "⨓", "scsim;": "≿", "scy;": "с", "sdot;": "⋅", "sdotb;": "⊡", "sdote;": "⩦", "seArr;": "⇘", "searhk;": "⤥", "searr;": "↘", "searrow;": "↘", sect: "§", "sect;": "§", "semi;": ";", "seswar;": "⤩", "setminus;": "∖", "setmn;": "∖", "sext;": "✶", "sfr;": "ᵓ0", "sfrown;": "⌢", "sharp;": "♯", "shchcy;": "щ", "shcy;": "ш", "shortmid;": "∣", "shortparallel;": "∥", shy: "­", "shy;": "­", "sigma;": "σ", "sigmaf;": "ς", "sigmav;": "ς", "sim;": "∼", "simdot;": "⩪", "sime;": "≃", "simeq;": "≃", "simg;": "⪞", "simgE;": "⪠", "siml;": "⪝", "simlE;": "⪟", "simne;": "≆", "simplus;": "⨤", "simrarr;": "⥲", "slarr;": "←", "smallsetminus;": "∖", "smashp;": "⨳", "smeparsl;": "⧤", "smid;": "∣", "smile;": "⌣", "smt;": "⪪", "smte;": "⪬", "smtes;": "⪬︀", "softcy;": "ь", "sol;": "/", "solb;": "⧄", "solbar;": "⌿", "sopf;": "ᵖ4", "spades;": "♠", "spadesuit;": "♠", "spar;": "∥", "sqcap;": "⊓", "sqcaps;": "⊓︀", "sqcup;": "⊔", "sqcups;": "⊔︀", "sqsub;": "⊏", "sqsube;": "⊑", "sqsubset;": "⊏", "sqsubseteq;": "⊑", "sqsup;": "⊐", "sqsupe;": "⊒", "sqsupset;": "⊐", "sqsupseteq;": "⊒", "squ;": "□", "square;": "□", "squarf;": "▪", "squf;": "▪", "srarr;": "→", "sscr;": "ᵌ8", "ssetmn;": "∖", "ssmile;": "⌣", "sstarf;": "⋆", "star;": "☆", "starf;": "★", "straightepsilon;": "ϵ", "straightphi;": "ϕ", "strns;": "¯", "sub;": "⊂", "subE;": "⫅", "subdot;": "⪽", "sube;": "⊆", "subedot;": "⫃", "submult;": "⫁", "subnE;": "⫋", "subne;": "⊊", "subplus;": "⪿", "subrarr;": "⥹", "subset;": "⊂", "subseteq;": "⊆", "subseteqq;": "⫅", "subsetneq;": "⊊", "subsetneqq;": "⫋", "subsim;": "⫇", "subsub;": "⫕", "subsup;": "⫓", "succ;": "≻", "succapprox;": "⪸", "succcurlyeq;": "≽", "succeq;": "⪰", "succnapprox;": "⪺", "succneqq;": "⪶", "succnsim;": "⋩", "succsim;": "≿", "sum;": "∑", "sung;": "♪", sup1: "¹", "sup1;": "¹", sup2: "²", "sup2;": "²", sup3: "³", "sup3;": "³", "sup;": "⊃", "supE;": "⫆", "supdot;": "⪾", "supdsub;": "⫘", "supe;": "⊇", "supedot;": "⫄", "suphsol;": "⟉", "suphsub;": "⫗", "suplarr;": "⥻", "supmult;": "⫂", "supnE;": "⫌", "supne;": "⊋", "supplus;": "⫀", "supset;": "⊃", "supseteq;": "⊇", "supseteqq;": "⫆", "supsetneq;": "⊋", "supsetneqq;": "⫌", "supsim;": "⫈", "supsub;": "⫔", "supsup;": "⫖", "swArr;": "⇙", "swarhk;": "⤦", "swarr;": "↙", "swarrow;": "↙", "swnwar;": "⤪", szlig: "ß", "szlig;": "ß", "target;": "⌖", "tau;": "τ", "tbrk;": "⎴", "tcaron;": "ť", "tcedil;": "ţ", "tcy;": "т", "tdot;": "⃛", "telrec;": "⌕", "tfr;": "ᵓ1", "there4;": "∴", "therefore;": "∴", "theta;": "θ", "thetasym;": "ϑ", "thetav;": "ϑ", "thickapprox;": "≈", "thicksim;": "∼", "thinsp;": " ", "thkap;": "≈", "thksim;": "∼", thorn: "þ", "thorn;": "þ", "tilde;": "˜", times: "×", "times;": "×", "timesb;": "⊠", "timesbar;": "⨱", "timesd;": "⨰", "tint;": "∭", "toea;": "⤨", "top;": "⊤", "topbot;": "⌶", "topcir;": "⫱", "topf;": "ᵖ5", "topfork;": "⫚", "tosa;": "⤩", "tprime;": "‴", "trade;": "™", "triangle;": "▵", "triangledown;": "▿", "triangleleft;": "◃", "trianglelefteq;": "⊴", "triangleq;": "≜", "triangleright;": "▹", "trianglerighteq;": "⊵", "tridot;": "◬", "trie;": "≜", "triminus;": "⨺", "triplus;": "⨹", "trisb;": "⧍", "tritime;": "⨻", "trpezium;": "⏢", "tscr;": "ᵌ9", "tscy;": "ц", "tshcy;": "ћ", "tstrok;": "ŧ", "twixt;": "≬", "twoheadleftarrow;": "↞", "twoheadrightarrow;": "↠", "uArr;": "⇑", "uHar;": "⥣", uacute: "ú", "uacute;": "ú", "uarr;": "↑", "ubrcy;": "ў", "ubreve;": "ŭ", ucirc: "û", "ucirc;": "û", "ucy;": "у", "udarr;": "⇅", "udblac;": "ű", "udhar;": "⥮", "ufisht;": "⥾", "ufr;": "ᵓ2", ugrave: "ù", "ugrave;": "ù", "uharl;": "↿", "uharr;": "↾", "uhblk;": "▀", "ulcorn;": "⌜", "ulcorner;": "⌜", "ulcrop;": "⌏", "ultri;": "◸", "umacr;": "ū", uml: "¨", "uml;": "¨", "uogon;": "ų", "uopf;": "ᵖ6", "uparrow;": "↑", "updownarrow;": "↕", "upharpoonleft;": "↿", "upharpoonright;": "↾", "uplus;": "⊎", "upsi;": "υ", "upsih;": "ϒ", "upsilon;": "υ", "upuparrows;": "⇈", "urcorn;": "⌝", "urcorner;": "⌝", "urcrop;": "⌎", "uring;": "ů", "urtri;": "◹", "uscr;": "ᵌA", "utdot;": "⋰", "utilde;": "ũ", "utri;": "▵", "utrif;": "▴", "uuarr;": "⇈", uuml: "ü", "uuml;": "ü", "uwangle;": "⦧", "vArr;": "⇕", "vBar;": "⫨", "vBarv;": "⫩", "vDash;": "⊨", "vangrt;": "⦜", "varepsilon;": "ϵ", "varkappa;": "ϰ", "varnothing;": "∅", "varphi;": "ϕ", "varpi;": "ϖ", "varpropto;": "∝", "varr;": "↕", "varrho;": "ϱ", "varsigma;": "ς", "varsubsetneq;": "⊊︀", "varsubsetneqq;": "⫋︀", "varsupsetneq;": "⊋︀", "varsupsetneqq;": "⫌︀", "vartheta;": "ϑ", "vartriangleleft;": "⊲", "vartriangleright;": "⊳", "vcy;": "в", "vdash;": "⊢", "vee;": "∨", "veebar;": "⊻", "veeeq;": "≚", "vellip;": "⋮", "verbar;": "|", "vert;": "|", "vfr;": "ᵓ3", "vltri;": "⊲", "vnsub;": "⊂⃒", "vnsup;": "⊃⃒", "vopf;": "ᵖ7", "vprop;": "∝", "vrtri;": "⊳", "vscr;": "ᵌB", "vsubnE;": "⫋︀", "vsubne;": "⊊︀", "vsupnE;": "⫌︀", "vsupne;": "⊋︀", "vzigzag;": "⦚", "wcirc;": "ŵ", "wedbar;": "⩟", "wedge;": "∧", "wedgeq;": "≙", "weierp;": "℘", "wfr;": "ᵓ4", "wopf;": "ᵖ8", "wp;": "℘", "wr;": "≀", "wreath;": "≀", "wscr;": "ᵌC", "xcap;": "⋂", "xcirc;": "◯", "xcup;": "⋃", "xdtri;": "▽", "xfr;": "ᵓ5", "xhArr;": "⟺", "xharr;": "⟷", "xi;": "ξ", "xlArr;": "⟸", "xlarr;": "⟵", "xmap;": "⟼", "xnis;": "⋻", "xodot;": "⨀", "xopf;": "ᵖ9", "xoplus;": "⨁", "xotime;": "⨂", "xrArr;": "⟹", "xrarr;": "⟶", "xscr;": "ᵌD", "xsqcup;": "⨆", "xuplus;": "⨄", "xutri;": "△", "xvee;": "⋁", "xwedge;": "⋀", yacute: "ý", "yacute;": "ý", "yacy;": "я", "ycirc;": "ŷ", "ycy;": "ы", yen: "¥", "yen;": "¥", "yfr;": "ᵓ6", "yicy;": "ї", "yopf;": "ᵖA", "yscr;": "ᵌE", "yucy;": "ю", yuml: "ÿ", "yuml;": "ÿ", "zacute;": "ź", "zcaron;": "ž", "zcy;": "з", "zdot;": "ż", "zeetrf;": "ℨ", "zeta;": "ζ", "zfr;": "ᵓ7", "zhcy;": "ж", "zigrarr;": "⇝", "zopf;": "ᵖB", "zscr;": "ᵌF", "zwj;": "‍", "zwnj;": "‌"}
    }, {}], 13: [function (e, t, n) {
        (function () {
            function n(e) {
                if (Object.keys)return Object.keys(e);
                var t = [];
                for (var n in e)Object.prototype.hasOwnProperty.call(e, n) && t.push(n);
                return t
            }

            function r(e, t) {
                return t === undefined ? "" + t : typeof t == "number" && (isNaN(t) || !isFinite(t)) ? t.toString() : typeof t == "function" || t instanceof RegExp ? t.toString() : t
            }

            function i(e, t) {
                return typeof e == "string" ? e.length < t ? e : e.slice(0, t) : e
            }

            function s(e, t, n, r, i) {
                throw new m.AssertionError({message: n, actual: e, expected: t, operator: r, stackStartFunction: i})
            }

            function o(e, t) {
                e || s(e, !0, t, "==", m.ok)
            }

            function u(e, t) {
                if (e === t)return!0;
                if (d.isBuffer(e) && d.isBuffer(t)) {
                    if (e.length != t.length)return!1;
                    for (var n = 0; n < e.length; n++)if (e[n] !== t[n])return!1;
                    return!0
                }
                return e instanceof Date && t instanceof Date ? e.getTime() === t.getTime() : typeof e != "object" && typeof t != "object" ? e == t : l(e, t)
            }

            function a(e) {
                return e === null || e === undefined
            }

            function f(e) {
                return Object.prototype.toString.call(e) == "[object Arguments]"
            }

            function l(e, t) {
                if (a(e) || a(t))return!1;
                if (e.prototype !== t.prototype)return!1;
                if (f(e))return f(t) ? (e = v.call(e), t = v.call(t), u(e, t)) : !1;
                try {
                    var r = n(e), i = n(t), s, o
                } catch (l) {
                    return!1
                }
                if (r.length != i.length)return!1;
                r.sort(), i.sort();
                for (o = r.length - 1; o >= 0; o--)if (r[o] != i[o])return!1;
                for (o = r.length - 1; o >= 0; o--) {
                    s = r[o];
                    if (!u(e[s], t[s]))return!1
                }
                return!0
            }

            function c(e, t) {
                return!e || !t ? !1 : t instanceof RegExp ? t.test(e) : e instanceof t ? !0 : t.call({}, e) === !0 ? !0 : !1
            }

            function h(e, t, n, r) {
                var i;
                typeof n == "string" && (r = n, n = null);
                try {
                    t()
                } catch (o) {
                    i = o
                }
                r = (n && n.name ? " (" + n.name + ")." : ".") + (r ? " " + r : "."), e && !i && s("Missing expected exception" + r), !e && c(i, n) && s("Got unwanted exception" + r);
                if (e && i && n && !c(i, n) || !e && i)throw i
            }

            var p = e("util"), d = e("buffer").Buffer, v = Array.prototype.slice, m = t.exports = o;
            m.AssertionError = function (e) {
                this.name = "AssertionError", this.message = e.message, this.actual = e.actual, this.expected = e.expected, this.operator = e.operator;
                var t = e.stackStartFunction || s;
                Error.captureStackTrace && Error.captureStackTrace(this, t)
            }, p.inherits(m.AssertionError, Error), m.AssertionError.prototype.toString = function () {
                return this.message ? [this.name + ":", this.message].join(" ") : [this.name + ":", i(JSON.stringify(this.actual, r), 128), this.operator, i(JSON.stringify(this.expected, r), 128)].join(" ")
            }, m.fail = s, m.ok = o, m.equal = function (e, t, n) {
                e != t && s(e, t, n, "==", m.equal)
            }, m.notEqual = function (e, t, n) {
                e == t && s(e, t, n, "!=", m.notEqual)
            }, m.deepEqual = function (e, t, n) {
                u(e, t) || s(e, t, n, "deepEqual", m.deepEqual)
            }, m.notDeepEqual = function (e, t, n) {
                u(e, t) && s(e, t, n, "notDeepEqual", m.notDeepEqual)
            }, m.strictEqual = function (e, t, n) {
                e !== t && s(e, t, n, "===", m.strictEqual)
            }, m.notStrictEqual = function (e, t, n) {
                e === t && s(e, t, n, "!==", m.notStrictEqual)
            }, m.throws = function (e, t, n) {
                h.apply(this, [!0].concat(v.call(arguments)))
            }, m.doesNotThrow = function (e, t, n) {
                h.apply(this, [!1].concat(v.call(arguments)))
            }, m.ifError = function (e) {
                if (e)throw e
            }
        })()
    }, {buffer: 17, util: 15}], 14: [function (e, t, n) {
        (function (e) {
            function t(e, t) {
                if (e.indexOf)return e.indexOf(t);
                for (var n = 0; n < e.length; n++)if (t === e[n])return n;
                return-1
            }

            e.EventEmitter || (e.EventEmitter = function () {
            });
            var r = n.EventEmitter = e.EventEmitter, i = typeof Array.isArray == "function" ? Array.isArray : function (e) {
                return Object.prototype.toString.call(e) === "[object Array]"
            }, s = 10;
            r.prototype.setMaxListeners = function (e) {
                this._events || (this._events = {}), this._events.maxListeners = e
            }, r.prototype.emit = function (e) {
                if (e === "error")if (!this._events || !this._events.error || i(this._events.error) && !this._events.error.length)throw arguments[1]instanceof Error ? arguments[1] : new Error("Uncaught, unspecified 'error' event.");
                if (!this._events)return!1;
                var t = this._events[e];
                if (!t)return!1;
                if (typeof t == "function") {
                    switch (arguments.length) {
                        case 1:
                            t.call(this);
                            break;
                        case 2:
                            t.call(this, arguments[1]);
                            break;
                        case 3:
                            t.call(this, arguments[1], arguments[2]);
                            break;
                        default:
                            var n = Array.prototype.slice.call(arguments, 1);
                            t.apply(this, n)
                    }
                    return!0
                }
                if (i(t)) {
                    var n = Array.prototype.slice.call(arguments, 1), r = t.slice();
                    for (var s = 0, o = r.length; s < o; s++)r[s].apply(this, n);
                    return!0
                }
                return!1
            }, r.prototype.addListener = function (e, t) {
                if ("function" != typeof t)throw new Error("addListener only takes instances of Function");
                this._events || (this._events = {}), this.emit("newListener", e, t);
                if (!this._events[e])this._events[e] = t; else if (i(this._events[e])) {
                    if (!this._events[e].warned) {
                        var n;
                        this._events.maxListeners !== undefined ? n = this._events.maxListeners : n = s, n && n > 0 && this._events[e].length > n && (this._events[e].warned = !0, console.error("(node) warning: possible EventEmitter memory leak detected. %d listeners added. Use emitter.setMaxListeners() to increase limit.", this._events[e].length), console.trace())
                    }
                    this._events[e].push(t)
                } else this._events[e] = [this._events[e], t];
                return this
            }, r.prototype.on = r.prototype.addListener, r.prototype.once = function (e, t) {
                var n = this;
                return n.on(e, function r() {
                    n.removeListener(e, r), t.apply(this, arguments)
                }), this
            }, r.prototype.removeListener = function (e, n) {
                if ("function" != typeof n)throw new Error("removeListener only takes instances of Function");
                if (!this._events || !this._events[e])return this;
                var r = this._events[e];
                if (i(r)) {
                    var s = t(r, n);
                    if (s < 0)return this;
                    r.splice(s, 1), r.length == 0 && delete this._events[e]
                } else this._events[e] === n && delete this._events[e];
                return this
            }, r.prototype.removeAllListeners = function (e) {
                return arguments.length === 0 ? (this._events = {}, this) : (e && this._events && this._events[e] && (this._events[e] = null), this)
            }, r.prototype.listeners = function (e) {
                return this._events || (this._events = {}), this._events[e] || (this._events[e] = []), i(this._events[e]) || (this._events[e] = [this._events[e]]), this._events[e]
            }
        })(e("__browserify_process"))
    }, {__browserify_process: 20}], 15: [function (e, t, n) {
        function r(e) {
            return Array.isArray(e) || typeof e == "object" && Object.prototype.toString.call(e) === "[object Array]"
        }

        function i(e) {
            typeof e == "object" && Object.prototype.toString.call(e) === "[object RegExp]"
        }

        function s(e) {
            return typeof e == "object" && Object.prototype.toString.call(e) === "[object Date]"
        }

        function o(e) {
            return e < 10 ? "0" + e.toString(10) : e.toString(10)
        }

        function u() {
            var e = new Date, t = [o(e.getHours()), o(e.getMinutes()), o(e.getSeconds())].join(":");
            return[e.getDate(), f[e.getMonth()], t].join(" ")
        }

        var a = e("events");
        n.isArray = r, n.isDate = function (e) {
            return Object.prototype.toString.call(e) === "[object Date]"
        }, n.isRegExp = function (e) {
            return Object.prototype.toString.call(e) === "[object RegExp]"
        }, n.print = function () {
        }, n.puts = function () {
        }, n.debug = function () {
        }, n.inspect = function (e, t, o, u) {
            function a(e, o) {
                if (e && typeof e.inspect == "function" && e !== n && (!e.constructor || e.constructor.prototype !== e))return e.inspect(o);
                switch (typeof e) {
                    case"undefined":
                        return h("undefined", "undefined");
                    case"string":
                        var u = "'" + JSON.stringify(e).replace(/^"|"$/g, "").replace(/'/g, "\\'").replace(/\\"/g, '"') + "'";
                        return h(u, "string");
                    case"number":
                        return h("" + e, "number");
                    case"boolean":
                        return h("" + e, "boolean")
                }
                if (e === null)return h("null", "null");
                var p = l(e), d = t ? c(e) : p;
                if (typeof e == "function" && d.length === 0) {
                    if (i(e))return h("" + e, "regexp");
                    var v = e.name ? ": " + e.name : "";
                    return h("[Function" + v + "]", "special")
                }
                if (s(e) && d.length === 0)return h(e.toUTCString(), "date");
                var m, g, y;
                r(e) ? (g = "Array", y = ["[", "]"]) : (g = "Object", y = ["{", "}"]);
                if (typeof e == "function") {
                    var b = e.name ? ": " + e.name : "";
                    m = i(e) ? " " + e : " [Function" + b + "]"
                } else m = "";
                s(e) && (m = " " + e.toUTCString());
                if (d.length === 0)return y[0] + m + y[1];
                if (o < 0)return i(e) ? h("" + e, "regexp") : h("[Object]", "special");
                f.push(e);
                var w = d.map(function (t) {
                    var n, i;
                    e.__lookupGetter__ && (e.__lookupGetter__(t) ? e.__lookupSetter__(t) ? i = h("[Getter/Setter]", "special") : i = h("[Getter]", "special") : e.__lookupSetter__(t) && (i = h("[Setter]", "special"))), p.indexOf(t) < 0 && (n = "[" + t + "]"), i || (f.indexOf(e[t]) < 0 ? (o === null ? i = a(e[t]) : i = a(e[t], o - 1), i.indexOf("\n") > -1 && (r(e) ? i = i.split("\n").map(function (e) {
                        return"  " + e
                    }).join("\n").substr(2) : i = "\n" + i.split("\n").map(function (e) {
                        return"   " + e
                    }).join("\n"))) : i = h("[Circular]", "special"));
                    if (typeof n == "undefined") {
                        if (g === "Array" && t.match(/^\d+$/))return i;
                        n = JSON.stringify("" + t), n.match(/^"([a-zA-Z_][a-zA-Z_0-9]*)"$/) ? (n = n.substr(1, n.length - 2), n = h(n, "name")) : (n = n.replace(/'/g, "\\'").replace(/\\"/g, '"').replace(/(^"|"$)/g, "'"), n = h(n, "string"))
                    }
                    return n + ": " + i
                });
                f.pop();
                var E = 0, S = w.reduce(function (e, t) {
                    return E++, t.indexOf("\n") >= 0 && E++, e + t.length + 1
                }, 0);
                return S > 50 ? w = y[0] + (m === "" ? "" : m + "\n ") + " " + w.join(",\n  ") + " " + y[1] : w = y[0] + m + " " + w.join(", ") + " " + y[1], w
            }

            var f = [], h = function (e, t) {
                var n = {bold: [1, 22], italic: [3, 23], underline: [4, 24], inverse: [7, 27], white: [37, 39], grey: [90, 39], black: [30, 39], blue: [34, 39], cyan: [36, 39], green: [32, 39], magenta: [35, 39], red: [31, 39], yellow: [33, 39]}, r = {special: "cyan", number: "blue", "boolean": "yellow", "undefined": "grey", "null": "bold", string: "green", date: "magenta", regexp: "red"}[t];
                return r ? "[" + n[r][0] + "m" + e + "[" + n[r][1] + "m" : e
            };
            return u || (h = function (e, t) {
                return e
            }), a(e, typeof o == "undefined" ? 2 : o)
        };
        var f = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        n.log = function (e) {
        }, n.pump = null;
        var l = Object.keys || function (e) {
            var t = [];
            for (var n in e)t.push(n);
            return t
        }, c = Object.getOwnPropertyNames || function (e) {
            var t = [];
            for (var n in e)Object.hasOwnProperty.call(e, n) && t.push(n);
            return t
        }, h = Object.create || function (e, t) {
            var n;
            if (e === null)n = {__proto__: null}; else {
                if (typeof e != "object")throw new TypeError("typeof prototype[" + typeof e + "] != 'object'");
                var r = function () {
                };
                r.prototype = e, n = new r, n.__proto__ = e
            }
            return typeof t != "undefined" && Object.defineProperties && Object.defineProperties(n, t), n
        };
        n.inherits = function (e, t) {
            e.super_ = t, e.prototype = h(t.prototype, {constructor: {value: e, enumerable: !1, writable: !0, configurable: !0}})
        };
        var p = /%[sdj%]/g;
        n.format = function (e) {
            if (typeof e != "string") {
                var t = [];
                for (var r = 0; r < arguments.length; r++)t.push(n.inspect(arguments[r]));
                return t.join(" ")
            }
            var r = 1, i = arguments, s = i.length, o = String(e).replace(p, function (e) {
                if (e === "%%")return"%";
                if (r >= s)return e;
                switch (e) {
                    case"%s":
                        return String(i[r++]);
                    case"%d":
                        return Number(i[r++]);
                    case"%j":
                        return JSON.stringify(i[r++]);
                    default:
                        return e
                }
            });
            for (var u = i[r]; r < s; u = i[++r])u === null || typeof u != "object" ? o += " " + u : o += " " + n.inspect(u);
            return o
        }
    }, {events: 14}], 16: [function (e, t, n) {
        n.readIEEE754 = function (e, t, n, r, i) {
            var s, o, u = i * 8 - r - 1, a = (1 << u) - 1, f = a >> 1, l = -7, c = n ? 0 : i - 1, h = n ? 1 : -1, p = e[t + c];
            c += h, s = p & (1 << -l) - 1, p >>= -l, l += u;
            for (; l > 0; s = s * 256 + e[t + c], c += h, l -= 8);
            o = s & (1 << -l) - 1, s >>= -l, l += r;
            for (; l > 0; o = o * 256 + e[t + c], c += h, l -= 8);
            if (s === 0)s = 1 - f; else {
                if (s === a)return o ? NaN : (p ? -1 : 1) * Infinity;
                o += Math.pow(2, r), s -= f
            }
            return(p ? -1 : 1) * o * Math.pow(2, s - r)
        }, n.writeIEEE754 = function (e, t, n, r, i, s) {
            var o, u, a, f = s * 8 - i - 1, l = (1 << f) - 1, c = l >> 1, h = i === 23 ? Math.pow(2, -24) - Math.pow(2, -77) : 0, p = r ? s - 1 : 0, d = r ? -1 : 1, v = t < 0 || t === 0 && 1 / t < 0 ? 1 : 0;
            t = Math.abs(t), isNaN(t) || t === Infinity ? (u = isNaN(t) ? 1 : 0, o = l) : (o = Math.floor(Math.log(t) / Math.LN2), t * (a = Math.pow(2, -o)) < 1 && (o--, a *= 2), o + c >= 1 ? t += h / a : t += h * Math.pow(2, 1 - c), t * a >= 2 && (o++, a /= 2), o + c >= l ? (u = 0, o = l) : o + c >= 1 ? (u = (t * a - 1) * Math.pow(2, i), o += c) : (u = t * Math.pow(2, c - 1) * Math.pow(2, i), o = 0));
            for (; i >= 8; e[n + p] = u & 255, p += d, u /= 256, i -= 8);
            o = o << i | u, f += i;
            for (; f > 0; e[n + p] = o & 255, p += d, o /= 256, f -= 8);
            e[n + p - d] |= v * 128
        }
    }, {}], 17: [function (e, t, n) {
        (function () {
            function t(e, n, i) {
                if (!(this instanceof t))return new t(e, n, i);
                this.parent = this, this.offset = 0;
                var o;
                if (typeof i == "number")this.length = r(n), this.offset = i; else {
                    switch (o = typeof e) {
                        case"number":
                            this.length = r(e);
                            break;
                        case"string":
                            this.length = t.byteLength(e, n);
                            break;
                        case"object":
                            this.length = r(e.length);
                            break;
                        default:
                            throw new Error("First argument needs to be a number, array or string.")
                    }
                    if (s(e))for (var u = 0; u < this.length; u++)e instanceof t ? this[u] = e.readUInt8(u) : this[u] = e[u]; else if (o == "string")this.length = this.write(e, 0, n); else if (o === "number")for (var u = 0; u < this.length; u++)this[u] = 0
                }
            }

            function r(e) {
                return e = ~~Math.ceil(+e), e < 0 ? 0 : e
            }

            function i(e) {
                return(Array.isArray || function (e) {
                    return{}.toString.apply(e) == "[object Array]"
                })(e)
            }

            function s(e) {
                return i(e) || t.isBuffer(e) || e && typeof e == "object" && typeof e.length == "number"
            }

            function o(e) {
                return e < 16 ? "0" + e.toString(16) : e.toString(16)
            }

            function u(e) {
                var t = [];
                for (var n = 0; n < e.length; n++)if (e.charCodeAt(n) <= 127)t.push(e.charCodeAt(n)); else {
                    var r = encodeURIComponent(e.charAt(n)).substr(1).split("%");
                    for (var i = 0; i < r.length; i++)t.push(parseInt(r[i], 16))
                }
                return t
            }

            function a(e) {
                var t = [];
                for (var n = 0; n < e.length; n++)t.push(e.charCodeAt(n) & 255);
                return t
            }

            function f(t) {
                return e("base64-js").toByteArray(t)
            }

            function l(e, t, n, r) {
                var i, s = 0;
                while (s < r) {
                    if (s + n >= t.length || s >= e.length)break;
                    t[s + n] = e[s], s++
                }
                return s
            }

            function c(e) {
                try {
                    return decodeURIComponent(e)
                } catch (t) {
                    return String.fromCharCode(65533)
                }
            }

            function h(e, t, n, r) {
                var i = 0;
                return r || (k.ok(typeof n == "boolean", "missing or invalid endian"), k.ok(t !== undefined && t !== null, "missing offset"), k.ok(t + 1 < e.length, "Trying to read beyond buffer length")), t >= e.length ? 0 : (n ? (i = e[t] << 8, t + 1 < e.length && (i |= e[t + 1])) : (i = e[t], t + 1 < e.length && (i |= e[t + 1] << 8)), i)
            }

            function p(e, t, n, r) {
                var i = 0;
                return r || (k.ok(typeof n == "boolean", "missing or invalid endian"), k.ok(t !== undefined && t !== null, "missing offset"), k.ok(t + 3 < e.length, "Trying to read beyond buffer length")), t >= e.length ? 0 : (n ? (t + 1 < e.length && (i = e[t + 1] << 16), t + 2 < e.length && (i |= e[t + 2] << 8), t + 3 < e.length && (i |= e[t + 3]), i += e[t] << 24 >>> 0) : (t + 2 < e.length && (i = e[t + 2] << 16), t + 1 < e.length && (i |= e[t + 1] << 8), i |= e[t], t + 3 < e.length && (i += e[t + 3] << 24 >>> 0)), i)
            }

            function d(e, t, n, r) {
                var i, s;
                return r || (k.ok(typeof n == "boolean", "missing or invalid endian"), k.ok(t !== undefined && t !== null, "missing offset"), k.ok(t + 1 < e.length, "Trying to read beyond buffer length")), s = h(e, t, n, r), i = s & 32768, i ? (65535 - s + 1) * -1 : s
            }

            function v(e, t, n, r) {
                var i, s;
                return r || (k.ok(typeof n == "boolean", "missing or invalid endian"), k.ok(t !== undefined && t !== null, "missing offset"), k.ok(t + 3 < e.length, "Trying to read beyond buffer length")), s = p(e, t, n, r), i = s & 2147483648, i ? (4294967295 - s + 1) * -1 : s
            }

            function m(t, n, r, i) {
                return i || (k.ok(typeof r == "boolean", "missing or invalid endian"), k.ok(n + 3 < t.length, "Trying to read beyond buffer length")), e("./buffer_ieee754").readIEEE754(t, n, r, 23, 4)
            }

            function g(t, n, r, i) {
                return i || (k.ok(typeof r == "boolean", "missing or invalid endian"), k.ok(n + 7 < t.length, "Trying to read beyond buffer length")), e("./buffer_ieee754").readIEEE754(t, n, r, 52, 8)
            }

            function y(e, t) {
                k.ok(typeof e == "number", "cannot write a non-number as a number"), k.ok(e >= 0, "specified a negative value for writing an unsigned value"), k.ok(e <= t, "value is larger than maximum value for type"), k.ok(Math.floor(e) === e, "value has a fractional component")
            }

            function b(e, t, n, r, i) {
                i || (k.ok(t !== undefined && t !== null, "missing value"), k.ok(typeof r == "boolean", "missing or invalid endian"), k.ok(n !== undefined && n !== null, "missing offset"), k.ok(n + 1 < e.length, "trying to write beyond buffer length"), y(t, 65535));
                for (var s = 0; s < Math.min(e.length - n, 2); s++)e[n + s] = (t & 255 << 8 * (r ? 1 - s : s)) >>> (r ? 1 - s : s) * 8
            }

            function w(e, t, n, r, i) {
                i || (k.ok(t !== undefined && t !== null, "missing value"), k.ok(typeof r == "boolean", "missing or invalid endian"), k.ok(n !== undefined && n !== null, "missing offset"), k.ok(n + 3 < e.length, "trying to write beyond buffer length"), y(t, 4294967295));
                for (var s = 0; s < Math.min(e.length - n, 4); s++)e[n + s] = t >>> (r ? 3 - s : s) * 8 & 255
            }

            function E(e, t, n) {
                k.ok(typeof e == "number", "cannot write a non-number as a number"), k.ok(e <= t, "value larger than maximum allowed value"), k.ok(e >= n, "value smaller than minimum allowed value"), k.ok(Math.floor(e) === e, "value has a fractional component")
            }

            function S(e, t, n) {
                k.ok(typeof e == "number", "cannot write a non-number as a number"), k.ok(e <= t, "value larger than maximum allowed value"), k.ok(e >= n, "value smaller than minimum allowed value")
            }

            function x(e, t, n, r, i) {
                i || (k.ok(t !== undefined && t !== null, "missing value"), k.ok(typeof r == "boolean", "missing or invalid endian"), k.ok(n !== undefined && n !== null, "missing offset"), k.ok(n + 1 < e.length, "Trying to write beyond buffer length"), E(t, 32767, -32768)), t >= 0 ? b(e, t, n, r, i) : b(e, 65535 + t + 1, n, r, i)
            }

            function T(e, t, n, r, i) {
                i || (k.ok(t !== undefined && t !== null, "missing value"), k.ok(typeof r == "boolean", "missing or invalid endian"), k.ok(n !== undefined && n !== null, "missing offset"), k.ok(n + 3 < e.length, "Trying to write beyond buffer length"), E(t, 2147483647, -2147483648)), t >= 0 ? w(e, t, n, r, i) : w(e, 4294967295 + t + 1, n, r, i)
            }

            function N(t, n, r, i, s) {
                s || (k.ok(n !== undefined && n !== null, "missing value"), k.ok(typeof i == "boolean", "missing or invalid endian"), k.ok(r !== undefined && r !== null, "missing offset"), k.ok(r + 3 < t.length, "Trying to write beyond buffer length"), S(n, 3.4028234663852886e38, -3.4028234663852886e38)), e("./buffer_ieee754").writeIEEE754(t, n, r, i, 23, 4)
            }

            function C(t, n, r, i, s) {
                s || (k.ok(n !== undefined && n !== null, "missing value"), k.ok(typeof i == "boolean", "missing or invalid endian"), k.ok(r !== undefined && r !== null, "missing offset"), k.ok(r + 7 < t.length, "Trying to write beyond buffer length"), S(n, 1.7976931348623157e308, -1.7976931348623157e308)), e("./buffer_ieee754").writeIEEE754(t, n, r, i, 52, 8)
            }

            var k = e("assert");
            n.Buffer = t, n.SlowBuffer = t, t.poolSize = 8192, n.INSPECT_MAX_BYTES = 50, t.prototype.get = function (e) {
                if (e < 0 || e >= this.length)throw new Error("oob");
                return this[e]
            }, t.prototype.set = function (e, t) {
                if (e < 0 || e >= this.length)throw new Error("oob");
                return this[e] = t
            }, t.byteLength = function (e, t) {
                switch (t || "utf8") {
                    case"hex":
                        return e.length / 2;
                    case"utf8":
                    case"utf-8":
                        return u(e).length;
                    case"ascii":
                    case"binary":
                        return e.length;
                    case"base64":
                        return f(e).length;
                    default:
                        throw new Error("Unknown encoding")
                }
            }, t.prototype.utf8Write = function (e, n, r) {
                var i, s;
                return t._charsWritten = l(u(e), this, n, r)
            }, t.prototype.asciiWrite = function (e, n, r) {
                var i, s;
                return t._charsWritten = l(a(e), this, n, r)
            }, t.prototype.binaryWrite = t.prototype.asciiWrite, t.prototype.base64Write = function (e, n, r) {
                var i, s;
                return t._charsWritten = l(f(e), this, n, r)
            }, t.prototype.base64Slice = function (t, n) {
                var r = Array.prototype.slice.apply(this, arguments);
                return e("base64-js").fromByteArray(r)
            }, t.prototype.utf8Slice = function () {
                var e = Array.prototype.slice.apply(this, arguments), t = "", n = "", r = 0;
                while (r < e.length)e[r] <= 127 ? (t += c(n) + String.fromCharCode(e[r]), n = "") : n += "%" + e[r].toString(16), r++;
                return t + c(n)
            }, t.prototype.asciiSlice = function () {
                var e = Array.prototype.slice.apply(this, arguments), t = "";
                for (var n = 0; n < e.length; n++)t += String.fromCharCode(e[n]);
                return t
            }, t.prototype.binarySlice = t.prototype.asciiSlice, t.prototype.inspect = function () {
                var e = [], t = this.length;
                for (var r = 0; r < t; r++) {
                    e[r] = o(this[r]);
                    if (r == n.INSPECT_MAX_BYTES) {
                        e[r + 1] = "...";
                        break
                    }
                }
                return"<Buffer " + e.join(" ") + ">"
            }, t.prototype.hexSlice = function (e, t) {
                var n = this.length;
                if (!e || e < 0)e = 0;
                if (!t || t < 0 || t > n)t = n;
                var r = "";
                for (var i = e; i < t; i++)r += o(this[i]);
                return r
            }, t.prototype.toString = function (e, t, n) {
                e = String(e || "utf8").toLowerCase(), t = +t || 0, typeof n == "undefined" && (n = this.length);
                if (+n == t)return"";
                switch (e) {
                    case"hex":
                        return this.hexSlice(t, n);
                    case"utf8":
                    case"utf-8":
                        return this.utf8Slice(t, n);
                    case"ascii":
                        return this.asciiSlice(t, n);
                    case"binary":
                        return this.binarySlice(t, n);
                    case"base64":
                        return this.base64Slice(t, n);
                    case"ucs2":
                    case"ucs-2":
                        return this.ucs2Slice(t, n);
                    default:
                        throw new Error("Unknown encoding")
                }
            }, t.prototype.hexWrite = function (e, n, r) {
                n = +n || 0;
                var i = this.length - n;
                r ? (r = +r, r > i && (r = i)) : r = i;
                var s = e.length;
                if (s % 2)throw new Error("Invalid hex string");
                r > s / 2 && (r = s / 2);
                for (var o = 0; o < r; o++) {
                    var u = parseInt(e.substr(o * 2, 2), 16);
                    if (isNaN(u))throw new Error("Invalid hex string");
                    this[n + o] = u
                }
                return t._charsWritten = o * 2, o
            }, t.prototype.write = function (e, t, n, r) {
                if (isFinite(t))isFinite(n) || (r = n, n = undefined); else {
                    var i = r;
                    r = t, t = n, n = i
                }
                t = +t || 0;
                var s = this.length - t;
                n ? (n = +n, n > s && (n = s)) : n = s, r = String(r || "utf8").toLowerCase();
                switch (r) {
                    case"hex":
                        return this.hexWrite(e, t, n);
                    case"utf8":
                    case"utf-8":
                        return this.utf8Write(e, t, n);
                    case"ascii":
                        return this.asciiWrite(e, t, n);
                    case"binary":
                        return this.binaryWrite(e, t, n);
                    case"base64":
                        return this.base64Write(e, t, n);
                    case"ucs2":
                    case"ucs-2":
                        return this.ucs2Write(e, t, n);
                    default:
                        throw new Error("Unknown encoding")
                }
            }, t.prototype.slice = function (e, n) {
                n === undefined && (n = this.length);
                if (n > this.length)throw new Error("oob");
                if (e > n)throw new Error("oob");
                return new t(this, n - e, +e)
            }, t.prototype.copy = function (e, t, n, r) {
                var i = this;
                n || (n = 0);
                if (r === undefined || isNaN(r))r = this.length;
                t || (t = 0);
                if (r < n)throw new Error("sourceEnd < sourceStart");
                if (r === n)return 0;
                if (e.length == 0 || i.length == 0)return 0;
                if (t < 0 || t >= e.length)throw new Error("targetStart out of bounds");
                if (n < 0 || n >= i.length)throw new Error("sourceStart out of bounds");
                if (r < 0 || r > i.length)throw new Error("sourceEnd out of bounds");
                r > this.length && (r = this.length), e.length - t < r - n && (r = e.length - t + n);
                var s = [];
                for (var o = n; o < r; o++)k.ok(typeof this[o] != "undefined", "copying undefined buffer bytes!"), s.push(this[o]);
                for (var o = t; o < t + s.length; o++)e[o] = s[o - t]
            }, t.prototype.fill = function (e, t, n) {
                e || (e = 0), t || (t = 0), n || (n = this.length), typeof e == "string" && (e = e.charCodeAt(0));
                if (typeof e != "number" || isNaN(e))throw new Error("value is not a number");
                if (n < t)throw new Error("end < start");
                if (n === t)return 0;
                if (this.length == 0)return 0;
                if (t < 0 || t >= this.length)throw new Error("start out of bounds");
                if (n < 0 || n > this.length)throw new Error("end out of bounds");
                for (var r = t; r < n; r++)this[r] = e
            }, t.isBuffer = function (e) {
                return e instanceof t || e instanceof t
            }, t.concat = function (e, n) {
                if (!i(e))throw new Error("Usage: Buffer.concat(list, [totalLength])\n       list should be an Array.");
                if (e.length === 0)return new t(0);
                if (e.length === 1)return e[0];
                if (typeof n != "number") {
                    n = 0;
                    for (var r = 0; r < e.length; r++) {
                        var s = e[r];
                        n += s.length
                    }
                }
                var o = new t(n), u = 0;
                for (var r = 0; r < e.length; r++) {
                    var s = e[r];
                    s.copy(o, u), u += s.length
                }
                return o
            }, t.prototype.readUInt8 = function (e, t) {
                var n = this;
                t || (k.ok(e !== undefined && e !== null, "missing offset"), k.ok(e < n.length, "Trying to read beyond buffer length"));
                if (e >= n.length)return;
                return n[e]
            }, t.prototype.readUInt16LE = function (e, t) {
                return h(this, e, !1, t)
            }, t.prototype.readUInt16BE = function (e, t) {
                return h(this, e, !0, t)
            }, t.prototype.readUInt32LE = function (e, t) {
                return p(this, e, !1, t)
            }, t.prototype.readUInt32BE = function (e, t) {
                return p(this, e, !0, t)
            }, t.prototype.readInt8 = function (e, t) {
                var n = this, r;
                t || (k.ok(e !== undefined && e !== null, "missing offset"), k.ok(e < n.length, "Trying to read beyond buffer length"));
                if (e >= n.length)return;
                return r = n[e] & 128, r ? (255 - n[e] + 1) * -1 : n[e]
            }, t.prototype.readInt16LE = function (e, t) {
                return d(this, e, !1, t)
            }, t.prototype.readInt16BE = function (e, t) {
                return d(this, e, !0, t)
            }, t.prototype.readInt32LE = function (e, t) {
                return v(this, e, !1, t)
            }, t.prototype.readInt32BE = function (e, t) {
                return v(this, e, !0, t)
            }, t.prototype.readFloatLE = function (e, t) {
                return m(this, e, !1, t)
            }, t.prototype.readFloatBE = function (e, t) {
                return m(this, e, !0, t)
            }, t.prototype.readDoubleLE = function (e, t) {
                return g(this, e, !1, t)
            }, t.prototype.readDoubleBE = function (e, t) {
                return g(this, e, !0, t)
            }, t.prototype.writeUInt8 = function (e, t, n) {
                var r = this;
                n || (k.ok(e !== undefined && e !== null, "missing value"), k.ok(t !== undefined && t !== null, "missing offset"), k.ok(t < r.length, "trying to write beyond buffer length"), y(e, 255)), t < r.length && (r[t] = e)
            }, t.prototype.writeUInt16LE = function (e, t, n) {
                b(this, e, t, !1, n)
            }, t.prototype.writeUInt16BE = function (e, t, n) {
                b(this, e, t, !0, n)
            }, t.prototype.writeUInt32LE = function (e, t, n) {
                w(this, e, t, !1, n)
            }, t.prototype.writeUInt32BE = function (e, t, n) {
                w(this, e, t, !0, n)
            }, t.prototype.writeInt8 = function (e, t, n) {
                var r = this;
                n || (k.ok(e !== undefined && e !== null, "missing value"), k.ok(t !== undefined && t !== null, "missing offset"), k.ok(t < r.length, "Trying to write beyond buffer length"), E(e, 127, -128)), e >= 0 ? r.writeUInt8(e, t, n) : r.writeUInt8(255 + e + 1, t, n)
            }, t.prototype.writeInt16LE = function (e, t, n) {
                x(this, e, t, !1, n)
            }, t.prototype.writeInt16BE = function (e, t, n) {
                x(this, e, t, !0, n)
            }, t.prototype.writeInt32LE = function (e, t, n) {
                T(this, e, t, !1, n)
            }, t.prototype.writeInt32BE = function (e, t, n) {
                T(this, e, t, !0, n)
            }, t.prototype.writeFloatLE = function (e, t, n) {
                N(this, e, t, !1, n)
            }, t.prototype.writeFloatBE = function (e, t, n) {
                N(this, e, t, !0, n)
            }, t.prototype.writeDoubleLE = function (e, t, n) {
                C(this, e, t, !1, n)
            }, t.prototype.writeDoubleBE = function (e, t, n) {
                C(this, e, t, !0, n)
            }
        })()
    }, {"./buffer_ieee754": 16, assert: 13, "base64-js": 18}], 18: [function (e, t, n) {
        (function (e) {
            function n(e) {
                var t, n, r, s, o, u;
                if (e.length % 4 > 0)throw"Invalid string. Length must be a multiple of 4";
                o = e.indexOf("="), o = o > 0 ? e.length - o : 0, u = [], r = o > 0 ? e.length - 4 : e.length;
                for (t = 0, n = 0; t < r; t += 4, n += 3)s = i.indexOf(e[t]) << 18 | i.indexOf(e[t + 1]) << 12 | i.indexOf(e[t + 2]) << 6 | i.indexOf(e[t + 3]), u.push((s & 16711680) >> 16), u.push((s & 65280) >> 8), u.push(s & 255);
                return o === 2 ? (s = i.indexOf(e[t]) << 2 | i.indexOf(e[t + 1]) >> 4, u.push(s & 255)) : o === 1 && (s = i.indexOf(e[t]) << 10 | i.indexOf(e[t + 1]) << 4 | i.indexOf(e[t + 2]) >> 2, u.push(s >> 8 & 255), u.push(s & 255)), u
            }

            function r(e) {
                function t(e) {
                    return i[e >> 18 & 63] + i[e >> 12 & 63] + i[e >> 6 & 63] + i[e & 63]
                }

                var n, r = e.length % 3, s = "", o, u;
                for (n = 0, u = e.length - r; n < u; n += 3)o = (e[n] << 16) + (e[n + 1] << 8) + e[n + 2], s += t(o);
                switch (r) {
                    case 1:
                        o = e[e.length - 1], s += i[o >> 2], s += i[o << 4 & 63], s += "==";
                        break;
                    case 2:
                        o = (e[e.length - 2] << 8) + e[e.length - 1], s += i[o >> 10], s += i[o >> 4 & 63], s += i[o << 2 & 63], s += "="
                }
                return s
            }

            var i = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
            t.exports.toByteArray = n, t.exports.fromByteArray = r
        })()
    }, {}], "./lib/sax/SAXParser.js": [function (e, t, n) {
        t.exports = e("DaboPu")
    }, {}], 20: [function (e, t, n) {
        var r = t.exports = {};
        r.nextTick = function () {
            var e = typeof window != "undefined" && window.setImmediate, t = typeof window != "undefined" && window.postMessage && window.addEventListener;
            if (e)return function (e) {
                return window.setImmediate(e)
            };
            if (t) {
                var n = [];
                return window.addEventListener("message", function (e) {
                    if (e.source === window && e.data === "getEditPanel-tick") {
                        e.stopPropagation();
                        if (n.length > 0) {
                            var t = n.shift();
                            t()
                        }
                    }
                }, !0), function (e) {
                    n.push(e), window.postMessage("getEditPanel-tick", "*")
                }
            }
            return function (e) {
                setTimeout(e, 0)
            }
        }(), r.title = "browser", r.browser = !0, r.env = {}, r.argv = [], r.binding = function (e) {
            throw new Error("process.binding is not supported")
        }, r.cwd = function () {
            return"/"
        }, r.chdir = function (e) {
            throw new Error("process.chdir is not supported")
        }
    }, {}]}, {}, ["DaboPu"]);
    t.SAXParser = r("./lib/sax/SAXParser.js").SAXParser
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
})