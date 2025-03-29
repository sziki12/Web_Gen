import React, {useContext, useEffect, useState} from "react";
import {Container, Card, CardContent, Typography, Button} from "@mui/material";
import Grid from '@mui/material/Grid2';
import {DataGrid} from "@mui/x-data-grid";
import {useNavigate} from "react-router-dom";
import {ProjectServices} from "../service/ProjectService";
import {DashboardResponse} from "../types/types";

const rows = [
    {id: 1, name: "Project Alpha", status: "Running"},
    {id: 2, name: "Project Beta", status: "Stopped"},
    {id: 3, name: "Project Gamma", status: "Error"},
];

const columns = [
    {field: "id", headerName: "ID", width: 70},
    {field: "name", headerName: "Project Name", flex: 1},
    {field: "status", headerName: "Status", flex: 1},
];

const Dashboard = () => {

    const navigate = useNavigate()
    const projectService = ProjectServices()
    const [content, setContent] = useState<DashboardResponse>({projects:[]})
    useEffect(() => {
        projectService.getDashboard().then((data) => {
            setContent(data)
        })
    }, [])

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
            <DataGrid onRowClick={(params, event, details) => {
                console.log(params.row)
                navigate(`/overview/${params.row.id}`)
            }} rows={content.projects} columns={columns}/>
            <Button variant="contained" color="primary" sx={{mt: 2}} onClick={() => {
                navigate("/generate")
            }}>
                Generate New Project
            </Button>
        </Container>
    );
};

export default Dashboard;
