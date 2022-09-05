import React from 'react';
import Register from '../../features/register/Register';

const candidate = 'Candidate';
const CandidateRegisterPage = () => {
    return (
        <Register userType={candidate} />
    );
};
export default CandidateRegisterPage;