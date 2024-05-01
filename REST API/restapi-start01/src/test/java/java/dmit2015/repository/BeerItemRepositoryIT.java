package java.dmit2015.repository;

import common.config.ApplicationConfig;
import dmit2015.entity.BeerItem;
import dmit2015.repository.AbstractJpaRepository;
import dmit2015.repository.BeerItemInitializer;
import dmit2015.repository.BeerItemRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ArquillianExtension.class)                  // Run with JUnit 5 instead of JUnit 4

public class BeerItemRepositoryIT {

    @Inject
    private BeerItemRepository _todoRepository;

    static BeerItem currentTodoItem;  // the TodoItem that is currently being added, find, update, or delete

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
                .addClasses(BeerItem.class, BeerItemRepository.class, AbstractJpaRepository.class, BeerItemInitializer.class)
               .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-todoitem.xml"),"META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Order(2)
    @Test
    void shouldCreate() {
        currentTodoItem = new BeerItem();
        currentTodoItem.setName("Create Arquillian IT");
        currentTodoItem.setDone(true);
        _todoRepository.add(currentTodoItem);

        Optional<BeerItem> optionalTodoItem = _todoRepository.findById(currentTodoItem.getId());
        assertThat(optionalTodoItem.isPresent())
                .isTrue();
        BeerItem existingTodoItem = optionalTodoItem.get();
        assertThat(currentTodoItem.getName())
                .isEqualTo(existingTodoItem.getName());
        assertThat(currentTodoItem.isDone())
                .isEqualTo(existingTodoItem.isDone());

    }

    @Order(3)
    @Test
    void shouldFindOne() {
        final Long todoId = currentTodoItem.getId();
        Optional<BeerItem> optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isPresent())
                .isTrue();

        BeerItem existingTodoItem = optionalTodoItem.get();
        assertThat(existingTodoItem)
                .usingRecursiveComparison()
                .ignoringFields("createTime")
                .isEqualTo(currentTodoItem);

    }

    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
            "Create JAX-RS demo project,true,Create DTO version of TodoResource,false"
    })
    void shouldFindAll(String firstName, boolean firstComplete, String lastName, boolean lastComplete) {
        List<BeerItem> queryResultList = _todoRepository.findAll();
        assertThat(queryResultList.size())
                .isEqualTo(3);

        BeerItem firstTodoItem = queryResultList.get(0);
        assertThat(firstTodoItem.getName())
                .isEqualTo(firstName);
        assertThat(firstTodoItem.isDone())
                .isEqualTo(firstComplete);

        BeerItem lastTodoItem = queryResultList.get(queryResultList.size() - 1);
        assertThat(lastTodoItem.getName())
                .isEqualTo(lastName);
        assertThat(lastTodoItem.isDone())
                .isEqualTo(lastComplete);
    }

    @Order(4)
    @Test
    void shouldUpdate() {
        currentTodoItem.setName("Update JPA Arquillian IT");
        currentTodoItem.setDone(false);
        _todoRepository.update(currentTodoItem);

        Optional<BeerItem> optionalUpdatedTodoItem = _todoRepository.findById(currentTodoItem.getId());
        assertThat(optionalUpdatedTodoItem.isPresent())
                .isTrue();
        BeerItem updatedTodoItem = optionalUpdatedTodoItem.orElseThrow();
        assertThat(updatedTodoItem)
                .usingRecursiveComparison()
                .ignoringFields("createTime","updateTime","version")
                .isEqualTo(currentTodoItem);
        assertThat(updatedTodoItem.getUpdateTime())
                .isNotNull();
        assertThat(updatedTodoItem.getVersion())
                .isNotEqualTo(currentTodoItem.getVersion());

    }

    @Order(5)
    @Test
    void shouldDelete() {
        final Long todoId = currentTodoItem.getId();
        Optional<BeerItem> optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isPresent())
                .isTrue();

        BeerItem existingTodoItem = optionalTodoItem.orElseThrow();
        _todoRepository.deleteById(existingTodoItem.getId());
        optionalTodoItem = _todoRepository.findById(todoId);
        assertThat(optionalTodoItem.isEmpty())
                .isTrue();
    }
}