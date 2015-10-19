package controllers.test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.HecticusController;
import play.libs.F;
import play.libs.Json;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by plesse on 7/15/14.
 */
public class test extends HecticusController {
    //GET         /client/:id                            controllers.test.test.getClient(id : Long)
    public static F.Promise<Result> getClient(Long id) {
        final Long idClient = id;
        F.Promise<ObjectNode> promiseOfObjectNode = F.Promise.promise(
                new F.Function0<ObjectNode>() {
                    public ObjectNode apply() {
                        return getCl(idClient);
                    }
                }
        );

        return promiseOfObjectNode.map(
                new F.Function<ObjectNode, Result>() {
                    public Result apply(ObjectNode i) {
                        return ok(i);
                    }
                }
        );
    }

    public static F.Promise<Result> getClients(Long seed, Long page) {
        final Long seedF = seed;
        final Long pageF = page;
        F.Promise<ObjectNode> promiseOfObjectNode = F.Promise.promise(
                new F.Function0<ObjectNode>() {
                    public ObjectNode apply() {
                        return getCls(seedF, pageF);
                    }
                }
        );

        return promiseOfObjectNode.map(
                new F.Function<ObjectNode, Result>() {
                    public Result apply(ObjectNode i) {
                        return ok(i);
                    }
                }
        );
    }


    private static ObjectNode getCl(Long idClient){
        try{
            Random rand = new Random();
            ObjectNode client = Json.newObject();
            client.put("idClient", idClient);
            client.put("app", 1);
//            int n = rand.nextInt(3);

            if(idClient == 11) {
                ArrayList<String> droid = new ArrayList<String>();
                droid.add("APA91bGt4KbjbD7XLX0SMd8kJPmKW63FWht0jxVhIfcWZx_qqBCBmV1oaVtow3vAiMRO0YIv3gPReoUVCET8sF_eSW2Dj7Fnu1H3YeP5AKoZ6KIjpuogeqrZQdq6TxFtk-hU9BZp8SI2BaedrDr6ymUGAw79zSdvbA");
                client.put("droid", Json.toJson(droid));
                ArrayList<String> ios = new ArrayList<String>();
                ios.add("75234cbf14d808bcb9ba0ad9b1301e29de878ff683e56f65efd21473aaf98040");
                client.put("ios", Json.toJson(ios));
            } else if(idClient == 19358) {
                ArrayList<String> droid = new ArrayList<String>();
                droid.add("APA91bHDFz4IRS5W4j0jl6mCKXcDs5BikO4hp4vGgBd39jWe4-dOE1-Yl0TDJQzc-P3atg3hIxj7MNF7vapgold34QD08TP_lPjDvLHN7vkUI5yBAVFZUVpCaOjTaiIY3AIYV94bhceS");
                client.put("droid", Json.toJson(droid));
            } else if(idClient == 8417) {
                ArrayList<String> droid = new ArrayList<String>();
                droid.add("APA91bF3UNDFJSUH4ijhnUrI8jr7jgRqM9G09ZpTS2_4U2SY0ltK5wOG-DcHEej-emlnx12LGV4i3L-4swOFzwZTPnWETdmj0NpBpegI9cp0LaWc0s3EhNDild3mcf60X8o8pErRQ3dS0mgmM4b0p4AP3XjrvmNLuw");
                droid.add("APA91bFj6sG1HYc8cGHFg3WzDXZui-0vJmoY3lEpOXJjxjVh20OyMHP63MY6Iu2IFilzckKixPYJTRzJ0p80Di_fVGBuSaMG_10jGHmy3JVH8wTPsBm4MW732ZR9vdZKwHecQaq9Au7UWvX-Rce5xGtLBE-0cd9VCA");
                client.put("droid", Json.toJson(droid));
            } else if(idClient == 13283) {
                ArrayList<String> droid = new ArrayList<String>();
                droid.add("APA91bGFqbu-nDJgir6Bpl5EiGvHHjH2-vboZi475Yo3GBnI28q1b1VX20taSwozshVMMqiTqlM9C7NWQUssUJI4fJ7mbS_o7u2RJNt2wVHZSYv87FglbTqAyl4cDZ3ZZ9pEjJhTwQ9mjJGZfx6dx8jD1XF54gGAZg");
                droid.add("APA91bGBTKQ3FtelgZjnww6qkwEiD-UF9NnCniXjo-IqaxMfkSI8Qr2ad8D9sdiC5AWtDV4u5PDd7rqSQcsi1Vkyrs5Gz_jDLWccyIa5nIwfzcSWt4RVFE1TCD8qYn1nanFlakdt9L3ApVgatrDuJb7AD8BSUtnunA");
                droid.add("APA91bFg--n7HO1Ew5X1tE_PNs6Yu-la2o4TIM_SdMYOevUHnBorPGPkmoZuA7mxed5LhmC57okvdQlkz9IGOb3YBB5XQsey49wtKJG5_z69vQgXQ_D-ZIJetXGmkhbgxGXVvfrBxWvZ");
                droid.add("APA91bEPJ3LTqfNSw6X4y75ZcO2ekIYiJ29-WFEtU1-lSfBGppxr7zddzuaopp_QmemSZO3p1Ik-DJGbcteGzI-I12O-QI8YIMOmKIxkQO7kk4RkHedVZWDbX7Cr2yTazxcDHvjh0indj28Ag9DLcm_ECQbffSqn4g");
                client.put("droid", Json.toJson(droid));
            } else if(idClient == 13291) {
                ArrayList<String> droid = new ArrayList<String>();
                droid.add("APA91bF90qLYMMKIyTzE1jodH0FJvbVunyPN9qpkZGHTcnrfTxFJ7FzBnCdbk1I3nRe7-dj63snLRrG4oKYAYecsAxhJjk0xrDImwrZlWUYziTyK2E05EZ-Y8P7v_iec7BiOgstK8Gef");
                client.put("droid", Json.toJson(droid));
            } else if(idClient == 2935) {
                ArrayList<String> droid = new ArrayList<String>();
                droid.add("APA91bHRKE8tKLtFrFgiUGQzfG5Evl6oFGVI1sR3T2L5SXkTFsGS-nz13XEpJ0BcUE5_V-IfUYcte_Ppjpn_61mhLNVFqulMBIzVKcjEVzA3enRqUbE8aILaYc8OV6yyqPxNSXfIRDjELKH2G3h7xGVuGyOqN0W3hQ");
                droid.add("APA91bH7L_qmUldRrlaDRlfSQwCLN7VLdo-tCpIFH-osghh0c8Dyo69QtZdLL2M1ZuUO9N-Wy4uLarlRd4eSFIIofd5WQPqhdtM5GscjLXuwHIARp8_kHQnhbwd7qC-KwhT3LJItscz0AO96ZcyUNpfJBs5Y8kahNw");
                droid.add("APA91bF1W1msvWA09xZh5bzZJdTeEXyzXAArSnRRfrt6i6e_R7pYiyC7nyyxcaYiHa2yqLvllnJzg397JrCYM3USPtUfrxxLQOl4Y3OOPeYEUyp_zGI5VUsVOnaw55p4sQ7_IxRzIbIbJ0XwT0JYwjjfqT5aPy5teQ");
                droid.add("APA91bE6nT-I1wUeU5WPywR6HjfAnRZbT6iAMGK8unVL-gNHIgS_tT_19hnS0x41iwtNCUQMWyoo0GPn0P9n3K0Er_NUyxESZQO-ve4ZbA88IBolURYShHi4HjovhW1Mj5VLAOTAyFbLKrxXGf2CLyvkGCfZ89oDSw");
                client.put("droid", Json.toJson(droid));
                ArrayList<String> ios = new ArrayList<String>();
                ios.add("f547e5fcc657a186b01d24c4b0bbf64fe784bc9210c53c4dd4b2dde9cea84eed");
                ios.add("70bb33bffac065b8a22be820f704f1046d471cfc905706bdae4adad479302041");
                ios.add("d3f49c64491f569b2a01eef66b944a3b41c247c27f070b77b405b21fa45f5004");
                client.put("ios", Json.toJson(ios));
            } else if(idClient == 52638) {
                ArrayList<String> droid = new ArrayList<String>();
                droid.add("APA91bEZZuesl1eJPRIl1OS4VjNnbLAieaCswgxwNi5YmwwlkjUQQVW3I_Jm4wLYKwjZ2QVokdg-N-AT5XkV8TcqZbp1XSKc7XyIpMhIFYurBZypsWoRXGF5k9SRIBOU81Ewsdezs6GJ");
                client.put("droid", Json.toJson(droid));
            } else if(idClient == 52639) {
                ArrayList<String> droid = new ArrayList<String>();
                droid.add("APA91bEit66JiLssunXf1mcig9Fdre6-5NsuoJNAEcbqqAnrG1X2JGdEHquYxilltKzJa3NkDIWiLg__SNgQ-xwrZsaJIPOtR03YSWatAnjfCuBVtTCEvpni5wHbAVAyfwDzR0WSdwI5");
                client.put("droid", Json.toJson(droid));
            } else {

            }


//            if(n > 0){
//                ArrayList<String> droid = new ArrayList<String>(n);
//                for(int i = 0; i < n; ++i){
//                    droid.add("DROID_" + n + "_" + idClient);
//                }
//                client.put("droid", Json.toJson(droid));
//            }
//            n = rand.nextInt(3);
//            if(n > 0){
//                ArrayList<String> ios = new ArrayList<String>(n);
//                for(int i = 0; i < n; ++i){
//                    ios.add("IOS_" + n + "_" + idClient);
//                }
//                client.put("ios", Json.toJson(ios));
//            }
//            n = rand.nextInt(3);
//            if(n > 0){
//                ArrayList<String> web = new ArrayList<String>(n);
//                for(int i = 0; i < n; ++i){
//                    web.add("WEB_" + n + "_" + idClient);
//                }
//                client.put("web", Json.toJson(web));
//            }
//            if(rand.nextBoolean()){
//                client.put("msisdn", rand.nextInt(5000)+1);
//            }
            ObjectNode response = Json.newObject();
            response.put("error", 0);
            response.put("description", "");
            response.put("response", client);
            return response;
        } catch (Exception ex) {
            ObjectNode response = Json.newObject();
            response.put("error", 1);
            response.put("description", ex.getMessage());
            return response;
        }
    }

    private static ObjectNode getCls(Long seed, Long page){
        try{
            Random rand = new Random();
            int n = rand.nextInt(100)+1;
            ArrayList<Long> clients = new ArrayList<Long>(n);
            for(int i = 0; i < n; ++i){
                clients.add((i+seed)*n);
            }
            ObjectNode response = Json.newObject();
            response.put("error", 0);
            response.put("description", "");
            response.put("response", Json.toJson(clients));
            return response;
        } catch (Exception ex) {
            ObjectNode response = Json.newObject();
            response.put("error", 1);
            response.put("description", ex.getMessage());
            return response;
        }
    }
}
