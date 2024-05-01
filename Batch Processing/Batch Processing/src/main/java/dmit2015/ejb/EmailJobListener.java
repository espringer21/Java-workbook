package dmit2015.ejb;


import jakarta.batch.api.listener.AbstractJobListener;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * This executes before and after a job execution runs.
 * To apply this listener to a batch job you must define a listener element in the Job Specification Language (JSL) file
 * BEFORE the step element as follows:
 *
 * <listeners>
 * <listener ref="jobListener" />
 * </listeners>
 */
@Named
@Dependent
public class EmailJobListener extends AbstractJobListener {

    @Inject
    private JobContext _jobContext;

    @Inject
    private EmailSessionBean mailBean;

    private Logger _logger = Logger.getLogger(EmailJobListener.class.getName());

    private long _startTime;
    @PersistenceContext(unitName = "mssql-DWNorthwindOrders-jpa-pu")
    private EntityManager _entityManager;

    @Inject
    @ConfigProperty(name="email")
    private String email;


    @Override
    public void beforeJob() throws Exception {
        _logger.info(_jobContext.getJobName() + " beforeJob");
        _startTime = System.currentTimeMillis();


    }

    @Override
    public void afterJob() throws Exception {
        _logger.info(_jobContext.getJobName() + " afterJob");
        long endTime = System.currentTimeMillis();

        // Retrieve data from the database
        List<Object[]> results = _entityManager.createNativeQuery("SELECT TOP 10 * FROM DWNorthwindOrders.dbo.FactOrders").getResultList();
        List<Object[]> results1 = _entityManager.createNativeQuery("SELECT TOP 10 * FROM DWNorthwindOrders.dbo.DimDates").getResultList();
        List<Object[]> results2 = _entityManager.createNativeQuery("SELECT TOP 10 * FROM DWNorthwindOrders.dbo.DimCustomers").getResultList();
        List<Object[]> results3 = _entityManager.createNativeQuery("SELECT TOP 10 * FROM DWNorthwindOrders.dbo.DimProducts").getResultList();
        List<Object[]> results4 = _entityManager.createNativeQuery("SELECT TOP 10 * FROM DWNorthwindOrders.dbo.DimEmployees").getResultList();

        // Get column names for each table

        List<String> columnNames1 = Arrays.asList("DateKey", "Date", "DateName", "Month", "MonthName", "Quarter", "QuarterName", "Year", "YearName");
        List<String> columnNames2 = Arrays.asList("CustomerKey", "CustomerID", "CustomerName", "CustomerCity", "CustomerRegion", "CustomerCountry");
        List<String> columnNames3 = Arrays.asList("ProductKey", "ProductID", "ProductName", "ProductCategory", "ProductStdPrice", "ProductIsDiscontinued");
        List<String> columnNames4 = Arrays.asList("EmployeeKey", "EmployeeID", "EmployeeName", "ManagerKey", "ManagerID");


        // Build HTML table
        StringBuilder bodyBuilder = new StringBuilder();

        // Start HTML structure
        bodyBuilder.append("<html><body>");

        // Add individual tables for each result
        appendTable(results, "FactOrders", Arrays.asList("OrderID", "CustomerKey", "EmployeeKey", "ProductKey", "OrderDateKey", "RequiredDateKey", "ShippedDateKey", "PriceOnOrder", "QuantityOnOrder"), bodyBuilder);
        appendTable(results1, "DimDates", columnNames1, bodyBuilder);
        appendTable(results2, "DimCustomers", columnNames2, bodyBuilder);
        appendTable(results3, "DimProducts", columnNames3, bodyBuilder);
        appendTable(results4, "DimEmployees", columnNames4, bodyBuilder);

        // End HTML structure
        bodyBuilder.append("</body></html>");

        String to = email;
        String subject = "DMIT2015 Assginment 8 Batch Job Completion Status from Elijah Springer";
        String body = bodyBuilder.toString();
        mailBean.sendHtmlEmail(to, subject, body);
    }


    private void appendTable(List<Object[]> results, String tableName, List<String> columnNames, StringBuilder bodyBuilder) {
        if (!results.isEmpty()) {
            // Add table name
            bodyBuilder.append("<h2>").append(tableName).append("</h2>");

            // Start table structure
            bodyBuilder.append("<table border='1'><tr>");

            // Add column names
            for (String columnName : columnNames) {
                bodyBuilder.append("<th>").append(columnName).append("</th>");
            }

            bodyBuilder.append("</tr>");

            // Add data rows
            for (Object[] row : results) {
                bodyBuilder.append("<tr>");
                for (Object column : row) {
                    bodyBuilder.append("<td>").append(column.toString()).append("</td>");
                }
                bodyBuilder.append("</tr>");
            }

            // End table structure
            bodyBuilder.append("</table>");
            // Add line break after the table
            bodyBuilder.append("<br/>");
        }
    }




}