package aw.rmjtromp.RunicCore.core;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitTask;
import org.reflections.Reflections;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicOfflinePlayer;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.Debug.Debuggable;
import aw.rmjtromp.RunicCore.utilities.DependencyManager;
import aw.rmjtromp.RunicCore.utilities.DependencyManager.Dependency;
import aw.rmjtromp.RunicCore.utilities.MySQL;
import aw.rmjtromp.RunicCore.utilities.configs.Config;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public final class Core implements Listener, Debuggable {
	
	public String getName() {
		return "Core";
	}

	private static final RunicCore plugin = RunicCore.getInstance();
	public static final List<String> tlds = Arrays.asList("aaa","aarp","abarth","abb","abbott","abbvie","abc","able","abogado","abudhabi","ac","academy","accenture","accountant","accountants","aco","active","actor","ad","adac","ads","adult","ae","aeg","aero","aetna","af","afamilycompany","afl","africa","ag","agakhan","agency","ai","aig","aigo","airbus","airforce","airtel","akdn","al","alfaromeo","alibaba","alipay","allfinanz","allstate","ally","alsace","alstom","am","americanexpress","americanfamily","amex","amfam","amica","amsterdam","an","analytics","android","anquan","anz","ao","aol","apartments","app","apple","aq","aquarelle","ar","arab","aramco","archi","army","arpa","art","arte","as","asda","asia","associates","at","athleta","attorney","au","auction","audi","audible","audio","auspost","author","auto","autos","avianca","aw","aws","ax","axa","az","azure","ba","baby","baidu","banamex","bananarepublic","band","bank","bar","barcelona","barclaycard","barclays","barefoot","bargains","baseball","basketball","bauhaus","bayern","bb","bbc","bbt","bbva","bcg","bcn","bd","be","beats","beauty","beer","bentley","berlin","best","bestbuy","bet","bf","bg","bh","bharti","bi","bible","bid","bike","bing","bingo","bio","biz","bj","bl","black","blackfriday","blanco","blockbuster","blog","bloomberg","blue","bm","bms","bmw","bn","bnl","bnpparibas","bo","boats","boehringer","bofa","bom","bond","boo","book","booking","boots","bosch","bostik","boston","bot","boutique","box","bq","br","bradesco","bridgestone","broadway","broker","brother","brussels","bs","bt","budapest","bugatti","build","builders","business","buy","buzz","bv","bw","by","bz","bzh","ca","cab","cafe","cal","call","calvinklein","cam","camera","camp","cancerresearch","canon","capetown","capital","capitalone","car","caravan","cards","care","career","careers","cars","cartier","casa","case","caseih","cash","casino","cat","catering","catholic","cba","cbn","cbre","cbs","cc","cd","ceb","center","ceo","cern","cf","cfa","cfd","cg","ch","chanel","channel","charity","chase","chat","cheap","chintai","chloe","christmas","chrome","chrysler","church","ci","cipriani","circle","cisco","citadel","citi","citic","city","cityeats","ck","cl","claims","cleaning","click","clinic","clinique","clothing","cloud","club","clubmed","cm","cn","co","coach","codes","coffee","college","cologne","com","comcast","commbank","community","company","compare","computer","comsec","condos","construction","consulting","contact","contractors","cooking","cookingchannel","cool","coop","corsica","country","coupon","coupons","courses","cr","credit","creditcard","creditunion","cricket","crown","crs","cruise","cruises","csc","cu","cuisinella","cv","cw","cx","cy","cymru","cyou","cz","dabur","dad","dance","data","date","dating","datsun","day","dclk","dds","de","deal","dealer","deals","degree","delivery","dell","deloitte","delta","democrat","dental","dentist","desi","design","dev","dhl","diamonds","diet","digital","direct","directory","discount","discover","dish","diy","dj","dk","dm","dnp","do","docs","doctor","dodge","dog","doha","domains","doosan","dot","download","drive","dtv","dubai","duck","dunlop","duns","dupont","durban","dvag","dvr","dz","earth","eat","ec","eco","edeka","edu","education","ee","eg","eh","email","emerck","energy","engineer","engineering","enterprises","epost","epson","equipment","er","ericsson","erni","es","esq","estate","esurance","et","etisalat","eu","eurovision","eus","events","everbank","exchange","expert","exposed","express","extraspace","fage","fail","fairwinds","faith","family","fan","fans","farm","farmers","fashion","fast","fedex","feedback","ferrari","ferrero","fi","fiat","fidelity","fido","film","final","finance","financial","fire","firestone","firmdale","fish","fishing","fit","fitness","fj","fk","flickr","flights","flir","florist","flowers","flsmidth","fly","fm","fo","foo","food","foodnetwork","football","ford","forex","forsale","forum","foundation","fox","fr","free","fresenius","frl","frogans","frontdoor","frontier","ftr","fujitsu","fujixerox","fun","fund","furniture","futbol","fyi","ga","gal","gallery","gallo","gallup","game","games","gap","garden","gb","gbiz","gd","gdn","ge","gea","gent","genting","george","gf","gg","ggee","gh","gi","gift","gifts","gives","giving","gl","glade","glass","gle","global","globo","gm","gmail","gmbh","gmo","gmx","gn","godaddy","gold","goldpoint","golf","goo","goodhands","goodyear","goog","google","gop","got","gov","gp","gq","gr","grainger","graphics","gratis","green","gripe","grocery","group","gs","gt","gu","guardian","gucci","guge","guide","guitars","guru","gw","gy","hair","hamburg","hangout","haus","hbo","hdfc","hdfcbank","health","healthcare","help","helsinki","here","hermes","hgtv","hiphop","hisamitsu","hitachi","hiv","hk","hkt","hm","hn","hockey","holdings","holiday","homedepot","homegoods","homes","homesense","honda","honeywell","horse","hospital","host","hosting","hot","hoteles","hotels","hotmail","house","how","hr","hsbc","ht","htc","hu","hughes","hyatt","hyundai","ibm","icbc","ice","icu","id","ie","ieee","ifm","iinet","ikano","il","im","imamat","imdb","immo","immobilien","in","industries","infiniti","info","ing","ink","institute","insurance","insure","int","intel","international","intuit","investments","io","ipiranga","iq","ir","irish","is","iselect","ismaili","ist","istanbul","it","itau","itv","iveco","iwc","jaguar","java","jcb","jcp","je","jeep","jetzt","jewelry","jio","jlc","jll","jm","jmp","jnj","jo","jobs","joburg","jot","joy","jp","jpmorgan","jprs","juegos","juniper","kaufen","kddi","ke","kerryhotels","kerrylogistics","kerryproperties","kfh","kg","kh","ki","kia","kim","kinder","kindle","kitchen","kiwi","km","kn","koeln","komatsu","kosher","kp","kpmg","kpn","kr","krd","kred","kuokgroup","kw","ky","kyoto","kz","la","lacaixa","ladbrokes","lamborghini","lamer","lancaster","lancia","lancome","land","landrover","lanxess","lasalle","lat","latino","latrobe","law","lawyer","lb","lc","lds","lease","leclerc","lefrak","legal","lego","lexus","lgbt","li","liaison","lidl","life","lifeinsurance","lifestyle","lighting","like","lilly","limited","limo","lincoln","linde","link","lipsy","live","living","lixil","lk","llc","loan","loans","locker","locus","loft","lol","london","lotte","lotto","love","lpl","lplfinancial","lr","ls","lt","ltd","ltda","lu","lundbeck","lupin","luxe","luxury","lv","ly","ma","macys","madrid","maif","maison","makeup","man","management","mango","map","market","marketing","markets","marriott","marshalls","maserati","mattel","mba","mc","mcd","mcdonalds","mckinsey","md","me","med","media","meet","melbourne","meme","memorial","men","menu","meo","merckmsd","metlife","mf","mg","mh","miami","microsoft","mil","mini","mint","mit","mitsubishi","mk","ml","mlb","mls","mm","mma","mn","mo","mobi","mobile","mobily","moda","moe","moi","mom","monash","money","monster","montblanc","mopar","mormon","mortgage","moscow","moto","motorcycles","mov","movie","movistar","mp","mq","mr","ms","msd","mt","mtn","mtpc","mtr","mu","museum","mutual","mutuelle","mv","mw","mx","my","mz","na","nab","nadex","nagoya","name","nationwide","natura","navy","nba","nc","ne","nec","net","netbank","netflix","network","neustar","new","newholland","news","next","nextdirect","nexus","nf","nfl","ng","ngo","nhk","ni","nico","nike","nikon","ninja","nissan","nissay","nl","no","nokia","northwesternmutual","norton","now","nowruz","nowtv","np","nr","nra","nrw","ntt","nu","nyc","nz","obi","observer","off","office","okinawa","olayan","olayangroup","oldnavy","ollo","om","omega","one","ong","onl","online","onyourside","ooo","open","oracle","orange","org","organic","orientexpress","origins","osaka","otsuka","ott","ovh","pa","page","pamperedchef","panasonic","panerai","paris","pars","partners","parts","party","passagens","pay","pccw","pe","pet","pf","pfizer","pg","ph","pharmacy","phd","philips","phone","photo","photography","photos","physio","piaget","pics","pictet","pictures","pid","pin","ping","pink","pioneer","pizza","pk","pl","place","play","playstation","plumbing","plus","pm","pn","pnc","pohl","poker","politie","porn","post","pr","pramerica","praxi","press","prime","pro","prod","productions","prof","progressive","promo","properties","property","protection","pru","prudential","ps","pt","pub","pw","pwc","py","qa","qpon","quebec","quest","qvc","racing","radio","raid","re","read","realestate","realtor","realty","recipes","red","redstone","redumbrella","rehab","reise","reisen","reit","reliance","ren","rent","rentals","repair","report","republican","rest","restaurant","review","reviews","rexroth","rich","richardli","ricoh","rightathome","ril","rio","rip","rmit","ro","rocher","rocks","rodeo","rogers","room","rs","rsvp","ru","rugby","ruhr","run","rw","rwe","ryukyu","sa","saarland","safe","safety","sakura","sale","salon","samsclub","samsung","sandvik","sandvikcoromant","sanofi","sap","sapo","sarl","sas","save","saxo","sb","sbi","sbs","sc","sca","scb","schaeffler","schmidt","scholarships","school","schule","schwarz","science","scjohnson","scor","scot","sd","se","search","seat","secure","security","seek","select","sener","services","ses","seven","sew","sex","sexy","sfr","sg","sh","shangrila","sharp","shaw","shell","shia","shiksha","shoes","shop","shopping","shouji","show","showtime","shriram","si","silk","sina","singles","site","sj","sk","ski","skin","sky","skype","sl","sling","sm","smart","smile","sn","sncf","so","soccer","social","softbank","software","sohu","solar","solutions","song","sony","soy","space","spiegel","sport","spot","spreadbetting","sr","srl","srt","ss","st","stada","staples","star","starhub","statebank","statefarm","statoil","stc","stcgroup","stockholm","storage","store","stream","studio","study","style","su","sucks","supplies","supply","support","surf","surgery","suzuki","sv","swatch","swiftcover","swiss","sx","sy","sydney","symantec","systems","sz","tab","taipei","talk","taobao","target","tatamotors","tatar","tattoo","tax","taxi","tc","tci","td","tdk","team","tech","technology","tel","telecity","telefonica","temasek","tennis","teva","tf","tg","th","thd","theater","theatre","tiaa","tickets","tienda","tiffany","tips","tires","tirol","tj","tjmaxx","tjx","tk","tkmaxx","tl","tm","tmall","tn","to","today","tokyo","tools","top","toray","toshiba","total","tours","town","toyota","toys","tp","tr","trade","trading","training","travel","travelchannel","travelers","travelersinsurance","trust","trv","tt","tube","tui","tunes","tushu","tv","tvs","tw","tz","ua","ubank","ubs","uconnect","ug","uk","um","unicom","university","uno","uol","ups","us","uy","uz","va","vacations","vana","vanguard","vc","ve","vegas","ventures","verisign","versicherung","vet","vg","vi","viajes","video","vig","viking","villas","vin","vip","virgin","visa","vision","vista","vistaprint","viva","vivo","vlaanderen","vn","vodka","volkswagen","volvo","vote","voting","voto","voyage","vu","vuelos","wales","walmart","walter","wang","wanggou","warman","watch","watches","weather","weatherchannel","webcam","weber","website","wed","wedding","weibo","weir","wf","whoswho","wien","wiki","williamhill","win","windows","wine","winners","wme","wolterskluwer","woodside","work","works","world","wow","ws","wtc","wtf","xbox","xerox","xfinity","xihuan","xin","æµ‹è¯•","à¤•à¥‰à¤®","à¤ªà¤°à¥€à¤•à¥�à¤·à¤¾","ã‚»ãƒ¼ãƒ«","ä½›å±±","à²­à²¾à²°à²¤","æ…ˆå–„","é›†å›¢","åœ¨çº¿","í•œêµ­","à¬­à¬¾à¬°à¬¤","å¤§ä¼—æ±½è½¦","ç‚¹çœ‹","à¸„à¸­à¸¡","à¦­à¦¾à§°à¦¤","à¦­à¦¾à¦°à¦¤","å…«å�¦","â€�Ù…ÙˆÙ‚Ø¹â€Ž","à¦¬à¦¾à¦‚à¦²à¦¾","å…¬ç›Š","å…¬å�¸","é¦™æ ¼é‡Œæ‹‰","ç½‘ç«™","ç§»åŠ¨","æˆ‘çˆ±ä½ ","�?¼�?¾Ñ��?º�?²�?°","�?¸Ñ��?¿Ñ‹Ñ‚�?°�?½�?¸�?µ","Ò›�?°�?·","�?º�?°Ñ‚�?¾�?»�?¸�?º","�?¾�?½�?»�?°�?¹�?½","Ñ��?°�?¹Ñ‚","è��?é€š","Ñ�Ñ€�?±","�?±�?³","�?±�?µ�?»","â€�×§×•×�â€Ž","æ—¶å°š","å¾®å�š","í…ŒìŠ¤íŠ¸","æ·¡é©¬é�?¡","ãƒ•ã‚¡ãƒƒã‚·ãƒ§ãƒ³","�?¾Ñ€�?³","à¤¨à¥‡à¤Ÿ","ã‚¹ãƒˆã‚¢","ì‚¼ì„±","à®šà®¿à®™à¯�à®•à®ªà¯�à®ªà¯‚à®°à¯�","å•†æ ‡","å•†åº—","å•†åŸŽ","�?´�?µÑ‚�?¸","�?¼�?º�?´","â€�×˜×¢×¡×˜â€Ž","�?µÑŽ","ãƒ�ã‚¤ãƒ³ãƒˆ","æ–°é—»","å·¥è¡Œ","å®¶é›»","â€�ÙƒÙˆÙ…â€Ž","ä¸­æ–‡ç½‘","ä¸­ä¿¡","ä¸­å›½","ä¸­åœ‹","å¨±ä¹�","è°·æ­Œ","à°­à°¾à°°à°¤à±�","à¶½à¶‚à¶šà·�","é›»è¨Šç›ˆç§‘","è´­ç‰©","æ¸¬è©¦","ã‚¯ãƒ©ã‚¦ãƒ‰","àª­àª¾àª°àª¤","é€šè²©","à¤­à¤¾à¤°à¤¤à¤®à¥�","à¤­à¤¾à¤°à¤¤","à¤­à¤¾à¤°à¥‹à¤¤","â€�Ø¢Ø²Ù…Ø§ÛŒØ´ÛŒâ€Ž","à®ªà®°à®¿à®Ÿà¯�à®šà¯ˆ","ç½‘åº—","à¤¸à¤‚à¤—à¤ à¤¨","é¤�åŽ…","ç½‘ç»œ","�?º�?¾�?¼","Ñƒ�?ºÑ€","é¦™æ¸¯","è¯ºåŸºäºš","é£Ÿå“�","Î´Î¿ÎºÎ¹Î¼Î®","é£žåˆ©æµ¦","â€�Ø¥Ø®ØªØ¨Ø§Ø±â€Ž","å�°æ¹¾","å�°ç�£","æ‰‹è¡¨","æ‰‹æœº","�?¼�?¾�?½","â€�Ø§Ù„Ø¬Ø²Ø§Ø¦Ø±â€Ž","â€�Ø¹Ù…Ø§Ù†â€Ž","â€�Ø§Ø±Ø§Ù…ÙƒÙˆâ€Ž","â€�Ø§ÛŒØ±Ø§Ù†â€Ž","â€�Ø§Ù„Ø¹Ù„ÙŠØ§Ù†â€Ž","â€�Ø§ØªØµØ§Ù„Ø§Øªâ€Ž","â€�Ø§Ù…Ø§Ø±Ø§Øªâ€Ž","â€�Ø¨Ø§Ø²Ø§Ø±â€Ž","â€�Ù…ÙˆØ±ÙŠØªØ§Ù†ÙŠØ§â€Ž","â€�Ù¾Ø§Ú©Ø³ØªØ§Ù†â€Ž","â€�Ø§Ù„Ø§Ø±Ø¯Ù†â€Ž","â€�Ù…ÙˆØ¨Ø§ÙŠÙ„ÙŠâ€Ž","â€�Ø¨Ø§Ø±Øªâ€Ž","â€�Ø¨Ú¾Ø§Ø±Øªâ€Ž","â€�Ø§Ù„Ù…ØºØ±Ø¨â€Ž","â€�Ø§Ø¨ÙˆØ¸Ø¨ÙŠâ€Ž","â€�Ø§Ù„Ø³Ø¹ÙˆØ¯ÙŠØ©â€Ž","â€�Ú€Ø§Ø±Øªâ€Ž","â€�ÙƒØ§Ø«ÙˆÙ„ÙŠÙƒâ€Ž","â€�Ø³ÙˆØ¯Ø§Ù†â€Ž","â€�Ù‡Ù…Ø±Ø§Ù‡â€Ž","â€�Ø¹Ø±Ø§Ù‚â€Ž","â€�Ù…Ù„ÙŠØ³ÙŠØ§â€Ž","æ¾³é–€","ë‹·ì»´","æ�?¿åºœ","â€�Ø´Ø¨ÙƒØ©â€Ž","â€�Ø¨ÙŠØªÙƒâ€Ž","â€�Ø¹Ø±Ø¨â€Ž","áƒ’áƒ�?","æœºæž„","ç»„ç»‡æœºæž„","å�¥åº·","à¹„à¸—à¸¢","â€�Ø³ÙˆØ±ÙŠØ©â€Ž","æ‹›è�˜","Ñ€ÑƒÑ�","Ñ€Ñ„","ç� å®�","â€�ØªÙˆÙ†Ø³â€Ž","å¤§æ‹¿","ã�¿ã‚“ã�ª","ã‚°ãƒ¼ã‚°ãƒ«","ÎµÎ»","ä¸–ç•Œ","æ›¸ç±�","à´­à´¾à´°à´¤à´‚","à¨­à¨¾à¨°à¨¤","ç½‘å�€","ë‹·ë„·","ã‚³ãƒ ","å¤©ä¸»æ•™","æ¸¸æˆ�","vermÃ¶gensberater","vermÃ¶gensberatung","ä¼�ä¸š","ä¿¡æ�¯","å˜‰é‡Œå¤§é…’åº—","å˜‰é‡Œ","â€�Ù…ØµØ±â€Ž","â€�Ù‚Ø·Ø±â€Ž","å¹¿ä¸œ","à®‡à®²à®™à¯�à®•à¯ˆ","à®‡à®¨à¯�à®¤à®¿à®¯à®¾","Õ°Õ¡Õµ","æ–°åŠ å�¡","â€�Ù�Ù„Ø³Ø·ÙŠÙ†â€Ž","ãƒ†ã‚¹ãƒˆ","æ�?¿åŠ¡","xperia","xxx","xyz","yachts","yahoo","yamaxun","yandex","ye","yodobashi","yoga","yokohama","you","youtube","yt","yun","za","zappos","zara","zero","zip","zippo","zm","zone","zuerich","zw");
	
	private static MySQL mysql;
	private static Config config = null;
	private static MessageConfig messages = null;
	
	private DependencyManager dependencyManager = null;
	private BungeeCord bungeecord;
	private ExtensionManager extensions;
	
	private BukkitTask repeatingTask;
	
	private boolean debug = false;
	
	// messages for operators on join
	private static List<String> warnings = new ArrayList<>();
	
	private Core() {
		enable();
	}
	
	public static Core init() {
		return new Core();
	}
	
	private void enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		onEnable();
	}
	
	private void disable() {
		HandlerList.unregisterAll(this);
		onDisable();
	}
	
	private void onEnable() {
		if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
		
		config = Config.init("config").loadFromResource("config");
		debug = config.getBoolean("debug", false);
		
		messages = MessageConfig.init();
		setupMySQL();

		bungeecord = BungeeCord.init();
		dependencyManager = DependencyManager.init();

		// load features
		Reflections reflections = new Reflections("aw.rmjtromp.RunicCore.core.features");
		Set<Class<? extends RunicFeature>> essentialFeatures = reflections.getSubTypesOf(RunicFeature.class);
		for(Class<? extends RunicFeature> essentialFeature : essentialFeatures) {
			try { essentialFeature.getConstructor().newInstance(); }
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) { e.printStackTrace(); }
		}
		debug(RunicFeature.getFeatures().size()+" features registered.");
		
		
		// load extensions
		extensions = ExtensionManager.init();
		
		tabCompleterFix();
		repeatingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runRepeatingTasks(), 20, 20);
	}
	
	private void onDisable() {
		if(repeatingTask != null) repeatingTask.cancel();
	}
	
	private void setupMySQL() {
		if(config.getBoolean("MySQL.enabled", false) == true) {
			String host = config.contains("MySQL.host") ? config.getString("MySQL.host", "localhost") : null;
			String username = config.contains("MySQL.username") ? config.getString("MySQL.username", "admin") : null;
			String password = config.contains("MySQL.password") ? config.getString("MySQL.password", "password") : null;
			String database = config.contains("MySQL.database") ? config.getString("MySQL.database", "root") : null;
			int port = config.contains("mysql.port") ? config.getInt("mysql.port", 3306) : 3306;
			
			if(host != null && username != null && password != null && database != null) {
				mysql = new MySQL(host, username, password, database, port);
				if(mysql.getConnection() == null) mysql = null;
			}
			
			if(Core.getMySQL() != null) {
				// check if tables exists or create it
				
				// players
				try {
					PreparedStatement statement = Core.getMySQL().getConnection()
							.prepareStatement("SHOW TABLES LIKE 'players';");
					ResultSet results = statement.executeQuery();
					if(results.next()==false) {
						// table doesnt exist
						PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `players` ( `player` VARCHAR(36) NOT NULL , `username` VARCHAR(16) NOT NULL , `internetProtocols` JSON NULL DEFAULT NULL , `alternateAccounts` JSON NULL DEFAULT NULL , `lastSeen` INT(10) NOT NULL , `networkExperience` MEDIUMINT NOT NULL DEFAULT '0' , `ranks` JSON NOT NULL , `settings` JSON NULL DEFAULT NULL , `ignores` JSON NULL DEFAULT NULL , PRIMARY KEY (`player`)) ENGINE = MyISAM;");
						ps.executeQuery();
					}
				} catch (SQLException e) {
					error("There was an error creating 'players' table in database: "+e.getMessage());
				}
				
				// bans
				try {
					PreparedStatement statement = Core.getMySQL().getConnection()
							.prepareStatement("SHOW TABLES LIKE 'bans';");
					ResultSet results = statement.executeQuery();
					if(results.next()==false) {
						// table doesnt exist
						PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `bans` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `executor` VARCHAR(36) NOT NULL , `reason` TINYTEXT NOT NULL , `time` INT(10) NOT NULL , `expiration` INT(10) NULL DEFAULT NULL , `server` VARCHAR(16) NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;");
						ps.executeQuery();
					}
				} catch (SQLException e) {
					error("There was an error creating 'bans' table in database: "+e.getMessage());
				}
				
				// mutes
				try {
					PreparedStatement statement = Core.getMySQL().getConnection()
							.prepareStatement("SHOW TABLES LIKE 'mutes';");
					ResultSet results = statement.executeQuery();
					if(results.next()==false) {
						// table doesnt exist
						PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `mutes` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `executor` VARCHAR(36) NOT NULL , `reason` TINYTEXT NOT NULL , `time` INT(10) NOT NULL , `expiration` INT(10) NULL DEFAULT NULL , `server` VARCHAR(16) NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;");
						ps.executeQuery();
					}
				} catch (SQLException e) {
					error("There was an error creating 'bans' table in database: "+e.getMessage());
				}
				
				// playerdata
				// TODO add support for multiple servers
				try {
					PreparedStatement statement = Core.getMySQL().getConnection()
							.prepareStatement("SHOW TABLES LIKE '_playerdata';");
					ResultSet results = statement.executeQuery();
					if(results.next()==false) {
						// table doesnt exist
						PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `_playerdata` ( `player` VARCHAR(36) NOT NULL , `homes` JSON NULL DEFAULT NULL , `ranks` JSON NULL DEFAULT NULL , `balance` FLOAT NOT NULL DEFAULT '0' ) ENGINE = MyISAM;");
						ps.executeQuery();
					}
				} catch (SQLException e) {
					error("There was an error creating '_playerdata' table in database: "+e.getMessage());
				}
				
				// flags
				try {
					PreparedStatement statement = Core.getMySQL().getConnection()
							.prepareStatement("SHOW TABLES LIKE 'flags';");
					ResultSet results = statement.executeQuery();
					if(results.next()==false) {
						// table doesnt exist
						PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `flags` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `reason` TINYTEXT NOT NULL , `extra` TINYTEXT NULL DEFAULT NULL , `server` VARCHAR(16) NOT NULL , `time` INT(10) NOT NULL , `solved` BOOLEAN NOT NULL DEFAULT FALSE , PRIMARY KEY (`id`)) ENGINE = MyISAM;");
						ps.executeQuery();
					}
				} catch (SQLException e) {
					error("There was an error creating 'flags' table in database: "+e.getMessage());
				}
				
				// reports
				try {
					PreparedStatement statement = Core.getMySQL().getConnection()
							.prepareStatement("SHOW TABLES LIKE 'reports';");
					ResultSet results = statement.executeQuery();
					if(results.next()==false) {
						// table doesnt exist
						PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `reports` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `reporter` VARCHAR(36) NOT NULL , `reason` TINYTEXT NOT NULL , `server` VARCHAR(16) NOT NULL , `time` INT(10) NOT NULL , `caseLeader` VARCHAR(36) NULL DEFAULT NULL , `solved` BOOLEAN NOT NULL DEFAULT FALSE , PRIMARY KEY (`id`)) ENGINE = MyISAM;");
						ps.executeQuery();
					}
				} catch (SQLException e) {
					error("There was an error creating 'reports' table in database: "+e.getMessage());
				}
				
				// punishment history
				try {
					PreparedStatement statement = Core.getMySQL().getConnection()
							.prepareStatement("SHOW TABLES LIKE 'history';");
					ResultSet results = statement.executeQuery();
					if(results.next()==false) {
						// table doesnt exist
						PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `history` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `executor` VARCHAR(36) NOT NULL , `type` VARCHAR(16) NOT NULL , `reason` TINYTEXT NOT NULL , `time` INT(10) NOT NULL , `expiration` INT(10) NULL DEFAULT NULL , `server` VARCHAR(16) NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;");
						ps.executeQuery();
					}
				} catch (SQLException e) {
					error("There was an error creating 'history' table in database: "+e.getMessage());
				}
				
				// economy trace
				try {
					PreparedStatement statement = Core.getMySQL().getConnection()
							.prepareStatement("SHOW TABLES LIKE '_economy_trace';");
					ResultSet results = statement.executeQuery();
					if(results.next()==false) {
						// table doesnt exist
						PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `_economy_trace` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `amount` FLOAT NOT NULL , `reason` TINYTEXT NOT NULL , `time` INT(10) NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;");
						ps.executeQuery();
					}
				} catch (SQLException e) {
					error("There was an error creating '_economy_trace' table in database: "+e.getMessage());
				}
			}
		}
	}
	
	/*
	 * Getters
	 */
	
	public static Config getConfig() {
		return config;
	}
	
	public static MessageConfig getMessages() {
		return messages;
	}
	
	public static MySQL getMySQL() {
		return mysql;
	}

	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}
	
	public ExtensionManager getExtensionManager() {
		return extensions;
	}
	
	public BungeeCord getBungeeCord() {
		return bungeecord;
	}
	
	public void debug(String string) {
		if(debug) plugin.getLogger().info(string);
	}
	
	/*
	 * Events
	 */
	
    private Runnable runRepeatingTasks() {
        return () -> {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            	RunicPlayer.cast(p).getRepeatingTask().run();
            }
        };
    }
	
	@EventHandler
	public void onRunicCoreReload(RunicCoreReloadEvent e) {
		if(!e.isCancelled()) {
			// Code below is automatically done inside Config class
			//config.reload();
			//messages.reload();
			
			mysql = null;
			setupMySQL();
		}
	}
	
	public void tabCompleterFix() {
		if(Dependency.PROTOCOLLIB.isRegistered()) {
			List<String> blocked = Arrays.asList("?", "about", "help", "pl", "plugins", "reload", "rl", "timings", "ver", "version");
			DependencyManager.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Server.TAB_COMPLETE }) {
				@EventHandler(priority = EventPriority.HIGHEST)
				public void onPacketSending(PacketEvent e) {
					if(e.getPacketType() == PacketType.Play.Server.TAB_COMPLETE) {
						try {
							for(int x = 0; x < e.getPacket().getStringArrays().getValues().size(); x++) {
								List<String> newCompleter = new ArrayList<String>();
								
								for(int y = 0; y < e.getPacket().getStringArrays().getValues().get(x).length; y++) {
									String suggestion = e.getPacket().getStringArrays().getValues().get(x)[y];
									if(suggestion.startsWith("/")) {
										String command = suggestion.substring(1).split(" ", 2)[0];
										if(!command.contains(":") && !newCompleter.contains(command) && !blocked.contains(command)) newCompleter.add("/"+command);
									} else {
										newCompleter.add(e.getPacket().getStringArrays().getValues().get(x)[y]);
									}
								}
								
								String[] newComp = new String[newCompleter.size()];
								for(int z = 0; z < newCompleter.size(); z++) {
									newComp[z] = newCompleter.get(z);
								}
								e.getPacket().getStringArrays().write(x, newComp);
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage().contains(" ") ? e.getMessage().split(" ")[0].toLowerCase().substring(1) : e.getMessage().toLowerCase().substring(1);
		String unknownCommand = plugin.getServer().spigot().getConfig().getString("messages.unknown-command", "Unknown command. Type \"/help\" for help.");
		if(!e.getPlayer().isOp() || cmd.matches("(bukkit:)?(rl|reload)")) {
			if(cmd.contains(":")) {
				String command = cmd.split(":", 2)[0];
//				String label = cmd.split(":", 2)[1];
				
				if(command.equalsIgnoreCase("bukkit") || command.equalsIgnoreCase("spigot")) e.setCancelled(true);
				else {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou're not allowed to use colons inside commands."));
					return;
				}
			} else {
				String label = cmd;
				List<String> blocked = Arrays.asList("?", "about", "help", "pl", "plugin", "plugins", "reload", "rl", "timings", "ver", "version");
				if(label.equalsIgnoreCase("restart") || label.equalsIgnoreCase("stop")) {
					if(e.getPlayer().isOp()) {
						if(getBungeeCord().hasBungee() && !getBungeeCord().getServerName().equalsIgnoreCase("hub")) {
							for(Player p : Bukkit.getOnlinePlayers()) {
								RunicPlayer player = RunicPlayer.cast(p);
								player.send("Hub");
							}
						}
					} else e.setCancelled(true);
				} else if(blocked.contains(label)) e.setCancelled(true);
			}
		}
		
		if(e.isCancelled()) e.getPlayer().sendMessage(unknownCommand);
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		if(e.getPlugin() instanceof RunicCore) {
			disable();
		}
	}
	
	public static void warn(String message) {
		if(message != null && !message.isEmpty()) warnings.add(message);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		RunicPlayer player = new RunicPlayer(e.getPlayer());
		if(player.isSleepingIgnored()) player.setSleepingIgnored(false);
		if(!player.spigot().getCollidesWithEntities()) player.spigot().setCollidesWithEntities(true);
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					player.sendMessage("&c&lWARNING: &cThis server is currently under development and is only open for testing purposes. If you do find any bugs/glitches/exploits, please report this to any staff member or on our Discord.");
					if(player.isOp()) {
						warnings.forEach((warning) -> {
							player.sendMessage("&8(&c&l!&8) &c"+warning);
						});
					}
				}
		}, 30);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		
		RunicPlayer player = RunicPlayer.cast(e.getPlayer());
		BaseComponent[] rank = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', player.getPrefix()+player.getName()+player.getSuffix()));

		BaseComponent[] hover = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&e"+player.getName()+"\n&7Rank: &e"+(player.getRank() != null ? player.getRank().getName() : "null")+"\n&7Level: &e3"));
		
		TextComponent t = new TextComponent(rank);
		t.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, hover));
		
		BaseComponent[] message = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&8� "+ChatColor.GRAY+e.getMessage()));
		TextComponent t1 = new TextComponent(message);
		
		TextComponent t2 = new TextComponent(t, t1);
		
		String c = ComponentSerializer.toString(t2);
		player.getCraftPlayer().getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(c)));
	}
	
	@EventHandler
	public static void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
		// check if player is banned or blacklisted
		// move this to a 'bannable' class or something related to punishment
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(final PlayerLoginEvent e) {
		if(e.getResult().equals(Result.ALLOWED)) {
			RunicPlayer player = new RunicPlayer(e.getPlayer());
			if(player.isSleepingIgnored()) player.setSleepingIgnored(false);
			if(!player.spigot().getCollidesWithEntities()) player.spigot().setCollidesWithEntities(true);
			
			player.isCustomItem(null);
		}
	}
	
	public static int currentTimeSeconds() {
		long a = System.currentTimeMillis()/1000;
		int b = (int) a;
		return b;
	}
	
	public static long currentTimeMillies() {
		return System.currentTimeMillis();
	}
	
	public static RunicOfflinePlayer getOfflinePlayer(UUID uuid) {
		return RunicOfflinePlayer.c(Bukkit.getOfflinePlayer(uuid));
	}
	
}
