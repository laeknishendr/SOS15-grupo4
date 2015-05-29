$(document).ready(function() {
	$(document).ready(function(){
    	$('.logo').fadeIn('slow'); 
    	$('.headertext').slideDown('slow'); 
	});
});

$('#tabla').hover(function(){
	$('#tab-drop').slideDown(); 
}, function(){
	$('#tab-drop').slideUp(); 
})
