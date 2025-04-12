from langchain_core.messages import HumanMessage
from langgraph.checkpoint.memory import MemorySaver
from langgraph.graph import START, MessagesState, StateGraph
from langchain_scripts.model_provider import ModelProvider


class  GenerationFlow:
    def __init__(self):
        self.model = ModelProvider()
        self.compile()
    
    def compile(self):
        # Define a new graph
        workflow = StateGraph(state_schema=MessagesState)
        # Define the (single) node in the graph
        workflow.add_edge(START, "model")
        workflow.add_node("model", self.model.invoke)

        # Add memory
        self.memory = MemorySaver()
        self.app = workflow.compile(checkpointer=self.memory)
    
    def call(self, input_messages: str, thread_id: str):
        # Invoke the graph with the input messages
        config = {"configurable": {"thread_id": thread_id}}
        input = [HumanMessage(input_messages)]
        output = self.app.invoke({"messages": input}, config=config)
        output["messages"][-1].pretty_print()
        print("Memory: ",self.memory.get(config))
        return output["messages"][-1].content
