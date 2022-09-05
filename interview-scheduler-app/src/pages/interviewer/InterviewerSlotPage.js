import React, { useEffect } from 'react';
import { Alert, Box, Button, Container, Snackbar, Typography } from "@mui/material";
import ResponsiveAppBar from '../../features/navbar/navbar';
import './../Layout.css';
import WeekCalendar from '../../features/week-calendar/WeekCalendar';
import CreateInterviewSlotPopUp from '../../features/interview-slots/CreateInterviewSlot';
import { INTERVIEW_SLOTS_BY_INTERVIEWER } from '../../api/APIconstants';
import axios from '../../api/axios';
import { SNACK_SEVERITY } from '../../app/AppConstants';
import { addHours } from '../../app/AppFunctions';
import { useDispatch, useSelector } from 'react-redux';
import slotsReducer, { INSERT_ITEMS } from '../../features/redux/interview-slots';

const currentDate = new Date();  

const InterviewerSlotPage = () => {
    const [openPopUp, setOpenPopUp] = React.useState(false);
    const dispatch = useDispatch();

    const handleOpenPopUp = () => {
        setOpenPopUp(true);
    };

    const handleClosePopUp = () => {
        setOpenPopUp(false);
    };

    const interviewSlots = useSelector((state) => state.interviewSlots);
    const user = JSON.parse(localStorage.getItem('user'));

    const [openSnack, setOpenSnack] = React.useState(false);
    const [severity, setSeverity] = React.useState('info');
    const [message, setMessage] = React.useState('');

    const showAlert = (message, severity) => {
        setMessage(message);
        setSeverity(severity);
        setOpenSnack(true);
    }

    useEffect(()=>{
        axios.get(`${INTERVIEW_SLOTS_BY_INTERVIEWER}/${user.id}`)
            .then(
                res => {
                    if (res.status === 200){
                        const data = res.data;
                        data.map((slot)=> 
                            dispatch(slotsReducer({type: INSERT_ITEMS, item:
                                {
                                    startDate: slot.time,
                                    endDate: addHours(1, new Date(slot.time)),
                                    title: slot.candidate === null ? '' : `Interview with ${slot.candidate.name}`
                                },
                        })));
                        
                        
                    }
                }
            ).catch(err =>{
                console.log(err);
                const status = err.response.status;
                if (status === 0){
                    showAlert('No server response', SNACK_SEVERITY.error);
                }else if(status === 404){
                    showAlert(`${user.name} is not an Interviewer!`, SNACK_SEVERITY.info);
                }else {
                    showAlert(`Unable to get Your Interview Slots`, SNACK_SEVERITY.info);
                }
            })
    }, [dispatch]);

    const handleCloseSnack = () => {
        setOpenSnack(false);
    };

    return (
        <div>
            <ResponsiveAppBar />
            <div className='centered header'>
                <Container >
                    <Typography variant="h2">
                        <b>{user.name.split(' ')[0]}</b>'s Interview Slots
                    </Typography>
                    <WeekCalendar currentDate={currentDate} schedulerData={interviewSlots} />
                    <Box sx={{my:2}}>
                        <Button onClick={handleOpenPopUp} className='success-btn'>Create Slots</Button>
                    </Box>
                    <CreateInterviewSlotPopUp openPopUp={openPopUp} handleClose={handleClosePopUp}/>
                    
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
export default InterviewerSlotPage;