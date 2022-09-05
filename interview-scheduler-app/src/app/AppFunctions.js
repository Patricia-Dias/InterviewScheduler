const HOUR = 60 * 60 * 1000;    //1 hour = 60*60*1000 miliseconds

export function addHours(numOfHours, date = new Date()) {
    date.setTime(date.getTime() + numOfHours * HOUR);
    return date.toJSON();
}

export function convertDayAvailabilityToSlots(day, startHour, endHour){

    const startOfAvailability = joinDayAndHour(day, startHour);
    const endOfAvailability = joinDayAndHour(day, endHour);     

    return divideAvailableHoursIntoSlots(startOfAvailability, endOfAvailability);
}

export function joinDayAndHour(day, hour){
    const dayDate = new Date(day);
    const hourDate = new Date(hour);
    return setHours(dayDate, hourDate.getHours());
}

function setHours(date, hour){
    date.setHours(hour);   //.setHours() converts to Milliseconds
    return new Date(date);  //convert miliseconds to Date
}

function divideAvailableHoursIntoSlots(start, end){
    const slots = [];
    console.log(start.toISOString()+"\t"+end.toISOString());

    // number of hours between start and end = end-start 
    // (since these hours are in milliseconds, divide per hour) 
    // thus (end-start)/hour
    let nSlots = (end.getTime() - start.getTime())/HOUR;
    

    for (let i=0; i<nSlots; i++){
        var date = start.getTime()+i*HOUR;
        date = new Date(date);

        slots.push(getExactHour(date));
    }

    return slots;
}

export function getExactHour(date){
    date = removeTimeZone(date);
    return date.substring(0, 14) + "00:00";     //removing the extra minutes and seconds
}

function removeTimeZone(dateWithTimeZone){
    dateWithTimeZone = new Date(dateWithTimeZone);
    var userTimezoneOffset = dateWithTimeZone.getTimezoneOffset() * 60000;  //milliseconds of timezone
    return new Date(dateWithTimeZone.getTime() - userTimezoneOffset).toISOString();
}