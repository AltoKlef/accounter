package com.company.accounter.view.needs;

import com.company.accounter.entity.Needs;
import com.company.accounter.entity.Period;
import com.company.accounter.entity.User;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "needses/:id", layout = MainView.class)
@ViewController(id = "Needs.detail")
@ViewDescriptor(path = "needs-detail-view.xml")
@EditedEntityContainer("needsDc")
public class NeedsDetailView extends StandardDetailView<Needs> {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private EntityPicker<User> recipientUserField;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Needs> event) {
        Needs needs = event.getEntity();
        User currentUser = (User) currentAuthentication.getUser();
        recipientUserField.setValue(currentUser);
        needs.setRecipientUser(currentUser);
        Optional<Period> latestOpenPeriod = dataManager.load(Period.class)
                .query("select p from Period_ p where p.opened = true order by p.id desc")
                .maxResults(1)
                .optional();

        latestOpenPeriod.ifPresent(needs::setPeriod);
    }
}