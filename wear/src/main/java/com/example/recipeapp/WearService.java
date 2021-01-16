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
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class WearService extends WearableListenerService {

    public static final String HEART_RATE = "HEART_RATE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String INSTRUCTIONS = "INSTRUCTIONS";
    public static final String ACCELERATION = "ACCELERATION";
    public static final String TOTACCELERATION = "TOTACCELERATION";

    // Tag for Logcat
    private final String TAG = this.getClass().getSimpleName();

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

    public static final String ACTIVITY_TO_START = "ACTIVITY_TO_START";
    public static final String MESSAGE = "MESSAGE";
    public static final String DATAMAP_INT = "DATAMAP_INT";
    public static final String DATAMAP_INT_ARRAYLIST = "DATAMAP_INT_ARRAYLIST";
    public static final String IMAGE = "IMAGE";
    public static final String PATH = "PATH";

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
            case ACCELERATION:
//                String acc = intent.getStringExtra(ACCELERATION);
//                if (acc == null) acc = "";
//                sendMessage(acc, BuildConfig.W_acceleration_path);
                float[] acc = intent.getFloatArrayExtra(ACCELERATION);
                Log.v(TAG, "ACCELERATION sending accel data" + acc[0] +" "+ acc[1] +" "+ acc[2]);
                putDataMapRequest = PutDataMapRequest.create(BuildConfig.W_acceleration_path);
                putDataMapRequest.getDataMap().putFloatArray(BuildConfig.W_acceleration_key, acc);
                sendPutDataMapRequest(putDataMapRequest);
                break;
            case TOTACCELERATION:
                int rating = intent.getIntExtra(TOTACCELERATION,0);

                break;
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
            case BuildConfig.W_path_start_activity:
                Log.v(TAG, "Message asked to open Activity");
                Intent startIntent = null;
                switch (data) {
                    case BuildConfig.W_mainactivity:
                        startIntent = new Intent(this, MainActivity.class);
                        break;
                    case BuildConfig.W_recipe_instructions_activity:
                        Log.d(TAG, "Start recipe instructions received");
                        startIntent = new Intent(this, RecipeInstructionsActivity.class);
                        Intent ratingintent = new Intent(this, RatingService.class);
                        startService(ratingintent);
                        break;
                }

                if (startIntent == null) {
                    Log.w(TAG, "Asked to start unhandled activity: " + data);
                    return;
                }
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
                break;
            case BuildConfig.W_path_stop_activity:
                switch (data) {
                    case BuildConfig.W_recipe_instructions_activity:
                        Intent intentStop = new Intent();
                        intentStop.setAction(RecipeInstructionsActivity.STOP_ACTIVITY);
                        LocalBroadcastManager.getInstance(WearService.this).sendBroadcast
                                (intentStop);
                        break;
                }
                break;
            default:
                Log.w(TAG, "Received a message for unknown path " + path + " : " + new String
                        (messageEvent.getData()));
        }
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
                        // Extract the data behind the key you know contains data
                        DataMap instructionDataMap = dataMapItem.getDataMap().getDataMap(BuildConfig.W_instructions_key);
                        AnalysedInstructions instructions = new AnalysedInstructions(instructionDataMap);
                        Log.i(TAG, "received instructions " + instructions.toString());

                        Intent startIntent = new Intent(this, RecipeInstructionsActivity.class);
                        startIntent.putExtra(INSTRUCTIONS, instructions);
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startIntent);
                        break;
                    default:
                        Log.v(TAG, "Data changed for unhandled path: " + uri);
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

    private void sendMessageToNodes(final String message, final String path) {
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

    private void sendPutDataMapRequest(PutDataMapRequest putDataMapRequest) {
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
        ACCELERATION,
        TOTACCELERATION;
    }
}