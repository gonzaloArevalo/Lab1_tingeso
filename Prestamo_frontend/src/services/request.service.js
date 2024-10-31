// al usar ´´ la transformacion ${id} se hace autoamticamente
//no hay diferencia entre comillas "" y ''
import httpClient from "../http-common";

const getAllRequests = () => {
    return httpClient.get("/api/v1/request/");
};

const get = id => {
    return httpClient.get(`/api/v1/request/${id}`);
}

const getRequestsByUserId = (iduser) => {
    return httpClient.get(`/api/v1/request/user/${iduser}`);
};

const deleteRequestById = (id) => {
    return httpClient.delete(`/api/v1/request/${id}`);
};

const updateRequest = (formData) => {
    return httpClient.put("/api/v1/request/", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
};

const requestLoan = (formData) => {
    return httpClient.post("/api/v1/request/request", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
};

const evaluateRequest = (idrequest) => {
    return httpClient.get(`/api/v1/request/evaluate/${idrequest}`);
};

const viewStatus = (id) => {
    return httpClient.get(`/api/v1/request/status`, { params: { id } });
};

const calculateTotalCosts = (request) => {
    return httpClient.post(`/api/v1/request/totalcosts`, request);
};

const deleteRequestsByUserId = (iduser) => {
    return httpClient.delete(`/api/v1/request/user/${iduser}`);
};

export default {
    getAllRequests,
    get,
    getRequestsByUserId,
    deleteRequestById,
    updateRequest,
    requestLoan,
    evaluateRequest,
    viewStatus,
    calculateTotalCosts,
    deleteRequestsByUserId,
};