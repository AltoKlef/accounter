package com.company.accounter.view.needtype;

import com.company.accounter.entity.NeedType;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "need-types", layout = MainView.class)
@ViewController(id = "NeedType.list")
@ViewDescriptor(path = "need-type-list-view.xml")
@LookupComponent("needTypesDataGrid")
@DialogMode(width = "64em")
public class NeedTypeListView extends StandardListView<NeedType> {
}