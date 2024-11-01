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
                

                const formattedCost = totalCostResponse.data.split("\n");
                setTotalCost(formattedCost);
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

    return(
        <div style={{ border: "1px solid #ccc", padding: "20px", borderRadius: "10px", maxWidth: "400px", margin: "auto" }}>
                <h2>Costo Total del Préstamo</h2>
                {totalCost.map((line, index) => (
                    <div key={index} style={{
                        border: "1px solid #ddd",
                        padding: "10px",
                        margin: "5px 0",
                        borderRadius: "5px",
                        backgroundColor: "#f9f9f9"
                    }}>
                        {line}
                    </div>
                ))}
            </div>
    );
};

export default TotalCosts;