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
import EventNoteIcon from '@mui/icons-material/EventNote';
import AssessmentIcon from '@mui/icons-material/Assessment';
import { useParams } from 'react-router-dom';

const RequestList = ({showAddButton}) => {
    const { id: iduser } = useParams();
    const [requests, setRequests] = useState([]);
    const [evaluationMessage, setEvaluationMessage] = useState("");
    const [statusMessage, setStatusMessage] = useState("");
    const navigate = useNavigate();

    const init = () => {
        if (iduser) {
            requestService
                .getRequestsByUserId(iduser)
                .then((response) => {
                    console.log(`Mostrando solicitudes del usuario ${iduser}.`, response.data);
                    setRequests(response.data);
                })
                .catch((error) => {
                    console.log(
                      `Error al intentar mostrar las solicitudes del usuario ${iduser}.`,
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
        console.log("User ID desde useParams:", iduser);
    }, [iduser]);

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

    const handleAddRequest = () => {
        if (iduser) {
            navigate(`/request/add/${iduser}`);
        } else {
            console.log("userId no está definido");
        }
    };    

    const handleTotalCosts = (id) => {
        navigate(`/quota/totalcosts/${id}`);
    };

    const handleEvaluate = (id) => {
        requestService.evaluateRequest(id)
            .then((response) => {
                console.log("Evaluación completada:", response.data);
                setEvaluationMessage(response.data);
            })
            .catch((error) => {
                console.log("Error al intentar evaluar la solicitud:", error);
            });
    };

    const handleState = (id) => {
        requestService.viewStatus(id)
            .then((response) => {
                console.log("Estado de la solicitud:", response.data);
                setStatusMessage(response.data);
            })
            .catch((error) => {
                console.log("Error al intentar obtener el estado de la solicitud:", error);
                setStatusMessage("Error al obtener el estado");
            });
    };

    return(
        <TableContainer component={Paper}>
            <br />
          <Link
            to="/request/add:iduser"
            style={{ textDecoration: "none", marginBottom: "1rem" }}
          >
            {showAddButton && (
                <Button 
                    variant="contained" 
                    color="primary" 
                    startIcon={<QueueIcon/>}
                    onClick={() => handleAddRequest()}
                >
                    Añadir Solicitud
                </Button>
            )}
          </Link>
          <br /> <br />
          {statusMessage && <p>{statusMessage}</p>}
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
                    key={request.id}
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
                                onClick={() => handleEdit(request.id)}
                                style={{ marginLeft: "0.5rem" }}
                                startIcon={<EditIcon />}
                                >
                                Editar
                            </Button>

                            <Button
                                variant="contained"
                                color="error"
                                size="small"
                                onClick={() => handleDelete(request.id)}
                                style={{ marginLeft: "0.5rem" }}
                                startIcon={<DeleteIcon />}
                            >
                                Eliminar
                            </Button>

                            <Button
                                variant="contained"
                                color="success"
                                size="small"
                                onClick={() => handleEvaluate(request.id)}
                                style={{ marginLeft: "0.5rem" }}
                                startIcon={<AssessmentIcon/>}
                            >
                                Evaluar
                            </Button>

                            <Button
                                variant="contained"
                                color="success"
                                size="small"
                                onClick={() => handleState(request.id)}
                                style={{ marginLeft: "0.5rem" }}
                                startIcon={<AssessmentIcon/>}
                            >
                                Estado
                            </Button>

                            <Button
                                variant="contained"
                                color="success"
                                size="small"
                                onClick={() => handleTotalCosts(request.id)}
                                style={{ marginLeft: "0.5rem" }}
                                startIcon={<EventNoteIcon/>}
                            >
                                Costos Totales
                            </Button>

                            {evaluationMessage && <p>{evaluationMessage}</p>}

                        </TableCell>
                    </TableRow>
                ))}
            </TableBody>
          </Table>
        </TableContainer>
    );

};

export default RequestList;