package com.company.accounter.app;

import com.company.accounter.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class NeedsSummaryService {

    private static final Logger log = LoggerFactory.getLogger(NeedsSummaryService.class);

    private final DataManager dataManager;
    private final CurrentAuthentication currentAuthentication;

    public NeedsSummaryService(DataManager dataManager, CurrentAuthentication currentAuthentication) {
        this.dataManager = dataManager;
        this.currentAuthentication = currentAuthentication;
    }

    public SummaryResult generateSummaryForPeriod(Period period) {
        log.info("Generating summary for period: {}", period.getId());

        List<Needs> approvedNeeds = dataManager.load(Needs.class)
                .query("select n from Needs n where n.period = :period and n.approved = true and n.recordType <> :recordType")
                .parameter("period", period)
                .parameter("recordType", RecordType.TOTAL)
                .list();

        log.info("Loaded {} approved needs", approvedNeeds.size());

        Map<NeedKind, Integer> summaryMap = approvedNeeds.stream()
                .collect(Collectors.groupingBy(
                        Needs::getKind,
                        Collectors.summingInt(Needs::getAmount)
                ));

        log.info("Calculated summary map: {}", summaryMap);

        List<Needs> totalNeeds = dataManager.load(Needs.class)
                .query("select n from Needs n where n.period = :period and n.recordType = :recordType")
                .parameter("period", period)
                .parameter("recordType", RecordType.TOTAL)
                .list();

        log.info("Loaded {} total needs", totalNeeds.size());

        int created = 0;
        int updated = 0;
        int deleted = 0;

        for (Needs total : totalNeeds) {
            NeedKind kind = total.getKind();
            Integer newAmount = summaryMap.get(kind);

            if (newAmount == null) {
                log.debug("Deleting total need: {}", total);
                dataManager.remove(total);
                deleted++;
            } else if (!Objects.equals(total.getAmount(), newAmount)) {
                log.debug("Updating total need: {}, new amount: {}", total, newAmount);
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
            log.debug("Creating new total need: {}", total);
            dataManager.save(total);
            created++;
        }

        for (Needs n : approvedNeeds) {
            if (!Boolean.TRUE.equals(n.getAccounted())) {
                log.trace("Marking as accounted: {}", n);
                n.setAccounted(true);
            }
        }

        dataManager.saveAll(approvedNeeds);
        log.info("Summary result — Created: {}, Updated: {}, Deleted: {}", created, updated, deleted);

        return new SummaryResult(created, updated, deleted);
    }
}