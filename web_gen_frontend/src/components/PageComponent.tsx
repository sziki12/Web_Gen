import React from "react";
import MainAppBar from "../components/MainAppBar"
import AuthState from "../states/AuthState"
import ProjectService from "../service/ProjectService";

export default function PageComponent({children}){

    return <>
        <AuthState>
            <ProjectService>
                <MainAppBar/>
                {children}
            </ProjectService>
        </AuthState>
    </>
}