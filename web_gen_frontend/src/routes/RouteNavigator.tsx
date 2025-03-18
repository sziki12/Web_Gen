import {AuthData} from "../states/AuthState";
import {createBrowserRouter, RouteObject, RouterProvider} from "react-router-dom";
import AppRoutes from "./routes";


export default function RouteNavigator() {

    const authData = AuthData()
    const isAuthenticated = authData.authenticated

    const routes = AppRoutes().routes
    const rootElement:RouteObject = AppRoutes().rootElement

    const availableRoutes = routes.filter((route) => {
        return (isAuthenticated || !route.isProtected)
    })

    return (
        <RouterProvider router={createBrowserRouter([rootElement])}/>
    )

}