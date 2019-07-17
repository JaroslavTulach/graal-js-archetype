package com.oracle.graaljs.nodewizard;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
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
        CountDownLatch wizReady = new CountDownLatch(1);
        StandaloneWizard[] wiz = { null };
        System.setProperty("com.dukescript.presenters.browser", browser);
        BrowserBuilder.newBrowser(dlg.getIds().toArray()).
                loadPage(dlg.getUrl()).
                loadFinished(() -> {
                    dlg.getOnPageLoad().run();
                    wiz[0] = new StandaloneWizard(it);
                    wizReady.countDown();
                }).
                showAndWait();
        
        wizReady.await();
        wiz[0].waitFinished();
    }
}
