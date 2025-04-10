package org.lupoi.http_test;
/*
    @author user
    @project HTTP_Test
    @class HTTPTestArchitectureTests
    @version 1.0.0
    @since 10.04.2025 - 15.02
*/

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;



public class HTTPTestArchitectureTests {
    private JavaClasses applicationClasses;

    @BeforeEach
    void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.lupoi.http_test");
    }

    // 1. Перевірка шарової архітектури
    @Test
    void shouldFollowLayerArchitecture()  {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                //
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                //
                .check(applicationClasses);
    }

    // 2. Контролери не повинні залежати від інших контролерів
    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("out of arch rules")
                .check(applicationClasses);
    }

    // 3. Репозиторії не повинні залежати від сервісів
    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..service..")
                .because("out of arch rules")
                .check(applicationClasses);
    }

    // 4. Класи контролерів повинні мати суфікс Controller
    @Test
    void  controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .haveSimpleNameEndingWith("Controller")
                .check(applicationClasses);
    }

    // 5. Класи контролерів повинні бути анотовані @RestController
    @Test
    void controllerClassesShouldBeAnnotatedByControllerClass() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(RestController.class)
                .check(applicationClasses);
    }

    // 6. Репозиторії повинні бути інтерфейсами
    @Test
    void repositoryShouldBeInterface() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .beInterfaces()
                .check(applicationClasses);
    }

    // 7. Поля в контролерах не повинні використовувати @Autowired
    @Test
    void anyControllerFieldsShouldNotBeAnnotatedAutowired() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(Autowired.class)
                .check(applicationClasses);
    }

    // 8. Поля в моделях повинні бути приватними
    @Test
    void modelFieldsShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..model..")
                .should().notBePublic()
                .because("smth")
                .check(applicationClasses);

    }

    // 9. Сервіси повинні мати суфікс Service
    @Test
    void serviceClassesShouldBeNamedXService() {
        classes()
                .that().resideInAPackage("..service..")
                .should()
                .haveSimpleNameEndingWith("Service")
                .check(applicationClasses);
    }

    // 10. Сервіси повинні бути анотовані @Service
    @Test
    void serviceClassesShouldBeAnnotatedWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .should()
                .beAnnotatedWith(org.springframework.stereotype.Service.class)
                .check(applicationClasses);
    }

    // 11. Репозиторії повинні мати суфікс Repository
    @Test
    void repositoryClassesShouldBeNamedXRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .haveSimpleNameEndingWith("Repository")
                .check(applicationClasses);
    }

    // 12. Репозиторії повинні бути анотовані @Repository
    @Test
    void repositoryClassesShouldBeAnnotatedWithRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .beAnnotatedWith(org.springframework.stereotype.Repository.class)
                .check(applicationClasses);
    }

    // 13. Контролери повинні знаходитись у правильному пакеті
    @Test
    void controllersShouldResideInControllerPackage() {
        classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    // 14. Сервіси повинні знаходитись у правильному пакеті
    @Test
    void servicesShouldResideInServicePackage() {
        classes()
                .that().haveSimpleNameEndingWith("Service")
                .should().resideInAPackage("..service..")
                .check(applicationClasses);
    }

    // 15. Репозиторії повинні знаходитись у правильному пакеті
    @Test
    void repositoriesShouldResideInRepositoryPackage() {
        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..repository..")
                .check(applicationClasses);
    }

    // 16. Класи моделей повинні знаходитись у пакеті model
    @Test
    void modelClassesShouldResideInModelPackage() {
        classes()
                .that().haveSimpleNameEndingWith("Entity")
                .or().haveSimpleNameEndingWith("Model")
                .should().resideInAPackage("..model..")
                .check(applicationClasses);
    }

    // 17. Контролери не повинні залежати від репозиторіїв напряму
    @Test
    void controllersShouldNotDependOnRepositories() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..repository..")
                .check(applicationClasses);
    }

    // 18. Всі залежності повинні ін'єктуватись через конструктор
    @Test
    void dependenciesShouldBeInjectedViaConstructor() {
        classes()
                .that().resideInAPackage("..controller..")
                .or().resideInAPackage("..service..")
                .should().haveOnlyFinalFields()
                .check(applicationClasses);
    }

    // 19. Моделі не повинні залежати від сервісів
    @Test
    void modelClassesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should()
                .dependOnClassesThat().resideInAPackage("..service..")
                .because("the model should not have service logic")
                .check(applicationClasses);
    }

    // 20. Класи не повинні мати префікс 'Abstract'
    @Test
    void noClassShouldHaveNameStartingWithAbstract() {
        noClasses()
                .should()
                .haveSimpleNameStartingWith("Abstract")
                .because("абстрактні класи краще не позначати через префікс Abstract — краще через логіку")
                .check(applicationClasses);
    }

    // 21. Всі моделі мають бути анотовані @Entity
    @Test
    void entityClassesShouldBeAnnotatedWithEntity() {
        classes()
                .that().resideInAPackage("..model..")
                .and().haveSimpleNameEndingWith("Entity")
                .should().beAnnotatedWith(jakarta.persistence.Entity.class)
                .check(applicationClasses);
    }

    // 22. Контролери не повинні мати полів типу List<>
    @Test
    void controllersShouldNotHaveListFields() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().notHaveRawType(List.class)
                .check(applicationClasses);
    }

    // 23. Класи сервісів не повинні залежати від контролерів
    @Test
    void servicesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    // 24. У контролерах не має бути публічних полів
    @Test
    void controllerFieldsShouldBePrivateOrProtected() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().notBePublic()
                .check(applicationClasses);
    }

    // 25. Усі сервіси повинні бути класами (не інтерфейсами)
    @Test
    void servicesShouldBeConcreteClasses() {
        classes()
                .that().resideInAPackage("..service..")
                .should().notBeInterfaces()
                .check(applicationClasses);
    }

    // 26. Усі контролери мають бути public
    @Test
    void controllerClassesShouldBePublic() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().bePublic()
                .check(applicationClasses);
    }

    // 27. Моделі не повинні залежати від Spring
    @Test
    void modelClassesShouldNotDependOnSpring() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..")
                .because("the model should not know about the framework")
                .check(applicationClasses);
    }

    // 28. Імена моделей не повинні містити слово "Service"
    @Test
    void modelClassesShouldNotContainServiceInName() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().haveSimpleNameContaining("Service")
                .because("моделі не повинні мати в імені слово Service")
                .check(applicationClasses);
    }
}
