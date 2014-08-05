package com.onetouch.view.context;

import com.onetouch.model.domainobject.Tenant;

public class TenantContextUtil {
    private static final ThreadLocal<Tenant> contextHolder =
        new ThreadLocal<Tenant>();

public static void setTenant(Tenant tenant) {
  
  contextHolder.set(tenant);
}

public static Tenant getTenant() {
  return (Tenant) contextHolder.get();
}

public static void clearTenant() {
  contextHolder.remove();
}

}

