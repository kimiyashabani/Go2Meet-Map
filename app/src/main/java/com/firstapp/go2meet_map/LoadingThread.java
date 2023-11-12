package com.firstapp.go2meet_map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadingThread extends Thread {
//This thread will download the source xml file and parse its information, as well as fill the dataset

    private final String XML_URL="https://datos.madrid.es/egob/catalogo/206974-0-agenda-eventos-culturales-100.xml";
    Dataset dataset;
    LoadingThread(Dataset dataset){this.dataset=dataset;}


    @Override
    public void run() {
        URL url = null;
        try {
            url = new URL(XML_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String contentType = urlConnection.getContentType();
            InputStream is = urlConnection.getInputStream();
            if ("xml".equals(contentType)) {
                XmlPullParserFactory parserFactory;
                // We check that the actual content type got from the server is the expected one
                parserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserFactory.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);
                int eventType = parser.getEventType();
                String parseString;
                String[] tokens;
                int eventCount=0;
                //Parse exactly 100 elements
                while (eventCount<100) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("Contenido")){
                                parser.nextTag();
                                if (!parser.nextText().equals("Evento")){
                                    continue;
                                }
                                //Parsing new event
                                eventCount++;
                                parser.nextTag();
                                Item item= new Item();
                                while(!parser.getName().equals("Contenido")){
                                    switch (parser.getName()){
                                        case "TITULO":
                                            item.setEventName(parser.nextText());
                                            break;
                                        case "GRATUITO":
                                            if(parser.nextText().equals("0"))item.setFree(false);
                                            else item.setFree(true);
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
                                            item.setLatitude(Long.getLong(parser.nextText()));
                                            break;
                                        case "LONGITUD":
                                            item.setLongitude(Long.getLong(parser.nextText()));
                                            break;
                                        case "TIPO":
                                            parseString=parser.nextText();
                                            tokens = parseString.split("/");
                                            item.setEndDate(tokens[-1]);
                                            dataset.addEventType(tokens[-1]);
                                            break;
                                    }
                                    parser.nextText();
                                }
                                dataset.addElement(item);
                            }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
