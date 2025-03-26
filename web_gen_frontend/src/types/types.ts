import {SvgIconClasses} from "@mui/material";
import {RouteObject} from "react-router-dom";
import {JSX} from "react";


export type BasicRoute = RouteObject & {
    isProtected:boolean,
}

export type MenuItemRoute = BasicRoute & {
    name:string,
    icon: JSX.Element
}

export type ComplexRoute = BasicRoute | MenuItemRoute
