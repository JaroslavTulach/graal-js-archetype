package com.oracle.graaljs.nodewizard;

import java.awt.Desktop;
import java.net.URI;
import java.util.Enumeration;
import net.java.html.boot.BrowserBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.netbeans.api.htmlui.HTMLDialog;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.TemplateWizard;

public class PlainViewTest {
    @Test
    public void plainView() throws Exception {
        FileObject fo = FileUtil.getConfigFile("Templates/Project/ClientSide/nodeJsJava.archetype");
        Assert.assertNotNull(fo);
        Enumeration<String> en = fo.getAttributes();
        while (en.hasMoreElements()) {
            String n = en.nextElement();
            System.err.println("N: " + n + " R: " + fo.getAttribute("raw:" + n));
            try {
                System.err.println("N: " + n + " V: " + fo.getAttribute(n));
            } catch (Exception e) {
                System.err.println("N: " + n + " N/A: " + e.getMessage());
            }
        }
        WizardDescriptor.InstantiatingIterator<WizardDescriptor> it = (WizardDescriptor.InstantiatingIterator<WizardDescriptor>) fo.getAttribute("instantiatingIterator");
        TemplateWizard wd = new TemplateWizard();
        it.initialize(wd);
        HTMLDialog dlg = (HTMLDialog) it.current().getComponent();
        
        String browser = "AWT";
        try {
            Desktop.getDesktop().browse(new URI("http://graalvm.org"));
        } catch (UnsupportedOperationException e) {
            browser = "xdg-open";
        }
        System.setProperty("com.dukescript.presenters.browser", browser);
        BrowserBuilder.newBrowser(dlg.getIds().toArray()).
                loadPage(dlg.getUrl()).
                loadFinished(dlg.getOnPageLoad()).
                showAndWait();
        
        System.in.read();
    }
}
