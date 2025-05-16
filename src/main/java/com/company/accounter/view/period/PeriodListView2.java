package com.company.accounter.view.period;

import com.company.accounter.app.NeedsSummaryService;
import com.company.accounter.entity.Period;
import com.company.accounter.entity.SummaryResult;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Route(value = "periods2", layout = MainView.class)
@ViewController(id = "Period_.2list")
@ViewDescriptor(path = "period-list-view2.xml")
@LookupComponent("periodsDataGrid")
@DialogMode(width = "64em")



public class PeriodListView2 extends StandardListView<Period> {
    @Subscribe(id = "periodsDc", target = Target.DATA_CONTAINER)
    public void onPeriodsDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<Period> event) {
        if ("opened".equals(event.getProperty())) {
            Boolean newValue = (Boolean) event.getValue();
            if (Boolean.FALSE.equals(newValue)) {
                notifications.create("Период закрывается!").show();
                SummaryResult result = needsSummaryService.generateSummaryForPeriod(event.getItem());
                notifications.create(result.toString()).show();
            }
        }
    }
    @ViewComponent("periodsDataGrid.closeAction")
    private BaseAction periodsDataGridCloseAction;
    @ViewComponent
    private DataGrid<Period> periodsDataGrid;
    @Autowired
    private Notifications notifications;
    @Autowired
    private NeedsSummaryService needsSummaryService;


    @Subscribe(target = Target.DATA_CONTEXT)
    public void onChange(final DataContext.ChangeEvent event) {

    }
    @Autowired
    private DataManager dataManager;
    @Autowired
    private ApplicationContext applicationContext;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        periodsDataGridCloseAction.withHandler(actionPerformedEvent -> {
            Period selectedPeriod = periodsDataGrid.getSingleSelectedItem();
            if (selectedPeriod == null) {
                notifications.create("Выберите период для закрытия").show();
                return;
            }

            if (!Boolean.TRUE.equals(selectedPeriod.getOpened())) {
                notifications.create("Период уже закрыт").show();
                return;
            }
            selectedPeriod.setOpened(false);
            dataManager.save(selectedPeriod);
        });
    }
}