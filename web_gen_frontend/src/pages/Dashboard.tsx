import React from "react";
import {Container, Card, CardContent, Typography, Button} from "@mui/material";
import Grid from '@mui/material/Grid2';
import {DataGrid} from "@mui/x-data-grid";

const rows = [
    {id: 1, name: "Project Alpha", status: "Completed"},
    {id: 2, name: "Project Beta", status: "In Progress"},
    {id: 3, name: "Project Gamma", status: "Pending"},
];

const columns = [
    {field: "id", headerName: "ID", width: 70},
    {field: "name", headerName: "Project Name", flex: 1},
    {field: "status", headerName: "Status", flex: 1},
];

const Dashboard = () => {
    return (
        <Container maxWidth="lg" sx={{mt: 4}}>
            <Typography variant="h4" gutterBottom>
                Dashboard
            </Typography>
            <Grid container spacing={3}>
                <Grid>
                    <Card>
                        <CardContent>
                            <Typography variant="h6">Total Projects</Typography>
                            <Typography variant="h4">3</Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid>
                    <Card>
                        <CardContent>
                            <Typography variant="h6">Active Projects</Typography>
                            <Typography variant="h4">1</Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid>
                    <Card>
                        <CardContent>
                            <Typography variant="h6">Completed Projects</Typography>
                            <Typography variant="h4">1</Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
            <Typography variant="h5" sx={{mt: 4, mb: 2}}>
                Recent Projects
            </Typography>
            <DataGrid rows={rows} columns={columns}/>
            <Button variant="contained" color="primary" sx={{mt: 2}}>
                Generate New Project
            </Button>
        </Container>
    );
};

export default Dashboard;
