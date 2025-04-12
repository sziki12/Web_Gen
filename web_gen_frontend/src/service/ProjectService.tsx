import {createContext, useContext} from "react";
import {DashboardResponse, OverviewResponse} from "../types/types";

interface IProjectService {
    getDashboard: () => Promise<DashboardResponse>,
    getProjects: () => Promise<any>,
    getOverview: (projectId: number) => Promise<OverviewResponse>,
    startApplication: (projectId: number) => Promise<number>,
    terminateApplication: (projectId: number) => Promise<number>,
    generateProject: (projectName: string, prompt: string) => Promise<number>,
    modifyProject: (projectId: number, prompt: string) => Promise<number>,
}

export const nullOverview = {
    id: -1,
    creation: new Date(),
    projectName: "",
    projectStatus: "",
    projectType: "",
    lastModification: new Date(),
    techStack: "",
}

const ProjectContext = createContext<IProjectService>({
    generateProject(projectName: string, prompt: string): Promise<number> {
        return Promise.resolve(0);
    }, modifyProject(projectId: number, prompt: string): Promise<number> {
        return Promise.resolve(0);
    },
    startApplication(projectId: number): Promise<number> {
        return Promise.resolve(-1);
    }, terminateApplication(projectId: number): Promise<number> {
        return Promise.resolve(-1);
    },
    getDashboard(): Promise<DashboardResponse> {
        return Promise.resolve({projects: []});
    }, getOverview(projectId: number): Promise<OverviewResponse> {
        return Promise.resolve(nullOverview);
    }, getProjects(): Promise<any> {
        return Promise.resolve([]);
    }
})

export const ProjectServices = () => useContext(ProjectContext)

export default function ProjectService({children}) {

    const basePath = "http://localhost:8080/api"

    const getDashboard = async (): Promise<DashboardResponse> => {
        const path = `${basePath}/project/dashboard`
        const response = await fetch(path)
        if (response.ok) {
            const json = await response.json()
            console.log(json)
            return json
        } else
            return {projects: []}
    }

    const getProjects = async (): Promise<any> => {

    }

    const getOverview = async (projectId: number): Promise<OverviewResponse> => {
        const path = `${basePath}/project/overview/${projectId}`
        const response = await fetch(path)
        if (response.ok)
            return await response.json()
        else
            return nullOverview
    }

    const generateProject = async (projectName: string, prompt: string) => {
        const path = `${basePath}/code/${projectName}/generate?prompt=${prompt}`
        const response = await fetch(path, {
            method: "POST"
        })
        return response.status
    }

    const modifyProject = async (projectId: number, prompt: string) => {
        const path = `${basePath}/code/${projectId}/modify?prompt=${prompt}`
        const response = await fetch(path, {
            method: "POST",
        })
        return response.status
    }

    const startApplication = async (projectId: number): Promise<number> => {
        const path = `${basePath}/code/${projectId}/start`
        const response = await fetch(path, {
            method: "POST"
        })
        return response.status

    }

    const terminateApplication = async (projectId: number): Promise<number> => {
        const path = `${basePath}/code/${projectId}/terminate`
        const response = await fetch(path, {
            method: "POST"
        })
        return response.status
    }

    return <ProjectContext.Provider value={
        {
            getDashboard,
            getProjects,
            getOverview,
            startApplication,
            terminateApplication,
            generateProject,
            modifyProject,
        }
    }>
        {children}
    </ProjectContext.Provider>
}