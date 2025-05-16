package com.company.accounter.view.needs;

import com.company.accounter.app.NeedsSummaryService;
import com.company.accounter.entity.*;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.logicalfilter.GroupFilter;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import io.jmix.flowui.download.Downloader;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;



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
    @Autowired
    private NeedsSummaryService needsSummaryService;
    @ViewComponent
    private JmixButton exportExcelBtn;
    @Subscribe(id = "exportExcelBtn", subject = "clickListener")
    public void onExportExcelBtnClick(final ClickEvent<JmixButton> event) {
        List<Needs> items = (List<Needs>) needsesDataGrid.getItems().getItems();
        if (items.isEmpty()) {
            notifications.create("Нет данных для экспорта").show();
            return;
        }

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID;Вид;Количество;Период;Тип записи;Утверждено;Учтено\n");

        for (Needs n : items) {
            csvBuilder.append(n.getId()).append(";")
                    .append(n.getKind() != null ? n.getKind().getName() : "").append(";")
                    .append(n.getAmount() != null ? n.getAmount() : "").append(";")
                    .append(n.getPeriod() != null ? n.getPeriod().getDuration() : "").append(";")
                    .append(n.getRecordType() != null ? n.getRecordType().name() : "").append(";")
                    .append(Boolean.TRUE.equals(n.getApproved()) ? "Да" : "Нет").append(";")
                    .append(Boolean.TRUE.equals(n.getAccounted()) ? "Да" : "Нет").append("\n");
        }

        byte[] csvBytes = ("\uFEFF" + csvBuilder).getBytes(StandardCharsets.UTF_8);


        StreamResource resource = new StreamResource("needs.csv", () -> new ByteArrayInputStream(csvBytes));
        Anchor downloadLink = new Anchor(resource, "");
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.getElement().setAttribute("style", "display:none");
        downloadLink.getElement().callJsFunction("click");

        exportExcelBtn.getParent().ifPresent(parent -> {
            parent.getElement().appendChild(downloadLink.getElement());
        });
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        Optional<Period> lastOpenPeriod = dataManager.load(Period.class)
                .query("select p from Period_ p where p.opened = true order by p.id desc")
                .maxResults(1)
                .optional();
        lastOpenPeriod.ifPresent(period -> periodsPicker.setValue(period));

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

        SummaryResult result = needsSummaryService.generateSummaryForPeriod(selectedPeriod);
        periodsPicker.setHelperText(result.toString());
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

        boolean totalExists = !dataManager.load(Needs.class)
                .query("select n from Needs n where n.period = :period and n.recordType = :type and n.kind = :kind")
                .parameter("kind", selectedNeeds.getKind())
                .parameter("period", selectedNeeds.getPeriod())
                .parameter("type", RecordType.TOTAL)
                .list().isEmpty();

        if (!isApproved && totalExists) {
            notifications.create("Потребность утверждена, но не учтена в итоговой.").show();
        } else if (isApproved && totalExists) {
            notifications.create("Потребность снята с утверждения. Требуется пересчёт итоговой.").show();
        }
    }
}
