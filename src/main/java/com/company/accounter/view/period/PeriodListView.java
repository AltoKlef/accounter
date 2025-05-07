package com.company.accounter.view.period;

import com.company.accounter.entity.Period;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "periods", layout = MainView.class)
@ViewController(id = "Period_.list")
@ViewDescriptor(path = "period-list-view.xml")
@LookupComponent("periodsDataGrid")
@DialogMode(width = "64em")
public class PeriodListView extends StandardListView<Period> {

}