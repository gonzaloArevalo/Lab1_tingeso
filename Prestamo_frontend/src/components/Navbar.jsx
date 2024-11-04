import * as React from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import Sidemenu from "./Sidemenu";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function Navbar(){
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();

    const toggleDrawer = (open) => (event) => {
        setOpen(open);
    };

    return (
        <Box sx={{ flexGrow: 1 }}>
          <AppBar position="fixed">
            <Toolbar>
              <IconButton
                size="large"
                edge="start"
                color="inherit"
                aria-label="menu"
                sx={{ mr: 2 }}
                onClick={toggleDrawer(true)}
              >
                <MenuIcon />
              </IconButton>
    
              <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                SisPB: Sistema de Prestamos Bancarios
              </Typography>
              <Button color="inherit" onClick={() => navigate('/client/add')}>Register</Button>
            </Toolbar>
          </AppBar>
    
          <Sidemenu open={open} toggleDrawer={toggleDrawer}></Sidemenu>
        </Box>
    );
}