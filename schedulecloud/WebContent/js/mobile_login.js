$(document).on('pagebeforeshow', '#login', function(){  
        $(document).on('click', '#submit', function() { // catch the form's submit event
        	var username = window.localStorage["username"];
        	if(username==='')
        		username = $('#j_username_input').val();
        	var password = window.localStorage["password"];
        	if(password==='')
        		password=$('#j_password_input').val();
        	console.log(username+" "+password);
        if($('#j_username_input').val().length > 0 && $('#j_password_input').val().length > 0){
            // Send data to server through ajax call
            // action is functionality we want to call and outputJSON is our data
                $.ajax({url: 'http://localhost:8181/schedulecloud/j_spring_security_check',
                    data: {j_username_input:username,j_password_input:password}, // Convert a form to a JSON string representation
                    type: 'post',                   
                    async: true,
                    beforeSend: function() {
                        // This callback function will trigger before data is sent
                    	window.localStorage["username"] = username;  
                        window.localStorage["password"] = password;
                        $.mobile.showPageLoadingMsg(true); // This will show ajax spinner
                    },
                    complete: function() {
                        // This callback function will trigger on data sent/received complete
                    	
                        $.mobile.hidePageLoadingMsg(); // This will hide ajax spinner
                    },
                    success: function (result) {
                            resultObject.formSubmitionResult = result;
                            
                            $.mobile.changePage("#second");
                    },
                    error: function (request,error) {
                        // This callback function will trigger on unsuccessful action                
                        alert('Network error has occurred please try again!');
                    }
                });                   
        } else {
            alert('Please fill all nececery fields');
        }           
            return false; // cancel original event to prevent form submitting
        });    
});

$(document).on('pagebeforeshow', '#second', function(){     
    $('#second [data-role="content"]').append('This is a result of form submition: ' + resultObject.formSubmitionResult);  
});

var resultObject = {
    formSubmitionResult : null  
}