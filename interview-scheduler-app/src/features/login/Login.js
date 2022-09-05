import React, { useState } from 'react';
import { Alert, Button, Container, Snackbar, TextField, Typography } from "@mui/material";
import ResponsiveAppBar from '../navbar/navbar';
import './../../pages/Layout.css'
import axios from '../../api/axios';
import { useNavigate } from 'react-router-dom';
import { SNACK_SEVERITY } from '../../app/AppConstants';

function Login (props)  {
    const userType = props.userType;
    var register = `/${userType.toLowerCase()}/register`;
    const navigate = useNavigate();


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
            console.log(e);
            showAlert('Invalid email!', SNACK_SEVERITY.error);
            return;
        }
        await axios.post(`/${userType.toLowerCase()}/${email}`)
            .then(
                res => {
                    if (res.status === 200){
                        const data = res.data;
                        const user = {
                            id: data.id,
                            name: data.name,
                            email: data.email,
                            userType: userType
                        }
                        localStorage.setItem('authenticated', JSON.stringify(true));
                        localStorage.setItem('user', JSON.stringify(user));
                        navigate(`/${userType.toLowerCase()}/interviews`);
                    }else{
                        showAlert('Login Failed', SNACK_SEVERITY.error);
                    }
                }
            ).catch(err =>{
                const status = err.response.status;
                if (status === 0){
                    showAlert('No server response', SNACK_SEVERITY.error);
                }else if(status === 404){
                    showAlert('Wrong email', SNACK_SEVERITY.error);
                }else{
                    showAlert('Login Failed', SNACK_SEVERITY.error);
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
                        <b>Login as {userType}</b>
                    </Typography>
                    <TextField id='email-input' label='Email' placeholder='example@email.com' required onChange={(e)=>setEmail(e.target.value)}/>
                    <Container sx={{my:3 }}>
                        <a href={register} style={{ fontSize:'13px'}}>Register as {userType} </a>
                    </Container>
                    <Container>
                        <Button onClick={handleSubmit} className='success-btn'>Login</Button>
                    </Container>
                </Container>
            </div>
        </div>
    );
}

export default Login;
