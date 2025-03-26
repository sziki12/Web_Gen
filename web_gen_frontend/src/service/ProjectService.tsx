import {createContext, useContext} from "react";

const ProjectContext = createContext({})

export const ProjectServices = useContext(ProjectContext)

export default function ProjectService({children}) {


    const getDashboard = ()=>{

    }

    const getProjects = ()=>{

    }

    const getOverview = ()=>{

    }

    return <ProjectContext.Provider value={
        {
            getDashboard,
            getProjects,
            getOverview,
        }
    }>
        {children}
    </ProjectContext.Provider>
}