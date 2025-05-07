package com.company.accounter.security;

import com.company.accounter.entity.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "Manager", code = ManagerRole.CODE)
public interface ManagerRole extends UiMinimalRole {
    String CODE = "manager";

    @MenuPolicy(menuIds = {"User.list", "NeedKind.list", "NeedType.list", "Period_.list", "NeedsManager.list"})
    @ViewPolicy(viewIds = {"User.list", "NeedKind.list", "NeedType.list", "Period_.list", "NeedsManager.list", "NeedKind.detail", "Needs.detail", "NeedType.detail", "Period_.detail", "User.detail"})
    void screens();

    @EntityAttributePolicy(entityClass = NeedType.class, attributes = {"description", "name"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = NeedType.class, actions = EntityPolicyAction.ALL)
    void needType();

    @EntityAttributePolicy(entityClass = NeedKind.class, attributes = {"name", "unit"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = NeedKind.class, actions = EntityPolicyAction.ALL)
    void needKind();

    @EntityAttributePolicy(entityClass = Period.class, attributes = {"duration", "opened", "id", "*"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Period.class, actions = EntityPolicyAction.ALL)
    void period();

    @EntityAttributePolicy(entityClass = Needs.class, attributes = {"recipientUser", "approved", "accounted", "period"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = Needs.class, attributes = {"createdBy", "justification", "amount", "recordType", "kind"}, action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Needs.class, actions = EntityPolicyAction.ALL)
    void needs();

    @EntityAttributePolicy(entityClass = User.class, attributes = {"username", "firstName", "lastName", "active", "timeZoneId", "email"}, action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.CREATE, EntityPolicyAction.READ})
    void user();
}