$(function(){
	ko.applyBindings(new PurchasesViewModel()); 
}); 

function PurchasesViewModel(){
	var self = this; 
	self.loading = ko.observable(false); 
	self.error = ko.observable(false); 
	self.errorMessage = ko.observable(''); 
	self.purchases = ko.observableArray([]); 
	self.pageNumber = ko.observable(1);
	self.pagesAvailable = ko.observable(false);
	self.viewCode = function(purchase){
		deleteTmpQrCode(); 
		saveTmpQrCode({
			"creationDate" : purchase.prchaseDate, 
			"imageUrl" : purchase.qrCodeUrl
		}); 
		window.location = "qr-code.html"; 
	}; 

	self.loading(true); 

	$.ajaxSetup({
		headers: {
			"Authorization" : getAuthToken()
		}
	}); 
	//get purchases
	$.ajax({
		url: host + '/shop/get_purchases', 
		type: 'GET', 
		accept: 'application/json', 
		success:  function(response){
			console.log("Got response: " + JSON.stringify(response)); 
			//add all responses 
			if(response.pageItems.length == 0){
				self.error(true); 
				self.errorMessage('No has comprado nada en ShakePoint, cuando realices compras, aparecerán en esta sección'); 
			}else{
				for(var i = 0; i < response.pageItems.length; i ++){
					self.purchases.push(response.pageItems[i]); 
				}
			}
			self.loading(false); 
		}, 
		error: function(request, status, error){
			console.log("Request: " + JSON.stringify(request)); 
			console.log("Status: " + JSON.stringify(status)); 
			console.log("Error: " + JSON.stringify(error)); 
			self.error(false); 
			self.errorMessage('Ha ocurrido un error al obtener tus compras, intentalo de nuevo');
		}
	}); 
}