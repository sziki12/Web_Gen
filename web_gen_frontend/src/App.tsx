import React from 'react';
import logo from './logo.svg';
import './App.css';
import Iframe from 'react-iframe'
import ProjectForm from "./pages/ProjectForm";
import PageComponent from "./components/PageComponent";

function App() {
    return (
        <PageComponent>
            <div className="App">
                <div className={"App-content"}>
                    <ProjectForm></ProjectForm>
                </div>
                <Iframe
                    className={"App-content"}
                    url="http://localhost:3000"
                    height={"900"}
                    width={"50%"}
                    id="iframe"
                    display="block"
                    position="relative"/>
            </div>
        </PageComponent>
    );
}

export default App;
