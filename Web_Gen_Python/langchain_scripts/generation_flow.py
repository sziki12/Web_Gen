from langchain_core.messages import HumanMessage, SystemMessage
from langgraph.checkpoint.memory import MemorySaver
from langgraph.graph import START, MessagesState, StateGraph
from langchain_scripts.model_provider import ModelProvider

class  GenerationFlow:
    def __init__(self):
        self.model = ModelProvider()
        self.compile()


    def improve(self,user_prompt:str):
        prompt = "Extend the given prompt with probable features, pages, design ideas and other nescessarry detailes to generate a complex web application.\n{user_prompt}".format(user_prompt)
        output = self.model.invoke(prompt)   
    
    def compile(self):
        # Define a new graph
        workflow = StateGraph(state_schema=MessagesState)
        # Define the (single) node in the graph
        workflow.add_edge(START, "improve")
        workflow.add_edge("improve", "model")
        workflow.add_node("improve", self.improve)
        workflow.add_node("model", self.model.invoke_generation)

        # Add memory
        self.memory = MemorySaver()
        self.app = workflow.compile(checkpointer=self.memory)
    
    def call(self,project_name:str, input_messages: str, thread_id: str):
        base_prompt = "You are a developer. Create or modify an application based on given input."
        user_prompt = """Root folder: USER_ACCOUNT 
            Project Name: {projectName}
            The folder structure should be Root folder\\Project Name\\rest of the path.
            The Path of the files should be the above mentioned path.
            The root folder already exists.
            The launch and the installation commands will be started from the Project Name folder, you dont have to cd there.
            The folder creation command will be started from the Root folder, you dont have to cd there.
            "Request: {query}""".format(
                projectName=project_name,
                query=input_messages)
        config = {"configurable": {"thread_id": thread_id}}
        input = [
            SystemMessage(base_prompt),
            HumanMessage(user_prompt)]
        output = self.app.invoke({"messages": input}, config=config)
        output["messages"][-1].pretty_print()
        return output["messages"][-1]
