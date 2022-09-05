import React, { useEffect } from 'react';
import { Box, TextField, Container, Typography, Button, Snackbar, Alert } from "@mui/material";
import ResponsiveAppBar from '../../features/navbar/navbar';
import WeekCalendar from '../../features/week-calendar/WeekCalendar';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { TimePicker, DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import dayjs from 'dayjs';
import "./../../pages/Layout.css";
import { INTERVIEW_SLOTS, INTERVIEW_SLOTS_AVAILABLE, INTERVIEW_SLOTS_BY_TIME } from '../../api/APIconstants';
import { SNACK_SEVERITY } from '../../app/AppConstants';
import axios from '../../api/axios';
import { addHours, getExactHour, joinDayAndHour } from '../../app/AppFunctions';
import { useNavigate } from 'react-router-dom';

const currentDate = new Date();

const ScheduleInterviewPage = () => {
    const navigate = useNavigate();

    const [hour, setHour] = React.useState(dayjs(null));

    const [day, setDay] = React.useState(dayjs(null));

    const [openSnack, setOpenSnack] = React.useState(false);
    const [severity, setSeverity] = React.useState('info');
    const [message, setMessage] = React.useState('');
    const [interviewSlots, setInterviewSlots] = React.useState([]);
    const savedUser = JSON.parse(localStorage.getItem('user'));

    const showAlert = (message, severity) => {
        setMessage(message);
        setSeverity(severity);
        setOpenSnack(true);
    }

    useEffect(()=>{
        axios.get(INTERVIEW_SLOTS_AVAILABLE)
            .then(
              res => {
                if (res.status === 200){
                    const data = res.data;
                    
                    const slots = [];
                    data.map((slot)=> 
                    slots.push(
                        {
                            startDate: slot.time,
                            endDate: addHours(1, new Date(slot.time)),
                            title: slot.candidate === null ? '' : `Interview with ${slot.candidate.name}`
                        }
                    )
                    )
                    setInterviewSlots(slots);
                    console.log(data);
                }else {
                    showAlert('Unable to get Your Interview Slots', SNACK_SEVERITY.info);
                }
              }
            ).catch(err =>{
              console.log(err);
              const status = err.response.status;
              if (status === 0){
                  showAlert('No server response', SNACK_SEVERITY.error);
              }else if(status === 404){
                  showAlert(`${savedUser.name} is not an Interviewer`, SNACK_SEVERITY.error);
              }else {
                  showAlert('Unable to get Your Interview Slots', SNACK_SEVERITY.info);
              }
            })
    }, []);

    const handleAssign = async (e) =>{

        if (!day.isValid() || !hour.isValid()){
            showAlert("Please fill all the fields", SNACK_SEVERITY.error);
            return;
        }
        if (hour.minute()!==0){
            showAlert("Only o'clock sharp hours!", SNACK_SEVERITY.error);
            return;
        }

        const date = joinDayAndHour(day, hour);
        const dayToSend = getExactHour(date.toISOString());
        console.log(dayToSend);

        var interviewSlot;
        var availableSlot = true; 
        
        await axios.get(`${INTERVIEW_SLOTS_BY_TIME}`, { params: { time: dayToSend } })
            .then(
                res => {
                    if (res.status === 200){
                        interviewSlot = res.data;
                    }else{
                        showAlert('Unable to create Slots', SNACK_SEVERITY.error);
                    }
                }
            ).catch(err =>{
                availableSlot = false;
                const status = err.response.status;
                if (status === 0){
                    showAlert('No server response', SNACK_SEVERITY.error);
                } else if (status === 404){
                    showAlert('Select an Available Interview Slot!', SNACK_SEVERITY.error);
                } else {
                    showAlert('Unable to create Slots', SNACK_SEVERITY.error);
                }
                return;
            });
            if (!availableSlot) return;

            await axios.put(`${INTERVIEW_SLOTS}?slotId=${interviewSlot.id}&candidateId=${savedUser.id}`)
            .then(
                res => {
                    if (res.status === 200){
                        showAlert('Slot Assigned', SNACK_SEVERITY.success);
                        navigate('/candidate/interviews');
                    }else{
                        showAlert('Unable to Assign Slot', SNACK_SEVERITY.error);
                    }
                }
            ).catch(err =>{
                const status = err.response.status;
                if (status === 0){
                    showAlert('No server response', SNACK_SEVERITY.error);
                }else if(status === 400){
                    showAlert('Bad inputs!', SNACK_SEVERITY.error);
                } else if (status === 404){
                    showAlert("Couldn't find Slot's Interviewer", SNACK_SEVERITY.error);
                }else if (status === 409){
                    showAlert('Slot is already assigned', SNACK_SEVERITY.error);}
                else{
                    showAlert('Unable to Assign Slot', SNACK_SEVERITY.error);
                }
            });
    };

    const handleCloseSnack = () => {
        setOpenSnack(false);
    };

    return (
        <div>
            <ResponsiveAppBar />
            <div className='centered header'>
                <Container > 
                    <Typography variant="h2" style={{marginTop: '150px'}}>
                        Available Interview Slots
                    </Typography>
                    <Box>
                        <WeekCalendar currentDate={currentDate} schedulerData={interviewSlots}/>                  
                    </Box>
                    <Box sx={{my:2}}>
                        <Typography>Choose an Available Slot</Typography>
                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                            <Box sx={{my:1}} >
                                <DatePicker
                                    disablePast
                                    label="Day"
                                    openTo="month"
                                    views={['year', 'month', 'day']}
                                    value={day}
                                    onChange={(newValue) => {
                                        setDay(newValue);
                                    }}
                                    renderInput={(params) => <TextField {...params} />}
                                />
                            </Box>
                            <Box sx={{my:1}} >
                                    <TimePicker
                                        label="Hour"
                                        value={hour}
                                        onChange={(newValue) => {
                                            setHour(newValue);
                                        }}
                                        renderInput={(params) => <TextField {...params} />}
                                        shouldDisableTime={(timeValue, clockType) => {
                                            if (clockType === 'minutes' && timeValue !== 0) {
                                                return true;
                                            }
                                            return false;
                                        }}
                                    />
                            </Box>
                        </LocalizationProvider>
                    </Box>
                    <Button sx={{my:1}} onClick={handleAssign} className='success-btn'>Assign Slot to me</Button>
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
export default ScheduleInterviewPage;