package dmit2015.repository;

import common.config.ApplicationConfig;
import common.jpa.AbstractJpaRepository;
import dmit2015.entity.Beer;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ArquillianExtension.class)                  // Run with JUnit 5 instead of JUnit 4

public class BeerRepositoryIT {

    @Inject
    private BeerRepository _todoRepository;

    static Beer currentBeer;  // the TodoItem that is currently being added, find, update, or delete

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");

        return ShrinkWrap.create(WebArchive.class,"test.war")
//                .addAsLibraries(pomFile.resolve("groupId:artifactId:version").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.h2database:h2:2.2.224").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc11:23.2.0.0").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:12.4.2.jre11").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.assertj:assertj-core:3.24.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:2.2").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(Beer.class, BeerRepository.class, AbstractJpaRepository.class, BeerInitializer.class)
               .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-todoitem.xml"),"META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Order(2)
    @Test
    void shouldCreate() {
        currentBeer = new Beer();
        currentBeer.setTask("Create Arquillian IT");
        currentBeer.setDone(true);
        _todoRepository.add(currentBeer);

        Optional<Beer> optionalTodoItem = _todoRepository.findById(currentBeer.getId());
        assertThat(optionalTodoItem.isPresent())
                .isTrue();
        Beer existingBeer = optionalTodoItem.get();
        assertThat(currentBeer.getTask())
                .isEqualTo(existingBeer.getTask());
        assertThat(currentBeer.isDone())
                .isEqualTo(existingBeer.isDone());

    }

    @Order(3)
    @Test
    void shouldFindOne() {
        final Long todoId = currentBeer.getId();
        Optional<Beer> optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isPresent())
                .isTrue();

        Beer existingBeer = optionalTodoItem.get();
        assertThat(existingBeer)
                .usingRecursiveComparison()
                .ignoringFields("createTime")
                .isEqualTo(currentBeer);

    }

    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
            "Create JAX-RS demo project,true,Create DTO version of TodoResource,false"
    })
    void shouldFindAll(String firstName, boolean firstComplete, String lastName, boolean lastComplete) {
        List<Beer> queryResultList = _todoRepository.findAll();
        assertThat(queryResultList.size())
                .isEqualTo(3);

        Beer firstBeer = queryResultList.get(0);
        assertThat(firstBeer.getTask())
                .isEqualTo(firstName);
        assertThat(firstBeer.isDone())
                .isEqualTo(firstComplete);

        Beer lastBeer = queryResultList.get(queryResultList.size() - 1);
        assertThat(lastBeer.getTask())
                .isEqualTo(lastName);
        assertThat(lastBeer.isDone())
                .isEqualTo(lastComplete);
    }

    @Order(4)
    @Test
    void shouldUpdate() {
        currentBeer.setTask("Update JPA Arquillian IT");
        currentBeer.setDone(false);
        _todoRepository.update(currentBeer);

        Optional<Beer> optionalUpdatedTodoItem = _todoRepository.findById(currentBeer.getId());
        assertThat(optionalUpdatedTodoItem.isPresent())
                .isTrue();
        Beer updatedBeer = optionalUpdatedTodoItem.orElseThrow();
        assertThat(updatedBeer)
                .usingRecursiveComparison()
                .ignoringFields("createTime","updateTime","version")
                .isEqualTo(currentBeer);
        assertThat(updatedBeer.getUpdateTime())
                .isNotNull();
        assertThat(updatedBeer.getVersion())
                .isNotEqualTo(currentBeer.getVersion());

    }

    @Order(5)
    @Test
    void shouldDelete() {
        final Long todoId = currentBeer.getId();
        Optional<Beer> optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isPresent())
                .isTrue();

        Beer existingBeer = optionalTodoItem.orElseThrow();
        _todoRepository.deleteById(existingBeer.getId());
        optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isEmpty())
                .isTrue();
    }
}