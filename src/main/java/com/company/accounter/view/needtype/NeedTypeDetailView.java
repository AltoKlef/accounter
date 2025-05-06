package com.company.accounter.view.needtype;

import com.company.accounter.entity.NeedType;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "need-types/:id", layout = MainView.class)
@ViewController(id = "NeedType.detail")
@ViewDescriptor(path = "need-type-detail-view.xml")
@EditedEntityContainer("needTypeDc")
public class NeedTypeDetailView extends StandardDetailView<NeedType> {
}