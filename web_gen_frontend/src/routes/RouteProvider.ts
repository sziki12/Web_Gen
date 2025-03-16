import App from "../App";
import {Route} from "../types/types";


export default function RouteProvider(isAuthenticated: boolean) {
    let routes:[Route]  = [
        {
            path: '/',
            element: App(),
            isProtected: true,
        },
    ]

    return routes.filter((route) => {
        return (isAuthenticated || !route.isProtected)
    })
}