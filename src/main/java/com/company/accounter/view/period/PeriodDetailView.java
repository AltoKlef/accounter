package com.company.accounter.view.period;

import com.company.accounter.entity.Period;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "periods/:id", layout = MainView.class)
@ViewController(id = "Period_.detail")
@ViewDescriptor(path = "period-detail-view.xml")
@EditedEntityContainer("periodDc")
public class PeriodDetailView extends StandardDetailView<Period> {

}