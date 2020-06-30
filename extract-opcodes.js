/** Extract op codes for twg2.jbcm.Opcodes from oracle's JVM spec html page.
 * run the following javascript code in the console tools of the page to extract opcodes.
 * https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html
 * @author TeamworkGuy2
 * @since 2020-06-11
 */

function texts(root, idx, selector, num) {
    var elems = root.querySelectorAll(selector);
    if(num > 0 && num != elems.length) {
        throw new Error("expected " + num + " but found " + elems.length + " elements: " + selector + " (at " + idx + ")");
    }
    var res = Array.prototype.reduce.call(elems, function (ary, elem) {
        ary.push(elem.textContent.trim());
        return ary;
    }, []);
    return res;
}
var baseUrl = "https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html";
var sections = Array.prototype.slice.call(document.querySelectorAll(".section-execution"))

res = [];
sections.forEach((el, idx) => {
    var operation = texts(el, idx, ".section[title='Operation'] p.norm", 1)[0].split("\n").join(" ").replace(/\s+/g, " ").trim();
    var formats = texts(el, idx, ".section .literallayout", -1).map(s => s.split("\n").map(t => t.trim()));
    var operandStack = texts(el, idx, ".section[title='Operand Stack'] p.norm", -1).join("\n").split(/[→\n]/).map(s => s.trim())
        .filter(s => s.length > 0 && (s.startsWith("...") || s.startsWith("[") || s.startsWith("Form ") || s.startsWith("Same as ")/*wide*/ || s === "No change"/*goto, goto_w, iinc, nop, ret, breakpoint*/ || s.indexOf(" ") === -1/*athrow*/));
    var description = texts(el, idx, ".norm-dynamic", -1).join(" ").replace(/\s+/g, " ").trim();
    var forms = texts(el, idx, ".section[title='Forms'] p.norm", -1).filter(s => s.length > 0);
    var hashLink = el.querySelector(".titlepage h3.title a").getAttribute("name");
    for(var j = 0; j < forms.length; j++) {
        var opCode = parseInt(forms[j].split(" = ")[1].split(" (")[0]);
        var name = forms[j].split(" = ")[0];
        res.push({ name: name, opCode: opCode, operation: operation, formats: formats, forms: forms, operandStack: operandStack, description: description, hashLink: hashLink });
    }
});
res.push({ name: "breakpoint", opCode: 202, operation: "reserved for debuggers", formats: [["breakpoint"]], forms: ["breakpoint"], operandStack: ["No change"], description: "Intended to be used by debuggers to implement breakpoints" });
res.push({ name: "impdep1", opCode: 254, operation: "reserved opcode", formats: [["impdep1"]], forms: ["impdep1"], operandStack: [""], description: "These instructions are intended to provide \"back doors\" or traps to implementation-specific functionality implemented in software and hardware, respectively" });
res.push({ name: "impdep2", opCode: 255, operation: "reserved opcode", formats: [["impdep2"]], forms: ["impdep2"], operandStack: [""], description: "These instructions are intended to provide \"back doors\" or traps to implementation-specific functionality implemented in software and hardware, respectively" });
res = res.sort((a,b) => a.opCode - b.opCode).filter(s => !isNaN(s.opCode)/*mnemonic example at top of page*/);
res.push({ name: "undefined", opCode: -1, operation: "", formats: [[]], forms: [], operandStack: [""], description: "" });

res.map((rr, idx, ary) => {
    var opStackOffset = rr.operandStack[0].startsWith("Form ") ? 1 : 0;
    var stackPopCount = rr.operandStack[opStackOffset].split(", ").length - 1;
    var stackPushCount = rr.operandStack.length > opStackOffset + 1 ? rr.operandStack[opStackOffset + 1].split(", ").length - 1 : 0;
    var operandCount = rr.description.indexOf("variable-length instruction") > -1 || rr.name === "wide" ? "Const.UNPREDICTABLE" : (rr.operation.startsWith("reserved") ? "Const.RESERVED" : rr.formats[0].length - 1);
    var isCondition = rr.name.startsWith("if");
    var isJump = rr.operation.startsWith("Branch ") || rr.name === "jsr" || rr.name === "jsr_w";
    var isCpIndex = rr.description.indexOf("index into the run-time constant pool of the current class") > -1;
    var isStackManipulate = rr.name.startsWith("dup") || rr.name === "swap";
    var isVariableStackPop = rr.operandStack[opStackOffset].indexOf("[arg") > -1;
    var types;
    var opUtils;
    return "\t/* " + String(rr.opCode).padStart(2, ' ') + " " + ("0x" + rr.opCode.toString(16).toUpperCase()).padStart(4, ' ') + " */" +
        rr.name.toUpperCase().padEnd(16, ' ') +
        "(" + rr.opCode + ", " + operandCount + ", " +
        ((types = [
            (isStackManipulate ? "Type.STACK_MANIPULATE" : null),
            (isVariableStackPop ? "Type.POP_UNPREDICTABLE" : null),
            (stackPopCount > 0 && !isStackManipulate && !isVariableStackPop ? "Type.POP" + stackPopCount : null),
            (stackPushCount > 0 && !isStackManipulate ? "Type.PUSH" + stackPushCount : null),
            (rr.name.indexOf("load") === 1 ? "Type.VAR_LOAD" : null),
            (rr.name.indexOf("store") === 1 ? "Type.VAR_STORE" : null),
            (rr.name.indexOf("aload") === 1 ? "Type.ARRAY_LOAD" : null),
            (rr.name.indexOf("astore") === 1 ? "Type.ARRAY_STORE" : null),
            (rr.name.indexOf("return") > -1 ? "Type.RETURN" : null),
            (isCondition ? "Type.CONDITION" : null),
            (isJump ? "Type.JUMP" : null),
            (isCpIndex ? "Type.CP_INDEX" : null)
        ].filter(s => s != null)).length > 0 ? "enums(" + types.join(", ") + ")" : "none(Type.class)") +
        ", " +
        ((opUtils = [
            (isCondition || isJump ? "IoUtility.offsetModifier(1, " + (rr.name.endsWith("_w") ? 4 : 2) + ")" : null),
            (isCpIndex ? "IoUtility.cpIndex(1, " + (rr.name.endsWith("_w") ? 4 : 2) + ")" : null),
            (rr.name === "tableswitch" ? "IoUtility.TableswitchOffsetModifier" : null),
            (rr.name === "lookupswitch" ? "IoUtility.LookupswitchOffsetModifier" : null)
        ].filter(s => s != null)).length > 0 ? "Op.of(" + opUtils.join(", ") + ")" : "null") +
        ")" + (idx < ary.length - 1 ? "," : ";") +
        " // " + rr.operation + "," + (!isStackManipulate ? " stack: " + JSON.stringify(rr.operandStack, undefined, " ").split("\n").map(s => s.trim()).join(" ") + "," : "") + (rr.hashLink != null ? " link: " + baseUrl + "#" + rr.hashLink : "");
}).join("\n")
