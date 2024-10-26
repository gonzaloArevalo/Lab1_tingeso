import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/user/');
}

const create = data => {
    return httpClient.post("/api/v1/user/", data);
}

const get = id => {
    return httpClient.get(`/api/v1/user/${id}`);
}

const update = data => {
    return httpClient.put('/api/v1/user/', data);
}

const remove = id => {
    return httpClient.delete(`/api/v1/user/${id}`);
}

const getClients = () => {
    return httpClient.get("/api/v1/user/clients");
}

const register = data => {
    return httpClient.post("/api/v1/user/register", data);
}

const simulateLoan = (amount, term, rate, loanType) => {
    return httpClient.get(`/api/v1/user/simulation`, {
        params: { amount, term, rate, loanType }
    });
}

export default { getAll, create, get, update, remove , getClients, register, simulateLoan};