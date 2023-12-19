import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class LayoutTest extends PApplet {




//---------------------------------------------VARIABLER-----------------------------------------------

//variablerne reservere plads i hukommelsen til disse værdier når programmet starter. og dem der er lig med noget, eller er arrays, placere også værdier i de variablers plads i hukommelsen.
//------Layout variabler-----
PImage texts;
PImage p;
PImage p1;
PImage Login;
PImage LoginBruger1;
PImage LoginBruger2;
PFont b;
int t = 17;

//------Tilstande variabler-----
Boolean login = false;
Boolean select = false;
boolean connected = false;
char letter;
int antalbeskeder = 0;
Client c;

int[] numbers = {660, 600, 540, 480, 420, 360, 300, 240, 180, 120};
String[] beskeder = new String[10];
boolean[] farve = new boolean[10];
String incomingMessage = " ";
String tekstfelt = "";
int port1 = 5204;
int navn=0;


//---------------------------------------------SETUP-----------------------------------------------
public void setup() {
  
  //indlæser nødvendige billeder  of fonts i hukommelsen på deres pladser.
  b = loadFont("Calibri-48.vlw");
  texts = loadImage("Layout.png");
  Login = loadImage("Login.png");
  p = loadImage("Besked.png");
  p1 = loadImage("Besked2.png");
  LoginBruger1 = loadImage("LoginBruger1.png");
  LoginBruger2 = loadImage("LoginBruger2.png");

  //processing forsøger at forbinde sig til den angivet server adresse, gennem den angivet port. derudover gemmer den det i hukommelsen, på variablet c's plads
  c = new Client(this, "localhost", 5204);

  //besked funktionen tæller manuelt hvor mange beskeder der er tomme og tæller dem, derfor skal funktionen køres en gang, men den vises ikke på skærmen.
  besked("den første besked er nødvendig for opstart, men bliver ikke vist", false);
}


//---------------------------------------------DRAW-----------------------------------------------
public void draw() {
  //tilstandende er her simplificeret, og gemt i funktioner   (login=/=login(), en er boolean den anden er funktionen
  if (!login) // hvis du ikke er logget ind -> login scene
  {
    login();
  } else     //ellers -> texts scene.  så må du være logget ind.
  {
    texts();
  }
}



//---------------------------------------------KEYPRESSED-----------------------------------------------
public void keyPressed() {
  //  lånt fra processing.org/examples/charactersstrings.html
  //gendkender alle ascii tegn mellem '!' og 'z', plus mellemrum
  if (login) {
    if ((key >= '!' && key <= 'z'|| key == ' '||key >= 128 && key <= 255)&&login&&textWidth(tekstfelt)<240  ) {
      tekstfelt = tekstfelt + key;  //tilføjer bogstav/tallet til beskeden du skriver.
      // lånt kode slutter
    } else if (key==BACKSPACE) {  //   opsummering: delete key
      try {
        // .substring kopiere tekstfelt, og fjern karaktere fra begyndelse og/eller slutningen
        // .length returnere mængden af karaktere i dets string som int. vi minuser med 1, for at slette et tegn
        tekstfelt = tekstfelt.substring(0, tekstfelt.length()-1);
      }
      catch(StringIndexOutOfBoundsException e) {  // hvis det ville give den error message out of bounds
        tekstfelt = "";
      }
    } else if (key==ENTER&&tekstfelt!="") {
      c.write(tekstfelt+navn);
      tekstfelt="";
    }
  }
}

//---------------------------------------------MOUSEPRESSED-----------------------------------------------
public void mousePressed() {
  print(navn);
  if (!login) {  // bruger 1 eller 2
    if (mouseX>66&&mouseX<(66+268)&&mouseY>294&&mouseY<(294+47)) {
      login=true;
      navn=2;
    } else if (mouseX>66&&mouseX<(66+268)&&mouseY>376&&mouseY<(376+47)) {
      login=true;
      navn=1;
    }
  } else if (mouseX>350-(55/2)&&mouseX<350+(55/2)) {  // tekstfelt   //mangler y
    c.write(tekstfelt+navn);
    tekstfelt="";
  } //else if(){  // send besked knap
}




//---------------------------------------------BESKED FUNKTION-----------------------------------------------
public void besked(String besked, boolean f) {
  antalbeskeder=0;

  for (int i = 0; i<10; i++) {  //
    if (beskeder[i]==null) {
      antalbeskeder+=1;
    }
  }
  arrayCopy(splice(beskeder, besked, 0), beskeder, 10);  // først kopieres beskeder[] og den nye besked bliver tilsat, derefter kopiere arraycopy uden den ældste besked, så der igen kun er 10 elementer
  arrayCopy(splice(farve, f, 0), farve, 10);
  imageMode(CORNER);
}


//---------------------------------------------LOGIN TILSTAND-----------------------------------------------
public void login() {

  if (mouseX>66&&mouseX<(66+268)&&mouseY>294&&mouseY<(294+47)) {
    image(LoginBruger1, 0, 0);
  } else if (mouseX>66&&mouseX<(66+268)&&mouseY>376&&mouseY<(376+47)) {
    image(LoginBruger2, 0, 0);
  } else {
    image(Login, 0, 0);
  }
}
//---------------------------------------------TEXTS TILSTAND-----------------------------------------------
public void texts() {
  //baggrund
  image(texts, 0, 0);
  fill(0);
  textSize(25);
  textAlign(CENTER);
  text("Bruger"+" "+navn, width/2, 35);
  textAlign(CORNER);
  textSize(t);
  text(tekstfelt, 40, 717+(55/2)+(t/4));

  for (int i = 0; i<10-antalbeskeder; i++) {

    imageMode(CENTER);
    if (farve[i]==false) {
      image(p, width/2, numbers[i]-17);
    } else if (farve[i]==true) {
      image(p1, width/2, numbers[i]-17);
    }
    textAlign(CORNER);
    textSize(15);
    text(beskeder[i], 75, numbers[i]-15);
  }

  //--------------------------------------------Tjekker hvem beskeden kommer fra--------------------------------------

  if (c.available() > 0) {  //.available() fortæller os hvor mange bytes der er i bufferen
    //hvis der er noget i bufferen så læser vi bufferen som en string, og gemmer den.
    incomingMessage = c.readString();
    if (incomingMessage.charAt(incomingMessage.length()-1)=='1') {  //beskeden er fra bruger 1
      incomingMessage = incomingMessage.substring(0, incomingMessage.length()-1); //fjerner brugertallet fra beskeden
      println(incomingMessage);
      if (navn==1) {
        besked(incomingMessage, false);
      } else {
        besked(incomingMessage, true);
      }
    } else if (incomingMessage.charAt(incomingMessage.length()-1)=='2') { //beskeden er fra bruger 2
      incomingMessage = incomingMessage.substring(0, incomingMessage.length()-1); //fjerner brugertallet fra beskeden
      println(incomingMessage);
      if (navn==2) {
        besked(incomingMessage, false);
      } else {
        besked(incomingMessage, true);
      }
    }
    //-------------------------------------------------------------------------------------------------------------------
  }
  imageMode(CORNER);
}
  public void settings() {  size(400, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "LayoutTest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
