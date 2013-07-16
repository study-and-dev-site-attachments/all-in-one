var d_zone_rss_splash = null;

startOfTikTak = 0;
tikTakTimerID = 0;
var PAGE_SIZE = 1;
var glob_SavedRSSItemsCount = 0;
var isShowedRSSSplash = 1;

function makeRSSNewsSplash(){
 

 d_zone_rss_splash = document.createElement('div');
 d_zone_rss_splash.id = 'd_zone_rss_splash';
 document.getElementsByTagName ('body')[0].appendChild (d_zone_rss_splash);
 $('#d_zone_rss_splash').addClass ('d_zone_rss_splash'); 


 d_zone_rss_splash_items_container = document.createElement('div');
 d_zone_rss_splash_items_container.id = 'd_zone_rss_splash_items_container';
 $('#d_zone_rss_splash_items_container').addClass ('d_zone_rss_splash_items_container'); 


 d_zone_dummy_while_loading = document.createElement('div');
 d_zone_dummy_while_loading.id = 'd_zone_dummy_while_loading';
 $('#d_zone_dummy_while_loading').addClass ('d_zone_dummy_while_loading'); 
 d_zone_dummy_while_loading.innerHTML = '<div id="loading_rss_1">RSS</div><div id="loading_rss_2">Loading...</div><div id="loading_rss_3">0. ms</div>';	
 document.getElementById ('d_zone_rss_splash').appendChild (d_zone_dummy_while_loading);


 startOfTikTak = new Date ().getTime();
 tikTakTimerID = window.setInterval (makeTikTak , 500);


 $.ajax({
   type: "POST",
   url: "/mediawiki/index.php/Rss",
   data: "",
   dataType: "xml",
   success: function(msg){

     window.clearInterval (tikTakTimerID);
     document.getElementById ('d_zone_rss_splash').removeChild (d_zone_dummy_while_loading);

     var title = 'N/A';
     var xml = true;
     if (xml){
	 title = msg.getElementsByTagName ('title') [0];
	 title = title.firstChild.nodeValue;
     }
     else{
         title = msg['title'];
     }

     var e_big_title = document.createElement('div');     
     e_big_title.innerHTML = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + title;
     e_big_title.className = 'e_big_title_rss_news_splash';	


     var e_close_pic = document.createElement('div');     
     e_close_pic.id = 'e_close_pic';	
     e_close_pic.className = 'e_close_pic';	
     e_close_pic.innerHTML = '<!-- abba --> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
     d_zone_rss_splash.appendChild (e_close_pic);


     e_close_pic.onclick = function (){
	   if (isShowedRSSSplash){
            $ ('#d_zone_rss_splash_items_container').hide ();
            $ ('#e_buttons_title').hide ();
            $ ('#e_close_pic').removeClass ('e_close_pic');
            $ ('#e_close_pic').addClass ('e_restore_pic');
           }
           else{
            $ ('#d_zone_rss_splash_items_container').show ();
            $ ('#e_buttons_title').show ();
            $ ('#e_close_pic').removeClass ('e_restore_pic');
            $ ('#e_close_pic').addClass ('e_close_pic');
           }
	   isShowedRSSSplash = ! isShowedRSSSplash;
     };
     e_big_title.onclick  = e_close_pic.onclick; 


     //----------------------------------------------------
     var e_halt_pic = document.createElement('div');     
     e_halt_pic.className = 'e_halt_pic';	
     e_halt_pic.innerHTML = '<!-- abba --> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
     d_zone_rss_splash.appendChild (e_halt_pic);
     e_halt_pic.onclick = function (){
            $('#d_zone_rss_splash').hide ();       
     };
     //----------------------------------------------------

     d_zone_rss_splash.appendChild (e_big_title);


     var items = new Array ();
     if (xml){
	 items = msg.getElementsByTagName ('item');
     }
     else{
         items = msg.items;
     }

     glob_SavedRSSItemsCount = 10, items.length;

     for (var i = 0; i < glob_SavedRSSItemsCount; i++){

	  var rss_item = items[i];

          var i_title = null;
          var i_link  = null;
          var i_guid  = null;
          var i_pubDate = null;
          var i_description = null;

          if (xml){
            i_title = rss_item.getElementsByTagName ('title')[0].firstChild.nodeValue; 
            i_link  = rss_item.getElementsByTagName ('link')[0].firstChild.nodeValue; 
            i_guid  = rss_item.getElementsByTagName ('guid')[0].firstChild.nodeValue; 
            i_pubDate = rss_item.getElementsByTagName ('pubDate')[0].firstChild.nodeValue; 
            i_description = rss_item.getElementsByTagName ('description')[0].firstChild.nodeValue; 
          }
          else{
            i_title = rss_item.title;
            i_link  = rss_item.url;
            i_guid  = null;
            i_pubDate = rss_item.date;
            i_description = rss_item.body;
          }


          var item = document.createElement('div');
	  item.id = 'news_rss_' + i;
	  item.className = 'div_rss_splash_news_item';


          var e_title = document.createElement('span');
          var e_link  = document.createElement('a');
          var e_pubDate = document.createElement('div');
          var e_description = document.createElement('div');

	  e_title.innerHTML = i_title;
	  e_link.href = i_link;
	  e_link.appendChild (e_title);
          
	  e_pubDate.innerHTML = i_pubDate;
          e_description.innerHTML = i_description;	

	  item.appendChild (e_pubDate);
	  item.appendChild (e_link);
	  item.appendChild (e_description);

          d_zone_rss_splash_items_container.appendChild (item);

	  // ---- css styles ----
          e_link.className = 'a_link_rss_news_splash';
          e_pubDate.className = 'div_date_rss_news_splash';
          e_description.className = 'div_description_rss_news_splash';
     }
     

     d_zone_rss_splash.appendChild (d_zone_rss_splash_items_container);

     var e_buttons_title = document.createElement('div');     
     e_buttons_title.id = 'e_buttons_title';	
     e_buttons_title.className = 'e_buttons_title_rss_news_splash';	
     d_zone_rss_splash.appendChild (e_buttons_title);


     var pages_count =  Math.ceil (glob_SavedRSSItemsCount / PAGE_SIZE);
     for (var i = 0;  i < pages_count; i++){

          var page = document.createElement('div');
	  page.id = 'page_news_rss_' + i;
	  page.page_num = i;
	  page.className = 'e_page_button_rss_news_splash';
	  page.innerHTML = (1+i);
          e_buttons_title.appendChild (page);
          page.onclick = function (){
              triggerPageButtonClick (this.page_num);
          }
       
     }


     var page = document.createElement('div');
     page.id = 'page_news_rss_all';
     page.page_num = i; 
     page.className = 'e_page_button_rss_news_splash';
     page.innerHTML = 'ВСЕ';
     e_buttons_title.appendChild (page);
     page.onclick = function (){
       window.location.href = '/mediawiki/index.php/Rss?showallashtml';
     }
   
     triggerPageButtonClick (0);
   }// if success ajax call
 }); // make ajax call

}


function triggerPageButtonClick (page_num){
     var p_start = 0;
     if (page_num > 0)
         p_start = (page_num )* PAGE_SIZE;

     var p_end = (page_num + 1 )* PAGE_SIZE - 1;
     for (var i = 0; i < glob_SavedRSSItemsCount; i++){
	  var item = document.getElementById ('news_rss_' + i);
          if ( (i >= p_start) && (i <= p_end ) ){
            $(item).css({ display: "" }); 
          }
          else{
            $(item).css({ display: "none" }); 
          }
     }
     var pages_count =  Math.ceil (glob_SavedRSSItemsCount / PAGE_SIZE);
     for (var i = 0;  i < pages_count; i++){
       $('#page_news_rss_' + i).removeClass ('btn_page_active');
     }

     $('#page_news_rss_' + page_num).addClass ('btn_page_active');
}


function makeTikTak (){
  if (document.getElementById ('loading_rss_3') != null)
    document.getElementById ('loading_rss_3').innerHTML = (new Date().getTime() - startOfTikTak ) + ' ms.';
}
