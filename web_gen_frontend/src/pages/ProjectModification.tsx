import React, {useState} from "react";
import {
    Container,
    Typography,
    Card,
    CardContent,
    TextField,
    Button,
} from "@mui/material";
import Grid from "@mui/material/Grid2";

const ProjectModification = () => {
    const [modificationPrompt, setModificationPrompt] = useState("");
    const [response, setResponse] = useState("");

    const handleSubmit = async () => {
        // Simulate API call
        setResponse("Processing your request...");
        setTimeout(() => {
            setResponse("Project modifications have been applied successfully.");
        }, 2000);
    };

    return (
        <Container maxWidth="md" sx={{mt: 4}}>
            <Typography variant="h4" gutterBottom>
                Modify Project
            </Typography>
            <Card>
                <CardContent>
                    <Typography variant="h6" gutterBottom>
                        Provide instructions for modifying your project:
                    </Typography>
                    <TextField
                        fullWidth
                        multiline
                        rows={4}
                        variant="outlined"
                        placeholder="Describe the modifications you need..."
                        value={modificationPrompt}
                        onChange={(e) => setModificationPrompt(e.target.value)}
                        sx={{mb: 2}}
                    />
                    <Grid container spacing={2}>
                        <Grid>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleSubmit}
                            >
                                Submit Modification
                            </Button>
                        </Grid>
                        <Grid>
                            <Button variant="outlined" color="secondary">
                                Cancel
                            </Button>
                        </Grid>
                    </Grid>
                    {response && (
                        <Typography variant="body1" color="success.main" sx={{mt: 2}}>
                            {response}
                        </Typography>
                    )}
                </CardContent>
            </Card>
        </Container>
    );
};

export default ProjectModification;
