package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Component for holding the DPU's configuration dialog. The component show message if the configuration
 * component is null.
 *
 * @author Škoda Petr
 */
public class DPUConfigHolder extends CustomComponent {

    /**
     * Layout for no-configuration dialog.
     */
    private VerticalLayout noConfigLayout;

    public DPUConfigHolder() {
        buildLayout();
    }

    private void buildLayout() {
        setSizeFull();
        // Build layout for no configuration.
        final Label infoLabel = new Label();
        infoLabel.setSizeUndefined();
        infoLabel.setValue("This DPU doesn't provide configuration component.");
        noConfigLayout = new VerticalLayout();
        noConfigLayout.setSizeFull();
        noConfigLayout.addComponent(infoLabel);
        noConfigLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);
    }

    /**
     * Set the component representing the dialog.
     *
     * @param confDialog
     *            Can be null.
     */
    public void setConfigComponent(Component confDialog) {
        if (confDialog == null) {
            setCompositionRoot(noConfigLayout);
        } else {
            setCompositionRoot(confDialog);
        }
    }

}
