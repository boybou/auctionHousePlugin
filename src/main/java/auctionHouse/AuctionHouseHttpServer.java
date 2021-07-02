package auctionHouse;

import auctionHouse.entity.Auction;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.inventory.Inventory;
import org.json.JSONObject;
import sun.misc.IOUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;


public class AuctionHouseHttpServer {

    final int portNumber = 3000;

    private HttpServer httpServer;

    public void start() throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(portNumber),0);
        this.httpServer.createContext("/api/auctions/fill", this::handleAuctionsFillEndpoint);
        this.httpServer.createContext("/api/auctions", this::handleAuctionsEndpoint);
        this.httpServer.createContext("/api/storage",this::handleStorageEndpoint);

        this.httpServer.setExecutor(null);
        this.httpServer.start();


    }


    private void handleStorageEndpoint(HttpExchange httpExchange){
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                getStorage(httpExchange);
                break;
            case "OPTIONS":
                answerOptions(httpExchange);
                break;
        }
    }
    private void handleAuctionsFillEndpoint(HttpExchange httpExchange){
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
        switch (httpExchange.getRequestMethod()) {
            case "POST":
                fillAuction(httpExchange);
                break;
            case "OPTIONS":
                answerOptions(httpExchange);
                break;
        }
    }

    private void deleteAuction(HttpExchange httpExchange){
        String lastPart = this.getLastPartFromUri(httpExchange.getRequestURI());
        if(!lastPart.equals("auctions")){
            UUID auctionUuid = UUID.fromString(lastPart);
            InstanceCollection.auctionService.removeAuction(auctionUuid);
            try {
                this.sendResponseHeadersAndClose(200,httpExchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                this.sendResponseHeadersAndClose(500, httpExchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillAuction(HttpExchange httpExchange){

        InputStream inputStream = httpExchange.getRequestBody();

        String inputString = this.inputStreamToString(inputStream);
        JSONObject jsonObject = new JSONObject(inputString);
        JSONObject fillOrderJson = jsonObject.getJSONObject("fillOrder");
        UUID auctionUuid = UUID.fromString(fillOrderJson.getString("auctionUuid"));
        UUID payingStorageUid = UUID.fromString(fillOrderJson.getString("payingStorageUuid"));

        JSONObject result = InstanceCollection.auctionService.fillAuction(auctionUuid,payingStorageUid);

        try {
            if(result.getBoolean("swapped")) {
                InstanceCollection.auctionService.removeAuction(auctionUuid);
                this.sendJsonAsResponse(httpExchange,result);
            }else{
                this.sendJsonAsErrorResponse(httpExchange,result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void handleAuctionsEndpoint(HttpExchange httpExchange)  {

        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
        switch (httpExchange.getRequestMethod()){
            case "GET":
                getAuctions(httpExchange);
                break;
            case "POST":
                postAuction(httpExchange);
                break;
            case "OPTIONS":
                answerOptions(httpExchange);
                break;
            case "DELETE":
                deleteAuction(httpExchange);
                break;
        }
    }

    private void answerOptions(HttpExchange httpExchange){
        try {
            this.sendResponseHeadersAndClose(200,httpExchange);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getStorage(HttpExchange httpExchange){
        String lastPart = getLastPartFromUri(httpExchange.getRequestURI());
        if(!lastPart.equals("storage")){
            if(lastPart.length() == 6){
                JSONObject inventoryJson = InstanceCollection.storageService.getInventoryJsonFromShortUuid(lastPart);

                try {
                    sendJsonAsResponse(httpExchange,inventoryJson);

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            else{
                JSONObject inventoryJson = InstanceCollection.storageService.getInventoryJson(lastPart);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("inventory",inventoryJson);
                try{
                    sendJsonAsResponse(httpExchange,jsonObject);

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }



        }
    }

    private void getAuctions(HttpExchange httpExchange){
        String lastPart = getLastPartFromUri(httpExchange.getRequestURI());
        if(lastPart.equals("auctions")){
            try{
                ArrayList<Auction> auctions = InstanceCollection.auctionService.getAuctions();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("auctions",auctions);
                sendJsonAsResponse(httpExchange,jsonObject);

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        else{
            UUID uuid = UUID.fromString(lastPart);

            try{
                Auction auction = InstanceCollection.auctionService.getAuction(uuid);
                sendJsonAsResponse(httpExchange,new JSONObject(auction));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendJsonAsResponse(HttpExchange httpExchange,JSONObject jsonObject) throws IOException {
        String jsonString = jsonObject.toString();
        httpExchange.getResponseHeaders().set("Content-type","application/json");
        httpExchange.sendResponseHeaders(200,0);

        OutputStream output = httpExchange.getResponseBody();

        output.write(jsonString.getBytes());
        output.flush();
        httpExchange.close();
    }

    private void sendJsonAsErrorResponse(HttpExchange httpExchange,JSONObject jsonObject) throws IOException {
        String jsonString = jsonObject.toString();
        httpExchange.getResponseHeaders().set("Content-type","application/json");
        httpExchange.sendResponseHeaders(500,0);

        OutputStream output = httpExchange.getResponseBody();

        output.write(jsonString.getBytes());
        output.flush();
        httpExchange.close();
    }

    private void postAuction(HttpExchange httpExchange){
        InputStream inputStream = httpExchange.getRequestBody();
        String inputString = this.inputStreamToString(inputStream);
        JSONObject jsonObject = new JSONObject(inputString);
        JSONObject auctionJson = jsonObject.getJSONObject("auction");
        if(auctionJson.getInt("price") < 0 || auctionJson.getInt("amount") < 0){
            try {
                this.sendResponseHeadersAndClose(500,httpExchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Auction auction = new Auction(
                UUID.fromString(auctionJson.getString("storageId")),
                auctionJson.getString("enchantments"),
                auctionJson.getInt("itemHashCode"),
                auctionJson.getString("itemName"),
                auctionJson.getInt("amount"),
                auctionJson.getInt("price"));

        try{
            InstanceCollection.auctionService.postAuction(auction);
            this.sendResponseHeadersAndClose(200,httpExchange);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String inputStreamToString(InputStream inputStream){
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }

    private void sendResponseHeadersAndClose(int resCode,HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(resCode,0);
        httpExchange.close();
    }

    private String getLastPartFromUri(URI uri){
        String completeUri = uri.toString();
        String[] parts = completeUri.split("/");
        return parts[parts.length-1];
    }


}
