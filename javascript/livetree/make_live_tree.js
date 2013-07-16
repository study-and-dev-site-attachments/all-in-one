     var URL_FOR_CHART_GEN = 'makechartget.php';

     $(document).ready(function(){
     // eaeea-oi aaenoaey

	$.each( $('div.treetag'), function(i, n){
          //alert( $(n).attr ('spec_w') );
          n = $(n);
	  var spec_w = n.attr ('spec_w');
	  var spec_h = n.attr ('spec_h');

	  n.load(URL_FOR_CHART_GEN + '?maketag=true',
	   {spec_w: spec_w, spec_h: spec_h, content: n.html()},
		   function() { 
		     
		   } 
	  );	
	});
     });
