package com.oracle.graaljs.nodewizard;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import net.java.html.boot.BrowserBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.netbeans.api.htmlui.HTMLDialog;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;

public class PlainViewTest {
    @Test
    public void plainView() throws Exception {
        FileObject template = FileUtil.getConfigFile("Templates/Project/ClientSide/nodeJsJava.archetype");
        Assert.assertNotNull(template);
        WizardDescriptor.InstantiatingIterator<WizardDescriptor> it;
        it = (WizardDescriptor.InstantiatingIterator<WizardDescriptor>) template.getAttribute("instantiatingIterator");
        TemplateWizard wd = new TemplateWizard();
        wd.setTemplate(DataObject.find(template));
        FileObject userDir = FileUtil.toFileObject(new File(System.getProperty("user.dir")));
        wd.setTargetFolder(DataFolder.findFolder(userDir));
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
        for (Object obj : wiz[0].waitFinished()) {
            System.err.println("created: " + obj);
        }
    }
}
