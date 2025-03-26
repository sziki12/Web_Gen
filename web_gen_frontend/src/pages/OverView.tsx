import React, {useState} from "react";
import {
    Container,
    Typography,
    Card,
    CardContent,
    Grid,
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
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

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

    const handleChange = (e) => {
        setProject({...project, [e.target.name]: e.target.value});
    };

    return (
        <Container maxWidth="md" sx={{mt: 4}}>
            <Typography variant="h4" gutterBottom>
                Project Overview
            </Typography>
            <Card>
                <CardContent>
                    <Grid container spacing={3}>
                        <Grid item xs={12} md={6}>
                            <TextField
                                fullWidth
                                label="Project Name"
                                variant="outlined"
                                name="name"
                                value={project.name}
                                onChange={handleChange}
                                sx={{mb: 2}}
                            />
                            <Typography>ID: {project.id}</Typography>
                            <Typography>Created At: {project.createdAt}</Typography>
                            <Typography>Last Modified: {project.lastModified}</Typography>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <Select
                                fullWidth
                                value={project.status}
                                onChange={handleChange}
                                name="status"
                                sx={{mb: 2}}
                            >
                                <MenuItem value="In Progress">In Progress</MenuItem>
                                <MenuItem value="Completed">Completed</MenuItem>
                                <MenuItem value="Failed">Failed</MenuItem>
                            </Select>
                            <Typography>Technology Stack: {project.techStack}</Typography>
                            <Typography>Project Type: {project.projectType}</Typography>
                        </Grid>
                    </Grid>
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
                    <Divider sx={{my: 2}}/>
                    <Button variant="contained" color="primary" sx={{mr: 2}}>
                        Save Changes
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
