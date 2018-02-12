/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function ProfileViewModel(){
    var self = this; 
    //create the datepicker 

    self.email = ko.observable(''); 
    self.name = ko.observable(''); 
    self.age = ko.observable(); 
    self.weight = ko.observable(); 
    self.height = ko.observable(); 
    self.birthday = ko.observable('Fecha de nacimiento'); 
    self.purchasesTotal = ko.observable(); 
    self.userSince = ko.observable(''); 
    self.loading = ko.observable(true); 
    self.pageNumber = ko.observable(1); 
    self.pagesAvailable = ko.observable(false); 
    self.errorMessage = ko.observable(); 
    self.error = ko.observable(false);
    self.errorMessage = ko.observable('') ;
    self.hasProfile = ko.observable(false); 
    self.profileExists = ko.observable(false); 
    self.newProfile = ko.observable(false);
    self.unavailableProfile = ko.observable(false); 
    self.cmi = ko.observable(); 
    self.viewPurchases = function(){
        window.location = "purchases.html"; 
    }; 
    self.saveProfile = function(){
        self.loading(true); 
        //create a request data 
        var data = {
            "age" : self.age(), 
            "weight" : self.weight(), 
            "height" : self.height(), 
            "birthday" : self.birthday()
        }; 
        //validate data
        if(! data.age){
            showErrorMessage("Debes de especificar tu edad"); 
            return;
        }
        else if(! data.weight){
            showErrorMessage("Debes de especificar tu peso"); 
            return;
        }
        else if(! data.height){
            showErrorMessage("Debes de especificar tu altura"); 
            return;
        }
        else if(! data.birthday){
            return;
            showErrorMessage("Debes de especificar tu edad"); 
        }
        //create a request 
        $.ajax({
            url: host + '/shop/save_profile', 
            type: 'POST', 
            contentType: 'application/json', 
            accept: 'application/json', 
            dataType: 'json', 
            data: JSON.stringify(data), 
            success: function(profile){ // response is a user profile 
                self.hasProfile(true); 
                self.email(profile.email); 
                self.name(profile.userName); 
                self.age(profile.age); 
                self.weight(profile.weight); 
                self.height(profile.height); 
                self.birthday(profile.birthday); 
                self.purchasesTotal(profile.purchasesTotal); 
                self.userSince(profile.userSince); 
                self.loading(false); 
                self.newProfile(false); 
            }, 
            error: function(request, status, error){
                self.error(true); 
                self.errorMessage("No hemos podido guardar tu perfil, intentalo de nuevo"); 
                self.loading(false); 
            }
        }); 

    }; 
    self.editProfile = function(){
        //hide current card 
        self.hasProfile(false); 
        self.unavailableProfile(false); 
        self.newProfile(true); 
        self.age = ko.observable(); 
        self.height = ko.observable(); 
        self.weight = ko.observable(); 
        self.birthday = ko.observable('Fecha de nacimiento'); 
        $("input").val(''); 
    };
    self.showDatePicker = function(){
        datePicker.show({
            date: new Date(), 
            mode: 'date', 
            titleText: 'Confirma tu fecha de nacimiento', 
            okText: 'Aceptar', 
            cancelText: 'Cancelar', 
            allowOldDates: true, //IOS
            allowFutureDates: true, //IOS
            doneButtonLabel: 'Aceptar', //IOS
            cancelButtonLabel: 'Cancelar' //IOS

        }, function(date){
            //set date 
            self.birthday(date.toLocaleDateString()); 
            $("#newProfileDate").val(date.toLocaleDateString());
            
        }, function(error){

        }); 
    }; 

    self.createProfile = function(){
        self.profileExists(true); 
        self.loading(false); 
        self.newProfile(true); 
        self.unavailableProfile(false); 
    };
    $.ajaxSetup({
        headers : {
        "Authorization" : getAuthToken()
        }
    });
    
    $.ajax({
        url: host + '/shop/profile', 
        type: 'GET', 
        accept: 'application/json', 
        success: function(profile){
            //got profile 
            //set values
            console.log("Profile: " + JSON.stringify(profile)); 
            if(profile.availableProfile == true){
                self.hasProfile(true); 
                self.email(profile.email); 
                self.name(profile.userName); 
                self.age(profile.age); 
                self.weight(profile.weight); 
                self.height(profile.height); 
                self.birthday(profile.birthday); 
                self.purchasesTotal(profile.purchasesTotal); 
                self.userSince(profile.userSince); 
                self.cmi(profile.cmi); 
            }else{
                self.hasProfile(false); 
                self.profileExists(false); 
                self.unavailableProfile(true); 
            }

            self.loading(false);             
        }, 
        error: function(request, status, error){
            self.loading(false);
            self.error(true); 
            self.errorMessage(JSON.stringify(error)); 
        }
    }); 
}

function showErrorMessage(message){
    var result = navigator.notification.confirm(message, function(index){
            
    }, 'Error', 'Aceptar'); 
}

$(function(){
    ko.applyBindings(new ProfileViewModel())
}); 