import React from 'react';
import { Box, Button, Card, CardContent, Container, Typography } from "@mui/material";
import ResponsiveAppBar from '../features/navbar/navbar';
import './Layout.css'
import { useNavigate } from 'react-router-dom';
import './HomePage.css'


const HomePage = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.setItem('authenticated', JSON.parse(false));
        localStorage.removeItem('user');
      };

    return (
        <div className='homepage-background'>
            <ResponsiveAppBar />
            <div className='centered'>
                <Container > 
                        <Typography variant="h2">
                            Welcome to the <br/><b>Interview Scheduler</b>.
                        </Typography>
                        <p> The website where you can create Interview Slots or schedule your Interviews </p>
                        <Container >
                            <Box mx={1} style={{display: 'inline-block'}}>
                                <Card>
                                    <CardContent>
                                        <Typography variant='body1'>I want to display my available time for Interviews.</Typography>
                                        <Button onClick={()=>{ handleLogout(); navigate("/interviewer/login");}} className='interviewer-btn'>I'm an Interviewer</Button>
                                    </CardContent>
                                </Card>                                    
                            </Box>
                            <Box mx={1} style={{display: 'inline-block'}}>
                                <Card>
                                    <CardContent>
                                        <Typography variant='body1'>I want to schedule/check my Interview!</Typography>
                                        <Button onClick={()=>{ handleLogout(); navigate("/candidate/login");}} className='candidate-btn'>I'm a Candidate</Button>
                                    </CardContent>
                                </Card>                                    
                            </Box>
                        </Container>
                </Container>
            </div>
        </div>
    );
};
export default HomePage;
  