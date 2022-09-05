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
import { useDispatch, useSelector } from 'react-redux';
import slotsReducer, { APPEND_SLOT, INSERT_ITEMS } from '../../features/redux/interview-slots';

const MyInterviewPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const savedUser = JSON.parse(localStorage.getItem('user'));

  const [openSnack, setOpenSnack] = React.useState(false);
  const [severity, setSeverity] = React.useState('info');
  const [message, setMessage] = React.useState('');
//   const [interviewSlots, setInterviewSlots] = React.useState([]);
  const interviewSlot = useSelector((state) => state.interviewSlots);
  const [loadedData, setLoadedData] = React.useState(false);

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
                    const slot = res.data;
                    if(slot){
                        dispatch(slotsReducer({type: APPEND_SLOT, item:
                                {
                                    startDate: slot.time,
                                    endDate: addHours(1, new Date(slot.time)),
                                    title: slot.candidate === null ? '' : `Interview with ${slot.candidate.name}`
                                },
                        }));
                        return;
                    }else{
                        showAlert('You have no Slot assigned', SNACK_SEVERITY.info);
                    }
                }
            }
          ).catch(err =>{
            console.log(err);
            const status = err.response.status;
            if (status === 0){
                showAlert('No server response', SNACK_SEVERITY.error);
            }else if(status === 404){
                showAlert("Could not find Candidate", SNACK_SEVERITY.info);
            }else {
                showAlert('Unable to get Your Interview Slot', SNACK_SEVERITY.info);
            }
          })
          setLoadedData(true);
  }, [dispatch]);

    const handleCloseSnack = () => {
        setOpenSnack(false);
    };

    console.log(interviewSlot.length>0 ? (interviewSlot[0].startDate) : new Date());

    

  return (
      <div>
          <ResponsiveAppBar />
          <div className='centered header'>
              <Container > 
                <Typography variant="h2" >
                    Scheduled Interview
                </Typography>
                <Box>
                    {loadedData ? 
                        // if there is an interview slot, start calendar date by its date
                        <WeekCalendar currentDate={interviewSlot.length>0 ? (interviewSlot[0].startDate) : new Date()} schedulerData={interviewSlot}/>
                        : null}          
                </Box>
                {
                    interviewSlot.length===0 ? 
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