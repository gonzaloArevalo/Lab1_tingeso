import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import QueueIcon from '@mui/icons-material/Queue';
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import requestService from "../services/request.service";
import { useParams } from 'react-router-dom';

const RequestList = ({ userId }) => {
    const [requests, setRequests] = useState([]);
    const navigate = useNavigate();

    const init = () => {
        if (userId) {
            requestService
                .getRequestsByUserId(userId)
                .then((response) => {
                    console.log(`Mostrando solicitudes del usuario ${userId}.`, response.data);
                    setRequests(response.data);
                })
                .catch((error) => {
                    console.log(
                      `Error al intentar mostrar las solicitudes del usuario ${userId}.`,
                      error
                    );
                });
        }
        else{
            requestService
                .getAllRequests()
                .then((response) => {
                    console.log("Mostrando listado de todas las solicitudes.", response.data);
                    setRequests(response.data);
                })
                .catch((error) => {
                    console.log(
                      "Se ha producido un error al intentar mostrar listado de todas las solicitudes.",
                      error
                    );
                });
        } 
    };
    

    useEffect(() => {
        init();
    }, [userId]);

    const handleDelete = (id) =>{
        console.log("Printing id", id);
        const confirmDelete = window.confirm(
          "¿Esta seguro que desea borrar esta solicitud?"
        );
        if (confirmDelete) {
            requestService
              .deleteRequestById(id)
              .then((response) => {
                console.log("solicitud eliminada.", response.data);
                init();
              })
              .catch((error) => {
                console.log(
                  "Se ha producido un error al intentar eliminar la solicitud",
                  error
                  );
              });
          }
    }


    const handleEdit = (id) => {
        console.log("Printing id", id);
        navigate(`/request/edit/${id}`);
    };

    return(
        <TableContainer component={Paper}>
            <br />
          <Link
            to="/request/add"
            style={{ textDecoration: "none", marginBottom: "1rem" }}
          >
            <Button
              variant="contained"
              color="primary"
              startIcon={<QueueIcon />}
            >
              Añadir solicitud
            </Button>
          </Link>
          <br /> <br />
          <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
            <TableHead>
                <TableRow>
                    <TableCell align="left" sx={{ fontWeight: "bold" }}>
                        Monto
                    </TableCell>
                    <TableCell align="left" sx={{ fontWeight: "bold" }}>
                        Plazo
                    </TableCell>
                    <TableCell align="left" sx={{ fontWeight: "bold" }}>
                        Tasa de Interes
                    </TableCell>
                    <TableCell align="left" sx={{ fontWeight: "bold" }}>
                        Tipo de prestamo
                    </TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {requests.map((request) =>(
                    <TableRow
                    key={user.id}
                    sx={{ "&:last-child td, &:last-child th": { border: 0 }}}
                    >
                        <TableCell align="left">{request.amount}</TableCell>
                        <TableCell align="left">{request.term}</TableCell>
                        <TableCell align="right">{request.rate}</TableCell>
                        <TableCell align="right">{request.loantye}</TableCell>
                        <TableCell>
                            <Button
                                variant="contained"
                                color="info"
                                size="small"
                                onClick={() => handleEdit(user.id)}
                                style={{ marginLeft: "0.5rem" }}
                                startIcon={<EditIcon />}
                                >
                                Editar
                            </Button>

                            <Button
                                variant="contained"
                                color="error"
                                size="small"
                                onClick={() => handleDelete(user.id)}
                                style={{ marginLeft: "0.5rem" }}
                                startIcon={<DeleteIcon />}
                            >
                                Eliminar
                            </Button>
                        </TableCell>
                    </TableRow>
                ))}
            </TableBody>
          </Table>
        </TableContainer>
    );

};

export default RequestList;