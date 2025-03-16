import {JSX} from "react";
import {SvgIconClasses} from "@mui/material";


export type Route = {
    path:String,
    element:JSX.Element,
    isProtected:boolean,
    icon?:SvgIconClasses

}
