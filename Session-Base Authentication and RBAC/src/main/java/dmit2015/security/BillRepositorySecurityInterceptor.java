package dmit2015.security;

import jakarta.inject.Inject;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.SecurityContext;

public class BillRepositorySecurityInterceptor {

    @Inject
    private SecurityContext securityContext;

    @AroundInvoke
    public Object verifyAccess(InvocationContext ic) throws Exception {
        String methodName = ic.getMethod().getName();

        // Permit only users with the role "Finance" to access create() and update() methods
        if (methodName.equals("create") || methodName.equals("update")) {
            boolean isInRole = securityContext.isCallerInRole("Finance");
            if (!isInRole) {
                throw new RuntimeException("Access denied! You do not have permission to execute this method");
            }
        }

        // Permit only users with the role "Accounting" to access remove() and delete() methods
        if (methodName.equals("remove") || methodName.equals("delete")) {
            boolean isInRole = securityContext.isCallerInRole("Accounting");
            if (!isInRole) {
                throw new RuntimeException("Access denied! You do not have permission to execute this method");
            }
        }

        return ic.proceed();
    }
}
