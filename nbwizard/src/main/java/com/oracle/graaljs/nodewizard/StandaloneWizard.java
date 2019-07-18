/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or data
 * (collectively the "Software"), free of charge and under any and all copyright
 * rights in the Software, and any and all patent rights owned or freely
 * licensable by each licensor hereunder covering either (i) the unmodified
 * Software as contributed to or provided by such licensor, or (ii) the Larger
 * Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.graaljs.nodewizard;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Exchanger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.java.html.BrwsrCtx;
import net.java.html.js.JavaScriptBody;
import org.openide.WizardDescriptor;

final class StandaloneWizard implements ChangeListener {
    private final WizardDescriptor.InstantiatingIterator<WizardDescriptor> it;
    private final Exchanger<Set<?>> finished = new Exchanger<>();
    private final BrwsrCtx ctx;

    StandaloneWizard(WizardDescriptor.InstantiatingIterator<WizardDescriptor> it) {
        this.it = it;
        wizardButtons(this);
        this.ctx = BrwsrCtx.findDefault(getClass());
        it.addChangeListener(this);
        enableButtons();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ctx.execute(this::enableButtons);
    }

    private void enableButtons() {
        disableButton("wizard-next", !it.hasNext() || !it.current().isValid());
        disableButton("wizard-prev", !it.hasPrevious());
        final boolean canFinish = !it.hasNext() || (
                it.current() instanceof WizardDescriptor.FinishablePanel &&
                ((WizardDescriptor.FinishablePanel<?>)it.current()).isFinishPanel()
                );
        disableButton("wizard-finish", !canFinish);
    }

    final void onClick(String id) throws Exception {
        enableButtons();
        it.current().removeChangeListener(this);
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
        it.current().addChangeListener(this);
        enableButtons();
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

    @JavaScriptBody(args = { "id", "disable" }, wait4js = false, body = "\n"
            + "var e = document.getElementById(id);\n"
            + "e.disabled = disable;\n"
    )
    private static native void disableButton(String id, boolean disable);

    @JavaScriptBody(args = {  }, body = "\n"
            + "window.close();\n"
            + "window.body.innerHTML = 'Closed';\n")
    private static native void windowClose();

    Set<?> waitFinished() throws InterruptedException {
        return finished.exchange(Collections.emptySet());
    }

}
