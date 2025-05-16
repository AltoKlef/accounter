package com.company.accounter.events;
import com.company.accounter.entity.Period;
import org.springframework.context.ApplicationEvent;

public class PeriodClosedEvent extends ApplicationEvent {

    private final Period period;

    public PeriodClosedEvent(Object source, Period period) {
        super(source);
        this.period = period;
    }

    public Period getPeriod() {
        return period;
    }
}
