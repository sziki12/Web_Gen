package app.web_gen.code_generation

import app.web_gen.code_generation.request.ProjectGenerationRequest
import app.web_gen.code_generation.response.ProjectCreationResponse
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class LangchainService {

    @Value("\${langchain.ip.address}")
    lateinit var langchain_address : String

    private val restTemplate = RestTemplate()
    val gson = Gson()

    fun generateProject(projectName: String, query: String): ProjectCreationResponse
    {
        val request = ProjectGenerationRequest(
            projectName=projectName.lowercase(),
            threadId = projectName.lowercase(),
            prompt = query)
        val response = khttp.post(
            url = langchain_address,
            json = request.toMap(),
            timeout = 60.0)
        //restTemplate.postForEntity("http://localhost:9000", request, ProjectCreationResponse::class.java)
        return gson.fromJson(response.text, ProjectCreationResponse::class.java)
    }
}