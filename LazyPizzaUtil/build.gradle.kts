plugins {
    kotlin("jvm")
}

group = "pl.lazypizzautil"
version = "1.0.0"

dependencies {



    implementation(libs.firebase.admin)
    implementation(libs.gson)

    implementation(libs.firebase.storage)

    implementation(libs.logback.classic)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}