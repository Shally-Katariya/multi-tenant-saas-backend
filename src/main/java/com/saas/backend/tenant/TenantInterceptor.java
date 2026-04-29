package com.saas.backend.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // Extract tenant from header
        String tenantId = request.getHeader("X-Tenant-ID");

        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setTenant(tenantId);
        } else {
            // Optional: You can enforce tenant presence
            // For now we allow it, but later we will secure this
            System.out.println("⚠️ No Tenant ID provided");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        // 🔥 VERY IMPORTANT: Clear ThreadLocal to prevent data leaks
        TenantContext.clear();
    }
}