package com.company.accounter.security;

import com.company.accounter.entity.Needs;
import com.company.accounter.entity.User;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.security.model.RowLevelBiPredicate;
import io.jmix.security.model.RowLevelPolicyAction;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.PredicateRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;
import org.springframework.context.ApplicationContext;

@RowLevelRole(name = "RestrictedEmployeeRole", code = RestrictedEmployeeRole.CODE)
public interface RestrictedEmployeeRole {
    String CODE = "restricted-employee-role";
    @JpqlRowLevelPolicy(entityClass = Needs.class,
            where = "{E}.period.opened = true and {E}.recordType = 'A'")
    void needsRead();

    @PredicateRowLevelPolicy(entityClass = Needs.class, actions = {RowLevelPolicyAction.UPDATE, RowLevelPolicyAction.DELETE})
    default RowLevelBiPredicate<Needs, ApplicationContext> needsPredicate() {
        return (needs, applicationContext) -> {
            CurrentAuthentication currentAuthentication = applicationContext.getBean(CurrentAuthentication.class);
            User currentUser = (User) currentAuthentication.getUser();

            return  (currentUser.getUsername().equals(needs.getCreatedBy().getUsername()) || currentUser.getUsername().equals(needs.getRecipientUser().getUsername()))
                    && needs.getPeriod() != null
                    && Boolean.TRUE.equals(needs.getPeriod().getOpened());
        };
    }
}