from langchain_core.messages import HumanMessage, SystemMessage, BaseMessage
from langgraph.checkpoint.memory import MemorySaver
from langgraph.graph import START, END, MessagesState, StateGraph
from langchain_scripts.model_provider import ModelProvider
from typing_extensions import TypedDict

class GenerationState(MessagesState):
    """The state of the generation flow."""
    def __init__(self, messages: list[BaseMessage], project_name:str, base_prompt:str, thread_id: str):
        super().__init__(messages=messages)
        self.thread_id = thread_id
        self.project_name = project_name
        self.base_prompt = base_prompt
        self.improve_prompt = ""

    def pretty_print(self):
        for message in self.messages:
            print(f"{message.role}: {message.content}") 


class GenerationState2(TypedDict):
    thread_id: str
    project_name: str
    base_prompt: str
    improve_prompt: str
    messages: list[BaseMessage]         
            
class  GenerationFlow:
    def __init__(self):
        self.model = ModelProvider()
        self.compile()


    def improve(self,state: GenerationState2):
        print("\nImprove  MessageState", state)
        prompt = "Extend the given prompt with probable features, pages, design ideas and other nescessarry detailes to generate a complex web application.\n{user_prompt}".format(user_prompt=state["base_prompt"])
        output = self.model.invoke(prompt)   
        return {"messages": output["messages"], "improve_prompt": output["messages"]}
    
    def generate(self,state: GenerationState2):
        user_prompt = """Root folder: USER_ACCOUNT 
            Project Name: {projectName}
            The folder structure should be Root folder\\Project Name\\rest of the path.
            The Path of the files should be the above mentioned path.
            The root folder already exists.
            The launch and the installation commands will be started from the Project Name folder, you dont have to cd there.
            The folder creation command will be started from the Root folder, you dont have to cd there.
            "Request: {query}""".format(
                projectName=state["project_name"],
                query=state["improve_prompt"])
        print("\nGenerate  MessageState", state)
        state["messages"].append(HumanMessage(user_prompt))
        state["messages"].append(HumanMessage("Please focus on providing the created files as newFiles."))
        response = self.model.invoke_generation({"messages": state["messages"]})
        return {"messages" : response["messages"]}
    
    def compile(self):
        # Define a new graph
        workflow = StateGraph(state_schema=GenerationState2)
        # Define the (single) node in the graph
        workflow.add_edge(START, "improve")
        workflow.add_edge("improve", "model")
        workflow.add_edge("model", END)
        workflow.add_node("improve", self.improve)
        workflow.add_node("model", self.generate)

        # Add memory
        self.memory = MemorySaver()
        self.app = workflow.compile(checkpointer=self.memory)    
    
    def call(self,project_name:str, base_prompt: str, thread_id: str):
        config = {"configurable": {"thread_id": thread_id}}
        output = self.app.invoke({
            "messages": [SystemMessage("developer")],
            "project_name": project_name,
            "base_prompt": base_prompt,
            }, config=config)
        print("\n",output)
        return output
       
