package org.netbeans.api.htmlui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;

public @interface HTMLDialog {
    public static final class Builder {
        private String url;
        private final List<String> ids;
        private Runnable onPageLoad;
        
        private Builder(String u) {
            url = u;
            ids = new ArrayList<>();
        }

        /**
         * Starts creation of a new HTML dialog. The page can contain hidden
         * buttons as described at {@link HTMLDialog}.
         *
         * @param url URL (usually using <code>nbresloc</code> protocol) of the
         * page to display in the dialog.
         * @return instance of the builder
         */
        public static Builder newDialog(String url) {
            return new Builder(url);
        }

        /**
         * Registers a runnable to be executed when the page becomes ready.
         *
         * @param run runnable to run
         * @return this builder
         */
        public Builder loadFinished(Runnable run) {
            this.onPageLoad = run;
            return this;
        }

        /**
         * Requests some of provided technologies. The HTML/Java API @ version
         * 1.1 supports {@link Id technology ids}. One can specify the preferred
         * ones to use in this NetBeans component by using calling this method.
         *
         * @param ids list of preferred technology ids to add to the builder
         * @return instance of the builder
         * @since 1.3
         */
        public Builder addTechIds(String... ids) {
            this.ids.addAll(Arrays.asList(ids));
            return this;
        }

        /**
         * Displays the dialog. This method blocks waiting for the dialog to be
         * shown and closed by the user.
         *
         * @return 'id' of a selected button element or <code>null</code> if the
         * dialog was closed without selecting a button
         */
        public String showAndWait() {
            if (true) {
                throw new UnsupportedOperationException();
            }
            return null;
        }

        /**
         * Obtains the component from the builder. The parameter can either be
         * {@link JFXPanel}.<b>class</b> or {@link WebView}.<b>class</b>. After
         * calling this method the builder becomes useless.
         *
         * @param <C> requested component type
         * @param type either {@link JFXPanel} or {@link WebView} class
         * @return instance of the requested component
         */
        public <C> C component(Class<C> type) {
            return type.cast(new JPanel());
        }
    }
    
}
