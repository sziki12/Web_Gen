from langchain_core.messages import HumanMessage, SystemMessage, BaseMessage, AIMessage
from langgraph.checkpoint.memory import MemorySaver
from langgraph.graph import START, END, MessagesState, StateGraph
from langchain_scripts.model_provider import ModelProvider
from typing_extensions import TypedDict

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
        response = self.model.invoke(prompt)   
        state["messages"].append(response["messages"][-1])
        return {"messages": state["messages"], "improve_prompt": response["messages"]}
    
    def generate_layout(self,state: GenerationState2):
        user_prompt = """
            Please generate the project according to the following request.
            The project should be a web application, which is a complex web application.
            Root user folder: USER_ACCOUNT 
            Project Name: {projectName}
            The folder structure should be Root folder\\Project Name\\rest of the path.
            The Path of the files should be the above mentioned path.
            The root folder already exists.
            The launch and the installation commands will be started from the Project Name folder, you dont have to cd there.
            The folder creation command will be started from the Root folder, you dont have to cd there.
            "Request: {query}""".format(
                projectName=state["project_name"],
                query=state["improve_prompt"])
        state["messages"].append(HumanMessage(user_prompt))
        response = self.model.invoke_layout_generation_({"messages": state["messages"]})
        state["messages"].append(response["messages"][-1])

        print("\nGenerate  MessageState", state["messages"])
        return {"messages" : state["messages"]}
    
    def generate_files(self,state: GenerationState2):
        user_prompt = """Please generate the previousely provided files."""
        print("\nGenerate  MessageState", state)
        #state["messages"].append(HumanMessage("Please focus on providing the created files as newFiles."))
        state["messages"].append(HumanMessage(user_prompt))
        response = self.model.invoke_file_generation({"messages": state["messages"]})
        state["messages"].append(response["messages"][-1])

        print("\n---\ngenerate_files", state["messages"])
        return {"messages" : state["messages"]}
    
    def compile(self):
        # Define a new graph
        workflow = StateGraph(state_schema=GenerationState2)
        # Define the (single) node in the graph
        workflow.add_edge(START, "improve")
        workflow.add_edge("improve", "generate_layout")
        workflow.add_edge("generate_layout", "generate_files")
        #workflow.add_edge("generate_files", END)
        #workflow.add_conditional_edges("generate_files", forecast_weather)
        
        workflow.add_node("improve", self.improve)
        workflow.add_node("generate_layout", self.generate_layout)
        workflow.add_node("generate_files", self.generate_files)
        

        # Add memory
        self.memory = MemorySaver()
        self.app = workflow.compile(checkpointer=self.memory)    
    
    def call(self,project_name:str, base_prompt: str, thread_id: str):
        config = {"configurable": {"thread_id": thread_id}}
        output = self.app.invoke({
            "messages": [SystemMessage(content="You are an expert developer, who helps to genearte the user's application ideas.", role="system")],
            "project_name": project_name,
            "base_prompt": base_prompt,
            }, config=config)
        print("\n",output)
        return output
    

    def generate_files_navigation(self, state: GenerationState2) -> bool:
        """Check if there are any ungenerated files in the state."""
        for message in state["messages"]:
            if isinstance(message, HumanMessage) and "newFiles" in message.content:
                return True
        return False
       
