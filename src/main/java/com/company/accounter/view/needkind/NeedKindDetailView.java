package com.company.accounter.view.needkind;

import com.company.accounter.entity.NeedKind;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "need-kinds/:id", layout = MainView.class)
@ViewController(id = "NeedKind.detail")
@ViewDescriptor(path = "need-kind-detail-view.xml")
@EditedEntityContainer("needKindDc")
public class NeedKindDetailView extends StandardDetailView<NeedKind> {
}