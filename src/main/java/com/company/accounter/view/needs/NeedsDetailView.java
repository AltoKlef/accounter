package com.company.accounter.view.needs;

import com.company.accounter.entity.Needs;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "needses/:id", layout = MainView.class)
@ViewController(id = "Needs.detail")
@ViewDescriptor(path = "needs-detail-view.xml")
@EditedEntityContainer("needsDc")
public class NeedsDetailView extends StandardDetailView<Needs> {
}