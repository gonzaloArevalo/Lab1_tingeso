import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import requestService from '../services/request.service';

const TotalCosts = () => {
    const { id } = useParams();
    const [totalCost, setTotalCost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTotalCost = async () => {
            try {
                
                const requestResponse = await requestService.get(id);
                const request = requestResponse.data;

                const totalCostResponse = await requestService.calculateTotalCosts(request);
                setTotalCost(totalCostResponse.data);
                setLoading(false);
            } 
            catch (error) {
                console.error("Error al calcular el costo total del préstamo:", error);
                setError("No se pudo calcular el costo total del préstamo.");
                setLoading(false);
            }
        };
    fetchTotalCost();
    }, [id]);



    if (loading) return <p>Cargando...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div>
            <h2>Costo Total del Préstamo</h2>
            <pre>El costo total del préstamo es: ${totalCost}</pre>
        </div>
    );
};

export default TotalCosts;