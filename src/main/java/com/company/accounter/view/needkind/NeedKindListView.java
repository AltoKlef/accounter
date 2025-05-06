package com.company.accounter.view.needkind;

import com.company.accounter.entity.NeedKind;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "need-kinds", layout = MainView.class)
@ViewController(id = "NeedKind.list")
@ViewDescriptor(path = "need-kind-list-view.xml")
@LookupComponent("needKindsDataGrid")
@DialogMode(width = "64em")
public class NeedKindListView extends StandardListView<NeedKind> {
}