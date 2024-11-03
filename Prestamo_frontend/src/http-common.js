//conexion con backend
import axios from "axios";

const prestamoBackendServer = import.meta.env.VITE_PRESTAMO_BACKEND_SERVER;
const prestamoBackendPort = 8090;

console.log(prestamoBackendServer)
console.log(prestamoBackendPort)

export default axios.create({
    baseURL: `http://loandeposit-app.brazilsouth.cloudapp.azure.com:8090`,
    headers: {
        'Content-Type': 'application/json'
    }
});