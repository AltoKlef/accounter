package com.company.accounter.view.needs;

import com.company.accounter.entity.Needs;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "needses", layout = MainView.class)
@ViewController(id = "Needs.list")
@ViewDescriptor(path = "needs-list-view.xml")
@LookupComponent("needsesDataGrid")
@DialogMode(width = "64em")
public class NeedsListView extends StandardListView<Needs> {

}