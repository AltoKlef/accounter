package com.company.accounter.security;

import com.company.accounter.entity.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "Employee", code = EmployeeRole.CODE)
public interface EmployeeRole extends RestrictedEmployeeRole, UiMinimalRole{
    String CODE = "employee";

    @EntityAttributePolicy(entityClass = Needs.class, attributes = {"accounted", "approved", "recordType", "createdBy"}, action = EntityAttributePolicyAction.VIEW)
    @EntityAttributePolicy(entityClass = Needs.class, attributes = {"period", "amount", "kind", "recipientUser", "justification"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Needs.class, actions = EntityPolicyAction.ALL)
    void needs();

    @EntityAttributePolicy(entityClass = NeedType.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = NeedType.class, actions = EntityPolicyAction.READ)
    void needType();

    @EntityAttributePolicy(entityClass = NeedKind.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = NeedKind.class, actions = EntityPolicyAction.READ)
    void needKind();

    @EntityAttributePolicy(entityClass = Period.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Period.class, actions = EntityPolicyAction.READ)
    void period();

    @MenuPolicy(menuIds = {"User.list", "NeedKind.list", "NeedType.list", "Period_.list", "Needs.list"})
    @ViewPolicy(viewIds = {"User.list", "NeedKind.list", "NeedType.list", "Period_.list", "Needs.list", "NeedKind.detail", "NeedType.detail", "Needs.detail", "Period_.detail"})
    void screens();

    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    void user();
}