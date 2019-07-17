/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.graaljs.nodewizard;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Exchanger;
import net.java.html.js.JavaScriptBody;
import org.openide.WizardDescriptor;

final class StandaloneWizard {
    private final WizardDescriptor.InstantiatingIterator<WizardDescriptor> it;
    private final Exchanger<Set<?>> finished = new Exchanger<>();
    
    StandaloneWizard(WizardDescriptor.InstantiatingIterator<WizardDescriptor> it) {
        this.it = it;
        wizardButtons(this);
    }
    
    final void onClick(String id) throws Exception {
        switch (id) {
            case "wizard-next":
//                if (it.current().isValid()) {
                if (it.hasNext()) {
                    it.nextPanel();
                }
//                }
                break;
            case "wizard-prev":
                if (it.hasPrevious()) {
                    it.previousPanel();
                }
                break;
            case "wizard-finish":
                if (it.current().isValid()) {
                    Set<?> res = Collections.emptySet();
                    try {
                        res = it.instantiate();
                    } finally {
                        finished.exchange(res);
                    }
                }
                break;
            case "wizard-cancel":
                finished.exchange(Collections.emptySet());
                windowClose();
                break;
        }
    }
    
    
    @JavaScriptBody(args = { "wizard" }, javacall = true, body = 
        "\n"
        + "var e = document.getElementById('wizard');\n"
        + "if (!e) {\n"
        + "  e = document.createElement('div');\n"
        + "  var text = '<button id=\"wizard-prev\">Previous</button>';\n"
        + "  text += '<button id=\"wizard-next\">Next</button>';\n"
        + "  text += '<button id=\"wizard-finish\">Finish</button>';\n"
        + "  text += '<button id=\"wizard-cancel\">Cancel</button>';\n"
        + "  e.innerHTML = text;\n"
        + "  document.body.appendChild(e);\n"
        + "  function listen(id) {\n"
        + "    var b = document.getElementById(id);\n"
        + "    b.onclick = function() {;\n"
        + "       wizard.@com.oracle.graaljs.nodewizard.StandaloneWizard::onClick(Ljava/lang/String;)(id);\n"
        + "    };\n"
        + "  }\n"
        + "  listen(\"wizard-prev\");\n"
        + "  listen(\"wizard-next\");\n"
        + "  listen(\"wizard-finish\");\n"
        + "  listen(\"wizard-cancel\");\n"
        + "}\n"
    )
    private static native void wizardButtons(StandaloneWizard wizard);
    
    @JavaScriptBody(args = {  }, body = "\n"
            + "window.close();\n"
            + "window.body.innerHTML = 'Closed';\n")
    private static native void windowClose();

    Set<?> waitFinished() throws InterruptedException {
        return finished.exchange(Collections.emptySet());
    }
    
}
