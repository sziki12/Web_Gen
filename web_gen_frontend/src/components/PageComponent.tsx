import React from "react";
import MainAppBar from "../components/MainAppBar"
import AuthState from "../states/AuthState"

export default function PageComponent({children}){

    return <>
        <AuthState>
            <MainAppBar/>
            {children}
        </AuthState>
    </>
}