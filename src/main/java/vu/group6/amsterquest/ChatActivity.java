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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get views
        chatList = (ListView) findViewById(R.id.chat_list);
        cameraButton = (ImageButton) findViewById(R.id.camera_button);
        chatField = (EditText) findViewById(R.id.chat_field);
        sendButton = (Button) findViewById(R.id.send_button);

        // Conversation service
        String username = getResources().getString(R.string.quest_conversation_username);
        String password = getResources().getString(R.string.quest_conversation_password);
        workspaceId = getResources().getString(R.string.quest_conversation_workspace_id);
        conversationService = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
        conversationService.setUsernameAndPassword(username, password);
        getConversationResponse("");

        // Retrieve and Rank service
        username = getResources().getString(R.string.quest_retriever_username);
        password = getResources().getString(R.string.quest_retriever_password);
        retrieveAndRankservice = new RetrieveAndRankExt();
        retrieveAndRankservice.setUsernameAndPassword(username, password);

        // handle chat messages
        chatAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        chatList.setAdapter(chatAdapter);

        if (savedInstanceState != null) {
            for (Parcelable message : savedInstanceState.getParcelableArrayList("chat_messages")) {
                chatAdapter.addAll((ChatMessage) message);
            }
            scrollChatListToBottom();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatField.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    chatAdapter.add(new ChatMessage(true, message));
                    chatField.setText("");
                    scrollChatListToBottom();
                    getConversationResponse(message);
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CameraUtils.dispatchTakePictureIntent(ChatActivity.this);
                openQuestMapActivity("vegan");
            }
        });
    }

    public void openQuestMapActivity(String query) {
        List<Quest> quests = new ArrayList<Quest>();

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

        Intent intent = new Intent(ChatActivity.this, QuestMapActivity.class);
        intent.putParcelableArrayListExtra("quests", (ArrayList<Parcelable>) (List<?>) quests);
        startActivityForResult(intent, REQUEST_QUEST);

        /*retrieveAndRankservice.getQuests(getResources(), query, "Trcid", new ServiceCallback<List<Quest>>() {
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
        });*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("chat_messages", chatAdapter.getItems());
        super.onSaveInstanceState(outState);
    }

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
            case CameraHelper.REQUEST_IMAGE_CAPTURE:
                chatAdapter.add(new ChatMessage(true, CameraUtils.getUri(resultCode)));
                scrollChatListToBottom();
                VisualUtils.test(ChatActivity.this, CameraUtils.getFile(resultCode), new ServiceCallback<VisualClassification>() {
                    @Override
                    public void onResponse(VisualClassification response) {
                        if (response.getImages().isEmpty() || response.getImages().get(0).getClassifiers().isEmpty() || response.getImages().get(0).getClassifiers().get(0).getClasses().isEmpty()) {
                            Utils.toast(ChatActivity.this, "Ticket not valid");
                        } else {
                            final VisualClassifier.VisualClass firstClass = response.getImages().get(0).getClassifiers().get(0).getClasses().get(0);
                            Utils.toast(ChatActivity.this, "Ticket valid! " + firstClass.getName() + ", " + firstClass.getScore());
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
                break;

            case REQUEST_QUEST:
                chatAdapter.add(new ChatMessage(true, data.getStringExtra("quest_details")));
                scrollChatListToBottom();
                break;
        }
    }
}
