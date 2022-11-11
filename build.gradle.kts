plugins {
    kotlin("jvm") version "1.6.10"
    java
}

group = "com.wanna"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

val ksVersion = "1.0-SNAPSHOT"

dependencies {
    implementation("com.wanna:kotlin-spring-web:$ksVersion")
    implementation("com.wanna:kotlin-spring-framework:$ksVersion")
    implementation("com.wanna:kotlin-spring-boot:$ksVersion")
    implementation("com.wanna:kotlin-spring-boot-autoconfigure:$ksVersion")

    implementation("com.wanna:kotlin-spring-boot-actuator:$ksVersion")
    implementation("com.wanna:kotlin-spring-boot-actuator-autoconfigure:$ksVersion")
    implementation("io.micrometer:micrometer-core:1.9.5")  // metrics

    // DevTools和Test有冲突？
    // implementation("com.wanna:Kotlin-Spring-Boot-Devtools:$ksVersion")

    implementation("com.wanna:kotlin-spring-mybatis:$ksVersion")

    implementation("com.alibaba.nacos:nacos-client:1.4.3")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("io.netty:netty-codec-http:4.1.77.Final")
    implementation(kotlin("stdlib"))

    testImplementation("com.wanna:kotlin-spring-boot-test:$ksVersion")
    testImplementation("com.wanna:kotlin-spring-test:$ksVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    testImplementation("com.alibaba:druid:1.2.10")
    testImplementation("mysql:mysql-connector-java:8.0.29")
    testImplementation("org.apache.httpcomponents:httpclient:4.5.13")
    testImplementation("com.wanna:logger-slf4j-impl:$ksVersion")
}