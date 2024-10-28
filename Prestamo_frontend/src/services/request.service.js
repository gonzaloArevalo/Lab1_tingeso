// al usar ´´ la transformacion ${id} se hace autoamticamente
//no hay diferencia entre comillas "" y ''
import httpClient from "../http-common";

const getAllRequests = () => {
    return httpClient.get("/api/v1/request/");
};

const getRequestsByUserId = (iduser) => {
    return httpClient.get(`/api/v1/request/user/${iduser}`);
};

const deleteRequestById = (id) => {
    return httpClient.delete(`/api/v1/request/${id}`);
};

const updateRequest = (data) => {
    return httpClient.put("/api/v1/request/", data);
};

const requestLoan = (formData) => {
    return httpClient.post("/api/v1/request/request", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
};

const evaluateRequest = (idrequest) => {
    return httpClient.get(`/api/v1/request/evaluate`, { params: { idrequest } });
};

const viewStatus = (iduser) => {
    return httpClient.get(`/api/v1/request/status`, { params: { iduser } });
};

const calculateTotalCosts = (request) => {
    return httpClient.post(`/api/v1/request/totalcosts`, request);
};

export default {
    getAllRequests,
    getRequestsByUserId,
    deleteRequestById,
    updateRequest,
    requestLoan,
    evaluateRequest,
    viewStatus,
    calculateTotalCosts,
};