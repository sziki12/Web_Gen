# Python 3 server example
from http.server import BaseHTTPRequestHandler, HTTPServer
from dotenv import load_dotenv
import json
from types import SimpleNamespace
from langchain_scripts.generation_flow import GenerationFlow

hostName = "localhost"
serverPort = 9000
class MyServer(BaseHTTPRequestHandler):
    
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()
        
        self.wfile.write(bytes(json.dumps({"text": "körte"}), "utf-8"))

    def do_POST(self):
        params = self.load_params()

        output = flow.call(params.project_name, params.prompt,params.thread_id)
        print("\nResponse  MessageState", output)
        formated_output = {
            "response":output.content,
            "data":output.additional_kwargs,
        }
        print(json.dumps(formated_output))
        self.send_response(200)
        self.send_header('Content-type','application/json')
        self.end_headers()
        self.wfile.write(bytes(json.dumps(formated_output), "utf8"))  


    def load_params(self):
        content_len = int(self.headers.get('Content-Length'))
        request_body = self.rfile.read(content_len).decode('utf-8')
        params = json.loads(request_body, object_hook=lambda d: SimpleNamespace(**d))
        return params  

if __name__ == "__main__": 
    load_dotenv("environment_variables.env")       
    flow = GenerationFlow()
    webServer = HTTPServer((hostName, serverPort), MyServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")