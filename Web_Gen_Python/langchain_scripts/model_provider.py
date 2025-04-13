from langgraph.graph import MessagesState
from langchain.chat_models import init_chat_model


class ModelProvider:
    def __init__(self):
      self.model = init_chat_model("gpt-4o-mini", model_provider="openai")

    def invoke(self, state: MessagesState):
      response = self.model.invoke(state["messages"])
      return {"messages": response}
    
