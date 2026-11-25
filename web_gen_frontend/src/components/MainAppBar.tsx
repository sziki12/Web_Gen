import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import DrawerMenu from "./DrawerMenu";
import ProfileAvatar from "./ProfileAvatar";

export default function MenuAppBar() {
    const toggleMenu = (newOpen: boolean) => {
        setIsMenuOpen(newOpen);
    };
    const [isMenuOpen, setIsMenuOpen] = React.useState(false);
    return (
        <Box sx={{flexGrow: 1}}>
            <AppBar position="static">
                <Toolbar>
                    <>
                        <IconButton
                            size="large"
                            edge="start"
                            color="inherit"
                            aria-label="menu"
                            sx={{mr: 2}}
                            onClick={() => {
                                toggleMenu(true)
                            }}
                        >
                            <MenuIcon/>
                        </IconButton>
                        <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                            Web Gen
                        </Typography>
                        <ProfileAvatar/>

                    </>
                </Toolbar>
                <DrawerMenu open={isMenuOpen} toggleDrawer={toggleMenu}/>
            </AppBar>
        </Box>
    );
}