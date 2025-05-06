package com.company.accounter.view.needs;

import com.company.accounter.entity.*;
import com.company.accounter.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Route(value = "manager-needses", layout = MainView.class)
@ViewController(id = "NeedsManager.list")
@ViewDescriptor(path = "needs-managert-list-view.xml")
@LookupComponent("needsesDataGrid")
@DialogMode(width = "64em")
public class NeedsManagerListView extends StandardListView<Needs> {
    @Autowired
    private DataManager dataManager;
    @ViewComponent
    private EntityPicker<Period> periodsPicker;
    @Autowired
    private CurrentAuthentication currentAuthentication;



    private void generateSummaryForPeriod(Period period) {
        //утверждённые потребности за этот период
        List<Needs> approvedNeeds = dataManager.load(Needs.class)
                .query("select n from Needs n where n.period = :period and n.approved = true and n.recordType <> :recordType")
                .parameter("period", period)
                .parameter("recordType", RecordType.TOTAL)
                .list();

        Map<NeedKind, Integer> summaryMap = approvedNeeds.stream()
                .collect(Collectors.groupingBy(
                        Needs::getKind,
                        Collectors.summingInt(n -> n.getAmount().intValue())
                ));

        List<Needs> totalNeeds = dataManager.load(Needs.class)
                .query("select n from Needs n where n.period = :period and n.recordType = :recordType")
                .parameter("period", period)
                .parameter("recordType", RecordType.TOTAL)
                .list();

        int created = 0, updated = 0, deleted = 0;

        // Удаление и обновление существующих итогов
        for (Needs total : totalNeeds) {
            NeedKind kind = total.getKind();
            Integer newAmount = summaryMap.get(kind);

            if (newAmount == null) {
                // не утверждён - удалить
                dataManager.remove(total);
                deleted++;
            } else if (!Objects.equals(total.getAmount(), newAmount)) {
                // Обновить сумму
                total.setAmount(newAmount);
                dataManager.save(total);
                updated++;
                summaryMap.remove(kind); // Чтобы потом не создать дубликат
            } else {
                summaryMap.remove(kind);
            }
        }

        // Создание новых итоговых, которых не было
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



        // Проставляем "учтено = да" для обработанных потребностей
        for (Needs n : approvedNeeds) {
            if (!Boolean.TRUE.equals(n.getAccounted())) {
                n.setAccounted(true);
            }
        }
        dataManager.saveAll(approvedNeeds);

        periodsPicker.setHelperText("Итоговая сформирована. Добавлено: " + created + ", Обновлено: " + updated + ", Удалено: " + deleted);
    }


    @ViewComponent
    private JmixButton summaryButton;


    @Subscribe(id = "summaryButton", subject = "clickListener")
    public void onSummaryButtonClick(final ClickEvent<JmixButton> event) {
        Period selectedPeriod = periodsPicker.getValue();
        if (selectedPeriod == null) {
            periodsPicker.setHelperText("Пожалуйста, выберите период");
            return;
        }

        if (!Boolean.TRUE.equals(selectedPeriod.getOpened())) {
            periodsPicker.setHelperText("Период закрыт, генерация итоговой невозможна");
            return;
        }

        // ✅ Здесь — логика формирования итоговой потребности:
        periodsPicker.setHelperText("Сгенерирована итоговая");
        generateSummaryForPeriod(selectedPeriod);
    }
}