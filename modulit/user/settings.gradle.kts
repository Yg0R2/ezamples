rootProject.name = "user"

include("user-api")
project(":user-api").projectDir = file("api")

include("user-dao")
project(":user-dao").projectDir = file("dao")

include("user-service")
project(":user-service").projectDir = file("service")

include("user-web")
project(":user-web").projectDir = file("web")
