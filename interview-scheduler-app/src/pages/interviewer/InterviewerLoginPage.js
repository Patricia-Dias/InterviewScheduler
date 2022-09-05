import React from 'react';
import Login from '../../features/login/Login';

const interviewer = 'Interviewer';
const InterviewerLoginPage = () => {
    return (
        <Login userType={interviewer} />
    );
};
export default InterviewerLoginPage;