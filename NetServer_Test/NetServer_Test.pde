//der er nærmest ingen elemter af det tilbage hvis nogle overhovedet. men det er baseret på Hans' eksempelprogram
//derudover har jeg ingen kode lånt til serveren.


import processing.net.*;

Server server;

String incomingMessage; //her reserveres en plads til strings, hvor beskederne som kører gennem serveren går igennem
String bruger1;  //her reserveres en plads til strings, og her kan gemmes en ip.
String bruger2;  //her reserveres en plads til strings, her kan gemmes endnu en ip.




void setup() {
  size(400, 200);
  // serveren startes med porten 5204
  server = new Server(this, 5204);
}


void draw() {
  // tjekker alle forbundet klienter efter data i bufferen, og hvis den finder en, gemmes den klient i 'client'  (server.available og client.available gør ikke det samme)
  Client client = server.available();

  // udfør kun kodeblokken hvis der er data i bufferen/en forbindelse.
  if (client != null) {
    // funktionen readString(), returnere data i bufferen som string
    incomingMessage = client.readString();
    server.write(incomingMessage);
    print(incomingMessage);
  }
}


//---------------------unødvendig kode blok til test--------------------------------//
void mousePressed() {                                                               //
  println(bruger1, " ", bruger2, " "); //debugging                                  //
}                                                                                   //
//----------------------------------------------------------------------------------//


void serverEvent(Server server, Client client) {
  if (bruger1==null) { //hvis der ikke er gemt en ip i bruger1 så gem dets ip på denne plads
    bruger1=client.ip();
  } else if (bruger2==null) { //hvis der ikke er gemt en ip i bruger2 så gem dets ip på denne plads
    bruger2=client.ip();
  } else {
    server.disconnect(client); // hvis der allerede er to klienter forbundet, så disconnecter serveren den nye klient
  }
}

void disconnectEvent(Client disconnect) {  //når en klient disconnecter udføres kodeblokken
  println((disconnect.ip()).length(), " ", bruger1.length());
  //her sammenlignes ip'en på den disconnectede klient, med de to gemte ip'er for at gøre plads til ny forbindelse
  //her er det nødvendigt at bruge funktionen "string.equals(string2)" for at at teste om to strings er ens, 
  //fordi at man ikke kan sammenligne to strings med '=='
  if (disconnect.ip().equals(bruger1)) {
    bruger1 = null;
  } else if (disconnect.ip().equals(bruger2)) {
    bruger2 = null;
  }
}
