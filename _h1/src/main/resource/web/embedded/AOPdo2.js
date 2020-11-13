function a(obj) {
    if(obj !=undefined){
    	if (obj['numa'] !=undefined){
    		obj['numa'] = "40000";
    	}
        
    }		
    
	return JSON.stringify(obj);
}
