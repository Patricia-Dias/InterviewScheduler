import React, { useEffect } from 'react';
import { Box, Container, Typography, Snackbar, Alert, Button } from "@mui/material";
import ResponsiveAppBar from '../../features/navbar/navbar';
import WeekCalendar from '../../features/week-calendar/WeekCalendar';
import "./../../pages/Layout.css";
import { SNACK_SEVERITY } from '../../app/AppConstants';
import axios from '../../api/axios';
import { INTERVIEW_SLOTS_BY_CANDIDATE } from '../../api/APIconstants';
import { addHours } from '../../app/AppFunctions';
import { useNavigate } from 'react-router-dom';

const MyInterviewPage = () => {
  const navigate = useNavigate();
  const savedUser = JSON.parse(localStorage.getItem('user'));

  const [openSnack, setOpenSnack] = React.useState(false);
  const [severity, setSeverity] = React.useState('info');
  const [message, setMessage] = React.useState('');
  const [interviewSlots, setInterviewSlots] = React.useState([]);

  const showAlert = (message, severity) => {
      setMessage(message);
      setSeverity(severity);
      setOpenSnack(true);
  }

  const handleClick = () => {
    navigate('/candidate/scheduleinterview');
  }

  useEffect(()=>{
      axios.get(`${INTERVIEW_SLOTS_BY_CANDIDATE}/${savedUser.id}`)
          .then(
            res => {
                if (res.status === 200){
                    const data = res.data;
                    if(data){
                        setInterviewSlots(
                            [{
                                startDate: data.time,
                                endDate: addHours(1, new Date(data.time)),
                                title: `Interview with ${data.interviewer.name}`
                            }]
                        )
                        return;
                    }
                }
            }
          ).catch(err =>{
            const status = err.response.status;
            if (status === 0){
                showAlert('No server response', SNACK_SEVERITY.error);
            }else if(status === 404){
                showAlert('You have no Slot assigned', SNACK_SEVERITY.info);
            }else {
                showAlert('Unable to get Your Interview Slot', SNACK_SEVERITY.info);
            }
          })
  }, []);

    const handleCloseSnack = () => {
        setOpenSnack(false);
    };

    

  return (
      <div>
          <ResponsiveAppBar />
          <div className='centered header'>
              <Container > 
                <Typography variant="h2" >
                    Scheduled Interview
                </Typography>
                <Box>
                    {/* if there is an interview slot, start calendar date by its date*/}
                    <WeekCalendar currentDate={interviewSlots.length>0 ? (interviewSlots[0].startDate) : new Date()} schedulerData={interviewSlots}/>                  
                </Box>
                {
                    interviewSlots.length===0 ? 
                    <Button sx={{my:2}} onClick={()=>{handleClick();}} className="success-btn"> Schedule Interview</Button>
                    : null
                }
                    
                </Container>
          </div>
          <Snackbar
              open={openSnack}
              onClose={handleCloseSnack}
              >
              <Alert severity={severity} onClose={handleCloseSnack} sx={{ width: '100%' }}>{message}</Alert>
          </Snackbar>
      </div>
  );
};
export default MyInterviewPage;