import React, {useEffect, useState} from "react";
import {
    Container,
    Typography,
    Card,
    CardContent,
    TextField,
    Select,
    MenuItem,
    Button,
    Divider,
    LinearProgress,
    Accordion,
    AccordionSummary,
    AccordionDetails,
} from "@mui/material";
import Grid from '@mui/material/Grid2';
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {DashboardResponse, OverviewResponse} from "../types/types";
import {nullOverview, ProjectServices} from "../service/ProjectService";
import {useParams} from "react-router-dom";

const ProjectOverview = () => {
    const [project, setProject] = useState({
        name: "Project Alpha",
        id: "123456",
        createdAt: "2025-03-17",
        lastModified: "2025-03-17",
        status: "In Progress",
        techStack: "React, Node.js",
        projectType: "Web LiveView",
        progress: 60,
        prompt: "Generate a full-stack React and Node.js application",
        logs: ["Initialized project", "Generated backend structure", "Generated frontend UI"],
    });

    const params = useParams()

    const handleChange = (e) => {
        setProject({...project, [e.target.name]: e.target.value});
    };
    const projectService = ProjectServices()
    const [content, setContent] = useState<OverviewResponse>(nullOverview)

    const updateOverview = ()=>{
        projectService.getOverview((params.id ?? -1) as number).then((data) => {
            setContent({
                ...data,
                creation: new Date(data.creation),
                lastModification: new Date(data.lastModification),
            })
            console.log(data)
        })
    }

    useEffect(updateOverview, [])

    return (
        <Container maxWidth="md" sx={{mt: 4}}>
            <Typography variant="h4" gutterBottom>
                Project Overview
            </Typography>
            <Card>
                <CardContent>
                    <Grid container spacing={3}>
                        <Grid>
                            <TextField
                                fullWidth
                                label="Project Name"
                                variant="outlined"
                                name="name"
                                value={content.projectName}
                                onChange={handleChange}
                                sx={{mb: 2}}
                            />
                            <Typography>ID: {content.id}</Typography>
                            <Typography>Created At: {content.creation.toTimeString()}</Typography>
                            <Typography>Last Modified: {content.lastModification.toTimeString()}</Typography>
                        </Grid>
                        <Grid>
                            <Typography>Status: {content.projectStatus}</Typography>
                            <Typography>Technology Stack: {content.techStack}</Typography>
                            <Typography>Project Type: {content.projectType}</Typography>
                        </Grid>
                    </Grid>
                    {
                        content.projectStatus === "Started"
                        ?
                            <>
                                <Divider sx={{my: 2}}/>
                                <Accordion>
                                    <AccordionSummary expandIcon={<ExpandMoreIcon/>}>
                                        <Typography variant="h6">Logs & Errors</Typography>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        {project.logs.map((log, index) => (
                                            <Typography key={index} variant="body2">
                                                {log}
                                            </Typography>
                                        ))}
                                    </AccordionDetails>
                                </Accordion>
                            </>
                        :
                        <></>
                    }
                    <Divider sx={{my: 2}}/>
                    {
                        content.projectStatus === "Stopped"
                        ?
                            <>
                                <Button variant="contained" color="primary" sx={{mr: 2}} onClick={()=>{
                                    projectService.startApplication(content.id).then(()=>{updateOverview()})
                                }}>
                                    Start Application
                                </Button>
                            </>
                        :
                            <></>
                    }
                    {
                        content.projectStatus === "Started"
                            ?
                            <>
                                <Button variant="contained" color="primary" sx={{mr: 2}} onClick={()=>{
                                    projectService.terminateApplication(content.id).then(()=>{updateOverview()})
                                }}>
                                    Stop Application
                                </Button>
                            </>
                            :
                            <></>
                    }
                    <Divider sx={{my: 2}}/>
                    <Button variant="contained" color="primary" sx={{mr: 2}}>
                        Save Changes
                    </Button>
                    <Button variant="contained" color="primary" sx={{mr: 2}}>
                        Revert Changes
                    </Button>
                    <Button variant="outlined" color="secondary">
                        Delete Project
                    </Button>
                </CardContent>
            </Card>
        </Container>
    );
};

export default ProjectOverview;
