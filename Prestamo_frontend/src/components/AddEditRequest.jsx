import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import requestService from "../services/request.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import Typography  from "@mui/material/Typography";

const AddEditRequest = () => {
    const { id, iduser: paramIdUser } = useParams();

    const [iduser, setIdUser] = useState(paramIdUser || "");
    const [requestStatus, setRequestStatus] = useState("");
    const [amount, setAmount] = useState("");
    const [term, setTerm] = useState("");
    const [rate, setRate] = useState("");
    const [propertyValue, setPropertyValue] = useState("");
    const [loanType, setLoanType] = useState("");
    const [incomeTicket, setIncomeTicket] = useState(null);
    const [creditHistorial, setCreditHistorial] = useState(null);
    const [appraisalCertificate, setAppraisalCertificate] = useState(null);
    const [deedFirstHome, setDeedFirstHome] = useState(null);
    const [businessState, setBusinessState] = useState(null);
    const [businessPlan, setBusinessPlan] = useState(null);
    const [remBudget, setRemBudget] = useState(null);
    const [appCertificateNew, setAppCertificateNew] = useState(null);
    const [titleRequestForm, SetTitleRequestForm] = useState("");

    const navigate = useNavigate();

    const saveRequest = (e) =>{
        e.preventDefault();

        const formData = new FormData();
        formData.append("iduser", iduser);
        formData.append("requestStatus", requestStatus);
        formData.append("amount", amount);
        formData.append("term", term);
        formData.append("rate", rate);
        formData.append("propertyValue", propertyValue);
        formData.append("loanType", loanType);

        if (incomeTicket) formData.append("incomeTicket", incomeTicket);
        if (creditHistorial) formData.append("creditHistorial", creditHistorial);
        if (appraisalCertificate) formData.append("appraisalCertificate", appraisalCertificate);
        if (deedFirstHome) formData.append("deedFirstHome", deedFirstHome);
        if (businessState) formData.append("businessState", businessState);
        if (businessPlan) formData.append("businessPlan", businessPlan);
        if (remBudget) formData.append("remBudget", remBudget);
        if (appCertificateNew) formData.append("appCertificateNew", appCertificateNew);

        if(id){
            requestService
            .updateRequest(formData)
            .then((response) => {
                console.log("solicitud ha sido actualizada.", response.data);
                navigate(`/request/list/${id}`);
              })
              .catch((error) => {
                console.log(
                  "Ha ocurrido un error al intentar actualizar datos de solicitud.",
                  error
                );
              });
        }
        else{
            requestService
            .requestLoan(formData)
            .then((response) => {
                console.log("Solicitud ha sido ingresada.", response.data);
                navigate("/request/list");
              })
              .catch((error) => {
                console.log(
                  "Ha ocurrido un error al intentar crear nueva Solicitud.",
                  error
                );
              });
        }
    }

    useEffect(() => {

        if(id){
            SetTitleRequestForm("Editar Solicitud");
            requestService
            .get(id)
            .then((response) => {
                const data = response.data;
                setIdUser(data.iduser);
                setRequestStatus(data.requeststatus);
                setAmount(data.amount);
                setTerm(data.term);
                setRate(data.rate);
                setPropertyValue(data.propertyvalue);
                setLoanType(data.loantype);

            })
            .catch((error) => {
                console.log("Error al cargar los datos de la solicitud:", error);
            });
        }
        else{
            SetTitleRequestForm("Nueva Solicitud")
        }
    }, []);

    const handleFileChange = (e, setFile) => {
        const file = e.target.files[0];
        setFile(file);
    };

    return (
        <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        component="form"
        >
            <h3> {titleRequestForm} </h3>
            <hr />
            <FormControl fullWidth margin="normal">
                <TextField
                    id="amount"
                    label="Amount"
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    variant="standard"
                    helperText="Ingrese el monto de la solicitud"
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <TextField
                    id="term"
                    label="Plazo"
                    type="number"
                    value={term}
                    onChange={(e) => setTerm(e.target.value)}
                    variant="standard"
                    helperText = "Ingrese plazo en años"
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <TextField
                    id="rate"
                    label="Tasa de Interés"
                    type="number"
                    value={rate}
                    onChange={(e) => setRate(e.target.value)}
                    variant="standard"
                    helperText = "ingrese Tasa de interes anual"
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <TextField
                    id="propertyValue"
                    label="Valor de la Propiedad"
                    type="number"
                    value={propertyValue}
                    onChange={(e) => setPropertyValue(e.target.value)}
                    variant="standard"
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <TextField
                    id="loanType"
                    label="Tipo de Préstamo"
                    type="text"
                    value={loanType}
                    select
                    defaultValue="Primera vivienda"
                    onChange={(e) => setLoanType(e.target.value)}
                    variant="standard"
                    style={{ width: "25%" }}
                >
                    <MenuItem value={"first living"}>Primera Vivienda</MenuItem>
                    <MenuItem value={"second living"}>Segunda Vivienda</MenuItem>
                    <MenuItem value={"commercial properties"}>Propiedades Comerciales</MenuItem>
                    <MenuItem value={"remodelation"}>Remodelacion</MenuItem>

                </TextField>
            </FormControl>

            <FormControl fullWidth margin="normal">
                <Typography variant="body2">Ticket de Ingresos</Typography>
                <input
                    type="file"
                    onChange={(e) => handleFileChange(e, setIncomeTicket)}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <Typography variant="body2">Historial de Crédito</Typography>
                <input
                    type="file"
                    onChange={(e) => handleFileChange(e, setCreditHistorial)}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <Typography variant="body2">Certificado de Avalúo</Typography>
                <input
                    type="file"
                    onChange={(e) => handleFileChange(e, setAppraisalCertificate)}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <Typography variant="body2">Escritura de la primera vivienda</Typography>
                <input
                    type="file"
                    onChange={(e) => handleFileChange(e, setDeedFirstHome)}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <Typography variant="body2">Estado financiero del negocio</Typography>
                <input
                    type="file"
                    onChange={(e) => handleFileChange(e, setBusinessState)}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <Typography variant="body2">Plan de negocios</Typography>
                <input
                    type="file"
                    onChange={(e) => handleFileChange(e, setBusinessPlan)}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <Typography variant="body2">Presupuesto de la remodelación</Typography>
                <input
                    type="file"
                    onChange={(e) => handleFileChange(e, setRemBudget)}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <Typography variant="body2">Certificado de avalúo actualizado</Typography>
                <input
                    type="file"
                    onChange={(e) => handleFileChange(e, setAppCertificateNew)}
                />
            </FormControl>

            <FormControl>
                <br />
                <Button
                    variant="contained"
                    color="info"
                    onClick={(e) => saveRequest(e)}
                    style={{ marginLeft: "0.5rem" }}
                    startIcon={<SaveIcon />}
                >
                    Grabar
                </Button>
            </FormControl>
            <hr />
            <Link to="/request/list:id">Back to List</Link>
        </Box>
    )

};

export default AddEditRequest;