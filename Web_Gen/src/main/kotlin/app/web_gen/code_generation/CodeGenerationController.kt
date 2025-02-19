package app.web_gen.code_generation

import app.web_gen.code_generation.response.ModelResponse
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/code")
class CodeGenerationController(
    private val codeRepository: CodeSnippetRepository,
    private val codeGenerationService: CodeGenerationService,
    private val openAiService: OpenAiService,
    private val modificationService: CodeGenerationService
) {
    val gson = Gson().newBuilder().create()

    @PostMapping("/modify")
    fun modifyCode(@RequestParam query: String): ResponseEntity<String> {
        val queryVector = openAiService.generateEmbedding(query)
        val relevantSnippets = codeRepository.findRelevantSnippets(queryVector.toString(), 3)

        val modifiedCode = openAiService.modifyCode(query, relevantSnippets)

        relevantSnippets.forEach { snippet ->
            modificationService.applyChanges("path/to/codebase/${snippet.filename}", snippet.content, modifiedCode)
        }

        return ResponseEntity.ok("Code modified successfully!")
    }

    @PostMapping("/generate")
    fun generateCode(@RequestParam prompt: String): ResponseEntity<String> {

        //val response = openAiService.generateCompletion(prompt)
        val constResponse = """
            {"textResponse":"We'll create a basic React web app that allows you to input and track orders. The app will have a simple form to add new orders and a list to display all the added orders.","newFiles":[{"path":"src/App.js","content":"import React, { useState } from 'react';\nimport './App.css';\n\nfunction App() {\n  const [orders, setOrders] = useState([]);\n  const [orderInput, setOrderInput] = useState('');\n\n  const handleInputChange = (event) => {\n    setOrderInput(event.target.value);\n  };\n\n  const addOrder = () => {\n    if (orderInput) {\n      setOrders([...orders, orderInput]);\n      setOrderInput('');\n    }\n  };\n\n  return (\n    <div className=\"App\">\n      <h1>Order Tracker</h1>\n      <div className=\"order-form\">\n        <input\n          type=\"text\"\n          value={orderInput}\n          onChange={handleInputChange}\n          placeholder=\"Enter order details\"\n        />\n        <button onClick={addOrder}>Add Order</button>\n      </div>\n      <div className=\"order-list\">\n        <h2>Orders</h2>\n        <ul>\n          {orders.map((order, index) => (\n            <li key={index}>{order}</li>\n          ))}\n        </ul>\n      </div>\n    </div>\n  );\n}\n\nexport default App;\n"},{"path":"src/index.js","content":"import React from 'react';\nimport ReactDOM from 'react-dom/client';\nimport './index.css';\nimport App from './App';\n\nconst root = ReactDOM.createRoot(document.getElementById('root'));\nroot.render(\n  <React.StrictMode>\n    <App />\n  </React.StrictMode>\n);\n"},{"path":"src/App.css","content":"body {\n  font-family: Arial, sans-serif;\n  background-color: #f4f4f9;\n  margin: 0;\n  padding: 20px;\n}\n\n.App {\n  max-width: 600px;\n  margin: 0 auto;\n  background: white;\n  padding: 20px;\n  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n  border-radius: 5px;\n}\n\nh1 {\n  text-align: center;\n}\n\n.order-form {\n  display: flex;\n  justify-content: space-between;\n  margin-bottom: 20px;\n}\n\n.order-form input {\n  flex: 1;\n  padding: 10px;\n  margin-right: 10px;\n  border: 1px solid #ddd;\n  border-radius: 5px;\n}\n\n.order-form button {\n  padding: 10px 20px;\n  border: none;\n  background-color: #28a745;\n  color: white;\n  border-radius: 5px;\n  cursor: pointer;\n}\n\n.order-form button:hover {\n  background-color: #218838;\n}\n\n.order-list ul {\n  list-style-type: none;\n  padding: 0;\n}\n\n.order-list li {\n  background-color: #f1f1f1;\n  padding: 10px;\n  border: 1px solid #ddd;\n  margin-bottom: 5px;\n  border-radius: 5px;\n}\n"}],"modifiedFiles":[]}
        """.trimIndent()
        val modelResponse = gson.fromJson(constResponse, ModelResponse::class.java)
        codeGenerationService.generateFiles(modelResponse)

        return ResponseEntity.ok(constResponse)

    }
}
