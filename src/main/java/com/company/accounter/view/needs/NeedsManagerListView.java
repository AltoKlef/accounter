package com.company.accounter.view.needs;

import com.company.accounter.entity.*;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.logicalfilter.GroupFilter;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Route(value = "manager-needses", layout = MainView.class)
@ViewController(id = "NeedsManager.list")
@ViewDescriptor(path = "needs-managert-list-view.xml")
@LookupComponent("needsesDataGrid")
@DialogMode(width = "64em")
public class NeedsManagerListView extends StandardListView<Needs> {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private Notifications notifications;

    @ViewComponent
    private EntityPicker<Period> periodsPicker;

    @ViewComponent
    private JmixButton approveButton;

    @ViewComponent
    private DataGrid<Needs> needsesDataGrid;
    @ViewComponent
    private GroupFilter groupFilter;
    periodsPicker.setValue(period);
    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        Optional<Period> lastOpenPeriod = dataManager.load(Period.class)
                .query("select p from Period_ p where p.opened = true order by p.id desc")
                .maxResults(1)
                .optional();

        lastOpenPeriod.ifPresent(period -> {

            groupFilter.
        });

    }

    @Subscribe("needsesDataGrid")
    public void onNeedsesDataGridSelection(SelectionEvent<DataGrid<Needs>, Needs> event) {
        approveButton.setEnabled(!event.getAllSelectedItems().isEmpty());
    }

    @Subscribe(id = "summaryButton", subject = "clickListener")
    public void onSummaryButtonClick(ClickEvent<JmixButton> event) {
        Period selectedPeriod = periodsPicker.getValue();

        if (selectedPeriod == null) {
            periodsPicker.setHelperText("Пожалуйста, выберите период");
            return;
        }

        if (!Boolean.TRUE.equals(selectedPeriod.getOpened())) {
            periodsPicker.setHelperText("Период закрыт, генерация итоговой невозможна");
            return;
        }

        generateSummaryForPeriod(selectedPeriod);
    }

    @Subscribe(id = "approveButton", subject = "clickListener")
    public void onApproveButtonClick(ClickEvent<JmixButton> event) {
        Needs selectedNeeds = needsesDataGrid.getSingleSelectedItem();

        if (selectedNeeds == null) {
            notifications.create("Выберите одну строку.").show();
            return;
        }

        boolean isApproved = Boolean.TRUE.equals(selectedNeeds.getApproved());
        selectedNeeds.setApproved(!isApproved);
        dataManager.save(selectedNeeds);

        boolean totalExists = dataManager.load(Needs.class)
                .query("select n from Needs n where n.period = :period and n.recordType = :type and n.kind = :kind")
                .parameter("kind", selectedNeeds.getKind())
                .parameter("period", selectedNeeds.getPeriod())
                .parameter("type", RecordType.TOTAL)
                .list()
                .size() > 0;

        if (!isApproved && totalExists) {
            notifications.create("Потребность утверждена, но не учтена в итоговой.").show();
        } else if (isApproved && totalExists) {
            notifications.create("Потребность снята с утверждения. Требуется пересчёт итоговой.").show();
        }
    }

    private void generateSummaryForPeriod(Period period) {
        List<Needs> approvedNeeds = dataManager.load(Needs.class)
                .query("select n from Needs n where n.period = :period and n.approved = true and n.recordType <> :recordType")
                .parameter("period", period)
                .parameter("recordType", RecordType.TOTAL)
                .list();

        Map<NeedKind, Integer> summaryMap = approvedNeeds.stream()
                .collect(Collectors.groupingBy(
                        Needs::getKind,
                        Collectors.summingInt(Needs::getAmount)
                ));

        List<Needs> totalNeeds = dataManager.load(Needs.class)
                .query("select n from Needs n where n.period = :period and n.recordType = :recordType")
                .parameter("period", period)
                .parameter("recordType", RecordType.TOTAL)
                .list();

        int created = 0;
        int updated = 0;
        int deleted = 0;

        for (Needs total : totalNeeds) {
            NeedKind kind = total.getKind();
            Integer newAmount = summaryMap.get(kind);

            if (newAmount == null) {
                dataManager.remove(total);
                deleted++;
            } else if (!Objects.equals(total.getAmount(), newAmount)) {
                total.setAmount(newAmount);
                dataManager.save(total);
                updated++;
                summaryMap.remove(kind);
            } else {
                summaryMap.remove(kind);
            }
        }

        for (Map.Entry<NeedKind, Integer> entry : summaryMap.entrySet()) {
            Needs total = dataManager.create(Needs.class);
            total.setRecipientUser((User) currentAuthentication.getUser());
            total.setKind(entry.getKey());
            total.setAmount(entry.getValue());
            total.setPeriod(period);
            total.setRecordType(RecordType.TOTAL);
            total.setAccounted(true);
            total.setApproved(true);
            dataManager.save(total);
            created++;
        }

        for (Needs n : approvedNeeds) {
            if (!Boolean.TRUE.equals(n.getAccounted())) {
                n.setAccounted(true);
            }
        }

        dataManager.saveAll(approvedNeeds);

        periodsPicker.setHelperText("Итоговая сформирована. Добавлено: " + created + ", Обновлено: " + updated + ", Удалено: " + deleted);
    }
}
