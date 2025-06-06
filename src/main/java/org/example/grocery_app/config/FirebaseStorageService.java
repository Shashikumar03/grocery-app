package org.example.grocery_app.service;

import com.google.firebase.cloud.StorageClient;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class FirebaseStorageService {

    public void uploadFile(String localFilePath, String firebasePath, String bucketName) throws Exception {
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);

        try (InputStream fileStream = new FileInputStream(localFilePath)) {
            Blob blob = bucket.create(firebasePath, fileStream, "application/pdf");
            System.out.println("✅ Uploaded file to Firebase Storage: " + blob.getMediaLink());
        }
    }
}
