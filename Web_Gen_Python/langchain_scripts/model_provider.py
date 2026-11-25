from langgraph.graph import MessagesState
from langchain_core.messages import HumanMessage, SystemMessage, BaseMessage, AIMessage
from langchain.chat_models import init_chat_model
from langchain_scripts.message_schemas.generation_request import layout_generation_request_schema, file_generation_request_schema
from langchain_scripts.message_schemas.modification_request import modification_request_schema

class ModelProvider:
    def __init__(self):
      self.model = init_chat_model("gpt-4o-mini", model_provider="openai")

    def invoke(self, state: MessagesState) -> dict:
      response = self.model.invoke(state)
      return {"messages": [response]}
    
    def invoke_layout_generation_(self, state: MessagesState) -> dict:
      self.structured_model = self.model.with_structured_output(layout_generation_request_schema)
      response = self.structured_model.invoke(state["messages"])
      return {"messages": [response]}
    
    def invoke_file_generation(self, state: MessagesState) -> dict:
      self.structured_model = self.model.with_structured_output(file_generation_request_schema)
      response = self.structured_model.invoke(state["messages"])
      return {"messages": [response]}
    
    def invoke_modification(self, state: MessagesState) -> dict:
      self.structured_model = self.model.with_structured_output(modification_request_schema)
      response = self.structured_model.invoke(state["messages"]) 
      return {"messages": [response]}
    
    
