import React from 'react';
import { Button,
    TextField,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Alert,
    Snackbar,
    Box
} from "@mui/material";
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { TimePicker, DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import dayjs from 'dayjs';
import "./../../pages/Layout.css";
import { SNACK_SEVERITY } from '../../app/AppConstants';
import { INTERVIEW_SLOTS } from '../../api/APIconstants';
import axios from '../../api/axios';
import { addHours, convertDayAvailabilityToSlots } from '../../app/AppFunctions';
import { useDispatch, useSelector } from 'react-redux';
import slotsReducer, { APPEND_SLOT } from '../redux/interview-slots';

const CreateInterviewSlotPopUp = (props) => {
    const user = JSON.parse(localStorage.getItem('user'));

    var openPopUp = props.openPopUp;
    var handleClose = props.handleClose;

    const dispatch = useDispatch();
    const reducerSlots = useSelector((state) => state.reducer);
    // const [smth, setSmth] = React.useState(state);

    const [startHour, setStartHour] = React.useState(dayjs(null));
    const [endHour, setEndHour] = React.useState(dayjs(null));

    const [day, setDay] = React.useState(dayjs(null));
    const [openSnack, setOpenSnack] = React.useState(false);
    const [severity, setSeverity] = React.useState('info');
    const [message, setMessage] = React.useState('');


    const handleCloseSnack = () => {
        setOpenSnack(false);
    };

    const showAlert = (message, severity) => {
        setMessage(message);
        setSeverity(severity);
        setOpenSnack(true);
    }

    const handleCreate = async (e) =>{
        e.preventDefault();

        if (!day.isValid() || !startHour.isValid() || !endHour.isValid()){
            showAlert("Please fill all the fields", SNACK_SEVERITY.error);
            return;
        }
        if (startHour.isAfter(endHour) || startHour.isSame(endHour)){
            showAlert("Can't start at an hour earlier than/equal to the end hour!", SNACK_SEVERITY.warning);
            return;
        }
        if (startHour.minute()!==0 || endHour.minute()!==0){
            showAlert("Only o'clock sharp hours!", SNACK_SEVERITY.error);
            return;
        }

        const slots = convertDayAvailabilityToSlots(day, startHour, endHour);
        
        slots.map( async (slot) =>{
        await axios.post(INTERVIEW_SLOTS, {
            time: slot,
            interviewer: {
                name: user.name,
                email: user.email,
            }
        })
            .then(
                res => {
                    if (res.status !== 201){
                        showAlert('Unable to create Slots', SNACK_SEVERITY.error);
                        return;
                    }
                    const slot = res.data;
                    console.log(slot);
                    
                    dispatch(slotsReducer( {type: APPEND_SLOT, item:
                        ({
                            startDate: slot.time,
                            endDate: addHours(1, new Date(slot.time)),
                            title: slot.candidate === null ? '' : `Interview with ${slot.candidate.name}`
                        })})
                    );
                }
            ).catch(err =>{
                console.log(err);
                const status = err.response.status;
                if (status === 0){
                    showAlert('No server response', SNACK_SEVERITY.error);
                }else if(status === 400){
                    showAlert('Bad inputs!', SNACK_SEVERITY.error);
                } else if (status === 404){
                    showAlert('Creating slots is only available for Interviewers', SNACK_SEVERITY.error);
                }else if (status === 409){
                    showAlert('Slot already exists', SNACK_SEVERITY.error);}
                else{
                    showAlert('Unable to create Slots', SNACK_SEVERITY.error);
                }
                return;
            })
        });
        showAlert("Slots created!", SNACK_SEVERITY.success);
        handleClose();
    };


    return (
        <>
            <Dialog open={openPopUp} onClose={handleClose} >
                <DialogTitle>Create Interview Slots</DialogTitle>
                <DialogContent>
                    <DialogContentText style={{textAlign:'center'}}>
                        Input the day of Interviews
                    </DialogContentText>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                        <Box sx={{my:1}} >
                            <DatePicker
                                disablePast
                                label="Available On"
                                openTo="month"
                                views={['year', 'month', 'day']}
                                value={day}
                                onChange={(newDay) => {
                                    setDay(newDay);
                                }}
                                renderInput={(params) => <TextField {...params} />}
                            />
                        </Box>
                        <DialogContentText style={{textAlign:'center'}}>
                            Input the hours when you are available for interviews
                        </DialogContentText>
                        <Box sx={{my:1}} >
                            <Box style={{display:'inline-block', marginRight:'5px', marginBottom:'10px'}} >
                                <TimePicker
                                    label="From Hour"
                                    value={startHour}
                                    onChange={(newValue) => {
                                        showAlert("Only o'clock sharp hours!", SNACK_SEVERITY.info)
                                        setStartHour(newValue);
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
                            <Box style={{display:'inline-block'}}>
                                <TimePicker
                                    label="To Hour"
                                    value={endHour}
                                    onChange={(newValue) => {
                                        setEndHour(newValue);
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
                        </Box>                    
                    </LocalizationProvider>
                    
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} className='dismiss-btn'>Cancel</Button>
                    <Button onClick={handleCreate} className='success-btn'>Create</Button>
                </DialogActions>
            </Dialog>
            <Snackbar
                open={openSnack}
                autoHideDuration={3000}
                onClose={handleCloseSnack}
                >
                <Alert severity={severity} onClose={handleCloseSnack} sx={{ width: '100%' }}>{message}</Alert>
            </Snackbar>
    </>
    );
  };
export default CreateInterviewSlotPopUp;