import React from 'react';
import Register from '../../features/register/Register';

const interviewer = 'Interviewer';
const InterviewerRegisterPage = () => {
    return (
        <Register userType={interviewer} />
    );
};
export default InterviewerRegisterPage;