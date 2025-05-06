package com.company.accounter.view.needs;

import com.company.accounter.entity.Needs;
import com.company.accounter.entity.RecordType;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.model.DataLoader;
import io.jmix.flowui.view.*;


@Route(value = "needses", layout = MainView.class)
@ViewController(id = "Needs.list")
@ViewDescriptor(path = "needs-list-view.xml")
@LookupComponent("needsesDataGrid")
@DialogMode(width = "64em")
public class NeedsListView extends StandardListView<Needs> {
    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        System.out.println("BeforeShow triggered");
        DataLoader needsDl = getViewData().getLoader("needsesDl");
        needsDl.setParameter("recordType", RecordType.USER.getId()); // т.е. "A"

        // Логируем параметры и результат
        System.out.println(">>> Параметры загрузчика: " + needsDl.getParameters());

        needsDl.load();
    }
}