ace.define("ace/mode/rhtml", ["require", "exports", "module", "ace/lib/oop", "ace/mode/html", "ace/tokenizer", "ace/mode/rhtml_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./html").Mode, s = e("../tokenizer").Tokenizer, o = e("./rhtml_highlight_rules").RHtmlHighlightRules, u = function (e, t) {
        this.$session = t, this.HighlightRules = o
    };
    r.inherits(u, i), function () {
        this.insertChunkInfo = {value: "<!--begin.rcode\n\nend.rcode-->\n", position: {row: 0, column: 15}}, this.getLanguageMode = function (e) {
            return this.$session.getState(e.row).match(/^r-/) ? "R" : "HTML"
        }, this.$id = "ace/mode/rhtml"
    }.call(u.prototype), t.Mode = u
}), ace.define("ace/mode/css", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/tokenizer", "ace/mode/css_highlight_rules", "ace/mode/matching_brace_outdent", "ace/worker/worker_client", "ace/mode/behaviour/css", "ace/mode/folding/cstyle"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./text").Mode, s = e("../tokenizer").Tokenizer, o = e("./css_highlight_rules").CssHighlightRules, u = e("./matching_brace_outdent").MatchingBraceOutdent, a = e("../worker/worker_client").WorkerClient, f = e("./behaviour/css").CssBehaviour, l = e("./folding/cstyle").FoldMode, c = function () {
        this.HighlightRules = o, this.$outdent = new u, this.$behaviour = new f, this.foldingRules = new l
    };
    r.inherits(c, i), function () {
        this.foldingRules = "cStyle", this.blockComment = {start: "/*", end: "*/"}, this.getNextLineIndent = function (e, t, n) {
            var r = this.$getIndent(t), i = this.getTokenizer().getLineTokens(t, e).tokens;
            if (i.length && i[i.length - 1].type == "comment")return r;
            var s = t.match(/^.*\{\s*$/);
            return s && (r += n), r
        }, this.checkOutdent = function (e, t, n) {
            return this.$outdent.checkOutdent(t, n)
        }, this.autoOutdent = function (e, t, n) {
            this.$outdent.autoOutdent(t, n)
        }, this.createWorker = function (e) {
            var t = new a(["ace"], "ace/mode/css_worker", "Worker");
            return t.attachToDocument(e.getDocument()), t.on("csslint", function (t) {
                e.setAnnotations(t.data)
            }), t.on("terminate", function () {
                e.clearAnnotations()
            }), t
        }, this.$id = "ace/mode/css"
    }.call(c.prototype), t.Mode = c
}), ace.define("ace/mode/css_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/lib/lang", "ace/mode/text_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("../lib/lang"), s = e("./text_highlight_rules").TextHighlightRules, o = t.supportType = "animation-fill-mode|alignment-adjust|alignment-baseline|animation-delay|animation-direction|animation-duration|animation-iteration-count|animation-name|animation-play-state|animation-timing-function|animation|appearance|azimuth|backface-visibility|background-attachment|background-break|background-clip|background-color|background-image|background-origin|background-position|background-repeat|background-size|background|baseline-shift|binding|bleed|bookmark-label|bookmark-level|bookmark-state|bookmark-target|border-bottom|border-bottom-color|border-bottom-left-radius|border-bottom-right-radius|border-bottom-style|border-bottom-width|border-collapse|border-color|border-image|border-image-outset|border-image-repeat|border-image-slice|border-image-source|border-image-width|border-left|border-left-color|border-left-style|border-left-width|border-radius|border-right|border-right-color|border-right-style|border-right-width|border-spacing|border-style|border-top|border-top-color|border-top-left-radius|border-top-right-radius|border-top-style|border-top-width|border-width|border|bottom|box-align|box-decoration-break|box-direction|box-flex-group|box-flex|box-lines|box-ordinal-group|box-orient|box-pack|box-shadow|box-sizing|break-after|break-before|break-inside|caption-side|clear|clip|color-profile|color|column-count|column-fill|column-gap|column-rule|column-rule-color|column-rule-style|column-rule-width|column-span|column-width|columns|content|counter-increment|counter-reset|crop|cue-after|cue-before|cue|cursor|direction|display|dominant-baseline|drop-initial-after-adjust|drop-initial-after-align|drop-initial-before-adjust|drop-initial-before-align|drop-initial-size|drop-initial-value|elevation|empty-cells|fit|fit-position|float-offset|float|font-family|font-size|font-size-adjust|font-stretch|font-style|font-variant|font-weight|font|grid-columns|grid-rows|hanging-punctuation|height|hyphenate-after|hyphenate-before|hyphenate-character|hyphenate-lines|hyphenate-resource|hyphens|icon|image-orientation|image-rendering|image-resolution|inline-box-align|left|letter-spacing|line-height|line-stacking-ruby|line-stacking-shift|line-stacking-strategy|line-stacking|list-style-image|list-style-position|list-style-type|list-style|margin-bottom|margin-left|margin-right|margin-top|margin|mark-after|mark-before|mark|marks|marquee-direction|marquee-play-count|marquee-speed|marquee-style|max-height|max-width|min-height|min-width|move-to|nav-down|nav-index|nav-left|nav-right|nav-up|opacity|orphans|outline-color|outline-offset|outline-style|outline-width|outline|overflow-style|overflow-x|overflow-y|overflow|padding-bottom|padding-left|padding-right|padding-top|padding|page-break-after|page-break-before|page-break-inside|page-policy|page|pause-after|pause-before|pause|perspective-origin|perspective|phonemes|pitch-range|pitch|play-during|pointer-events|position|presentation-level|punctuation-trim|quotes|rendering-intent|resize|rest-after|rest-before|rest|richness|right|rotation-point|rotation|ruby-align|ruby-overhang|ruby-position|ruby-span|size|speak-header|speak-numeral|speak-punctuation|speak|speech-rate|stress|string-set|table-layout|target-name|target-new|target-position|target|text-align-last|text-align|text-decoration|text-emphasis|text-height|text-indent|text-justify|text-outline|text-shadow|text-transform|text-wrap|top|transform-origin|transform-style|transform|transition-delay|transition-duration|transition-property|transition-timing-function|transition|unicode-bidi|vertical-align|visibility|voice-balance|voice-duration|voice-family|voice-pitch-range|voice-pitch|voice-rate|voice-stress|voice-volume|volume|white-space-collapse|white-space|widows|width|word-break|word-spacing|word-wrap|z-index", u = t.supportFunction = "rgb|rgba|url|attr|counter|counters", a = t.supportConstant = "absolute|after-edge|after|all-scroll|all|alphabetic|always|antialiased|armenian|auto|avoid-column|avoid-page|avoid|balance|baseline|before-edge|before|below|bidi-override|block-line-height|block|bold|bolder|border-box|both|bottom|box|break-all|break-word|capitalize|caps-height|caption|center|central|char|circle|cjk-ideographic|clone|close-quote|col-resize|collapse|column|consider-shifts|contain|content-box|cover|crosshair|cubic-bezier|dashed|decimal-leading-zero|decimal|default|disabled|disc|disregard-shifts|distribute-all-lines|distribute-letter|distribute-space|distribute|dotted|double|e-resize|ease-in|ease-in-out|ease-out|ease|ellipsis|end|exclude-ruby|fill|fixed|georgian|glyphs|grid-height|groove|hand|hanging|hebrew|help|hidden|hiragana-iroha|hiragana|horizontal|icon|ideograph-alpha|ideograph-numeric|ideograph-parenthesis|ideograph-space|ideographic|inactive|include-ruby|inherit|initial|inline-block|inline-box|inline-line-height|inline-table|inline|inset|inside|inter-ideograph|inter-word|invert|italic|justify|katakana-iroha|katakana|keep-all|last|left|lighter|line-edge|line-through|line|linear|list-item|local|loose|lower-alpha|lower-greek|lower-latin|lower-roman|lowercase|lr-tb|ltr|mathematical|max-height|max-size|medium|menu|message-box|middle|move|n-resize|ne-resize|newspaper|no-change|no-close-quote|no-drop|no-open-quote|no-repeat|none|normal|not-allowed|nowrap|nw-resize|oblique|open-quote|outset|outside|overline|padding-box|page|pointer|pre-line|pre-wrap|pre|preserve-3d|progress|relative|repeat-x|repeat-y|repeat|replaced|reset-size|ridge|right|round|row-resize|rtl|s-resize|scroll|se-resize|separate|slice|small-caps|small-caption|solid|space|square|start|static|status-bar|step-end|step-start|steps|stretch|strict|sub|super|sw-resize|table-caption|table-cell|table-column-group|table-column|table-footer-group|table-header-group|table-row-group|table-row|table|tb-rl|text-after-edge|text-before-edge|text-bottom|text-size|text-top|text|thick|thin|transparent|underline|upper-alpha|upper-latin|upper-roman|uppercase|use-script|vertical-ideographic|vertical-text|visible|w-resize|wait|whitespace|z-index|zero", f = t.supportConstantColor = "aqua|black|blue|fuchsia|gray|green|lime|maroon|navy|olive|orange|purple|red|silver|teal|white|yellow", l = t.supportConstantFonts = "arial|century|comic|courier|garamond|georgia|helvetica|impact|lucida|symbol|system|tahoma|times|trebuchet|utopia|verdana|webdings|sans-serif|serif|monospace", c = t.numRe = "\\-?(?:(?:[0-9]+)|(?:[0-9]*\\.[0-9]+))", h = t.pseudoElements = "(\\:+)\\b(after|before|first-letter|first-line|moz-selection|selection)\\b", p = t.pseudoClasses = "(:)\\b(active|checked|disabled|empty|enabled|first-child|first-of-type|focus|hover|indeterminate|invalid|last-child|last-of-type|link|not|nth-child|nth-last-child|nth-last-of-type|nth-of-type|only-child|only-of-type|required|root|target|valid|visited)\\b", d = function () {
        var e = this.createKeywordMapper({"support.function": u, "support.constant": a, "support.type": o, "support.constant.color": f, "support.constant.fonts": l}, "text", !0);
        this.$rules = {start: [
            {token: "comment", regex: "\\/\\*", push: "comment"},
            {token: "paren.lparen", regex: "\\{", push: "ruleset"},
            {token: "string", regex: "@.*?{", push: "media"},
            {token: "keyword", regex: "#[a-z0-9-_]+"},
            {token: "variable", regex: "\\.[a-z0-9-_]+"},
            {token: "string", regex: ":[a-z0-9-_]+"},
            {token: "constant", regex: "[a-z0-9-_]+"},
            {caseInsensitive: !0}
        ], media: [
            {token: "comment", regex: "\\/\\*", push: "comment"},
            {token: "paren.lparen", regex: "\\{", push: "ruleset"},
            {token: "string", regex: "\\}", next: "pop"},
            {token: "keyword", regex: "#[a-z0-9-_]+"},
            {token: "variable", regex: "\\.[a-z0-9-_]+"},
            {token: "string", regex: ":[a-z0-9-_]+"},
            {token: "constant", regex: "[a-z0-9-_]+"},
            {caseInsensitive: !0}
        ], comment: [
            {token: "comment", regex: "\\*\\/", next: "pop"},
            {defaultToken: "comment"}
        ], ruleset: [
            {token: "paren.rparen", regex: "\\}", next: "pop"},
            {token: "comment", regex: "\\/\\*", push: "comment"},
            {token: "string", regex: '["](?:(?:\\\\.)|(?:[^"\\\\]))*?["]'},
            {token: "string", regex: "['](?:(?:\\\\.)|(?:[^'\\\\]))*?[']"},
            {token: ["constant.numeric", "keyword"], regex: "(" + c + ")(ch|cm|deg|em|ex|fr|gd|grad|Hz|in|kHz|mm|ms|pc|pt|px|rad|rem|s|turn|vh|vm|vw|%)"},
            {token: "constant.numeric", regex: c},
            {token: "constant.numeric", regex: "#[a-f0-9]{6}"},
            {token: "constant.numeric", regex: "#[a-f0-9]{3}"},
            {token: ["punctuation", "entity.other.attribute-name.pseudo-element.css"], regex: h},
            {token: ["punctuation", "entity.other.attribute-name.pseudo-class.css"], regex: p},
            {token: ["support.function", "string", "support.function"], regex: "(url\\()(.*)(\\))"},
            {token: e, regex: "\\-?[a-zA-Z_][a-zA-Z0-9_\\-]*"},
            {caseInsensitive: !0}
        ]}, this.normalizeRules()
    };
    r.inherits(d, s), t.CssHighlightRules = d
}), ace.define("ace/mode/behaviour/css", ["require", "exports", "module", "ace/lib/oop", "ace/mode/behaviour", "ace/mode/behaviour/cstyle", "ace/token_iterator"], function (e, t, n) {
    var r = e("../../lib/oop"), i = e("../behaviour").Behaviour, s = e("./cstyle").CstyleBehaviour, o = e("../../token_iterator").TokenIterator, u = function () {
        this.inherit(s), this.add("colon", "insertion", function (e, t, n, r, i) {
            if (i === ":") {
                var s = n.getCursorPosition(), u = new o(r, s.row, s.column), a = u.getCurrentToken();
                a && a.value.match(/\s+/) && (a = u.stepBackward());
                if (a && a.type === "support.type") {
                    var f = r.doc.getLine(s.row), l = f.substring(s.column, s.column + 1);
                    if (l === ":")return{text: "", selection: [1, 1]};
                    if (!f.substring(s.column).match(/^\s*;/))return{text: ":;", selection: [1, 1]}
                }
            }
        }), this.add("colon", "deletion", function (e, t, n, r, i) {
            var s = r.doc.getTextRange(i);
            if (!i.isMultiLine() && s === ":") {
                var u = n.getCursorPosition(), a = new o(r, u.row, u.column), f = a.getCurrentToken();
                f && f.value.match(/\s+/) && (f = a.stepBackward());
                if (f && f.type === "support.type") {
                    var l = r.doc.getLine(i.start.row), c = l.substring(i.end.column, i.end.column + 1);
                    if (c === ";")return i.end.column++, i
                }
            }
        }), this.add("semicolon", "insertion", function (e, t, n, r, i) {
            if (i === ";") {
                var s = n.getCursorPosition(), o = r.doc.getLine(s.row), u = o.substring(s.column, s.column + 1);
                if (u === ";")return{text: "", selection: [1, 1]}
            }
        })
    };
    r.inherits(u, s), t.CssBehaviour = u
}), ace.define("ace/mode/folding/cstyle", ["require", "exports", "module", "ace/lib/oop", "ace/range", "ace/mode/folding/fold_mode"], function (e, t, n) {
    var r = e("../../lib/oop"), i = e("../../range").Range, s = e("./fold_mode").FoldMode, o = t.FoldMode = function (e) {
        e && (this.foldingStartMarker = new RegExp(this.foldingStartMarker.source.replace(/\|[^|]*?$/, "|" + e.start)), this.foldingStopMarker = new RegExp(this.foldingStopMarker.source.replace(/\|[^|]*?$/, "|" + e.end)))
    };
    r.inherits(o, s), function () {
        this.foldingStartMarker = /(\{|\[)[^\}\]]*$|^\s*(\/\*)/, this.foldingStopMarker = /^[^\[\{]*(\}|\])|^[\s\*]*(\*\/)/, this.getFoldWidgetRange = function (e, t, n, r) {
            var i = e.getLine(n), s = i.match(this.foldingStartMarker);
            if (s) {
                var o = s.index;
                if (s[1])return this.openingBracketBlock(e, s[1], n, o);
                var u = e.getCommentFoldRange(n, o + s[0].length, 1);
                return u && !u.isMultiLine() && (r ? u = this.getSectionRange(e, n) : t != "all" && (u = null)), u
            }
            if (t === "markbegin")return;
            var s = i.match(this.foldingStopMarker);
            if (s) {
                var o = s.index + s[0].length;
                return s[1] ? this.closingBracketBlock(e, s[1], n, o) : e.getCommentFoldRange(n, o, -1)
            }
        }, this.getSectionRange = function (e, t) {
            var n = e.getLine(t), r = n.search(/\S/), s = t, o = n.length;
            t += 1;
            var u = t, a = e.getLength();
            while (++t < a) {
                n = e.getLine(t);
                var f = n.search(/\S/);
                if (f === -1)continue;
                if (r > f)break;
                var l = this.getFoldWidgetRange(e, "all", t);
                if (l) {
                    if (l.start.row <= s)break;
                    if (l.isMultiLine())t = l.end.row; else if (r == f)break
                }
                u = t
            }
            return new i(s, o, u, e.getLine(u).length)
        }
    }.call(o.prototype)
}), ace.define("ace/mode/xml_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/xml_util", "ace/mode/text_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./xml_util"), s = e("./text_highlight_rules").TextHighlightRules, o = function (e) {
        this.$rules = {start: [
            {token: "punctuation.string.begin", regex: "<\\!\\[CDATA\\[", next: "cdata"},
            {token: ["punctuation.instruction.begin", "keyword.instruction"], regex: "(<\\?)(xml)(?=[\\s])", next: "xml_declaration"},
            {token: ["punctuation.instruction.begin", "keyword.instruction"], regex: "(<\\?)([-_a-zA-Z0-9]+)", next: "instruction"},
            {token: "comment", regex: "<\\!--", next: "comment"},
            {token: ["punctuation.doctype.begin", "meta.tag.doctype"], regex: "(<\\!)(DOCTYPE)(?=[\\s])", next: "doctype"},
            {include: "tag"},
            {include: "reference"}
        ], xml_declaration: [
            {include: "attributes"},
            {include: "instruction"}
        ], instruction: [
            {token: "punctuation.instruction.end", regex: "\\?>", next: "start"}
        ], doctype: [
            {include: "space"},
            {include: "string"},
            {token: "punctuation.doctype.end", regex: ">", next: "start"},
            {token: "xml-pe", regex: "[-_a-zA-Z0-9:]+"},
            {token: "punctuation.begin", regex: "\\[", push: "declarations"}
        ], declarations: [
            {token: "text", regex: "\\s+"},
            {token: "punctuation.end", regex: "]", next: "pop"},
            {token: ["punctuation.begin", "keyword"], regex: "(<\\!)([-_a-zA-Z0-9]+)", push: [
                {token: "text", regex: "\\s+"},
                {token: "punctuation.end", regex: ">", next: "pop"},
                {include: "string"}
            ]}
        ], cdata: [
            {token: "string.end", regex: "\\]\\]>", next: "start"},
            {token: "text", regex: "\\s+"},
            {token: "text", regex: "(?:[^\\]]|\\](?!\\]>))+"}
        ], comment: [
            {token: "comment", regex: "-->", next: "start"},
            {defaultToken: "comment"}
        ], tag: [
            {token: ["meta.tag.punctuation.begin", "meta.tag.name"], regex: "(<)((?:[-_a-zA-Z0-9]+:)?[-_a-zA-Z0-9]+)", next: [
                {include: "attributes"},
                {token: "meta.tag.punctuation.end", regex: "/?>", next: "start"}
            ]},
            {token: ["meta.tag.punctuation.begin", "meta.tag.name"], regex: "(</)((?:[-_a-zA-Z0-9]+:)?[-_a-zA-Z0-9]+)", next: [
                {include: "space"},
                {token: "meta.tag.punctuation.end", regex: ">", next: "start"}
            ]}
        ], space: [
            {token: "text", regex: "\\s+"}
        ], reference: [
            {token: "constant.language.escape", regex: "(?:&#[0-9]+;)|(?:&#x[0-9a-fA-F]+;)|(?:&[a-zA-Z0-9_:\\.-]+;)"},
            {token: "text", regex: "&"}
        ], string: [
            {token: "string", regex: "'", push: "qstring_inner"},
            {token: "string", regex: '"', push: "qqstring_inner"}
        ], qstring_inner: [
            {token: "string", regex: "'", next: "pop"},
            {include: "reference"},
            {defaultToken: "string"}
        ], qqstring_inner: [
            {token: "string", regex: '"', next: "pop"},
            {include: "reference"},
            {defaultToken: "string"}
        ], attributes: [
            {token: "entity.other.attribute-name", regex: "(?:[-_a-zA-Z0-9]+:)?[-_a-zA-Z0-9]+"},
            {token: "keyword.operator.separator", regex: "="},
            {include: "space"},
            {include: "string"}
        ]}, this.constructor === o && this.normalizeRules()
    };
    (function () {
        this.embedTagRules = function (e, t, n) {
            this.$rules.tag.unshift({token: ["meta.tag.punctuation.begin", "meta.tag.name." + n], regex: "(<)(" + n + ")", next: [
                {include: "space"},
                {include: "attributes"},
                {token: "meta.tag.punctuation.end", regex: "/?>", next: t + "start"}
            ]}), this.$rules[n + "-end"] = [
                {include: "space"},
                {token: "meta.tag.punctuation.end", regex: ">", next: "start", onMatch: function (e, t, n) {
                    return n.splice(0), this.token
                }}
            ], this.embedRules(e, t, [
                {token: ["meta.tag.punctuation.begin", "meta.tag.name." + n], regex: "(</)(" + n + ")", next: n + "-end"},
                {token: "string.begin", regex: "<\\!\\[CDATA\\["},
                {token: "string.end", regex: "\\]\\]>"}
            ])
        }
    }).call(s.prototype), r.inherits(o, s), t.XmlHighlightRules = o
}), ace.define("ace/mode/xml_util", ["require", "exports", "module"], function (e, t, n) {
    function r(e) {
        return[
            {token: "string", regex: '"', next: e + "_qqstring"},
            {token: "string", regex: "'", next: e + "_qstring"}
        ]
    }

    function i(e, t) {
        return[
            {token: "string", regex: e, next: t},
            {token: "constant.language.escape", regex: "(?:&#[0-9]+;)|(?:&#x[0-9a-fA-F]+;)|(?:&[a-zA-Z0-9_:\\.-]+;)"},
            {defaultToken: "string"}
        ]
    }

    t.tag = function (e, t, n, s) {
        e[t] = [
            {token: "text", regex: "\\s+"},
            {token: s ? function (e) {
                return s[e] ? "meta.tag.tag-name." + s[e] : "meta.tag.tag-name"
            } : "meta.tag.tag-name", regex: "[-_a-zA-Z0-9:]+", next: t + "_embed_attribute_list"},
            {token: "empty", regex: "", next: t + "_embed_attribute_list"}
        ], e[t + "_qstring"] = i("'", t + "_embed_attribute_list"), e[t + "_qqstring"] = i('"', t + "_embed_attribute_list"), e[t + "_embed_attribute_list"] = [
            {token: "meta.tag.r", regex: "/?>", next: n},
            {token: "keyword.operator", regex: "="},
            {token: "entity.other.attribute-name", regex: "[-_a-zA-Z0-9:]+"},
            {token: "constant.numeric", regex: "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"},
            {token: "text", regex: "\\s+"}
        ].concat(r(t))
    }
}), ace.define("ace/mode/behaviour/html", ["require", "exports", "module", "ace/lib/oop", "ace/mode/behaviour/xml", "ace/mode/behaviour/cstyle", "ace/token_iterator"], function (e, t, n) {
    function a(e, t) {
        var n = e.type.split(".");
        return t.split(".").every(function (e) {
            return n.indexOf(e) !== -1
        })
    }

    var r = e("../../lib/oop"), i = e("../behaviour/xml").XmlBehaviour, s = e("./cstyle").CstyleBehaviour, o = e("../../token_iterator").TokenIterator, u = ["area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"], f = function () {
        this.inherit(i), this.add("autoclosing", "insertion", function (e, t, n, r, i) {
            if (i == ">") {
                var s = n.getCursorPosition(), f = new o(r, s.row, s.column), l = f.getCurrentToken();
                if (l && a(l, "string") && f.getCurrentTokenColumn() + l.value.length > s.column)return;
                var c = !1;
                if (!l || !a(l, "meta.tag") && (!a(l, "text") || !l.value.match("/"))) {
                    do l = f.stepBackward(); while (l && (a(l, "string") || a(l, "keyword.operator") || a(l, "entity.attribute-name") || a(l, "text")))
                } else c = !0;
                if (!l || !a(l, "meta.tag.name") || f.stepBackward().value.match("/"))return;
                var h = l.value;
                if (c)var h = h.substring(0, s.column - l.start);
                if (u.indexOf(h) !== -1)return;
                return{text: "></" + h + ">", selection: [1, 1]}
            }
        })
    };
    r.inherits(f, i), t.HtmlBehaviour = f
}), ace.define("ace/mode/behaviour/xml", ["require", "exports", "module", "ace/lib/oop", "ace/mode/behaviour", "ace/mode/behaviour/cstyle", "ace/token_iterator"], function (e, t, n) {
    function u(e, t) {
        var n = e.type.split(".");
        return t.split(".").every(function (e) {
            return n.indexOf(e) !== -1
        })
    }

    var r = e("../../lib/oop"), i = e("../behaviour").Behaviour, s = e("./cstyle").CstyleBehaviour, o = e("../../token_iterator").TokenIterator, a = function () {
        this.inherit(s, ["string_dquotes"]), this.add("autoclosing", "insertion", function (e, t, n, r, i) {
            if (i == ">") {
                var s = n.getCursorPosition(), a = new o(r, s.row, s.column), f = a.getCurrentToken();
                if (f && u(f, "string") && a.getCurrentTokenColumn() + f.value.length > s.column)return;
                var l = !1;
                if (!f || !u(f, "meta.tag") && (!u(f, "text") || !f.value.match("/"))) {
                    do f = a.stepBackward(); while (f && (u(f, "string") || u(f, "keyword.operator") || u(f, "entity.attribute-name") || u(f, "text")))
                } else l = !0;
                if (!f || !u(f, "meta.tag.name") || a.stepBackward().value.match("/"))return;
                var c = f.value;
                if (l)var c = c.substring(0, s.column - f.start);
                return{text: "></" + c + ">", selection: [1, 1]}
            }
        }), this.add("autoindent", "insertion", function (e, t, n, r, i) {
            if (i == "\n") {
                var s = n.getCursorPosition(), o = r.getLine(s.row), u = o.substring(s.column, s.column + 2);
                if (u == "</") {
                    var a = this.$getIndent(o), f = a + r.getTabString();
                    return{text: "\n" + f + "\n" + a, selection: [1, f.length, 1, f.length]}
                }
            }
        })
    };
    r.inherits(a, i), t.XmlBehaviour = a
}), ace.define("ace/mode/folding/html", ["require", "exports", "module", "ace/lib/oop", "ace/mode/folding/mixed", "ace/mode/folding/xml", "ace/mode/folding/cstyle"], function (e, t, n) {
    var r = e("../../lib/oop"), i = e("./mixed").FoldMode, s = e("./xml").FoldMode, o = e("./cstyle").FoldMode, u = t.FoldMode = function () {
        i.call(this, new s({area: 1, base: 1, br: 1, col: 1, command: 1, embed: 1, hr: 1, img: 1, input: 1, keygen: 1, link: 1, meta: 1, param: 1, source: 1, track: 1, wbr: 1, li: 1, dt: 1, dd: 1, p: 1, rt: 1, rp: 1, optgroup: 1, option: 1, colgroup: 1, td: 1, th: 1}), {"js-": new o, "css-": new o})
    };
    r.inherits(u, i)
}), ace.define("ace/mode/behaviour/cstyle", ["require", "exports", "module", "ace/lib/oop", "ace/mode/behaviour", "ace/token_iterator", "ace/lib/lang"], function (e, t, n) {
    var r = e("../../lib/oop"), i = e("../behaviour").Behaviour, s = e("../../token_iterator").TokenIterator, o = e("../../lib/lang"), u = ["text", "paren.rparen", "punctuation.operator"], a = ["text", "paren.rparen", "punctuation.operator", "comment"], f, l = {}, c = function (e) {
        var t = -1;
        e.multiSelect && (t = e.selection.id, l.rangeCount != e.multiSelect.rangeCount && (l = {rangeCount: e.multiSelect.rangeCount}));
        if (l[t])return f = l[t];
        f = l[t] = {autoInsertedBrackets: 0, autoInsertedRow: -1, autoInsertedLineEnd: "", maybeInsertedBrackets: 0, maybeInsertedRow: -1, maybeInsertedLineStart: "", maybeInsertedLineEnd: ""}
    }, h = function () {
        this.add("braces", "insertion", function (e, t, n, r, i) {
            var s = n.getCursorPosition(), u = r.doc.getLine(s.row);
            if (i == "{") {
                c(n);
                var a = n.getSelectionRange(), l = r.doc.getTextRange(a);
                if (l !== "" && l !== "{" && n.getWrapBehavioursEnabled())return{text: "{" + l + "}", selection: !1};
                if (h.isSaneInsertion(n, r))return/[\]\}\)]/.test(u[s.column]) || n.inMultiSelectMode ? (h.recordAutoInsert(n, r, "}"), {text: "{}", selection: [1, 1]}) : (h.recordMaybeInsert(n, r, "{"), {text: "{", selection: [1, 1]})
            } else if (i == "}") {
                c(n);
                var p = u.substring(s.column, s.column + 1);
                if (p == "}") {
                    var d = r.$findOpeningBracket("}", {column: s.column + 1, row: s.row});
                    if (d !== null && h.isAutoInsertedClosing(s, u, i))return h.popAutoInsertedClosing(), {text: "", selection: [1, 1]}
                }
            } else {
                if (i == "\n" || i == "\r\n") {
                    c(n);
                    var v = "";
                    h.isMaybeInsertedClosing(s, u) && (v = o.stringRepeat("}", f.maybeInsertedBrackets), h.clearMaybeInsertedClosing());
                    var p = u.substring(s.column, s.column + 1);
                    if (p === "}") {
                        var m = r.findMatchingBracket({row: s.row, column: s.column + 1}, "}");
                        if (!m)return null;
                        var g = this.$getIndent(r.getLine(m.row))
                    } else {
                        if (!v) {
                            h.clearMaybeInsertedClosing();
                            return
                        }
                        var g = this.$getIndent(u)
                    }
                    var y = g + r.getTabString();
                    return{text: "\n" + y + "\n" + g + v, selection: [1, y.length, 1, y.length]}
                }
                h.clearMaybeInsertedClosing()
            }
        }), this.add("braces", "deletion", function (e, t, n, r, i) {
            var s = r.doc.getTextRange(i);
            if (!i.isMultiLine() && s == "{") {
                c(n);
                var o = r.doc.getLine(i.start.row), u = o.substring(i.end.column, i.end.column + 1);
                if (u == "}")return i.end.column++, i;
                f.maybeInsertedBrackets--
            }
        }), this.add("parens", "insertion", function (e, t, n, r, i) {
            if (i == "(") {
                c(n);
                var s = n.getSelectionRange(), o = r.doc.getTextRange(s);
                if (o !== "" && n.getWrapBehavioursEnabled())return{text: "(" + o + ")", selection: !1};
                if (h.isSaneInsertion(n, r))return h.recordAutoInsert(n, r, ")"), {text: "()", selection: [1, 1]}
            } else if (i == ")") {
                c(n);
                var u = n.getCursorPosition(), a = r.doc.getLine(u.row), f = a.substring(u.column, u.column + 1);
                if (f == ")") {
                    var l = r.$findOpeningBracket(")", {column: u.column + 1, row: u.row});
                    if (l !== null && h.isAutoInsertedClosing(u, a, i))return h.popAutoInsertedClosing(), {text: "", selection: [1, 1]}
                }
            }
        }), this.add("parens", "deletion", function (e, t, n, r, i) {
            var s = r.doc.getTextRange(i);
            if (!i.isMultiLine() && s == "(") {
                c(n);
                var o = r.doc.getLine(i.start.row), u = o.substring(i.start.column + 1, i.start.column + 2);
                if (u == ")")return i.end.column++, i
            }
        }), this.add("brackets", "insertion", function (e, t, n, r, i) {
            if (i == "[") {
                c(n);
                var s = n.getSelectionRange(), o = r.doc.getTextRange(s);
                if (o !== "" && n.getWrapBehavioursEnabled())return{text: "[" + o + "]", selection: !1};
                if (h.isSaneInsertion(n, r))return h.recordAutoInsert(n, r, "]"), {text: "[]", selection: [1, 1]}
            } else if (i == "]") {
                c(n);
                var u = n.getCursorPosition(), a = r.doc.getLine(u.row), f = a.substring(u.column, u.column + 1);
                if (f == "]") {
                    var l = r.$findOpeningBracket("]", {column: u.column + 1, row: u.row});
                    if (l !== null && h.isAutoInsertedClosing(u, a, i))return h.popAutoInsertedClosing(), {text: "", selection: [1, 1]}
                }
            }
        }), this.add("brackets", "deletion", function (e, t, n, r, i) {
            var s = r.doc.getTextRange(i);
            if (!i.isMultiLine() && s == "[") {
                c(n);
                var o = r.doc.getLine(i.start.row), u = o.substring(i.start.column + 1, i.start.column + 2);
                if (u == "]")return i.end.column++, i
            }
        }), this.add("string_dquotes", "insertion", function (e, t, n, r, i) {
            if (i == '"' || i == "'") {
                c(n);
                var s = i, o = n.getSelectionRange(), u = r.doc.getTextRange(o);
                if (u !== "" && u !== "'" && u != '"' && n.getWrapBehavioursEnabled())return{text: s + u + s, selection: !1};
                var a = n.getCursorPosition(), f = r.doc.getLine(a.row), l = f.substring(a.column - 1, a.column);
                if (l == "\\")return null;
                var p = r.getTokens(o.start.row), d = 0, v, m = -1;
                for (var g = 0; g < p.length; g++) {
                    v = p[g], v.type == "string" ? m = -1 : m < 0 && (m = v.value.indexOf(s));
                    if (v.value.length + d > o.start.column)break;
                    d += p[g].value.length
                }
                if (!v || m < 0 && v.type !== "comment" && (v.type !== "string" || o.start.column !== v.value.length + d - 1 && v.value.lastIndexOf(s) === v.value.length - 1)) {
                    if (!h.isSaneInsertion(n, r))return;
                    return{text: s + s, selection: [1, 1]}
                }
                if (v && v.type === "string") {
                    var y = f.substring(a.column, a.column + 1);
                    if (y == s)return{text: "", selection: [1, 1]}
                }
            }
        }), this.add("string_dquotes", "deletion", function (e, t, n, r, i) {
            var s = r.doc.getTextRange(i);
            if (!i.isMultiLine() && (s == '"' || s == "'")) {
                c(n);
                var o = r.doc.getLine(i.start.row), u = o.substring(i.start.column + 1, i.start.column + 2);
                if (u == s)return i.end.column++, i
            }
        })
    };
    h.isSaneInsertion = function (e, t) {
        var n = e.getCursorPosition(), r = new s(t, n.row, n.column);
        if (!this.$matchTokenType(r.getCurrentToken() || "text", u)) {
            var i = new s(t, n.row, n.column + 1);
            if (!this.$matchTokenType(i.getCurrentToken() || "text", u))return!1
        }
        return r.stepForward(), r.getCurrentTokenRow() !== n.row || this.$matchTokenType(r.getCurrentToken() || "text", a)
    }, h.$matchTokenType = function (e, t) {
        return t.indexOf(e.type || e) > -1
    }, h.recordAutoInsert = function (e, t, n) {
        var r = e.getCursorPosition(), i = t.doc.getLine(r.row);
        this.isAutoInsertedClosing(r, i, f.autoInsertedLineEnd[0]) || (f.autoInsertedBrackets = 0), f.autoInsertedRow = r.row, f.autoInsertedLineEnd = n + i.substr(r.column), f.autoInsertedBrackets++
    }, h.recordMaybeInsert = function (e, t, n) {
        var r = e.getCursorPosition(), i = t.doc.getLine(r.row);
        this.isMaybeInsertedClosing(r, i) || (f.maybeInsertedBrackets = 0), f.maybeInsertedRow = r.row, f.maybeInsertedLineStart = i.substr(0, r.column) + n, f.maybeInsertedLineEnd = i.substr(r.column), f.maybeInsertedBrackets++
    }, h.isAutoInsertedClosing = function (e, t, n) {
        return f.autoInsertedBrackets > 0 && e.row === f.autoInsertedRow && n === f.autoInsertedLineEnd[0] && t.substr(e.column) === f.autoInsertedLineEnd
    }, h.isMaybeInsertedClosing = function (e, t) {
        return f.maybeInsertedBrackets > 0 && e.row === f.maybeInsertedRow && t.substr(e.column) === f.maybeInsertedLineEnd && t.substr(0, e.column) == f.maybeInsertedLineStart
    }, h.popAutoInsertedClosing = function () {
        f.autoInsertedLineEnd = f.autoInsertedLineEnd.substr(1), f.autoInsertedBrackets--
    }, h.clearMaybeInsertedClosing = function () {
        f && (f.maybeInsertedBrackets = 0, f.maybeInsertedRow = -1)
    }, r.inherits(h, i), t.CstyleBehaviour = h
}), ace.define("ace/mode/folding/mixed", ["require", "exports", "module", "ace/lib/oop", "ace/mode/folding/fold_mode"], function (e, t, n) {
    var r = e("../../lib/oop"), i = e("./fold_mode").FoldMode, s = t.FoldMode = function (e, t) {
        this.defaultMode = e, this.subModes = t
    };
    r.inherits(s, i), function () {
        this.$getMode = function (e) {
            typeof e != "string" && (e = e[0]);
            for (var t in this.subModes)if (e.indexOf(t) === 0)return this.subModes[t];
            return null
        }, this.$tryMode = function (e, t, n, r) {
            var i = this.$getMode(e);
            return i ? i.getFoldWidget(t, n, r) : ""
        }, this.getFoldWidget = function (e, t, n) {
            return this.$tryMode(e.getState(n - 1), e, t, n) || this.$tryMode(e.getState(n), e, t, n) || this.defaultMode.getFoldWidget(e, t, n)
        }, this.getFoldWidgetRange = function (e, t, n) {
            var r = this.$getMode(e.getState(n - 1));
            if (!r || !r.getFoldWidget(e, t, n))r = this.$getMode(e.getState(n));
            if (!r || !r.getFoldWidget(e, t, n))r = this.defaultMode;
            return r.getFoldWidgetRange(e, t, n)
        }
    }.call(s.prototype)
}), ace.define("ace/mode/matching_brace_outdent", ["require", "exports", "module", "ace/range"], function (e, t, n) {
    var r = e("../range").Range, i = function () {
    };
    (function () {
        this.checkOutdent = function (e, t) {
            return/^\s+$/.test(e) ? /^\s*\}/.test(t) : !1
        }, this.autoOutdent = function (e, t) {
            var n = e.getLine(t), i = n.match(/^(\s*\})/);
            if (!i)return 0;
            var s = i[1].length, o = e.findMatchingBracket({row: t, column: s});
            if (!o || o.row == t)return 0;
            var u = this.$getIndent(e.getLine(o.row));
            e.replace(new r(t, 0, t, s - 1), u)
        }, this.$getIndent = function (e) {
            return e.match(/^\s*/)[0]
        }
    }).call(i.prototype), t.MatchingBraceOutdent = i
}), ace.define("ace/mode/folding/xml", ["require", "exports", "module", "ace/lib/oop", "ace/lib/lang", "ace/range", "ace/mode/folding/fold_mode", "ace/token_iterator"], function (e, t, n) {
    var r = e("../../lib/oop"), i = e("../../lib/lang"), s = e("../../range").Range, o = e("./fold_mode").FoldMode, u = e("../../token_iterator").TokenIterator, a = t.FoldMode = function (e) {
        o.call(this), this.voidElements = e || {}
    };
    r.inherits(a, o), function () {
        this.getFoldWidget = function (e, t, n) {
            var r = this._getFirstTagInLine(e, n);
            return r.closing ? t == "markbeginend" ? "end" : "" : !r.tagName || this.voidElements[r.tagName.toLowerCase()] ? "" : r.selfClosing ? "" : r.value.indexOf("/" + r.tagName) !== -1 ? "" : "start"
        }, this._getFirstTagInLine = function (e, t) {
            var n = e.getTokens(t), r = "";
            for (var s = 0; s < n.length; s++) {
                var o = n[s];
                o.type.lastIndexOf("meta.tag", 0) === 0 ? r += o.value : r += i.stringRepeat(" ", o.value.length)
            }
            return this._parseTag(r)
        }, this.tagRe = /^(\s*)(<?(\/?)([-_a-zA-Z0-9:!]*)\s*(\/?)>?)/, this._parseTag = function (e) {
            var t = e.match(this.tagRe), n = 0;
            return{value: e, match: t ? t[2] : "", closing: t ? !!t[3] : !1, selfClosing: t ? !!t[5] || t[2] == "/>" : !1, tagName: t ? t[4] : "", column: t[1] ? n + t[1].length : n}
        }, this._readTagForward = function (e) {
            var t = e.getCurrentToken();
            if (!t)return null;
            var n = "", r;
            do if (t.type.lastIndexOf("meta.tag", 0) === 0) {
                if (!r)var r = {row: e.getCurrentTokenRow(), column: e.getCurrentTokenColumn()};
                n += t.value;
                if (n.indexOf(">") !== -1) {
                    var i = this._parseTag(n);
                    return i.start = r, i.end = {row: e.getCurrentTokenRow(), column: e.getCurrentTokenColumn() + t.value.length}, e.stepForward(), i
                }
            } while (t = e.stepForward());
            return null
        }, this._readTagBackward = function (e) {
            var t = e.getCurrentToken();
            if (!t)return null;
            var n = "", r;
            do if (t.type.lastIndexOf("meta.tag", 0) === 0) {
                r || (r = {row: e.getCurrentTokenRow(), column: e.getCurrentTokenColumn() + t.value.length}), n = t.value + n;
                if (n.indexOf("<") !== -1) {
                    var i = this._parseTag(n);
                    return i.end = r, i.start = {row: e.getCurrentTokenRow(), column: e.getCurrentTokenColumn()}, e.stepBackward(), i
                }
            } while (t = e.stepBackward());
            return null
        }, this._pop = function (e, t) {
            while (e.length) {
                var n = e[e.length - 1];
                if (!t || n.tagName == t.tagName)return e.pop();
                if (this.voidElements[t.tagName])return;
                if (this.voidElements[n.tagName]) {
                    e.pop();
                    continue
                }
                return null
            }
        }, this.getFoldWidgetRange = function (e, t, n) {
            var r = this._getFirstTagInLine(e, n);
            if (!r.match)return null;
            var i = r.closing || r.selfClosing, o = [], a;
            if (!i) {
                var f = new u(e, n, r.column), l = {row: n, column: r.column + r.tagName.length + 2};
                while (a = this._readTagForward(f)) {
                    if (a.selfClosing) {
                        if (!o.length)return a.start.column += a.tagName.length + 2, a.end.column -= 2, s.fromPoints(a.start, a.end);
                        continue
                    }
                    if (a.closing) {
                        this._pop(o, a);
                        if (o.length == 0)return s.fromPoints(l, a.start)
                    } else o.push(a)
                }
            } else {
                var f = new u(e, n, r.column + r.match.length), c = {row: n, column: r.column};
                while (a = this._readTagBackward(f)) {
                    if (a.selfClosing) {
                        if (!o.length)return a.start.column += a.tagName.length + 2, a.end.column -= 2, s.fromPoints(a.start, a.end);
                        continue
                    }
                    if (!a.closing) {
                        this._pop(o, a);
                        if (o.length == 0)return a.start.column += a.tagName.length + 2, s.fromPoints(a.start, c)
                    } else o.push(a)
                }
            }
        }
    }.call(a.prototype)
}), ace.define("ace/mode/doc_comment_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./text_highlight_rules").TextHighlightRules, s = function () {
        this.$rules = {start: [
            {token: "comment.doc.tag", regex: "@[\\w\\d_]+"},
            {token: "comment.doc.tag", regex: "\\bTODO\\b"},
            {defaultToken: "comment.doc"}
        ]}
    };
    r.inherits(s, i), s.getStartRule = function (e) {
        return{token: "comment.doc", regex: "\\/\\*(?=\\*)", next: e}
    }, s.getEndRule = function (e) {
        return{token: "comment.doc", regex: "\\*\\/", next: e}
    }, t.DocCommentHighlightRules = s
}), ace.define("ace/mode/html_completions", ["require", "exports", "module", "ace/token_iterator"], function (e, t, n) {
    function f(e, t) {
        var n = e.type.split(".");
        return t.split(".").every(function (e) {
            return n.indexOf(e) !== -1
        })
    }

    function l(e, t) {
        var n = new r(e, t.row, t.column), i = n.getCurrentToken();
        if (!i || !f(i, "tag") && (!f(i, "text") || !i.value.match("/")))do i = n.stepBackward(); while (i && (f(i, "string") || f(i, "operator") || f(i, "attribute-name") || f(i, "text")));
        if (i && f(i, "tag-name") && !n.stepBackward().value.match("/"))return i.value
    }

    var r = e("../token_iterator").TokenIterator, i = ["accesskey", "class", "contenteditable", "contextmenu", "dir", "draggable", "dropzone", "hidden", "id", "lang", "spellcheck", "style", "tabindex", "title", "translate"], s = ["onabort", "onblur", "oncancel", "oncanplay", "oncanplaythrough", "onchange", "onclick", "onclose", "oncontextmenu", "oncuechange", "ondblclick", "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop", "ondurationchange", "onemptied", "onended", "onerror", "onfocus", "oninput", "oninvalid", "onkeydown", "onkeypress", "onkeyup", "onload", "onloadeddata", "onloadedmetadata", "onloadstart", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup", "onmousewheel", "onpause", "onplay", "onplaying", "onprogress", "onratechange", "onreset", "onscroll", "onseeked", "onseeking", "onselect", "onshow", "onstalled", "onsubmit", "onsuspend", "ontimeupdate", "onvolumechange", "onwaiting"], o = i.concat(s), u = {html: ["manifest"], head: [], title: [], base: ["href", "target"], link: ["href", "hreflang", "rel", "media", "type", "sizes"], meta: ["http-equiv", "name", "content", "charset"], style: ["type", "media", "scoped"], script: ["charset", "type", "src", "defer", "async"], noscript: ["href"], body: ["onafterprint", "onbeforeprint", "onbeforeunload", "onhashchange", "onmessage", "onoffline", "onpopstate", "onredo", "onresize", "onstorage", "onundo", "onunload"], section: [], nav: [], article: ["pubdate"], aside: [], h1: [], h2: [], h3: [], h4: [], h5: [], h6: [], header: [], footer: [], address: [], main: [], p: [], hr: [], pre: [], blockquote: ["cite"], ol: ["start", "reversed"], ul: [], li: ["value"], dl: [], dt: [], dd: [], figure: [], figcaption: [], div: [], a: ["href", "target", "ping", "rel", "media", "hreflang", "type"], em: [], strong: [], small: [], s: [], cite: [], q: ["cite"], dfn: [], abbr: [], data: [], time: ["datetime"], code: [], "var": [], samp: [], kbd: [], sub: [], sup: [], i: [], b: [], u: [], mark: [], ruby: [], rt: [], rp: [], bdi: [], bdo: [], span: [], br: [], wbr: [], ins: ["cite", "datetime"], del: ["cite", "datetime"], img: ["alt", "src", "height", "width", "usemap", "ismap"], iframe: ["name", "src", "height", "width", "sandbox", "seamless"], embed: ["src", "height", "width", "type"], object: ["param", "data", "type", "height", "width", "usemap", "name", "form", "classid"], param: ["name", "value"], video: ["src", "autobuffer", "autoplay", "loop", "controls", "width", "height", "poster"], audio: ["src", "autobuffer", "autoplay", "loop", "controls"], source: ["src", "type", "media"], track: ["kind", "src", "srclang", "label", "default"], canvas: ["width", "height"], map: ["name"], area: ["shape", "coords", "href", "hreflang", "alt", "target", "media", "rel", "ping", "type"], svg: [], math: [], table: ["summary"], caption: [], colgroup: ["span"], col: ["span"], tbody: [], thead: [], tfoot: [], tr: [], td: ["headers", "rowspan", "colspan"], th: ["headers", "rowspan", "colspan", "scope"], form: ["accept-charset", "action", "autocomplete", "enctype", "method", "name", "novalidate", "target"], fieldset: ["disabled", "form", "name"], legend: [], label: ["form", "for"], input: ["type", "accept", "alt", "autocomplete", "checked", "disabled", "form", "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget", "height", "list", "max", "maxlength", "min", "multiple", "pattern", "placeholder", "readonly", "required", "size", "src", "step", "width", "files", "value"], button: ["autofocus", "disabled", "form", "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget", "name", "value", "type"], select: ["autofocus", "disabled", "form", "multiple", "name", "size"], datalist: [], optgroup: ["disabled", "label"], option: ["disabled", "selected", "label", "value"], textarea: ["autofocus", "disabled", "form", "maxlength", "name", "placeholder", "readonly", "required", "rows", "cols", "wrap"], keygen: ["autofocus", "challenge", "disabled", "form", "keytype", "name"], output: ["for", "form", "name"], progress: ["value", "max"], meter: ["value", "min", "max", "low", "high", "optimum"], details: ["open"], summary: [], command: ["type", "label", "icon", "disabled", "checked", "radiogroup", "command"], menu: ["type", "label"], dialog: ["open"]}, a = Object.keys(u), c = function () {
    };
    (function () {
        this.getCompletions = function (e, t, n, r) {
            var i = t.getTokenAt(n.row, n.column);
            return i ? f(i, "tag-name") || i.value == "<" && f(i, "text") ? this.getTagCompletions(e, t, n, r) : f(i, "text") || f(i, "attribute-name") ? this.getAttributeCompetions(e, t, n, r) : [] : []
        }, this.getTagCompletions = function (e, t, n, r) {
            var i = a;
            return r && (i = i.filter(function (e) {
                return e.indexOf(r) === 0
            })), i.map(function (e) {
                return{value: e, meta: "tag"}
            })
        }, this.getAttributeCompetions = function (e, t, n, r) {
            var i = l(t, n);
            if (!i)return[];
            var s = o;
            return i in u && (s = s.concat(u[i])), r && (s = s.filter(function (e) {
                return e.indexOf(r) === 0
            })), s.map(function (e) {
                return{caption: e, snippet: e + '="$0"', meta: "attribute"}
            })
        }
    }).call(c.prototype), t.HtmlCompletions = c
}), ace.define("ace/mode/javascript_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/doc_comment_highlight_rules", "ace/mode/text_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./doc_comment_highlight_rules").DocCommentHighlightRules, s = e("./text_highlight_rules").TextHighlightRules, o = function () {
        var e = this.createKeywordMapper({"variable.language": "Array|Boolean|Date|Function|Iterator|Number|Object|RegExp|String|Proxy|Namespace|QName|XML|XMLList|ArrayBuffer|Float32Array|Float64Array|Int16Array|Int32Array|Int8Array|Uint16Array|Uint32Array|Uint8Array|Uint8ClampedArray|Error|EvalError|InternalError|RangeError|ReferenceError|StopIteration|SyntaxError|TypeError|URIError|decodeURI|decodeURIComponent|encodeURI|encodeURIComponent|eval|isFinite|isNaN|parseFloat|parseInt|JSON|Math|this|arguments|prototype|window|document", keyword: "const|yield|import|get|set|break|case|catch|continue|default|delete|do|else|finally|for|function|if|in|instanceof|new|return|switch|throw|try|typeof|let|var|while|with|debugger|__parent__|__count__|escape|unescape|with|__proto__|class|enum|extends|super|export|implements|private|public|interface|package|protected|static", "storage.type": "const|let|var|function", "constant.language": "null|Infinity|NaN|undefined", "support.function": "alert", "constant.language.boolean": "true|false"}, "identifier"), t = "case|do|else|finally|in|instanceof|return|throw|try|typeof|yield|void", n = "[a-zA-Z\\$_\xa1-\uffff][a-zA-Z\\d\\$_\xa1-\uffff]*\\b", r = "\\\\(?:x[0-9a-fA-F]{2}|u[0-9a-fA-F]{4}|[0-2][0-7]{0,2}|3[0-6][0-7]?|37[0-7]?|[4-7][0-7]?|.)";
        this.$rules = {no_regex: [
            {token: "comment", regex: "\\/\\/", next: "line_comment"},
            i.getStartRule("doc-start"),
            {token: "comment", regex: /\/\*/, next: "comment"},
            {token: "string", regex: "'(?=.)", next: "qstring"},
            {token: "string", regex: '"(?=.)', next: "qqstring"},
            {token: "constant.numeric", regex: /0[xX][0-9a-fA-F]+\b/},
            {token: "constant.numeric", regex: /[+-]?\d+(?:(?:\.\d*)?(?:[eE][+-]?\d+)?)?\b/},
            {token: ["storage.type", "punctuation.operator", "support.function", "punctuation.operator", "entity.name.function", "text", "keyword.operator"], regex: "(" + n + ")(\\.)(prototype)(\\.)(" + n + ")(\\s*)(=)", next: "function_arguments"},
            {token: ["storage.type", "punctuation.operator", "entity.name.function", "text", "keyword.operator", "text", "storage.type", "text", "paren.lparen"], regex: "(" + n + ")(\\.)(" + n + ")(\\s*)(=)(\\s*)(function)(\\s*)(\\()", next: "function_arguments"},
            {token: ["entity.name.function", "text", "keyword.operator", "text", "storage.type", "text", "paren.lparen"], regex: "(" + n + ")(\\s*)(=)(\\s*)(function)(\\s*)(\\()", next: "function_arguments"},
            {token: ["storage.type", "punctuation.operator", "entity.name.function", "text", "keyword.operator", "text", "storage.type", "text", "entity.name.function", "text", "paren.lparen"], regex: "(" + n + ")(\\.)(" + n + ")(\\s*)(=)(\\s*)(function)(\\s+)(\\w+)(\\s*)(\\()", next: "function_arguments"},
            {token: ["storage.type", "text", "entity.name.function", "text", "paren.lparen"], regex: "(function)(\\s+)(" + n + ")(\\s*)(\\()", next: "function_arguments"},
            {token: ["entity.name.function", "text", "punctuation.operator", "text", "storage.type", "text", "paren.lparen"], regex: "(" + n + ")(\\s*)(:)(\\s*)(function)(\\s*)(\\()", next: "function_arguments"},
            {token: ["text", "text", "storage.type", "text", "paren.lparen"], regex: "(:)(\\s*)(function)(\\s*)(\\()", next: "function_arguments"},
            {token: "keyword", regex: "(?:" + t + ")\\b", next: "start"},
            {token: ["punctuation.operator", "support.function"], regex: /(\.)(s(?:h(?:ift|ow(?:Mod(?:elessDialog|alDialog)|Help))|croll(?:X|By(?:Pages|Lines)?|Y|To)?|t(?:op|rike)|i(?:n|zeToContent|debar|gnText)|ort|u(?:p|b(?:str(?:ing)?)?)|pli(?:ce|t)|e(?:nd|t(?:Re(?:sizable|questHeader)|M(?:i(?:nutes|lliseconds)|onth)|Seconds|Ho(?:tKeys|urs)|Year|Cursor|Time(?:out)?|Interval|ZOptions|Date|UTC(?:M(?:i(?:nutes|lliseconds)|onth)|Seconds|Hours|Date|FullYear)|FullYear|Active)|arch)|qrt|lice|avePreferences|mall)|h(?:ome|andleEvent)|navigate|c(?:har(?:CodeAt|At)|o(?:s|n(?:cat|textual|firm)|mpile)|eil|lear(?:Timeout|Interval)?|a(?:ptureEvents|ll)|reate(?:StyleSheet|Popup|EventObject))|t(?:o(?:GMTString|S(?:tring|ource)|U(?:TCString|pperCase)|Lo(?:caleString|werCase))|est|a(?:n|int(?:Enabled)?))|i(?:s(?:NaN|Finite)|ndexOf|talics)|d(?:isableExternalCapture|ump|etachEvent)|u(?:n(?:shift|taint|escape|watch)|pdateCommands)|j(?:oin|avaEnabled)|p(?:o(?:p|w)|ush|lugins.refresh|a(?:ddings|rse(?:Int|Float)?)|r(?:int|ompt|eference))|e(?:scape|nableExternalCapture|val|lementFromPoint|x(?:p|ec(?:Script|Command)?))|valueOf|UTC|queryCommand(?:State|Indeterm|Enabled|Value)|f(?:i(?:nd|le(?:ModifiedDate|Size|CreatedDate|UpdatedDate)|xed)|o(?:nt(?:size|color)|rward)|loor|romCharCode)|watch|l(?:ink|o(?:ad|g)|astIndexOf)|a(?:sin|nchor|cos|t(?:tachEvent|ob|an(?:2)?)|pply|lert|b(?:s|ort))|r(?:ou(?:nd|teEvents)|e(?:size(?:By|To)|calc|turnValue|place|verse|l(?:oad|ease(?:Capture|Events)))|andom)|g(?:o|et(?:ResponseHeader|M(?:i(?:nutes|lliseconds)|onth)|Se(?:conds|lection)|Hours|Year|Time(?:zoneOffset)?|Da(?:y|te)|UTC(?:M(?:i(?:nutes|lliseconds)|onth)|Seconds|Hours|Da(?:y|te)|FullYear)|FullYear|A(?:ttention|llResponseHeaders)))|m(?:in|ove(?:B(?:y|elow)|To(?:Absolute)?|Above)|ergeAttributes|a(?:tch|rgins|x))|b(?:toa|ig|o(?:ld|rderWidths)|link|ack))\b(?=\()/},
            {token: ["punctuation.operator", "support.function.dom"], regex: /(\.)(s(?:ub(?:stringData|mit)|plitText|e(?:t(?:NamedItem|Attribute(?:Node)?)|lect))|has(?:ChildNodes|Feature)|namedItem|c(?:l(?:ick|o(?:se|neNode))|reate(?:C(?:omment|DATASection|aption)|T(?:Head|extNode|Foot)|DocumentFragment|ProcessingInstruction|E(?:ntityReference|lement)|Attribute))|tabIndex|i(?:nsert(?:Row|Before|Cell|Data)|tem)|open|delete(?:Row|C(?:ell|aption)|T(?:Head|Foot)|Data)|focus|write(?:ln)?|a(?:dd|ppend(?:Child|Data))|re(?:set|place(?:Child|Data)|move(?:NamedItem|Child|Attribute(?:Node)?)?)|get(?:NamedItem|Element(?:sBy(?:Name|TagName)|ById)|Attribute(?:Node)?)|blur)\b(?=\()/},
            {token: ["punctuation.operator", "support.constant"], regex: /(\.)(s(?:ystemLanguage|cr(?:ipts|ollbars|een(?:X|Y|Top|Left))|t(?:yle(?:Sheets)?|atus(?:Text|bar)?)|ibling(?:Below|Above)|ource|uffixes|e(?:curity(?:Policy)?|l(?:ection|f)))|h(?:istory|ost(?:name)?|as(?:h|Focus))|y|X(?:MLDocument|SLDocument)|n(?:ext|ame(?:space(?:s|URI)|Prop))|M(?:IN_VALUE|AX_VALUE)|c(?:haracterSet|o(?:n(?:structor|trollers)|okieEnabled|lorDepth|mp(?:onents|lete))|urrent|puClass|l(?:i(?:p(?:boardData)?|entInformation)|osed|asses)|alle(?:e|r)|rypto)|t(?:o(?:olbar|p)|ext(?:Transform|Indent|Decoration|Align)|ags)|SQRT(?:1_2|2)|i(?:n(?:ner(?:Height|Width)|put)|ds|gnoreCase)|zIndex|o(?:scpu|n(?:readystatechange|Line)|uter(?:Height|Width)|p(?:sProfile|ener)|ffscreenBuffering)|NEGATIVE_INFINITY|d(?:i(?:splay|alog(?:Height|Top|Width|Left|Arguments)|rectories)|e(?:scription|fault(?:Status|Ch(?:ecked|arset)|View)))|u(?:ser(?:Profile|Language|Agent)|n(?:iqueID|defined)|pdateInterval)|_content|p(?:ixelDepth|ort|ersonalbar|kcs11|l(?:ugins|atform)|a(?:thname|dding(?:Right|Bottom|Top|Left)|rent(?:Window|Layer)?|ge(?:X(?:Offset)?|Y(?:Offset)?))|r(?:o(?:to(?:col|type)|duct(?:Sub)?|mpter)|e(?:vious|fix)))|e(?:n(?:coding|abledPlugin)|x(?:ternal|pando)|mbeds)|v(?:isibility|endor(?:Sub)?|Linkcolor)|URLUnencoded|P(?:I|OSITIVE_INFINITY)|f(?:ilename|o(?:nt(?:Size|Family|Weight)|rmName)|rame(?:s|Element)|gColor)|E|whiteSpace|l(?:i(?:stStyleType|n(?:eHeight|kColor))|o(?:ca(?:tion(?:bar)?|lName)|wsrc)|e(?:ngth|ft(?:Context)?)|a(?:st(?:M(?:odified|atch)|Index|Paren)|yer(?:s|X)|nguage))|a(?:pp(?:MinorVersion|Name|Co(?:deName|re)|Version)|vail(?:Height|Top|Width|Left)|ll|r(?:ity|guments)|Linkcolor|bove)|r(?:ight(?:Context)?|e(?:sponse(?:XML|Text)|adyState))|global|x|m(?:imeTypes|ultiline|enubar|argin(?:Right|Bottom|Top|Left))|L(?:N(?:10|2)|OG(?:10E|2E))|b(?:o(?:ttom|rder(?:Width|RightWidth|BottomWidth|Style|Color|TopWidth|LeftWidth))|ufferDepth|elow|ackground(?:Color|Image)))\b/},
            {token: ["storage.type", "punctuation.operator", "support.function.firebug"], regex: /(console)(\.)(warn|info|log|error|time|timeEnd|assert)\b/},
            {token: e, regex: n},
            {token: "keyword.operator", regex: /--|\+\+|[!$%&*+\-~]|===|==|=|!=|!==|<=|>=|<<=|>>=|>>>=|<>|<|>|!|&&|\|\||\?\:|\*=|%=|\+=|\-=|&=|\^=/, next: "start"},
            {token: "punctuation.operator", regex: /\?|\:|\,|\;|\./, next: "start"},
            {token: "paren.lparen", regex: /[\[({]/, next: "start"},
            {token: "paren.rparen", regex: /[\])}]/},
            {token: "keyword.operator", regex: /\/=?/, next: "start"},
            {token: "comment", regex: /^#!.*$/}
        ], start: [i.getStartRule("doc-start"), {token: "comment", regex: "\\/\\*", next: "comment_regex_allowed"}, {token: "comment", regex: "\\/\\/", next: "line_comment_regex_allowed"}, {token: "string.regexp", regex: "\\/", next: "regex"}, {token: "text", regex: "\\s+|^$", next: "start"}, {token: "empty", regex: "", next: "no_regex"}], regex: [
            {token: "regexp.keyword.operator", regex: "\\\\(?:u[\\da-fA-F]{4}|x[\\da-fA-F]{2}|.)"},
            {token: "string.regexp", regex: "/[sxngimy]*", next: "no_regex"},
            {token: "invalid", regex: /\{\d+\b,?\d*\}[+*]|[+*$^?][+*]|[$^][?]|\?{3,}/},
            {token: "constant.language.escape", regex: /\(\?[:=!]|\)|\{\d+\b,?\d*\}|[+*]\?|[()$^+*?.]/},
            {token: "constant.language.delimiter", regex: /\|/},
            {token: "constant.language.escape", regex: /\[\^?/, next: "regex_character_class"},
            {token: "empty", regex: "$", next: "no_regex"},
            {defaultToken: "string.regexp"}
        ], regex_character_class: [
            {token: "regexp.keyword.operator", regex: "\\\\(?:u[\\da-fA-F]{4}|x[\\da-fA-F]{2}|.)"},
            {token: "constant.language.escape", regex: "]", next: "regex"},
            {token: "constant.language.escape", regex: "-"},
            {token: "empty", regex: "$", next: "no_regex"},
            {defaultToken: "string.regexp.charachterclass"}
        ], function_arguments: [
            {token: "variable.parameter", regex: n},
            {token: "punctuation.operator", regex: "[, ]+"},
            {token: "punctuation.operator", regex: "$"},
            {token: "empty", regex: "", next: "no_regex"}
        ], comment_regex_allowed: [
            {token: "comment", regex: "\\*\\/", next: "start"},
            {defaultToken: "comment"}
        ], comment: [
            {token: "comment", regex: "\\*\\/", next: "no_regex"},
            {defaultToken: "comment"}
        ], line_comment_regex_allowed: [
            {token: "comment", regex: "$|^", next: "start"},
            {defaultToken: "comment"}
        ], line_comment: [
            {token: "comment", regex: "$|^", next: "no_regex"},
            {defaultToken: "comment"}
        ], qqstring: [
            {token: "constant.language.escape", regex: r},
            {token: "string", regex: "\\\\$", next: "qqstring"},
            {token: "string", regex: '"|$', next: "no_regex"},
            {defaultToken: "string"}
        ], qstring: [
            {token: "constant.language.escape", regex: r},
            {token: "string", regex: "\\\\$", next: "qstring"},
            {token: "string", regex: "'|$", next: "no_regex"},
            {defaultToken: "string"}
        ]}, this.embedRules(i, "doc-", [i.getEndRule("no_regex")])
    };
    r.inherits(o, s), t.JavaScriptHighlightRules = o
}), ace.define("ace/mode/rhtml_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/r_highlight_rules", "ace/mode/html_highlight_rules", "ace/mode/text_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./r_highlight_rules").RHighlightRules, s = e("./html_highlight_rules").HtmlHighlightRules, o = e("./text_highlight_rules").TextHighlightRules, u = function () {
        s.call(this), this.$rules.start.unshift({token: "support.function.codebegin", regex: "^<!--\\s*begin.rcode\\s*(?:.*)", next: "r-start"}), this.embedRules(i, "r-", [
            {token: "support.function.codeend", regex: "^\\s*end.rcode\\s*-->", next: "start"}
        ], ["start"]), this.normalizeRules()
    };
    r.inherits(u, o), t.RHtmlHighlightRules = u
}), ace.define("ace/mode/javascript", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/tokenizer", "ace/mode/javascript_highlight_rules", "ace/mode/matching_brace_outdent", "ace/range", "ace/worker/worker_client", "ace/mode/behaviour/cstyle", "ace/mode/folding/cstyle"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./text").Mode, s = e("../tokenizer").Tokenizer, o = e("./javascript_highlight_rules").JavaScriptHighlightRules, u = e("./matching_brace_outdent").MatchingBraceOutdent, a = e("../range").Range, f = e("../worker/worker_client").WorkerClient, l = e("./behaviour/cstyle").CstyleBehaviour, c = e("./folding/cstyle").FoldMode, h = function () {
        this.HighlightRules = o, this.$outdent = new u, this.$behaviour = new l, this.foldingRules = new c
    };
    r.inherits(h, i), function () {
        this.lineCommentStart = "//", this.blockComment = {start: "/*", end: "*/"}, this.getNextLineIndent = function (e, t, n) {
            var r = this.$getIndent(t), i = this.getTokenizer().getLineTokens(t, e), s = i.tokens, o = i.state;
            if (s.length && s[s.length - 1].type == "comment")return r;
            if (e == "start" || e == "no_regex") {
                var u = t.match(/^.*(?:\bcase\b.*\:|[\{\(\[])\s*$/);
                u && (r += n)
            } else if (e == "doc-start") {
                if (o == "start" || o == "no_regex")return"";
                var u = t.match(/^\s*(\/?)\*/);
                u && (u[1] && (r += " "), r += "* ")
            }
            return r
        }, this.checkOutdent = function (e, t, n) {
            return this.$outdent.checkOutdent(t, n)
        }, this.autoOutdent = function (e, t, n) {
            this.$outdent.autoOutdent(t, n)
        }, this.createWorker = function (e) {
            var t = new f(["ace"], "ace/mode/javascript_worker", "JavaScriptWorker");
            return t.attachToDocument(e.getDocument()), t.on("jslint", function (t) {
                e.setAnnotations(t.data)
            }), t.on("terminate", function () {
                e.clearAnnotations()
            }), t
        }, this.$id = "ace/mode/javascript"
    }.call(h.prototype), t.Mode = h
}), ace.define("ace/mode/r_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/lib/lang", "ace/mode/text_highlight_rules", "ace/mode/tex_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("../lib/lang"), s = e("./text_highlight_rules").TextHighlightRules, o = e("./tex_highlight_rules").TexHighlightRules, u = function () {
        var e = i.arrayToMap("function|if|in|break|next|repeat|else|for|return|switch|while|try|tryCatch|stop|warning|require|library|attach|detach|source|setMethod|setGeneric|setGroupGeneric|setClass".split("|")), t = i.arrayToMap("NULL|NA|TRUE|FALSE|T|F|Inf|NaN|NA_integer_|NA_real_|NA_character_|NA_complex_".split("|"));
        this.$rules = {start: [
            {token: "comment.sectionhead", regex: "#+(?!').*(?:----|====|####)\\s*$"},
            {token: "comment", regex: "#+'", next: "rd-start"},
            {token: "comment", regex: "#.*$"},
            {token: "string", regex: '["]', next: "qqstring"},
            {token: "string", regex: "[']", next: "qstring"},
            {token: "constant.numeric", regex: "0[xX][0-9a-fA-F]+[Li]?\\b"},
            {token: "constant.numeric", regex: "\\d+L\\b"},
            {token: "constant.numeric", regex: "\\d+(?:\\.\\d*)?(?:[eE][+\\-]?\\d*)?i?\\b"},
            {token: "constant.numeric", regex: "\\.\\d+(?:[eE][+\\-]?\\d*)?i?\\b"},
            {token: "constant.language.boolean", regex: "(?:TRUE|FALSE|T|F)\\b"},
            {token: "identifier", regex: "`.*?`"},
            {onMatch: function (n) {
                return e[n] ? "keyword" : t[n] ? "constant.language" : n == "..." || n.match(/^\.\.\d+$/) ? "variable.language" : "identifier"
            }, regex: "[a-zA-Z.][a-zA-Z0-9._]*\\b"},
            {token: "keyword.operator", regex: "%%|>=|<=|==|!=|\\->|<\\-|\\|\\||&&|=|\\+|\\-|\\*|/|\\^|>|<|!|&|\\||~|\\$|:"},
            {token: "keyword.operator", regex: "%.*?%"},
            {token: "paren.keyword.operator", regex: "[[({]"},
            {token: "paren.keyword.operator", regex: "[\\])}]"},
            {token: "text", regex: "\\s+"}
        ], qqstring: [
            {token: "string", regex: '(?:(?:\\\\.)|(?:[^"\\\\]))*?"', next: "start"},
            {token: "string", regex: ".+"}
        ], qstring: [
            {token: "string", regex: "(?:(?:\\\\.)|(?:[^'\\\\]))*?'", next: "start"},
            {token: "string", regex: ".+"}
        ]};
        var n = (new o("comment")).getRules();
        for (var r = 0; r < n.start.length; r++)n.start[r].token += ".virtual-comment";
        this.addRules(n, "rd-"), this.$rules["rd-start"].unshift({token: "text", regex: "^", next: "start"}), this.$rules["rd-start"].unshift({token: "keyword", regex: "@(?!@)[^ ]*"}), this.$rules["rd-start"].unshift({token: "comment", regex: "@@"}), this.$rules["rd-start"].push({token: "comment", regex: "[^%\\\\[({\\])}]+"})
    };
    r.inherits(u, s), t.RHighlightRules = u
}), ace.define("ace/mode/html", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/mode/javascript", "ace/mode/css", "ace/tokenizer", "ace/mode/html_highlight_rules", "ace/mode/behaviour/html", "ace/mode/folding/html", "ace/mode/html_completions", "ace/worker/worker_client"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./text").Mode, s = e("./javascript").Mode, o = e("./css").Mode, u = e("../tokenizer").Tokenizer, a = e("./html_highlight_rules").HtmlHighlightRules, f = e("./behaviour/html").HtmlBehaviour, l = e("./folding/html").FoldMode, c = e("./html_completions").HtmlCompletions, h = e("../worker/worker_client").WorkerClient, p = function (e) {
        this.fragmentContext = e && e.fragmentContext, this.HighlightRules = a, this.$behaviour = new f, this.$completer = new c, this.createModeDelegates({"js-": s, "css-": o}), this.foldingRules = new l
    };
    r.inherits(p, i), function () {
        this.blockComment = {start: "<!--", end: "-->"}, this.getNextLineIndent = function (e, t, n) {
            return this.$getIndent(t)
        }, this.checkOutdent = function (e, t, n) {
            return!1
        }, this.getCompletions = function (e, t, n, r) {
            return this.$completer.getCompletions(e, t, n, r)
        }, this.createWorker = function (e) {
            if (this.constructor != p)return;
            var t = new h(["ace"], "ace/mode/html_worker", "Worker");
            return t.attachToDocument(e.getDocument()), this.fragmentContext && t.call("setOptions", [
                {context: this.fragmentContext}
            ]), t.on("error", function (t) {
                e.setAnnotations(t.data)
            }), t.on("terminate", function () {
                e.clearAnnotations()
            }), t
        }, this.$id = "ace/mode/html"
    }.call(p.prototype), t.Mode = p
}), ace.define("ace/mode/tex_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/lib/lang", "ace/mode/text_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("../lib/lang"), s = e("./text_highlight_rules").TextHighlightRules, o = function (e) {
        e || (e = "text"), this.$rules = {start: [
            {token: "comment", regex: "%.*$"},
            {token: e, regex: "\\\\[$&%#\\{\\}]"},
            {token: "keyword", regex: "\\\\(?:documentclass|usepackage|newcounter|setcounter|addtocounter|value|arabic|stepcounter|newenvironment|renewenvironment|ref|vref|eqref|pageref|label|cite[a-zA-Z]*|tag|begin|end|bibitem)\\b", next: "nospell"},
            {token: "keyword", regex: "\\\\(?:[a-zA-z0-9]+|[^a-zA-z0-9])"},
            {token: "paren.keyword.operator", regex: "[[({]"},
            {token: "paren.keyword.operator", regex: "[\\])}]"},
            {token: e, regex: "\\s+"}
        ], nospell: [
            {token: "comment", regex: "%.*$", next: "start"},
            {token: "nospell." + e, regex: "\\\\[$&%#\\{\\}]"},
            {token: "keyword", regex: "\\\\(?:documentclass|usepackage|newcounter|setcounter|addtocounter|value|arabic|stepcounter|newenvironment|renewenvironment|ref|vref|eqref|pageref|label|cite[a-zA-Z]*|tag|begin|end|bibitem)\\b"},
            {token: "keyword", regex: "\\\\(?:[a-zA-z0-9]+|[^a-zA-z0-9])", next: "start"},
            {token: "paren.keyword.operator", regex: "[[({]"},
            {token: "paren.keyword.operator", regex: "[\\])]"},
            {token: "paren.keyword.operator", regex: "}", next: "start"},
            {token: "nospell." + e, regex: "\\s+"},
            {token: "nospell." + e, regex: "\\w+"}
        ]}
    };
    r.inherits(o, s), t.TexHighlightRules = o
}), ace.define("ace/mode/html_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/lib/lang", "ace/mode/css_highlight_rules", "ace/mode/javascript_highlight_rules", "ace/mode/xml_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("../lib/lang"), s = e("./css_highlight_rules").CssHighlightRules, o = e("./javascript_highlight_rules").JavaScriptHighlightRules, u = e("./xml_highlight_rules").XmlHighlightRules, a = i.createMap({a: "anchor", button: "form", form: "form", img: "image", input: "form", label: "form", option: "form", script: "script", select: "form", textarea: "form", style: "style", table: "table", tbody: "table", td: "table", tfoot: "table", th: "table", tr: "table"}), f = function () {
        u.call(this), this.addRules({attributes: [
            {include: "space"},
            {token: "entity.other.attribute-name", regex: "[-_a-zA-Z0-9:]+"},
            {token: "keyword.operator.separator", regex: "=", push: [
                {include: "space"},
                {token: "string", regex: "[^<>='\"`\\s]+", next: "pop"},
                {token: "empty", regex: "", next: "pop"}
            ]},
            {include: "string"}
        ], tag: [
            {token: function (e, t) {
                var n = a[t];
                return["meta.tag.punctuation.begin", "meta.tag.name" + (n ? "." + n : "")]
            }, regex: "(<)([-_a-zA-Z0-9:]+)", next: "start_tag_stuff"},
            {token: function (e, t) {
                var n = a[t];
                return["meta.tag.punctuation.begin", "meta.tag.name" + (n ? "." + n : "")]
            }, regex: "(</)([-_a-zA-Z0-9:]+)", next: "end_tag_stuff"}
        ], start_tag_stuff: [
            {include: "attributes"},
            {token: "meta.tag.punctuation.end", regex: "/?>", next: "start"}
        ], end_tag_stuff: [
            {include: "space"},
            {token: "meta.tag.punctuation.end", regex: ">", next: "start"}
        ]}), this.embedTagRules(s, "css-", "style"), this.embedTagRules(o, "js-", "script"), this.constructor === f && this.normalizeRules()
    };
    r.inherits(f, u), t.HtmlHighlightRules = f
})