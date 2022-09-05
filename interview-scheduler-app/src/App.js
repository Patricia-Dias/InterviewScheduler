import React from 'react';
import HomePage from './pages/HomePage';
import { Routes, Route } from 'react-router-dom';
import CandidateRegisterPage from './pages/candidate/CandidateRegisterPage';
import InterviewerRegisterPage from './pages/interviewer/InterviewerRegisterPage';
import CandidateLoginPage from './pages/candidate/CandidateLoginPage';
import InterviewerLoginPage from './pages/interviewer/InterviewerLoginPage';
import InterviewerSlotPage from './pages/interviewer/InterviewerSlotPage';
import ScheduleInterviewPage from './pages/candidate/ScheduleInterviewPage';
import MyInterviewPage from './pages/candidate/MyInterviewPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />}/>
      <Route path="/interviewer/register" element={<InterviewerRegisterPage />}/>
      <Route path="/candidate/register" element={< CandidateRegisterPage/>}/>
      <Route path="/interviewer/login" element={<InterviewerLoginPage />}/>
      <Route path="/candidate/login" element={<CandidateLoginPage />}/>
      <Route path="/interviewer/interviews" element={<InterviewerSlotPage />}/>
      <Route path="/candidate/scheduleinterview" element={<ScheduleInterviewPage />}/>
      <Route path="/candidate/interviews" element={<MyInterviewPage />}/>
    </Routes>
  );
}

export default App;
