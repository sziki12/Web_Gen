import PageComponent from "../components/PageComponent";
import {Outlet, useLocation} from "react-router-dom";


export default function Root() {
    const location = useLocation();
    return <PageComponent>
        {
            (location.pathname === "/")
                ?
                <>
                    <RootContent key={"root_content"}/>
                </>
                :
                <>
                    <Outlet key={"outlet"}/>
                </>
        }
    </PageComponent>
}

function RootContent() {
    return <></>
}
