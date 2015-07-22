$(document).ready(function() {
	$("#sbmbtn").click(function(event) {
		event.preventDefault();
		console.log("click")
		$.ajax({
			method : "POST",
			url : "/login",
			data : {
				email : $("#email").val(),
				password : $("#password").val(),
				_csrf : $("#csrf").val()
			}
		}).done(function(msg) {
			console.log(msg)
			var text = $(msg, "p").text();
			console.log(text)
			vars = text.split("|");
			if (vars[0] == 'false') {
				$("#error").show();
				$("#csrf").val(vars[2])
			} else {
				//location.reload();
				window.location.href = "/user/day/list";
			}
		})

		return false;
	})

	$("#email").on("input focus change", function() {
		$("#error").hide();
	})
	$("#password").on("input focus change", function() {
		$("#error").hide();
	})
	
	
	$('.logout').click(function(e){
		e.preventDefault();
		$("#logoutButton").click();
	})
})
