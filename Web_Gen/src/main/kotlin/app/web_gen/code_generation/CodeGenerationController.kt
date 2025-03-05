package app.web_gen.code_generation

import app.web_gen.code_generation.response.ProjectCreationResponse
import app.web_gen.code_generation.response.ProjectModificationResponse
import app.web_gen.code_snippet.CodeSnippetRepository
import app.web_gen.project.GeneratedProjectRepository
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/code")
class CodeGenerationController(
    private val codeRepository: CodeSnippetRepository,
    private val projectRepository: GeneratedProjectRepository,
    private val codeGenerationService: CodeGenerationService,
    private val openAiService: OpenAiService,
) {
    private val gson: Gson = Gson().newBuilder().create()

    @PostMapping("/{projectName}/modify")
    fun modifyCode(
        @RequestParam query: String,
        @PathVariable projectName: String
    ): ResponseEntity<ProjectModificationResponse> {

        try {
            val queryVector = openAiService.generateEmbedding(query)
            val relevantSnippets = codeRepository.findRelevantSnippets(projectName, queryVector, 3)
            val response = openAiService.modifyCode(query, relevantSnippets)
            val modifiedCode = gson.fromJson(response, ProjectModificationResponse::class.java)

            codeGenerationService.updateProjectFiles(projectName,modifiedCode, relevantSnippets)
            return ResponseEntity.ok(modifiedCode)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("{projectName}/generate")
    fun generateCode(@RequestParam prompt: String, @PathVariable projectName: String): ResponseEntity<ProjectCreationResponse> {
        //val response = openAiService.generateProject(projectName,prompt)
        val responseString = """
            {
                "textResponse": "To create a basic React web app for tracking, adding, removing, and modifying orders, follow the steps below.",
                "codeToGenerateFiles": "npx create-react-app app4.0",
                "codeToInstallPackages": "npm install react-redux redux react-router-dom",
                "codeToRun": "npm start",
                "newFiles": [
                    {
                        "path": "app4.0/src/components/OrderList.js",
                        "content": "import React from 'react';\n\nconst OrderList = ({ orders, onEdit, onDelete }) => {\n  return (\n    <div>\n      <h2>Order List</h2>\n      <ul>\n        {orders.map((order) => (\n          <li key={order.id}>\n            <span>{order.name}</span>\n            <button onClick={() => onEdit(order.id)}>Edit</button>\n            <button onClick={() => onDelete(order.id)}>Delete</button>\n          </li>\n        ))}\n      </ul>\n    </div>\n  );\n};\n\nexport default OrderList;"
                    },
                    {
                        "path": "app4.0/src/components/AddOrder.js",
                        "content": "import React, { useState } from 'react';\n\nconst AddOrder = ({ onAdd }) => {\n  const [order, setOrder] = useState('');\n\n  const handleSubmit = (event) => {\n    event.preventDefault();\n    if (order) {\n      onAdd(order);\n      setOrder('');\n    }\n  };\n\n  return (\n    <form onSubmit={handleSubmit}>\n      <input\n        type=\"text\"\n        value={order}\n        onChange={(e) => setOrder(e.target.value)}\n        placeholder=\"Add new order\"\n      />\n      <button type=\"submit\">Add Order</button>\n    </form>\n  );\n};\n\nexport default AddOrder;"
                    },
                    {
                        "path": "app4.0/src/App.js",
                        "content": "import React, { useState } from 'react';\nimport AddOrder from './components/AddOrder';\nimport OrderList from './components/OrderList';\nimport './App.css';\n\nfunction App() {\n  const [orders, setOrders] = useState([]);\n\n  const addOrder = (name) => {\n    const newOrder = { id: Date.now(), name };\n    setOrders([...orders, newOrder]);\n  };\n\n  const deleteOrder = (id) => {\n    setOrders(orders.filter((order) => order.id !== id));\n  };\n\n  const editOrder = (id) => {\n    const newName = prompt(\"Enter the new order name:\");\n    if (newName) {\n      setOrders(orders.map(\n        order => order.id === id ? { ...order, name: newName } : order\n      ));\n    }\n  };\n\n  return (\n    <div className=\"App\">\n      <h1>Order Tracker</h1>\n      <AddOrder onAdd={addOrder} />\n      <OrderList orders={orders} onEdit={editOrder} onDelete={deleteOrder} />\n    </div>\n  );\n}\n\nexport default App;"
                    },
                    {
                        "path": "app4.0/src/App.css",
                        "content": "body {\n  font-family: Arial, sans-serif;\n  background-color: #f0f0f0;\n  margin: 0;\n  padding: 0;\n}\n\n.App {\n  max-width: 600px;\n  margin: 50px auto;\n  padding: 20px;\n  background-color: #fff;\n  border-radius: 8px;\n  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n}\n\nbutton {\n  margin-left: 8px;\n  padding: 5px 10px;\n  background-color: #007bff;\n  color: #fff;\n  border: none;\n  border-radius: 4px;\n  cursor: pointer;\n}\n\nbutton:hover {\n  background-color: #0056b3;\n}"
                    }
                ]
            }
            """.trimIndent()
        val response = gson.fromJson(responseString,ProjectCreationResponse::class.java)
        codeGenerationService.generateProjectFiles(projectName,response)
        return ResponseEntity.ok(response)

    }

    @GetMapping("/{projectName}/query")
    fun findRelevantSnippets(
        @RequestParam query: String,
        @PathVariable projectName: String
    ): ResponseEntity<List<String>> {
        val transformedQuery = openAiService.generateEmbedding(query)
        val relevantSnippets = codeRepository.findRelevantSnippets(projectName, transformedQuery, limit = 2)
        return ResponseEntity.ok(relevantSnippets.map { it.filename })
    }

    @PostMapping("/{projectName}/start")
    fun startProject(@PathVariable projectName: String): ResponseEntity<String> {
        try {
            projectRepository.findByName(projectName).get()
            codeGenerationService.runApplication(projectName)
            return ResponseEntity.ok(projectName)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{projectName}/terminate")
    fun terminateProject(@PathVariable projectName: String): ResponseEntity<String> {
        try {
            projectRepository.findByName(projectName).get()
            codeGenerationService.terminateApplication(projectName)
            return ResponseEntity.ok(projectName)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }
}
