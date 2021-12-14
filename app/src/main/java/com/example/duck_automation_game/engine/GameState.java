package com.example.duck_automation_game.engine;

import android.util.Log;

import com.example.duck_automation_game.MainActivity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GameState {
    GameMap map;
    public Map<String, Double> playerResource;
    public Map<String, Double> playerProduction;
    Resource[] resourcesList;


    public GameState() {
        this.playerResource = new HashMap<>();
        this.playerProduction = new HashMap<>();

        //TODO: load the catalog from a file
        String[] resourcesNames = new String[]{"iron", "watermelons", "ducks", "pancakes", "wood", "spice melange","rightful vengeance","resource1","resource2","resource3"};

        this.resourcesList = new Resource[resourcesNames.length];
        for (int i = 0; i < resourcesList.length; i++) {
            // Create the resource and add it to the catalog
            this.resourcesList[i] = new Resource(resourcesNames[i]);

            // set this same resource in the player starting resources
            this.playerResource.put(this.resourcesList[i].name, 0.0);
            this.playerProduction.put(this.resourcesList[i].name, 0.0);
        }


        // TODO: load the mapSize from a config file and pass it to the GameMap constructor
        this.map = new GameMap(20, resourcesList);

    }



    /**
     * update the game state per seconds
     *
     * @param timeOffset in seconds
     */
    public void update(double timeOffset) {
        // if we had 1 w00d in player res, and we have +1.5 wood producation
        // and we have timeOffset of 3 seconds we want that playa resoursa is gonna be
        // 1 + (3*1.5)
        // do you understand, my man?! <> ==> www.burgerIsBadFOrYOuButWe<3itAnyroads.gov.zulu

        for (String key : this.playerResource.keySet()) {
            Double currentResourceValue = this.playerResource.get(key);
            Double currentProdcution = this.playerProduction.get(key);

            Double newResourceAmount=currentResourceValue +(currentProdcution*timeOffset);
            this.playerResource.put(key,newResourceAmount);
            //Log.d("GAD", "update: "+playerResource.get(key));
            //notifyAdapter();

        }
    }
//    private void func() {
////        new BufferedInputStream(getResources().openRawResource("game_res"));
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        try {
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document= builder.parse(new File("game_resources.xml"));
//
//            //normalize the structure
//            document.getDocumentElement().normalize();
//            //put the XML into a nodelist
//            NodeList resourceNodeList = document.getElementsByTagName("resource");
//
//            for (int i = 0; i < resourceNodeList.getLength(); i++) {
//                Node resource = resourceNodeList.item(i);
//                if (true){
//                    //element is now a single resource class
//                    Element resourceElement = (Element) resource;
//
//                    Log.d("GAD", "name: "+resourceElement.getAttribute("name"));
//                    NodeList resourceChildList= resource.getChildNodes();
//
//                    Log.d("GAD", "nmae: "+((Element)(resourceChildList.item(0))).getTagName()+"WAA"+((Element)(resourceChildList.item(0))).getTextContent());
//
//                }
//                Element resourceElement = (Element) resource;
//                Log.d("GAD", "name: "+resourceElement.getAttribute("name"));
//
//
//            }
//
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("GAD", "func:3!!!!!!!!!! ");
//        }
//
//    }



    public Map<String, Double> getPlayerResource() {
        return this.playerResource;
    }

    public Map<String, Double> getPlayerProduction() {
        return this.playerProduction;
    }

    public Resource[] getResourceList() {

        return resourcesList;
    }


}
