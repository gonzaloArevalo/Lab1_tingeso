import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import userService from "../services/user.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";

const AddEditClient = () => {
    const [rut, setRut] = useState("");
    const [name, setName] = useState("");
    const [income, setIncome] = useState("");
    const [usertype, setUserType] = useState("");
    const [age, setAge] = useState("");
    const [timeinwork, setTimeInWork] = useState("");
    const [bankaccount, setBankAccount] = useState("");
    const [creation, SetCreation] = useState("");
    const [credithistory, SetCreditHistory] = useState(false);
    const [debts, SetDebts] = useState("");
    const [files, SetFiles] = useState(false);
    const [titleClientForm, setTitleClientForm] = useState("");
    const [movements, setMovements] = useState("");
    const [movmntsdate, setMovmntsDate] = useState("");

    const { id } = useParams();
    const navigate = useNavigate();

    const saveUser = (e) =>{
        e.preventDefault();

        const user = {rut, name, salary, usertype, age, timeinwork, bankaccount
            , creation, credithistory, debts, files, movements, movmntsdate, id
        };
        if(id){
            userService
            .update(user)
            .then((response) => {
                console.log("Usuario ha sido actualizado", response.data);
                navigate("/client/list");
            })
            .catch((error) => {
                console.log(
                  "Ha ocurrido un error al intentar actualizar datos del usuario.",
                  error
                );
              });
        }
        else{
            userService
            .create(user)
            .then((response) => {
                console.log("usuario ha sido añadido.", response.data);
                navigate("/client/list");
              })
              .catch((error) => {
                console.log(
                  "Ha ocurrido un error al intentar crear nuevo usuario.",
                  error
                );
              });
        }
    };

    useEffect(() => {
        if(id){
            setTitleClientForm("Editar Usuario");
            userService
            .get(id)
            .then((user) => {
                setRut(user.data.rut);
                setName(user.data.name);
                setIncome(user.data.income);
                setUserType(user.data.usertype);
                setAge(user.data.usertype);
                setTimeInWork(user.data.timeinwork);
                setBankAccount(user.data.bankaccount);
                SetCreation(user.data.creation);
                SetCreditHistory(user.data.credithistory);
                SetDebts(user.data.debts);
                SetFiles(user.data.files);
                setMovements(user.data.movements);
                setMovmntsDate(user.data.movmntsdate);

            })
            .catch((error) => {
                console.log("Se ha producido un error.", error);
            });
        }
        else{
            setTitleClientForm("Nuevo usuario");
        }
    }, []);

    return (
        <Box
            display="flex"
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
            component="form"
        >
            <h3> {titleClientForm} </h3>
            <hr />
            <form>
                <FormControl fullWidth>
                    <TextField
                        id="rut"
                        label="Rut"
                        value={rut}
                        variant="standard"
                        onChange={(e) => setRut(e.target.value)}
                        helperText="Ej. 12.587.698-8"
                    />
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="name"
                        label="Name"
                        value={name}
                        variant="standard"
                        onChange={(e) => setName(e.target.value)}
                    />
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="income"
                        label="Income"
                        type="number"
                        value={salary}
                        variant="standard"
                        onChange={(e) => setIncome(e.target.value)}
                        helperText="Salario mensual en Pesos Chilenos"
                    />
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="usertype"
                        label="User type"
                        value={usertype}
                        select
                        variant="standard"
                        defaultValue="Cliente"
                        onChange={(e) => setUserType(e.target.value)}
                        style={{ width: "25%" }}
                    >
                        <MenuItem value={"Client"}>Cliente</MenuItem>
                        <MenuItem value={"Executive"}>Ejecutivo</MenuItem>
                    </TextField>
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="age"
                        label="date of birth"
                        type="date"
                        value={age}
                        variant="standard"
                        onChange={(e) => setAge(e.target.value)}
                        helperText="Ej: 27/01/2001 "
                    />
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="timeinwork"
                        label= "date of work employence"
                        type="date"
                        value={timeinwork}
                        variant="standard"
                        onChange={(e) => setTimeInWork(e.target.value)}
                        helperText="Ej: 27/01/2001 , porfavor coloque fecha de contratacion"
                    />
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="bankaccount"
                        label=" bank account"
                        type="number"
                        value={bankaccount}
                        variant="standard"
                        onChange={(e) => setBankAccount(e.target.value)}
                        helperText="saldo total de la cuenta en pesos chilenos"
                    />
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="creation"
                        label = "creation of bank account"
                        type="date"
                        value={creation}
                        variant="standard"
                        onChange={(e) => SetCreation(e.target.value)}
                        helperText="Ej: 16/04/2013"
                    />
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="credithistory"
                        label= "Credit History"
                        value={credithistory}
                        select
                        variant="standard"
                        onChange={(e) => SetCreditHistory(e.target.value == "true")}
                    >
                        <MenuItem value={"true"}>si</MenuItem>
                        <MenuItem value={"false"}>no</MenuItem>
                    </TextField>
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="debts"
                        label ="debts"
                        type="number"
                        value={debts}
                        variant="standard"
                        onChange={(e) => SetDebts(e.target.value)}
                        helperText="Monto total de deudas en pesos chilenos"
                    />
                </FormControl>

                <FormControl fullWidth>
                    <TextField
                        id="files"
                        label="files"
                        value={files}
                        select
                        variant="standard"
                        onChange={(e) => SetFiles(e.target.value == "true")}
                    >
                        <MenuItem value={"true"}>subidos</MenuItem>
                        <MenuItem value={"false"}>no subidos</MenuItem>
                    </TextField>
                </FormControl>

                <FormControl>
                <br />
                    <Button
                        variant="contained"
                        color="info"
                        onClick={(e) => saveUser(e)}
                        style={{ marginLeft: "0.5rem" }}
                        startIcon={<SaveIcon />}
                    >
                        Grabar
                    </Button>
                </FormControl>
            </form>
            <hr />
            <Link to="/client/list">Back to List</Link>
        </Box>
    );
};

export default AddEditClient;