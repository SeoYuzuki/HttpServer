function a(obj) {
    if(obj.URLparameterMap !=undefined){
        obj.URLparameterMap['numa'] = "40000";
    }
		
    
	return JSON.stringify(obj);

}
