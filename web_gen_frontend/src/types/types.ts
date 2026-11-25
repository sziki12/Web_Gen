import {SvgIconClasses} from "@mui/material";
import {RouteObject} from "react-router-dom";
import {JSX} from "react";


export type BasicRoute = RouteObject & {
    isProtected: boolean,
}

export type MenuItemRoute = BasicRoute & {
    name: string,
    icon: JSX.Element
}

export type ComplexRoute = BasicRoute | MenuItemRoute

export type DashboardResponse = {
    projects: DashboardResponseItem[]
}

export type DashboardResponseItem = {
    id: number,
    projectName: string,
    status: string,
}

export type OverviewResponse =
    {
        "id": number,
        "projectName": string,
        "projectStatus": string,
        "creation": Date,
        "lastModification": Date,
        "techStack": string,
        "projectType": string
    }
