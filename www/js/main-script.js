/* 
 * IMPORTANT: [This file must be present on all of the html files of this application]
 */

/**
 * Creates a variable to indicates if user is already signed in
 */
var signed_in = false; 
var host = 'http://shakepoint.com.mx/rest';
var TOKEN_NAME = 'ShakePoint____AuthToken'; 
var EMAIL = "ShakePoint____EMAIL"; 
var CLOSEST_MACHINE = "ShakePoint____ClosestMachine";
var TMP_QR_CODE = "ShakePoint____QR_CODE";  

function getAuthToken(){
    return 'Basic ' + localStorage.getItem(TOKEN_NAME);
}

function saveClosestMachine(machineId){
    //removes first
    localStorage.removeItem(CLOSEST_MACHINE); 
    var json = JSON.stringify(machineId); 
    //save the machine 
    localStorage.setItem(CLOSEST_MACHINE, json); 
}

function saveTmpQrCode(code){
    localStorage.setItem(TMP_QR_CODE, JSON.stringify(code)); 
}

function getTmpQrCode(){
    var activeQrCodes = JSON.parse(localStorage.getItem(TMP_QR_CODE)); 
    return activeQrCodes;
}

function deleteTmpQrCode(){
    localStorage.removeItem(TMP_QR_CODE); 

}

function getClosestMachine(){
    var machine = localStorage.getItem(CLOSEST_MACHINE); 
    console.log("Got machine on localStorage: " + machine); 
    return JSON.parse(machine); 
}

function saveAuthToken(token){
    localStorage.setItem(TOKEN_NAME, token);
    signed_in = true;
}

function saveUserEmail(email){
    localStorage.setItem(EMAIL, email);
}

function getUserEmail(){
    return localStorage.getItem(EMAIL);
}


function logout(){
    localStorage.removeItem(TOKEN_NAME);
    localStorage.removeItem(EMAIL);
    signed_in = false;
}

function showConfirm(message, callback, title, labels){
    navigator.notification.confirm(message, callback, title, labels);
}