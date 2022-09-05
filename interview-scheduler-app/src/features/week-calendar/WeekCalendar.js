import React from 'react';
import { Paper } from "@mui/material";
import { ViewState } from '@devexpress/dx-react-scheduler';
import {
  Scheduler,
  Appointments,
  WeekView,
  Toolbar,
  DateNavigator,
  TodayButton,
} from '@devexpress/dx-react-scheduler-material-ui';

export default class WeekCalendar extends React.Component{
    render(){
        const schedulerData = this.props.schedulerData;
        const currentDate = this.props.currentDate;
        return (
            <Paper>
                <Scheduler
                    data={schedulerData}
                    height={660}
                    >
                    <ViewState
                        defaultCurrentDate={currentDate}
                    />
                    <WeekView
                        name='work-week'
                        excludedDays={[0,6]}
                        startDayHour={9}
                        endDayHour={19}
                    />
                    <Appointments />
                    <Toolbar/>
                    <DateNavigator />
                    <TodayButton />
                </Scheduler>
            </Paper>
        );
    }
}