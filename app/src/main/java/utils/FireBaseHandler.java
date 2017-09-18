package utils;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * Created by Aisha on 9/3/2017.
 */

public class FireBaseHandler {

    private FirebaseDatabase mDatabase;

    public FireBaseHandler() {
        mDatabase = FirebaseDatabase.getInstance();

    }

    public void uploadNewsArticleImage(final HealthFact healthFact, Uri uri, final OnHealthFactUploadListener onHealthFactUploadListener) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        DatabaseReference myRef = mDatabase.getReference().child("healthfact/");
        healthFact.setmHealthFactID(myRef.push().getKey());

        StorageReference riversRef = storageRef.child("healthfactImage/" + healthFact.getmHealthFactID() + "/" + "main");

        UploadTask uploadTask = riversRef.putFile(uri);


        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                onHealthFactUploadListener.onHealthFactImageUpload(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.


                try {

                    //to add image in link during sharing a fact
                     healthFact.setmHealthImageAddress(taskSnapshot.getMetadata().getDownloadUrl().toString());

                    //  healthFact.setmHealthImageAddress(taskSnapshot.getDownloadUrl().getPath());
                    onHealthFactUploadListener.onHealthFactImageUpload(true);

                } catch (Exception e) {

                }

                //finished uploading image now upload newsarticle
                uploadHealthFact(healthFact, onHealthFactUploadListener);

            }
        });

    }

    public void uploadHealthFact(HealthFact healthFact, final OnHealthFactUploadListener onHealthFactUploadListener) {


        DatabaseReference myRef = mDatabase.getReference().child("healthfact/" + healthFact.getmHealthFactID());
        myRef.setValue(healthFact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onHealthFactUploadListener.onHealthFactUpload(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onHealthFactUploadListener.onHealthFactUpload(false);
            }
        });


    }

    public interface OnHealthFactUploadListener {
        void onHealthFactUpload(boolean isSuccessful);

        void onHealthFactImageUpload(boolean isSuccessful);

    }

}
