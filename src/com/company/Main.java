package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Main {
    public static class Shoe {
        String size;
        String endpoint;

        Shoe(String s, String l) {
            this.size = s;
            this.endpoint = l;
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        //TO DO:
        //-multithreading
        //-if error then try again
        //-fix speed
        //-sockets?
        List<Shoe> arrayOfShoes = new ArrayList<Shoe>();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        int timeout = 60000;
        try {
            //SET COOKIES
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

            //SET CONNECTION
            URL url = new URL("https://sklep.sizeer.com/adidas-zx-2k-boost-damskie-sneakersy-czarny-fv9993");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Connection", "close");

            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            con.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            //GET SIZES
            con.disconnect();
            Document doc = Jsoup.parse(content.toString());
            Elements sizes = doc.getElementById("js-size-pick").children();
            for (Element size: sizes) {
                if (isNumeric(size.text().split(" US")[0])) {
                    String tempSize = size.text().split(" US")[0];
                    String tempUrl = size.child(0).attr("data-carturl");
                    arrayOfShoes.add(new Shoe(tempSize, tempUrl));
                }
            }
            System.out.print("[TASK 1][" + formatter.format(System.currentTimeMillis()) + "] Dostepne rozmiary: ");
            for (Shoe shoe: arrayOfShoes) {
                System.out.print(shoe.size + ", ");
            }
            System.out.println();

            //CHOOSE RANDOM SIZE
            Random random = new Random();
            int randomIndex = random.nextInt(arrayOfShoes.size());
            System.out.println("[TASK 1][" + formatter.format(System.currentTimeMillis()) + "] Losowo wybrano rozmiar " + arrayOfShoes.get(randomIndex).size );
            url = new URL("https://sklep.sizeer.com" + arrayOfShoes.get(randomIndex).endpoint);

            //ADD TO BASKET
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Connection", "close");

            con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
            con.setRequestMethod("GET");
            con.connect();


            //GET TOKEN
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            doc = Jsoup.parse(content.toString());
            String cart_flow_list_step__token = doc.getElementById("cart_flow_list_step__token").val();
            System.out.println("[TASK 1][" + formatter.format(System.currentTimeMillis()) +"] Pomyslnie dodano do koszyka");


            //CHOOSE DELIVERY
            url = new URL("https://sklep.sizeer.com/koszyk/lista/zapisz?isAjax=1");

            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Connection", "close");

            con.setRequestMethod("POST");
            con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("cart_flow_list_step[_token]", cart_flow_list_step__token);
            params.put("cart_flow_list_step[transportMethod]", "43");
            params.put("cart_flow_list_step[paymentGroup]", "103");
            params.put("cart_flow_list_step[coupon]", "");
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            con.setDoOutput(true);
            con.connect();

            con.getOutputStream().write(postDataBytes);
            con.getInputStream();
            con.disconnect();
            System.out.println("[TASK 1][" + formatter.format(System.currentTimeMillis()) + "] Pomyslnie wybrano opcje dostawy");

            //GET TOKEN
            url = new URL("https://sklep.sizeer.com/koszyk/lista/zapisz");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Connection", "close");

            con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
            con.setRequestMethod("GET");
            con.connect();

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            con.disconnect();
            doc = Jsoup.parse(content.toString());
            String cart_flow_security_step__token = doc.getElementById("cart_flow_security_step__token").val();
            System.out.println("[TASK 1][" + formatter.format(System.currentTimeMillis()) + "] Pomyslnie zapisano koszyk");

            //GET READY TO FILL DATA
            url = new URL("https://sklep.sizeer.com/koszyk/konto/zapisz");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Connection", "close");

            con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
            con.setRequestMethod("POST");
            params = new HashMap<>();
            params.put("cart_flow_security_step[doRegistration]", "0");
            params.put("cart_flow_security_step[_token]", cart_flow_security_step__token);
            postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            postDataBytes = postData.toString().getBytes("UTF-8");
            con.setDoOutput(true);
            con.connect();
            con.getOutputStream().write(postDataBytes);
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            con.disconnect();
            doc = Jsoup.parse(content.toString());
            String cart_flow_address_step__token = doc.getElementById("cart_flow_address_step__token").val();
            System.out.println("[TASK 1][" + formatter.format(System.currentTimeMillis()) + "] Pomyslnie wybrano zakupy bez rejestracji");

            //FILL DATA
            url = new URL("https://sklep.sizeer.com/koszyk/adres/zapisz");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Connection", "close");
            con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
            con.setRequestMethod("POST");
            params = new HashMap<>();
            params.put("cart_flow_address_step[accountAddress][firstName]", "Magda");
            params.put("cart_flow_address_step[accountAddress][lastName]", "Kamil");
            params.put("cart_flow_address_step[accountAddress][email]", "fdgdgrtgre@gmail.com");
            params.put("cart_flow_address_step[accountAddress][addressType]", "person");
            params.put("cart_flow_address_step[accountAddress][company]", "");
            params.put("cart_flow_address_step[accountAddress][nip]", "");
            params.put("cart_flow_address_step[accountAddress][phone]", "661-222-555");
            params.put("cart_flow_address_step[accountAddress][street]", "ul. Popka");
            params.put("cart_flow_address_step[accountAddress][houseNumber]", "3");
            params.put("cart_flow_address_step[accountAddress][apartmentNumber]", "");
            params.put("cart_flow_address_step[accountAddress][postcode]", "12-345");
            params.put("cart_flow_address_step[accountAddress][city]", "Swinoujscie");
            params.put("cart_flow_address_step[sameTransportAddress]", "1");
            params.put("cart_flow_address_step[transportAddress][company]", "");
            params.put("cart_flow_address_step[transportAddress][firstName]", "");
            params.put("cart_flow_address_step[transportAddress][lastName]", "");
            params.put("cart_flow_address_step[transportAddress][phone]", "");
            params.put("cart_flow_address_step[transportAddress][street]", "");
            params.put("cart_flow_address_step[transportAddress][houseNumber]", "");
            params.put("cart_flow_address_step[transportAddress][apartmentNumber]", "");
            params.put("cart_flow_address_step[transportAddress][postcode]", "");
            params.put("cart_flow_address_step[transportAddress][city]", "");
            params.put("cart_flow_address_step[sameBillingAddress]", "1");
            params.put("cart_flow_address_step[billingAddress][firstName]", "");
            params.put("cart_flow_address_step[billingAddress][lastName]", "");
            params.put("cart_flow_address_step[billingAddress][addressType]", "person");
            params.put("cart_flow_address_step[billingAddress][company]", "");
            params.put("cart_flow_address_step[billingAddress][nip]", "");
            params.put("cart_flow_address_step[billingAddress][phone]", "");
            params.put("cart_flow_address_step[billingAddress][street]", "");
            params.put("cart_flow_address_step[billingAddress][houseNumber]", "");
            params.put("cart_flow_address_step[billingAddress][apartmentNumber]", "");
            params.put("cart_flow_address_step[billingAddress][postcode]", "");
            params.put("cart_flow_address_step[billingAddress][city]", "");
            params.put("cart_flow_address_step[consentForm][consent_1925][]", "1925");
            params.put("cart_flow_address_step[consentForm][consent_1760][]", "1760");
            params.put("cart_flow_address_step[consentForm][consent_1778][]", "1778");
            params.put("cart_flow_address_step[transportAddress][addressType]", "person");
            params.put("cart_flow_address_step[customerComment]", "");
            params.put("cart_flow_address_step[_token]", cart_flow_address_step__token);

            postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            postDataBytes = postData.toString().getBytes("UTF-8");
            con.setDoOutput(true);
            con.connect();
            con.getOutputStream().write(postDataBytes);
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            doc = Jsoup.parse(content.toString());
            String cart_flow_summation_step__token = doc.getElementById("cart_flow_summation_step__token").val();
            System.out.println("[TASK 1][" + formatter.format(System.currentTimeMillis()) + "] Pomyslnie zapisano dane do wysylki");

            //SUMMARY AND PAYMENT
            url = new URL("https://sklep.sizeer.com/koszyk/podsumowanie/zapisz");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Connection", "close");

            con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
            con.setRequestMethod("POST");
            params = new HashMap<>();
            params.put("cart_flow_summation_step[_token]", cart_flow_summation_step__token);
            postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            postDataBytes = postData.toString().getBytes("UTF-8");
            con.setDoOutput(true);
            con.connect();
            con.getOutputStream().write(postDataBytes);
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            con.disconnect();

            doc = Jsoup.parse(content.toString());
            String orderId = doc.getElementsByClass("m-typo m-typo_primary").text();
            System.out.println("[TASK 1][" + formatter.format(System.currentTimeMillis()) + "] " + orderId);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("blad");
        }
    }

    private static boolean isNumeric(String s) {
        try {
            s = s.replace(',', '.');
            Float.parseFloat(s);
            return true;
        }
        catch (Exception ex) {
            if (s.contains("/"))
                return true;
            else {
                return false;
            }
        }
    }
}
