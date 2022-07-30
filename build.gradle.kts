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
    implementation("com.wanna:Kotlin-Spring-Web:$ksVersion")
    implementation("com.wanna:Kotlin-Spring-Framework:$ksVersion")
    implementation("com.wanna:Kotlin-Spring-Boot:$ksVersion")
    implementation("com.wanna:Kotlin-Spring-Boot-Autoconfigure:$ksVersion")

    implementation("com.wanna:Kotlin-Spring-Boot-Actuator:$ksVersion")
    implementation("com.wanna:Kotlin-Spring-Boot-Actuator-Autoconfigure:$ksVersion")

    implementation("com.wanna:Kotlin-Spring-Boot-Devtools:$ksVersion")

    implementation("com.wanna:Kotlin-Spring-MyBatis:$ksVersion")

    implementation("com.alibaba.nacos:nacos-client:1.4.3")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("io.netty:netty-codec-http:4.1.77.Final")
    implementation(kotlin("stdlib"))

    testImplementation("com.alibaba:druid:1.2.10")
    testImplementation("mysql:mysql-connector-java:8.0.29")
    testImplementation("org.apache.httpcomponents:httpclient:4.5.13")
    testImplementation("com.wanna:logger-slf4j-impl:$ksVersion")
}