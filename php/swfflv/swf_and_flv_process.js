function __process_flv_and_swf(i, n){
        //debugger

        var id = $(n).attr ('id');
        var so = new SWFObject((id.indexOf ('flv_')==-1?window[id].__filesource:window[id].__flvplayer), id+"_num_" +i, 
          window[id].width, window[id].height, ('version' in window[id]?window[id].version:'7'), ('bgcolor' in window[id]?window[id].bgcolor:"#ffffff")
        );
        if ('wmode' in window[id])
           so.addParam("wmode", window[id].wmode);
        if ('quality' in window[id])
	   so.addParam('quality', window[id].quality);

        if ('useExpressInstall' in window[id])
	   so.setAttribute('useExpressInstall', window[id].useExpressInstall);
        if ('redirectUrl' in window[id])
	   so.setAttribute('redirectUrl', window[id].redirectUrl);
        if ('xiRedirectUrl' in window[id])
	   so.addParam('xiRedirectUrl', window[id].xiRedirectUrl);


        if (id.indexOf ('flv_') === 0){
          so.addVariable("file", window[id].__filesource);
	  so.addVariable("image", window[id].__dummy);
        }


	var forbidden = {width:0, height:0, bgcolor:0, wmode:0, quality:0, useExpressInstall:0, redirectUrl:0, xiRedirectUrl:0};


        for (var key in window[id])
	 if (!(key in forbidden))
	   so.addVariable(key, window[id][key]);

        if (!so.write(id)){
          $(n).html('<img src="'+window[id].__dummy+'" alt="" border="0"  width="'+window[id].width+'" height="'+window[id].height+'" >');           
	}

}



$(document).ready(function(){
  $.each( $('div[@id^=swf_]'), __process_flv_and_swf  );
  $.each( $('div[@id^=flv_]'), __process_flv_and_swf  );
});

