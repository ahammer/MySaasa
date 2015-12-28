ace.define("ace/mode/powershell", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/tokenizer", "ace/mode/powershell_highlight_rules", "ace/mode/matching_brace_outdent", "ace/mode/behaviour/cstyle", "ace/mode/folding/cstyle"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./text").Mode, s = e("../tokenizer").Tokenizer, o = e("./powershell_highlight_rules").PowershellHighlightRules, u = e("./matching_brace_outdent").MatchingBraceOutdent, a = e("./behaviour/cstyle").CstyleBehaviour, f = e("./folding/cstyle").FoldMode, l = function () {
        this.HighlightRules = o, this.$outdent = new u, this.$behaviour = new a, this.foldingRules = new f({start: "^\\s*(<#)", end: "^[#\\s]>\\s*$"})
    };
    r.inherits(l, i), function () {
        this.lineCommentStart = "#", this.blockComment = {start: "<#", end: "#>"}, this.getNextLineIndent = function (e, t, n) {
            var r = this.$getIndent(t), i = this.getTokenizer().getLineTokens(t, e), s = i.tokens;
            if (s.length && s[s.length - 1].type == "comment")return r;
            if (e == "start") {
                var o = t.match(/^.*[\{\(\[]\s*$/);
                o && (r += n)
            }
            return r
        }, this.checkOutdent = function (e, t, n) {
            return this.$outdent.checkOutdent(t, n)
        }, this.autoOutdent = function (e, t, n) {
            this.$outdent.autoOutdent(t, n)
        }, this.createWorker = function (e) {
            return null
        }, this.$id = "ace/mode/powershell"
    }.call(l.prototype), t.Mode = l
}), ace.define("ace/mode/powershell_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text_highlight_rules"], function (e, t, n) {
    var r = e("../lib/oop"), i = e("./text_highlight_rules").TextHighlightRules, s = function () {
        var e = "function|if|else|elseif|switch|while|default|for|do|until|break|continue|foreach|return|filter|in|trap|throw|param|begin|getEditPanel|end", t = "Get-Alias|Import-Alias|New-Alias|Set-Alias|Get-AuthenticodeSignature|Set-AuthenticodeSignature|Set-Location|Get-ChildItem|Clear-Item|Get-Command|Measure-Command|Trace-Command|Add-Computer|Checkpoint-Computer|Remove-Computer|Restart-Computer|Restore-Computer|Stop-Computer|Reset-ComputerMachinePassword|Test-ComputerSecureChannel|Add-Content|Get-Content|Set-Content|Clear-Content|Get-Command|Invoke-Command|Enable-ComputerRestore|Disable-ComputerRestore|Get-ComputerRestorePoint|Test-Connection|ConvertFrom-CSV|ConvertTo-CSV|ConvertTo-Html|ConvertTo-Xml|ConvertFrom-SecureString|ConvertTo-SecureString|Copy-Item|Export-Counter|Get-Counter|Import-Counter|Get-Credential|Get-Culture|Get-ChildItem|Get-Date|Set-Date|Remove-Item|Compare-Object|Get-Event|Get-WinEvent|New-Event|Remove-Event|Unregister-Event|Wait-Event|Clear-EventLog|Get-Eventlog|Limit-EventLog|New-Eventlog|Remove-EventLog|Show-EventLog|Write-EventLog|Get-EventSubscriber|Register-EngineEvent|Register-ObjectEvent|Register-WmiEvent|Get-ExecutionPolicy|Set-ExecutionPolicy|Export-Alias|Export-Clixml|Export-Console|Export-Csv|ForEach-Object|Format-Custom|Format-List|Format-Table|Format-Wide|Export-FormatData|Get-FormatData|Get-Item|Get-ChildItem|Get-Help|Add-History|Clear-History|Get-History|Invoke-History|Get-Host|Read-Host|Write-Host|Get-HotFix|Import-Clixml|Import-Csv|Invoke-Command|Invoke-Expression|Get-Item|Invoke-Item|New-Item|Remove-Item|Set-Item|Clear-ItemProperty|Copy-ItemProperty|Get-ItemProperty|Move-ItemProperty|New-ItemProperty|Remove-ItemProperty|Rename-ItemProperty|Set-ItemProperty|Get-Job|Receive-Job|Remove-Job|Start-Job|Stop-Job|Wait-Job|Stop-Process|Update-List|Get-Location|Pop-Location|Push-Location|Set-Location|Send-MailMessage|Add-Member|Get-Member|Move-Item|Compare-Object|Group-Object|Measure-Object|New-Object|Select-Object|Sort-Object|Where-Object|Out-Default|Out-File|Out-GridView|Out-Host|Out-Null|Out-Printer|Out-String|Convert-Path|Join-Path|Resolve-Path|Split-Path|Test-Path|Get-Pfxcertificate|Pop-Location|Push-Location|Get-Process|Start-Process|Stop-Process|Wait-Process|Enable-PSBreakpoint|Disable-PSBreakpoint|Get-PSBreakpoint|Set-PSBreakpoint|Remove-PSBreakpoint|Get-PSDrive|New-PSDrive|Remove-PSDrive|Get-PSProvider|Set-PSdebug|Enter-PSSession|Exit-PSSession|Export-PSSession|Get-PSSession|Import-PSSession|New-PSSession|Remove-PSSession|Disable-PSSessionConfiguration|Enable-PSSessionConfiguration|Get-PSSessionConfiguration|Register-PSSessionConfiguration|Set-PSSessionConfiguration|Unregister-PSSessionConfiguration|New-PSSessionOption|Add-PsSnapIn|Get-PsSnapin|Remove-PSSnapin|Get-Random|Read-Host|Remove-Item|Rename-Item|Rename-ItemProperty|Select-Object|Select-XML|Send-MailMessage|Get-Service|New-Service|Restart-Service|Resume-Service|Set-Service|Start-Service|Stop-Service|Suspend-Service|Sort-Object|Start-Sleep|ConvertFrom-StringData|Select-String|Tee-Object|New-Timespan|Trace-Command|Get-Tracesource|Set-Tracesource|Start-Transaction|Complete-Transaction|Get-Transaction|Use-Transaction|Undo-Transaction|Start-Transcript|Stop-Transcript|Add-Type|Update-TypeData|Get-Uiculture|Get-Unique|Update-Formatdata|Update-Typedata|Clear-Variable|Get-Variable|New-Variable|Remove-Variable|Set-Variable|New-WebServiceProxy|Where-Object|Write-Debug|Write-Error|Write-Host|Write-Output|Write-Progress|Write-Verbose|Write-Warning|Set-WmiInstance|Invoke-WmiMethod|Get-WmiObject|Remove-WmiObject|Connect-WSMan|Disconnect-WSMan|Test-WSMan|Invoke-WSManAction|Disable-WSManCredSSP|Enable-WSManCredSSP|Get-WSManCredSSP|New-WSManInstance|Get-WSManInstance|Set-WSManInstance|Remove-WSManInstance|Set-WSManQuickConfig|New-WSManSessionOption", n = this.createKeywordMapper({"support.function": t, keyword: e}, "identifier"), r = "eq|ne|ge|gt|lt|le|like|notlike|match|notmatch|replace|contains|notcontains|ieq|ine|ige|igt|ile|ilt|ilike|inotlike|imatch|inotmatch|ireplace|icontains|inotcontains|is|isnot|as|and|or|band|bor|not";
        this.$rules = {start: [
            {token: "comment", regex: "#.*$"},
            {token: "comment.start", regex: "<#", next: "comment"},
            {token: "string", regex: '["](?:(?:\\\\.)|(?:[^"\\\\]))*?["]'},
            {token: "string", regex: "['](?:(?:\\\\.)|(?:[^'\\\\]))*?[']"},
            {token: "constant.numeric", regex: "0[xX][0-9a-fA-F]+\\b"},
            {token: "constant.numeric", regex: "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"},
            {token: "constant.language.boolean", regex: "[$](?:[Tt]rue|[Ff]alse)\\b"},
            {token: "constant.language", regex: "[$][Nn]ull\\b"},
            {token: "variable.instance", regex: "[$][a-zA-Z][a-zA-Z0-9_]*\\b"},
            {token: n, regex: "[a-zA-Z_$][a-zA-Z0-9_$\\-]*\\b"},
            {token: "keyword.operator", regex: "\\-(?:" + r + ")"},
            {token: "keyword.operator", regex: "&|\\*|\\+|\\-|\\=|\\+=|\\-="},
            {token: "lparen", regex: "[[({]"},
            {token: "rparen", regex: "[\\])}]"},
            {token: "text", regex: "\\s+"}
        ], comment: [
            {token: "comment.end", regex: "#>", next: "start"},
            {token: "doc.comment.tag", regex: "^\\.\\w+"},
            {token: "comment", regex: "\\w+"},
            {token: "comment", regex: "."}
        ]}
    };
    r.inherits(s, i), t.PowershellHighlightRules = s
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
})