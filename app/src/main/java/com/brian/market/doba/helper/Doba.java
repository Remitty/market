package com.brian.market.doba.helper;

import android.util.Log;

import com.brian.market.doba.models.DobaCategory;
import com.brian.market.models.ShippingAddressModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Doba{
    public static String appKey = "20210729870182797329825792";
    public static String signType = "rsa2";
    public static String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDXPHyf3qJONvKDnvGLf1OQPFFvwUWTpbkAsQNzgEzGC+9a02j5AT/aswchChn3Sp+DA1L0UaoNyYpurJiISvbGjZpjN98osxQaNin2VOtySZFTqgtZrqMy/XFW//Hneoqy0ZT/sscn2Bup9+JHKXYKrsVJs/NpnWpjETNG/gywsPlcsiNeLW35UyNuyTvAB03KGVu0+M3E+KWzbvQDungxNZEi/fnQcIvTi0S/oiLjSgHgthhFbyqH4czC9/67RnJLmQW7hksWxepL7p3pF3Z8sr7j3gUxqVTlopOthlULY10csvWsInZDPr2YKEMH12k1VGJsMer1M2/dC/r6O+vpAgMBAAECggEAQUeqML7QkVJfCYaakgmjKZrxxChlVturmccWWeCu6F5UUULPkr5LRKSJ4/5xU6yBZrYKYemm3xmib+DPPn8mC6CFE9StTWXLEgwdfAXTh01C05I+cQvjmaJ+zrlAAEoNaSDhxQ/PEYmdElcJfTXZBHJvieQXTpAc5UEhQUXqSWhE/yAVuw+cCKwpc/nWeW59SlVACZnaqQrX34iV3yFtHsmI11ZqXuwzk3Cw93LHY8oVbz4lIpwb8IKP/QejY0t2wUl6J37yBdh+G0a+uO/eg6iiLITahXt9V/hEfK29vef/+c/vbqRLUgWpOBOdvZeOQq9AWfH2CqB9vzzit0qxgQKBgQD5yeQoVjTpVNO7vq4OlwqrX1Lmf50pK9Jjrpd0lr/diQZEEUomc+8uVZ4+qN4X8c3zLYJYzLP7LwPk/GHkWwsMolObsmyjVDx6UW4lb1F+FAgt/b6kTskmWCI0Wz17tbYv3UjHFiwg5Qtq/79ZmzhoOtU6PmLJwa8nN1Bqk2eumQKBgQDclqQ9rd0Lqh+VBVzeEL+nrJFxN8FBrKRfRo74P8t50ztFR2R+VBZ8s+zJZnp93rnztr/UiEKnfUHKQwexN9GofqAG0IH4X6F7x2Lcf+wFyJa1W6UdmdZr8ScjqmaykqMYBnOA3RWx4A/PwkT0loAfEvk2dZ6Z2+SDLItHz+sJ0QKBgGJCHNIRNzch+zg7RdTPbY8ELGQnhvcPGe/EWieutUK8CA72jiiaKlgbregQHW1l3RHvsPqla5c/Y1TMQuXFOo95u1Hf2gRVuoSTS7TGGVRzJNSSoE+Z/l5rBOl7irADRuuFLH/EJQ+7icZJXEgvWv0FR6Nlrw6WdgQDKksCOSLhAoGBAJMt07Hvom/Y96+HX3ovNKEcjAI9kC1Qkm+bdWL/tbS4EnDb/JopYsObjR6nyIzZIbsRsRaOP+LeuRnJ9YTx2GqS6hUB4+YjcGxVEYpLs9/8AUpKNnX0odNtmI68cD8vhbBPBZvyZzpCZGpblYSYMi4Ji8whWEwSInq/KsMkYA8xAoGBAMhq0/TWcAorib5zbHDJUpVuJKRX3A68hjIGh/ttVfy9tp/v5/JlkPVeYtcZm/FXXUqWFKQ3gDvVQuxoJ008/BRbwgzv4+N2QPvQcbVocdfuGvpSvX25vfHgd7tECTtMebhgjU9I+dGLzA7uLyWf2ewXTif+uyQafBMNgiZ5U5kD";
    private String base = "https://openapi.doba.com/api/";
    public Doba() {
    }

    public Call requestCategories() {
        HttpUrl.Builder urlBuilder = HttpUrl.get(base+"category/doba/list").newBuilder();
        DobaHttp http = new DobaHttp();
        return http.get(urlBuilder);
    }

    public Call requestGoods(String catId, Integer pageNumber, String keyword) {
        HttpUrl.Builder urlBuilder = HttpUrl.get(base+"goods/doba/spu/list").newBuilder();
        urlBuilder.addQueryParameter("catId", catId);
        urlBuilder.addQueryParameter("pageNumber", pageNumber+"");
        urlBuilder.addQueryParameter("pageSize", "18");
        if(!keyword.isEmpty())
            urlBuilder.addQueryParameter("keyword", keyword);
        DobaHttp http = new DobaHttp();
        return http.get(urlBuilder);
    }

    public Call requestGood(String spuId) {
        HttpUrl.Builder urlBuilder = HttpUrl.get(base+"goods/doba/spu/detail").newBuilder();
        urlBuilder.addQueryParameter("spuId", spuId);
        DobaHttp http = new DobaHttp();
        return http.get(urlBuilder);
    }

    public Call requestCountryList(String country) {
        HttpUrl.Builder urlBuilder = HttpUrl.get(base+"region/doba/country/list").newBuilder();
        urlBuilder.addQueryParameter("countryCode", country);
        DobaHttp http = new DobaHttp();
        return http.get(urlBuilder);
    }

    public Call requestShipFee(ShippingAddressModel shipAddress, JSONArray goods) {
        JSONObject object = new JSONObject();
        try {
            object.put("shipToCountry", shipAddress.getCountry());
            object.put("shipToProvince", shipAddress.getCountry());
            object.put("shipToCity", shipAddress.getCity());
            object.put("shipToZipCode", shipAddress.getPostalCode());
            object.put("goods", goods);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("est ship fee params: " + object.toString());
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), object.toString());

        DobaHttp http = new DobaHttp();
        return http.post(requestBody, "shipping/doba/cost/goods");
    }

    public Call requestImportOrder(JSONObject billingAddress, JSONArray orders) {
        JSONObject object = new JSONObject();
        try {
            object.put("billingAddress", billingAddress);
            object.put("openApiImportDSOrderList", orders);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("import order params: " + object.toString());
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), object.toString());
        DobaHttp http = new DobaHttp();
        return http.post(requestBody, "order/doba/importOrder");
    }

    public Call requestConfirmOrder(String orderId) {
        JSONObject object = new JSONObject();
        try {
            object.put("ordBusiIds", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), object.toString());
        DobaHttp http = new DobaHttp();
        return http.post(requestBody, "order/doba/signOrder");
    }

    public Call requestCancelOrder(String orderId, String reason) {
        JSONObject object = new JSONObject();
        try {
            object.put("ordBusiIds", orderId);
            object.put("closerRemark", reason);
            object.put("closeReasonType", "101");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), object.toString());
        DobaHttp http = new DobaHttp();
        return http.post(requestBody, "order/doba/closeOrder");
    }

    public Call requestSystemCardId() {
        HttpUrl.Builder urlBuilder = HttpUrl.get(base+"pay/payManage/doba/queryPaymentCardList").newBuilder();
        DobaHttp http = new DobaHttp();
        return http.get(urlBuilder);
    }

    public Call requestSystemPayment(String orderBatchId, String cardId) {
        JSONObject object = new JSONObject();
        try {
            object.put("encryptOrdBatchIds", orderBatchId);
            object.put("cardId", cardId);
            object.put("paymentMethodCode", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), object.toString());
        DobaHttp http = new DobaHttp();
        return http.post(requestBody, "pay/payManage/doba/queryPaymentCardList");
    }

    private static SSLSocketFactory getSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{getX509TrustManager()}, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    private static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
    }

    private class DobaHttp{
        private Call get(HttpUrl.Builder urlBuilder) {

            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .sslSocketFactory(getSSLSocketFactory(), getX509TrustManager())
                        .hostnameVerifier((s1, s2) -> true)
                        .build();

                long timestamp = System.currentTimeMillis();
                Request request = new Request.Builder().url(urlBuilder.build())
                        .header("appKey", Doba.appKey)
                        .header("signType", Doba.signType)
                        .header("timestamp", String.valueOf(timestamp))
                        .header("sign", RsaUtil.sign(Doba.appKey, Doba.privateKey, timestamp))
                        .build();

                return client.newCall(request);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private Call post(RequestBody requestBody, String path) {

            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .sslSocketFactory(getSSLSocketFactory(), getX509TrustManager())
                        .hostnameVerifier((s1, s2) -> true)
                        .build();

                long timestamp = System.currentTimeMillis();
                Request request = new Request.Builder().url(base + path)
                        .header("appKey", Doba.appKey)
                        .header("signType", Doba.signType)
                        .header("timestamp", String.valueOf(timestamp))
                        .header("sign", RsaUtil.sign(Doba.appKey, Doba.privateKey, timestamp))
                        .post(requestBody)
                        .build();

                return client.newCall(request);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
