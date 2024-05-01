package dmit2015.repository;

import dmit2015.entity.Bill;
import dmit2015.entity.BillPayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.security.enterprise.SecurityContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class BillPaymentRepository {

    @PersistenceContext
    private EntityManager _entityManager;

    @Inject
    private SecurityContext securityContext;


    public Optional<BillPayment> findOneById(Long id) {
        return Optional.ofNullable(_entityManager.find(BillPayment.class, id));
    }

    public List<BillPayment> findAll() {
        List<BillPayment> payments;

        if (!securityContext.isCallerInRole("anonymous")) {
            String callerRole = securityContext.getCallerPrincipal().getName();

            if (securityContext.isCallerInRole("Finance")) {
                String authenticatedUsername = securityContext.getCallerPrincipal().getName();
                payments = _entityManager.createQuery("""
                            SELECT bp
                            FROM BillPayment bp
                            WHERE bp.username = :username
                            ORDER BY bp.paymentDate DESC
                            """, BillPayment.class)
                        .setParameter("username", authenticatedUsername)
                        .getResultList();
            } else if (securityContext.isCallerInRole("Accounting") || securityContext.isCallerInRole("Executive")) {
                payments = _entityManager.createQuery("""
                            SELECT bp
                            FROM BillPayment bp
                            ORDER BY bp.paymentDate DESC
                            """, BillPayment.class)
                        .getResultList();
            } else {
                payments = Collections.emptyList();
            }
        } else {
            throw new RuntimeException("Anonymous users are not allowed to access bill payments.");
        }

        return payments;
    }


    public List<BillPayment> findAllByBillId(Long billId) {
        return _entityManager.createQuery("""
                SELECT bp
                FROM BillPayment bp
                WHERE bp.billToPay.id = :billId
                ORDER BY bp.paymentDate DESC
                """, BillPayment.class)
                .setParameter("billId", billId)
                .getResultList();
    }

    public void create(BillPayment newBillPayment) {
        String username = securityContext.getCallerPrincipal().getName();
        newBillPayment.setUsername(username);
        // Subtract the BillPayment paymentAmount from the Bill paymentBalance
        Bill existingBill = newBillPayment.getBillToPay();
        existingBill.setPaymentBalance(existingBill.getPaymentBalance().subtract(newBillPayment.getPaymentAmount()));
        // Set the payment to the current date
        newBillPayment.setPaymentDate(LocalDate.now());
        // Save the newBillPayment
        _entityManager.persist(newBillPayment);
        // Update the existingBill
        _entityManager.merge(existingBill);
    }

    public void update(BillPayment updatedBillPayment) {
        Optional<BillPayment> optionalBillPayment = findOneById(updatedBillPayment.getId());
        if (optionalBillPayment.isPresent()) {
            BillPayment existingBillPayment = optionalBillPayment.get();

            // Update the amountBalance on the Bill by adding the previous paymentAmount
            // and subtract the new paymentAmount
            Bill existingBill = existingBillPayment.getBillToPay();
            BigDecimal previousPaymentAmount = existingBillPayment.getPaymentAmount();
            BigDecimal newPaymentAmount = updatedBillPayment.getPaymentAmount();
            BigDecimal paymentAmountChange = newPaymentAmount.subtract(previousPaymentAmount);
            BigDecimal newAmountBalance = existingBill.getPaymentBalance().subtract(paymentAmountChange);
            existingBill.setPaymentBalance(newAmountBalance);
            _entityManager.merge(existingBill);

            existingBillPayment.setPaymentAmount(updatedBillPayment.getPaymentAmount());
            existingBillPayment.setPaymentDate(updatedBillPayment.getPaymentDate());
            existingBillPayment.setVersion(updatedBillPayment.getVersion());

            // Update the existingBillPayment
            _entityManager.merge(existingBillPayment);
            _entityManager.flush();
        }
    }

    public void delete(Long id) {
        Optional<BillPayment> optionalBillPayment = findOneById(id);
        if (optionalBillPayment.isPresent()) {
            BillPayment existingBillPayment = optionalBillPayment.get();

            // Add the paymentAmount from the Bill
            Bill existingBill = existingBillPayment.getBillToPay();
            existingBill.setPaymentBalance(existingBill.getPaymentBalance().add(existingBillPayment.getPaymentAmount()));
            _entityManager.merge(existingBill);
            // Remove the Bill
            _entityManager.remove(existingBillPayment);
        }
    }
}
