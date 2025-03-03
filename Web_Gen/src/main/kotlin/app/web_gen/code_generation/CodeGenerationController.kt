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
    fun generateCode(@RequestParam prompt: String, @PathVariable projectName: String): ResponseEntity<String> {
        //TODO Move to OpenAI Service projectGeneration
        val response = openAiService.structuredResponse(prompt,ProjectCreationResponse.responseFormat)
        /*val response = """
            {"textResponse":"This application will allow users to track, add, remove, and modify orders. It will consist of a basic React front-end using functional components and React Hooks for managing state.","projectName":"order-tracker","codeToGenerate":"npx create-react-app order-tracker --template typescript\ncd order-tracker\nnpm install\n","codeToRun":"npm start","newFiles":[{"path":"src/components/OrderList.tsx","content":"import React from 'react';\nimport { Order } from '../types';\n\ninterface OrderListProps {\n  orders: Order[];\n  onDelete: (id: number) => void;\n  onEdit: (order: Order) => void;\n}\n\nconst OrderList: React.FC<OrderListProps> = ({ orders, onDelete, onEdit }) => {\n  return (\n    <div>\n      <h2>Order List</h2>\n      <ul>\n        {orders.map(order => (\n          <li key={order.id}>\n            <span>{order.name} - {order.quantity}</span>\n            <button onClick={() => onEdit(order)}>Edit</button>\n            <button onClick={() => onDelete(order.id)}>Delete</button>\n          </li>\n        ))}\n      </ul>\n    </div>\n  );\n}\n\nexport default OrderList;\n"},{"path":"src/components/OrderForm.tsx","content":"import React, { useState, useEffect } from 'react';\nimport { Order } from '../types';\n\ninterface OrderFormProps {\n  order?: Order;\n  onSave: (order: Order) => void;\n}\n\nconst OrderForm: React.FC<OrderFormProps> = ({ order, onSave }) => {\n  const [name, setName] = useState(order ? order.name : '');\n  const [quantity, setQuantity] = useState(order ? order.quantity : 0);\n  const [id, setId] = useState(order ? order.id : Math.floor(Math.random() * 1000));\n\n  useEffect(() => {\n    if (order) {\n      setName(order.name);\n      setQuantity(order.quantity);\n      setId(order.id);\n    }\n  }, [order]);\n\n  const handleSubmit = (event: React.FormEvent) => {\n    event.preventDefault();\n    onSave({ id, name, quantity });\n  };\n\n  return (\n    <form onSubmit={handleSubmit}>\n      <div>\n        <label>Name:</label>\n        <input type=\"text\" value={name} onChange={e => setName(e.target.value)} />\n      </div>\n      <div>\n        <label>Quantity:</label>\n        <input type=\"number\" value={quantity} onChange={e => setQuantity(parseInt(e.target.value))} />\n      </div>\n      <button type=\"submit\">Save</button>\n    </form>\n  );\n};\n\nexport default OrderForm;\n"},{"path":"src/types.ts","content":"export interface Order {\n  id: number;\n  name: string;\n  quantity: number;\n}\n"},{"path":"src/App.tsx","content":"import React, { useState } from 'react';\nimport OrderList from './components/OrderList';\nimport OrderForm from './components/OrderForm';\nimport { Order } from './types';\n\nconst App: React.FC = () => {\n  const [orders, setOrders] = useState<Order[]>([]);\n  const [currentOrder, setCurrentOrder] = useState<Order | undefined>(undefined);\n\n  const addOrder = (order: Order) => {\n    setOrders([...orders, order]);\n    setCurrentOrder(undefined);\n  };\n\n  const editOrder = (order: Order) => {\n    setOrders(orders.map(o => (o.id === order.id ? order : o)));\n    setCurrentOrder(undefined);\n  };\n\n  const deleteOrder = (id: number) => {\n    setOrders(orders.filter(order => order.id !== id));\n  };\n\n  const handleEditClick = (order: Order) => {\n    setCurrentOrder(order);\n  };\n\n  return (\n    <div>\n      <h1>Order Tracker</h1>\n      <OrderForm order={currentOrder} onSave={currentOrder ? editOrder : addOrder} />\n      <OrderList orders={orders} onDelete={deleteOrder} onEdit={handleEditClick} />\n    </div>\n  );\n};\n\nexport default App;\n"}],"modifiedFiles":[{"path":"src/index.tsx","oldContent":"import React from 'react';\nimport ReactDOM from 'react-dom';\nimport './index.css';\nimport App from './App';\nimport reportWebVitals from './reportWebVitals';\n\nReactDOM.render(\n  <React.StrictMode>\n    <App />\n  </React.StrictMode>,\n  document.getElementById('root')\n);\n\nreportWebVitals();","newContent":"import React from 'react';\nimport ReactDOM from 'react-dom/client';\nimport './index.css';\nimport App from './App';\n\nconst root = ReactDOM.createRoot(\n  document.getElementById('root') as HTMLElement\n);\n\nroot.render(\n  <React.StrictMode>\n    <App />\n  </React.StrictMode>\n);\n"}]}
            """.trimIndent()*/
        val modelResponse = gson.fromJson(response, ProjectCreationResponse::class.java)
        codeGenerationService.generateProjectFiles(projectName,modelResponse)
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
