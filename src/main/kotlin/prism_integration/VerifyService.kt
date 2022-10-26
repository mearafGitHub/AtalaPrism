package prism_integration

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post

@Controller("/verify") //
class VerifyService {
    @Get(produces = [MediaType.TEXT_PLAIN]) //
    fun index(): String {
        return "Hello Prism: get Verification" //
    }

}