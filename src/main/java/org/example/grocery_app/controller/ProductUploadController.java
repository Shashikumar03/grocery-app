package org.example.grocery_app.controller;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.example.grocery_app.entities.Category;
import org.example.grocery_app.entities.Inventory;
import org.example.grocery_app.entities.Product;
import org.example.grocery_app.repository.CategoryRepository;
import org.example.grocery_app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductUploadController {

    private static final int EXPECTED_COLUMN_COUNT = 9;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Invalid file format. Please upload a .csv file.");
        }

        int processedCount = 0;
        int skippedCount = 0;
        List<String> skippedReasons = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new com.opencsv.CSVParserBuilder().withSeparator(',').withQuoteChar('"').build())
                     .build()) {

            String[] header = csvReader.readNext(); // read and validate header
            if (header == null || header.length < EXPECTED_COLUMN_COUNT) {
                return ResponseEntity.badRequest().body("Invalid or missing CSV header.");
            }

            String[] line;
            List<Product> productsToSave = new ArrayList<>();

            while ((line = csvReader.readNext()) != null) {
                if (line.length < EXPECTED_COLUMN_COUNT) {
                    skippedCount++;
                    skippedReasons.add("Skipped row (less than expected columns): " + Arrays.toString(line));
                    continue;
                }

                for (int i = 0; i < line.length; i++) {
                    line[i] = line[i].trim();
                }

                try {
                    String name = line[0];
                    String description = line[1];
                    double price = Double.parseDouble(line[2]);
                    String imageUrl = line[3];
                    boolean available = Boolean.parseBoolean(line[4]);
                    String categoryName = line[5];
                    int stockQuantity = Integer.parseInt(line[6]);
                    int reservedStock = Integer.parseInt(line[7]);
                    String unit = line[8];

                    if (price < 0 || stockQuantity < 0 || reservedStock < 0 || reservedStock > stockQuantity) {
                        skippedCount++;
                        skippedReasons.add("Invalid numeric values: " + Arrays.toString(line));
                        continue;
                    }

                    if (productRepository.findByName(name).isPresent()) {
                        skippedCount++;
                        skippedReasons.add("Skipped duplicate product: " + name);
                        continue;
                    }

                    Category category = categoryRepository.findByName(categoryName)
                            .orElseGet(() -> categoryRepository.save(new Category(null, categoryName, "Auto-created", null, unit)));

                    Product product = new Product();
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(price);
                    product.setImageUrl(imageUrl);
                    product.setAvailable(available);
                    product.setCategory(category);
                    product.setUnit(unit);

                    Inventory inventory = new Inventory();
                    inventory.setStockQuantity(stockQuantity);
                    inventory.setReservedStock(reservedStock);
                    inventory.setProduct(product);

                    product.setInventory(inventory);

                    productsToSave.add(product);
                    processedCount++;

                } catch (NumberFormatException e) {
                    skippedCount++;
                    skippedReasons.add("Number format error in row: " + Arrays.toString(line));
                } catch (Exception e) {
                    skippedCount++;
                    skippedReasons.add("Unexpected error in row: " + Arrays.toString(line));
                }
            }

            if (!productsToSave.isEmpty()) {
                productRepository.saveAll(productsToSave);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("message", "CSV processed successfully.");
            result.put("processedCount", processedCount);
            result.put("skippedCount", skippedCount);
            result.put("skippedReasons", skippedReasons);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process CSV: " + e.getMessage());
        }
    }
}
