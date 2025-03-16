import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Dashboard from "../pages/Dashboard";
import ProjectForm from "../pages/ProjectForm";
import ProjectList from "../pages/ProjectList";
import App from "../App";

const AppRoutes = () => (
    <Router>
        <Routes>
            <Route path="/" element={<App/>} />
            <Route path="/generate" element={<ProjectForm />} />
            <Route path="/projects" element={<ProjectList />} />
        </Routes>
    </Router>
);

export default AppRoutes;