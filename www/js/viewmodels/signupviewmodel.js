$(function(){
    ko.applyBindings(new SignupViewModel());
}); 

function SignupViewModel(){
    var self = this; 
    self.name = ko.observable(''); 
    self.email = ko.observable(''); 
    self.password1 = ko.observable(''); 
    self.password2 = ko.observable(''); 
    self.error = ko.observable(false); 
    self.errorMessage = ko.observable(''); 

    self.signup = function(){
        //validate values 
        if(! self.name()){
            self.error(true); 
            self.errorMessage('No ha especificado un nombre'); 
            return; 
        }else if(! self.email()){
            self.error(true); 
            self.errorMessage('No ha especificado un correo electrónico'); 
            return; 
        }else if(! self.password1()){
            self.error(true); 
            self.errorMessage('Debes de especificar una contraseña'); 
            return; 
        }else if(! self.password2()){
            self.error(true); 
            self.errorMessage('Debes de confirmar tu contraseña'); 
            return; 
        }else if(self.password1() !== self.password2()){
            self.error(true); 
            self.errorMessage('Las contraseñas no coinciden'); 
            return; 
        }

        self.error(false); 
        //create an ajax request 
        $.ajax({
            url: host + "/security/signup", 
            type: 'POST', 
            contentType: 'application/json', 
            accept: 'application/json', 
            dataType: 'json', 
            data: JSON.stringify({
                "name": self.name(), 
                "email": self.email(), 
                "password": self.password1()
            }),
            success: function(response){
                saveAuthToken(response.authenticationToken); 
                saveUserEmail(self.email()); 
                //redirect 
                window.location = "member/index.html"; 
            }, 
            error: function(response){
                self.error(true); 
                self.errorMessage(JSON.stringify(response)); 
            }
        }); 
    }; 
}