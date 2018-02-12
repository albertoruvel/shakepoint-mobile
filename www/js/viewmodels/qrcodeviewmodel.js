$(function(){
	ko.applyBindings(new QRCodeViewModel()); 
}); 

function QRCodeViewModel(){
	var self = this; 
	self.imageUrl = ko.observable('');
	self.creationDate = ko.observable(''); 
	self.description = ko.observable(''); 
	var tmpCode = getTmpQrCode(); 

	self.imageUrl(tmpCode.imageUrl); 
	self.creationDate(tmpCode.creationDate); 

	self.goBack = function(){
		window.history.back(); 
		//delete current tmp code 
		 deleteTmpQrCode(); 
	}; 
}