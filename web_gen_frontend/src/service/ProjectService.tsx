import {createContext, useContext} from "react";
import {DashboardResponse, OverviewResponse} from "../types/types";

interface IProjectService {
    getDashboard: ()=> Promise<DashboardResponse>,
    getProjects: () => Promise<any>,
    getOverview: (projectId: number) => Promise<OverviewResponse>,
}

export const nullOverview = {
    id:-1,
    creation: new Date(),
    projectName: "",
    projectStatus: "",
    projectType:"",
    lastModification:new Date(),
    techStack:"",
}

const ProjectContext = createContext<IProjectService>({
    getDashboard(): Promise<DashboardResponse> {
        return Promise.resolve({projects:[]});
    }, getOverview(projectId: number): Promise<OverviewResponse> {
        return Promise.resolve(nullOverview);
    }, getProjects(): Promise<any> {
        return Promise.resolve([]);
    }
})

export const ProjectServices = () => useContext(ProjectContext)

export default function ProjectService({children}) {

    const basePath = "http://localhost:8080/api"

    const getDashboard = async ():Promise<DashboardResponse> => {
        const path = `${basePath}/project/dashboard`
        const response = await fetch(path)
        if (response.ok) {
            const json = await response.json()
            console.log(json)
            return json
        } else
            return {projects:[]}
    }

    const getProjects = async (): Promise<any> => {

    }

    const getOverview = async (projectId: number):Promise<OverviewResponse> => {
        const path = `${basePath}/project/overview/${projectId}`
        const response = await fetch(path)
        if (response.ok)
            return await response.json()
        else
            return nullOverview
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