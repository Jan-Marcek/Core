package cz.cuni.mff.xrg.odcs.frontend.gui;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.mff.xrg.odcs.db.updater.DBUpdater;

/**
 * Class represents main application component when
 * the DB needs to be initialized
 * 
 * @author mvi
 *
 */
public class DbUpdateLayout extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DbUpdateLayout.class);

    private static final float TEXT_FIELD_WIDTH = 300;
    
    private VerticalLayout mainLayout;
    
    private TextField admin = new TextField("Admin Username (to create Database)");
    
    private PasswordField adminPassword = new PasswordField("Admin Password (to create Database)");

    private Button startButton;

    public void build(DBUpdater updater) {
        setSizeFull();
        
        mainLayout = new VerticalLayout();
        mainLayout.setMargin(false);
        mainLayout.setSizeFull();
        
        FormLayout form = createFormLayout(updater);
        
        Label label = new Label("Database initialization");
        label.setWidth(100, Unit.PERCENTAGE);
        label.setHeight(37, Unit.PIXELS);
                
        mainLayout.addComponent(label);        
        mainLayout.addComponent(form);
        mainLayout.setExpandRatio(form, 1.0f);
        
        setCompositionRoot(mainLayout);
    }

    private FormLayout createFormLayout(final DBUpdater updater) {
        FormLayout form = new FormLayout();
        
        PropertysetItem item = initItem(updater);
        
        TextField dbType = new TextField("Type of Database");
        TextField connectionString = new TextField("Database connection string");
        TextField driver = new TextField("Driver class (to connect to Database)");
        TextField username = new TextField("Username (for Database)");
        
        dbType.setWidth(TEXT_FIELD_WIDTH, Unit.PIXELS);
        connectionString.setWidth(TEXT_FIELD_WIDTH, Unit.PIXELS);
        driver.setWidth(TEXT_FIELD_WIDTH, Unit.PIXELS);
        username.setWidth(TEXT_FIELD_WIDTH, Unit.PIXELS);
        admin.setWidth(TEXT_FIELD_WIDTH, Unit.PIXELS);
        adminPassword.setWidth(TEXT_FIELD_WIDTH, Unit.PIXELS);
        
        form.addComponent(dbType);
        form.addComponent(connectionString);
        form.addComponent(driver);
        form.addComponent(username);
        form.addComponent(admin);
        form.addComponent(adminPassword);
        
        final FieldGroup binder = new FieldGroup(item);
        binder.bind(dbType, "dbType");
        binder.bind(connectionString, "connectionString");
        binder.bind(driver, "driver");
        binder.bind(username, "username");
        binder.bind(admin, "adminUsername");
        binder.bind(adminPassword, "adminPassword");
        
        dbType.setEnabled(false);
        connectionString.setEnabled(false);
        driver.setEnabled(false);
        username.setEnabled(false);
        
        startButton = new Button("Init DB", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    binder.commit();
                    try {
                        startButton.setEnabled(false);
                        updater.createAndInitDB(admin.getValue(), adminPassword.getValue());
                        Notification.show("Refresh page to continue...");
                    } catch (FileNotFoundException | SQLException e) {
                        String msg = "Error while initializing DB:\n" + e.getMessage();
                        Notification.show(msg, Notification.Type.ERROR_MESSAGE);
                        LOG.error(msg);
                        startButton.setEnabled(true);
                    }
                    // TODO refresh
                } catch (CommitException e) {
                    Notification.show(e.getMessage());
                }
            }
        });
        
        form.addComponent(startButton);
        
        return form;
    }

    private PropertysetItem initItem(DBUpdater updater) {
        PropertysetItem item =  new PropertysetItem();
        item.addItemProperty("dbType", new ObjectProperty<String>(updater.getDbType()));
        item.addItemProperty("connectionString", new ObjectProperty<String>(updater.getConnectionString()));
        item.addItemProperty("driver", new ObjectProperty<String>(updater.getDriver()));
        item.addItemProperty("username", new ObjectProperty<String>(updater.getUsername()));
        item.addItemProperty("adminUsername", new ObjectProperty<String>(""));
        item.addItemProperty("adminPassword", new ObjectProperty<String>(""));
        return item;
    }
}
