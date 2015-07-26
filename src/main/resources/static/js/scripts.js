$(document)
		.ready(
				function() {
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
								// location.reload();
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

					$('.logout').click(function(e) {
						e.preventDefault();
						$("#logoutButton").click();
					})

					$(document).click(function() {
						$(".wordList").html("")
					})

					$(".sugesstionAutocomplete")
							.on("keyup",
									function(e) {
										if (e.keyCode >= 46 && e.keyCode <= 90 || e.keyCode == 8) {
											value = $(this).val();
											$(this).val(value);
											$.get("/sugestions?letters="+ value,
															function(data) {
																var text = $(
																		data,
																		"p")
																		.text();
																words = JSON
																		.parse(text);
																inputid = $(
																		this)
																		.attr(
																				"id");
																var id = "#"
																		+ inputid
																		+ '_list';
																$(id).html("");
																var html = "";
																for (word in words) {
																	html += '<a href="javascript:void(0)" data-parent="'
																			+ inputid
																			+ '" class="list-group-item" >'
																			+ words[word]
																			+ '</a>';
																}
																$(id)
																		.html(
																				html);
																$(".wordList")
																		.hide();
																$(id).show()
																$(".wordList a")
																		.click(
																				function() {
																					var parent = $(
																							this)
																							.attr(
																									"data-parent");
																					var word = $(
																							this)
																							.text();
																					$(
																							"#"
																									+ parent)
																							.val(
																									word);
																					$(
																							this)
																							.parent()
																							.html(
																									"")
																					$(
																							this)
																							.parent()
																							.hide();
																				})
															}.bind(this));
										}
									})

				})
