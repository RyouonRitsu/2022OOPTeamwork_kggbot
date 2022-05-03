plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.10.1"
}

group = "org.example"
version = "0.1.0"

dependencies {
    implementation("com.alibaba:fastjson:1.2.80")
    implementation("ch.qos.logback:logback-core:1.2.11")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    //implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    //implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    //implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
    //implementation("org.apache.logging.log4j:log4j-jcl:2.17.2")
    implementation("org.slf4j:slf4j-api:1.7.36")
    //implementation("org.slf4j:slf4j-simple:1.7.36")
    //implementation("org.apache.logging.log4j:log4j-web:2.17.2")
    implementation("com.vdurmont:emoji-java:5.1.1")
}

repositories {
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    mavenCentral()
}
