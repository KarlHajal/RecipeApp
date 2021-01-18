package com.example.recipeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;


public class WearService extends WearableListenerService {

    public static final String EXTRA_INSTRUCTIONS = "EXTRA_INSTRUCTIONS";

    // Tag for Logcat
    private final String TAG = this.getClass().getSimpleName();

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDifficultyRatingsRef;
    private DatabaseReference mRecipeDifficultyRatingsRef;
    private String m_recipeId = "";

    private static Bitmap resizeImage(Bitmap bitmap, int newSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Image smaller, return it as is!
        if (width <= newSize && height <= newSize) return bitmap;

        int newWidth;
        int newHeight;

        if (width > height) {
            newWidth = newSize;
            newHeight = (newSize * height) / width;
        } else if (width < height) {
            newHeight = newSize;
            newWidth = (newSize * width) / height;
        } else {
            newHeight = newSize;
            newWidth = newSize;
        }

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Asset createAssetFromBitmap(Bitmap bitmap) {
        bitmap = resizeImage(bitmap, 390);

        if (bitmap != null) {
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        }
        return null;
    }


    @Override
    public void onCreate() {
        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getCurrentUser().getUid();
        mUserDifficultyRatingsRef = FirebaseDatabase.getInstance().getReference().child("profiles").child(uid).child("difficulty_ratings");
        mRecipeDifficultyRatingsRef = FirebaseDatabase.getInstance().getReference().child("recipes");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // If no action defined, return
        if (intent.getAction() == null) return START_NOT_STICKY;

        // Match against the given action
        ACTION_SEND action = ACTION_SEND.valueOf(intent.getAction());
        PutDataMapRequest putDataMapRequest;
        switch (action) {
            case INSTRUCTIONS:

                //if (watch_available==true){
                    AnalysedInstructions instructions = (AnalysedInstructions) intent.getParcelableExtra(EXTRA_INSTRUCTIONS);
                    m_recipeId = instructions.getRecipeId();
                    putDataMapRequest = PutDataMapRequest.create(BuildConfig.W_instructions_path);
                    putDataMapRequest.getDataMap().putDataMap(BuildConfig.W_instructions_key, instructions.toDataMap());
                    sendPutDataMapRequest(putDataMapRequest);
                break;//} else {
                   // Toast.makeText(this, "Watch not connected", Toast.LENGTH_LONG).show();
                //}
            default:
                Log.w(TAG, "Unknown action " + action);
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // A message has been received from the Wear API
        // Get the URI of the event
        String path = messageEvent.getPath();
        String data = new String(messageEvent.getData());
        Log.v(TAG, "Received a message for path " + path + " : \"" + data + "\", from node " +
                messageEvent.getSourceNodeId());

        switch (path) {
            case BuildConfig.W_rating_path:
                Log.i(TAG, "Message contained rating data : " + data);
                int rating = Integer.parseInt(data);
                Constants.Recipe_on_Watch = false;

                saveDifficultyRatingToDB(rating);

                break;
            default:
                Log.w(TAG, "Received a message for unknown path " + path + " : " + data);
                break;
        }
    }

    private void saveDifficultyRatingToDB(final int rating) {
        final DatabaseReference userRecipeRef = mUserDifficultyRatingsRef.child(m_recipeId);
        final String uid = mAuth.getCurrentUser().getUid();
        final DatabaseReference recipeDifficultyRef = mRecipeDifficultyRatingsRef.child(m_recipeId).child("difficulty_ratings");
        userRecipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRecipeRef.setValue(rating);
                recipeDifficultyRef.child(uid).setValue(rating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recipeDifficultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long average = (Long) snapshot.child("average").getValue();
                Long childrenCount = snapshot.getChildrenCount();
                if(average == null){
                    average = (long) rating;
                }
                else{
                    average = (average * childrenCount + rating) / (childrenCount + 1);
                }
                recipeDifficultyRef.child("average").setValue(average);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.v(TAG, "onDataChanged: " + dataEvents);

        for (DataEvent event : dataEvents) {

            // Get the URI of the event
            Uri uri = event.getDataItem().getUri();

            // Test if data has changed or has been removed
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // Extract the dataMap from the event
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                Log.v(TAG, "DataItem Changed: " + event.getDataItem().toString() + "\n" +
                        "\tPath: " + uri + "\tDatamap: " + dataMapItem.getDataMap() + "\n");

                Intent intent;

                assert uri.getPath() != null;
                switch (uri.getPath()) {
                    case BuildConfig.W_instructions_path:
                        Log.v(TAG, "not doing anything here");
                        break;
                    default:
                        Log.w(TAG, "Data changed for unhandled path: " + uri);
                        break;
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.w(TAG, "DataItem deleted: " + event.getDataItem().toString());
            }
        }
    }

    private void sendMessage(String message, String path) {
        // Send message to ALL connected nodes
        sendMessageToNodes(message, path);
    }

    void sendMessageToNodes(final String message, final String path) {
        Log.v(TAG, "Sending message " + message);
        // Lists all the nodes (devices) connected to the Wear API
        Wearable.getNodeClient(this).getConnectedNodes().addOnCompleteListener(new OnCompleteListener<List<Node>>() {
            @Override
            public void onComplete(@NonNull Task<List<Node>> listTask) {
                List<Node> nodes = listTask.getResult();
                for (Node node : nodes) {
                    Log.v(TAG, "Try to send message to a specific node");
                    WearService.this.sendMessage(message, path, node.getId());
                }
            }
        });
    }

    private void sendMessage(String message, String path, final String nodeId) {
        // Sends a message through the Wear API
        Wearable.getMessageClient(this).sendMessage(nodeId, path, message.getBytes())
                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.v(TAG, "Sent message to " + nodeId + ". Result = " + integer);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Message not sent. " + e.getMessage());
            }
        });
    }

    void sendPutDataMapRequest(PutDataMapRequest putDataMapRequest) {
        putDataMapRequest.getDataMap().putLong("time", System.nanoTime());
        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        request.setUrgent();
        Wearable.getDataClient(this).putDataItem(request).addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                Log.v(TAG, "Sent datamap.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Datamap not sent. " + e.getMessage());
            }
        });
    }

    private void bitmapFromAsset(Asset asset, final Intent intent, final String extraName) {
        // Reads an asset from the Wear API and parse it as an image
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        // Convert asset and convert it back to an image
        Wearable.getDataClient(this).getFdForAsset(asset).addOnCompleteListener(new OnCompleteListener<DataClient.GetFdForAssetResponse>() {
            @Override
            public void onComplete(@NonNull Task<DataClient.GetFdForAssetResponse> runnable) {
                Log.v(TAG, "Got bitmap from asset");
                InputStream assetInputStream = runnable.getResult().getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(assetInputStream);

                final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                byte[] bytes = byteStream.toByteArray();
                intent.putExtra(extraName, bytes);
                LocalBroadcastManager.getInstance(WearService.this).sendBroadcast(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception runnable) {
                Log.e(TAG, "Failed to get bitmap from asset");
            }
        });
    }

    // Constants
    public enum ACTION_SEND {
        INSTRUCTIONS
    }
}
