plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.studio"
version = "0.0.1-SNAPSHOT"
description = "Project of Booking Studio"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    implementation("org.modelmapper:modelmapper:3.1.1")
    implementation("org.apache.commons:commons-text:1.14.0") {
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }
    implementation("org.projectlombok:lombok")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("com.sun.mail:jakarta.mail:2.0.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.sendgrid:sendgrid-java:4.10.3")
    implementation("com.cloudinary:cloudinary-http44:1.39.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    runtimeOnly("com.h2database:h2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    annotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
