function a(obj) {
    if(obj.URLparameterMap !=undefined){
        obj.URLparameterMap['numa'] = "20000";
    }
		

	return JSON.stringify(obj);

}
