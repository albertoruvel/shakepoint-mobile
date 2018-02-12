$(function () {
    ko.applyBindings(new HomeViewModel());
});

/* 
 * Creates a view model for products
 */
function HomeViewModel() {
    var closestMachine = getClosestMachine(); 
    var token = getAuthToken(); 
    console.log("Auth Token: " + token); 
    $.ajaxSetup({
        headers : {
            'Authorization' : token
        }
    }); 

    var self = this;
    self.email = getUserEmail();
    self.products = ko.observableArray([]);
    if(closestMachine == null){
        self.machineId = ko.observable(''); 
    }else{
        self.machineId = ko.observable(closestMachine); 
    }
    self.pagesAvailable = ko.observable(''); 
    self.pageNumber = ko.observable(1); 
    self.error = ko.observable(false); 
    self.loading = ko.observable(false); 
    self.errorMessage = ko.observable('');
    self.gotMachine = ko.observable(false); 
    self.title = ko.observable(closestMachine == null ? 'ShakePoint Mobile' : closestMachine.machineName);

    //*************** PACKAGES ****************
    self.loadingPackages = ko.observable(true); 
    self.packages = ko.observableArray([]); 
    self.packagesErrorMessage = ko.observable(''); 
    self.packagesError = ko.observable(false); 
    //buy product 
    self.buyProduct = function(product){
        //show a confirmation message 
        var result = navigator.notification.confirm("Quieres comprar este producto?", function(index){
            if(index === 1){
                //buy
                createPurchaseRequest(self, product);
            }
        }, 'Comprar producto', 'Comprar,Cancelar'); 

    };

    self.logout = function(){
        //shows a confirm dialog 
        navigator.notification.confirm('¿Estas seguro de salir?', function(index){
            if(index == 1){
                logout(); 
                window.location = '../index.html';
            }
        }, '¿Quieres salir de ShakePoint?', 'Salir,Cancelar');
    }; 
    
    self.loading(true); 
    if (closestMachine){
        console.log("User has a closest machine"); 
        self.machineId(closestMachine.machineId); 
        self.pageNumber(1); 
        getMachineProducts(self); 
        getMachineCombos(self); 
    }
    else{
        console.log("User does not have closest machine"); 
        self.loading(false); 
        self.error(true); 
        self.errorMessage("No has buscado una máquina ShakePoint, busca una desde el menú lateral"); 
        self.packagesError(true); 
        self.packagesErrorMessage('No has buscado una máquina ShakePoint, busca una desde el menú lateral'); 
        self.loadingPackages(false); 
    }
    //search machine 
    self.searchMachine = function () {
        searchMachine(self);
    };
    self.searchAgain = function () {
        searchMachine(self);
    };
}

function getMachineCombos(self){
    console.log('Getting combos');
    $.getJSON(host + '/shop/combos?machine_id=' + self.machineId(), function(response){
        console.log('Received combos: ' + JSON.stringify(response));
        if(response.pageItems.length == 0){
            self.packagesError(true); 
            self.packagesErrorMessage('Esta máquina no vende paquetes'); 
            self.loadingPackages(false); 
        }else{
            console.log('Adding combos to view');
            var item = null; 
            for(var i = 0; i < response.pageItems.length; i ++){
                item = response.pageItems[i]; 
                self.packages.push(item);
                console.log('Inserted package: ' + JSON.stringify(item));
            }
            self.packagesError(false); 
            self.packagesErrorMessage(''); 
            self.loadingPackages(false); 
        }
    });
}

function searchMachine(self) {
    self.gotMachine(false);
    self.loading(true); 
    //get current location 
    console.log("Searching machine"); 
    navigator.geolocation.getCurrentPosition(function (position) {
        latitude = position.coords.latitude;
        longitude = position.coords.longitude;
        if(latitude === null || longitude === null){
            self.loading(false);
            self.error(true); 
            self.errorMessage('No se ha podido obtener tu ubicación, enciende la funcionalidad desde la configuración de tu dispositivo');
            return;
        }
        console.log("Got device coordinates"); 
        getMachine(self, latitude, longitude); 

    }, function (error) {
        console.log('Error: ' + JSON.stringify(error) + '\n'); 
        self.error(true);
        self.loading(false); 
        self.errorMessage('Error al obtener la ubicación de tu dispositivo, intenta de nuevo');  
    },
    { enableHighAccuracy: false, timeout: 15000 });
}

function getMachine(self, latitude, longitude){
    if(self.gotMachine() === true)return; 
    else{
        self.error(false); 
        self.errorMessage(''); 
        self.gotMachine(true); 
        $.ajax({
            contentType: 'application/json;charset=utf-8',
            accept: 'text',
            dataType: 'json',
            url: host + "/shop/search_machine?latitude=" + latitude + "&longitude=" + longitude, 
            type: 'GET', 
            success: function(response){
                console.log("Success response: " + JSON.stringify(response)); 
                saveClosestMachine(response); 
                self.machineId(response.machineId);
                self.pageNumber(1); 
                self.gotMachine(true); 
                getMachineProducts(self);
                getMachineCombos(self); 
            }, 
            error: function(request, status, error){
                console.log("Request: " + request); 
                console.log("Status: " + status); 
                console.log("Error: " + error); 
                self.loading(false); 
                self.error(true); 
                self.errorMessage(JSON.stringify(request)); 
            }
        });
    }
}


function getMachineProducts(self) {
    //create ajax request 
    console.log("Getting machine products"); 
    var machineId = self.machineId();
    console.log("MachineID: " + machineId); 
    var url = host + '/shop/get_products?machine_id=' + machineId + '&page_number=' + self.pageNumber(); 

    $.getJSON(url, function(response){
        if(response.pageItems.length == 0){
                self.error(true); 
                self.errorMessage('No hay resultados, intenta buscar una máquina ShakePoint'); 
                self.pagesAvailable(false);
            }else{
                //clear productos 
                self.products([]); 
                //add products 
                for(var i = 0; i < response.pageItems.length; i ++){
                    self.products.push(response.pageItems[i]); 
                }
                self.pageNumber(response.pageNumber); 
                if(self.pageNumber() < response.pagesAvailable)
                    self.pagesAvailable(true);
                else
                    self.pagesAvailable(false);
            }
            self.loading(false); 
    }); 
}

function createPurchaseRequest(self, product){
    //create an ajax request to pre-purchase a product
    var data = {
       "machineId" : getClosestMachine().machineId, 
       "productId" : product.id, 
       "price" : product.price
    }; 
    
    var url = host + '/shop/request_buy'; 
    var headers = {
        "Authorization" : getAuthToken()
    };
    $.ajax({
        url: url, 
        type: 'POST', 
        contentType: 'application/json',
        dataType: 'json', 
        data: JSON.stringify(data), 
        accept: 'application/json', 
        headers: headers,
        success: function(purchase){
            banwireProduct(purchase, product); 
        }, 
        error: function(purchaseError){
            console.log('Error: ' + JSON.stringify(purchaseError) + '\n');
        }
    }); 
}

function banwireProduct(purchase, product){
    var SW = new BwGateway({
        // Test mode, remove when going to production mode
        sandbox: true,
        // Nombre de usuario de Banwire
        user: 'pruebasbw',
        // Titulo de la entana
        title: "ShakePoint",
        // Referencia
        reference: purchase.purchaseId,
        // Concepto
        concept: 'Compra ShakePoint',
        infoMsg: 'Pago para compra ' + product.name,
        // Moneda
        currency: 'MXN',
        total: purchase.total,
        items: [
            {
                name: product.name, 
                qty: 1, 
                desc: product.desciption, 
                unitPrice: product.price
            }
        ],
        // Customer information
        cust: {
            fname: "",
            mname: "",
            lname: "",
            email: "",
            phone: "",
            addr: "",
            city: "",
            state: "",
            country: "",
            zip: ""
        },
        notifyUrl: "http://192.168.0.7:8080/rest/shop/confirm_purchase",
        // Opciones de pago
        paymentOptions: 'visa,mastercard', // visa,mastercard,amex,oxxo
        // Mostrar o no pagina de resumen de compra
        reviewOrder: true,
        // Handler en caso de exito en el pago
        successPage: '',
        onSuccess: function (data) {
            confirmPurchase(data); 
        },
        // Pago pendiente OXXO 
        pendingPage: '',
        onPending: function (data) {
            console.log('' + JSON.stringify(data) + '\n');
            navigator.notification.alert("El pago esta pendiente por ser efectuado", function(){

            }, 'Pendiente');
        },
        // Pago challenge
        challengePage: '',
        onChallenge: function () {
            navigator.notification.alert("Pago enviado a validaciones de seguridad", function(){

            }, 'Pago a ser validado');
        },
        // Handler en caso de error en el pago
        errorPage: '',
        onError: function (error) {
            navigator.notification.alert("Ha ocurrido un error con tu pago, vuelve a intentarlo", function(){

            }, 'Error');
        },
        // Cuando cierra el popup sin completar el proceso
        onCancel: function () {
            
        }
    });
    SW.pay();
    }

function confirmPurchase(data){
    var url = host + "/shop/confirm_purchase"

    $.ajax({
        url: url,
        type: 'POST',
        data: JSON.stringify({
            "authCode" : data.auth_code, 
            "reference" : data.reference, 
            "total" : data.total, 
            "event" : data.event, 
            "id" : data.id
        }), 
        accept: 'application/json', 
        contentType: 'application/json', 
        dataType: 'json',
        success: function(code){
            //save to tmp qr code 
            saveTmpQrCode(code); 
            //goes to qr code page 
            window.location = "qr-code.html"; 
        }, 
        error: function(request, status, error){
            console.log("error: " + JSON.stringify(error)); 
        }
    });
}

var latitude = null;
var longitude = null; 
var productsPageNumber = null;

