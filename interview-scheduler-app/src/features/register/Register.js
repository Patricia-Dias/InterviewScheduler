import React, { useState } from 'react';
import { Alert, Box, Button, Container, Snackbar, TextField, Typography } from "@mui/material";
import ResponsiveAppBar from '../navbar/navbar';
import './../../pages/Layout.css'
import axios from '../../api/axios';
import { useNavigate } from 'react-router-dom';
import { SNACK_SEVERITY } from '../../app/AppConstants';

function Register (props)  {
    const userType = props.userType;
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');

    const [openSnack, setOpenSnack] = React.useState(false);
    const [severity, setSeverity] = React.useState('info');
    const [message, setMessage] = React.useState('');

    const showAlert = (message, severity) => {
        setMessage(message);
        setSeverity(severity);
        setOpenSnack(true);
    }

    const handleSubmit = async (e) =>{
        e.preventDefault();
        
        // eslint-disable-next-line
        let re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        
        if(!re.test(email)){
            showAlert('Invalid email!', SNACK_SEVERITY.error);
            return;
        }
        if (!name || name.split(' ').length<2 || name.length<3){
            showAlert('Enter First Name and Last Name!', SNACK_SEVERITY.error);
            return;
        }
        const user = {
            "name": name,
            "email": email
        };
        await axios.post(`/${userType.toLowerCase()}/register`, user)
            .then(
                res => {
                    if (res.status === 201){
                        navigate(`/${userType.toLowerCase()}/login`);
                    }else{
                        showAlert('Register Failed', SNACK_SEVERITY.error);
                    }
                }
            ).catch(err =>{
                const status = err.response.status;
                if (status === 0){
                    showAlert('No server response', SNACK_SEVERITY.error);
                }else if (status === 409){
                    showAlert('Email already in use!', SNACK_SEVERITY.error);
                } else{
                    showAlert('Register Failed', SNACK_SEVERITY.error);
                }
            })
    };

    const handleCloseSnack = () => {
        setOpenSnack(false);
    };
    
    return (
        <div>
            <ResponsiveAppBar />
            <div className='centered'>
                <Container >
                    <Snackbar
                        open={openSnack}
                        onClose={handleCloseSnack}
                        >
                        <Alert severity={severity} onClose={handleCloseSnack} sx={{ width: '100%' }}>{message}</Alert>
                    </Snackbar>
                    <Typography variant="h2">
                        <b>Register as {userType}</b>
                    </Typography>
                    <Container sx={{my: 1}}>
                        <Box sx={{mx: 1, my: 1}} style={{display: 'inline-block'}}>
                            <TextField id='name-input' label='Name' required onChange={(e)=>setName(e.target.value)} />
                        </Box>
                        <Box sx={{mx: 1, my: 1}} style={{display: 'inline-block'}}>
                            <TextField id='email-input' label='Email' placeholder='example@email.com' required onChange={(e)=>setEmail(e.target.value)}/>
                        </Box>
                    </Container>
                    <Container>
                        <Button onClick={handleSubmit} className='success-btn'>Register</Button>
                    </Container>
                </Container>
            </div>
        </div>
    );
}

export default Register;