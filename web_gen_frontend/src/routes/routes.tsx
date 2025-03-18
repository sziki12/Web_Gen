import {ComplexRoute, MenuItemRoute} from "../types/types"
import LiveView from "../pages/LiveView";
import Dashboard from "../pages/Dashboard";
import ProjectForm from "../pages/ProjectForm";
import {faHouse, faMicrochip, faTableColumns, faTriangleExclamation} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import OverView from "../pages/OverView";
import Root from "../pages/Root";
import ErrorPage from "../pages/ErrorPage";
import ProjectModification from "../pages/ProjectModification";

export default function AppRoutes() {
    const routes: ComplexRoute[] = [
        {
            path: '/live',
            element: LiveView(),
            isProtected: false,
            name: "Live View",
            icon: <FontAwesomeIcon icon={faHouse}/>
        },
        {
            path: '/dashboard',
            element: Dashboard(),
            isProtected: false,
            name: "Dashboard",
            icon: <FontAwesomeIcon icon={faTableColumns}/>
        },
        {
            path: '/generate',
            element: ProjectForm(),
            isProtected: false,
            name: "New Project",
            icon: <FontAwesomeIcon icon={faMicrochip}/>
        },
        {
            path: '/modify',
            element: ProjectModification(),
            isProtected: false,
            name: "Modify Project",
            icon: <FontAwesomeIcon icon={faMicrochip}/>
        },
        {
            path: '/overview',
            element: OverView(),
            isProtected: false,
            name: "Overview",
            icon: <FontAwesomeIcon icon={faMicrochip}/>
        },
        {
            path: '/error',
            element: <ErrorPage/>,
            isProtected: false,
            name: "Error Page",
            icon: <FontAwesomeIcon icon={faTriangleExclamation}/>
        },
    ]

    const rootElement: MenuItemRoute = {
        path: '/',
        element: <Root/>,
        isProtected: false,
        errorElement: <ErrorPage/>,
        name: "Home Page",
        icon: <FontAwesomeIcon icon={faHouse}/>,
        children: routes
    }

    const menuItems = [rootElement, ...routes.filter((route) => {
        return (route as MenuItemRoute).icon !== undefined
    })]

    return {
        routes,
        rootElement,
        menuItems
    }
};