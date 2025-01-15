tasks.register("stage") {
    dependsOn(":backend:bootJar")
}