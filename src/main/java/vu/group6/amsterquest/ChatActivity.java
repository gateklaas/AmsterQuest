package vu.group6.amsterquest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    public static final int REQUEST_QUEST = 4000;

    private ListView chatList;
    private ImageButton cameraButton;
    private EditText chatField;
    private Button sendButton;
    private ChatMessageAdapter chatAdapter;
    private String workspaceId;
    private ConversationService conversationService;
    private Map<String, Object> conversationContext;
    private RetrieveAndRankExt retrieveAndRankservice;

    private static boolean similar(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        return s1.contains(s2) || s2.contains(s1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // setup Conversation service
        String username = getResources().getString(R.string.quest_conversation_username);
        String password = getResources().getString(R.string.quest_conversation_password);
        workspaceId = getResources().getString(R.string.quest_conversation_workspace_id);
        conversationService = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
        conversationService.setUsernameAndPassword(username, password);
        if (savedInstanceState == null)
            getConversationResponseMockup("");

        // setup Retrieve and Rank service
        username = getResources().getString(R.string.quest_retriever_username);
        password = getResources().getString(R.string.quest_retriever_password);
        retrieveAndRankservice = new RetrieveAndRankExt();
        retrieveAndRankservice.setUsernameAndPassword(username, password);

        // get views
        chatList = (ListView) findViewById(R.id.chat_list);
        cameraButton = (ImageButton) findViewById(R.id.camera_button);
        chatField = (EditText) findViewById(R.id.chat_field);
        sendButton = (Button) findViewById(R.id.send_button);

        // handle chat messages
        chatAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        chatList.setAdapter(chatAdapter);

        if (savedInstanceState != null) {
            for (Parcelable message : savedInstanceState.getParcelableArrayList("chat_messages")) {
                chatAdapter.addAll((ChatMessage) message);
            }
            scrollChatListToBottom();
        }

        // handle buttons
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatField.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    chatAdapter.add(new ChatMessage(true, message));
                    chatField.setText("");
                    scrollChatListToBottom();
                    getConversationResponseMockup(message);
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtils.dispatchTakePictureIntent(ChatActivity.this);
            }
        });
    }

    public void openQuestMapActivityMockup(String query) {
        List<Quest> quests = new ArrayList<Quest>();

        if (query.equalsIgnoreCase("museum")) {
            quests.add(new Quest(new String[]{"4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3", "Museum Ons' Lieve Heer op Solder", "Ons' Lieve Heer op Solder is ��n van de oudste en meest bijzondere musea van Amsterdam. Ga op ontdekking in een zeldzaam goed bewaard grachtenhuis uit de Gouden Eeuw. Smalle gangen en trappen leiden naar historisch ingerichte woonvertrekken, keukens en bedstedes en eindigt in het hoogtepunt van het museum: een complete kerk op zolder.", "<h2>Kerk op zolder</h2> De Rooms-Katholieke huiskerk werd in 1663 gebouwd, toen misvieringen officieel verboden waren, maar oogluikend werden toegestaan. De kerk is daarmee een typisch voorbeeld van de voor Nederland zo kenmerkende (religieuze) tolerantie, waarvoor Willem van Oranje in de 16de eeuw de basis legde. Vrijheid van geloof en vrijheid van geweten vormen tot op de dag van vandaag de pijlers van het museum. Daardoor is Museum Ons&rsquo: Lieve Heer op Solder ook nu nog een prachtige plek van beleving en bezinning.<br /> <br /> <h2>Museumcaf&eacute: en museumwinkel</h2> In het entreegebouw bevinden zich de museumwinkel (begane grond) en het museumcaf&eacute: (eerste verdieping). De museumwinkel biedt een breed assortiment aan boeken, geschenken en souvenirs. De winkel is vrij toegankelijk. Entree tot het museumcaf&eacute: is alleen mogelijk met toegangsbewijs voor het museum. Het museumcaf&eacute: is bereikbaar met de trap en met de lift.<br /> <br /> <h2>Verhuur</h2> Museum Ons&rsquo: Lieve Heer op Solder is af te huren voor feesten en partijen. Bovendien is het een offici&euml:le trouwlocatie.", "Ma-za: 10:00 - 17:00 uur, zo: 13:00 - 17:00 uur, Gesloten op wo 27 april 2016 Geopend op zo 1 januari 2017 van 13:00 - 17:00 uur.", "Museum Ons? Lieve Heer op Solder", "Tucked away in the heart of Amsterdam?s inner city lies a small marvel: Our Lord in the Attic Museum. Explore the narrow corridors of this uniquely preserved seventeenth-century house and climb the stairs to discover living rooms, kitchens and bedsteads furnished in Dutch Golden Age style, leading up to the literal highpoint of the museum: an entire church in the attic.", "<h2>A hidden treasure in Amsterdam<o:p></o:p></h2> This hidden church dates from 1663, when it was prohibited for Catholics to celebrate mass. However, the authorities tended to turn a blind eye and indeed, the church symbolises the characteristic (religious) tolerance of the Netherlands, established by the Dutch in the sixteenth century under William of Orange. Freedom of religion and conscience are central themes at the museum today. It makes Our Lord in the Attic far more than a museum: it&rsquo:s a special place in which to contemplate and to experience. <o:p></o:p><br /> <br /> <strong> <h2>Museum cafe and Dutch souvenirs</h2> </strong>The entrance building also houses the museum shop (on the ground floor) and the museum caf&eacute: (on the first floor). The museum shop offers a wide range of books, gifts and souvenirs, and is freely accessible. Access to the museum caf&eacute: is only possible with an admission ticket to the museum. The museum caf&eacute: can be reached by the stairs or by the lift.", "Mo -Sa : 10:00 - 17:00 hour, Su: 13:00 - 17:00 hour, Closed on We 27 April 2016 Open on Su 1 January 2017 from 13:00 - 17:00 hour.", "", "2.1.6", "Museum Ons' Lieve Heer op Solder", "AMSTERDAM", "Oudezijds Voorburgwal 38", "1012 GE", "52,3751050", "4,8994870", "http://www.opsolder.nl,http://www.amsterdamticketshop.nl/4425,http://www.facebook.com/pages/Museum-Ons-Lieve-Heer-op-Solder", "https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/157c35e1-80bd-450b-8c65-962a8fe8e08e.jpg,https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/39b45332-c39f-487d-bfb1-c63a55e5869b.jpg,https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/eaebbc08-dfea-4019-b1b4-14aaa6ee77f5.jpg,https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/57b9ec02-1bb7-4095-a37a-86145a32f3e1.jpg,https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/5c266769-5e66-46e7-b058-597adcec7793.jpg,https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/2ff57b3b-04ad-4d62-ae2f-f9dcbbe9f934.jpg,https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/c41158f5-a09f-429a-8c3a-470f2956500b.jpg,https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/b70ef9f3-2ae0-42b1-b87e-27c4f3c16383.jpg", "https://media.iamsterdam.com/ndtrc/Images/4e/4e39ce81-1b8f-48d4-ac7c-3dc8062fe1a3/157c35e1-80bd-450b-8c65-962a8fe8e08e.jpg", "07-12-2010", "", "", "", "2016-09-22 13:26:40"}));
            quests.add(new Quest(new String[]{"a900b5d7-cbe6-4013-a7da-fb32f8f4e92e", "Moco Museum", "Het Moco Museum (Modern Contemporary) heeft als doel om een brede en jonge doelgroep in Amsterdam kennis te laten maken met werken van internationale grootheden in de kunstwereld, zoals Banksy, Warhol en Basquiat. Het Amsterdamse museum richt zich vooral op moderne en hedendaagse kunst.", "<h2>Modern en hedendaags museum in Amsterdam</h2> Het <strong>Moco Museum</strong> is opgericht door Lionel en Kim Logchies, de eigenaren van een moderne kunstgalerie in het Amsterdamse <strong>Spiegelkwartier</strong>. De galeriehouders werkten eerder met grootse moderne kunstenaars zoals Koons, Hirst en Haring, en dankzij hun persoonlijke netwerk heeft het duo toegang tot de tentoonstellingswerken. Het museum maakt het mogelijk om exclusieve kunstwerken uit particuliere collecties met tijdelijke tentoonstellingen toegankelijk te maken voor een groter publiek.<br /> <h2>Openingstentoonstelling: Banksy &amp: Warhol</h2> Het nieuwe museum opent met een bijzondere duo-tentoonstelling van twee iconen uit de moderne kunst. Bij 'Laugh Now' zijn tot en met 4 september meesterwerken van street-art legende&nbsp:<strong>Banksy</strong>&nbsp:te bewonderen en de pop-art van&nbsp:<strong>Andy Warhol </strong>staat tot en met 4 juli in 'Royal' centraal.<br /> <br /> Het Moco Museum bevindt zich in Villa Alsberg op het <strong>Museumplein</strong>&nbsp:en ligt op een steenworp afstand van het <strong>Rijksmuseum</strong> en <strong>Van Gogh Museum</strong>.&nbsp:<br /> <br /> Bekijk hier meer <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/zien-en-doen/musea-en-galeries/musea-in-amsterdam&quot: target=&quot:blank&quot:>musea in Amsterdam</a>.", "Ma-zo: 10:00 - 18:00 uur, Toelichting: In de zomermaanden is het museum dagelijks tot 20.00 uur geopend.", "Moco Museum", "The Moco Museum (Modern Contemporary) aims to reach out to a wide, international audience in Amsterdam by featuring works created by the ?rock stars? of the art world, such as Banksy, Warhol and Basquiat. It is a private initiative, and the pieces on exhibition are made available thanks to individual loans.", "<h2>Modern and contemporary museum in Amsterdam</h2> The <strong>Moco Museum</strong> was founded by Lionel and Kim Logchies who have a permanent modern art gallery in Amsterdam&rsquo:s <strong>Spiegelkwartier</strong>. Having worked with some of modern art&rsquo:s greatest creatives over the years (think Koons, Hirst and Haring) it is due to the couple&rsquo:s personal network that the museum has inside access to its exhibition pieces. Now, they are able to place exclusive pieces that have been previously concealed in personal collections or private galleries on temporary exhibition for the enjoyment of the greater public. <br /> <br /> The Moco Museum is located on <strong>Museumplein</strong> and is sandwiched by the renowned <strong>Rijksmuseum</strong> and <strong>Van Gogh Museum</strong>. Find more <a target=&quot:_blank&quot:>museums in Amsterdam</a>.<a href=&quot:http://www.iamsterdam.com/en/visiting/what-to-do/museums-and-galleries&quot:><br /> </a>", "Mo -Su: 10:00 - 18:00 hour.", "", "2.1.6", "Moco Museum", "AMSTERDAM", "Honthorststraat 20", "1071 DE", "52,3587090", "4,8819010", "http://www.mocomuseum.com/", "https://media.iamsterdam.com/ndtrc/Images/a9/a900b5d7-cbe6-4013-a7da-fb32f8f4e92e/560d1bd0-4dff-4d35-a642-77684002eea7.jpg,https://media.iamsterdam.com/ndtrc/Images/a9/a900b5d7-cbe6-4013-a7da-fb32f8f4e92e/0c4ec6dd-a182-4b11-a630-6038e3bdeb15.jpg", "https://media.iamsterdam.com/ndtrc/Images/a9/a900b5d7-cbe6-4013-a7da-fb32f8f4e92e/560d1bd0-4dff-4d35-a642-77684002eea7.jpg", "06-04-2016", "", "", "", "2016-04-20 15:05:27"}));
            quests.add(new Quest(new String[]{"4fb89255-8a30-4ca4-b043-8cb7b37ab471", "Stedelijk Museum", "Het Stedelijk Museum, gelegen aan het Museumplein in Amsterdam, is het grootste museum met moderne en hedendaagse moderne kunst in Nederland. Het historische museumgebouw met rode bakstenen dateert uit 1895 en de collectie van het museum bestaat onder meer uit bijzondere kunstwerken van wereldberoemde moderne kunstenaars, onder wie Nederlandse kunstenaars uit de 20e eeuw.", "<h2>Moderne kunst in Amsterdam</h2> <p>Bij een bezoek aan het <strong>Stedelijk Museum</strong> maak je een reis door 150 jaar kunst, waarbij het beste van <strong>moderne kunst in Amsterdam</strong> de revue passeert. Iconische werken van Karel Appel, C&eacute:zanne, Chagall, Marlene Dumas, Kandinsky, Edward Kienholz, De Kooning, Koons, Malevich, Matisse, Mondrian, Picasso, Pollock, Gerrit Rietveld, Warhol en vele anderen zijn hier te zien. De <strong>designcollectie</strong> van het Stedelijk Museum toont de geschiedenis van vormgeving, vanaf het begin van de vorige eeuw tot nu, met meubels, keramiek, posters, sieraden en andere voorwerpen.</p> <h2>Grote stromingen in kunst, vormgeving en architectuur</h2> <p>De collectie van het Stedelijk Museum bestaat uit ongeveer 90.000 kunstwerken en voorwerpen, vanaf 1870 tot nu. De collectie bevat schilderijen, sculpturen, fotografie, film en video, installaties, werken op papier, kunstenaarsboeken, toegepaste kunst en industri&euml:le en grafische vormgeving. Met grote kunststromingen, zoals Bauhaus, de <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/zien-en-doen/architectuur/amsterdamse-school&quot:>Amsterdamse School</a>, De Stijl, CoBrA, abstract expressionisme, popart, minimalistische kunst en conceptuele kunst allen vertegenwoordigd, biedt het Stedelijk Museum een geweldig overzicht van <strong>moderne</strong> en<strong> hedendaagse kunst</strong>, <strong>vormgeving</strong> en <strong>architectuur</strong>.</p> <h2>De groten der kunstwereld</h2> De uitgebreide collectie van het StedelijkMuseum is qua stijl vergelijkbaar met het <strong>Museum of Modern Art</strong> in New York en het Art Institute van Chicago. Vormgeving heeft voor het eerst een permanente plek gekregen in het museum, met grote namen als Joseph Hoffmann, Gerrit Rietveld en Tapio Wirkkala. Ook <strong>hedendaagse kunstenaars</strong> zijn sterk vertegenwoordigd met topstukken van onder anderen Maarten Baas, Barbara Bloom, Rineke Dijkstra, Marlene Dumas, Sheila Hicks, Jeff Koons, Wolfgang Tillmans, Edward Kienholz, Willem de Kooning en Andy Warhol. Sinds de renovatie en uitbreiding van het gebouw kan het Stedelijk meer van de beroemde collectie laten zien dan ooit, met dank aan de vele nieuwe museumzalen.<br /> <br /> <h2>Innovatieve tentoonstellingszalen</h2> Naast de grote zalen voor <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/uit/agenda#-f667KytS50+bOuL49X2fQA-------1&quot:>tentoonstellingen</a> is ook het aanzicht van het museum zelf behoorlijk veranderd tijdens de renovatie: het gebouw kijkt nu niet meer weg van het <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/ontdek-amsterdam/amsterdamse-buurten/zuid/museumkwartier&quot:>Museumplein</a>, maar kijkt er juist op uit. De indrukwekkende foyer en het restaurant zien er uitnodigend uit en er is een futuristisch uitziende <strong>nieuwe vleugel</strong>, ontworpen door <strong>Benthem Crouwel Architecten</strong>, die Amsterdammers ook wel de 'badkuip' noemen.<br /> <br /> Het Stedelijk Museum is&nbsp:gratis toegankelijk voor jongeren tot en met 18 jaar en is vanaf 2016 op vrijdag tot 22.00 uur geopend.<br /> <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/zien-en-doen/musea-en-galeries&quot:>Ontdek meer musea in Amsterdam</a><br /> <br /> <h2>Openbaar vervoer</h2> Tram 2, 3, 5, 12. Deze informatie is onder voorbehoud. Kijk op de website van <a href=&quot:http://www.gvb.nl&quot:>GVB</a> voor de meest recente informatie.", "Ma-do: 10:00 - 18:00 uur, vr: 10:00 - 22:00 uur, za, zo: 10:00 - 18:00 uur.", "Stedelijk Museum", "Situated on Museumplein, where it first opened in 1895, Amsterdam?s Stedelijk Museum is the largest Dutch museum dedicated to modern and contemporary art and design. The collection contains extraordinary pieces of art by world-renowned artists, including a great number of works by major twentieth-century Dutch artists. ", "<h2>Modern art in Amsterdam </h2> <p>A visit to the <strong>Stedelijk Museum</strong> takes the visitor on a journey through the last 150 years of art, presenting the best of <strong>modern art in Amsterdam</strong>. Iconic works by Karel Appel, C&eacute:zanne, Chagall, Marlene Dumas, Kandinsky, Edward Kienholz, De Kooning, Koons, Malevich, Matisse, Mondrian, Picasso, Pollock, Gerrit Rietveld, Warhol and many others are on show. The Stedelijk Museum&rsquo:s <strong>design collection</strong> also traces the history of design from the turn of the last century to the present, showcasing furniture, ceramics, posters, jewellery and other objects.</p> <h2>Major movements of art, design and architecture</h2> <p>The Stedelijk Museum's collection contains ca. 90,000 artworks and objects, dating from 1870 to the present. It includes paintings and sculpture, photography, film and video, installations, works on paper, artist&rsquo:s books, applied arts and industrial and graphic design. With major movements, such as Bauhaus, the <a href=&quot:http://www.iamsterdam.com/en/visiting/what-to-do/architecture/amsterdam-school&quot:>Amsterdam School</a>, De Stijl, CoBrA, abstract expressionism, pop art, minimal art and conceptual art all represented, the Stedelijk Museum offers a great overview of <strong>modern</strong> and <strong>contemporary art</strong>, <strong>design</strong> and <strong>architecture</strong>.</p> <h2>Innovative art space</h2> <p>In addition to providing more spacious halls for <a href=&quot:http://www.iamsterdam.com/en/visiting/whats-on/search-agenda#-f667KytS50+bOuL49X2fQA-------2&quot:>exhibitions</a>, the refurbishment and new wing have also changed the public face of the museum &ndash: which now looks out onto <a href=&quot:http://www.iamsterdam.com/en/visiting/areas/amsterdam-neighbourhoods/centre/museum-quarter&quot:>Museumplein</a> rather than away from it. The impressive new foyer and restaurant are extremely inviting to both locals and travellers alike, the large museum shop is well-stocked with posters, art books, postcards and plenty of design trinkets, and thanks an avid team of art loving volunteers, the Stedelijk&rsquo:s programming is regularly enriched by special <strong>guided tours</strong>, <strong>lectures</strong> and other <strong>live performances</strong>.</p> <h2>The new wing</h2> <p>The Stedelijk reopened in September 2012 following extensive renovations that saw the interior of the museum brought right up to date and the addition of an eye-catching, futuristic <strong>new wing</strong>, designed by <strong>Benthem Crouwel Architects</strong> and known among Amsterdammers as &lsquo:the bathtub&rsquo: because of its distinctive shape and its smooth white surface. </p> <p><a target=&quot:blank&quot: href=&quot:http://www.iamsterdam.com/en/visiting/what-to-do/museums-and-galleries&quot:>Discover more museums in Amsterdam</a><br /> <br /> </p> <h2>How to get there</h2> Tram 2, 3, 5, 12. This information is subject to change: for up-to-date public transport info check the <a href=&quot:http://en.gvb.nl&quot:>GVB website</a>", "Mo -Th : 10:00 - 18:00 hour, Fr : 10:00 - 22:00 hour, Sa , Su: 10:00 - 18:00 hour.", "", "2.1.6", "", "AMSTERDAM", "Museumplein 10", "1071 DJ", "52,4957689", "4,531446", "http://www.stedelijk.nl/,http://www.facebook.com/Stedelijk", "https://media.iamsterdam.com/ndtrc/Images/20101215/5fec99a7-9084-4269-8d97-0c229c02b394.jpg,https://media.iamsterdam.com/ndtrc/Images/20101215/fd178148-08ed-4ff8-8999-d6203d48b151.jpg,https://media.iamsterdam.com/ndtrc/Images/20101215/a5479c1b-0e2e-46f8-b116-4b574447e9a4.jpg,https://media.iamsterdam.com/ndtrc/Images/20101215/829c4442-3ad6-493d-8b88-addb19f2710d.jpg", "https://media.iamsterdam.com/ndtrc/Images/20101215/5fec99a7-9084-4269-8d97-0c229c02b394.jpg", "15-12-2010", "", "", "", "2016-10-18 14:51:34"}));
            quests.add(new Quest(new String[]{"cbf77bab-b6c9-4443-b62b-4ec7c01a7f52", "Van Gogh Museum", "Het Van Gogh Museum in Amsterdam beheert de grootste verzameling werken van Vincent van Gogh ter wereld. De vaste collectie omvat 200 schilderijen, 500 tekeningen, meer dan 700 brieven en Van Goghs verzameling Japanse prenten. Het Van Gogh opende in 1973 op het Museumplein en is een van de populairste musea ter wereld.", "<h2>Vincent van Gogh in Amsterdam</h2> <p>Een bezoek aan het <strong>Van Gogh Museum</strong> is een unieke en inspirerende ervaring, voor bewoners &eacute:n toeristen. Naast de herkenbare post-impressionistische werken van Van Gogh, als zijn landschappen, zelfportretten en stillevens &ndash: &lsquo:<strong>Zonnebloemen</strong>&rsquo: in het bijzonder &ndash: biedt het museum ook de kans om de ontwikkeling van de kunstenaar te verkennen en zijn schilderijen te vergelijken met andere 19de-eeuwse kunstenaars. De collectie omvat werken van de impressionisten en post-impressionisten, van kunstenaars die <strong>Vincent van Gogh</strong> inspireerden of juist door hem zijn be&iuml:nvloed en van zijn vrienden en tijdgenoten.</p> <h2>Uitzonderlijke tentoonstellingen en concepten</h2> <p>Het museum is in 1999 uitgebreid en in 2013 volledig gerenoveerd. Het blijft hiermee een unieke plek voor de bijzondere werken van Van Gogh en de <strong>indrukwekkende tijdelijke tentoonstellingen</strong>. In 2015 opende het museum een nieuwe entree aan het <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/ontdek-amsterdam/amsterdamse-buurten/zuid/museumkwartier&quot:>Museumplein</a>, een moderne en aantrekkelijke toegang die beter inspeelt op het grote aantal bezoekers. In de afgelopen jaren passeerden <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/uit/agenda#-f667KytS50+bOuL49X2fQA-------1&quot:>grote tentoonstellingen</a> de revue in het museum, met meesterwerken van Van Goghs gelijken als Gauguin, Munch, Malevich, Kandinsky en Vallotton.</p> <h2>Meer dan schilderijen</h2> <p>Het museum biedt ook onderdak aan bijzondere <strong>onderzoeksfaciliteiten</strong>, die onze kennis verrijkt over Van Goghs leven, hoe hij werkte, en biedt bezoekers een kijkje in het doorlopende conservatieproces. Er zijn <strong>creatieve workshops voor kinderen</strong> en <strong>elke vrijdag&nbsp:blijft het museum open tot 22.00 uur</strong>, voor een live programma met muziek, dj's, performances workshops, rondleidingen en een cocktailbar.</p> <h2>Openbaar vervoer</h2> Tram 2, 3, 5, 12. Deze informatie is onder voorbehoud. Kijk op de website van <a href=&quot:http://www.gvb.nl&quot:>GVB</a> voor de meest recente informatie.", "Ma-do: 09:00 - 18:00 uur, vr: 09:00 - 22:00 uur, za, zo: 09:00 - 18:00 uur.", "Van Gogh Museum", "The Van Gogh Museum in Amsterdam is one of the most popular museums in the world, attracting visitors from every corner of the globe. Naturally, this is in large part due to it housing the largest collection of works by Vincent van Gogh ? more than 200 paintings, 500 drawings and 700 of his letters. Having originally opened on Museumplein in 1973, the Van Gogh Museum has been expanded and modernised over the years, ensuring it's a truly cutting-edge exhibition and visitor space.", "<h2>The legacy of Vincent van Gogh in Amsterdam</h2> <p>For both locals and far-travelling visitors, the <strong>Van Gogh Museum</strong> is a unique and inspirational experience. Alongside the legacy of <strong>Vincent van Gogh</strong>'s instantly recognisable impressionist works, such as his landscapes, self-portraits and still lifes &ndash: especially &lsquo:<strong>Sunflowers</strong>&rsquo: &ndash: the museum provides opportunities to track the artist's development and compare his paintings to works by other artists from the 19th century &ndash: those who inspired him and those who drew inspiration from him.</p> <h2>Cutting-edge exhibitions and ideas</h2> <p>The Van Gogh Museum was expanded in 1999, then entirely refurbished in 2013, ensuring that it remains a <strong>cutting-edge exhibition space</strong> for both the Van Gogh works and the impressive temporary exhibitions. In 2015 the museum opened a new entrance directly on the <a href=&quot:http://www.iamsterdam.com/en/visiting/areas/amsterdam-neighbourhoods/centre/museum-quarter&quot:>Museumplein</a>, providing a modern and attractive entry point that better caters to the large number of visitors. In recent years the museum has also welcomed <a href=&quot:http://www.iamsterdam.com/en/visiting/whats-on/search-agenda#-f667KytS50+bOuL49X2fQA-------2&quot:>major exhibitions</a> that showcased <strong>masterworks</strong> from Van Gogh's peers, such as Gauguin, Munch, Malevich, Kandinsky and Vallotton.</p> <h2> More than paintings on a wall</h2> <p>The museum is also home to exceptional<strong> research facilities</strong> that add true depth to our knowledge about Van Gogh's life, how he worked, as well as showing visitors an insider look at the conservation work that goes on. There are <strong>art workshops for kids</strong>, and <strong>on&nbsp:Friday&nbsp:it stays open until 22:00</strong>, inviting musicians, DJs and other performers to share its public spaces with the artworks and visitors.</p> <h2>How to get there</h2> Tram 2, 3, 5, 12. This information is subject to change: for up-to-date public transport info check the <a href=&quot:http://en.gvb.nl&quot:>GVB website</a>", "Mo -Th : 09:00 - 18:00 hour, Fr : 09:00 - 22:00 hour, Sa , Su: 09:00 - 18:00 hour.", "", "2.1.6", "Van Gogh Museum", "AMSTERDAM", "Museumplein 6", "1071 DJ", "52,3584192", "4,8788869", "http://www.vangoghmuseum.nl/,http://www.facebook.com/VanGoghMuseum", "https://media.iamsterdam.com/ndtrc/Images/cb/cbf77bab-b6c9-4443-b62b-4ec7c01a7f52/621e14a4-0de5-42c7-b2f8-551198e742e6.jpg,https://media.iamsterdam.com/ndtrc/Images/cb/cbf77bab-b6c9-4443-b62b-4ec7c01a7f52/1e740808-6103-4061-a766-577144024c11.jpg,https://media.iamsterdam.com/ndtrc/Images/cb/cbf77bab-b6c9-4443-b62b-4ec7c01a7f52/5e955f23-b6ea-4a57-bf79-3fc0af86ec9b.jpg,https://media.iamsterdam.com/ndtrc/Images/cb/cbf77bab-b6c9-4443-b62b-4ec7c01a7f52/af75d487-19fe-45d6-bfd5-bd21102536fd.jpg", "https://media.iamsterdam.com/ndtrc/Images/cb/cbf77bab-b6c9-4443-b62b-4ec7c01a7f52/af75d487-19fe-45d6-bfd5-bd21102536fd.jpg", "26-10-2010", "", "", "", "2016-09-26 10:58:56"}));
            quests.add(new Quest(new String[]{"95404120-70b7-427f-bd55-485b7bd144a5", "Joods Historisch Museum", "Synagogen, markante gebouwen, monumenten en bijzondere beelden kleuren het straatbeeld van de oude Amsterdamse jodenbuurt. Midden in deze buurt bevindt zich het Joods Cultureel Kwartier. Tijdens een wandeling door het kwartier kom je langs het Joods Historisch Museum met haar Kindermuseum, de kolossale Portugese Synagoge en de Hollandsche Schouwburg. Vier culturele instellingen - te bezoeken met ��n toegangsticket -  die samen een uniek beeld geven van het joodse leven van toen en nu.", "<h2>Joods Historisch Museum</h2> Het Joods Historisch Museum is een modern museum over het jodendom, dat is gevestigd in vier monumentale synagogen bij het Waterlooplein, in het hart van de oude jodenbuurt van Amsterdam. In drie vaste opstellingen wordt hier de geschiedenis en de cultuur van de joden in Nederland zichtbaar. Met schilderijen, tekeningen, gebruiksvoorwerpen, foto's, films en moderne 3D-presentaties kan het met recht een multimediaal museum genoemd worden, voor zowel jong als oud. Naast de drie vaste opstellingen, herbergt het JHM een Kindermuseum en zijn er altijd een of twee tijdelijke tentoonstellingen.<br /> <br /> <h2>JHM Kindermuseum</h2> In het JHM Kindermuseum woont de joodse familie Hollander. Bij hen thuis ben je van harte welkom. De Hollanders houden van de joodse traditie: ieder op zijn eigen manier, soms serieus, soms met een knipoog. En wat leuk is, je kunt er van alles doen. Brood vlechten in de keuken, Hebreeuws leren in de studeerkamer of samen muziek maken in de muziekkamer. Huisvriend Max de Matze wijst iedereen de weg en maakt van het bezoek een feestje!<br /> <br /> <h2> Joods Cultureel Kwartier Amsterdam</h2> Ervaar de hoogtepunten van Joods Amsterdam met het speciale <a target=&quot:blank&quot: href=&quot:http://www.jhm.nl/bezoek/joods-cultureel-kwartier&quot: re_target=&quot:_new&quot: tabindex=&quot:0&quot:>Joods Cultureel Kwartier ticket</a>. Hij is geldig voor een maand en je kunt er het Joods Historisch Museum, het JHM Kindermuseum, de Portugese Synagoge en de Hollandse Schouwburg mee bezoeken - inclusief evenementen en tentoonstellingen, tenzij anders aangegeven. <p> </p> <h2>Openbaar vervoer</h2> <br /> Metro 51, 53, 54, tram 9, 14. Deze informatie is onder voorbehoud. Kijk op de website van <a href=&quot:http://www.gvb.nl&quot:>GVB</a> voor de meest recente informatie.", "Ma-zo: 11:00 - 17:00 uur, Gesloten op wo 27 april 2016 Gesloten op ma 3 oktober 2016 Gesloten op di 4 oktober 2016 Gesloten op wo 12 oktober 2016 Toelichting: Op 5, 24, 25 en 31 december 2016 open tot 16.00 uur.", "Jewish Historical Museum", "The Jewish Historical Museum is located at the heart of Amsterdam's former Jewish quarter. Although not as famous as the bigger art museums, it is in fact one of the city?s most acclaimed museums due to its incisive programming. Housed in four former Ashkenazi synagogues dating from the 17th and 18th centuries, its permanent exhibition reflects the versatility of Judaism in the past and present. It also has its own dedicated children's museum with exceptional educational facilities.", "<h2>Shared history </h2> The Jewish Historical Museum collects objects and works of art associated with the religion, culture and history of Jews and Judaism in the Netherlands and its former colonies. The museum has more than 30,000 works of art, ceremonial items and historical objects, of which only 5% is on display at any one time. The museum is especially renowned for its temporary exhibitions that highlight Jewish history, artists, musicians and others who&rsquo:ve influenced social and cultural events. <br /> <br /> <h2>JHM Children's Museum</h2> The Children's Museum is home to the Hollanders, a Dutch Jewish family. The various rooms in the house reflect the family's concerns and key elements in Jewish tradition: that each person has a responsibility for the world in which they live and that everyone can contribute in their own way. Younger visitors can explore everyday subjects such as food, study, memories and music.<br /> <br /> <h2>Jewish Cultural Quarter in Amsterdam </h2> Experience the highlights of Jewish Amsterdam by purchasing a <a href=&quot:http://www.jhm.nl/visit/jewish-cultural-quarter&quot: target=&quot:blank&quot: re_target=&quot:_new&quot: tabindex=&quot:0&quot:>Jewish Cultural Quarter</a> ticket. Valid for one month, you can enjoy access to the JHM, the Children's Museum, Portuguese Synagogue and the Hollandsche Schouwburg (National Holocaust Memorial) &ndash: including events and exhibitions, unless otherwise specified. <p> </p> <h2>How to get there</h2> <br /> Metro 51, 53, 54, tram 9, 14. This information is subject to change: for up-to-date public transport info check the <a href=&quot:http://en.gvb.nl&quot:>GVB website</a>", "Mo -Su: 11:00 - 17:00 hour, Closed on We 27 April 2016 Closed on Mo 3 October 2016 Closed on Tu 4 October 2016 Closed on We 12 October 2016.", "", "2.1.6", "", "AMSTERDAM", "Nieuwe Amstelstraat 1", "1011 PL", "52,3673490", "4,9039150", "http://www.jhm.nl,http://www.facebook.com/joodscultureelkwartier", "https://media.iamsterdam.com/ndtrc/Images/20101125/757b9357-1f45-4614-b47d-c9124fe2741d.jpg,https://media.iamsterdam.com/ndtrc/Images/20101125/40cf455f-81ba-418b-b077-57e396dd53e6.jpg,https://media.iamsterdam.com/ndtrc/Images/20101125/0e561909-2a47-4e76-881a-3467ae7f9c97.jpg,https://media.iamsterdam.com/ndtrc/Images/20101125/077175f6-1c67-4afe-ba9f-502c03c0f7b6.jpg,https://media.iamsterdam.com/ndtrc/Images/20101125/75179d74-f575-4516-99e8-7fe42ada5e64.jpg", "https://media.iamsterdam.com/ndtrc/Images/20101125/757b9357-1f45-4614-b47d-c9124fe2741d.jpg", "25-11-2010", "", "", "", "2016-09-22 13:06:15"}));
            quests.add(new Quest(new String[]{"163610f3-3d64-42b3-9528-96404cd16058", "Museum Vrolik", "Museum Vrolik kan met recht een van de meest ongewone musea in Amsterdam genoemd worden. Het museum is gevestigd in de Universiteit van Amsterdam (UVA) en huisvest een collectie van buitengewone medische items verzameld door de 19e eeuwse medische professor Gerardus Vrolik en zijn zoon Willem. Bezoekers kunnen vanaf dit jaar in het geheel gerenoveerde Museum Vrolik een bijzondere ontdekkingstocht maken door het (ongewone) menselijk lichaam.", "<h2>Museum Vrolik</h2> De collectie in Museum Vrolik is zeker niet voor watjes!  Stalen zenuwen zijn sterk aangeraden: de <strong>tentoonstelling </strong>laat onder andere anatomische modellen, skeletten, schedels, anatomische modellen en reconstructies zien. Ook zijn er voorbeelden van baby's met aangeboren afwijkingen, zoals een Siamese tweeling en cyclopische baby's, te bewonderen.<br /> <br /> <h2>Anatomische collectie</h2> Professor Gerardus Vrolik was naast ontleed- en dierkundige ook gespecialiseerd in aangeboren afwijkingen, ook wel teralogie genoemd. Hij en zijn zoon verzamelden jarenlang een collectie van medische en anatomische items. Een <strong>bijzondere</strong> <strong>collectie</strong> die hedendaags nog steeds van grote wetenschappelijke waarde is. Het Athenaeum Illustre (de voorloper van de UVA) kocht deze collectie in 1865 van de professor over, maar stelde het pas tentoon in 1984. Museum Vrolik maakte toentertijd nog deel uit van het Amsterdam Medisch Centrum.<br /> <br /> <h2>Toegankelijk voor het grote publiek</h2> Het&nbsp:<strong>museum</strong> verwelkomde voornamelijk vele studenten en medische specialisten, maar richt zich sinds enkele jaren ook op een breder publiek.&nbsp:<span style=&quot:font-size: 12.8px:&quot:>Museum Vrolik is doordeweeks geopend van 09.30 tot 17.00 uur. In het weekend is het museum gesloten.&nbsp:Meer informatie vind je op de website.</span><br /> <br /> Bekijk hier een overzicht van <a href=&quot:&ldquo:http://www.iamsterdam.com/nl/uit-in-amsterdam/zien-en-doen/musea-en-galeries&rdquo:&quot:>alle musea in Amsterdam</a>.", "ma-vr: 10:00 - 17:00 uur za, zo: gesloten", "Museum Vrolik", "Certainly one of Amsterdam?s more unusual museums, Museum Vrolik is home to an extraordinary collection of medical case studies, accrued by 19th-century medical professor Gerardus Vrolik, his son Willem and numerous other medical professionals.", "<h2>Museum Vrolik</h2> The Athenaeum Illustre of Amsterdam acquired the original collection in 1865 but it wasn&rsquo:t until 1984 that a selection of exhibits went on show at Museum Vrolik, part of the Amsterdam Medical Center.<br /> <h2 > Anatomical collection</h2> Not for the faint-hearted, the unique collection contains an extensive range of conserved anatomical specimens, skeletons, skulls, anatomical models and reconstructions. Of huge scientific value is the collection of specimens showing birth defects, including Siamese twins and cyclopean babies.<br /> <h2 > Appealing to a wider audience</h2> The museum has primarily welcomed students and medical specialists over the years, but aims to extend its appeal to a much wider audience following refurbishment and reorganisation (completed in 2012), providing a unique experience to all visitors interested in the (abnormal) human body.<br /> <br /> Please note that the museum is closed during the weekend.", "Mo -Fr : 10:00 - 17:00 hour Sa , Su: Closed", "", "2.1.6", "", "AMSTERDAM", "Meibergdreef 9", "1105 AZ", "52,2950080", "4,9578980", "http://www.museumvrolik.nl/", "https://media.iamsterdam.com/ndtrc/Images/20101215/2f9f7849-9caf-477b-bca3-7ef20b32f6a5.jpg,https://media.iamsterdam.com/ndtrc/Images/20101215/568ae3f4-75a3-4dff-b578-eb9168e1442a.jpg,https://media.iamsterdam.com/ndtrc/Images/20101215/d6e3d8d0-5870-450a-b6c8-6c431c10c321.jpg,https://media.iamsterdam.com/ndtrc/Images/20101215/8726ae8f-1f0d-478e-87d5-fd35636c2942.jpg,https://media.iamsterdam.com/ndtrc/Images/20101215/461cbf39-d361-4024-87ac-ee8c220fc97a.jpg", "https://media.iamsterdam.com/ndtrc/Images/20101215/2f9f7849-9caf-477b-bca3-7ef20b32f6a5.jpg", "15-12-2010", "", "", "", "2016-04-06 12:10:08"}));
            quests.add(new Quest(new String[]{"fe7b7dba-c768-4713-8802-043f580d5dec", "Museum De Zwarte Tulp", "Museum De Zwarte Tulp, h�t museum over de bloembollencultuur en -kunst, is gevestigd in Lisse, hart van de Bloembollenstreek.  ", "Museum De Zwarte Tulp, h&eacute:t museum over de bloembollencultuur en -kunst, is gevestigd in Lisse, hart van de Bloembollenstreek.<br /> <br /> Het museum vertelt over bloembollenteelt, bollenexport en over vermeerderen en veredelen van bollen. Ook besteden wij aandacht aan belangrijke gebeurtenissen uit de geschiedenis van de tulp en andere bolgewassen: de komst van de tulp naar Europa en Nederland, de tulpomanie &ndash: tulpengekte &ndash: in de 17de eeuw, de hyacintenspeculatie in de 18de eeuw en het verhaal van de zwarte tulp.<br /> <br /> Het museum is gehuisvest in een gebouw in het centrum van Lisse dat ooit als bollenschuur dienst deed. Onderdeel van het museum zijn de indrukwekkende vroeg 20ste-eeuwse directiekamers van Kalkzandsteenfabriek Van Herwaarden BV &ndash: de Comparitie genaamd. In de museumtuin bloeit in het seizoen uiteraard een variatie aan bolgewassen.<br /> <br /> Er is een museumwinkel aanwezig en daarbij is er de gelegenheid tot het drinken van koffie, thee of frisdrank in het museumcaf&eacute:.", "Toelichting: Openingstijden  Reguliere openingstijden: 13:00 ? 17:00 uur Maart t/m augustus    : 10:00 ? 17:00 uur  Op maandagen is het museum gesloten  Gesloten op 1e Paasdag, Bloemencorso, Koningsdag, 1e Pinksterdag, Start Feestweek Lisse, Harddraverij Lisse, 5 december, 1e Kerstdag, Oudjaar en Nieuwjaarsdag.", "Museum 'De Zwarte Tulp'", "With the emphasis on tulips, the bulb cultivation and development of both the past and present, are vividly displayed in the museum.", "<p class=&quot:MsoNormal&quot: style=&quot:margin-bottom: 0.0001pt:&quot:><span lang=&quot:EN-US&quot: style=&quot:font-size: 10pt: font-family: arial, sans-serif: color: #606080:&quot:>Museum &ldquo:de Zwarte Tulp&rdquo: is situated in a restored, old building at the town centre of Lisse. It shows the history of the Bulb District as well as the origins and the development of bulb culture.<br /> <br /> The museum paints an enthralling picture of the remarkable &lsquo:bulb-to-flower-to-bulb&rsquo: cycle by means of: pictures, photos and videos on the working of soil: bulb culture artifacts: shed interiors: and a small specialized library.<o:p></o:p></span></p> <p class=&quot:MsoNormal&quot: style=&quot:margin-bottom: 0.0001pt:&quot:><span lang=&quot:EN-US&quot: style=&quot:font-size: 10pt: font-family: arial, sans-serif: color: #606080:&quot:>&nbsp:</span></p> <h4 style=&quot:margin: 0cm 0cm 0.0001pt:&quot:><span lang=&quot:EN-US&quot: style=&quot:font-size: 10pt: font-family: arial, sans-serif: color: #606080:&quot:>A visitor's guide is available in 6 languages.</span><span lang=&quot:EN-US&quot: style=&quot:font-size: 10pt: font-family: arial, sans-serif: color: #606080:&quot:> Next to the </span><span lang=&quot:EN-US&quot: style=&quot:font-size: 10pt: font-family: arial, sans-serif: color: #606080:&quot:>permanent exhibition the museum has a number of special exhibitions throughout the year.<br /> A visit to the museum can be easily combined with a visit to Keukenhof flower gardens, also situated in Lisse.<o:p></o:p></span></h4>", "", "", "2.1.6", "Museum De Zwarte Tulp", "LISSE", "Heereweg 219", "2161 BG", "52,2588810", "4,5536360", "http://www.museumdezwartetulp.nl/", "https://media.iamsterdam.com/ndtrc/Images/fe/fe7b7dba-c768-4713-8802-043f580d5dec/bdc01f62-6f32-4a98-be30-510bc023c58a.jpg,https://media.iamsterdam.com/ndtrc/Images/fe/fe7b7dba-c768-4713-8802-043f580d5dec/9cb3b2ee-cf07-4f03-a592-fb0f4b29dd2d.jpg,https://media.iamsterdam.com/ndtrc/Images/fe/fe7b7dba-c768-4713-8802-043f580d5dec/b160cd0e-8969-4cbb-82af-a07272cb768e.jpg,https://media.iamsterdam.com/ndtrc/Images/fe/fe7b7dba-c768-4713-8802-043f580d5dec/ef2b922f-aae5-45c1-8bdd-6cfb5ce543a6.jpg", "https://media.iamsterdam.com/ndtrc/Images/fe/fe7b7dba-c768-4713-8802-043f580d5dec/bdc01f62-6f32-4a98-be30-510bc023c58a.jpg", "", "", "", "", "2016-01-26 09:30:49"}));
            quests.add(new Quest(new String[]{"7d869de4-71fc-4ce6-9534-a5988cc90041", "Museum Het Schip - De Amsterdamse School", "In Museum Het Schip leer je alles over de sociale, politieke en artistieke context van de Amsterdamse Schoolstijl. Het museum bevindt zich in Het Schip aan het Spaarndammerplantsoen, een van de drie monumentale woningbouwcomplexen ontworpen door Michel de Klerk in 1919.", "<h2>Museum Het Schip</h2> Het Schip bestaat uit 102 appartementen voor arbeidersgezinnen, een kleine ontmoetingshal en een voormalig postkantoor dat nu dienst doet als museum ter ere van de Amsterdamse School-beweging. In dit museum vind je een reeks aan vaste tentoonstellingen en ontdek je meer over de (sociale) geschiedenis en vormtaal van de architectuurstroming. Ook kun je er een gerestaureerde woning inclusief inrichting uit de jaren '20 bekijken.<br /> <br /> <h2> Amsterdamse School</h2> Museum Het Schip organiseert regelmatig rondleidingen langs de belangrijkste bouwwerken van de Amsterdamse School en op zondagen is er een speciale rondleiding door het Scheepvaarthuis.<br /> In het museum zijn dagelijks rondleidingen vanaf 11:00 uur, op elk heel uur. Duur van de rondleiding is 45 minuten, waarbij alle onderdelen van het museum belicht worden. <br /> <br /> Ontdek meer over <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/zien-en-doen/architectuur/amsterdamse-school&quot:>architectuurstroming de Amsterdamse School</a>.<br /> <br /> <h2>Openbaar vervoer</h2> Bus 22, 48. Deze informatie is onder voorbehoud. Kijk op de website van <a href=&quot:http://www.gvb.nl&quot:>GVB</a> voor de meest recente informatie.", "Di-zo: 11:00 - 17:00 uur, Gesloten op wo 27 april 2016 Toelichting: Op feestdagen geopend op maandag van 11.00 tot 17.00 uur.", "Museum Het Schip", "Museum Het Schip explores the Amsterdam School style of architecture in its social, political and artistic context. The museum is located inside Het Schip (the ship) on the Spaarndammerplantsoen, one of the three monumental Amsterdam School apartment blocks there designed by Michel de Klerk. ", "<h2>Museum het Schip</h2> Het Schip consists of 102 flats for working-class families, a small meeting hall and a post office which is&nbsp:now home to the museum. The museum has a variety of interesting permanent displays about the Amsterdam School movement, including its background and sociological contributions to the city. You can also see a restored working-class flat and a collection of &ldquo:street furniture&rdquo: done in Amsterdam School style, including lamps, benches, trash bins and more. <br /> <br /> <h2>Amsterdamse School Tours</h2> Museum Het Schip regularly organizes guided walking tours past the most prominent buildings done in the Amsterdam School style, as well as tours of the interior of the Scheepvaarthuis.<br /> Daily tours in museum het Schip during opening hours every hour on the hour there is a guided tour of 45 minutes, first one is at 11:00 hours.<br /> <br /> <h2>How to get there</h2> Bus 22, 48. This information is subject to change: for up-to-date public transport info check the <a href=&quot:http://en.gvb.nl&quot:>GVB website</a>", "Tu -Su: 11:00 - 17:00 hour, Closed on We 27 April 2016.", "", "2.1.6", "", "AMSTERDAM", "Spaarndammerplantsoen 140", "1013 XT", "52,3899360", "4,8741410", "http://www.hetschip.nl,http://www.facebook.com/MuseumHetSchip", "https://media.iamsterdam.com/ndtrc/Images/20101130/29bb21f4-3f4a-4973-bf4e-cb01ec00a0a2.jpg,https://media.iamsterdam.com/ndtrc/Images/20101130/bebb08fa-e44f-4c08-b145-c86af02da102.jpg,https://media.iamsterdam.com/ndtrc/Images/20101130/da878400-414d-4c44-bafc-3e68f5c96782.jpg,https://media.iamsterdam.com/ndtrc/Images/20101130/c8e74457-d62e-4ca8-a9a6-8f549156cad8.jpg,https://media.iamsterdam.com/ndtrc/Images/20101130/efae884a-91ad-4fb9-95c8-9f7d3be98289.jpg,https://media.iamsterdam.com/ndtrc/Images/20101130/39c2321e-506c-4bbe-8efa-f788e400bb25.jpg,https://media.iamsterdam.com/ndtrc/Images/20101130/4abe5127-9e67-4113-a5d8-04b33f506213.jpg,https://media.iamsterdam.com/ndtrc/Images/20101130/a87ff022-69ed-4431-89f5-5e6ecf89a109.jpg", "https://media.iamsterdam.com/ndtrc/Images/20101130/29bb21f4-3f4a-4973-bf4e-cb01ec00a0a2.jpg", "30-11-2010", "", "", "", "2016-09-22 13:25:34"}));
            quests.add(new Quest(new String[]{"6c66bbe4-f649-4465-b98d-5af9ca01a7be", "Museum 't Kromhout", "Museum 't Kromhout heeft een educatief programma dat de ontwikkeling van de Oostelijke eilanden en van de scheepsbouw volgt. ", "<h2>Bezoek</h2> Het museum geeft een beeld van scheepsbouw en de gereedschappen die daarvoor gebruikt worden. Het toont een collectie stoommachines, Kromhout- en andere motoren.&nbsp:Het museum is alleen op dinsdagen van 10:00-15:00 uur geopend en voor groepen van 15 personen of meer, ook (op afspraak) op andere dagen. Daarnaast is het museum in de zomermaanden gratis te bezoeken op elke derde zondag van de maand van 12:00 tot 16:00 uur. <br /> <br /> <h2> Locatie</h2> Het museum is gevestigd in een oude fabriekshal, die volledig is gerenoveerd. Drie belangrijke periodes komen hierbij aan de orde: de houtbouw, de bouw van ijzeren schepen met gebruik van klinknagels en de ijzerbouw volgens 20e-eeuwse technieken, met behulp van elektrisch lassen. Een belangrijk onderdeel van de expositie is de collectie historische Hollandse scheepsmotoren. <br /> <br /> <h2> Terug in de tijd</h2> Pronkstuk is de legendarische Kromhout 12 pk petroleummotor. Ook is een smederij ingericht, zoals honderd jaar geleden. Op aanvraag wordt hier het smidsvuur opgestookt en vinden demonstraties plaats van smeden en klinken, volgens de oude technieken. Het museum bevindt zich op het terrein van werf `t Kromhout, &eacute:&eacute:n van de oudste werven van Amsterdam. Al sinds de 17e eeuw vinden hier scheepsnieuwbouw en -reparatie plaats. Rond 1890 werd de werf overdekt met twee karakteristieke vloeiijzeren kappen, die als industrieel monument behouden bleven. Ook is een oude stoomlier te zien. <br /> <br /> <h2>Doel</h2> De doelstelling van de vereniging is het in stand houden van het cultureel en industrieel erfgoed. Het museum wordt geheel gerund door vrijwilligers.<br /> <br /> De museumhal kan worden gehuurd voor evenementen.", "Ma: gesloten di: 10:00 - 15:00 uur wo-zo: gesloten.", "Museum 't Kromhout (?t Kromhout Shipyard)", "Museum 't Kromhout (The Kromhout Shipyard) is located on the Hoogte Kadijk in Amsterdam. It?s one of the few shipyards of cultural and historical importance that is still operating ? a unique combination of industrial monument, operating shipyard and museum.", "<h2>Living museum</h2> <br /> Established in 1757, 't Kromhout Shipyard is a living museum that still has an active repair yard. The shipyard&rsquo:s main activities are restoring and repairing of all kinds of floating vessels, both modern and historical models. Ships still lie on the shipway.<br /> <br /> <h2>Museum</h2> <br /> The museum&rsquo:s exhibition explains shipbuilding and the development of naval propulsion. Here you will find the oldest remaining steam winch as well as ancient shipbuilding tools and steam engines. The museum also has a fine collection of internal combustion engines.", "Mo : Closed Tu : 10:00 - 15:00 hour We -Su: Closed.", "", "2.1.6", "", "AMSTERDAM", "Kruithuisstraat 25", "1018 WJ", "52,3679990", "4,9204550", "http://www.stadsherstel.nl", "https://media.iamsterdam.com/ndtrc/Images/20101207/67a31759-75c4-4aac-8f7a-408b650cd562.jpg,https://media.iamsterdam.com/ndtrc/Images/20101207/ebd948ea-2140-434e-9289-44c20131ef8b.jpg,https://media.iamsterdam.com/ndtrc/Images/20101207/5b56dfae-a8bd-40a9-898e-aa68c5a55d47.jpg,https://media.iamsterdam.com/ndtrc/Images/20101207/9a04fd14-e8f1-43c8-a5a0-7d37cfdc7638.jpg,https://media.iamsterdam.com/ndtrc/Images/20101207/3a7b1d4b-5ddd-448c-b7c5-444a635e5c03.jpg,https://media.iamsterdam.com/ndtrc/Images/20101207/f92604c7-2b14-4bcb-9df4-ede5a6aa78e4.jpg,https://media.iamsterdam.com/ndtrc/Images/20101207/69d913e8-3709-4046-a36e-4633485ba097.jpg,https://media.iamsterdam.com/ndtrc/Images/20101207/68cce3d5-ab68-4594-a7b4-3871a490d0d9.jpg", "https://media.iamsterdam.com/ndtrc/Images/20101207/67a31759-75c4-4aac-8f7a-408b650cd562.jpg", "07-12-2010", "", "", "", "2016-10-12 10:45:52"}));
            quests.add(new Quest(new String[]{"dd19da01-45a7-470a-b0df-a2feef783f77", "Anne Frank Huis", "Ruim twee jaar zat Anne Frank met haar familie ondergedoken in het achterhuis van het pand aan de Prinsengracht 263, waar Anne?s vader, Otto Frank, zijn bedrijf had. Ook de familie Van Pels en Fritz Pfeffer hadden hier hun schuilplek.", "<h2>Ondergedoken</h2> De toegang tot <strong>het achterhuis</strong> was verborgen achter een draaikast die speciaal voor dit doel was gemaakt. Het kantoorpersoneel hielp de onderduikers met eten en bracht 'nieuws van buiten'. Op 4 augustus 1944 werd de schuilplaats verraden en de onderduikers werden gedeporteerd naar verschillende concentratie kampen. Alleen Otto Frank overleefde de oorlog.<br /> <br /> <h2>Beleef de tijd van Anne Frank</h2> De kamers in het <strong>Anne Frank Huis</strong>, alhoewel er geen meubels meer in staan, ademen nog steeds de sfeer uit de onderduikperiode. Citaten uit het dagboek, foto's, filmbeelden en originele voorwerpen van de onderduikers en de helpers illustreren de gebeurtenissen die zich hier hebben afgespeeld. Het geruite dagboek en andere originele geschriften van Anne Frank zijn hier te zien. <br /> <br /> <h2>Niet geschikt voor mindervaliden</h2> Het voor- en achterhuis zijn beide slecht toegankelijk voor mindervaliden. De 'Hollandse beenbreektrap', zoals Anne Frank het in haar <strong>dagboek</strong> omschreef, en de indeling van het pand zijn hiervan de oorzaak.<br /> <br /> <h2> Kaartverkoop en toegang tot het museum vanaf 1 mei 2016</h2> Om meer mensen de gelegenheid te geven het museum te bezoeken zonder in een rij te hoeven staan, gaat het <strong>Anne Frank Huis vanaf 1 mei 2016</strong> over op een nieuw entreesysteem. <br /> <strong>Van 9.00 tot 15.30</strong> uur is het Anne Frank Huis <strong>alleen toegankelijk voor bezoekers met een online kaartje met tijdslot.</strong> Deze online kaartjes zijn te koop via <a target=&quot:_blank&quot: href=&quot:&ldquo:www.annefrank.org&rdquo:&quot: re_target=&quot:_blank&quot:>onze website.&nbsp:</a><br /> Van <strong>15:30 uur tot sluitingstijd </strong>is het museum toegankelijk voor&nbsp:<span style=&quot:font-size: 11pt: line-height: 105%: font-family: calibri, sans-serif:&quot:>bezoekers die een <strong>kaartje bij de ingang </strong>van het museum kopen.<br /> <br /> <h2>Tip voor entree-kaartjes</h2> Tijdens de schoolvakanties is het vaak erg druk en kan het zijn dat je in de rij moet wachten. Je kunt dit vermijden door een kaartje te kopen via www.annefrank.org.<br /> <br /> <h2>Openingstijden</h2> Kijk voor de actuele openingstijden op de website. Gesloten op 12 oktober 2016 wegens Jom Kipoer (Grote Verzoendag).<br /> <br /> Het Anne Frank huis behoort tot de <a href=&quot:http://www.iamsterdam.com/nl/uit-in-amsterdam/zien-en-doen/musea-en-galeries/top-12-grote-amsterdamse-musea&quot: target=&quot:blank&quot: re_target=&quot:blank&quot:>grootste musea</a> van Amsterdam.</span>", "", "Anne Frank House", "Anne Frank is one of Amsterdam?s most well known former residents. The Anne Frank House at Prinsengracht 263 in Amsterdam is where she lived in hiding with her family for more than two years during World War II. Now converted into a museum it contains a sobering exhibition about the persecution of the Jews during the war, as well as discrimination in general.", "<h2>In hiding</h2> The doorway to the annex was concealed behind a specially constructed moveable bookcase. On 4 August 1944, their hiding place was betrayed. The people in hiding were deported to various concentration camps. Only Otto Frank survived the war. Anne&rsquo:s diary from the period was published and became a bestseller worldwide.<br /> <br /> <h2>Exhibition of Anne Frank</h2> The rooms at the Anne Frank House still portray the atmosphere of the period spent in hiding. Historical documents, photographs, film images and original objects that belonged to those in hiding and those who assisted them help illustrate the events that took place. Anne&rsquo:s original diary and other notebooks are also on display in the <strong>museum</strong>.<br /> <br /> <h2>Anne&rsquo:s Amsterdam</h2> The Anne Frank House has also released a mobile app that encourages you to take to the streets and learn about wartime Amsterdam, Anne&rsquo:s childhood and her friends and family.<br /> <br /> <a href=&quot:http://www.iamsterdam.com/en/visiting/what-to-do/museums-and-galleries&quot:>More museums</a>", "Explanation: The museum will be closed October 12th due to Jom Kippoer.", "", "2.1.6", "", "AMSTERDAM", "Prinsengracht 267", "1016 GV", "52,3752100", "4,8839060", "http://www.annefrank.org,http://www.facebook.com/annefrankhouse", "https://media.iamsterdam.com/ndtrc/Images/20101028/e8955076-58ec-467f-ac29-58905a01bfa4.jpg,https://media.iamsterdam.com/ndtrc/Images/20101028/c669e3c0-0125-4d36-b931-709a9669a254.jpg,https://media.iamsterdam.com/ndtrc/Images/20101028/776d38a9-1507-426c-9ed4-77748f5ee3ac.jpg,https://media.iamsterdam.com/ndtrc/Images/20101028/f6e781fc-6d47-4a90-bc9b-ad022a41b240.jpg", "https://media.iamsterdam.com/ndtrc/Images/20101028/e8955076-58ec-467f-ac29-58905a01bfa4.jpg", "", "", "", "", "2016-09-21 14:47:24"}));
        } else {
            quests.add(new Quest(new String[]{"1eed12ca-9775-4cfc-a16f-fcf2b652a5da", "Club OT301", "Voormalige filmacademie en voormalig kraakpand dat nu een multi-media alternatief cultureel centrum huisvest. Met ruimtes voor muziek en film, een non-profit drukkerij, ateliers, het veganistische restaurant De Peper. OT301 ontving de Amsterdamprijs voor de Kunsten van het Amsterdams Fonds voor de Kunst in 2007 van toenmalig Burgemeester Job Cohen.", "", "", "OT301", "Former squat turned multi-media centre and vegan restaurant on the Overtoom.", "<h2>Creative space and vegan dining</h2> This non-profit has been feeding Amsterdam&rsquo:s alternative art scene with great projects since 1999, back when squatting buildings was the thing to do for young emerging artists. Since then they&rsquo:ve grown and gone &lsquo:legit&rsquo:, but without losing their spirit for creative adventures, and the space offers a programme that&rsquo:s not only rich in concerts, dance and every other kind of performances under the sun, but also art exhibits, workshops and film screenings. They also host a small bookshop and a vegan kitchen, and they hire out studio space to artists, lecturers and musicians. In brief, it&rsquo:s a fabulous, independent free-for-all of emerging creativity.<o:p></o:p>", "", "", "3.3.2", "Club OT301", "AMSTERDAM", "Overtoom 301", "1054 HW", "52,3601340", "4,8656040", "http://ot301.nl/", "https://media.iamsterdam.com/ndtrc/Images/20121015/8848459a-a6f3-4def-b7f0-e61e93aa1fcd.jpg,https://media.iamsterdam.com/ndtrc/Images/20121015/0e23a333-6b4e-4cdf-ad88-4a60f7e94e5b.jpg", "https://media.iamsterdam.com/ndtrc/Images/20121015/8848459a-a6f3-4def-b7f0-e61e93aa1fcd.jpg", "", "", "", "", "2016-06-09 17:25:11"}));
            quests.add(new Quest(new String[]{"e095cc2f-1870-4d9b-9b5b-7a4a8a61b2b0", "Koffie ende Koeck", "Recht tegenover de Westergasfabriek in Amsterdam kun je bij vegan lunchroom Koffie ende Koeck genieten van 100 procent plantaardige dranken en gerechten.", "<h2>100 procent plantaardig </h2> Ben je vegan, vegetarisch of kun je niet tegen lactose? Bij Koffie ende Koeck allemaal geen probleem. Alles op het menu is 100 procent plantaardig en meestal ook biologisch. Kies voor je koffie uit soja-, amandel-, hazelnoot, of speltmelk en neem daarbij &eacute:&eacute:n van de taartjes of andere lekkernijen die eigenaresse en vegan chef Maartje voor je maakt. Naast alles op het menu, is ook het interieur verantwoord: alle gebruikte materialen zijn gerecycled.", "Ma: geopend, di-vr: 09:30 - 17:30 uur, za: 10:00 - 17:30 uur.", "Koffie ende Koeck", "This bakery and caf� is located on the Haarlemerweg, directly across from the Westergasfabriek in Amsterdam West. ", "<h2>Sustainable and organic</h2> <br /> The menu is as sustainable as possible, with everything vegetable based, vegan, kosher and as much organic and fair trade ingredients are used as possible. Apart from baked goods, other items such as sandwiches, soup and breakfast fare are also made to order.", "Mo : Open, Tu -Fr : 09:30 - 17:30 hour, Sa : 10:00 - 17:30 hour.", "", "3.3.1", "Koffie ende Koeck", "AMSTERDAM", "Haarlemmerweg 175", "1051 LA", "52,3852600", "4,8716390", "http://koffieendekoeck.nl/", "https://media.iamsterdam.com/ndtrc/Images/e0/e095cc2f-1870-4d9b-9b5b-7a4a8a61b2b0/e0b3949f-c939-45d9-a395-d6e0c6504ed6.jpg", "https://media.iamsterdam.com/ndtrc/Images/e0/e095cc2f-1870-4d9b-9b5b-7a4a8a61b2b0/e0b3949f-c939-45d9-a395-d6e0c6504ed6.jpg", "08-02-2016", "", "", "", "2016-04-08 16:15:09"}));
            quests.add(new Quest(new String[]{"74c8f401-b159-479b-b2ba-58668932cbeb", "Meatless District", "In dit geheel veganistische restaurant in Amsterdam Oud West staat geen enkel dierlijk product op het menu. Met slechts vier hoofdgerechten op de kaart is er niet enorm veel keuze, maar hierdoor kan de keuken zich naast unieke gerechten voor het diner ook focussen op ontbijt en lunch.", "", "Di-vr: 12:00 - 23:45 uur, za, zo: 09:00 - 01:00 uur.", "Meatless District", "?Newly opened on the Bilderdijkstraat in Amsterdam?s Oud-West in June 2016, Meatless District?s menu is locally sourced and completely free of animal products.", "<h2>Beautifully presented vegan breakfast, lunch and dinner </h2> <p class=&quot:MsoNormal&quot: style=&quot:text-align: justify:&quot:><span lang=&quot:EN-US&quot:>The d&eacute:cor is a knowing nod to New York&rsquo:s Meatpacking District, and the vegan dishes themselves are things of carefully assembled beauty. The menu is fairly small, with only four main courses in the evening, but that allows the kitchen to get extra creative, and to feed hungry vegans (and drooling non-vegans) delicious dishes at breakfast, lunch and dinnertime. If you don&rsquo:t have time, just enjoy some drinks or order your food to take away.</span></p>", "Tu -Fr : 12:00 - 23:45 hour, Sa , Su: 09:00 - 01:00 hour.", "", "3.1.1", "Meatless District", "AMSTERDAM", "Bilderdijkstraat 65-67hs", "1053 KM", "52,3711860", "4,8703190", "http://www.meatlessdistrict.com", "https://media.iamsterdam.com/ndtrc/Images/74/74c8f401-b159-479b-b2ba-58668932cbeb/95743a0a-9212-4de5-a619-79752455322b.jpg,https://media.iamsterdam.com/ndtrc/Images/74/74c8f401-b159-479b-b2ba-58668932cbeb/792b4339-c1d4-4405-8f09-1cfb25e1a74c.jpg", "https://media.iamsterdam.com/ndtrc/Images/74/74c8f401-b159-479b-b2ba-58668932cbeb/792b4339-c1d4-4405-8f09-1cfb25e1a74c.jpg", "02-08-2016", "", "", "", "2016-08-02 17:43:25"}));
            quests.add(new Quest(new String[]{"f634d1c0-4e27-4ed6-aa61-3c5a027937fa", "De Peper", "Verstopt in OT301 op de Overtoom, kun je bij deze 'culturele keuken' in Oud West terecht voor 100% biologische en veganistische producten. Wees voorbereid op een bijzondere ervaring: gerund door vrijwilligers en zonder bediening of menu, maar met toegankelijke gerechten en een relaxte sfeer.", "", "Di: 18:00 - 01:00 uur, do: 18:00 - 01:00 uur, vr: 08:00 - 03:00 uur, zo: 18:00 - 01:00 uur.", "De Peper", "De Peper is a not-for-profit vegan and organic caf� in Amsterdam's Oud-West neighbourhood, located in the former Netherlands Film Academy building OT301.", "<h2>Vegan organic food in an anarchist experiment </h2> <h2><o:p></o:p></h2> <p class=&quot:MsoNormal&quot: style=&quot:text-align: justify:&quot:><span lang=&quot:EN-US&quot:>Alongside OT301's large performance space, cinema and gallery, and an inspired programme of [sub]cultural activities, De Peper is a cultural kitchen run by an anarchist-style volunteer collective. Open on Tuesday, Thursday, Friday and Sunday evenings, the set menu changes daily, and the kitchen is kept suitable for kosher and halal food. If you have additional dietary needs, call early to talk to the cook. </span></p>", "Tu : 18:00 - 01:00 hour, Th : 18:00 - 01:00 hour, Fr : 08:00 - 03:00 hour, Su: 18:00 - 01:00 hour.", "", "3.3.1", "De Peper", "AMSTERDAM", "Overtoom 301", "1054 HW", "52,3601400", "4,8657070", "https://depeper.org/", "https://media.iamsterdam.com/ndtrc/Images/f6/f634d1c0-4e27-4ed6-aa61-3c5a027937fa/d50a0549-94ae-4e9b-aab2-b333bcc9ec87.jpg", "https://media.iamsterdam.com/ndtrc/Images/f6/f634d1c0-4e27-4ed6-aa61-3c5a027937fa/d50a0549-94ae-4e9b-aab2-b333bcc9ec87.jpg", "02-08-2016", "", "", "", "2016-08-02 15:43:21"}));
            quests.add(new Quest(new String[]{"f9616c17-3980-4a27-96f4-8802f8a87e2e", "DeShima", "Ben je op zoek naar macrobiotisch, dan zit je bij DeShima in De Pijp goed! De gerechten zijn veganistisch en ge�nspireerd door de Aziatische keuken: je eet ze ter plekke op, maar kunt overdag ook lunch laten thuisbezorgen.", "", "", "DeShima", "Deshima is a macrobiotic food shop and lunchroom close to De Pijp, Amsterdam. The vegan menu has an Asian twist ? keep an eye on the Facebook page for their daily dishes.", "<h2>Macrobiotic, vegan lunches and food shop </h2> <p class=&quot:MsoNormal&quot:><span lang=&quot:EN-US&quot:>The lunchroom is part of a larger operation including webshop, take-away &amp: catering service, along with cooking classes, workshops and even personal counselling.&nbsp:</span></p>", "", "", "3.1.1", "DeShima", "AMSTERDAM", "Weteringschans", "1017 RX", "52,3596910", "4,8908290", "http://deshima.eu/", "https://media.iamsterdam.com/ndtrc/Images/f9/f9616c17-3980-4a27-96f4-8802f8a87e2e/cc39c205-bab1-4584-b4e0-0a4e93fa9e30.jpg", "https://media.iamsterdam.com/ndtrc/Images/f9/f9616c17-3980-4a27-96f4-8802f8a87e2e/cc39c205-bab1-4584-b4e0-0a4e93fa9e30.jpg", "", "", "", "", "2016-08-24 14:57:37"}));
            quests.add(new Quest(new String[]{"5428d15c-a2b2-417a-8cdc-a9585be4201f", "YAY Health Store & More", "Naast een ontbijtje en lunch in het Raw Food Caf� vind je bij YAY in de Pijp in Amsterdam eerlijke producten voor een gezonde levensstijl.", "<h2>Veganistische lunch</h2> In een groot pand aan de Albert Cuypstraat zit YAY, een fijne concept store met een <em></em>raw lunchrestaurant. Alles hier is veganistisch en niets mag bij bereiding boven de 42 graden verwarmd worden. Ze serveren bijvoorbeeld een frisse salade van gemasseerde boerenkool, noedels van kelp en wortel met pittige kimchee of een Thaise curry van avocado met bloemkoolrijst. Eventueel kun je er ook nog een yogalesje aan vastplakken.", "Ma-zo: 08:00 - 18:00 uur, Gesloten op zo 1 januari 2017 Geopend op za 31 december 2016 van 08:00 - 16:00 uur.", "YAY Health Store & More", "Enjoy the raw food caf�, find inner peace in the yoga space and browse the healthy, sustainable products at YAY, in De Pijp, Amsterdam", "<h2>Vegan food and healthy relaxation</h2> YAY Health Store &amp: More is all about the joys of a healthy lifestyle. Enjoy a raw, vegan breakfast, lunch or food to go, and take your pick from the honest, sustainable products in the YAY store. Find your zen on the ecofriendly yoga mats or just hang out in the hammocks and comfy pillows with a fresh juice. YAY brings all the good stuff together in a welcoming space on the Albert Cuypstraat.", "Mo -Su: 08:00 - 18:00 hour, Closed on Su 1 January 2017 Open on Sa 31 December 2016 from 08:00 - 16:00 hour.", "", "3.1.1", "YAY Health Store & More", "AMSTERDAM", "Gerard Doustraat", "1072 VV", "52,3562180", "4,8895560", "http://yayamsterdam.nl/nl/", "https://media.iamsterdam.com/ndtrc/Images/54/5428d15c-a2b2-417a-8cdc-a9585be4201f/9be0168e-3742-460c-b6a0-823b5e63cd0a.jpg", "https://media.iamsterdam.com/ndtrc/Images/54/5428d15c-a2b2-417a-8cdc-a9585be4201f/9be0168e-3742-460c-b6a0-823b5e63cd0a.jpg", "15-02-2016", "", "", "", "2016-08-03 14:27:16"}));
            quests.add(new Quest(new String[]{"14cc43d0-8df6-477a-b79a-7d693a2d7766", "By Lima", "Voor feelgood food dat biologisch, lokaal geproduceerd �n ook nog eens heel lekker is, ben je bij By Lima aan het goede adres. Dit is een van de beste plekken in Haarlem voor koffie, koudgeperste sapjes en smoothies.", "<h2>Biologische lunch en lekkernijen</h2> <p>Als voorloper op de laatste foodtrends, is de menukaart van By Lima ge&iuml:nspireerd door populaire foodblogs. Stap binnen voor een smakelijk ontbijt (dat je de heledag door kunt bestellen), een veggie bowl, en plateaus om gezellig te delen. Het caf&eacute: serveert ook een high tea die vegan, glutenvrij en suikervrij is &ndash: ideaal om van te smikkelen zonder schuldgevoel.</p>", "Ma-za: 08:00 - 18:00 uur, zo: 08:00 - 17:00 uur.", "By Lima", "For feel-good food that?s organic, locally-sourced, and most-importantly, delicious ? look no further than By Lima. The coffee, cold-pressed juices and smoothies are among the best in Haarlem. ", "<h2>Organic lunch and treats </h2> <p>By Lima is a step ahead of the latest food trends, taking menu inspiration from popular food blogs. Stop into the airy light-filled space for the ever popular all-day breakfast, veggie bowls and &ldquo:mega-gezellig&rdquo: boards for sharing. The caf&eacute: even serve high tea that is vegan, gluten-free and refined sugar free, so you can indulge in sweet treats while feeling supremely virtuous. </p>", "Mo -Sa : 08:00 - 18:00 hour, Su: 08:00 - 17:00 hour.", "", "3.3.1", "By Lima", "HAARLEM", "Zijlstraat 65", "2011 TL", "52,3822390", "4,6337450", "http://www.by-lima.nl/", "https://media.iamsterdam.com/ndtrc/Images/14/14cc43d0-8df6-477a-b79a-7d693a2d7766/8c466a78-3c92-4a29-b196-4153f97ced15.jpg", "https://media.iamsterdam.com/ndtrc/Images/14/14cc43d0-8df6-477a-b79a-7d693a2d7766/8c466a78-3c92-4a29-b196-4153f97ced15.jpg", "21-09-2016", "", "", "", "2016-09-21 15:43:28"}));
            quests.add(new Quest(new String[]{"b0ac10e0-5d41-40a8-b38d-7c406010a2a0", "Ibis", "Fans van de typisch Ethiopische pannenkoeken en pittige stoofschotels moeten naar Amsterdam Oost, waar je volgens velen het beste Oost-Afrikaanse eetcaf� vindt.", "<h2>Traditioneel Ethiopisch</h2> De specialiteit van het huis bevat vijf traditionele gerechten (dorrowot, kifto, tibs, sigawot en doro tibs) met een selectie van bijgerechten en injera. Daarnaast heb je de keuze uit vele vegetarische gerechten, waarvan er veel glutenvrij en vegan zijn.", "Di-zo: 17:00 - 01:00 uur, Toelichting: Keuken is geopend tot 22:00 uur en de bar tot 01:00 uur.", "Eetcaf� Ibis", "Fans of Ethiopian cuisine?s sour, spongy injera pancakes, chilli-spiked stews and mushy legume-based salads should head to Amsterdam East, to what some consider the city?s best East African eetcaf�.  ", "<h2>Traditional Ethiopian cuisine</h2> The house speciality includes five traditional dishes (dorowot, kitfo, tibs, sigawot and doro tibs) with a selection of side dishes and injera. There are also a dozen vegetarian dishes, many of which are naturally vegan and gluten-free.", "Tu -Su: 17:00 - 01:00 hour.", "", "3.1.1", "Ibis", "AMSTERDAM", "Weesperzijde 43", "1091 EE", "52,3557840", "4,9073240", "", "https://media.iamsterdam.com/ndtrc/Images/b0/b0ac10e0-5d41-40a8-b38d-7c406010a2a0/1ce7891f-a5c0-48ee-bcf5-646a2d99f8c8.jpg,https://media.iamsterdam.com/ndtrc/Images/b0/b0ac10e0-5d41-40a8-b38d-7c406010a2a0/5e14af99-42f2-468a-a648-2225b925c3d6.jpg,https://media.iamsterdam.com/ndtrc/Images/b0/b0ac10e0-5d41-40a8-b38d-7c406010a2a0/94b6a3bb-8923-4277-9c18-deb6ba32bb39.jpg,https://media.iamsterdam.com/ndtrc/Images/b0/b0ac10e0-5d41-40a8-b38d-7c406010a2a0/82488bbe-a678-4a7b-9215-e482e6d9e4f7.jpg", "https://media.iamsterdam.com/ndtrc/Images/b0/b0ac10e0-5d41-40a8-b38d-7c406010a2a0/1ce7891f-a5c0-48ee-bcf5-646a2d99f8c8.jpg", "01-03-2016", "", "", "", "2016-04-19 12:48:44"}));
            quests.add(new Quest(new String[]{"cd317f01-8127-41f6-8452-76c7a3414b0c", "Hummus Bistro d&a", "De naam verraad het al: bij Hummus Bistro d&a in Amsterdam Centrum voert kikkerwtenpasta in diverse varianten de boventoon. ", "<h2>Hummus en verse pita</h2> Ga je voor hummus met kip, vlees of groenten? Het kan bij Hummus Bistro d&amp:a in de Westerstraat allemaal. Geniet daarbij van een versgebakken pitabroodje en je maaltijd is eigenlijk al compleet. Maar naast hummus staat er nog heel veel ander lekkers op de kaart en voor degenen die niet kunnen kiezen, is er ook een proeverij van diverse gerechten. Bij de huiselijk ingerichte Bistro kun je iedere ochtend van de week al terecht voor een laat ontbijt en ben je tot elf uur 's avonds welkom.", "Ma-zo: 11:00 - 23:00 uur.", "Hummus Bistro d&a", "You guessed it: this is Amsterdam's go-to place for hummus. Five different kinds are on the menu, plus lovely stuff such as shakshuka, baba ganoush, pita chips and an all-day breakfast. And although some of the hummus variants contain beef or chicken, vegetarians and vegans will be quite happy here.", "", "Mo -Su: 11:00 - 23:00 hour.", "", "3.1.1", "Hummus Bistro d&a", "AMSTERDAM", "Westerstraat 136", "1015 MP", "52,3785340", "4,8826160", "https://www.facebook.com/Hummus-bistro-da-899657320096824/", "https://media.iamsterdam.com/ndtrc/Images/cd/cd317f01-8127-41f6-8452-76c7a3414b0c/4ff1d629-c6a5-482b-9cd7-e1a8b3740519.jpg", "https://media.iamsterdam.com/ndtrc/Images/cd/cd317f01-8127-41f6-8452-76c7a3414b0c/4ff1d629-c6a5-482b-9cd7-e1a8b3740519.jpg", "25-02-2016", "", "", "", "2016-04-19 12:49:10"}));
            quests.add(new Quest(new String[]{"3e47b230-e0b0-4df5-b572-aff3aaae86ab", "Dophert", "De naam doet het al vermoeden: bij Dophert in Amsterdam eet je hartstikke gezond. Alles op de kaart is veganistisch dus 100% plantaardig, ei- en lactosevrij. Dan krijg je dus een club sandwich met gerookte tempeh en avocado, een rijstnoedelsalade met broccoli en zwarte bonensoep, de Dophert Burger met gepofte paprika en wortel of bijvoorbeeld de Dophert Kroketten met knolselderij. ", "<h2>Plantaardige cappuccino en taart</h2> Je biologische koffie of chai tea latte drink je hier met verschillende soorten plantaardige melk. Erbij bestel je uiteraard een vegan peanutbutter-brownietaart. Ben je jarig dan kun je hier ook een overheerlijke verjaardagstaart bestellen. En omdat alles zo gezond is, kun je best nog wel een extra stukje nemen&hellip:", "", "Dophert", "", "", "", "", "3.1.1", "Dophert", "AMSTERDAM", "Spaarndammerstraat", "1013 ST", "52,3889960", "4,8797270", "http://dophertcatering.nl", "https://media.iamsterdam.com/ndtrc/Images/3e/3e47b230-e0b0-4df5-b572-aff3aaae86ab/4d3ce719-e573-4785-949b-53be7231191d.jpg,https://media.iamsterdam.com/ndtrc/Images/3e/3e47b230-e0b0-4df5-b572-aff3aaae86ab/917ac95b-025e-4d49-a438-e47dbba38b25.jpg,https://media.iamsterdam.com/ndtrc/Images/3e/3e47b230-e0b0-4df5-b572-aff3aaae86ab/2e699d79-dfb8-47a0-8715-5744d3044d6a.jpg", "https://media.iamsterdam.com/ndtrc/Images/3e/3e47b230-e0b0-4df5-b572-aff3aaae86ab/4d3ce719-e573-4785-949b-53be7231191d.jpg", "", "", "", "", "2016-04-06 11:49:30"}));
        }
        Intent intent = new Intent(ChatActivity.this, QuestMapActivity.class);
        intent.putParcelableArrayListExtra("quests", (ArrayList<Parcelable>) (List<?>) quests);
        startActivityForResult(intent, REQUEST_QUEST);
    }

    /**
     * Get quests from Retrieve and Rank service. WORKS!
     * Handle incomming quests. ALMOST WORKS!
     * Then open quests in map. WORKS
     */
    public void openQuestMapActivity(String query) {

        retrieveAndRankservice.getQuests(getResources(), query, "Trcid", new ServiceCallback<List<Quest>>() {
            @Override
            public void onResponse(List<Quest> quests) {
                if (quests.isEmpty()) {
                    chatAdapter.add(new ChatMessage(false, "No quests found. Try a different subject."));
                    scrollChatListToBottom();
                } else {
                    Intent intent = new Intent(ChatActivity.this, QuestMapActivity.class);
                    intent.putParcelableArrayListExtra("quests", (ArrayList<Parcelable>) (List<?>) quests);
                    startActivityForResult(intent, REQUEST_QUEST);
                }
            }

            @Override
            public void onFailure(Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /* Get mockup response. */
    private void getConversationResponseMockup(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (message.equalsIgnoreCase("")) {
                            chatAdapter.add(new ChatMessage(false, "Hi there! I am Watson, your Quest Master. I will reward you with AmsterPoints if you complete your quest to a place in Amsterdam. What kind of place do you want to visit?"));
                        } else if (similar(message, "I want to go to a museum")) {
                            chatAdapter.add(new ChatMessage(false, "OK, what type art do you like?"));
                        } else if (similar(message, "I want to go to a restaurant")) {
                            chatAdapter.add(new ChatMessage(false, "OK, what type food do you like?"));
                        } else if (similar(message, "I like classic art")) {
                            chatAdapter.add(new ChatMessage(false, "OK, I have some suggestions for you"));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            openQuestMapActivityMockup("museum");
                                        }
                                    });
                                }
                            }).start();
                        } else if (similar(message, "I like Indian food")) {
                            chatAdapter.add(new ChatMessage(false, "OK, I have some suggestions for you"));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            openQuestMapActivityMockup("restaurant");
                                        }
                                    });
                                }
                            }).start();
                        } else if (similar(message, "Start quest:")) {
                            chatAdapter.add(new ChatMessage(false, "Good luck with your quest! Please let me know if you have arrived."));
                        } else if (similar(message, "I have arrived at ")) {
                            chatAdapter.add(new ChatMessage(false, "I am glad you made it! In order to verify your arrival I want to ask you some questions."));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatAdapter.add(new ChatMessage(false, "What color is the ceiling?"));
                                            scrollChatListToBottom();
                                        }
                                    });
                                }
                            }).start();

                        } else if (similar(message, "Blue")) {
                            chatAdapter.add(new ChatMessage(false, "That is correct!"));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatAdapter.add(new ChatMessage(false, "When was the museum established?"));
                                            scrollChatListToBottom();
                                        }
                                    });
                                }
                            }).start();
                        } else if (similar(message, "Red")) {
                            chatAdapter.add(new ChatMessage(false, "That is correct!"));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatAdapter.add(new ChatMessage(false, "When was the restaurant established?"));
                                            scrollChatListToBottom();
                                        }
                                    });
                                }
                            }).start();
                        } else if (similar(message, "In 1990")) {
                            chatAdapter.add(new ChatMessage(false, "That is correct!"));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatAdapter.add(new ChatMessage(false, "Can you upload your receipt?"));
                                            scrollChatListToBottom();
                                        }
                                    });
                                }
                            }).start();
                        } else if (similar(message, "I liked the classic art") || similar(message, "I liked the Indian food")) {
                            chatAdapter.add(new ChatMessage(false, "Thank you for your feedback."));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatAdapter.add(new ChatMessage(false, "If you want to spend the AmsterPoints click on the shop button."));
                                            scrollChatListToBottom();
                                        }
                                    });
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatAdapter.add(new ChatMessage(false, "If you want to do something else and start another quest, please hit me up again! :)"));
                                            scrollChatListToBottom();
                                        }
                                    });
                                }
                            }).start();
                        }

                        scrollChatListToBottom();
                    }
                });
            }
        }).start();
    }

    /* Get response from the watson conversation service. WORKS!.
    * Handle watson conversation response. ALMOST WORKS! */
    private void getConversationResponse(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageRequest request = new MessageRequest.Builder()
                        .inputText(message)
                        .context(conversationContext)
                        .build();
                conversationService.message(workspaceId, request).enqueue(new ServiceCallback<MessageResponse>() {
                    @Override
                    public void onResponse(final MessageResponse response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (message.isEmpty()) {
                                    conversationContext = response.getContext();
                                }
                                chatAdapter.add(new ChatMessage(false, response.getText().get(0)));
                                scrollChatListToBottom();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }).start();
    }

    private void scrollChatListToBottom() {
        chatList.post(new Runnable() {
            @Override
            public void run() {
                chatList.setSelection(chatAdapter.getCount() - 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            // Mockup handle incomming camera picture
            case CameraHelper.REQUEST_IMAGE_CAPTURE:
                chatAdapter.add(new ChatMessage(true, CameraUtils.getUri(resultCode)));
                scrollChatListToBottom();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.add(new ChatMessage(false, "Thank you. Your visit has been verified. You have received 10 AmsterPoints. Enjoy your visit."));
                                scrollChatListToBottom();
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.add(new ChatMessage(false, "Please let me know what you liked about this place and how you would rate this place on a scale from 1 to 5, with 5 being the highest score."));
                                scrollChatListToBottom();
                            }
                        });
                    }
                }).start();
                break;

            // Handle incomming camera picture
            case CameraHelper.REQUEST_IMAGE_CAPTURE + 1:
                chatAdapter.add(new ChatMessage(true, CameraUtils.getUri(resultCode)));
                scrollChatListToBottom();
                VisualUtils.test(ChatActivity.this, CameraUtils.getFile(resultCode), new ServiceCallback<VisualClassification>() {
                    @Override
                    public void onResponse(VisualClassification response) {
                        if (response.getImages().isEmpty() || response.getImages().get(0).getClassifiers().isEmpty() || response.getImages().get(0).getClassifiers().get(0).getClasses().isEmpty()) {
                            chatAdapter.add(new ChatMessage(false, "Ticket is not valid"));

                        } else {
                            final VisualClassifier.VisualClass firstClass = response.getImages().get(0).getClassifiers().get(0).getClasses().get(0);

                            if (firstClass.getScore() < 0.5) {
                                chatAdapter.add(new ChatMessage(false, "Ticket is not valid. Score: " + firstClass.getScore()));
                            } else {
                                chatAdapter.add(new ChatMessage(false, "Ticket is valid. Score: " + firstClass.getScore()));
                            }
                        }

                        scrollChatListToBottom();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
                break;

            // Handle a 'start quest' -request
            case REQUEST_QUEST:
                if (data != null) {
                    String questDetails = data.getStringExtra("quest_details");
                    getConversationResponseMockup(questDetails);
                    chatAdapter.add(new ChatMessage(true, questDetails));
                    scrollChatListToBottom();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("chat_messages", chatAdapter.getItems());
        super.onSaveInstanceState(outState);
    }
}
