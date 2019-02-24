package Helper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.util.Log;

import com.example.jarvis.sproject.FileManager;

import java.util.List;

import Model.FileManagerItem;

public class EncryptFiles extends AsyncTask<Void, Void, Void> {

    private Context context;
    private List<FileManagerItem> selectionList;
    private String sdCardsPath;

    public EncryptFiles(Context context, List<FileManagerItem> list, String sdCardPath) {
        this.context = context;
        this.selectionList = list;
        this.sdCardsPath = sdCardPath;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for(FileManagerItem f : selectionList) {
            if (f.getType().equals("file")) {
                FileHelper.encryptFile(context, f.getPath(), sdCardsPath);
            } else if (f.getType().equals("dir")) {

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
