import './App.css'
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import Navbar from "./components/Navbar"
import Home from './components/Home';
import Simulation from './components/Simulation';
import ClientList from './components/ClientList';
import AddEditClient from './components/AddEditClient';
import RequestList from './components/RequestList';
import AddEditRequest from './components/AddEditRequest';
import NotFound from './components/Notfound';
import TotalCosts from './components/TotalCosts';

function App() {
  return (
      <Router>
          <div className="container">
          <Navbar></Navbar>
            <Routes>
              <Route path="/home" element={<Home/>} />
              <Route path="/simulation" element={<Simulation/>} />
              <Route path="/client/list" element={<ClientList/>} />
              <Route path="/client/add" element={<AddEditClient/>} />
              <Route path="/client/edit/:id" element={<AddEditClient/>} />
              <Route path="/request/list" element={<RequestList/>} />
              <Route path="/request/add" element={<AddEditRequest/>} />
              <Route path="/request/edit/:id" element={<AddEditRequest/>} />
              <Route path="/quota/totalcosts" element={<TotalCosts/>} />
              <Route path="*" element={<NotFound/>} />
            </Routes>
          </div>
      </Router>
  );
}

export default App
