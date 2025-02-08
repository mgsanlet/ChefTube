package com.mgsanlet.cheftube.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mgsanlet.cheftube.R;

import org.json.JSONObject;

public class HealthyFragment extends Fragment { //TODO DOC
    // -Declaring constants-
    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v3/product/";
    private static final String BASE_URL_IMG = "https://images.openfoodfacts.org/images/products/";

    // -Declaring UI elements-
    private ImageButton scanButton;
    private TextView productNameTView;
    private TextView nutriscoreTView;
    private TextView ecoscoreTView;
    private ImageView productImageView;

    // -Declaring string resources-
    private String scanPromptStr;
    private String productNameStr;
    private String nutriscoreStr;
    private String ecoscoreStr;
    private String productNotFoundStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healthy, container, false);
        // -Initializing UI elements-
        scanButton = view.findViewById(R.id.scanButton);
        productNameTView = view.findViewById(R.id.productNameTView);
        nutriscoreTView = view.findViewById(R.id.nutriscoreTView);
        ecoscoreTView = view.findViewById(R.id.ecoscoreTView);
        //productImageView = view.fi


        // -Initializing string resources-
        scanPromptStr = getString(R.string.scan_prompt);
        productNameStr = getString(R.string.product_name);
        nutriscoreStr = getString(R.string.nutriscore);
        ecoscoreStr = getString(R.string.ecoscore);
        productNotFoundStr = getString(R.string.product_not_found);

        scanButton.setOnClickListener(v -> startBarcodeScan());
        return view;
    }

    private void startBarcodeScan() { //TODO DOC
        // Iniciar el escáner de ZXing
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt(scanPromptStr);
        integrator.setCameraId(0);  // Usa la cámara trasera
        integrator.setBeepEnabled(true);  // Sonido al escanear
        integrator.setBarcodeImageEnabled(false);  // No guardar imagen del código
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //TODO DOC
        super.onActivityResult(requestCode, resultCode, data);
        //String barcode = "3017620422003"; example
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedBarcode = result.getContents();
                fetchProductData(scannedBarcode);
            }
        }
    }


    private void fetchProductData(String barcode) { //TODO DOC
        String url = BASE_URL + barcode;

        // Crear una instancia de RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Crear una solicitud JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Usar Gson para procesar la respuesta
                        Log.d("API_RESPONSE", response.toString());
                        processResponse(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            Toast.makeText(getContext(), "API: Error " + statusCode, Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Añadir la solicitud a la cola
        requestQueue.add(jsonObjectRequest);

        String imageUrl = getProductImageUrl(barcode);
        if (imageUrl != null) {
            Glide.with(getContext()).load(imageUrl).into(productImageView);
        }
    }

    private void processResponse(String jsonResponse) { //TODO DOC
        // Usar Gson para mapear el JSON a una clase
        Gson gson = new Gson();
        ProductResponse productResponse = gson.fromJson(jsonResponse, ProductResponse.class);

        if (productResponse.getProduct() != null) {
            String productName = productResponse.getProduct().getProductName();
            String nutriScore = productResponse.getProduct().getNutriscoreGrade();
            String ecoScore = productResponse.getProduct().getEcoscoreGrade();
            productNameTView.setText(productNameStr + "\n" + productName);
            nutriscoreTView.setText(nutriscoreStr + " " + nutriScore.toUpperCase());
            ecoscoreTView.setText(ecoscoreStr + " " + ecoScore.toUpperCase());

        } else {
            Toast.makeText(getContext(), productNotFoundStr, Toast.LENGTH_LONG).show();
        }
    }

    private String getProductImageUrl(String barcode) {
        if (barcode.length() < 8) {
            return null; // Evita errores si el código de barras es demasiado corto
        }

        String formattedBarcode = String.format("%s/%s/%s/%s",
                barcode.substring(0, 3),
                barcode.substring(3, 6),
                barcode.substring(6, 9),
                barcode.substring(9));

        return BASE_URL_IMG + formattedBarcode + "/front_es.126.400.jpg";
    }

    // Clases para mapear la respuesta JSON
    public static class ProductResponse { //TODO DOC
        private Product product;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
    }

    public static class Product { //TODO DOC
        private String product_name;
        private String nutriscore_grade;
        private String ecoscore_grade;

        public String getProductName() {
            return product_name;
        }

        public String getNutriscoreGrade() {
            return nutriscore_grade;
        }

        public String getEcoscoreGrade() {
            return ecoscore_grade;
        }
    }
}