import { useEffect, useState } from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import { TextField, Button, Typography, Box, MenuItem } from "@mui/material";
import userService from "../services/user.service";


export default function Simulation(){
    const [amount, setAmount] = useState('');
    const [term, setTerm] = useState('');
    const [rate, setRate] = useState('');
    const [loanType, setLoanType] = useState('');
    const [simulationResult, setSimulationResult] = useState(null);

    const handleSimulation = async () => {
        try {
            const response = await userService.simulateLoan(amount, term, rate, loanType);
            setSimulationResult(response.data);
        } catch (error) {
            console.error("Error al simular el préstamo:", error);
            setSimulationResult("Hubo un error con la simulación");
        }
    };

    return(
        <Box sx={{ maxWidth: 400, mx: "auto", p: 2 }}>
            <Typography variant="h4" gutterBottom>
                Simulación de Préstamo
            </Typography>
            <TextField
                label="Monto del Préstamo"
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                fullWidth
                margin="normal"
            >
                
            </TextField>
            <TextField
                label="Plazo (en años)"
                type="number"
                value={term}
                onChange={(e) => setTerm(e.target.value)}
                fullWidth
                margin="normal"
            >
        
            </TextField>
            <TextField
                label="Tasa de Interés anual (%)"
                type="number"
                value={rate}
                onChange={(e) => setRate(e.target.value)}
                fullWidth
                margin="normal"
            >
            </TextField>
            <TextField
                label="Tipo de Préstamo"
                select
                value={loanType}
                onChange={(e) => setLoanType(e.target.value)}
                fullWidth
                margin="normal"
            >
                <MenuItem value="first living">Primera vivienda</MenuItem>
                <MenuItem value="second living">Segunda vivienda </MenuItem>
                <MenuItem value="commercial properties">Propiedades comerciales</MenuItem>
                <MenuItem value="remodelation">Remodelacion</MenuItem>

            </TextField>
            <Button variant="contained" color="primary" onClick={handleSimulation} fullWidth sx={{ mt: 2 }}>
                Calcular Simulación
            </Button>

            {simulationResult !== null && (
                <Typography variant="h6" sx={{ mt: 3 }}>
                    La cuota mensual seria de: {simulationResult}
                </Typography>
            )}
        </Box>
    );
}
