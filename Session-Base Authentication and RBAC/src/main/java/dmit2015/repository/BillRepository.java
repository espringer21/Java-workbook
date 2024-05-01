package dmit2015.repository;

import dmit2015.entity.Bill;
import dmit2015.security.BillRepositorySecurityInterceptor;
import dmit2015.security.DenyAnonymousAccessSecurityInterceptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.Interceptors;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.security.enterprise.SecurityContext;
import org.apache.poi.ss.formula.functions.Finance;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
@Interceptors({BillRepositorySecurityInterceptor.class, DenyAnonymousAccessSecurityInterceptor.class})
public class BillRepository {

    @PersistenceContext
    private EntityManager _entityManager;

    @Inject
    private SecurityContext securityContext;


    @Transactional
    public void create(Bill newBill) {
        String username = securityContext.getCallerPrincipal().getName();
        newBill.setUsername(username);
        newBill.setPaymentBalance(newBill.getPaymentDue());
        _entityManager.persist(newBill);
    }

    private void remove(Bill existingBill) {
        // Delete all the BillPayment associated with the existingBill
        _entityManager.createQuery("DELETE FROM BillPayment bp WHERE bp.billToPay.id = :billIdValue")
                .setParameter("billIdValue", existingBill.getId())
                .executeUpdate();
        // Delete the existingBill
        _entityManager.remove(existingBill);
    }

    public void delete(Long billId) {
        Optional<Bill> optionalBill = findOneById(billId);
        optionalBill.ifPresent(this::remove);
    }

    public List<Bill> findAll() {
        List<Bill> qu;

        if (securityContext.isCallerInRole("anonymous")) {
            throw new RuntimeException("Anonymous users are not allowed to access bills.");
        } else if (securityContext.isCallerInRole("Finance")) {
            String authenticatedUsername = securityContext.getCallerPrincipal().getName();
            qu = _entityManager.createQuery("""
                        SELECT b
                        FROM Bill b
                        WHERE b.username = :username
                        ORDER BY b.dueDate DESC
                        """, Bill.class)
                    .setParameter("username", authenticatedUsername)
                    .getResultList();
        } else if (securityContext.isCallerInRole("Accounting") || securityContext.isCallerInRole("Executive")) {
            qu = _entityManager.createQuery("""
                    SELECT b
                    FROM Bill b
                    ORDER BY b.dueDate DESC
                    """, Bill.class)
                    .getResultList();
        } else {
            qu = Collections.emptyList();
        }

        return qu;
    }

    public Optional<Bill> findOneById(Long billId) {
        return Optional.ofNullable(_entityManager.find(Bill.class, billId));
    }

    public void update(Bill existingBill) {
        Optional<Bill> optionalBill = findOneById(existingBill.getId());
        optionalBill.ifPresent(editBill -> {
            editBill.setPayeeName(existingBill.getPayeeName());
            editBill.setDueDate(existingBill.getDueDate());
            editBill.setPaymentDue(existingBill.getPaymentDue());
            editBill.setPaymentBalance(existingBill.getPaymentBalance());
            editBill.setVersion(existingBill.getVersion());
            _entityManager.merge(editBill);
            _entityManager.flush();
        });
    }
}
