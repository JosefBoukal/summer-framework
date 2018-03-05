package org.summerframework.core.tenant;

/**
 * The contract for providers to provide or resolve a tenant identification. A service is usually shared between several
 * tenants and this provider is used to identify the tenant of a requested service.
 * <p>
 * TODO move to some other Tenantable Java Project.
 */
public interface TenantIdentityProvider {

    /**
     * The <code>"Tenant-Id"</code> constant that is used to hold the tenant identity value.
     */
    String TENANT_ID = "Tenant-Id";

    /**
     * The <code>"X-Tenant-Id"</code> constant that is used to hold the tenant identity value (usually in HTTP header).
     */
    String X_TENANT_ID = "X-Tenant-Id";

    /**
     * Returns the tenant identifier of the currently requested service or null if the service is not a multitenancy
     * service or a tenant is not known.
     */
    String getTenantIdentity();

}
