package common.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.enterprise.context.ApplicationScoped;

@DataSourceDefinitions({

        @DataSourceDefinition(
                name="java:app/datasources/Northwind",
                className="com.microsoft.sqlserver.jdbc.SQLServerDataSource",
                url="jdbc:sqlserver://localhost;databaseName=Northwind;TrustServerCertificate=true",
                user="user2015",
                password="Password2015"),

        @DataSourceDefinition(
                name="java:app/datasources/DWNorthwindOrders",
                className="com.microsoft.sqlserver.jdbc.SQLServerDataSource",
                url="jdbc:sqlserver://localhost;databaseName=DWNorthwindOrders;TrustServerCertificate=true",
                user="user2015",
                password="Password2015"),

})

@ApplicationScoped
public class ApplicationConfig {

}
