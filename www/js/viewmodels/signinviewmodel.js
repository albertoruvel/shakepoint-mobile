
$(function(){
    ko.applyBindings(new SigninViewModel()); 
});

function SigninViewModel(){
    var self = this; 
    self.email = ko.observable(''); 
    self.password = ko.observable('');
    self.error = ko.observable(false); 
    self.errorMessage = ko.observable('');
    self.signin = function(){
        if(self.email() && self.password()){
            var signinData = $("#signinForm").serialize();
            
            $.support.corps = true; 
            $.ajax({
                url: host + '/shop/shakepoint_rest_security_check',
                type: "POST",
                data: signinData,
                crossDomain: true,
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                statusCode: {
                    200: function(response){
                        if(response.success == true){
                            saveAuthToken(response.authenticationToken); 
                            saveUserEmail(self.email()); 
                            window.location = 'member/index.html'; 
                        }else{
                            self.error(true); 
                            self.errorMessage("Autenticación fallida, intenta de nuevo");
                        }
                    }
                }
            });
        }else{
            self.error(true); 
            self.errorMessage("Debes de ingresar ambos campos para iniciar sesión"); 
        }
    };
}