$(function(){
	ko.applyBindings(new ActiveCodesViewModel()); 
}); 

function ActiveCodesViewModel(){
	var currentMachine = getClosestMachine(); 
	if(currentMachine === null){
		var result = navigator.notification.confirm("No se ha encontrado una máquina guardada, se redirigirá a la pantalla principal", function(index){
            window.location = "index.html"; 
        }, 'Máquina actual', 'Aceptar'); 
	}
	var self = this; 
	self.loading = ko.observable(false);
	self.error = ko.observable(false); 
	self.errorMessage = ko.observable(''); 
	self.codes = ko.observableArray([]); 
	self.pageNumber = ko.observable(1); 
	self.pagesAvailable = ko.observable(false); 
	self.machineName = ko.observable(currentMachine.machineName); 
	self.machineId = ko.observable(currentMachine.machineId); 

	self.viewCode = function(code){
		deleteTmpQrCode(); 
		saveTmpQrCode(code); 
		window.location = "qr-code.html"; 
	}; 

	//get codes 
	$.ajaxSetup({
		headers: {
			"Authorization" : getAuthToken()
		}
	}); 

	self.loading(true); 
	$.ajax({
		url: host + '/shop/get_active_codes?machine_id=' + self.machineId() + '&page_number=' + self.pageNumber(), 
		type: 'GET', 
		accept: 'application/json', 
		success: function(response){
			//create list 
			if(response.pageItems.length == 0){
				//no items 
				self.error(true); 
				self.errorMessage("Aún no has comprado nada en ShakePoint, realiza alguna compra, y tus códigos activos aparecerán aquí listos para ser canjeados en la máquina donde los compraste"); 

			}else{
				for(var i = 0; i < response.pageItems.length; i ++){
					self.codes.push(response.pageItems[i]); 
				}
			}
			self.loading(false); 
		}, 
		error: function(request, status, error){
			self.loading(false); 
			self.error(true); 
			self.errorMessage("Ha ocurrido un error al obtener tus códigos, intenta de nuevo"); 
		}
	}); 
}