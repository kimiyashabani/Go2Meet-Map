package com.firstapp.go2meet_map;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadingThread extends Thread {
//This thread will download the source xml file and parse its information, as well as fill the dataset

    private final String XML_URL="https://datos.madrid.es/egob/catalogo/206974-0-agenda-eventos-culturales-100.xml";
    Dataset dataset;
    DBHelper db;
    LoadingThread(Dataset dataset, DBHelper db){this.dataset=dataset;this.db=db;}

    @Override
    public void run(){
        if (dataset.fillDB(db)<0){
            fillDatabase();
        }
    }

    private void fillDatabase() {
        URL url;
        try {
            url = new URL(XML_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String contentType = urlConnection.getContentType();
            InputStream is = urlConnection.getInputStream();
            XmlPullParserFactory parserFactory;
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            String parseString;
            String[] tokens;
            int eventCount=0;
            //Parse exactly 100 elements
            while (eventCount<100) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equals("contenido")){
                            parser.nextTag();
                            if (!parser.nextText().equals("Evento")){
                                continue;
                            }
                            //Parsing new event
                            eventCount++;
                            parser.nextTag();
                            Item item= new Item();
                            while(parser.getName() == null || !parser.getName().equals("contenido")){
                                if (parser.getEventType() != XmlPullParser.START_TAG) {
                                    parser.next();
                                    continue;
                                }
                                //Go through the attributes of the event
                                String debug=parser.getAttributeValue(0);
                                if(debug!=null)Log.d("Loading thread: string ->", debug);
                                switch (debug){
                                    case "TITULO":
                                        item.setEventName(parser.nextText());
                                        break;
                                    case "GRATUITO":
                                        item.setFree(parser.nextText());
                                        break;
                                    case "DIAS-SEMANA":
                                        item.setWeekdays(parser.nextText());
                                        break;
                                    case "FECHA-EVENTO":
                                        parseString=parser.nextText();
                                        tokens = parseString.split(" ");
                                        item.setStartDate(tokens[0]);
                                        break;
                                    case "FECHA-FIN-EVENTO":
                                        parseString=parser.nextText();
                                        tokens = parseString.split(" ");
                                        item.setEndDate(tokens[0]);
                                        break;
                                    case "HORA-EVENTO":
                                        item.setTime(parser.nextText());
                                        break;
                                    case "CONTENT-URL":
                                        item.setUrl(parser.nextText());
                                        break;
                                    case "NOMBRE-INSTALACION":
                                        item.setPlace(parser.nextText());
                                        break;
                                    case "LATITUD":
                                        item.setLatitude(parser.nextText());
                                        break;
                                    case "LONGITUD":
                                        item.setLongitude(parser.nextText());
                                        break;
                                    case "TIPO":
                                        parseString=parser.nextText();
                                        tokens = parseString.split("/");
                                        item.setType(tokens[tokens.length-1]);
                                        dataset.addEventType(tokens[tokens.length-1]);
                                        break;
                                }
                                do {
                                    eventType = parser.next();
                                } while (eventType == XmlPullParser.TEXT && parser.isWhitespace());
                            }
                            dataset.addElement(item);
                            db.addItem(item.getStartDate().toString(),item.getEndDate().toString(),item.getWeekdays(), item.getEventName(),item.isFree(),Double.toString(item.getLatitude()),Double.toString(item.getLongitude()),item.getTime(),item.getUrl(),item.getPlace(), item.getType());
                        }
                }
                eventType=parser.next();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
