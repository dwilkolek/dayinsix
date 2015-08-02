

function gup( name, url ) {
  if (!url) url = location.href
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( url );
  return results == null ? null : results[1];
}

$(document).ready(function(){
                                		$("#searchbox").bind("input", function(){
//                                 			console.log("click",$(this).val().length)
                                			if ($(this).val().length >= 3){
//                                 				console.log("post")
                                				$.ajax({
                        							method : "GET",
                        							url : "/explore/"+$(this).val()
                        						}).done(function(msg) {
//                        							console.log(msg)
                        							var result = JSON.parse(msg); 
                        							$to = $(".resultbox");
                        							$to.html("");
                        							if ( result.length == 0) {
                        								
                        								var tpl = "<div class='card explore' >"+"No results..."+"</div>";
														$to.append(tpl);
                        								
                        							}else{
                        							 $.each(result,function(key,val){
//                         							     console.log(val); 
																var tpl ='';
																var vals = val.split(";")
																var sharePage = '<a href="/s/'+vals[0]+'">'+vals[0]+'</a>';
																tpl += sharePage;
																if (vals[2] != 0){
																	if (vals[2] == 1){
																		tpl += '<a class="action" href="/user/follow/'+vals[0]+'">Follow</a>';
																	} else {
																		tpl += '<a class="action" href="/user/unfollow/'+vals[0]+'">Unfollow</a>';
																	}
																	
																}
																if (vals[1] != 0){
																	if (vals[1] == 1){
																		tpl += '<a class="action" href="/user/share/'+vals[0]+'">Share</a>';
																	} else {
																		tpl += '<a class="action" href="/user/unshare/'+vals[0]+'">Unshare</a>';
																	}
																	
																}
// 																var shareA = '<a href="/user/share/'+val+'">Share</a>';
																tpl = "<div class='card explore' >"+tpl+"</div>";
																$to.append(tpl);
                        							    })
                        							}
                        						})
                                			} else {
                                				$(".resultbox").html("<div class='card explore' >"+"Type at least 3 letters..."+"</div>");
                                			}
                                		
                                		})
                                	})
$(document)
		.ready(
				function() {
//					$("#sbmbtn").click(function(event) {
//						event.preventDefault();
////						console.log("click")
//						$.ajax({
//							method : "POST",
//							url : "/login?redirect="+$(location).attr('href'),
//							data : {
//								email : $("#email").val(),
//								password : $("#password").val(),
//								_csrf : $("#csrf").val()
//							}
//						}).done(function(msg) {
//							//console.log(msg)
//							var text = $(msg, "p").text();
//							//console.log(text)
//							vars = text.split("|");
//							if (vars[0] == 'false') {
//								$("#error").show();
//								$("#csrf").val(vars[2])
//							} else {
//								// location.reload();
//								// window.location.href = "/user/day/list";
//							}
//						})
//
//						return false;
//					})
//
//					$("#email").on("input focus change", function() {
//						$("#error").hide();
//					})
//					$("#password").on("input focus change", function() {
//						$("#error").hide();
//					})

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
