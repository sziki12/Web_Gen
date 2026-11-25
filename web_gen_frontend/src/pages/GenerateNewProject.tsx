import React, {useState} from "react";
import {
    TextField,
    Button,
    Slider,
    Typography,
    Card,
    CardContent,
    Container, LinearProgress
} from "@mui/material";
import Grid from "@mui/material/Grid2";
import {ProjectServices} from "../service/ProjectService";

export default function GenerateNewProject() {
    const projectService = ProjectServices()
    const [response, setResponse] = useState("")
    const [project, setProject] = useState({
        projectName: "",
        generationPrompt: "",
        techStack: "",
        progress: 0,
        complexity: 50,
    })
    const handleSubmit = () => {
        console.log({...project})
        projectService.generateProject(project.projectName, project.generationPrompt+"; Tech Stack: "+project.techStack).then(responseCode => {
                if (responseCode === 200) {
                    setTimeout(() => {
                        setResponse("Request Submitted")
                        for (let i = 0; i < 10; i++) {
                            setTimeout(() => {
                                setProject({
                                    ...project,
                                    progress: 10 + i * 10
                                })
                            }, 500 * i)
                        }
                    }, 500)
                } else {
                    console.log("Error, response code: "+responseCode)
                }
            }
        )


        // TODO Call API to generate the project
    };

    const handleEdit = (property: string, e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setProject(prevState => {
                return ({
                    ...prevState,
                    [property]: e.target.value
                })
            }
        )
    }

    return (
        <Container sx={{p: 3, maxWidth: 500}}>
            <Typography variant="h4" gutterBottom>
                Generate Project
            </Typography>
            <TextField
                fullWidth
                label="Project Name"
                variant="outlined"
                value={project.projectName}
                onChange={(e) => handleEdit("projectName", e)}
                sx={{mb: 2}}
            />

            <Card>
                <CardContent>
                    <Typography variant="h6" gutterBottom>
                        Provide instructions to generate your project:
                    </Typography>
                    <TextField
                        fullWidth
                        multiline
                        rows={4}
                        variant="outlined"
                        placeholder="Describe your applicaion idea, what you need it to do and  look like:"
                        value={project.generationPrompt}
                        onChange={(e) => handleEdit("generationPrompt", e)}
                        sx={{mb: 2}}
                    />
                    <TextField
                        fullWidth
                        multiline
                        rows={1}
                        variant="outlined"
                        placeholder="What is the requiered  tech stack:"
                        value={project.techStack}
                        onChange={(e) => handleEdit("techStack", e)}
                        sx={{mb: 2}}
                    />
                </CardContent>
            </Card>
            <Typography variant="h6" gutterBottom>
                Project Complexity:
            </Typography>
            <Slider
                value={project.complexity}
                onChange={(e, newValue) => setProject({
                    ...project,
                    "complexity": newValue as number
                })}
                step={10}
                marks
                min={10}
                max={100}
            />
            {response && (
                <>
                    <Typography variant="body1" color="success.main" sx={{mt: 2}}>
                        {response}
                    </Typography>
                    <Typography variant="h6">Generation Progress</Typography>
                    <LinearProgress variant="determinate" value={project.progress} sx={{my: 2}}/>
                    <Typography>{project.progress}% Completed</Typography>
                </>
            )}
            <Button disabled={response !== ""} variant="contained" color="primary" onClick={handleSubmit} sx={{mt: 2}}>
                Start Generation
            </Button>
        </Container>
    );
}
