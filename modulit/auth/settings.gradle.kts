rootProject.name = "auth"

include("auth-api")
project(":auth-api").projectDir = file("api")

include("auth-service")
project(":auth-service").projectDir = file("service")

include("auth-web")
project(":auth-web").projectDir = file("web")