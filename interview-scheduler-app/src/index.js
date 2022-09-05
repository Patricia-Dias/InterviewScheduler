import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import reportWebVitals from './reportWebVitals';
import './index.css';
import { BrowserRouter } from 'react-router-dom';
import {configureStore} from '@reduxjs/toolkit'
// import { createStore } from "redux";
import { Provider } from "react-redux";
import slotsReducer from './features/redux/interview-slots';

const container = document.getElementById('root');

const root = createRoot(container);

//The created store
const store = configureStore({
  reducer: slotsReducer,
});

root.render(
    <BrowserRouter>
      <Provider store={store}>
        <App />
      </Provider>
    </BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
