package bc.cestaplus.test;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.view.ViewGroup.LayoutParams;

import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.R;


public class ArticleActivity_OtherWay
    extends ActionBarActivity {

    protected FrameLayout webViewPlaceholder;
    protected WebView mWebView;

    private ArticleObj article;
    //private Bundle webViewBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_other_way);

        article = getIntent().getParcelableExtra("clanok");

        //Toast.makeText(this, rubrika, Toast.LENGTH_SHORT).show(); // testovaci vypis

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); ak by nesla navigacia UP, resp. sa nezobrazila šípka

        // Initialize the UI
        initUI();

    } //end onCreate

    private void initUI() {
        getSupportActionBar().setTitle(article.getSection()); //nastavenie label-u konkretnej aktivity

        // Retrieve UI elements
        webViewPlaceholder = ((FrameLayout)findViewById(R.id.webViewPlaceholder));

        // Initialize the WebView if necessary
        if (mWebView == null){
            // Create the webview
            mWebView = new WebView(this);
            mWebView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            mWebView.getSettings().setSupportZoom(true);
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mWebView.setScrollbarFadingEnabled(true);
            mWebView.getSettings().setLoadsImagesAutomatically(true);

            // Load the URLs inside the WebView, not in the external web browser
            mWebView.setWebViewClient(new WebViewClient());

            // Load a page
            String dataHtlm = "<html>\n" +
                    "    <head>\n" +
                    "        <title>Test html CLEAR</title>\n" +
                    "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/articleStyle.css\"/>\n" +
                    "    </head>\n" +
                    "    \n" +
                    "    <body>\n" +
                    "        <div class=\"body1_left_pisuinde\" style=\" height: 100%;\">                    \n" +
                    "            <h1>\n" +
                    "                Školská psychologička a sociálna pedagogička Mirka Szitová: \n" +
                    "                Referendum posilní pozíciu a podmienky rodín s deťmi \n" +
                    "            </h1>\n" +
                    "\n" +
                    "            <div style=\"margin-bottom: 15px; color: #343434; font-weight: bold;\">\n" +
                    "                Máloktorý z politikov tak aktívne vystupuje v príbehu referenda \n" +
                    "                a jemu súvisiacich témach, ako ona. A to nielen cez svoju materskú stranu, \n" +
                    "                ale aj prostredníctvom blogu či osobných stretnutí. Okrem postoja ponúka aj \n" +
                    "                kontrétne rady a pomoc. K hodnoteniu situácie sa stavia výsostne odborne ako \n" +
                    "                dlhoročná školská psychologička a sociálna pedagogička, zároveň tiež ako \n" +
                    "                veriaci človek. Miroslava Szitová, podpredsedníčka KDH.\n" +
                    "            </div>\n" +
                    "\n" +
                    "            <div>\n" +
                    "                <img src=\"http://www.cestaplus.sk/images/tema_clanok/mirka.jpg\">\n" +
                    "            </div>\n" +
                    "\n" +
                    "            <div id=\"text_clanok\">\n" +
                    "                <p><strong>Mnoho ľud&iacute; m&aacute; pocit, že referendum už vylezie aj pri otvoren&iacute; z&nbsp;chladničky. Ako prež&iacute;vate dni pred referendom Vy?</strong></p>\n" +
                    "                <p>V&nbsp;na&scaron;om živote sa dej&uacute; veci podstatn&eacute; i&nbsp;menej podstatn&eacute;. Keď niekto z&nbsp;bl&iacute;zkych trp&iacute;, v&scaron;etci m&aacute;me tendenciu mobilizovať sily, aby sme mu vedeli v&nbsp;jeho ťažkostiach pom&ocirc;cť. Sme pred podstatn&yacute;m a d&ocirc;ležit&yacute;m rozhodnut&iacute;m o&nbsp;bud&uacute;cnosti Slovenska. Uvedomujem si v&yacute;znam referenda pre ďal&scaron;&iacute; v&yacute;voj na&scaron;ej krajiny a&nbsp;svoju zodpovednosť za jeho v&yacute;sledok.</p>\n" +
                    "                <p><strong>Referendum o&nbsp;rodine iniciovala AZR. V&nbsp;pl&eacute;ne a&nbsp;diskusi&aacute;ch je považovan&aacute; za skryt&uacute; odnož KDH alebo Cirkvi...</strong></p>\n" +
                    "                <p>AZR je občianskou iniciat&iacute;vou, ktor&aacute; zorganizovala pet&iacute;ciu s&nbsp;vy&scaron;e 400-tis&iacute;c podpismi ľud&iacute;. Neviem, či je považovan&aacute; za skryt&uacute; odnož KDH, ale najnov&scaron;ie sa som sa doč&iacute;tala, že AZR bude politickou stranou, teda konkurenciou KDH, ktorej by sme sa vraj mali bať :)</p>\n" +
                    "                <p><strong>Tri ot&aacute;zky &scaron;tiepia spoločnosť a&nbsp;to nielen odvek&yacute;ch n&aacute;zorov&yacute;ch oponentov, ale aj veriacich ľud&iacute;. Mnoh&iacute; aj na blogoch či na soci&aacute;lnych f&oacute;rach vyhlasuj&uacute;, že na referendum nep&ocirc;jdu a&nbsp;odvol&aacute;vaj&uacute; sa na sv. otca Franti&scaron;ka...</strong></p>\n" +
                    "                <p>&Aacute;no, ľudia sa vyjadruj&uacute; r&ocirc;zne... Pre mňa je v&scaron;ak zar&aacute;žaj&uacute;ce, že mnoh&iacute;, ktor&iacute; presadzuj&uacute; priamu demokraciu, teda chc&uacute;, aby rozhodovali občania a nie politici, pr&aacute;ve pred v&scaron;eľudov&yacute;m hlasovan&iacute;m žiadaj&uacute;, aby ľudia hlasovať ne&scaron;li. Vyjadrili sa tak napr&iacute;klad predstavitelia strany SAS. Tento sign&aacute;l je veľmi zl&yacute;, lebo obmedzuje slobodu rozhodovania a&nbsp;zodpovednosť každ&eacute;ho z n&aacute;s. Sv&auml;t&yacute; Otec Franti&scaron;ek n&aacute;s vyz&yacute;va, aby sme usilovali o&nbsp;ochranu a&nbsp;posilnenie rodiny, ktor&aacute; je najv&auml;č&scaron;&iacute;m pokladom krajiny.</p>\n" +
                    "                <p><strong>Ak&yacute; je v&aacute;&scaron; postoj k&nbsp;referendu ako občana, politika, kresťana? </strong></p>\n" +
                    "                <p>Referendum, teda v&scaron;eľudov&eacute; hlasovanie je n&aacute;strojom priamej demokracie. Nerozhoduje za n&aacute;s zvolen&yacute; z&aacute;stupca, poslanec, ale každ&yacute; z n&aacaacute;s m&ocirc;že vyjadriť svoj postoj k&nbsp;d&ocirc;ležitej z&aacute;ležitosti priamo. Rovnako je to aj pri referende o&nbsp;rodine. Prezident republiky ho vyhl&aacute;sil na žiadost vy&scaron;e 400-tis&iacute;c ľud&iacute; a&nbsp;na z&aacute;klade rozhodnutia &Uacute;stavn&eacute;ho s&uacute;du. Ako občan, politik, kresťan chcem rozhodovať o&nbsp;svojej bud&uacute;cnosti a&nbsp;bud&uacute;cnosti Slovenska. Preto sa z&uacute;častňujem v&scaron;etk&yacute;ch volieb a urob&iacute;m tak aj v&nbsp;pr&iacute;pade referenda.</p>\n" +
                    "                <p><strong>Tak &ndash; zmen&iacute; sa de facto niečo alebo nie?</strong></p>\n" +
                    "                <p>Určite &aacute;no. Č&iacute;m viac ľud&iacute; pr&iacute;de a&nbsp;vyjadr&iacute; svoj n&aacute;zor, t&yacute;m lep&scaron;ie. Pre predstaviteľov &scaron;t&aacute;tu, teda prezidenta, vl&aacute;du a&nbsp;parlament, bude v&yacute;sledok hlasovania aj &uacute;časť d&ocirc;ležit&yacute;m sign&aacute;lom pre prij&iacute;manie z&aacute;konov. Viacer&iacute; sme zaregistrovali pr&iacute;pravu Strat&eacute;gie ľudsk&yacute;ch pr&aacute;v. Tento dokument bol už 2x stiahnut&yacute; z&nbsp;rokovania vl&aacute;dy pr&aacute;ve preto, lebo s&nbsp;jeho obsahom nes&uacute;hlasili mnoh&eacute; neziskov&eacute; organiz&aacute;cie, cirkv&iacute;, ľudia podp&iacute;san&iacute; pod pet&iacute;ciou i protestuj&uacute;ci na ulici. Som veľmi rada, že v&nbsp;na&scaron;ej &Uacute;stave už m&aacute;me ukotven&uacute; ochranu maželstva, rodiny a&nbsp;det&iacute;. Treba v&scaron;ak urobiť viac zmien v&nbsp;legislat&iacute;ve, aby bola rodina posilnen&aacute; a aby sa zlep&scaron;ili podmienky rod&iacute;n s&nbsp;deťmi.</p>\n" +
                    "                <p><strong>Poďme sa na to pozrieť po poriadku: prečo s&uacute; tak&eacute; tlaky na to, aby homosexu&aacute;lne ži&uacute;ci ľudia mali pr&aacute;ve manželstvo? </strong></p>\n" +
                    "                <p>Predov&scaron;etk&yacute;m je treba odpovedať na ot&aacute;zku, čo je manželstvo. Manželstvo je pr&aacute;vna a&nbsp;spoločensk&aacute; in&scaron;tit&uacute;cia, ktor&aacute; tvor&iacute; legislat&iacute;vny z&aacute;klad pre vznik rodiny. Hlavn&yacute; cieľom manželstva je založenie rodiny, v&yacute;chova det&iacute;, teda biologick&aacute; a&nbsp;v&yacute;chovn&aacute; funkcia a&nbsp;vz&aacute;jomn&aacute; pomoc a podpora. Tu sa dost&aacute;vame do rozporu s&nbsp;požiadavkou, aby ľudia, ktor&iacute; s&uacute; homosexu&aacute;lne c&iacute;tiaci, uzatv&aacute;rali manželstva. Manželstvo je in&scaron;tit&uacute;cia, ktorej hlavn&eacute; funkcie a&nbsp;&uacute;lohy žiadne osoby rovnak&eacute;ho pohlavia nem&ocirc;žu a nedok&aacute;žu naplniť. S&uacute; ľudia, ktor&iacute; by radi presadili v&nbsp;na&scaron;ej spoločnosti zmenu hodn&ocirc;t a&nbsp;t&iacute; sa usiluj&uacute; o&nbsp;redefin&iacute;ciu manželstva.</p>\n" +
                    "\n" +
                    "                <!-- <p><img src=\"../../../images/tema/mirka/unnamed 1.jpg\" alt=\"\" width=\"500\" height=\"751\" /></p> -->\n" +
                    "                <p><img src=\"http://www.cestaplus.sk/images/tema/mirka/unnamed 1.jpg\" alt=\"\"/></p>\n" +
                    "\n" +
                    "                <p><strong>Pracovali ste ako &scaron;kolsk&aacute; psychologička &ndash; viete si z&nbsp;odborn&eacute;ho hľadiska predstaviť zdrav&uacute; v&yacute;chovu dieťaťa v&nbsp;rodine s&nbsp;rovnakou pohlavnou identitou rodičov?</strong></p>\n" +
                    "                <p>Dvaja muži a&nbsp;dve ženy nem&ocirc;žu prirodzen&yacute;m sp&ocirc;sobom splodiť dieťa, nem&ocirc;žu prirodzen&yacute;m sp&ocirc;sobom vytvoriť rodinu. Mamu nenahrad&iacute; žiaden muž a&nbsp;otca nenarad&iacute; žiadna žena. M&ocirc;žu poskytn&uacute;ť dieťaťu určit&eacute; z&aacute;zemie, ale nie v&scaron;estrann&yacute; harmonick&yacute; rozvoj. V&nbsp;oblasti poradenstva a&nbsp;prevencie som pracovala 11 rokov. Dieťa potrebuje vlastn&uacute; miluj&uacute;cu mamu a&nbsp;vlastn&eacute;ho starostliv&eacute;ho otca. Takto tvoren&aacute; rodina vytv&aacute;ra ide&aacute;lne prostredie pre harmonicky rozvoj dieťaťa. D&ocirc;kazov z&nbsp;biologick&eacute;ho, psychologick&eacute;ho, soci&aacute;lneho hľadiska je množstvo.</p>\n" +
                    "                <p>Osobnosť dieťaťa sa vytv&aacute;ra t&yacute;m, že sa konfrontuje s&nbsp;identitou a&nbsp;rozdielnosťou vo v&scaron;etk&yacute;ch v&yacute;vinov&yacute;ch obdobiach. Pozn&aacute;va seba sameho napodobňovan&iacute;m rodiča toho ist&eacute;ho pohlavia, na druhej strane pozorovan&iacute;m rodiča opačn&eacute;ho pohlavia. Mama sa prihov&aacute;ra dieťaťu, m&aacute;zna sa s n&iacute;m, hra sa s&nbsp;n&iacute;m in&yacute;m sp&ocirc;sobom ako otec a&nbsp;pod. Vo svojej praxi som sa stretla aj s&nbsp;pr&iacute;padmi, kde zlyh&aacute;val vo v&yacute;chove jeden, alebo obaja rodičia. V&nbsp;takejto situ&aacute;cii by mala nast&uacute;piť pomoc zo strany &scaron;t&aacute;tu a&nbsp;mimovl&aacute;deho sektora, aby sa primeran&yacute;m sp&ocirc;sobom a p&ocirc;soben&iacute;m odborn&iacute;kov mohol stav zlep&scaron;iť. Tu vid&iacute;m obrovsk&eacute; rezervy &scaron;t&aacute;tu. &nbsp; &nbsp;</p>\n" +
                    "                <p><strong>Hor&uacute;cim zemiakom je aj t&eacute;ma sexu&aacute;lnej v&yacute;chovy na &scaron;kol&aacute;ch: vy s&nbsp;t&yacute;m sk&uacute;senosť podľa V&aacute;&scaron;ho svedectva m&aacute;te pri z&aacute;sahu v&nbsp;Spi&scaron;skej Belej, Ko&scaron;iciach. M&aacute; &scaron;t&aacute;t pr&aacute;vo zasiahnuť do medzin&aacute;rodne určen&yacute;ch pr&aacute;v rodiča na v&yacute;chovu?</strong></p>\n" +
                    "                <p>&Uacute;stava Slovenskej republiky, čl&aacute;lnok 41, ods. 4 stanovuje: Starostlivosť o&nbsp;deti a&nbsp;ich v&yacute;chova je pr&aacute;vom rodičov, deti maj&uacute; pr&aacute;vo na rodičovsk&uacute; v&yacute;chovu a&nbsp;starostlivosť. Pr&aacute;va rodičov možno obmedziť len rozhodnut&iacute;m s&uacute;du na z&aacute;klade z&aacute;kona. Ďalej Z&aacute;kon o&nbsp;rodine v&nbsp;čl&aacute;nku 4 ustanovuje: &bdquo;Rodičia maj&uacute; pr&aacute;vo vychov&aacute;vať deti v&nbsp;zhode s&nbsp;vlastn&yacute;m n&aacute;božensk&yacute;m a&nbsp;filozofick&yacute;m presvedčen&iacutiacute;m....&ldquo; Uviedla som časť legislat&iacute;vy, ktor&aacute; n&aacute;s zorientuje v&nbsp;pr&aacute;vach a&nbsp;povinnostiach rodičov a&nbsp;v&yacute;chove det&iacute;.</p>\n" +
                    "                <p>Sexu&aacute;lna v&yacute;chova v&nbsp;&scaron;kol&aacute;ch je s&uacute;časťou <em>V&yacute;chovy k&nbsp;manželstvu a&nbsp;rodičovstvu</em>, ktor&uacute; si &scaron;kola m&ocirc;že zahrn&uacute;ť ako samostatn&yacute; predmet do svojho &scaron;kolsk&eacute;ho vzdel&aacute;vacieho programu v&nbsp;r&aacute;mci voliteľn&yacute;ch hod&iacute;n. Osnovy<em> V&yacute;chovy k manželstvu a&nbsp;rodičovstvu</em> schv&aacute;lil &Scaron;t&aacute;tny pedagogick&yacute; &uacute;stav. Vzťahov&yacute; r&aacute;mec sexu&aacute;lnej v&yacute;chovy je zahrnut&yacute; aj v&nbsp;predmetoch biol&oacute;gia, etick&aacute; v&yacute;chova, n&aacute;božensk&aacute; v&yacute;chova, občianska v&yacute;chova a&nbsp;pod. In&yacute;m probl&eacute;mom je v&scaron;ak v&yacute;chova a&nbsp;vzdel&aacute;vanie v&nbsp;oblasti sexu&aacute;lnej v&yacute;chovy mimo osnov schv&aacute;len&yacute;ch &Scaron;t&aacute;tnym pedagogick&yacute;m &uacute;stavom a&nbsp;vstup organiz&aacute;ci&iacute;, ktor&eacute; chc&uacute; realizovať t&uacute;to v&yacute;chovu na p&ocirc;de &scaron;koly.</p>\n" +
                    "                <p>Moja negat&iacute;vna sk&uacute;senosť je pr&aacute;ve s&nbsp;t&yacute;mito organiz&aacute;ciami a&nbsp;ich nevhodn&yacute;m p&ocirc;soben&iacute;m. Potrebujeme legislat&iacute;vne vymedziť, ktor&eacute; in&scaron;tit&uacute;cie maj&uacute; kompetenciu realizovať v&nbsp;&scaron;kol&aacute;ch prevenciu tohto typu a&nbsp;rovnako d&ocirc;sledne dbať na akredit&aacute;ciu prevent&iacute;vnych programov realizovan&yacute;ch v&nbsp;&scaron;kol&aacute;ch.</p>\n" +
                    "                <p><strong>Ak&eacute; odpor&uacute;čania by ste dali rodičom &scaron;kolopovinn&yacute;ch det&iacute; k&nbsp;tomu, aby sa vyhli tak&yacute;mto jednostranne vzdel&aacute;vac&iacute;m projektom?</strong></p>\n" +
                    "                <p>Odpor&uacute;čanie je veľmi jednoduch&eacute;. Rodičia na začiatku &scaron;kolsk&eacute;ho roka podpisuj&uacute; <em>Univerz&aacute;lny informovan&yacute; s&uacute;hlas</em>. T&yacute;ka sa r&ocirc;znych aktiv&iacute;t, do ktor&yacute;ch bude zaraden&eacute; ich dieťa počas &scaron;kolsk&eacute;ho vyučovania. Odpor&uacute;čam rodičom, aby žiadali do textu <em>Univerz&aacute;lneho informovan&eacute;ho s&uacute;hlasu</em> doplniť, že s&uacute;hlas sa nevzťahuje na predn&aacute;&scaron;ky, semin&aacute;re, programy k &bdquo;prevencii rizikov&eacute;ho spr&aacute;vania....&ldquo;.</p>\n" +
                    "                <p>...</p>\n" +
                    "\n" +
                    "                <p><img src=\"http://www.cestaplus.sk/images/tema/mirka/suhlas.jpg\" alt=\"\"/></p>\n" +
                    "\n" +
                    "                <p>&nbsp;</p>\n" +
                    "                <p>S&uacute;hlas si m&ocirc;žete&nbsp;<a title=\"informovany_suhlas_cestaplus\" href=\"../../../images/tema/mirka/informovany_suhlas_cestaplus.doc\" target=\"_blank\">stiahn&uacute;ť tu</a>&nbsp;(word).</p>\n" +
                    "                <p>...</p>\n" +
                    "                <p>Pri takejto aktivite &scaron;kola vyzve z&aacute;konn&eacute;ho z&aacute;stupcu o&nbsp;samostatn&yacute; p&iacute;somn&yacute; s&uacute;hlas. V&nbsp;pr&iacute;pade nes&uacute;hlasu rodiča bude dieťa preraden&eacute; počas trvania aktivity do inej triedy a&nbsp;uvedenej aktivity sa nez&uacute;častni.</p>\n" +
                    "                <p><strong>Ch&yacute;ba&nbsp; teda sexu&aacute;lna v&yacute;chova v&nbsp;&scaron;kole alebo nie? Ako to vn&iacute;maj&uacute; deti? Je to pre nich potrebn&eacute; v&nbsp;tej podobe,&nbsp; ako bež&iacute; v&nbsp;zahranič&iacute;?</strong></p>\n" +
                    "                <p>Počas m&ocirc;jho odborn&eacute;ho p&ocirc;sobenia som často s&nbsp;mlad&yacute;mi stredo&scaron;kol&aacute;kmi diskutovala na t&eacute;mu vzťahov. Mnoh&iacute; mlad&iacute; ľudia, ktor&iacute; nevyrastali v&nbsp;rodine s&nbsp;mamou a&nbsp;otcom, žili v&nbsp;ne&uacute;plnej rodine, prežili rozvod rodičov, žili v&nbsp;rodine, kde sa vystriedalo viac partnerov a &bdquo;s&uacute;rodencov&ldquo; a&nbsp;pod., boli zm&auml;ten&iacute; a&nbsp;mali obavu, že si nedok&aacute;žu vybrať vhodn&eacute;ho partnera, nedok&aacute;žu si založiť rodinu a&nbsp;bud&uacute; kop&iacute;rovať chyby ich rodičov. Absenciu pozit&iacute;vneho vzoru a&nbsp;modelu plnohodnotne nenahrad&iacute; žiadna v&yacute;chova v&nbsp;&scaron;kole. Preto je nevyhnutn&aacute; prevencia a&nbsp;pr&aacute;ca s&nbsp;rodinou, ktor&aacute; sa nach&aacute;dza v&nbsp;kr&iacute;ze.</p>\n" +
                    "                <p>Hovoriť iba o&nbsp;sexu&aacute;lnej v&yacute;yacute;chove nestač&iacute;. Potrebn&aacute; je v&yacute;chova k&nbsp;vzťahom, l&aacute;ske, &uacute;cte a&nbsp;zodpovednosti, vždy s&nbsp;ohľadom na vek žiakov a&nbsp;s&nbsp;dostatočn&yacute;m poznan&iacute;m žiakov, s&nbsp;ktor&yacute;mi odborn&iacute;k alebo učiteľ pracuje.</p>\n" +
                    "                <p><strong>Hoci sa krič&iacute; o&nbsp;netolerancii k&nbsp;istej men&scaron;ine,&nbsp; nie je cel&yacute; tento projekt gender&nbsp; o&nbsp;prekročen&iacute; hran&iacute;c z&aacute;kladn&yacute;ch ľudsk&yacute;ch pr&aacute;v ostatn&yacute;ch?</strong></p>\n" +
                    "                <p>Ľudsk&eacute; pr&aacute;va s&uacute; univerz&aacute;lne, patria každ&eacute;mu človeku od narodenia bez ohľadu na rasu, pohlavie, etnick&uacute;, n&aacute;rodnostn&uacute; alebo &scaron;t&aacute;tnu pr&iacute;slu&scaron;nosť. Žiadna skupina nem&aacute; byť zv&yacute;hodnen&aacute;, ani žiadať nadpr&aacute;va. Gender ideol&oacute;gia popiera prirodzen&yacute; z&aacute;kon, popiera prirodzenosť človeka, preto je de&scaron;trukt&iacute;vna pre človeka i&nbsp;spoločnosť.</p>\n" +
                    "                <p><strong>Keď už sme pri tom: prečo sa v&nbsp;tejto republike nedar&iacute; zapracovať do &nbsp;<em>Strat&eacute;gie ľudsk&yacute;ch pr&aacute;v</em> tie pr&aacute;va, ktor&eacute; s&uacute; podstatn&eacute; a&nbsp;prines&uacute; osoh v&scaron;etk&yacute;m?</strong></p>\n" +
                    "                <p>K&nbsp;prijatiu <em>Strat&eacute;gie ľudsk&yacute;ch pr&aacute;v</em> n&aacute;s neviaže žiaden medzin&aacute;rodn&yacute; dokument. Prv&aacute; verzia dokumentu bola e&scaron;te v&nbsp;roku 2013 stiahnut&aacute; na prepracovanie. Na rokovanie vl&aacute;dy bola predložen&aacute; prepracovan&aacute; verzia už aj v&nbsp;janu&aacute;ri 2015, napokon v&scaron;ak bola z&nbsp;rokovania znovu stiahnut&aacute;. Ak m&aacute; byť dokument kvalitne pripraven&yacute;, pri jeho tvorbe by mali zodpovedn&iacute; autori spolupracovať so v&scaron;etk&yacute;mi zainteresovan&yacute;mi in&scaron;tit&uacute;ciami, organiz&aacute;ciami, odborn&iacute;kmi a&nbsp;zapracovať pripomienky v&nbsp;s&uacute;lade s&nbsp;platnou legislat&iacute;vou.</p>\n" +
                    "\n" +
                    "                <p><img src=\"http://www.cestaplus.sk/images/tema/mirka/unnamed.jpg\" alt=\"\"/></p>\n" +
                    "\n" +
                    "                <p><strong>Komplikovan&yacute; je aj postoj m&eacute;di&iacute; k&nbsp;takej podstatnej t&eacute;me, ako je rodina a&nbsp;jej prirodzen&yacute; okruh, ako je manželstvo muža a&nbsp;ženy, v&yacute;chova det&iacute;...&nbsp;&nbsp; </strong></p>\n" +
                    "                <p>S&uacute;kromn&eacute; medi&aacute; su v&nbsp;ruk&aacute;ch majiteľov a&nbsp;t&iacute; m&ocirc;žu ovplyvňovať obsah vysielania. In&eacute; je to v&nbsp;pr&iacute;pade verejnopr&aacute;vnych m&eacute;dii. Sklaman&aacute; som najm&auml; z&nbsp;postupu RTVS v&nbsp;pr&iacute;pade neodvysielania spotu Aliancie za rodinu.&nbsp;</p>\n" +
                    "                <p><strong>Obstoj&iacute; ako d&ocirc;vod ne&uacute;časti na referende pozn&aacute;mka, že AZR presolila sp&ocirc;sob komunik&aacute;cie a&nbsp;že ot&aacute;zky s&uacute; nespr&aacute;vne formulovan&eacute;?</strong></p>\n" +
                    "                <p>Vy&scaron;e 400&nbsp;000 ľud&iacute; podp&iacute;salo pet&iacute;ciu, prezident republiky dal &Uacute;stavn&eacute;mu s&uacute;du pos&uacute;diť jednotliv&eacute; ot&aacute;zky. &Uacute;stavn&yacute; s&uacute;d vydal rozhodnutie a&nbsp;prezident republiky vyhl&aacute;sil referendum. V&scaron;etko ostatn&eacute; je pre mňa v&nbsp;tomto momente nepodstatn&eacute;. Chcem a&nbsp;m&ocirc;žem svojou &uacute;časťou ovplyvniť bud&uacute;cnosť Slovenska.</p>\n" +
                    "                <p><strong>Priaznivcom referenda sa dost&aacute;vaj&uacute; fakt &bdquo;pekn&eacute;&ldquo; pr&iacute;vlastky, jedn&yacute;m z&nbsp;top je &bdquo;homof&oacute;b&ldquo;... &Scaron;&eacute;fovi AZR sa vyhr&aacute;žaj&uacute; smrťou det&iacute;... Ak&eacute; s&uacute; to sign&aacute;ly?</strong></p>\n" +
                    "                <p>Demokratick&aacute; spoločnosť d&aacute;va priestor na vyjadrenie každ&eacute;mu. Často pod pseudonymom alebo anonymne ľudia vyjadruj&uacute; r&ocirc;zne n&aacute;zory, provokuj&uacute;, ur&aacute;žaj&uacute;, &uacute;točia. Je to vidieť najm&auml; na soci&aacute;lnych sieťach, pod r&ocirc;znymi čl&aacute;nkami apod. Zverejnen&iacute;m mena autora a&nbsp;prevzat&iacute;m zodpovednosti, by bol iste minimalizovan&yacute; nevhodn&yacute; obsah. &nbsp;Aj to svedč&iacute; o&nbsp;kult&uacute;re jednotlivca a&nbsp;jeho vn&uacute;tornom prež&iacute;van&iacute;.</p>\n" +
                    "                <p><strong>Ako je možn&eacute;, že pr&aacute;ve t&eacute;ma rodiny, manželstva, v&yacute;chovy sp&ocirc;sobila na Slovensku tak&yacute;to rozruch?</strong></p>\n" +
                    "                <p>Nikto z&nbsp;n&aacute;s by pred p&aacute;r rokmi neveril, že budeme musieť zv&aacute;dzať z&aacute;pas o&nbsp;manželstvo a&nbsp;rodinu proti rodovej ideol&oacute;gii. Že budeme za rodinu pochodovať na n&aacute;mestiach, demon&scaron;trovať proti prij&iacute;maniu protirodinn&yacute;ch opatren&iacute; a strat&eacute;gi&iacute;, chr&aacute;niť deti pred cielenou predčasnou sexualiz&aacute;ciou a rodov&yacute;m scitlivovan&iacute;m, že na&scaron;i odborn&iacute;ci z&nbsp;oblasti medic&iacute;ny, psychol&oacute;gie bud&uacute; vyz&yacute;vať vl&aacute;du, aby zabr&aacute;nila vstupu rodovej ideol&oacute;gie, ktor&aacute; predstavuje v&aacute;žnu formu du&scaron;evn&eacute;ho n&aacute;silia a&nbsp;experimentuje s&nbsp;du&scaron;evn&yacute;m zdrav&iacute;m dieťaťa, do edukačn&eacute;ho procesu. Dnes m&aacute;me prijat&uacute; Strat&eacute;giu rodovej rovnosti, zo &scaron;t&aacute;tneho rozpočtu sa financuje gender ide&oacute;l&oacute;gia a podprahovo sa zav&aacute;dza do &scaron;k&ocirc;l, m&aacute;me v&yacute;bor pre pr&aacute;va lesieb, gejov, bisexu&aacute;lnych, transrodov&yacute;ch a&nbsp;intersexu&aacute;lnlych os&ocirc;b.</p>\n" +
                    "                <p>Vďaka Bohu a&nbsp;&uacute;silu niektor&yacute;ch politikov sa podarilo presadiť do &Uacute;stavy SR ochranu manželstva, rodiny a&nbsp;det&iacute;. T&aacuteaacute;to zmena je fundamentom, ale nestač&iacute;. Referendum d&aacute;va možnosť vyjadriť sa každ&eacute;mu z&nbsp;n&aacute;s.</p>\n" +
                    "                <p><strong>Zd&aacute; sa, že referendum je bez veľkej politickej podpory, bez medi&aacute;lneho z&aacute;zemia... M&aacute; &scaron;ancu?</strong></p>\n" +
                    "                <p>Sv&auml;t&yacute; J&aacute;n Pavol II. v&nbsp;roku 1995 na n&aacute;v&scaron;teve Slovenska povedal: &bdquo;Slovensko m&aacute; osobitn&uacute; &uacute;lohu pri budovan&iacute; Eur&oacute;py tretieho tis&iacute;cročia.&ldquo; Mne jeho slov&aacute; rezonuj&uacute; v&nbsp;srdci aj pri tomto z&aacute;pase. Nebojme sa!</p>\n" +
                    "                <p>&nbsp;</p>\n" +
                    "                <p><em>Fotografie: Mirka Szitov&aacute;</em></p>\n" +
                    "            </div>        \n" +
                    "        </div>\n" +
                    "    </body>\n" +
                    "</html>";

            //mWebView.loadData(dataHtlm, "text/html", "utf-8"); // nefunguje utf-8 kodovanie
            //mWebView.loadDataWithBaseURL("http://www.cestaplus.sk", dataHtlm, "text/html", "utf-8", null);
            mWebView.loadDataWithBaseURL(null, dataHtlm, "text/html", "utf-8", null);
        }

        // Attach the WebView to its placeholder
        webViewPlaceholder.addView(mWebView);
    } //end initUI


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clanok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        if (mWebView != null){
            // Remove the WebView from the old placeholder
            webViewPlaceholder.removeView(mWebView);
        }

        super.onConfigurationChanged(newConfig);

        // Load the layout resource for the new configuration
        setContentView(R.layout.activity_article_other_way);

        // Reinitialize the UI
        initUI();
    } //end onConfigurationChanged

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); // always call super class first !!

        // Save the state of the WebView
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState); // always call super class first !!

        // Restore the state of the WebView
        mWebView.restoreState(savedInstanceState);
    }

    /*
    @Override
    public void onPause() {
        super.onPause();

        webViewBundle = new Bundle();
        mWebView.saveState(webViewBundle);
    }
    */
}// end of ArticleActivity_OtherWay
