import React, {useState} from "react";
import {TextField, Button, Select, MenuItem, Slider, Box} from "@mui/material";

export default function ProjectForm() {
    const [projectName, setProjectName] = useState("");
    const [techStack, setTechStack] = useState("React");
    const [complexity, setComplexity] = useState(50);

    const handleSubmit = () => {
        console.log({projectName, techStack, complexity});
        // Call API to generate the project
    };

    return (
        <Box sx={{p: 3, maxWidth: 500}}>
            <TextField
                fullWidth
                label="Project Name"
                variant="outlined"
                value={projectName}
                onChange={(e) => setProjectName(e.target.value)}
                sx={{mb: 2}}
            />

            <Select
                fullWidth
                value={techStack}
                onChange={(e) => setTechStack(e.target.value)}
                sx={{mb: 2}}
            >
                <MenuItem value="React">React</MenuItem>
                <MenuItem value="Vue">Vue</MenuItem>
                <MenuItem value="Angular">Angular</MenuItem>
            </Select>

            <Slider
                value={complexity}
                onChange={(e, newValue) => setComplexity(newValue as number)}
                step={10}
                marks
                min={10}
                max={100}
            />

            <Button variant="contained" color="primary" onClick={handleSubmit} sx={{mt: 2}}>
                Generate Project
            </Button>
        </Box>
    );
}
