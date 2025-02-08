package com.mgsanlet.cheftube.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mgsanlet.cheftube.R;

/**
 * A fragment that provides functionality for scanning product barcodes and displaying
 * nutritional information using the Open Food Facts API. Users can scan product barcodes
 * to retrieve and display product name, Nutri-Score, and Eco-Score information.
 */
public class HealthyFragment extends Fragment {
    // -Declaring constants-
    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v3/product/";
    // -Declaring UI elements-
    private ImageButton scanButton;
    private TextView productNameTView;
    private TextView nutriscoreTView;
    private TextView ecoscoreTView;
    private Button infoBtn;


    // -Declaring string resources-
    private String scanPromptStr;
    private String productNameStr;
    private String nutriscoreStr;
    private String ecoscoreStr;
    private String productNotFoundStr;

    // -Declaring variables-
    private String currentBarcode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healthy, container, false);
        // -Initializing UI elements-
        scanButton = view.findViewById(R.id.scanButton);
        productNameTView = view.findViewById(R.id.productNameTView);
        nutriscoreTView = view.findViewById(R.id.nutriscoreTView);
        ecoscoreTView = view.findViewById(R.id.ecoscoreTView);
        infoBtn = view.findViewById(R.id.infoBtn);

        // -Initializing string resources-
        scanPromptStr = getString(R.string.scan_prompt);
        productNameStr = getString(R.string.product_name);
        nutriscoreStr = getString(R.string.nutriscore);
        ecoscoreStr = getString(R.string.ecoscore);
        productNotFoundStr = getString(R.string.product_not_found);

        scanButton.setOnClickListener(v -> startBarcodeScan());
        infoBtn.setOnClickListener(v -> openProductPage());
        return view;
    }

    /**
     * Initiates the barcode scanning process using the device's camera.
     * Configures the scanner with default settings:
     * - Uses all supported barcode formats
     * - Uses rear camera
     * - Plays beep sound on successful scan
     * - Disables barcode image saving
     */
    private void startBarcodeScan() {
        // Iniciar el escáner de ZXing
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt(scanPromptStr);
        integrator.setCameraId(0);  // Usa la cámara trasera
        integrator.setBeepEnabled(true);  // Sonido al escanear
        integrator.setBarcodeImageEnabled(false);  // No guardar imagen del código
        integrator.initiateScan();
    }

    /**
     * Handles the result from the barcode scanning activity
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult()
     * @param resultCode The integer result code returned by the child activity
     * @param data An Intent containing the result data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String barcode = "3017620422003"; // example
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                currentBarcode = result.getContents();
                fetchProductData();
            }
        }
    }


    /**
     * Fetches product data from the Open Food Facts API using the scanned barcode
     */
    private void fetchProductData() {
        String url = BASE_URL + currentBarcode;

        // Crear una instancia de RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Crear una solicitud JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Usar Gson para procesar la respuesta
                    Log.d("API_RESPONSE", response.toString());
                    processResponse(response.toString(), currentBarcode);
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        Toast.makeText(getContext(), "API: Error " + statusCode, Toast.LENGTH_LONG).show();
                    }
                });

        // Añadir la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Processes the JSON response from the API and updates the UI
     *
     * @param jsonResponse The JSON string response from the API
     */
    private void processResponse(String jsonResponse, String currentBarcode) { //TODO DOC
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
            if (currentBarcode != null && !currentBarcode.isEmpty()) {
                infoBtn.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(getContext(), productNotFoundStr, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Opens the product page on Open Food Facts website in a browser
     */
    private void openProductPage() {
        String productUrl = "https://world.openfoodfacts.org/product/" + currentBarcode;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(productUrl));
        startActivity(browserIntent);
    }

    /**
     * Data class for mapping the API response structure
     */
    public static class ProductResponse { //TODO DOC
        private Product product;

        public Product getProduct() {
            return product;
        }
    }

    /**
     * Data class representing product information from the API
     */
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