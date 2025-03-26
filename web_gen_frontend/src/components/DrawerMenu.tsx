import * as React from 'react';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import Divider from '@mui/material/Divider';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import AppRoutes from "../routes/routes";
import {MenuItemRoute} from "../types/types";
import {useNavigate} from "react-router-dom";

export default function DrawerMenu({open, toggleDrawer}) {
    const navigate = useNavigate()
    const items = AppRoutes().menuItems as MenuItemRoute[]
    const DrawerList = (
        <Box sx={{width: 250}} role="presentation">
            <List>
                {items.map((route) => (
                    <ListItem key={route.name} disablePadding>
                        <ListItemButton onClick={()=>{
                            navigate(route.path ?? "/error")
                            toggleDrawer(false)
                        }}>
                            <ListItemIcon>
                                {route.icon}
                            </ListItemIcon>
                            <ListItemText primary={route.name}/>
                        </ListItemButton>
                    </ListItem>
                ))}
            </List>
        </Box>
    );

    return (
        <div>
            <Drawer open={open} onClose={() => toggleDrawer(false)}>
                {DrawerList}
            </Drawer>
        </div>
    );
}