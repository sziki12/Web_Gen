package app.web_gen.test_response

val testModifyResponse = """
                {
                    "textResponse": "To fulfill your request, we need to extend the existing application with functionality to track quantity and price for each order. We will modify the `AddOrder` component to include additional inputs for these new fields, and update the `OrderList` component and `App` component to handle and display these additional data points.\n\n### Modifications made:\n- Extended `AddOrder` to include inputs for `quantity` and `price`.\n- Updated `App` to handle adding and displaying `quantity` and `price`.\n- Modified `OrderList` to display `quantity` and `price` information.",
                    "codeToGenerate": "No additional npm packages required.",
                    "newFiles": [],
                    "modifiedFiles": [
                        {
                            "path": "app4.0/src/components/AddOrder.js",
                            "oldContent": "import React, { useState } from 'react';\n\nconst AddOrder = ({ onAdd }) => {\n  const [order, setOrder] = useState('');\n\n  const handleSubmit = (event) => {\n    event.preventDefault();\n    if (order) {\n      onAdd(order);\n      setOrder('');\n    }\n  };\n\n  return (\n    <form onSubmit={handleSubmit}>\n      <input\n        type=\"text\"\n        value={order}\n        onChange={(e) => setOrder(e.target.value)}\n        placeholder=\"Add new order\"\n      />\n      <button type=\"submit\">Add Order</button>\n    </form>\n  );\n};\n\nexport default AddOrder;\n",
                            "newContent": "import React, { useState } from 'react';\n\nconst AddOrder = ({ onAdd }) => {\n  const [orderDetails, setOrderDetails] = useState({ name: '', quantity: '', price: '' });\n\n  const handleSubmit = (event) => {\n    event.preventDefault();\n    const { name, quantity, price } = orderDetails;\n    if (name && quantity && price) {\n      onAdd(orderDetails);\n      setOrderDetails({ name: '', quantity: '', price: '' });\n    }\n  };\n\n  const handleChange = (e) => {\n    const { name, value } = e.target;\n    setOrderDetails({ ...orderDetails, [name]: value });\n  };\n\n  return (\n    <form onSubmit={handleSubmit}>\n      <input\n        type=\"text\"\n        name=\"name\"\n        value={orderDetails.name}\n        onChange={handleChange}\n        placeholder=\"Order Name\"\n      />\n      <input\n        type=\"number\"\n        name=\"quantity\"\n        value={orderDetails.quantity}\n        onChange={handleChange}\n        placeholder=\"Quantity\"\n      />\n      <input\n        type=\"number\"\n        step=\"0.01\"\n        name=\"price\"\n        value={orderDetails.price}\n        onChange={handleChange}\n        placeholder=\"Price\"\n      />\n      <button type=\"submit\">Add Order</button>\n    </form>\n  );\n};\n\nexport default AddOrder;\n"
                        },
                        {
                            "path": "app4.0/src/components/OrderList.js",
                            "oldContent": "import React from 'react';\n\nconst OrderList = ({ orders, onEdit, onDelete }) => {\n  return (\n    <div>\n      <h2>Order List</h2>\n      <ul>\n        {orders.map((order) => (\n          <li key={order.id}>\n            <span>{order.name}</span>\n            <button onClick={() => onEdit(order.id)}>Edit</button>\n            <button onClick={() => onDelete(order.id)}>Delete</button>\n          </li>\n        ))}\n      </ul>\n    </div>\n  );\n};\n\nexport default OrderList;\n",
                            "newContent": "import React from 'react';\n\nconst OrderList = ({ orders, onEdit, onDelete }) => {\n  return (\n    <div>\n      <h2>Order List</h2>\n      <ul>\n        {orders.map((order) => (\n          <li key={order.id}>\n            <span>Name: {order.name}, Quantity: {order.quantity}, Price: {order.price}$</span>\n            <button onClick={() => onEdit(order.id)}>Edit</button>\n            <button onClick={() => onDelete(order.id)}>Delete</button>\n          </li>\n        ))}\n      </ul>\n    </div>\n  );\n};\n\nexport default OrderList;\n"
                        },
                        {
                            "path": "app4.0/src/App.js",
                            "oldContent": "import React, { useState } from 'react';\nimport AddOrder from './components/AddOrder';\nimport OrderList from './components/OrderList';\nimport './App.css';\n\nfunction App() {\n  const [orders, setOrders] = useState([]);\n\n  const addOrder = (name) => {\n    const newOrder = { id: Date.now(), name };\n    setOrders([...orders, newOrder]);\n  };\n\n  const deleteOrder = (id) => {\n    setOrders(orders.filter((order) => order.id !== id));\n  };\n\n  const editOrder = (id) => {\n    const newName = prompt(\"Enter the new order name:\");\n    if (newName) {\n      setOrders(orders.map(\n        order => order.id === id ? { ...order, name: newName } : order\n      ));\n    }\n  };\n\n  return (\n    <div className=\"App\">\n      <h1>Order Tracker</h1>\n      <AddOrder onAdd={addOrder} />\n      <OrderList orders={orders} onEdit={editOrder} onDelete={deleteOrder} />\n    </div>\n  );\n}\n\nexport default App;\n",
                            "newContent": "import React, { useState } from 'react';\nimport AddOrder from './components/AddOrder';\nimport OrderList from './components/OrderList';\nimport './App.css';\n\nfunction App() {\n  const [orders, setOrders] = useState([]);\n\n  const addOrder = ({ name, quantity, price }) => {\n    const newOrder = { id: Date.now(), name, quantity, price };\n    setOrders([...orders, newOrder]);\n  };\n\n  const deleteOrder = (id) => {\n    setOrders(orders.filter((order) => order.id !== id));\n  };\n\n  const editOrder = (id) => {\n    const newDetails = prompt(\"Enter the new details (name,quantity,price) separated by commas:\");\n    if (newDetails) {\n      const [newName, newQuantity, newPrice] = newDetails.split(',').map(val => val.trim());\n      if (newName && newQuantity && newPrice) {\n        setOrders(orders.map(\n          order => order.id === id ? { ...order, name: newName, quantity: newQuantity, price: newPrice } : order\n        ));\n      }\n    }\n  };\n\n  return (\n    <div className=\"App\">\n      <h1>Order Tracker</h1>\n      <AddOrder onAdd={addOrder} />\n      <OrderList orders={orders} onEdit={editOrder} onDelete={deleteOrder} />\n    </div>\n  );\n}\n\nexport default App;\n"
                        }
                    ]
                }
            """.trimIndent()


val testGenerateResponse = """
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