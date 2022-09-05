import React from 'react';
import Login from '../../features/login/Login';

const candidate = 'Candidate';
const CandidateLoginPage = () => {
    return (
        <Login userType={candidate} />
    );
};
export default CandidateLoginPage;